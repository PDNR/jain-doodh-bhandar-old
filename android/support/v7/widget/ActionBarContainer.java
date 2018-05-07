package android.support.v7.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

@RestrictTo({Scope.LIBRARY_GROUP})
public class ActionBarContainer extends FrameLayout {
    private View mActionBarView;
    Drawable mBackground;
    private View mContextView;
    private int mHeight;
    boolean mIsSplit;
    boolean mIsStacked;
    private boolean mIsTransitioning;
    Drawable mSplitBackground;
    Drawable mStackedBackground;
    private View mTabContainer;

    public ActionMode startActionModeForChild(View view, Callback callback) {
        return null;
    }

    public ActionBarContainer(Context context) {
        this(context, null);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ActionBarContainer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        ViewCompat.setBackground(this, VERSION.SDK_INT >= 21 ? new ActionBarBackgroundDrawableV21(this) : new ActionBarBackgroundDrawable(this));
        context = context.obtainStyledAttributes(attributeSet, R.styleable.ActionBar);
        this.mBackground = context.getDrawable(R.styleable.ActionBar_background);
        this.mStackedBackground = context.getDrawable(R.styleable.ActionBar_backgroundStacked);
        this.mHeight = context.getDimensionPixelSize(R.styleable.ActionBar_height, -1);
        if (getId() == R.id.split_action_bar) {
            this.mIsSplit = true;
            this.mSplitBackground = context.getDrawable(R.styleable.ActionBar_backgroundSplit);
        }
        context.recycle();
        attributeSet = null;
        if (this.mIsSplit == null) {
            if (this.mBackground == null && this.mStackedBackground == null) {
            }
            setWillNotDraw(attributeSet);
        }
        attributeSet = 1;
        setWillNotDraw(attributeSet);
    }

