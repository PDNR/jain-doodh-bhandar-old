package com.jaindoodhbhandaaran.example.jeremyfeinstein.slidingmenu.lib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.support.v4.view.KeyEventCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Scroller;
import com.jaindoodhbhandaaran.example.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.jaindoodhbhandaaran.example.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenedListener;
import java.util.ArrayList;
import java.util.List;

public class CustomViewAbove extends ViewGroup {
    private static final boolean DEBUG = false;
    private static final int INVALID_POINTER = -1;
    private static final int MAX_SETTLE_DURATION = 600;
    private static final int MIN_DISTANCE_FOR_FLING = 25;
    private static final String TAG = "CustomViewAbove";
    private static final boolean USE_CACHE = false;
    private static final Interpolator sInterpolator = new Interpolator() {
        public float getInterpolation(float f) {
            f -= 1.0f;
            return ((((f * f) * f) * f) * f) + 1.0f;
        }
    };
    protected int mActivePointerId;
    private OnClosedListener mClosedListener;
    private View mContent;
    private int mCurItem;
    private boolean mEnabled;
    private int mFlingDistance;
    private List<View> mIgnoredViews;
    private float mInitialMotionX;
    private OnPageChangeListener mInternalPageChangeListener;
    private boolean mIsBeingDragged;
    private boolean mIsUnableToDrag;
    private float mLastMotionX;
    private float mLastMotionY;
    protected int mMaximumVelocity;
    private int mMinimumVelocity;
    private OnPageChangeListener mOnPageChangeListener;
    private OnOpenedListener mOpenedListener;
    private boolean mQuickReturn;
    private float mScrollX;
    private Scroller mScroller;
    private boolean mScrolling;
    private boolean mScrollingCacheEnabled;
    protected int mTouchMode;
    private int mTouchSlop;
    protected VelocityTracker mVelocityTracker;
    private CustomViewBehind mViewBehind;

    public interface OnPageChangeListener {
        void onPageScrolled(int i, float f, int i2);

        void onPageSelected(int i);
    }

    public static class SimpleOnPageChangeListener implements OnPageChangeListener {
        public void onPageScrollStateChanged(int i) {
        }

        public void onPageScrolled(int i, float f, int i2) {
        }

        public void onPageSelected(int i) {
        }
    }

    public CustomViewAbove(Context context) {
        this(context, null);
    }

    public CustomViewAbove(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mActivePointerId = -1;
        this.mEnabled = true;
        this.mIgnoredViews = new ArrayList();
        this.mTouchMode = 0;
        this.mQuickReturn = false;
        this.mScrollX = null;
        initCustomViewAbove();
    }

    void initCustomViewAbove() {
        setWillNotDraw(false);
        setDescendantFocusability(262144);
        setFocusable(true);
        Context context = getContext();
        this.mScroller = new Scroller(context, sInterpolator);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        this.mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(viewConfiguration);
        this.mMinimumVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        this.mMaximumVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        setInternalPageChangeListener(new SimpleOnPageChangeListener() {
            public void onPageSelected(int i) {
                if (CustomViewAbove.this.mViewBehind != null) {
                    switch (i) {
                        case 0:
                        case 2:
                            CustomViewAbove.this.mViewBehind.setChildrenEnabled(true);
                            return;
                        case 1:
                            CustomViewAbove.this.mViewBehind.setChildrenEnabled(false);
                            return;
                        default:
                            return;
                    }
                }
            }
        });
        this.mFlingDistance = (int) (25.0f * context.getResources().getDisplayMetrics().density);
    }

    public void setCurrentItem(int i) {
        setCurrentItemInternal(i, true, false);
    }

    public void setCurrentItem(int i, boolean z) {
        setCurrentItemInternal(i, z, false);
    }

    public int getCurrentItem() {
        return this.mCurItem;
    }

    void setCurrentItemInternal(int i, boolean z, boolean z2) {
        setCurrentItemInternal(i, z, z2, 0);
    }

