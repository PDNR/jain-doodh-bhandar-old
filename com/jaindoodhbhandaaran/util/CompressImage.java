package com.jaindoodhbhandaaran.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;

public class CompressImage {
    Context context;

    public CompressImage(Context context) {
        this.context = context;
    }

    public Bitmap compress(String str) {
        Bitmap decodeFile;
        str = getRealPathFromURI(str);
        Options options = new Options();
        options.inJustDecodeBounds = true;
        Bitmap decodeFile2 = BitmapFactory.decodeFile(str, options);
        int i = options.outHeight;
        int i2 = options.outWidth;
        float f = (float) (i2 / i);
        float f2 = (float) i;
        if (f2 > 666.0f || ((float) i2) > 462.0f) {
            if (f < 0.6936937f) {
                i2 = (int) ((666.0f / f2) * ((float) i2));
                i = (int) 1143373824;
            } else if (f > 0.6936937f) {
                i = (int) ((462.0f / ((float) i2)) * f2);
                i2 = (int) 1139212288;
            } else {
                i = (int) 1143373824;
                i2 = (int) 1139212288;
            }
        }
        options.inSampleSize = calculateInSampleSize(options, i2, i);
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16384];
        try {
            decodeFile = BitmapFactory.decodeFile(str, options);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            decodeFile = decodeFile2;
        }
        try {
            decodeFile2 = Bitmap.createBitmap(i2, i, Config.ARGB_8888);
        } catch (OutOfMemoryError e2) {
            e2.printStackTrace();
            decodeFile2 = null;
        }
        float f3 = (float) i2;
        f2 = f3 / ((float) options.outWidth);
        float f4 = (float) i;
        float f5 = f4 / ((float) options.outHeight);
        f3 /= 2.0f;
        f4 /= 2.0f;
        Matrix matrix = new Matrix();
        matrix.setScale(f2, f5, f3, f4);
        Canvas canvas = new Canvas(decodeFile2);
        canvas.setMatrix(matrix);
        canvas.drawBitmap(decodeFile, f3 - ((float) (decodeFile.getWidth() / 2)), f4 - ((float) (decodeFile.getHeight() / 2)), new Paint(2));
        try {
            str = new ExifInterface(str).getAttributeInt("Orientation", 0);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Exif: ");
            stringBuilder.append(str);
            Log.d("EXIF", stringBuilder.toString());
            Matrix matrix2 = new Matrix();
            if (str == 6) {
                matrix2.postRotate(90.0f);
                stringBuilder = new StringBuilder();
                stringBuilder.append("Exif: ");
                stringBuilder.append(str);
                Log.d("EXIF", stringBuilder.toString());
            } else if (str == 3) {
                matrix2.postRotate(180.0f);
                stringBuilder = new StringBuilder();
                stringBuilder.append("Exif: ");
                stringBuilder.append(str);
                Log.d("EXIF", stringBuilder.toString());
            } else if (str == 8) {
                matrix2.postRotate(270.0f);
                stringBuilder = new StringBuilder();
                stringBuilder.append("Exif: ");
                stringBuilder.append(str);
                Log.d("EXIF", stringBuilder.toString());
            }
            return Bitmap.createBitmap(decodeFile2, 0, 0, decodeFile2.getWidth(), decodeFile2.getHeight(), matrix2, true);
        } catch (String str2) {
            str2.printStackTrace();
            return decodeFile2;
        }
    }

    private String getRealPathFromURI(String str) {
        str = Uri.parse(str);
        Cursor query = this.context.getContentResolver().query(str, null, null, null, null);
        if (query == null) {
            return str.getPath();
        }
        query.moveToFirst();
        return query.getString(query.getColumnIndex("_data"));
    }

    public int calculateInSampleSize(Options options, int i, int i2) {
        int i3;
        int i4 = options.outHeight;
        options = options.outWidth;
        if (i4 <= i2) {
            if (options <= i) {
                i3 = 1;
                while (((float) (options * i4)) / ((float) (i3 * i3)) > ((float) ((i * i2) * 2))) {
                    i3++;
                }
                return i3;
            }
        }
        i3 = Math.round(((float) i4) / ((float) i2));
        int round = Math.round(((float) options) / ((float) i));
        if (i3 >= round) {
            i3 = round;
        }
        while (((float) (options * i4)) / ((float) (i3 * i3)) > ((float) ((i * i2) * 2))) {
            i3++;
        }
        return i3;
    }
}
