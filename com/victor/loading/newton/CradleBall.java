package com.victor.loading.newton;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;
import com.victor.loading.R;

public class CradleBall extends View {
    private int height;
    private int loadingColor = -1;
    private Paint paint;
    private int width;

    public CradleBall(Context context) {
        super(context);
        initView(null);
    }

    public CradleBall(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView(attributeSet);
    }

    public CradleBall(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initView(attributeSet);
    }

    private void initView(AttributeSet attributeSet) {
        if (attributeSet != null) {
            attributeSet = getContext().obtainStyledAttributes(attributeSet, R.styleable.CradleBall);
            this.loadingColor = attributeSet.getColor(R.styleable.CradleBall_cradle_ball_color, -1);
            attributeSet.recycle();
        }
        this.paint = new Paint();
        this.paint.setColor(this.loadingColor);
        this.paint.setStyle(Style.FILL);
        this.paint.setAntiAlias(true);
    }

    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.width = i;
        this.height = i2;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle((float) (this.width / 2), (float) (this.height / 2), (float) (this.width / 2), this.paint);
    }

    public void setLoadingColor(int i) {
        this.loadingColor = i;
        this.paint.setColor(i);
        postInvalidate();
    }

    public int getLoadingColor() {
        return this.loadingColor;
    }
}
