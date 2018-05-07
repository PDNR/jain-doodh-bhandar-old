package com.squareup.picasso;

import android.content.Context;
import android.net.Uri;
import android.net.http.HttpResponseCache;
import android.os.Build.VERSION;
import com.squareup.picasso.Downloader.Response;
import com.squareup.picasso.Downloader.ResponseException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class UrlConnectionDownloader implements Downloader {
    private static final ThreadLocal<StringBuilder> CACHE_HEADER_BUILDER = new ThreadLocal<StringBuilder>() {
        protected StringBuilder initialValue() {
            return new StringBuilder();
        }
    };
    private static final String FORCE_CACHE = "only-if-cached,max-age=2147483647";
    static final String RESPONSE_SOURCE = "X-Android-Response-Source";
    static volatile Object cache;
    private static final Object lock = new Object();
    private final Context context;

    private static class ResponseCacheIcs {
        private ResponseCacheIcs() {
        }

        static Object install(Context context) throws IOException {
            context = Utils.createDefaultCacheDir(context);
            Object installed = HttpResponseCache.getInstalled();
            return installed == null ? HttpResponseCache.install(context, Utils.calculateDiskCacheSize(context)) : installed;
        }

        static void close(java.lang.Object r0) {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
            /*
            r0 = (android.net.http.HttpResponseCache) r0;	 Catch:{ IOException -> 0x0005 }
            r0.close();	 Catch:{ IOException -> 0x0005 }
        L_0x0005:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.squareup.picasso.UrlConnectionDownloader.ResponseCacheIcs.close(java.lang.Object):void");
        }
    }

    public UrlConnectionDownloader(Context context) {
        this.context = context.getApplicationContext();
    }

    protected HttpURLConnection openConnection(Uri uri) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(uri.toString()).openConnection();
        httpURLConnection.setConnectTimeout(15000);
        httpURLConnection.setReadTimeout(20000);
        return httpURLConnection;
    }

    public Response load(Uri uri, int i) throws IOException {
        if (VERSION.SDK_INT >= 14) {
            installCacheIfNeeded(this.context);
        }
        uri = openConnection(uri);
        uri.setUseCaches(true);
        if (i != 0) {
            String str;
            if (NetworkPolicy.isOfflineOnly(i)) {
                str = FORCE_CACHE;
            } else {
                StringBuilder stringBuilder = (StringBuilder) CACHE_HEADER_BUILDER.get();
                stringBuilder.setLength(0);
                if (!NetworkPolicy.shouldReadFromDiskCache(i)) {
                    stringBuilder.append("no-cache");
                }
                if (!NetworkPolicy.shouldWriteToDiskCache(i)) {
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append(',');
                    }
                    stringBuilder.append("no-store");
                }
                str = stringBuilder.toString();
            }
            uri.setRequestProperty("Cache-Control", str);
        }
        int responseCode = uri.getResponseCode();
        if (responseCode >= 300) {
            uri.disconnect();
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(responseCode);
            stringBuilder2.append(" ");
            stringBuilder2.append(uri.getResponseMessage());
            throw new ResponseException(stringBuilder2.toString(), i, responseCode);
        }
        long headerFieldInt = (long) uri.getHeaderFieldInt("Content-Length", -1);
        return new Response(uri.getInputStream(), Utils.parseResponseSourceHeader(uri.getHeaderField(RESPONSE_SOURCE)), headerFieldInt);
    }

    public void shutdown() {
        if (VERSION.SDK_INT >= 14 && cache != null) {
            ResponseCacheIcs.close(cache);
        }
    }

    private static void installCacheIfNeeded(android.content.Context r2) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r0 = cache;
        if (r0 != 0) goto L_0x0016;
    L_0x0004:
        r0 = lock;	 Catch:{ IOException -> 0x0016 }
        monitor-enter(r0);	 Catch:{ IOException -> 0x0016 }
        r1 = cache;	 Catch:{ all -> 0x0013 }
        if (r1 != 0) goto L_0x0011;	 Catch:{ all -> 0x0013 }
    L_0x000b:
        r2 = com.squareup.picasso.UrlConnectionDownloader.ResponseCacheIcs.install(r2);	 Catch:{ all -> 0x0013 }
        cache = r2;	 Catch:{ all -> 0x0013 }
    L_0x0011:
        monitor-exit(r0);	 Catch:{ all -> 0x0013 }
        goto L_0x0016;	 Catch:{ all -> 0x0013 }
    L_0x0013:
        r2 = move-exception;	 Catch:{ all -> 0x0013 }
        monitor-exit(r0);	 Catch:{ all -> 0x0013 }
        throw r2;	 Catch:{ IOException -> 0x0016 }
    L_0x0016:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.squareup.picasso.UrlConnectionDownloader.installCacheIfNeeded(android.content.Context):void");
    }
}
