package okhttp3.internal.ws;

import android.support.v4.view.PointerIconCompat;
import java.io.Closeable;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.ws.WebSocketReader.FrameCallback;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;

public final class RealWebSocket implements WebSocket, FrameCallback {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final long CANCEL_AFTER_CLOSE_MILLIS = 60000;
    private static final long MAX_QUEUE_SIZE = 16777216;
    private static final List<Protocol> ONLY_HTTP1 = Collections.singletonList(Protocol.HTTP_1_1);
    private Call call;
    private ScheduledFuture<?> cancelFuture;
    private boolean enqueuedClose;
    private ScheduledExecutorService executor;
    private boolean failed;
    private final String key;
    final WebSocketListener listener;
    private final ArrayDeque<Object> messageAndCloseQueue = new ArrayDeque();
    private final Request originalRequest;
    int pingCount;
    int pongCount;
    private final ArrayDeque<ByteString> pongQueue = new ArrayDeque();
    private long queueSize;
    private final Random random;
    private WebSocketReader reader;
    private int receivedCloseCode = -1;
    private String receivedCloseReason;
    private Streams streams;
    private WebSocketWriter writer;
    private final Runnable writerRunnable;

    final class CancelRunnable implements Runnable {
        CancelRunnable() {
        }

        public void run() {
            RealWebSocket.this.cancel();
        }
    }

    static final class Close {
        final long cancelAfterCloseMillis;
        final int code;
        final ByteString reason;

        Close(int i, ByteString byteString, long j) {
            this.code = i;
            this.reason = byteString;
            this.cancelAfterCloseMillis = j;
        }
    }

    static final class Message {
        final ByteString data;
        final int formatOpcode;

        Message(int i, ByteString byteString) {
            this.formatOpcode = i;
            this.data = byteString;
        }
    }

    private final class PingRunnable implements Runnable {
        PingRunnable() {
        }

        public void run() {
            RealWebSocket.this.writePingFrame();
        }
    }

    public static abstract class Streams implements Closeable {
        public final boolean client;
        public final BufferedSink sink;
        public final BufferedSource source;

        public Streams(boolean z, BufferedSource bufferedSource, BufferedSink bufferedSink) {
            this.client = z;
            this.source = bufferedSource;
            this.sink = bufferedSink;
        }
    }

    public RealWebSocket(Request request, WebSocketListener webSocketListener, Random random) {
        if ("GET".equals(request.method())) {
            this.originalRequest = request;
            this.listener = webSocketListener;
            this.random = random;
            byte[] bArr = new byte[16];
            random.nextBytes(bArr);
            this.key = ByteString.of(bArr).base64();
            this.writerRunnable = new Runnable() {
                public void run() {
                    while (RealWebSocket.this.writeOneFrame()) {
                        try {
                        } catch (Exception e) {
                            RealWebSocket.this.failWebSocket(e, null);
                            return;
                        }
                    }
                }
            };
            return;
        }
        random = new StringBuilder();
        random.append("Request must be GET: ");
        random.append(request.method());
        throw new IllegalArgumentException(random.toString());
    }

    public Request request() {
        return this.originalRequest;
    }

    public synchronized long queueSize() {
        return this.queueSize;
    }

    public void cancel() {
        this.call.cancel();
    }

    public void connect(OkHttpClient okHttpClient) {
        okHttpClient = okHttpClient.newBuilder().protocols(ONLY_HTTP1).build();
        final int pingIntervalMillis = okHttpClient.pingIntervalMillis();
        final Request build = this.originalRequest.newBuilder().header("Upgrade", "websocket").header("Connection", "Upgrade").header("Sec-WebSocket-Key", this.key).header("Sec-WebSocket-Version", "13").build();
        this.call = Internal.instance.newWebSocketCall(okHttpClient, build);
        this.call.enqueue(new Callback() {
            public void onResponse(Call call, Response response) {
                try {
                    RealWebSocket.this.checkResponse(response);
                    call = Internal.instance.streamAllocation(call);
                    call.noNewStreams();
                    Streams newWebSocketStreams = call.connection().newWebSocketStreams(call);
                    try {
                        RealWebSocket.this.listener.onOpen(RealWebSocket.this, response);
                        response = new StringBuilder();
                        response.append("OkHttp WebSocket ");
                        response.append(build.url().redact());
                        RealWebSocket.this.initReaderAndWriter(response.toString(), (long) pingIntervalMillis, newWebSocketStreams);
                        call.connection().socket().setSoTimeout(null);
                        RealWebSocket.this.loopReader();
                    } catch (Call call2) {
                        RealWebSocket.this.failWebSocket(call2, null);
                    }
                } catch (Call call22) {
                    RealWebSocket.this.failWebSocket(call22, response);
                    Util.closeQuietly((Closeable) response);
                }
            }

            public void onFailure(Call call, IOException iOException) {
                RealWebSocket.this.failWebSocket(iOException, null);
            }
        });
    }

