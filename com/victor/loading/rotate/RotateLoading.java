package com.victor.loading.rotate;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;
import com.victor.loading.R;

public class RotateLoading extends View {
    private static final int DEFAULT_SHADOW_POSITION = 2;
    private static final int DEFAULT_SPEED_OF_DEGREE = 10;
    private static final int DEFAULT_WIDTH = 6;
    private float arc;
    private int bottomDegree = 190;
    private boolean changeBigger = true;
    private int color;
    private boolean isStart = false;
    private RectF loadingRectF;
    private Paint mPaint;
    private int shadowPosition;
    private RectF shadowRectF;
    private float speedOfArc;
    private int speedOfDegree;
    private int topDegree = 10;
    private int width;

    public RotateLoading(Context context) {
        super(context);
        initView(context, null);
    }

    public RotateLoading(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView(context, attributeSet);
    }

    public RotateLoading(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initView(context, attributeSet);
    }

    private void initView(Context context, AttributeSet attributeSet) {
        this.color = -1;
        this.width = dpToPx(context, 6.0f);
        this.shadowPosition = dpToPx(getContext(), 2.0f);
        this.speedOfDegree = 10;
        if (attributeSet != null) {
            attributeSet = context.obtainStyledAttributes(attributeSet, R.styleable.RotateLoading);
            this.color = attributeSet.getColor(R.styleable.RotateLoading_loading_color, -1);
            this.width = attributeSet.getDimensionPixelSize(R.styleable.RotateLoading_loading_width, dpToPx(context, 6.0f));
            this.shadowPosition = attributeSet.getInt(R.styleable.RotateLoading_shadow_position, 2);
            this.speedOfDegree = attributeSet.getInt(R.styleable.RotateLoading_loading_speed, 10);
            attributeSet.recycle();
        }
        this.speedOfArc = (float) (this.speedOfDegree / 4);
        this.mPaint = new Paint();
        this.mPaint.setColor(this.color);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStyle(Style.STROKE);
        this.mPaint.setStrokeWidth((float) this.width);
        this.mPaint.setStrokeCap(Cap.ROUND);
    }

    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.arc = 1092616192;
        this.loadingRectF = new RectF((float) (this.width * 2), (float) (this.width * 2), (float) (i - (this.width * 2)), (float) (i2 - (this.width * 2)));
        this.shadowRectF = new RectF((float) ((this.width * 2) + this.shadowPosition), (float) ((this.width * 2) + this.shadowPosition), (float) ((i - (this.width * 2)) + this.shadowPosition), (float) ((i2 - (2 * this.width)) + this.shadowPosition));
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.isStart) {
            this.mPaint.setColor(Color.parseColor("#1a000000"));
            Canvas canvas2 = canvas;
            canvas2.drawArc(this.shadowRectF, (float) this.topDegree, this.arc, false, this.mPaint);
            Canvas canvas3 = canvas;
            canvas3.drawArc(this.shadowRectF, (float) this.bottomDegree, this.arc, false, this.mPaint);
            this.mPaint.setColor(this.color);
            canvas2.drawArc(this.loadingRectF, (float) this.topDegree, this.arc, false, this.mPaint);
            canvas3.drawArc(this.loadingRectF, (float) this.bottomDegree, this.arc, false, this.mPaint);
            this.topDegree += this.speedOfDegree;
            this.bottomDegree += this.speedOfDegree;
            if (this.topDegree > 360) {
                this.topDegree -= 360;
            }
            if (this.bottomDegree > 360) {
                this.bottomDegree -= 360;
            }
            if (this.changeBigger != null) {
                if (this.arc < 1126170624) {
                    this.arc += this.speedOfArc;
                    invalidate();
                }
            } else if (this.arc > ((float) this.speedOfDegree)) {
                this.arc -= 2.0f * this.speedOfArc;
                invalidate();
            }
            if (this.arc >= 1126170624 || this.arc <= 10.0f) {
                this.changeBigger ^= 1;
                invalidate();
            }
        }
    }

    public void setLoadingColor(int i) {
        this.color = i;
    }

    public int getLoadingColor() {
        return this.color;
    }

    public void start() {
        startAnimator();
        this.isStart = true;
        invalidate();
    }

    public void stop() {
        stopAnimator();
        invalidate();
    }

    public boolean isStart() {
        return this.isStart;
    }

    private void startAnimator() {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "scaleX", new float[]{0.0f, 1.0f});
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this, "scaleY", new float[]{0.0f, 1.0f});
        ofFloat.setDuration(300);
        ofFloat.setInterpolator(new LinearInterpolator());
        ofFloat2.setDuration(300);
        ofFloat2.setInterpolator(new LinearInterpolator());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ofFloat, ofFloat2});
        animatorSet.start();
    }

    private void stopAnimator() {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "scaleX", new float[]{1.0f, 0.0f});
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this, "scaleY", new float[]{1.0f, 0.0f});
        ofFloat.setDuration(300);
        ofFloat.setInterpolator(new LinearInterpolator());
        ofFloat2.setDuration(300);
        ofFloat2.setInterpolator(new LinearInterpolator());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ofFloat, ofFloat2});
        animatorSet.addListener(new AnimatorListener() {
            public void onAnimationCancel(Animator animator) {
            }

            public void onAnimationRepeat(Animator animator) {
            }

            public void onAnimationStart(Animator animator) {
            }

            public void onAnimationEnd(Animator animator) {
                RotateLoading.this.isStart = false;
            }
        });
        animatorSet.start();
    }

    public int dpToPx(Context context, float f) {
        return (int) TypedValue.applyDimension(1, f, context.getResources().getDisplayMetrics());
    }
}