    void setCurrentItemInternal(int i, boolean z, boolean z2, int i2) {
        if (z2 || this.mCurItem != i) {
            boolean menuPage = this.mViewBehind.getMenuPage(i);
            z2 = this.mCurItem != menuPage;
            this.mCurItem = menuPage;
            int destScrollX = getDestScrollX(this.mCurItem);
            if (z2 && this.mOnPageChangeListener != null) {
                this.mOnPageChangeListener.onPageSelected(menuPage);
            }
            if (z2 && this.mInternalPageChangeListener) {
                this.mInternalPageChangeListener.onPageSelected(menuPage);
            }
            if (z) {
                smoothScrollTo(destScrollX, 0, i2);
            } else {
                completeScroll();
                scrollTo(destScrollX, 0);
            }
            return;
        }
        setScrollingCacheEnabled(false);
    }

    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.mOnPageChangeListener = onPageChangeListener;
    }

    public void setOnOpenedListener(OnOpenedListener onOpenedListener) {
        this.mOpenedListener = onOpenedListener;
    }

    public void setOnClosedListener(OnClosedListener onClosedListener) {
        this.mClosedListener = onClosedListener;
    }

    OnPageChangeListener setInternalPageChangeListener(OnPageChangeListener onPageChangeListener) {
        OnPageChangeListener onPageChangeListener2 = this.mInternalPageChangeListener;
        this.mInternalPageChangeListener = onPageChangeListener;
        return onPageChangeListener2;
    }

    public void addIgnoredView(View view) {
        if (!this.mIgnoredViews.contains(view)) {
            this.mIgnoredViews.add(view);
        }
    }

    public void removeIgnoredView(View view) {
        this.mIgnoredViews.remove(view);
    }

    public void clearIgnoredViews() {
        this.mIgnoredViews.clear();
    }

    float distanceInfluenceForSnapDuration(float f) {
        return (float) Math.sin((double) ((float) (((double) (f - 0.5f)) * 0.4712389167638204d)));
    }

    public int getDestScrollX(int i) {
        switch (i) {
            case 0:
            case 2:
                return this.mViewBehind.getMenuLeft(this.mContent, i);
            case 1:
                return this.mContent.getLeft();
            default:
                return 0;
        }
    }

    private int getLeftBound() {
        return this.mViewBehind.getAbsLeftBound(this.mContent);
    }

    private int getRightBound() {
        return this.mViewBehind.getAbsRightBound(this.mContent);
    }

    public int getContentLeft() {
        return this.mContent.getLeft() + this.mContent.getPaddingLeft();
    }

    public boolean isMenuOpen() {
        if (this.mCurItem != 0) {
            if (this.mCurItem != 2) {
                return false;
            }
        }
        return true;
    }

    private boolean isInIgnoredView(MotionEvent motionEvent) {
        Rect rect = new Rect();
        for (View hitRect : this.mIgnoredViews) {
            hitRect.getHitRect(rect);
            if (rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                return true;
            }
        }
        return null;
    }

    public int getBehindWidth() {
        if (this.mViewBehind == null) {
            return 0;
        }
        return this.mViewBehind.getBehindWidth();
    }

    public int getChildWidth(int i) {
        switch (i) {
            case 0:
                return getBehindWidth();
            case 1:
                return this.mContent.getWidth();
            default:
                return 0;
        }
    }

    public boolean isSlidingEnabled() {
        return this.mEnabled;
    }

    public void setSlidingEnabled(boolean z) {
        this.mEnabled = z;
    }

    void smoothScrollTo(int i, int i2) {
        smoothScrollTo(i, i2, 0);
    }

    void smoothScrollTo(int i, int i2, int i3) {
        if (getChildCount() == 0) {
            setScrollingCacheEnabled(0);
            return;
        }
        int scrollX = getScrollX();
        int scrollY = getScrollY();
        int i4 = i - scrollX;
        int i5 = i2 - scrollY;
        if (i4 == 0 && i5 == 0) {
            completeScroll();
            if (isMenuOpen() != 0) {
                if (this.mOpenedListener != 0) {
                    this.mOpenedListener.onOpened();
                }
            } else if (this.mClosedListener != 0) {
                this.mClosedListener.onClosed();
            }
            return;
        }
        setScrollingCacheEnabled(true);
        this.mScrolling = true;
        i = getBehindWidth();
        i2 = (float) (i / 2);
        i2 += distanceInfluenceForSnapDuration(Math.min(1.0f, (((float) Math.abs(i4)) * 1.0f) / ((float) i))) * i2;
        i = Math.abs(i3);
        if (i > 0) {
            i = Math.round(1000.0f * Math.abs(i2 / ((float) i))) * 4;
        } else {
            Math.abs(i4);
            i = MAX_SETTLE_DURATION;
        }
        this.mScroller.startScroll(scrollX, scrollY, i4, i5, Math.min(i, MAX_SETTLE_DURATION));
        invalidate();
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

    public void setCustomViewBehind(CustomViewBehind customViewBehind) {
        this.mViewBehind = customViewBehind;
    }

    protected void onMeasure(int i, int i2) {
        int defaultSize = getDefaultSize(0, i);
        int defaultSize2 = getDefaultSize(0, i2);
        setMeasuredDimension(defaultSize, defaultSize2);
        this.mContent.measure(getChildMeasureSpec(i, 0, defaultSize), getChildMeasureSpec(i2, 0, defaultSize2));
    }

    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (i != i3) {
            completeScroll();
            scrollTo(getDestScrollX(this.mCurItem), getScrollY());
        }
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        this.mContent.layout(0, 0, i3 - i, i4 - i2);
    }

    public void setAboveOffset(int i) {
        this.mContent.setPadding(i, this.mContent.getPaddingTop(), this.mContent.getPaddingRight(), this.mContent.getPaddingBottom());
    }

    public void computeScroll() {
        if (this.mScroller.isFinished() || !this.mScroller.computeScrollOffset()) {
            completeScroll();
            return;
        }
        int scrollX = getScrollX();
        int scrollY = getScrollY();
        int currX = this.mScroller.getCurrX();
        int currY = this.mScroller.getCurrY();
        if (!(scrollX == currX && scrollY == currY)) {
            scrollTo(currX, currY);
            pageScrolled(currX);
        }
        invalidate();
    }

    private void pageScrolled(int i) {
        int width = getWidth();
        int i2 = i / width;
        i %= width;
        onPageScrolled(i2, ((float) i) / ((float) width), i);
    }

    protected void onPageScrolled(int i, float f, int i2) {
        if (this.mOnPageChangeListener != null) {
            this.mOnPageChangeListener.onPageScrolled(i, f, i2);
        }
        if (this.mInternalPageChangeListener != null) {
            this.mInternalPageChangeListener.onPageScrolled(i, f, i2);
        }
    }

    private void completeScroll() {
        if (this.mScrolling) {
            setScrollingCacheEnabled(false);
            this.mScroller.abortAnimation();
            int scrollX = getScrollX();
            int scrollY = getScrollY();
            int currX = this.mScroller.getCurrX();
            int currY = this.mScroller.getCurrY();
            if (!(scrollX == currX && scrollY == currY)) {
                scrollTo(currX, currY);
            }
            if (isMenuOpen()) {
                if (this.mOpenedListener != null) {
                    this.mOpenedListener.onOpened();
                }
            } else if (this.mClosedListener != null) {
                this.mClosedListener.onClosed();
            }
        }
        this.mScrolling = false;
    }

    public void setTouchMode(int i) {
        this.mTouchMode = i;
    }

    public int getTouchMode() {
        return this.mTouchMode;
    }

    private boolean thisTouchAllowed(MotionEvent motionEvent) {
        int x = (int) (motionEvent.getX() + this.mScrollX);
        if (isMenuOpen()) {
            return this.mViewBehind.menuOpenTouchAllowed(this.mContent, this.mCurItem, (float) x);
        }
        switch (this.mTouchMode) {
            case 0:
                return this.mViewBehind.marginTouchAllowed(this.mContent, x);
            case 1:
                return isInIgnoredView(motionEvent) ^ 1;
            case 2:
                return false;
            default:
                return false;
        }
    }

    private boolean thisSlideAllowed(float f) {
        if (isMenuOpen()) {
            return this.mViewBehind.menuOpenSlideAllowed(f);
        }
        return this.mViewBehind.menuClosedSlideAllowed(f);
    }

    private int getPointerIndex(MotionEvent motionEvent, int i) {
        motionEvent = MotionEventCompat.findPointerIndex(motionEvent, i);
        if (motionEvent == -1) {
            this.mActivePointerId = -1;
        }
        return motionEvent;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        boolean z = false;
        if (!this.mEnabled) {
            return false;
        }
        int action = motionEvent.getAction() & 255;
        if (!(action == 3 || action == 1)) {
            if (action == 0 || !this.mIsUnableToDrag) {
                if (action == 0) {
                    action = MotionEventCompat.getActionIndex(motionEvent);
                    this.mActivePointerId = MotionEventCompat.getPointerId(motionEvent, action);
                    if (this.mActivePointerId != -1) {
                        float x = MotionEventCompat.getX(motionEvent, action);
                        this.mInitialMotionX = x;
                        this.mLastMotionX = x;
                        this.mLastMotionY = MotionEventCompat.getY(motionEvent, action);
                        if (thisTouchAllowed(motionEvent)) {
                            this.mIsBeingDragged = false;
                            this.mIsUnableToDrag = false;
                            if (isMenuOpen() && this.mViewBehind.menuTouchInQuickReturn(this.mContent, this.mCurItem, motionEvent.getX() + this.mScrollX)) {
                                this.mQuickReturn = true;
                            }
                        } else {
                            this.mIsUnableToDrag = true;
                        }
                    }
                } else if (action == 2) {
                    determineDrag(motionEvent);
                } else if (action == 6) {
                    onSecondaryPointerUp(motionEvent);
                }
                if (!this.mIsBeingDragged) {
                    if (this.mVelocityTracker == null) {
                        this.mVelocityTracker = VelocityTracker.obtain();
                    }
                    this.mVelocityTracker.addMovement(motionEvent);
                }
                if (!(this.mIsBeingDragged == null && this.mQuickReturn == null)) {
                    z = true;
                }
                return z;
            }
        }
        endDrag();
        return false;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!this.mEnabled) {
            return false;
        }
        if (!this.mIsBeingDragged && !thisTouchAllowed(motionEvent)) {
            return false;
        }
        int action = motionEvent.getAction();
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
        float scrollX;
        switch (action & 255) {
            case 0:
                completeScroll();
                this.mActivePointerId = MotionEventCompat.getPointerId(motionEvent, MotionEventCompat.getActionIndex(motionEvent));
                motionEvent = motionEvent.getX();
                this.mInitialMotionX = motionEvent;
                this.mLastMotionX = motionEvent;
                break;
            case 1:
                if (!this.mIsBeingDragged) {
                    if (this.mQuickReturn && this.mViewBehind.menuTouchInQuickReturn(this.mContent, this.mCurItem, motionEvent.getX() + this.mScrollX) != null) {
                        setCurrentItem(1);
                        endDrag();
                        break;
                    }
                }
                VelocityTracker velocityTracker = this.mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, (float) this.mMaximumVelocity);
                action = (int) VelocityTrackerCompat.getXVelocity(velocityTracker, this.mActivePointerId);
                scrollX = ((float) (getScrollX() - getDestScrollX(this.mCurItem))) / ((float) getBehindWidth());
                int pointerIndex = getPointerIndex(motionEvent, this.mActivePointerId);
                if (this.mActivePointerId != -1) {
                    setCurrentItemInternal(determineTargetPage(scrollX, action, (int) (MotionEventCompat.getX(motionEvent, pointerIndex) - this.mInitialMotionX)), true, true, action);
                } else {
                    setCurrentItemInternal(this.mCurItem, true, true, action);
                }
                this.mActivePointerId = -1;
                endDrag();
                break;
            case 2:
                if (!this.mIsBeingDragged) {
                    determineDrag(motionEvent);
                    if (this.mIsUnableToDrag) {
                        return false;
                    }
                }
                if (this.mIsBeingDragged) {
                    action = getPointerIndex(motionEvent, this.mActivePointerId);
                    if (this.mActivePointerId != -1) {
                        motionEvent = MotionEventCompat.getX(motionEvent, action);
                        float f = this.mLastMotionX - motionEvent;
                        this.mLastMotionX = motionEvent;
                        motionEvent = ((float) getScrollX()) + f;
                        f = (float) getLeftBound();
                        scrollX = (float) getRightBound();
                        if (motionEvent < f) {
                            motionEvent = f;
                        } else if (motionEvent > scrollX) {
                            motionEvent = scrollX;
                        }
                        int i = (int) motionEvent;
                        this.mLastMotionX += motionEvent - ((float) i);
                        scrollTo(i, getScrollY());
                        pageScrolled(i);
                        break;
                    }
                    break;
                }
                break;
            case 3:
                if (this.mIsBeingDragged != null) {
                    setCurrentItemInternal(this.mCurItem, true, true);
                    this.mActivePointerId = -1;
                    endDrag();
                    break;
                }
                break;
            case 5:
                action = MotionEventCompat.getActionIndex(motionEvent);
                this.mLastMotionX = MotionEventCompat.getX(motionEvent, action);
                this.mActivePointerId = MotionEventCompat.getPointerId(motionEvent, action);
                break;
            case 6:
                onSecondaryPointerUp(motionEvent);
                action = getPointerIndex(motionEvent, this.mActivePointerId);
                if (this.mActivePointerId != -1) {
                    this.mLastMotionX = MotionEventCompat.getX(motionEvent, action);
                    break;
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void determineDrag(MotionEvent motionEvent) {
        int i = this.mActivePointerId;
        int pointerIndex = getPointerIndex(motionEvent, i);
        if (i != -1) {
            if (pointerIndex != -1) {
                float x = MotionEventCompat.getX(motionEvent, pointerIndex);
                float f = x - this.mLastMotionX;
                float abs = Math.abs(f);
                motionEvent = MotionEventCompat.getY(motionEvent, pointerIndex);
                float abs2 = Math.abs(motionEvent - this.mLastMotionY);
                if (abs > ((float) (isMenuOpen() ? this.mTouchSlop / 2 : this.mTouchSlop)) && abs > abs2 && thisSlideAllowed(f)) {
                    startDrag();
                    this.mLastMotionX = x;
                    this.mLastMotionY = motionEvent;
                    setScrollingCacheEnabled(true);
                } else if (abs > ((float) this.mTouchSlop)) {
                    this.mIsUnableToDrag = true;
                }
            }
        }
    }

    public void scrollTo(int i, int i2) {
        super.scrollTo(i, i2);
        this.mScrollX = (float) i;
        this.mViewBehind.scrollBehindTo(this.mContent, i, i2);
        ((SlidingMenu) getParent()).manageLayers(getPercentOpen());
    }

    private int determineTargetPage(float f, int i, int i2) {
        int i3 = this.mCurItem;
        if (Math.abs(i2) <= this.mFlingDistance || Math.abs(i) <= this.mMinimumVelocity) {
            return Math.round(((float) this.mCurItem) + f);
        }
        if (i > 0 && i2 > 0) {
            return i3 - 1;
        }
        if (i >= 0 || i2 >= 0) {
            return i3;
        }
        return i3 + 1;
    }

    protected float getPercentOpen() {
        return Math.abs(this.mScrollX - ((float) this.mContent.getLeft())) / ((float) getBehindWidth());
    }

    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        this.mViewBehind.drawShadow(this.mContent, canvas);
        this.mViewBehind.drawFade(this.mContent, canvas, getPercentOpen());
        this.mViewBehind.drawSelector(this.mContent, canvas, getPercentOpen());
    }

    private void onSecondaryPointerUp(MotionEvent motionEvent) {
        int actionIndex = MotionEventCompat.getActionIndex(motionEvent);
        if (MotionEventCompat.getPointerId(motionEvent, actionIndex) == this.mActivePointerId) {
            actionIndex = actionIndex == 0 ? 1 : 0;
            this.mLastMotionX = MotionEventCompat.getX(motionEvent, actionIndex);
            this.mActivePointerId = MotionEventCompat.getPointerId(motionEvent, actionIndex);
            if (this.mVelocityTracker != null) {
                this.mVelocityTracker.clear();
            }
        }
    }

    private void startDrag() {
        this.mIsBeingDragged = true;
        this.mQuickReturn = false;
    }

    private void endDrag() {
        this.mQuickReturn = false;
        this.mIsBeingDragged = false;
        this.mIsUnableToDrag = false;
        this.mActivePointerId = -1;
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    private void setScrollingCacheEnabled(boolean z) {
        if (this.mScrollingCacheEnabled != z) {
            this.mScrollingCacheEnabled = z;
        }
    }

    protected boolean canScroll(View view, boolean z, int i, int i2, int i3) {
        View view2 = view;
        boolean z2 = true;
        if (view2 instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view2;
            int scrollX = view2.getScrollX();
            int scrollY = view2.getScrollY();
            for (int childCount = viewGroup.getChildCount() - 1; childCount >= 0; childCount--) {
                View childAt = viewGroup.getChildAt(childCount);
                int i4 = i2 + scrollX;
                if (i4 >= childAt.getLeft() && i4 < childAt.getRight()) {
                    int i5 = i3 + scrollY;
                    if (i5 >= childAt.getTop() && i5 < childAt.getBottom()) {
                        if (canScroll(childAt, true, i, i4 - childAt.getLeft(), i5 - childAt.getTop())) {
                            return true;
                        }
                    }
                }
            }
        }
        if (!z || !ViewCompat.canScrollHorizontally(view2, -i)) {
            z2 = false;
        }
        return z2;
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (!super.dispatchKeyEvent(keyEvent)) {
            if (executeKeyEvent(keyEvent) == null) {
                return null;
            }
        }
        return true;
    }

    public boolean executeKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getAction() == 0) {
            int keyCode = keyEvent.getKeyCode();
            if (keyCode != 61) {
                switch (keyCode) {
                    case 21:
                        return arrowScroll(17);
                    case 22:
                        return arrowScroll(66);
                    default:
                        break;
                }
            } else if (VERSION.SDK_INT >= 11) {
                if (KeyEventCompat.hasNoModifiers(keyEvent)) {
                    return arrowScroll(2);
                }
                if (KeyEventCompat.hasModifiers(keyEvent, 1) != null) {
                    return arrowScroll(1);
                }
            }
        }
        return null;
    }

    public boolean arrowScroll(int i) {
        View findFocus = findFocus();
        if (findFocus == this) {
            findFocus = null;
        }
        boolean z = false;
        View findNextFocus = FocusFinder.getInstance().findNextFocus(this, findFocus, i);
        if (findNextFocus == null || findNextFocus == findFocus) {
            if (i != 17) {
                if (i != 1) {
                    if (i == 66 || i == 2) {
                        z = pageRight();
                    }
                }
            }
            z = pageLeft();
        } else if (i == 17) {
            z = findNextFocus.requestFocus();
        } else if (i == 66) {
            z = (findFocus == null || findNextFocus.getLeft() > findFocus.getLeft()) ? findNextFocus.requestFocus() : pageRight();
        }
        if (z) {
            playSoundEffect(SoundEffectConstants.getContantForFocusDirection(i));
        }
        return z;
    }

    boolean pageLeft() {
        if (this.mCurItem <= 0) {
            return false;
        }
        setCurrentItem(this.mCurItem - 1, true);
        return true;
    }

    boolean pageRight() {
        if (this.mCurItem >= 1) {
            return false;
        }
        setCurrentItem(this.mCurItem + 1, true);
        return true;
    }
}
