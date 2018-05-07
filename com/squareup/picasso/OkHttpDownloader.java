package com.squareup.picasso;

import android.content.Context;
import android.net.Uri;
import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.CacheControl.Builder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Downloader.Response;
import com.squareup.picasso.Downloader.ResponseException;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OkHttpDownloader implements Downloader {
    private final OkHttpClient client;

    private static OkHttpClient defaultOkHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(15000, TimeUnit.MILLISECONDS);
        okHttpClient.setReadTimeout(20000, TimeUnit.MILLISECONDS);
        okHttpClient.setWriteTimeout(20000, TimeUnit.MILLISECONDS);
        return okHttpClient;
    }

    public OkHttpDownloader(Context context) {
        this(Utils.createDefaultCacheDir(context));
    }

    public OkHttpDownloader(File file) {
        this(file, Utils.calculateDiskCacheSize(file));
    }

    public OkHttpDownloader(Context context, long j) {
        this(Utils.createDefaultCacheDir(context), j);
    }

    public OkHttpDownloader(java.io.File r3, long r4) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r2 = this;
        r0 = defaultOkHttpClient();
        r2.<init>(r0);
        r0 = r2.client;	 Catch:{ IOException -> 0x0011 }
        r1 = new com.squareup.okhttp.Cache;	 Catch:{ IOException -> 0x0011 }
        r1.<init>(r3, r4);	 Catch:{ IOException -> 0x0011 }
        r0.setCache(r1);	 Catch:{ IOException -> 0x0011 }
    L_0x0011:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.squareup.picasso.OkHttpDownloader.<init>(java.io.File, long):void");
    }

    public OkHttpDownloader(OkHttpClient okHttpClient) {
        this.client = okHttpClient;
    }

    protected final OkHttpClient getClient() {
        return this.client;
    }

    public Response load(Uri uri, int i) throws IOException {
        CacheControl cacheControl;
        if (i == 0) {
            cacheControl = null;
        } else if (NetworkPolicy.isOfflineOnly(i)) {
            cacheControl = CacheControl.FORCE_CACHE;
        } else {
            Builder builder = new Builder();
            if (!NetworkPolicy.shouldReadFromDiskCache(i)) {
                builder.noCache();
            }
            if (!NetworkPolicy.shouldWriteToDiskCache(i)) {
                builder.noStore();
            }
            cacheControl = builder.build();
        }
        uri = new Request.Builder().url(uri.toString());
        if (cacheControl != null) {
            uri.cacheControl(cacheControl);
        }
        uri = this.client.newCall(uri.build()).execute();
        int code = uri.code();
        if (code >= 300) {
            uri.body().close();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(code);
            stringBuilder.append(" ");
            stringBuilder.append(uri.message());
            throw new ResponseException(stringBuilder.toString(), i, code);
        }
        boolean z = uri.cacheResponse() != 0;
        uri = uri.body();
        return new Response(uri.byteStream(), z, uri.contentLength());
    }

    public void shutdown() {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r1 = this;
        r0 = r1.client;
        r0 = r0.getCache();
        if (r0 == 0) goto L_0x000b;
    L_0x0008:
        r0.close();	 Catch:{ IOException -> 0x000b }
    L_0x000b:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.squareup.picasso.OkHttpDownloader.shutdown():void");
    }
}
