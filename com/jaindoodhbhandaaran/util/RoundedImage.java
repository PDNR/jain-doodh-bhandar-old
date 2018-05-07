package com.jaindoodhbhandaaran.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

public class RoundedImage {
    String Tag = getClass().getName();

    public static Bitmap GetBitmapClippedCircle(Bitmap bitmap) {
        StringBuilder stringBuilder;
        Bitmap bitmap2 = null;
        try {
            Bitmap createBitmap;
            if (bitmap.getWidth() > bitmap.getHeight()) {
                createBitmap = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Config.ARGB_8888);
            } else {
                createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Config.ARGB_8888);
            }
            bitmap2 = createBitmap;
            if (bitmap2 != null) {
                float height;
                Canvas canvas = new Canvas(bitmap2);
                Paint paint = new Paint();
                Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                if (bitmap.getWidth() > bitmap.getHeight()) {
                    height = (float) (bitmap.getHeight() / 2);
                } else {
                    height = (float) (bitmap.getWidth() / 2);
                }
                paint.setAntiAlias(true);
                canvas.drawARGB(0, 0, 0, 0);
                paint.setColor(-12434878);
                canvas.drawCircle(height, height, height, paint);
                paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
                canvas.drawBitmap(bitmap, rect, rect, paint);
            }
        } catch (Bitmap bitmap3) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(bitmap3.toString());
            Log.e("RoundedImage", stringBuilder.toString());
        } catch (Bitmap bitmap32) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(bitmap32.toString());
            Log.e("RoundedImage", stringBuilder.toString());
        }
        return bitmap2;
    }

    public static Bitmap getRoundedCornerImage(Bitmap bitmap) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(-12434878);
        canvas.drawRoundRect(rectF, 7.0f, 7.0f, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return createBitmap;
    }
}
