package com.jaindoodhbhandaaran.example.jeremyfeinstein.slidingmenu.lib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.jaindoodhbhandaaran.example.jeremyfeinstein.slidingmenu.lib.SlidingMenu.CanvasTransformer;

public class CustomViewBehind extends ViewGroup {
    private static final int MARGIN_THRESHOLD = 48;
    private static final String TAG = "CustomViewBehind";
    private boolean mChildrenEnabled;
    private View mContent;
    private float mFadeDegree;
    private boolean mFadeEnabled;
    private final Paint mFadePaint;
    private int mMarginThreshold;
    private int mMode;
    private float mScrollScale;
    private View mSecondaryContent;
    private Drawable mSecondaryShadowDrawable;
    private View mSelectedView;
    private Bitmap mSelectorDrawable;
    private boolean mSelectorEnabled;
    private Drawable mShadowDrawable;
    private int mShadowWidth;
    private int mTouchMode;
    private CanvasTransformer mTransformer;
    private CustomViewAbove mViewAbove;
    private int mWidthOffset;

    public CustomViewBehind(Context context) {
        this(context, null);
    }

    public CustomViewBehind(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mTouchMode = null;
        this.mFadePaint = new Paint();
        this.mSelectorEnabled = true;
        this.mMarginThreshold = (int) TypedValue.applyDimension(1, 48.0f, getResources().getDisplayMetrics());
    }

    public void setCustomViewAbove(CustomViewAbove customViewAbove) {
        this.mViewAbove = customViewAbove;
    }

    public void setCanvasTransformer(CanvasTransformer canvasTransformer) {
        this.mTransformer = canvasTransformer;
    }

    public void setWidthOffset(int i) {
        this.mWidthOffset = i;
        requestLayout();
    }

    public void setMarginThreshold(int i) {
        this.mMarginThreshold = i;
    }

    public int getMarginThreshold() {
        return this.mMarginThreshold;
    }

    public int getBehindWidth() {
        return this.mContent.getWidth();
    }

    public void setContent(View view) {
        if (this.mContent != null) {
            removeView(this.mContent);
        }
        this.mContent = view;
        addView(this.mContent);
    }

    public View getContent() {
        return this.mContent;
    }

    public void setSecondaryContent(View view) {
        if (this.mSecondaryContent != null) {
            removeView(this.mSecondaryContent);
        }
        this.mSecondaryContent = view;
        addView(this.mSecondaryContent);
    }

    public View getSecondaryContent() {
        return this.mSecondaryContent;
    }

    public void setChildrenEnabled(boolean z) {
        this.mChildrenEnabled = z;
    }