    public void onFinishInflate() {
        super.onFinishInflate();
        this.mActionBarView = findViewById(R.id.action_bar);
        this.mContextView = findViewById(R.id.action_context_bar);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setPrimaryBackground(Drawable drawable) {
        if (this.mBackground != null) {
            this.mBackground.setCallback(null);
            unscheduleDrawable(this.mBackground);
        }
        this.mBackground = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
            if (this.mActionBarView != null) {
                this.mBackground.setBounds(this.mActionBarView.getLeft(), this.mActionBarView.getTop(), this.mActionBarView.getRight(), this.mActionBarView.getBottom());
            }
        }
        boolean z = false;
        if (this.mIsSplit == null) {
            if (this.mBackground == null && this.mStackedBackground == null) {
            }
            setWillNotDraw(z);
            invalidate();
        }
        z = true;
        setWillNotDraw(z);
        invalidate();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setStackedBackground(Drawable drawable) {
        if (this.mStackedBackground != null) {
            this.mStackedBackground.setCallback(null);
            unscheduleDrawable(this.mStackedBackground);
        }
        this.mStackedBackground = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
            if (!(this.mIsStacked == null || this.mStackedBackground == null)) {
                this.mStackedBackground.setBounds(this.mTabContainer.getLeft(), this.mTabContainer.getTop(), this.mTabContainer.getRight(), this.mTabContainer.getBottom());
            }
        }
        boolean z = false;
        if (this.mIsSplit == null) {
            if (this.mBackground == null && this.mStackedBackground == null) {
            }
            setWillNotDraw(z);
            invalidate();
        }
        z = true;
        setWillNotDraw(z);
        invalidate();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setSplitBackground(Drawable drawable) {
        if (this.mSplitBackground != null) {
            this.mSplitBackground.setCallback(null);
            unscheduleDrawable(this.mSplitBackground);
        }
        this.mSplitBackground = drawable;
        boolean z = false;
        if (drawable != null) {
            drawable.setCallback(this);
            if (!(this.mIsSplit == null || this.mSplitBackground == null)) {
                this.mSplitBackground.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            }
        }
        if (this.mIsSplit == null) {
            if (this.mBackground == null && this.mStackedBackground == null) {
            }
            setWillNotDraw(z);
            invalidate();
        }
        z = true;
        setWillNotDraw(z);
        invalidate();
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        i = i == 0 ? 1 : 0;
        if (this.mBackground != null) {
            this.mBackground.setVisible(i, false);
        }
        if (this.mStackedBackground != null) {
            this.mStackedBackground.setVisible(i, false);
        }
        if (this.mSplitBackground != null) {
            this.mSplitBackground.setVisible(i, false);
        }
    }

    protected boolean verifyDrawable(Drawable drawable) {
        return ((drawable != this.mBackground || this.mIsSplit) && (!(drawable == this.mStackedBackground && this.mIsStacked) && (!(drawable == this.mSplitBackground && this.mIsSplit) && super.verifyDrawable(drawable) == null))) ? null : true;
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.mBackground != null && this.mBackground.isStateful()) {
            this.mBackground.setState(getDrawableState());
        }
        if (this.mStackedBackground != null && this.mStackedBackground.isStateful()) {
            this.mStackedBackground.setState(getDrawableState());
        }
        if (this.mSplitBackground != null && this.mSplitBackground.isStateful()) {
            this.mSplitBackground.setState(getDrawableState());
        }
    }

    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (this.mBackground != null) {
            this.mBackground.jumpToCurrentState();
        }
        if (this.mStackedBackground != null) {
            this.mStackedBackground.jumpToCurrentState();
        }
        if (this.mSplitBackground != null) {
            this.mSplitBackground.jumpToCurrentState();
        }
    }

    public void setTransitioning(boolean z) {
        this.mIsTransitioning = z;
        setDescendantFocusability(z ? true : true);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (!this.mIsTransitioning) {
            if (super.onInterceptTouchEvent(motionEvent) == null) {
                return null;
            }
        }
        return true;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        super.onTouchEvent(motionEvent);
        return true;
    }

    public boolean onHoverEvent(MotionEvent motionEvent) {
        super.onHoverEvent(motionEvent);
        return true;
    }

    public void setTabContainer(ScrollingTabContainerView scrollingTabContainerView) {
        if (this.mTabContainer != null) {
            removeView(this.mTabContainer);
        }
        this.mTabContainer = scrollingTabContainerView;
        if (scrollingTabContainerView != null) {
            addView(scrollingTabContainerView);
            LayoutParams layoutParams = scrollingTabContainerView.getLayoutParams();
            layoutParams.width = -1;
            layoutParams.height = -2;
            scrollingTabContainerView.setAllowCollapse(false);
        }
    }

    public View getTabContainer() {
        return this.mTabContainer;
    }

    public ActionMode startActionModeForChild(View view, Callback callback, int i) {
        return i != 0 ? super.startActionModeForChild(view, callback, i) : null;
    }

    private boolean isCollapsed(View view) {
        if (!(view == null || view.getVisibility() == 8)) {
            if (view.getMeasuredHeight() != null) {
                return null;
            }
        }
        return true;
    }

    private int getMeasuredHeightWithMargins(View view) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        return (view.getMeasuredHeight() + layoutParams.topMargin) + layoutParams.bottomMargin;
    }

    public void onMeasure(int i, int i2) {
        if (this.mActionBarView == null && MeasureSpec.getMode(i2) == Integer.MIN_VALUE && this.mHeight >= 0) {
            i2 = MeasureSpec.makeMeasureSpec(Math.min(this.mHeight, MeasureSpec.getSize(i2)), Integer.MIN_VALUE);
        }
        super.onMeasure(i, i2);
        if (this.mActionBarView != 0) {
            i = MeasureSpec.getMode(i2);
            if (!(this.mTabContainer == null || this.mTabContainer.getVisibility() == 8 || i == 1073741824)) {
                int measuredHeightWithMargins = !isCollapsed(this.mActionBarView) ? getMeasuredHeightWithMargins(this.mActionBarView) : !isCollapsed(this.mContextView) ? getMeasuredHeightWithMargins(this.mContextView) : 0;
                setMeasuredDimension(getMeasuredWidth(), Math.min(measuredHeightWithMargins + getMeasuredHeightWithMargins(this.mTabContainer), i == Integer.MIN_VALUE ? MeasureSpec.getSize(i2) : ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED));
            }
        }
    }

    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        z = this.mTabContainer;
        i4 = 1;
        int i5 = 0;
        boolean z2 = z && z.getVisibility() != 8;
        if (z && z.getVisibility() != 8) {
            i2 = getMeasuredHeight();
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) z.getLayoutParams();
            z.layout(i, (i2 - z.getMeasuredHeight()) - layoutParams.bottomMargin, i3, i2 - layoutParams.bottomMargin);
        }
        if (this.mIsSplit == 0) {
            if (this.mBackground != 0) {
                if (this.mActionBarView.getVisibility() == 0) {
                    this.mBackground.setBounds(this.mActionBarView.getLeft(), this.mActionBarView.getTop(), this.mActionBarView.getRight(), this.mActionBarView.getBottom());
                } else if (this.mContextView == 0 || this.mContextView.getVisibility() != 0) {
                    this.mBackground.setBounds(0, 0, 0, 0);
                } else {
                    this.mBackground.setBounds(this.mContextView.getLeft(), this.mContextView.getTop(), this.mContextView.getRight(), this.mContextView.getBottom());
                }
                i5 = 1;
            }
            this.mIsStacked = z2;
            if (z2 && this.mStackedBackground != 0) {
                this.mStackedBackground.setBounds(z.getLeft(), z.getTop(), z.getRight(), z.getBottom());
                if (i4 == 0) {
                    invalidate();
                }
            }
        } else if (this.mSplitBackground) {
            this.mSplitBackground.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            if (i4 == 0) {
                invalidate();
            }
        }
        i4 = i5;
        if (i4 == 0) {
            invalidate();
        }
    }
}