    void checkResponse(Response response) throws ProtocolException {
        if (response.code() != 101) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Expected HTTP 101 response but was '");
            stringBuilder.append(response.code());
            stringBuilder.append(" ");
            stringBuilder.append(response.message());
            stringBuilder.append("'");
            throw new ProtocolException(stringBuilder.toString());
        }
        String header = response.header("Connection");
        if ("Upgrade".equalsIgnoreCase(header)) {
            header = response.header("Upgrade");
            if ("websocket".equalsIgnoreCase(header)) {
                response = response.header("Sec-WebSocket-Accept");
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(this.key);
                stringBuilder2.append("258EAFA5-E914-47DA-95CA-C5AB0DC85B11");
                header = ByteString.encodeUtf8(stringBuilder2.toString()).sha1().base64();
                if (!header.equals(response)) {
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("Expected 'Sec-WebSocket-Accept' header value '");
                    stringBuilder3.append(header);
                    stringBuilder3.append("' but was '");
                    stringBuilder3.append(response);
                    stringBuilder3.append("'");
                    throw new ProtocolException(stringBuilder3.toString());
                }
                return;
            }
            stringBuilder = new StringBuilder();
            stringBuilder.append("Expected 'Upgrade' header value 'websocket' but was '");
            stringBuilder.append(header);
            stringBuilder.append("'");
            throw new ProtocolException(stringBuilder.toString());
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append("Expected 'Connection' header value 'Upgrade' but was '");
        stringBuilder.append(header);
        stringBuilder.append("'");
        throw new ProtocolException(stringBuilder.toString());
    }

    public void initReaderAndWriter(String str, long j, Streams streams) throws IOException {
        synchronized (this) {
            this.streams = streams;
            this.writer = new WebSocketWriter(streams.client, streams.sink, this.random);
            this.executor = new ScheduledThreadPoolExecutor(1, Util.threadFactory(str, false));
            if (j != 0) {
                this.executor.scheduleAtFixedRate(new PingRunnable(), j, j, TimeUnit.MILLISECONDS);
            }
            if (this.messageAndCloseQueue.isEmpty() == null) {
                runWriter();
            }
        }
        this.reader = new WebSocketReader(streams.client, streams.source, this);
    }

    public void loopReader() throws IOException {
        while (this.receivedCloseCode == -1) {
            this.reader.processNextFrame();
        }
    }

    boolean processNextFrame() throws IOException {
        boolean z = false;
        try {
            this.reader.processNextFrame();
            if (this.receivedCloseCode == -1) {
                z = true;
            }
            return z;
        } catch (Exception e) {
            failWebSocket(e, null);
            return false;
        }
    }

    void awaitTermination(int i, TimeUnit timeUnit) throws InterruptedException {
        this.executor.awaitTermination((long) i, timeUnit);
    }

    void tearDown() throws InterruptedException {
        if (this.cancelFuture != null) {
            this.cancelFuture.cancel(false);
        }
        this.executor.shutdown();
        this.executor.awaitTermination(10, TimeUnit.SECONDS);
    }

    synchronized int pingCount() {
        return this.pingCount;
    }

    synchronized int pongCount() {
        return this.pongCount;
    }

    public void onReadMessage(String str) throws IOException {
        this.listener.onMessage((WebSocket) this, str);
    }

