package com.squareup.picasso;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ThreadFactory;

final class Utils {
    static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 15000;
    static final int DEFAULT_READ_TIMEOUT_MILLIS = 20000;
    static final int DEFAULT_WRITE_TIMEOUT_MILLIS = 20000;
    private static final int KEY_PADDING = 50;
    static final char KEY_SEPARATOR = '\n';
    static final StringBuilder MAIN_THREAD_KEY_BUILDER = new StringBuilder();
    private static final int MAX_DISK_CACHE_SIZE = 52428800;
    private static final int MIN_DISK_CACHE_SIZE = 5242880;
    static final String OWNER_DISPATCHER = "Dispatcher";
    static final String OWNER_HUNTER = "Hunter";
    static final String OWNER_MAIN = "Main";
    private static final String PICASSO_CACHE = "picasso-cache";
    static final String THREAD_IDLE_NAME = "Picasso-Idle";
    static final int THREAD_LEAK_CLEANING_MS = 1000;
    static final String THREAD_PREFIX = "Picasso-";
    static final String VERB_BATCHED = "batched";
    static final String VERB_CANCELED = "canceled";
    static final String VERB_CHANGED = "changed";
    static final String VERB_COMPLETED = "completed";
    static final String VERB_CREATED = "created";
    static final String VERB_DECODED = "decoded";
    static final String VERB_DELIVERED = "delivered";
    static final String VERB_ENQUEUED = "enqueued";
    static final String VERB_ERRORED = "errored";
    static final String VERB_EXECUTING = "executing";
    static final String VERB_IGNORED = "ignored";
    static final String VERB_JOINED = "joined";
    static final String VERB_PAUSED = "paused";
    static final String VERB_REMOVED = "removed";
    static final String VERB_REPLAYING = "replaying";
    static final String VERB_RESUMED = "resumed";
    static final String VERB_RETRYING = "retrying";
    static final String VERB_TRANSFORMED = "transformed";
    private static final String WEBP_FILE_HEADER_RIFF = "RIFF";
    private static final int WEBP_FILE_HEADER_SIZE = 12;
    private static final String WEBP_FILE_HEADER_WEBP = "WEBP";

    @TargetApi(11)
    private static class ActivityManagerHoneycomb {
        private ActivityManagerHoneycomb() {
        }

        static int getLargeMemoryClass(ActivityManager activityManager) {
            return activityManager.getLargeMemoryClass();
        }
    }

    @TargetApi(12)
    private static class BitmapHoneycombMR1 {
        private BitmapHoneycombMR1() {
        }

        static int getByteCount(Bitmap bitmap) {
            return bitmap.getByteCount();
        }
    }

    private static class OkHttpLoaderCreator {
        private OkHttpLoaderCreator() {
        }

        static Downloader create(Context context) {
            return new OkHttpDownloader(context);
        }
    }

    private static class PicassoThread extends Thread {
        public PicassoThread(Runnable runnable) {
            super(runnable);
        }

        public void run() {
            Process.setThreadPriority(10);
            super.run();
        }
    }

    static class PicassoThreadFactory implements ThreadFactory {
        PicassoThreadFactory() {
        }

        public Thread newThread(Runnable runnable) {
            return new PicassoThread(runnable);
        }
    }

    private Utils() {
    }