    public void scrollTo(int i, int i2) {
        super.scrollTo(i, i2);
        if (this.mTransformer != 0) {
            invalidate();
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return this.mChildrenEnabled ^ 1;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return this.mChildrenEnabled ^ 1;
    }

    protected void dispatchDraw(Canvas canvas) {
        if (this.mTransformer != null) {
            canvas.save();
            this.mTransformer.transformCanvas(canvas, this.mViewAbove.getPercentOpen());
            super.dispatchDraw(canvas);
            canvas.restore();
            return;
        }
        super.dispatchDraw(canvas);
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        i3 -= i;
        i4 -= i2;
        this.mContent.layout(0, 0, i3 - this.mWidthOffset, i4);
        if (this.mSecondaryContent) {
            this.mSecondaryContent.layout(0, 0, i3 - this.mWidthOffset, i4);
        }
    }

    protected void onMeasure(int i, int i2) {
        int defaultSize = getDefaultSize(0, i);
        int defaultSize2 = getDefaultSize(0, i2);
        setMeasuredDimension(defaultSize, defaultSize2);
        i = getChildMeasureSpec(i, 0, defaultSize - this.mWidthOffset);
        i2 = getChildMeasureSpec(i2, 0, defaultSize2);
        this.mContent.measure(i, i2);
        if (this.mSecondaryContent != null) {
            this.mSecondaryContent.measure(i, i2);
        }
    }

    public void setMode(int i) {
        if (i == 0 || i == 1) {
            if (this.mContent != null) {
                this.mContent.setVisibility(0);
            }
            if (this.mSecondaryContent != null) {
                this.mSecondaryContent.setVisibility(4);
            }
        }
        this.mMode = i;
    }

    public int getMode() {
        return this.mMode;
    }

    public void setScrollScale(float f) {
        this.mScrollScale = f;
    }

    public float getScrollScale() {
        return this.mScrollScale;
    }

    public void setShadowDrawable(Drawable drawable) {
        this.mShadowDrawable = drawable;
        invalidate();
    }

    public void setSecondaryShadowDrawable(Drawable drawable) {
        this.mSecondaryShadowDrawable = drawable;
        invalidate();
    }

    public void setShadowWidth(int i) {
        this.mShadowWidth = i;
        invalidate();
    }

    public void setFadeEnabled(boolean z) {
        this.mFadeEnabled = z;
    }

    public void setFadeDegree(float f) {
        if (f <= 1.0f) {
            if (f >= 0.0f) {
                this.mFadeDegree = f;
                return;
            }
        }
        throw new IllegalStateException("The BehindFadeDegree must be between 0.0f and 1.0f");
    }

    public int getMenuPage(int i) {
        if (i > 1) {
            i = 2;
        } else if (i < 1) {
            i = 0;
        }
        if (this.mMode == 0 && i > 1) {
            return 0;
        }
        if (this.mMode != 1 || i >= 1) {
            return i;
        }
        return 2;
    }

    public void scrollBehindTo(View view, int i, int i2) {
        int i3 = 0;
        if (this.mMode == 0) {
            if (i >= view.getLeft()) {
                i3 = 4;
            }
            scrollTo((int) (((float) (i + getBehindWidth())) * this.mScrollScale), i2);
        } else if (this.mMode == 1) {
            if (i <= view.getLeft()) {
                i3 = 4;
            }
            scrollTo((int) (((float) (getBehindWidth() - getWidth())) + (((float) (i - getBehindWidth())) * this.mScrollScale)), i2);
        } else if (this.mMode == 2) {
            this.mContent.setVisibility(i >= view.getLeft() ? 4 : 0);
            this.mSecondaryContent.setVisibility(i <= view.getLeft() ? 4 : 0);
            if (i == 0) {
                i3 = 4;
            }
            if (i <= view.getLeft()) {
                scrollTo((int) (((float) (i + getBehindWidth())) * this.mScrollScale), i2);
            } else {
                scrollTo((int) (((float) (getBehindWidth() - getWidth())) + (((float) (i - getBehindWidth())) * this.mScrollScale)), i2);
            }
        }
        if (i3 == 4) {
            Log.v(TAG, "behind INVISIBLE");
        }
        setVisibility(i3);
    }

    public int getMenuLeft(View view, int i) {
        if (this.mMode == 0) {
            if (i == 0) {
                return view.getLeft() - getBehindWidth();
            }
            if (i == 2) {
                return view.getLeft();
            }
        } else if (this.mMode == 1) {
            if (i == 0) {
                return view.getLeft();
            }
            if (i == 2) {
                return view.getLeft() + getBehindWidth();
            }
        } else if (this.mMode == 2) {
            if (i == 0) {
                return view.getLeft() - getBehindWidth();
            }
            if (i == 2) {
                return view.getLeft() + getBehindWidth();
            }
        }
        return view.getLeft();
    }

    public int getAbsLeftBound(View view) {
        if (this.mMode != 0) {
            if (this.mMode != 2) {
                return this.mMode == 1 ? view.getLeft() : null;
            }
        }
        return view.getLeft() - getBehindWidth();
    }

    public int getAbsRightBound(View view) {
        if (this.mMode == 0) {
            return view.getLeft();
        }
        if (this.mMode != 1) {
            if (this.mMode != 2) {
                return null;
            }
        }
        return view.getLeft() + getBehindWidth();
    }

    public boolean marginTouchAllowed(View view, int i) {
        int left = view.getLeft();
        view = view.getRight();
        boolean z = false;
        if (this.mMode == 0) {
            if (i >= left && i <= this.mMarginThreshold + left) {
                z = true;
            }
            return z;
        } else if (this.mMode == 1) {
            if (i <= view && i >= view - this.mMarginThreshold) {
                z = true;
            }
            return z;
        } else if (this.mMode != 2) {
            return false;
        } else {
            if ((i >= left && i <= this.mMarginThreshold + left) || (i <= view && i >= view - this.mMarginThreshold)) {
                z = true;
            }
            return z;
        }
    }

    public void setTouchMode(int i) {
        this.mTouchMode = i;
    }

    public boolean menuOpenTouchAllowed(View view, int i, float f) {
        switch (this.mTouchMode) {
            case 0:
                return menuTouchInQuickReturn(view, i, f);
            case 1:
                return true;
            default:
                return null;
        }
    }

    public boolean menuTouchInQuickReturn(View view, int i, float f) {
        boolean z = false;
        if (this.mMode != 0) {
            if (this.mMode != 2 || i != 0) {
                if (this.mMode != 1) {
                    if (this.mMode != 2 || i != 2) {
                        return false;
                    }
                }
                if (f <= ((float) view.getRight())) {
                    z = true;
                }
                return z;
            }
        }
        if (f >= ((float) view.getLeft())) {
            z = true;
        }
        return z;
    }

    public boolean menuClosedSlideAllowed(float f) {
        boolean z = false;
        if (this.mMode == 0) {
            if (f > 0.0f) {
                z = true;
            }
            return z;
        } else if (this.mMode != 1) {
            return this.mMode == 2;
        } else {
            if (f < 0.0f) {
                z = true;
            }
            return z;
        }
    }

    public boolean menuOpenSlideAllowed(float f) {
        boolean z = false;
        if (this.mMode == 0) {
            if (f < 0.0f) {
                z = true;
            }
            return z;
        } else if (this.mMode != 1) {
            return this.mMode == 2;
        } else {
            if (f > 0.0f) {
                z = true;
            }
            return z;
        }
    }

    public void drawShadow(View view, Canvas canvas) {
        if (this.mShadowDrawable != null) {
            if (this.mShadowWidth > 0) {
                if (this.mMode == 0) {
                    view = view.getLeft() - this.mShadowWidth;
                } else if (this.mMode == 1) {
                    view = view.getRight();
                } else if (this.mMode == 2) {
                    if (this.mSecondaryShadowDrawable != null) {
                        int right = view.getRight();
                        this.mSecondaryShadowDrawable.setBounds(right, 0, this.mShadowWidth + right, getHeight());
                        this.mSecondaryShadowDrawable.draw(canvas);
                    }
                    view = view.getLeft() - this.mShadowWidth;
                } else {
                    view = null;
                }
                this.mShadowDrawable.setBounds(view, 0, this.mShadowWidth + view, getHeight());
                this.mShadowDrawable.draw(canvas);
            }
        }
    }

    public void drawFade(View view, Canvas canvas, float f) {
        if (this.mFadeEnabled) {
            int i = 0;
            this.mFadePaint.setColor(Color.argb((int) ((this.mFadeDegree * 255.0f) * Math.abs(1.0f - f)), 0, 0, 0));
            if (this.mMode == null) {
                i = view.getLeft() - getBehindWidth();
                view = view.getLeft();
            } else if (this.mMode == 1) {
                i = view.getRight();
                view = view.getRight() + getBehindWidth();
            } else if (this.mMode == 2) {
                Canvas canvas2 = canvas;
                canvas2.drawRect((float) (view.getLeft() - getBehindWidth()), 0.0f, (float) view.getLeft(), (float) getHeight(), this.mFadePaint);
                i = view.getRight();
                view = view.getRight() + getBehindWidth();
            } else {
                view = null;
            }
            canvas.drawRect((float) i, 0.0f, (float) view, (float) getHeight(), this.mFadePaint);
        }
    }

    public void drawSelector(View view, Canvas canvas, float f) {
        if (this.mSelectorEnabled && this.mSelectorDrawable != null && this.mSelectedView != null && "".equals("CustomViewBehindSelectedView")) {
            canvas.save();
            f = (int) (((float) this.mSelectorDrawable.getWidth()) * f);
            if (this.mMode == 0) {
                view = view.getLeft();
                f = view - f;
                canvas.clipRect(f, 0, view, getHeight());
                canvas.drawBitmap(this.mSelectorDrawable, (float) f, (float) getSelectorTop(), null);
            } else if (this.mMode == 1) {
                view = view.getRight();
                f += view;
                canvas.clipRect(view, 0, f, getHeight());
                canvas.drawBitmap(this.mSelectorDrawable, (float) (f - this.mSelectorDrawable.getWidth()), (float) getSelectorTop(), null);
            }
            canvas.restore();
        }
    }

    public void setSelectorEnabled(boolean z) {
        this.mSelectorEnabled = z;
    }

    public void setSelectedView(View view) {
        if (this.mSelectedView != null) {
            this.mSelectedView = null;
        }
        if (view != null && view.getParent() != null) {
            this.mSelectedView = view;
            invalidate();
        }
    }

    private int getSelectorTop() {
        return this.mSelectedView.getTop() + ((this.mSelectedView.getHeight() - this.mSelectorDrawable.getHeight()) / 2);
    }

    public void setSelectorBitmap(Bitmap bitmap) {
        this.mSelectorDrawable = bitmap;
        refreshDrawableState();
    }
}
