package com.squareup.picasso;

import android.graphics.Bitmap;
import android.net.NetworkInfo;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.RequestHandler.Result;
import java.io.IOException;
import java.io.InputStream;

class NetworkRequestHandler extends RequestHandler {
    static final int RETRY_COUNT = 2;
    private static final String SCHEME_HTTP = "http";
    private static final String SCHEME_HTTPS = "https";
    private final Downloader downloader;
    private final Stats stats;

    static class ContentLengthException extends IOException {
        public ContentLengthException(String str) {
            super(str);
        }
    }

    int getRetryCount() {
        return 2;
    }

    boolean supportsReplay() {
        return true;
    }

    public NetworkRequestHandler(Downloader downloader, Stats stats) {
        this.downloader = downloader;
        this.stats = stats;
    }

    public boolean canHandleRequest(Request request) {
        request = request.uri.getScheme();
        if (!SCHEME_HTTP.equals(request)) {
            if (SCHEME_HTTPS.equals(request) == null) {
                return null;
            }
        }
        return true;
    }

    public Result load(Request request, int i) throws IOException {
        request = this.downloader.load(request.uri, request.networkPolicy);
        if (request == null) {
            return null;
        }
        LoadedFrom loadedFrom = request.cached ? LoadedFrom.DISK : LoadedFrom.NETWORK;
        Bitmap bitmap = request.getBitmap();
        if (bitmap != null) {
            return new Result(bitmap, loadedFrom);
        }
        InputStream inputStream = request.getInputStream();
        if (inputStream == null) {
            return null;
        }
        if (loadedFrom == LoadedFrom.DISK && request.getContentLength() == 0) {
            Utils.closeQuietly(inputStream);
            throw new ContentLengthException("Received response with 0 content-length header.");
        }
        if (loadedFrom == LoadedFrom.NETWORK && request.getContentLength() > 0) {
            this.stats.dispatchDownloadFinished(request.getContentLength());
        }
        return new Result(inputStream, loadedFrom);
    }

    boolean shouldRetry(boolean z, NetworkInfo networkInfo) {
        if (networkInfo != null) {
            if (!networkInfo.isConnected()) {
                return false;
            }
        }
        return true;
    }
}
