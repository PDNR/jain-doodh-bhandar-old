package com.victor.loading.book;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;
import com.victor.loading.R;

public class BookView extends View {
    private int height;
    private Paint paint;
    private int width;

    public BookView(Context context) {
        super(context);
        initView();
    }

    public BookView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView();
    }

    public BookView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initView();
    }

    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.width = i;
        this.height = i2;
    }

    private void initView() {
        this.paint = new Paint();
        this.paint.setColor(getResources().getColor(R.color.book_loading_book));
        this.paint.setStrokeWidth(getResources().getDimension(R.dimen.book_border));
        this.paint.setAntiAlias(true);
        this.paint.setStyle(Style.STROKE);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0.0f, 0.0f, (float) this.width, (float) this.height, this.paint);
    }
}
