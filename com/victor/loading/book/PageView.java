package com.victor.loading.book;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import com.victor.loading.R;

public class PageView extends View {
    private int border;
    private int height;
    private float padding;
    private Paint paint;
    private Path path;
    private int width;

    public PageView(Context context) {
        super(context);
        initView();
    }

    public PageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView();
    }

    public PageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initView();
    }

    private void initView() {
        this.padding = getResources().getDimension(R.dimen.book_padding);
        this.border = getResources().getDimensionPixelOffset(R.dimen.book_border);
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setStrokeWidth(getResources().getDimension(R.dimen.page_border));
        this.path = new Path();
    }

    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.width = i;
        this.height = i2;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.paint.setColor(getResources().getColor(R.color.book_loading_book));
        this.paint.setStyle(Style.STROKE);
        float f = (float) (this.border / 4);
        this.path.moveTo((float) (this.width / 2), this.padding + f);
        this.path.lineTo((((float) this.width) - this.padding) - f, this.padding + f);
        this.path.lineTo((((float) this.width) - this.padding) - f, (((float) this.height) - this.padding) - f);
        this.path.lineTo((float) (this.width / 2), (((float) this.height) - this.padding) - f);
        canvas.drawPath(this.path, this.paint);
        this.paint.setColor(getResources().getColor(R.color.book_loading_page));
        this.paint.setStyle(Style.FILL);
        f = (float) (this.border / 2);
        canvas.drawRect((float) (this.width / 2), this.padding + f, (((float) this.width) - this.padding) - f, (((float) this.height) - this.padding) - f, this.paint);
    }
}
