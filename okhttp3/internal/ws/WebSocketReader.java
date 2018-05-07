package okhttp3.internal.ws;

import java.io.EOFException;
import java.io.IOException;
import java.net.ProtocolException;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;

final class WebSocketReader {
    boolean closed;
    long frameBytesRead;
    final FrameCallback frameCallback;
    long frameLength;
    final boolean isClient;
    boolean isControlFrame;
    boolean isFinalFrame;
    boolean isMasked;
    final byte[] maskBuffer = new byte[8192];
    final byte[] maskKey = new byte[4];
    int opcode;
    final BufferedSource source;

    public interface FrameCallback {
        void onReadClose(int i, String str);

        void onReadMessage(String str) throws IOException;

        void onReadMessage(ByteString byteString) throws IOException;

        void onReadPing(ByteString byteString);

        void onReadPong(ByteString byteString);
    }

    private void readHeader() throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Not initialized variable reg: 1, insn: 0x003e: MOVE  (r0 ?[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r1 ?[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:10:0x003e, method: okhttp3.internal.ws.WebSocketReader.readHeader():void
	at jadx.core.dex.visitors.ssa.SSATransform.renameVar(SSATransform.java:168)
	at jadx.core.dex.visitors.ssa.SSATransform.renameVar(SSATransform.java:197)
	at jadx.core.dex.visitors.ssa.SSATransform.renameVar(SSATransform.java:197)
	at jadx.core.dex.visitors.ssa.SSATransform.renameVar(SSATransform.java:197)
	at jadx.core.dex.visitors.ssa.SSATransform.renameVar(SSATransform.java:197)
	at jadx.core.dex.visitors.ssa.SSATransform.renameVar(SSATransform.java:197)
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:132)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
        /*
        r8 = this;
        r0 = r8.closed;
        if (r0 == 0) goto L_0x000c;
    L_0x0004:
        r0 = new java.io.IOException;
        r1 = "closed";
        r0.<init>(r1);
        throw r0;
    L_0x000c:
        r0 = r8.source;
        r0 = r0.timeout();
        r0 = r0.timeoutNanos();
        r2 = r8.source;
        r2 = r2.timeout();
        r2.clearTimeout();
        r2 = r8.source;	 Catch:{ all -> 0x011b }
        r2 = r2.readByte();	 Catch:{ all -> 0x011b }
        r2 = r2 & 255;
        r3 = r8.source;
        r3 = r3.timeout();
        r4 = java.util.concurrent.TimeUnit.NANOSECONDS;
        r3.timeout(r0, r4);
        r0 = r2 & 15;
        r8.opcode = r0;
        r0 = r2 & 128;
        r1 = 0;
        r3 = 1;
        if (r0 == 0) goto L_0x003e;
    L_0x003c:
        r0 = r3;
        goto L_0x003f;
    L_0x003e:
        r0 = r1;
    L_0x003f:
        r8.isFinalFrame = r0;
        r0 = r2 & 8;
        if (r0 == 0) goto L_0x0047;
    L_0x0045:
        r0 = r3;
        goto L_0x0048;
    L_0x0047:
        r0 = r1;
    L_0x0048:
        r8.isControlFrame = r0;
        r0 = r8.isControlFrame;
        if (r0 == 0) goto L_0x005a;
    L_0x004e:
        r0 = r8.isFinalFrame;
        if (r0 != 0) goto L_0x005a;
    L_0x0052:
        r0 = new java.net.ProtocolException;
        r1 = "Control frames must be final.";
        r0.<init>(r1);
        throw r0;
    L_0x005a:
        r0 = r2 & 64;
        if (r0 == 0) goto L_0x0060;
    L_0x005e:
        r0 = r3;
        goto L_0x0061;
    L_0x0060:
        r0 = r1;
    L_0x0061:
        r4 = r2 & 32;
        if (r4 == 0) goto L_0x0067;
    L_0x0065:
        r4 = r3;
        goto L_0x0068;
    L_0x0067:
        r4 = r1;
    L_0x0068:
        r2 = r2 & 16;
        if (r2 == 0) goto L_0x006e;
    L_0x006c:
        r2 = r3;
        goto L_0x006f;
    L_0x006e:
        r2 = r1;
    L_0x006f:
        if (r0 != 0) goto L_0x0113;
    L_0x0071:
        if (r4 != 0) goto L_0x0113;
    L_0x0073:
        if (r2 == 0) goto L_0x0077;
    L_0x0075:
        goto L_0x0113;
    L_0x0077:
        r0 = r8.source;
        r0 = r0.readByte();
        r0 = r0 & 255;
        r2 = r0 & 128;
        if (r2 == 0) goto L_0x0084;
    L_0x0083:
        r1 = r3;
    L_0x0084:
        r8.isMasked = r1;
        r1 = r8.isMasked;
        r2 = r8.isClient;
        if (r1 != r2) goto L_0x009b;
    L_0x008c:
        r0 = new java.net.ProtocolException;
        r1 = r8.isClient;
        if (r1 == 0) goto L_0x0095;
    L_0x0092:
        r1 = "Server-sent frames must not be masked.";
        goto L_0x0097;
    L_0x0095:
        r1 = "Client-sent frames must be masked.";
    L_0x0097:
        r0.<init>(r1);
        throw r0;
    L_0x009b:
        r0 = r0 & 127;
        r0 = (long) r0;
        r8.frameLength = r0;
        r0 = r8.frameLength;
        r2 = 126; // 0x7e float:1.77E-43 double:6.23E-322;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        r0 = 0;
        if (r4 != 0) goto L_0x00b9;
    L_0x00aa:
        r2 = r8.source;
        r2 = r2.readShort();
        r2 = (long) r2;
        r4 = 65535; // 0xffff float:9.1834E-41 double:3.23786E-319;
        r6 = r2 & r4;
        r8.frameLength = r6;
        goto L_0x00f1;
    L_0x00b9:
        r2 = r8.frameLength;
        r4 = 127; // 0x7f float:1.78E-43 double:6.27E-322;
        r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r6 != 0) goto L_0x00f1;
    L_0x00c1:
        r2 = r8.source;
        r2 = r2.readLong();
        r8.frameLength = r2;
        r2 = r8.frameLength;
        r4 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1));
        if (r4 >= 0) goto L_0x00f1;
    L_0x00cf:
        r0 = new java.net.ProtocolException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Frame length 0x";
        r1.append(r2);
        r2 = r8.frameLength;
        r2 = java.lang.Long.toHexString(r2);
        r1.append(r2);
        r2 = " > 0x7FFFFFFFFFFFFFFF";
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x00f1:
        r8.frameBytesRead = r0;
        r0 = r8.isControlFrame;
        if (r0 == 0) goto L_0x0107;
    L_0x00f7:
        r0 = r8.frameLength;
        r2 = 125; // 0x7d float:1.75E-43 double:6.2E-322;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 <= 0) goto L_0x0107;
    L_0x00ff:
        r0 = new java.net.ProtocolException;
        r1 = "Control frame must be less than 125B.";
        r0.<init>(r1);
        throw r0;
    L_0x0107:
        r0 = r8.isMasked;
        if (r0 == 0) goto L_0x0112;
    L_0x010b:
        r0 = r8.source;
        r1 = r8.maskKey;
        r0.readFully(r1);
    L_0x0112:
        return;
    L_0x0113:
        r0 = new java.net.ProtocolException;
        r1 = "Reserved flags are unsupported.";
        r0.<init>(r1);
        throw r0;
    L_0x011b:
        r2 = move-exception;
        r3 = r8.source;
        r3 = r3.timeout();
        r4 = java.util.concurrent.TimeUnit.NANOSECONDS;
        r3.timeout(r0, r4);
        throw r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.ws.WebSocketReader.readHeader():void");
    }

    WebSocketReader(boolean z, BufferedSource bufferedSource, FrameCallback frameCallback) {
        if (bufferedSource == null) {
            throw new NullPointerException("source == null");
        } else if (frameCallback == null) {
            throw new NullPointerException("frameCallback == null");
        } else {
            this.isClient = z;
            this.source = bufferedSource;
            this.frameCallback = frameCallback;
        }
    }

    void processNextFrame() throws IOException {
        readHeader();
        if (this.isControlFrame) {
            readControlFrame();
        } else {
            readMessageFrame();
        }
    }

    private void readControlFrame() throws IOException {
        int read;
        Buffer buffer = new Buffer();
        if (this.frameBytesRead < this.frameLength) {
            if (this.isClient) {
                this.source.readFully(buffer, this.frameLength);
            } else {
                while (this.frameBytesRead < this.frameLength) {
                    read = this.source.read(this.maskBuffer, 0, (int) Math.min(this.frameLength - this.frameBytesRead, (long) this.maskBuffer.length));
                    if (read == -1) {
                        throw new EOFException();
                    }
                    long j = (long) read;
                    WebSocketProtocol.toggleMask(this.maskBuffer, j, this.maskKey, this.frameBytesRead);
                    buffer.write(this.maskBuffer, 0, read);
                    this.frameBytesRead += j;
                }
            }
        }
        switch (this.opcode) {
            case 8:
                read = 1005;
                String str = "";
                long size = buffer.size();
                if (size == 1) {
                    throw new ProtocolException("Malformed close payload length of 1.");
                }
                if (size != 0) {
                    read = buffer.readShort();
                    str = buffer.readUtf8();
                    String closeCodeExceptionMessage = WebSocketProtocol.closeCodeExceptionMessage(read);
                    if (closeCodeExceptionMessage != null) {
                        throw new ProtocolException(closeCodeExceptionMessage);
                    }
                }
                this.frameCallback.onReadClose(read, str);
                this.closed = true;
                return;
            case 9:
                this.frameCallback.onReadPing(buffer.readByteString());
                return;
            case 10:
                this.frameCallback.onReadPong(buffer.readByteString());
                return;
            default:
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unknown control opcode: ");
                stringBuilder.append(Integer.toHexString(this.opcode));
                throw new ProtocolException(stringBuilder.toString());
        }
    }

    private void readMessageFrame() throws IOException {
        int i = this.opcode;
        if (i == 1 || i == 2) {
            Buffer buffer = new Buffer();
            readMessage(buffer);
            if (i == 1) {
                this.frameCallback.onReadMessage(buffer.readUtf8());
                return;
            } else {
                this.frameCallback.onReadMessage(buffer.readByteString());
                return;
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unknown opcode: ");
        stringBuilder.append(Integer.toHexString(i));
        throw new ProtocolException(stringBuilder.toString());
    }

    void readUntilNonControlFrame() throws IOException {
        while (!this.closed) {
            readHeader();
            if (this.isControlFrame) {
                readControlFrame();
            } else {
                return;
            }
        }
    }

    private void readMessage(Buffer buffer) throws IOException {
        while (!this.closed) {
            long read;
            if (this.frameBytesRead == this.frameLength) {
                if (!this.isFinalFrame) {
                    readUntilNonControlFrame();
                    if (this.opcode != 0) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Expected continuation opcode. Got: ");
                        stringBuilder.append(Integer.toHexString(this.opcode));
                        throw new ProtocolException(stringBuilder.toString());
                    } else if (this.isFinalFrame && this.frameLength == 0) {
                        return;
                    }
                }
                return;
            }
            long j = this.frameLength - this.frameBytesRead;
            if (this.isMasked) {
                read = (long) this.source.read(this.maskBuffer, 0, (int) Math.min(j, (long) this.maskBuffer.length));
                if (read == -1) {
                    throw new EOFException();
                }
                WebSocketProtocol.toggleMask(this.maskBuffer, read, this.maskKey, this.frameBytesRead);
                buffer.write(this.maskBuffer, 0, (int) read);
            } else {
                read = this.source.read(buffer, j);
                if (read == -1) {
                    throw new EOFException();
                }
            }
            this.frameBytesRead += read;
        }
        throw new IOException("closed");
    }
}