    static int getBitmapBytes(Bitmap bitmap) {
        int byteCount;
        if (VERSION.SDK_INT >= 12) {
            byteCount = BitmapHoneycombMR1.getByteCount(bitmap);
        } else {
            byteCount = bitmap.getRowBytes() * bitmap.getHeight();
        }
        if (byteCount >= 0) {
            return byteCount;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Negative size: ");
        stringBuilder.append(bitmap);
        throw new IllegalStateException(stringBuilder.toString());
    }

    static <T> T checkNotNull(T t, String str) {
        if (t != null) {
            return t;
        }
        throw new NullPointerException(str);
    }

    static void checkNotMain() {
        if (isMain()) {
            throw new IllegalStateException("Method call should not happen from the main thread.");
        }
    }

    static void checkMain() {
        if (!isMain()) {
            throw new IllegalStateException("Method call should happen from the main thread.");
        }
    }

    static boolean isMain() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    static String getLogIdsForHunter(BitmapHunter bitmapHunter) {
        return getLogIdsForHunter(bitmapHunter, "");
    }

    static String getLogIdsForHunter(BitmapHunter bitmapHunter, String str) {
        StringBuilder stringBuilder = new StringBuilder(str);
        str = bitmapHunter.getAction();
        if (str != null) {
            stringBuilder.append(str.request.logId());
        }
        bitmapHunter = bitmapHunter.getActions();
        if (bitmapHunter != null) {
            int size = bitmapHunter.size();
            for (int i = 0; i < size; i++) {
                if (i > 0 || str != null) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append(((Action) bitmapHunter.get(i)).request.logId());
            }
        }
        return stringBuilder.toString();
    }

    static void log(String str, String str2, String str3) {
        log(str, str2, str3, "");
    }

    static void log(String str, String str2, String str3, String str4) {
        Log.d("Picasso", String.format("%1$-11s %2$-12s %3$s %4$s", new Object[]{str, str2, str3, str4}));
    }

    static String createKey(Request request) {
        request = createKey(request, MAIN_THREAD_KEY_BUILDER);
        MAIN_THREAD_KEY_BUILDER.setLength(0);
        return request;
    }

    static String createKey(Request request, StringBuilder stringBuilder) {
        if (request.stableKey != null) {
            stringBuilder.ensureCapacity(request.stableKey.length() + 50);
            stringBuilder.append(request.stableKey);
        } else if (request.uri != null) {
            String uri = request.uri.toString();
            stringBuilder.ensureCapacity(uri.length() + 50);
            stringBuilder.append(uri);
        } else {
            stringBuilder.ensureCapacity(50);
            stringBuilder.append(request.resourceId);
        }
        stringBuilder.append(KEY_SEPARATOR);
        if (request.rotationDegrees != 0.0f) {
            stringBuilder.append("rotation:");
            stringBuilder.append(request.rotationDegrees);
            if (request.hasRotationPivot) {
                stringBuilder.append('@');
                stringBuilder.append(request.rotationPivotX);
                stringBuilder.append('x');
                stringBuilder.append(request.rotationPivotY);
            }
            stringBuilder.append(KEY_SEPARATOR);
        }
        if (request.hasSize()) {
            stringBuilder.append("resize:");
            stringBuilder.append(request.targetWidth);
            stringBuilder.append('x');
            stringBuilder.append(request.targetHeight);
            stringBuilder.append(KEY_SEPARATOR);
        }
        if (request.centerCrop) {
            stringBuilder.append("centerCrop");
            stringBuilder.append(KEY_SEPARATOR);
        } else if (request.centerInside) {
            stringBuilder.append("centerInside");
            stringBuilder.append(KEY_SEPARATOR);
        }
        if (request.transformations != null) {
            int size = request.transformations.size();
            for (int i = 0; i < size; i++) {
                stringBuilder.append(((Transformation) request.transformations.get(i)).key());
                stringBuilder.append(KEY_SEPARATOR);
            }
        }
        return stringBuilder.toString();
    }

    static void closeQuietly(java.io.InputStream r0) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
        /*
        if (r0 != 0) goto L_0x0003;
    L_0x0002:
        return;
    L_0x0003:
        r0.close();	 Catch:{ IOException -> 0x0006 }
    L_0x0006:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.squareup.picasso.Utils.closeQuietly(java.io.InputStream):void");
    }

    static boolean parseResponseSourceHeader(java.lang.String r4) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
        /*
        r0 = 0;
        if (r4 != 0) goto L_0x0004;
    L_0x0003:
        return r0;
    L_0x0004:
        r1 = " ";
        r2 = 2;
        r4 = r4.split(r1, r2);
        r1 = "CACHE";
        r2 = r4[r0];
        r1 = r1.equals(r2);
        r2 = 1;
        if (r1 == 0) goto L_0x0017;
    L_0x0016:
        return r2;
    L_0x0017:
        r1 = r4.length;
        if (r1 != r2) goto L_0x001b;
    L_0x001a:
        return r0;
    L_0x001b:
        r1 = "CONDITIONAL_CACHE";	 Catch:{ NumberFormatException -> 0x0031 }
        r3 = r4[r0];	 Catch:{ NumberFormatException -> 0x0031 }
        r1 = r1.equals(r3);	 Catch:{ NumberFormatException -> 0x0031 }
        if (r1 == 0) goto L_0x0030;	 Catch:{ NumberFormatException -> 0x0031 }
    L_0x0025:
        r4 = r4[r2];	 Catch:{ NumberFormatException -> 0x0031 }
        r4 = java.lang.Integer.parseInt(r4);	 Catch:{ NumberFormatException -> 0x0031 }
        r1 = 304; // 0x130 float:4.26E-43 double:1.5E-321;
        if (r4 != r1) goto L_0x0030;
    L_0x002f:
        r0 = r2;
    L_0x0030:
        return r0;
    L_0x0031:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.squareup.picasso.Utils.parseResponseSourceHeader(java.lang.String):boolean");
    }

    static com.squareup.picasso.Downloader createDefaultDownloader(android.content.Context r1) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
        /*
        r0 = "com.squareup.okhttp.OkHttpClient";	 Catch:{ ClassNotFoundException -> 0x000a }
        java.lang.Class.forName(r0);	 Catch:{ ClassNotFoundException -> 0x000a }
        r0 = com.squareup.picasso.Utils.OkHttpLoaderCreator.create(r1);	 Catch:{ ClassNotFoundException -> 0x000a }
        return r0;
    L_0x000a:
        r0 = new com.squareup.picasso.UrlConnectionDownloader;
        r0.<init>(r1);
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.squareup.picasso.Utils.createDefaultDownloader(android.content.Context):com.squareup.picasso.Downloader");
    }

    static File createDefaultCacheDir(Context context) {
        File file = new File(context.getApplicationContext().getCacheDir(), PICASSO_CACHE);
        if (file.exists() == null) {
            file.mkdirs();
        }
        return file;
    }

    static long calculateDiskCacheSize(java.io.File r7) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
        /*
        r0 = 5242880; // 0x500000 float:7.34684E-39 double:2.590327E-317;
        r2 = new android.os.StatFs;	 Catch:{ IllegalArgumentException -> 0x001c }
        r7 = r7.getAbsolutePath();	 Catch:{ IllegalArgumentException -> 0x001c }
        r2.<init>(r7);	 Catch:{ IllegalArgumentException -> 0x001c }
        r7 = r2.getBlockCount();	 Catch:{ IllegalArgumentException -> 0x001c }
        r3 = (long) r7;	 Catch:{ IllegalArgumentException -> 0x001c }
        r7 = r2.getBlockSize();	 Catch:{ IllegalArgumentException -> 0x001c }
        r5 = (long) r7;	 Catch:{ IllegalArgumentException -> 0x001c }
        r3 = r3 * r5;	 Catch:{ IllegalArgumentException -> 0x001c }
        r5 = 50;	 Catch:{ IllegalArgumentException -> 0x001c }
        r2 = r3 / r5;	 Catch:{ IllegalArgumentException -> 0x001c }
        goto L_0x001d;
    L_0x001c:
        r2 = r0;
    L_0x001d:
        r4 = 52428800; // 0x3200000 float:4.7019774E-37 double:2.5903269E-316;
        r2 = java.lang.Math.min(r2, r4);
        r0 = java.lang.Math.max(r2, r0);
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.squareup.picasso.Utils.calculateDiskCacheSize(java.io.File):long");
    }

    static int calculateMemoryCacheSize(Context context) {
        ActivityManager activityManager = (ActivityManager) getService(context, "activity");
        context = (context.getApplicationInfo().flags & 1048576) != null ? true : null;
        int memoryClass = activityManager.getMemoryClass();
        if (context != null && VERSION.SDK_INT >= 11) {
            memoryClass = ActivityManagerHoneycomb.getLargeMemoryClass(activityManager);
        }
        return (1048576 * memoryClass) / 7;
    }

    static boolean isAirplaneModeOn(android.content.Context r2) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
        /*
        r2 = r2.getContentResolver();
        r0 = 0;
        r1 = "airplane_mode_on";	 Catch:{ NullPointerException -> 0x000f }
        r2 = android.provider.Settings.System.getInt(r2, r1, r0);	 Catch:{ NullPointerException -> 0x000f }
        if (r2 == 0) goto L_0x000e;
    L_0x000d:
        r0 = 1;
    L_0x000e:
        return r0;
    L_0x000f:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.squareup.picasso.Utils.isAirplaneModeOn(android.content.Context):boolean");
    }

    static <T> T getService(Context context, String str) {
        return context.getSystemService(str);
    }

    static boolean hasPermission(Context context, String str) {
        return context.checkCallingOrSelfPermission(str) == null ? true : null;
    }

    static byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[4096];
        while (true) {
            int read = inputStream.read(bArr);
            if (-1 == read) {
                return byteArrayOutputStream.toByteArray();
            }
            byteArrayOutputStream.write(bArr, 0, read);
        }
    }

    static boolean isWebPFile(InputStream inputStream) throws IOException {
        byte[] bArr = new byte[12];
        if (inputStream.read(bArr, 0, 12) != 12 || WEBP_FILE_HEADER_RIFF.equals(new String(bArr, 0, 4, "US-ASCII")) == null || WEBP_FILE_HEADER_WEBP.equals(new String(bArr, 8, 4, "US-ASCII")) == null) {
            return false;
        }
        return true;
    }

    static int getResourceId(android.content.res.Resources r6, com.squareup.picasso.Request r7) throws java.io.FileNotFoundException {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
        /*
        r0 = r7.resourceId;
        if (r0 != 0) goto L_0x00b0;
    L_0x0004:
        r0 = r7.uri;
        if (r0 != 0) goto L_0x000a;
    L_0x0008:
        goto L_0x00b0;
    L_0x000a:
        r0 = r7.uri;
        r0 = r0.getAuthority();
        if (r0 != 0) goto L_0x002b;
    L_0x0012:
        r6 = new java.io.FileNotFoundException;
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "No package provided: ";
        r0.append(r1);
        r7 = r7.uri;
        r0.append(r7);
        r7 = r0.toString();
        r6.<init>(r7);
        throw r6;
    L_0x002b:
        r1 = r7.uri;
        r1 = r1.getPathSegments();
        if (r1 == 0) goto L_0x0097;
    L_0x0033:
        r2 = r1.isEmpty();
        if (r2 == 0) goto L_0x003a;
    L_0x0039:
        goto L_0x0097;
    L_0x003a:
        r2 = r1.size();
        r3 = 0;
        r4 = 1;
        if (r2 != r4) goto L_0x0066;
    L_0x0042:
        r6 = r1.get(r3);	 Catch:{ NumberFormatException -> 0x004d }
        r6 = (java.lang.String) r6;	 Catch:{ NumberFormatException -> 0x004d }
        r6 = java.lang.Integer.parseInt(r6);	 Catch:{ NumberFormatException -> 0x004d }
        goto L_0x007d;
    L_0x004d:
        r6 = new java.io.FileNotFoundException;
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "Last path segment is not a resource ID: ";
        r0.append(r1);
        r7 = r7.uri;
        r0.append(r7);
        r7 = r0.toString();
        r6.<init>(r7);
        throw r6;
    L_0x0066:
        r2 = r1.size();
        r5 = 2;
        if (r2 != r5) goto L_0x007e;
    L_0x006d:
        r7 = r1.get(r3);
        r7 = (java.lang.String) r7;
        r1 = r1.get(r4);
        r1 = (java.lang.String) r1;
        r6 = r6.getIdentifier(r1, r7, r0);
    L_0x007d:
        return r6;
    L_0x007e:
        r6 = new java.io.FileNotFoundException;
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "More than two path segments: ";
        r0.append(r1);
        r7 = r7.uri;
        r0.append(r7);
        r7 = r0.toString();
        r6.<init>(r7);
        throw r6;
    L_0x0097:
        r6 = new java.io.FileNotFoundException;
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "No path segments: ";
        r0.append(r1);
        r7 = r7.uri;
        r0.append(r7);
        r7 = r0.toString();
        r6.<init>(r7);
        throw r6;
    L_0x00b0:
        r6 = r7.resourceId;
        return r6;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.squareup.picasso.Utils.getResourceId(android.content.res.Resources, com.squareup.picasso.Request):int");
    }

    static android.content.res.Resources getResources(android.content.Context r2, com.squareup.picasso.Request r3) throws java.io.FileNotFoundException {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
        /*
        r0 = r3.resourceId;
        if (r0 != 0) goto L_0x004c;
    L_0x0004:
        r0 = r3.uri;
        if (r0 != 0) goto L_0x0009;
    L_0x0008:
        goto L_0x004c;
    L_0x0009:
        r0 = r3.uri;
        r0 = r0.getAuthority();
        if (r0 != 0) goto L_0x002a;
    L_0x0011:
        r2 = new java.io.FileNotFoundException;
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "No package provided: ";
        r0.append(r1);
        r3 = r3.uri;
        r0.append(r3);
        r3 = r0.toString();
        r2.<init>(r3);
        throw r2;
    L_0x002a:
        r2 = r2.getPackageManager();	 Catch:{ NameNotFoundException -> 0x0033 }
        r2 = r2.getResourcesForApplication(r0);	 Catch:{ NameNotFoundException -> 0x0033 }
        return r2;
    L_0x0033:
        r2 = new java.io.FileNotFoundException;
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "Unable to obtain resources for package: ";
        r0.append(r1);
        r3 = r3.uri;
        r0.append(r3);
        r3 = r0.toString();
        r2.<init>(r3);
        throw r2;
    L_0x004c:
        r2 = r2.getResources();
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.squareup.picasso.Utils.getResources(android.content.Context, com.squareup.picasso.Request):android.content.res.Resources");
    }

    static void flushStackLocalLeaks(Looper looper) {
        Handler anonymousClass1 = new Handler(looper) {
            public void handleMessage(Message message) {
                sendMessageDelayed(obtainMessage(), 1000);
            }
        };
        anonymousClass1.sendMessageDelayed(anonymousClass1.obtainMessage(), 1000);
    }
}