    public void onReadMessage(ByteString byteString) throws IOException {
        this.listener.onMessage((WebSocket) this, byteString);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void onReadPing(ByteString byteString) {
        if (!this.failed) {
            if (!this.enqueuedClose || !this.messageAndCloseQueue.isEmpty()) {
                this.pongQueue.add(byteString);
                runWriter();
                this.pingCount++;
            }
        }
    }

    public synchronized void onReadPong(ByteString byteString) {
        this.pongCount++;
    }

    public void onReadClose(int i, String str) {
        if (i == -1) {
            throw new IllegalArgumentException();
        }
        Closeable closeable;
        synchronized (this) {
            if (this.receivedCloseCode != -1) {
                throw new IllegalStateException("already closed");
            }
            this.receivedCloseCode = i;
            this.receivedCloseReason = str;
            if (this.enqueuedClose && this.messageAndCloseQueue.isEmpty()) {
                closeable = this.streams;
                this.streams = null;
                if (this.cancelFuture != null) {
                    this.cancelFuture.cancel(false);
                }
                this.executor.shutdown();
            } else {
                closeable = null;
            }
        }
        try {
            this.listener.onClosing(this, i, str);
            if (closeable != null) {
                this.listener.onClosed(this, i, str);
            }
            Util.closeQuietly(closeable);
        } catch (Throwable th) {
            Util.closeQuietly(closeable);
        }
    }

    public boolean send(String str) {
        if (str != null) {
            return send(ByteString.encodeUtf8(str), 1);
        }
        throw new NullPointerException("text == null");
    }

    public boolean send(ByteString byteString) {
        if (byteString != null) {
            return send(byteString, 2);
        }
        throw new NullPointerException("bytes == null");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized boolean send(ByteString byteString, int i) {
        if (!this.failed) {
            if (!this.enqueuedClose) {
                if (this.queueSize + ((long) byteString.size()) > MAX_QUEUE_SIZE) {
                    close(PointerIconCompat.TYPE_CONTEXT_MENU, 0);
                    return false;
                }
                this.queueSize += (long) byteString.size();
                this.messageAndCloseQueue.add(new Message(i, byteString));
                runWriter();
                return true;
            }
        }
    }

    synchronized boolean pong(ByteString byteString) {
        if (!this.failed) {
            if (!this.enqueuedClose || !this.messageAndCloseQueue.isEmpty()) {
                this.pongQueue.add(byteString);
                runWriter();
                return true;
            }
        }
        return null;
    }

    public boolean close(int i, String str) {
        return close(i, str, CANCEL_AFTER_CLOSE_MILLIS);
    }

    synchronized boolean close(int i, String str, long j) {
        WebSocketProtocol.validateCloseCode(i);
        ByteString byteString = null;
        if (str != null) {
            byteString = ByteString.encodeUtf8(str);
            if (((long) byteString.size()) > 123) {
                j = new StringBuilder();
                j.append("reason.size() > 123: ");
                j.append(str);
                throw new IllegalArgumentException(j.toString());
            }
        }
        if (this.failed == null) {
            if (this.enqueuedClose == null) {
                this.enqueuedClose = true;
                this.messageAndCloseQueue.add(new Close(i, byteString, j));
                runWriter();
                return true;
            }
        }
        return false;
    }

    private void runWriter() {
        if (this.executor != null) {
            this.executor.execute(this.writerRunnable);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    boolean writeOneFrame() throws IOException {
        synchronized (this) {
            if (this.failed) {
                return false;
            }
            WebSocketWriter webSocketWriter = this.writer;
            ByteString byteString = (ByteString) this.pongQueue.poll();
            Closeable closeable = null;
            Object poll;
            int i;
            String str;
            if (byteString == null) {
                poll = this.messageAndCloseQueue.poll();
                if (poll instanceof Close) {
                    i = this.receivedCloseCode;
                    str = this.receivedCloseReason;
                    if (i != -1) {
                        Streams streams = this.streams;
                        this.streams = null;
                        this.executor.shutdown();
                        closeable = streams;
                    } else {
                        this.cancelFuture = this.executor.schedule(new CancelRunnable(), ((Close) poll).cancelAfterCloseMillis, TimeUnit.MILLISECONDS);
                    }
                } else if (poll == null) {
                    return false;
                } else {
                    i = -1;
                    str = null;
                }
            } else {
                i = -1;
                poll = null;
                str = poll;
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    void writePingFrame() {
        synchronized (this) {
            if (this.failed) {
                return;
            }
            WebSocketWriter webSocketWriter = this.writer;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void failWebSocket(Exception exception, Response response) {
        synchronized (this) {
            if (this.failed) {
                return;
            }
            this.failed = true;
            Closeable closeable = this.streams;
            this.streams = null;
            if (this.cancelFuture != null) {
                this.cancelFuture.cancel(false);
            }
            if (this.executor != null) {
                this.executor.shutdown();
            }
        }
    }
}
