package okhttp3.internal.cache2;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.ByteString;
import okio.Source;
import okio.Timeout;

final class Relay {
    private static final long FILE_HEADER_SIZE = 32;
    static final ByteString PREFIX_CLEAN = ByteString.encodeUtf8("OkHttp cache v1\n");
    static final ByteString PREFIX_DIRTY = ByteString.encodeUtf8("OkHttp DIRTY :(\n");
    private static final int SOURCE_FILE = 2;
    private static final int SOURCE_UPSTREAM = 1;
    final Buffer buffer = new Buffer();
    final long bufferMaxSize;
    boolean complete;
    RandomAccessFile file;
    private final ByteString metadata;
    int sourceCount;
    Source upstream;
    final Buffer upstreamBuffer = new Buffer();
    long upstreamPos;
    Thread upstreamReader;

    class RelaySource implements Source {
        private FileOperator fileOperator = new FileOperator(Relay.this.file.getChannel());
        private long sourcePos;
        private final Timeout timeout = new Timeout();

        RelaySource() {
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public long read(Buffer buffer, long j) throws IOException {
            Throwable th;
            long j2 = j;
            if (this.fileOperator == null) {
                throw new IllegalStateException("closed");
            }
            synchronized (Relay.this) {
                long j3;
                while (true) {
                    try {
                        long j4 = r1.sourcePos;
                        j3 = Relay.this.upstreamPos;
                        int i = (j4 > j3 ? 1 : (j4 == j3 ? 0 : -1));
                        j4 = 2;
                        if (i == 0) {
                            if (!Relay.this.complete) {
                                if (Relay.this.upstreamReader == null) {
                                    break;
                                }
                                r1.timeout.waitUntilNotified(Relay.this);
                            } else {
                                return -1;
                            }
                        }
                        break;
                        if (r6 == 2) {
                            long min = Math.min(j2, j3 - r1.sourcePos);
                            FileOperator fileOperator = r1.fileOperator;
                            j4 = 32 + r1.sourcePos;
                            return min;
                        }
                        try {
                            j4 = Relay.this.upstream.read(Relay.this.upstreamBuffer, Relay.this.bufferMaxSize);
                            if (j4 == -1) {
                                Relay.this.commit(j3);
                                synchronized (Relay.this) {
                                    try {
                                        Relay.this.upstreamReader = null;
                                        Relay.this.notifyAll();
                                    } catch (Throwable th2) {
                                        Throwable th3 = th2;
                                    }
                                }
                                return -1;
                            }
                            j2 = Math.min(j4, j2);
                            Buffer buffer2 = Relay.this.upstreamBuffer;
                            synchronized (j3) {
                                Relay.this.buffer.write(Relay.this.upstreamBuffer, j4);
                                if (Relay.this.buffer.size() > Relay.this.bufferMaxSize) {
                                    Relay.this.buffer.skip(Relay.this.buffer.size() - Relay.this.bufferMaxSize);
                                }
                                Relay relay = Relay.this;
                                relay.upstreamPos += j4;
                            }
                            synchronized (Relay.this) {
                                try {
                                    Relay.this.upstreamReader = null;
                                    Relay.this.notifyAll();
                                } catch (Throwable th22) {
                                    th = th22;
                                }
                            }
                            return j2;
                        } catch (Buffer th4) {
                            synchronized (Relay.this) {
                                try {
                                    Relay.this.upstreamReader = null;
                                    Relay.this.notifyAll();
                                } catch (Throwable th222) {
                                    while (true) {
                                        th = th222;
                                    }
                                }
                            }
                        } finally {
                            j2 = 
/*
Method generation error in method: okhttp3.internal.cache2.Relay.RelaySource.read(okio.Buffer, long):long
jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0118: MERGE  (r2_8 'j2' long) = (r0_5 'th4' okio.Buffer), (r23_0 'buffer' okio.Buffer) in method: okhttp3.internal.cache2.Relay.RelaySource.read(okio.Buffer, long):long
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:226)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:203)
	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:100)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:50)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:93)
	at jadx.core.codegen.RegionGen.makeTryCatch(RegionGen.java:297)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:93)
	at jadx.core.codegen.RegionGen.makeTryCatch(RegionGen.java:277)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:93)
	at jadx.core.codegen.RegionGen.makeLoop(RegionGen.java:174)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:61)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:93)
	at jadx.core.codegen.RegionGen.makeSynchronizedRegion(RegionGen.java:227)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:65)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:183)
	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:328)
	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:265)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:228)
	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:118)
	at jadx.core.codegen.ClassGen.addInnerClasses(ClassGen.java:241)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:118)
	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:83)
	at jadx.core.codegen.CodeGen.visit(CodeGen.java:19)
	at jadx.core.ProcessClass.process(ProcessClass.java:43)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
Caused by: jadx.core.utils.exceptions.CodegenException: MERGE can be used only in fallback mode
	at jadx.core.codegen.InsnGen.fallbackOnlyInsn(InsnGen.java:530)
	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:514)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:220)
	... 49 more

*/

                            public Timeout timeout() {
                                return this.timeout;
                            }

                            public void close() throws IOException {
                                if (this.fileOperator != null) {
                                    Closeable closeable = null;
                                    this.fileOperator = null;
                                    synchronized (Relay.this) {
                                        Relay relay = Relay.this;
                                        relay.sourceCount--;
                                        if (Relay.this.sourceCount == 0) {
                                            RandomAccessFile randomAccessFile = Relay.this.file;
                                            Relay.this.file = null;
                                            closeable = randomAccessFile;
                                        }
                                    }
                                    if (closeable != null) {
                                        Util.closeQuietly(closeable);
                                    }
                                }
                            }
                        }

                        private Relay(RandomAccessFile randomAccessFile, Source source, long j, ByteString byteString, long j2) {
                            this.file = randomAccessFile;
                            this.upstream = source;
                            this.complete = source == null ? true : null;
                            this.upstreamPos = j;
                            this.metadata = byteString;
                            this.bufferMaxSize = j2;
                        }

                        public static Relay edit(File file, Source source, ByteString byteString, long j) throws IOException {
                            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                            File relay = new Relay(randomAccessFile, source, 0, byteString, j);
                            randomAccessFile.setLength(null);
                            relay.writeHeader(PREFIX_DIRTY, -1, -1);
                            return relay;
                        }

                        public static Relay read(File file) throws IOException {
                            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                            file = new FileOperator(randomAccessFile.getChannel());
                            Buffer buffer = new Buffer();
                            file.read(0, buffer, 32);
                            if (buffer.readByteString((long) PREFIX_CLEAN.size()).equals(PREFIX_CLEAN)) {
                                long readLong = buffer.readLong();
                                long readLong2 = buffer.readLong();
                                buffer = new Buffer();
                                file.read(32 + readLong, buffer, readLong2);
                                return new Relay(randomAccessFile, null, readLong, buffer.readByteString(), 0);
                            }
                            throw new IOException("unreadable cache file");
                        }

                        private void writeHeader(ByteString byteString, long j, long j2) throws IOException {
                            Buffer buffer = new Buffer();
                            buffer.write(byteString);
                            buffer.writeLong(j);
                            buffer.writeLong(j2);
                            if (buffer.size() != 32) {
                                throw new IllegalArgumentException();
                            }
                            new FileOperator(this.file.getChannel()).write(0, buffer, 32);
                        }

                        private void writeMetadata(long j) throws IOException {
                            Buffer buffer = new Buffer();
                            buffer.write(this.metadata);
                            new FileOperator(this.file.getChannel()).write(32 + j, buffer, (long) this.metadata.size());
                        }

                        void commit(long j) throws IOException {
                            writeMetadata(j);
                            this.file.getChannel().force(false);
                            writeHeader(PREFIX_CLEAN, j, (long) this.metadata.size());
                            this.file.getChannel().force(false);
                            synchronized (this) {
                                this.complete = 1;
                            }
                            Util.closeQuietly(this.upstream);
                            this.upstream = 0;
                        }

                        boolean isClosed() {
                            return this.file == null;
                        }

                        public ByteString metadata() {
                            return this.metadata;
                        }

                        public Source newSource() {
                            synchronized (this) {
                                if (this.file == null) {
                                    return null;
                                }
                                this.sourceCount++;
                                return new RelaySource();
                            }
                        }
                    }
