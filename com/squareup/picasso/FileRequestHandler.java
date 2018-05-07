package com.squareup.picasso;

import android.content.Context;
import android.media.ExifInterface;
import android.net.Uri;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.RequestHandler.Result;
import java.io.IOException;

class FileRequestHandler extends ContentStreamRequestHandler {
    FileRequestHandler(Context context) {
        super(context);
    }

    public boolean canHandleRequest(Request request) {
        return "file".equals(request.uri.getScheme());
    }

    public Result load(Request request, int i) throws IOException {
        return new Result(null, getInputStream(request), LoadedFrom.DISK, getFileExifRotation(request.uri));
    }

    static int getFileExifRotation(Uri uri) throws IOException {
        uri = new ExifInterface(uri.getPath()).getAttributeInt("Orientation", 1);
        if (uri == 3) {
            return 180;
        }
        if (uri != 6) {
            return uri != 8 ? null : 270;
        } else {
            return 90;
        }
    }
}
