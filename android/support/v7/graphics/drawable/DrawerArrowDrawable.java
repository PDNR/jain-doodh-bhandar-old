package android.support.v7.graphics.drawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.appcompat.R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class DrawerArrowDrawable extends Drawable {
    public static final int ARROW_DIRECTION_END = 3;
    public static final int ARROW_DIRECTION_LEFT = 0;
    public static final int ARROW_DIRECTION_RIGHT = 1;
    public static final int ARROW_DIRECTION_START = 2;
    private static final float ARROW_HEAD_ANGLE = ((float) Math.toRadians(45.0d));
    private float mArrowHeadLength;
    private float mArrowShaftLength;
    private float mBarGap;
    private float mBarLength;
    private int mDirection = 2;
    private float mMaxCutForBarSize;
    private final Paint mPaint = new Paint();
    private final Path mPath = new Path();
    private float mProgress;
    private final int mSize;
    private boolean mSpin;
    private boolean mVerticalMirror = false;

    @RestrictTo({Scope.LIBRARY_GROUP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ArrowDirection {
    }

    private static float lerp(float f, float f2, float f3) {
        return f + ((f2 - f) * f3);
    }

    public int getOpacity() {
        return -3;
    }

    public DrawerArrowDrawable(Context context) {
        this.mPaint.setStyle(Style.STROKE);
        this.mPaint.setStrokeJoin(Join.MITER);
        this.mPaint.setStrokeCap(Cap.BUTT);
        this.mPaint.setAntiAlias(true);
        context = context.getTheme().obtainStyledAttributes(null, R.styleable.DrawerArrowToggle, R.attr.drawerArrowStyle, R.style.Base_Widget_AppCompat_DrawerArrowToggle);
        setColor(context.getColor(R.styleable.DrawerArrowToggle_color, 0));
        setBarThickness(context.getDimension(R.styleable.DrawerArrowToggle_thickness, 0.0f));
        setSpinEnabled(context.getBoolean(R.styleable.DrawerArrowToggle_spinBars, true));
        setGapSize((float) Math.round(context.getDimension(R.styleable.DrawerArrowToggle_gapBetweenBars, 0.0f)));
        this.mSize = context.getDimensionPixelSize(R.styleable.DrawerArrowToggle_drawableSize, 0);
        this.mBarLength = (float) Math.round(context.getDimension(R.styleable.DrawerArrowToggle_barLength, 0.0f));
        this.mArrowHeadLength = (float) Math.round(context.getDimension(R.styleable.DrawerArrowToggle_arrowHeadLength, 0.0f));
        this.mArrowShaftLength = context.getDimension(R.styleable.DrawerArrowToggle_arrowShaftLength, 0.0f);
        context.recycle();
    }

    public void setArrowHeadLength(float f) {
        if (this.mArrowHeadLength != f) {
            this.mArrowHeadLength = f;
            invalidateSelf();
        }
    }

    public float getArrowHeadLength() {
        return this.mArrowHeadLength;
    }

    public void setArrowShaftLength(float f) {
        if (this.mArrowShaftLength != f) {
            this.mArrowShaftLength = f;
            invalidateSelf();
        }
    }

    public float getArrowShaftLength() {
        return this.mArrowShaftLength;
    }

    public float getBarLength() {
        return this.mBarLength;
    }

    public void setBarLength(float f) {
        if (this.mBarLength != f) {
            this.mBarLength = f;
            invalidateSelf();
        }
    }

    public void setColor(@ColorInt int i) {
        if (i != this.mPaint.getColor()) {
            this.mPaint.setColor(i);
            invalidateSelf();
        }
    }

    @ColorInt
    public int getColor() {
        return this.mPaint.getColor();
    }

    public void setBarThickness(float f) {
        if (this.mPaint.getStrokeWidth() != f) {
            this.mPaint.setStrokeWidth(f);
            this.mMaxCutForBarSize = (float) (((double) (f / 2.0f)) * Math.cos((double) ARROW_HEAD_ANGLE));
            invalidateSelf();
        }
    }

    public float getBarThickness() {
        return this.mPaint.getStrokeWidth();
    }

    public float getGapSize() {
        return this.mBarGap;
    }

    public void setGapSize(float f) {
        if (f != this.mBarGap) {
            this.mBarGap = f;
            invalidateSelf();
        }
    }

    public void setDirection(int i) {
        if (i != this.mDirection) {
            this.mDirection = i;
            invalidateSelf();
        }
    }

    public boolean isSpinEnabled() {
        return this.mSpin;
    }

    public void setSpinEnabled(boolean z) {
        if (this.mSpin != z) {
            this.mSpin = z;
            invalidateSelf();
        }
    }

    public int getDirection() {
        return this.mDirection;
    }

    public void setVerticalMirror(boolean z) {
        if (this.mVerticalMirror != z) {
            this.mVerticalMirror = z;
            invalidateSelf();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void draw(Canvas canvas) {
        float lerp;
        float lerp2;
        float round;
        float lerp3;
        double d;
        float lerp4;
        double d2;
        int i;
        float round2;
        float lerp5;
        Canvas canvas2 = canvas;
        Rect bounds = getBounds();
        int i2 = this.mDirection;
        int i3 = 0;
        int i4 = 1;
        if (i2 != 3) {
            switch (i2) {
                case 0:
                    break;
                case 1:
                    break;
                default:
                    break;
            }
        }
        if (DrawableCompat.getLayoutDirection(this) == 0) {
        }
        lerp = lerp(r0.mBarLength, (float) Math.sqrt((double) ((r0.mArrowHeadLength * r0.mArrowHeadLength) * 2.0f)), r0.mProgress);
        lerp2 = lerp(r0.mBarLength, r0.mArrowShaftLength, r0.mProgress);
        round = (float) Math.round(lerp(0.0f, r0.mMaxCutForBarSize, r0.mProgress));
        lerp3 = lerp(0.0f, ARROW_HEAD_ANGLE, r0.mProgress);
        d = (double) lerp;
        lerp4 = lerp(i3 == 0 ? 0.0f : -180.0f, i3 == 0 ? 180.0f : 0.0f, r0.mProgress);
        d2 = (double) lerp3;
        i = i3;
        lerp = (float) Math.round(d * Math.cos(d2));
        round2 = (float) Math.round(d * Math.sin(d2));
        r0.mPath.rewind();
        lerp5 = lerp(r0.mBarGap + r0.mPaint.getStrokeWidth(), -r0.mMaxCutForBarSize, r0.mProgress);
        lerp3 = (-lerp2) / 2.0f;
        r0.mPath.moveTo(lerp3 + round, 0.0f);
        r0.mPath.rLineTo(lerp2 - (round * 2.0f), 0.0f);
        r0.mPath.moveTo(lerp3, lerp5);
        r0.mPath.rLineTo(lerp, round2);
        r0.mPath.moveTo(lerp3, -lerp5);
        r0.mPath.rLineTo(lerp, -round2);
        r0.mPath.close();
        canvas.save();
        lerp = r0.mPaint.getStrokeWidth();
        canvas2.translate((float) bounds.centerX(), ((float) ((((int) ((((float) bounds.height()) - (3.0f * lerp)) - (r0.mBarGap * 2.0f))) / 4) * 2)) + ((lerp * 1.5f) + r0.mBarGap));
        if (r0.mSpin) {
            if ((r0.mVerticalMirror ^ i) != 0) {
                i4 = -1;
            }
            canvas2.rotate(lerp4 * ((float) i4));
        } else if (i != 0) {
            canvas2.rotate(180.0f);
        }
        canvas2.drawPath(r0.mPath, r0.mPaint);
        canvas.restore();
        i3 = 1;
        lerp = lerp(r0.mBarLength, (float) Math.sqrt((double) ((r0.mArrowHeadLength * r0.mArrowHeadLength) * 2.0f)), r0.mProgress);
        lerp2 = lerp(r0.mBarLength, r0.mArrowShaftLength, r0.mProgress);
        round = (float) Math.round(lerp(0.0f, r0.mMaxCutForBarSize, r0.mProgress));
        lerp3 = lerp(0.0f, ARROW_HEAD_ANGLE, r0.mProgress);
        if (i3 == 0) {
        }
        if (i3 == 0) {
        }
        d = (double) lerp;
        lerp4 = lerp(i3 == 0 ? 0.0f : -180.0f, i3 == 0 ? 180.0f : 0.0f, r0.mProgress);
        d2 = (double) lerp3;
        i = i3;
        lerp = (float) Math.round(d * Math.cos(d2));
        round2 = (float) Math.round(d * Math.sin(d2));
        r0.mPath.rewind();
        lerp5 = lerp(r0.mBarGap + r0.mPaint.getStrokeWidth(), -r0.mMaxCutForBarSize, r0.mProgress);
        lerp3 = (-lerp2) / 2.0f;
        r0.mPath.moveTo(lerp3 + round, 0.0f);
        r0.mPath.rLineTo(lerp2 - (round * 2.0f), 0.0f);
        r0.mPath.moveTo(lerp3, lerp5);
        r0.mPath.rLineTo(lerp, round2);
        r0.mPath.moveTo(lerp3, -lerp5);
        r0.mPath.rLineTo(lerp, -round2);
        r0.mPath.close();
        canvas.save();
        lerp = r0.mPaint.getStrokeWidth();
        canvas2.translate((float) bounds.centerX(), ((float) ((((int) ((((float) bounds.height()) - (3.0f * lerp)) - (r0.mBarGap * 2.0f))) / 4) * 2)) + ((lerp * 1.5f) + r0.mBarGap));
        if (r0.mSpin) {
            if ((r0.mVerticalMirror ^ i) != 0) {
                i4 = -1;
            }
            canvas2.rotate(lerp4 * ((float) i4));
        } else if (i != 0) {
            canvas2.rotate(180.0f);
        }
        canvas2.drawPath(r0.mPath, r0.mPaint);
        canvas.restore();
    }

    public void setAlpha(int i) {
        if (i != this.mPaint.getAlpha()) {
            this.mPaint.setAlpha(i);
            invalidateSelf();
        }
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    public int getIntrinsicHeight() {
        return this.mSize;
    }

    public int getIntrinsicWidth() {
        return this.mSize;
    }

    @FloatRange(from = 0.0d, to = 1.0d)
    public float getProgress() {
        return this.mProgress;
    }

    public void setProgress(@FloatRange(from = 0.0d, to = 1.0d) float f) {
        if (this.mProgress != f) {
            this.mProgress = f;
            invalidateSelf();
        }
    }

    public final Paint getPaint() {
        return this.mPaint;
    }
}
