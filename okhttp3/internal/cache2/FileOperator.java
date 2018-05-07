package okhttp3.internal.cache2;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

final class FileOperator {
    private static final int BUFFER_SIZE = 8192;
    private final byte[] byteArray = new byte[8192];
    private final ByteBuffer byteBuffer = ByteBuffer.wrap(this.byteArray);
    private final FileChannel fileChannel;

    public void read(long r7, okio.Buffer r9, long r10) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.ssa.SSATransform.placePhi(SSATransform.java:82)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:50)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
        /*
        r6 = this;
        r0 = 0;
        r2 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1));
        if (r2 >= 0) goto L_0x000c;
    L_0x0006:
        r7 = new java.lang.IndexOutOfBoundsException;
        r7.<init>();
        throw r7;
    L_0x000c:
        r2 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1));
        if (r2 <= 0) goto L_0x004d;
    L_0x0010:
        r2 = r6.byteBuffer;	 Catch:{ all -> 0x0046 }
        r3 = 8192; // 0x2000 float:1.14794E-41 double:4.0474E-320;	 Catch:{ all -> 0x0046 }
        r3 = java.lang.Math.min(r3, r10);	 Catch:{ all -> 0x0046 }
        r3 = (int) r3;	 Catch:{ all -> 0x0046 }
        r2.limit(r3);	 Catch:{ all -> 0x0046 }
        r2 = r6.fileChannel;	 Catch:{ all -> 0x0046 }
        r3 = r6.byteBuffer;	 Catch:{ all -> 0x0046 }
        r2 = r2.read(r3, r7);	 Catch:{ all -> 0x0046 }
        r3 = -1;	 Catch:{ all -> 0x0046 }
        if (r2 != r3) goto L_0x002d;	 Catch:{ all -> 0x0046 }
    L_0x0027:
        r7 = new java.io.EOFException;	 Catch:{ all -> 0x0046 }
        r7.<init>();	 Catch:{ all -> 0x0046 }
        throw r7;	 Catch:{ all -> 0x0046 }
    L_0x002d:
        r2 = r6.byteBuffer;	 Catch:{ all -> 0x0046 }
        r2 = r2.position();	 Catch:{ all -> 0x0046 }
        r3 = r6.byteArray;	 Catch:{ all -> 0x0046 }
        r4 = 0;	 Catch:{ all -> 0x0046 }
        r9.write(r3, r4, r2);	 Catch:{ all -> 0x0046 }
        r2 = (long) r2;
        r4 = r7 + r2;
        r7 = r10 - r2;
        r10 = r6.byteBuffer;
        r10.clear();
        r10 = r7;
        r7 = r4;
        goto L_0x000c;
    L_0x0046:
        r7 = move-exception;
        r8 = r6.byteBuffer;
        r8.clear();
        throw r7;
    L_0x004d:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache2.FileOperator.read(long, okio.Buffer, long):void");
    }

    public void write(long r8, okio.Buffer r10, long r11) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.ssa.SSATransform.placePhi(SSATransform.java:82)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:50)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
        /*
        r7 = this;
        r0 = 0;
        r2 = (r11 > r0 ? 1 : (r11 == r0 ? 0 : -1));
        if (r2 < 0) goto L_0x004d;
    L_0x0006:
        r2 = r10.size();
        r4 = (r11 > r2 ? 1 : (r11 == r2 ? 0 : -1));
        if (r4 <= 0) goto L_0x000f;
    L_0x000e:
        goto L_0x004d;
    L_0x000f:
        r2 = (r11 > r0 ? 1 : (r11 == r0 ? 0 : -1));
        if (r2 <= 0) goto L_0x004c;
    L_0x0013:
        r2 = 8192; // 0x2000 float:1.14794E-41 double:4.0474E-320;
        r2 = java.lang.Math.min(r2, r11);	 Catch:{ all -> 0x0045 }
        r2 = (int) r2;	 Catch:{ all -> 0x0045 }
        r3 = r7.byteArray;	 Catch:{ all -> 0x0045 }
        r4 = 0;	 Catch:{ all -> 0x0045 }
        r10.read(r3, r4, r2);	 Catch:{ all -> 0x0045 }
        r3 = r7.byteBuffer;	 Catch:{ all -> 0x0045 }
        r3.limit(r2);	 Catch:{ all -> 0x0045 }
    L_0x0025:
        r3 = r7.fileChannel;	 Catch:{ all -> 0x0045 }
        r4 = r7.byteBuffer;	 Catch:{ all -> 0x0045 }
        r3 = r3.write(r4, r8);	 Catch:{ all -> 0x0045 }
        r3 = (long) r3;	 Catch:{ all -> 0x0045 }
        r5 = r8 + r3;	 Catch:{ all -> 0x0045 }
        r8 = r7.byteBuffer;	 Catch:{ all -> 0x0045 }
        r8 = r8.hasRemaining();	 Catch:{ all -> 0x0045 }
        if (r8 != 0) goto L_0x0043;
    L_0x0038:
        r8 = (long) r2;
        r2 = r11 - r8;
        r8 = r7.byteBuffer;
        r8.clear();
        r11 = r2;
        r8 = r5;
        goto L_0x000f;
    L_0x0043:
        r8 = r5;
        goto L_0x0025;
    L_0x0045:
        r8 = move-exception;
        r9 = r7.byteBuffer;
        r9.clear();
        throw r8;
    L_0x004c:
        return;
    L_0x004d:
        r8 = new java.lang.IndexOutOfBoundsException;
        r8.<init>();
        throw r8;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache2.FileOperator.write(long, okio.Buffer, long):void");
    }

    FileOperator(FileChannel fileChannel) {
        this.fileChannel = fileChannel;
    }
}
