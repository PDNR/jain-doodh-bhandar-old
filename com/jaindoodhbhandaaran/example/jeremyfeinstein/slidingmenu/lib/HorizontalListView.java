package com.jaindoodhbhandaaran.example.jeremyfeinstein.slidingmenu.lib;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Scroller;
import com.jaindoodhbhandaaran.R;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class HorizontalListView extends AdapterView<ListAdapter> {
    private static final String BUNDLE_ID_CURRENT_X = "BUNDLE_ID_CURRENT_X";
    private static final String BUNDLE_ID_PARENT_STATE = "BUNDLE_ID_PARENT_STATE";
    private static final float FLING_DEFAULT_ABSORB_VELOCITY = 30.0f;
    private static final float FLING_FRICTION = 0.009f;
    private static final int INSERT_AT_END_OF_LIST = -1;
    private static final int INSERT_AT_START_OF_LIST = 0;
    protected ListAdapter mAdapter;
    private DataSetObserver mAdapterDataObserver = new DataSetObserver() {
        public void onChanged() {
            HorizontalListView.this.mDataChanged = true;
            HorizontalListView.this.mHasNotifiedRunningLowOnData = false;
            HorizontalListView.this.unpressTouchedChild();
            HorizontalListView.this.invalidate();
            HorizontalListView.this.requestLayout();
        }

        public void onInvalidated() {
            HorizontalListView.this.mHasNotifiedRunningLowOnData = false;
            HorizontalListView.this.unpressTouchedChild();
            HorizontalListView.this.reset();
            HorizontalListView.this.invalidate();
            HorizontalListView.this.requestLayout();
        }
    };
    private boolean mBlockTouchAction = false;
    private ScrollState mCurrentScrollState = ScrollState.SCROLL_STATE_IDLE;
    protected int mCurrentX;
    private int mCurrentlySelectedAdapterIndex;
    private boolean mDataChanged = false;
    private Runnable mDelayedLayout = new Runnable() {
        public void run() {
            HorizontalListView.this.requestLayout();
        }
    };
    private int mDisplayOffset;
    private Drawable mDivider = null;
    private int mDividerWidth = 0;
    private EdgeEffectCompat mEdgeGlowLeft;
    private EdgeEffectCompat mEdgeGlowRight;
    protected Scroller mFlingTracker = new Scroller(getContext());
    private GestureDetector mGestureDetector;
    private final GestureListener mGestureListener = new GestureListener();
    private boolean mHasNotifiedRunningLowOnData = false;
    private int mHeightMeasureSpec;
    private boolean mIsParentVerticiallyScrollableViewDisallowingInterceptTouchEvent = false;
    private int mLeftViewAdapterIndex;
    private int mMaxX = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
    protected int mNextX;
    private OnClickListener mOnClickListener;
    private OnScrollStateChangedListener mOnScrollStateChangedListener = null;
    private Rect mRect = new Rect();
    private List<Queue<View>> mRemovedViewsCache = new ArrayList();
    private Integer mRestoreX = null;
    private int mRightViewAdapterIndex;
    private RunningOutOfDataListener mRunningOutOfDataListener = null;
    private int mRunningOutOfDataThreshold = 0;
    private View mViewBeingTouched = null;

    private class GestureListener extends SimpleOnGestureListener {
        private GestureListener() {
        }

        public boolean onDown(MotionEvent motionEvent) {
            return HorizontalListView.this.onDown(motionEvent);
        }

        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            return HorizontalListView.this.onFling(motionEvent, motionEvent2, f, f2);
        }

        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            HorizontalListView.this.requestParentListViewToNotInterceptTouchEvents(Boolean.valueOf(true));
            HorizontalListView.this.setCurrentScrollState(ScrollState.SCROLL_STATE_TOUCH_SCROLL);
            HorizontalListView.this.unpressTouchedChild();
            motionEvent = HorizontalListView.this;
            motionEvent.mNextX += (int) f;
            HorizontalListView.this.updateOverscrollAnimation(Math.round(f));
            HorizontalListView.this.requestLayout();
            return true;
        }

        public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
            HorizontalListView.this.unpressTouchedChild();
            OnItemClickListener onItemClickListener = HorizontalListView.this.getOnItemClickListener();
            motionEvent = HorizontalListView.this.getChildIndex((int) motionEvent.getX(), (int) motionEvent.getY());
            if (motionEvent >= null && !HorizontalListView.this.mBlockTouchAction) {
                View childAt = HorizontalListView.this.getChildAt(motionEvent);
                int access$1100 = HorizontalListView.this.mLeftViewAdapterIndex + motionEvent;
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(HorizontalListView.this, childAt, access$1100, HorizontalListView.this.mAdapter.getItemId(access$1100));
                    return true;
                }
            }
            if (HorizontalListView.this.mOnClickListener != null && HorizontalListView.this.mBlockTouchAction == null) {
                HorizontalListView.this.mOnClickListener.onClick(HorizontalListView.this);
            }
            return null;
        }

        public void onLongPress(MotionEvent motionEvent) {
            HorizontalListView.this.unpressTouchedChild();
            motionEvent = HorizontalListView.this.getChildIndex((int) motionEvent.getX(), (int) motionEvent.getY());
            if (motionEvent >= null && !HorizontalListView.this.mBlockTouchAction) {
                View childAt = HorizontalListView.this.getChildAt(motionEvent);
                OnItemLongClickListener onItemLongClickListener = HorizontalListView.this.getOnItemLongClickListener();
                if (onItemLongClickListener != null) {
                    int access$1100 = HorizontalListView.this.mLeftViewAdapterIndex + motionEvent;
                    if (onItemLongClickListener.onItemLongClick(HorizontalListView.this, childAt, access$1100, HorizontalListView.this.mAdapter.getItemId(access$1100)) != null) {
                        HorizontalListView.this.performHapticFeedback(0);
                    }
                }
            }
        }
    }

    @TargetApi(11)
    private static final class HoneycombPlus {
        private HoneycombPlus() {
        }

        static {
            if (VERSION.SDK_INT < 11) {
                throw new RuntimeException("Should not get to HoneycombPlus class unless sdk is >= 11!");
            }
        }

        public static void setFriction(Scroller scroller, float f) {
            if (scroller != null) {
                scroller.setFriction(f);
            }
        }
    }

    @TargetApi(14)
    private static final class IceCreamSandwichPlus {
        private IceCreamSandwichPlus() {
        }

        static {
            if (VERSION.SDK_INT < 14) {
                throw new RuntimeException("Should not get to IceCreamSandwichPlus class unless sdk is >= 14!");
            }
        }

        public static float getCurrVelocity(Scroller scroller) {
            return scroller.getCurrVelocity();
        }
    }

    public interface OnScrollStateChangedListener {

        public enum ScrollState {
            SCROLL_STATE_IDLE,
            SCROLL_STATE_TOUCH_SCROLL,
            SCROLL_STATE_FLING
        }

        void onScrollStateChanged(ScrollState scrollState);
    }

    public interface RunningOutOfDataListener {
        void onRunningOutOfData();
    }

    protected void dispatchSetPressed(boolean z) {
    }

    public HorizontalListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mEdgeGlowLeft = new EdgeEffectCompat(context);
        this.mEdgeGlowRight = new EdgeEffectCompat(context);
        this.mGestureDetector = new GestureDetector(context, this.mGestureListener);
        bindGestureDetector();
        initView();
        retrieveXmlConfiguration(context, attributeSet);
        setWillNotDraw(false);
        if (VERSION.SDK_INT >= 11) {
            HoneycombPlus.setFriction(this.mFlingTracker, FLING_FRICTION);
        }
    }

    private void bindGestureDetector() {
        setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return HorizontalListView.this.mGestureDetector.onTouchEvent(motionEvent);
            }
        });
    }

    private void requestParentListViewToNotInterceptTouchEvents(Boolean bool) {
        if (this.mIsParentVerticiallyScrollableViewDisallowingInterceptTouchEvent != bool.booleanValue()) {
            View view = this;
            while (view.getParent() instanceof View) {
                if (!(view.getParent() instanceof ListView)) {
                    if (!(view.getParent() instanceof ScrollView)) {
                        view = (View) view.getParent();
                    }
                }
                view.getParent().requestDisallowInterceptTouchEvent(bool.booleanValue());
                this.mIsParentVerticiallyScrollableViewDisallowingInterceptTouchEvent = bool.booleanValue();
                return;
            }
        }
    }

    private void retrieveXmlConfiguration(Context context, AttributeSet attributeSet) {
        if (attributeSet != null) {
            context = context.obtainStyledAttributes(attributeSet, R.styleable.SlidingMenu);
            attributeSet = context.getDrawable(1);
            if (attributeSet != null) {
                setDivider(attributeSet);
            }
            setDividerWidth(17);
            context.recycle();
        }
    }

    public Parcelable onSaveInstanceState() {
        Parcelable bundle = new Bundle();
        bundle.putParcelable(BUNDLE_ID_PARENT_STATE, super.onSaveInstanceState());
        bundle.putInt(BUNDLE_ID_CURRENT_X, this.mCurrentX);
        return bundle;
    }

    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof Bundle) {
            Bundle bundle = (Bundle) parcelable;
            this.mRestoreX = Integer.valueOf(bundle.getInt(BUNDLE_ID_CURRENT_X));
            super.onRestoreInstanceState(bundle.getParcelable(BUNDLE_ID_PARENT_STATE));
        }
    }

    public void setDivider(Drawable drawable) {
        this.mDivider = drawable;
        if (drawable != null) {
            setDividerWidth(drawable.getIntrinsicWidth());
        } else {
            setDividerWidth(null);
        }
    }

    public void setDividerWidth(int i) {
        this.mDividerWidth = i;
        requestLayout();
        invalidate();
    }

    private void initView() {
        this.mLeftViewAdapterIndex = -1;
        this.mRightViewAdapterIndex = -1;
        this.mDisplayOffset = 0;
        this.mCurrentX = 0;
        this.mNextX = 0;
        this.mMaxX = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        setCurrentScrollState(ScrollState.SCROLL_STATE_IDLE);
    }

    private void reset() {
        initView();
        removeAllViewsInLayout();
        requestLayout();
    }

    public void setSelection(int i) {
        this.mCurrentlySelectedAdapterIndex = i;
    }

    public View getSelectedView() {
        return getChild(this.mCurrentlySelectedAdapterIndex);
    }

    public void setAdapter(ListAdapter listAdapter) {
        if (this.mAdapter != null) {
            this.mAdapter.unregisterDataSetObserver(this.mAdapterDataObserver);
        }
        if (listAdapter != null) {
            this.mHasNotifiedRunningLowOnData = false;
            this.mAdapter = listAdapter;
            this.mAdapter.registerDataSetObserver(this.mAdapterDataObserver);
        }
        initializeRecycledViewCache(this.mAdapter.getViewTypeCount());
        reset();
    }

    public ListAdapter getAdapter() {
        return this.mAdapter;
    }

    private void initializeRecycledViewCache(int i) {
        this.mRemovedViewsCache.clear();
        for (int i2 = 0; i2 < i; i2++) {
            this.mRemovedViewsCache.add(new LinkedList());
        }
    }

    private View getRecycledView(int i) {
        i = this.mAdapter.getItemViewType(i);
        return isItemViewTypeValid(i) ? (View) ((Queue) this.mRemovedViewsCache.get(i)).poll() : 0;
    }

    private void recycleView(int i, View view) {
        i = this.mAdapter.getItemViewType(i);
        if (isItemViewTypeValid(i)) {
            ((Queue) this.mRemovedViewsCache.get(i)).offer(view);
        }
    }

    private boolean isItemViewTypeValid(int i) {
        return i < this.mRemovedViewsCache.size();
    }

    private void addAndMeasureChild(View view, int i) {
        addViewInLayout(view, i, getLayoutParams(view), true);
        measureChild(view);
    }

    private void measureChild(View view) {
        int makeMeasureSpec;
        LayoutParams layoutParams = getLayoutParams(view);
        int childMeasureSpec = ViewGroup.getChildMeasureSpec(this.mHeightMeasureSpec, getPaddingTop() + getPaddingBottom(), layoutParams.height);
        if (layoutParams.width > 0) {
            makeMeasureSpec = MeasureSpec.makeMeasureSpec(layoutParams.width, 1073741824);
        } else {
            makeMeasureSpec = MeasureSpec.makeMeasureSpec(0, 0);
        }
        view.measure(makeMeasureSpec, childMeasureSpec);
    }

    private LayoutParams getLayoutParams(View view) {
        view = view.getLayoutParams();
        return view == null ? new LayoutParams(-2, -1) : view;
    }

    @SuppressLint({"WrongCall"})
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.mAdapter != null) {
            int i5;
            invalidate();
            if (this.mDataChanged) {
                i5 = this.mCurrentX;
                initView();
                removeAllViewsInLayout();
                this.mNextX = i5;
                this.mDataChanged = false;
            }
            if (this.mRestoreX != null) {
                this.mNextX = this.mRestoreX.intValue();
                this.mRestoreX = null;
            }
            if (this.mFlingTracker.computeScrollOffset()) {
                this.mNextX = this.mFlingTracker.getCurrX();
            }
            if (this.mNextX < 0) {
                this.mNextX = 0;
                if (this.mEdgeGlowLeft.isFinished()) {
                    this.mEdgeGlowLeft.onAbsorb((int) determineFlingAbsorbVelocity());
                }
                this.mFlingTracker.forceFinished(true);
                setCurrentScrollState(ScrollState.SCROLL_STATE_IDLE);
            } else if (this.mNextX > this.mMaxX) {
                this.mNextX = this.mMaxX;
                if (this.mEdgeGlowRight.isFinished()) {
                    this.mEdgeGlowRight.onAbsorb((int) determineFlingAbsorbVelocity());
                }
                this.mFlingTracker.forceFinished(true);
                setCurrentScrollState(ScrollState.SCROLL_STATE_IDLE);
            }
            i5 = this.mCurrentX - this.mNextX;
            removeNonVisibleChildren(i5);
            fillList(i5);
            positionChildren(i5);
            this.mCurrentX = this.mNextX;
            if (determineMaxX()) {
                onLayout(z, i, i2, i3, i4);
                return;
            }
            if (!this.mFlingTracker.isFinished()) {
                ViewCompat.postOnAnimation(this, this.mDelayedLayout);
            } else if (this.mCurrentScrollState == ScrollState.SCROLL_STATE_FLING) {
                setCurrentScrollState(ScrollState.SCROLL_STATE_IDLE);
            }
        }
    }

    protected float getLeftFadingEdgeStrength() {
        int horizontalFadingEdgeLength = getHorizontalFadingEdgeLength();
        if (this.mCurrentX == 0) {
            return 0.0f;
        }
        return this.mCurrentX < horizontalFadingEdgeLength ? ((float) this.mCurrentX) / ((float) horizontalFadingEdgeLength) : 1.0f;
    }

    protected float getRightFadingEdgeStrength() {
        int horizontalFadingEdgeLength = getHorizontalFadingEdgeLength();
        if (this.mCurrentX == this.mMaxX) {
            return 0.0f;
        }
        return this.mMaxX - this.mCurrentX < horizontalFadingEdgeLength ? ((float) (this.mMaxX - this.mCurrentX)) / ((float) horizontalFadingEdgeLength) : 1.0f;
    }

    private float determineFlingAbsorbVelocity() {
        return VERSION.SDK_INT >= 14 ? IceCreamSandwichPlus.getCurrVelocity(this.mFlingTracker) : FLING_DEFAULT_ABSORB_VELOCITY;
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.mHeightMeasureSpec = i2;
    }

    private boolean determineMaxX() {
        if (isLastItemInAdapter(this.mRightViewAdapterIndex)) {
            View rightmostChild = getRightmostChild();
            if (rightmostChild != null) {
                int i = this.mMaxX;
                this.mMaxX = (this.mCurrentX + (rightmostChild.getRight() - getPaddingLeft())) - getRenderWidth();
                if (this.mMaxX < 0) {
                    this.mMaxX = 0;
                }
                if (this.mMaxX != i) {
                    return true;
                }
            }
        }
        return false;
    }

    private void fillList(int i) {
        View rightmostChild = getRightmostChild();
        int i2 = 0;
        fillListRight(rightmostChild != null ? rightmostChild.getRight() : 0, i);
        rightmostChild = getLeftmostChild();
        if (rightmostChild != null) {
            i2 = rightmostChild.getLeft();
        }
        fillListLeft(i2, i);
    }

    private void removeNonVisibleChildren(int i) {
        View leftmostChild = getLeftmostChild();
        while (leftmostChild != null && leftmostChild.getRight() + i <= 0) {
            this.mDisplayOffset += isLastItemInAdapter(this.mLeftViewAdapterIndex) ? leftmostChild.getMeasuredWidth() : this.mDividerWidth + leftmostChild.getMeasuredWidth();
            recycleView(this.mLeftViewAdapterIndex, leftmostChild);
            removeViewInLayout(leftmostChild);
            this.mLeftViewAdapterIndex++;
            leftmostChild = getLeftmostChild();
        }
        leftmostChild = getRightmostChild();
        while (leftmostChild != null && leftmostChild.getLeft() + i >= getWidth()) {
            recycleView(this.mRightViewAdapterIndex, leftmostChild);
            removeViewInLayout(leftmostChild);
            this.mRightViewAdapterIndex--;
            leftmostChild = getRightmostChild();
        }
    }

    private void fillListRight(int i, int i2) {
        while ((i + i2) + this.mDividerWidth < getWidth() && this.mRightViewAdapterIndex + 1 < this.mAdapter.getCount()) {
            this.mRightViewAdapterIndex++;
            if (this.mLeftViewAdapterIndex < 0) {
                this.mLeftViewAdapterIndex = this.mRightViewAdapterIndex;
            }
            View view = this.mAdapter.getView(this.mRightViewAdapterIndex, getRecycledView(this.mRightViewAdapterIndex), this);
            addAndMeasureChild(view, -1);
            i += (this.mRightViewAdapterIndex == 0 ? 0 : this.mDividerWidth) + view.getMeasuredWidth();
            determineIfLowOnData();
        }
    }

    private void fillListLeft(int i, int i2) {
        while ((i + i2) - this.mDividerWidth > 0 && this.mLeftViewAdapterIndex >= 1) {
            this.mLeftViewAdapterIndex--;
            View view = this.mAdapter.getView(this.mLeftViewAdapterIndex, getRecycledView(this.mLeftViewAdapterIndex), this);
            addAndMeasureChild(view, 0);
            i -= this.mLeftViewAdapterIndex == 0 ? view.getMeasuredWidth() : this.mDividerWidth + view.getMeasuredWidth();
            this.mDisplayOffset -= i + i2 == 0 ? view.getMeasuredWidth() : view.getMeasuredWidth() + this.mDividerWidth;
        }
    }

    private void positionChildren(int i) {
        int childCount = getChildCount();
        if (childCount > 0) {
            this.mDisplayOffset += i;
            i = this.mDisplayOffset;
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = getChildAt(i2);
                int paddingLeft = getPaddingLeft() + i;
                int paddingTop = getPaddingTop();
                childAt.layout(paddingLeft, paddingTop, childAt.getMeasuredWidth() + paddingLeft, childAt.getMeasuredHeight() + paddingTop);
                i += childAt.getMeasuredWidth() + this.mDividerWidth;
            }
        }
    }

    private View getLeftmostChild() {
        return getChildAt(0);
    }

    private View getRightmostChild() {
        return getChildAt(getChildCount() - 1);
    }

    private View getChild(int i) {
        return (i < this.mLeftViewAdapterIndex || i > this.mRightViewAdapterIndex) ? 0 : getChildAt(i - this.mLeftViewAdapterIndex);
    }

    private int getChildIndex(int i, int i2) {
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            getChildAt(i3).getHitRect(this.mRect);
            if (this.mRect.contains(i, i2)) {
                return i3;
            }
        }
        return -1;
    }

    private boolean isLastItemInAdapter(int i) {
        return i == this.mAdapter.getCount() - 1;
    }

    private int getRenderHeight() {
        return (getHeight() - getPaddingTop()) - getPaddingBottom();
    }

    private int getRenderWidth() {
        return (getWidth() - getPaddingLeft()) - getPaddingRight();
    }

    public void scrollTo(int i) {
        this.mFlingTracker.startScroll(this.mNextX, 0, i - this.mNextX, 0);
        setCurrentScrollState(ScrollState.SCROLL_STATE_FLING);
        requestLayout();
    }

    public int getFirstVisiblePosition() {
        return this.mLeftViewAdapterIndex;
    }

    public int getLastVisiblePosition() {
        return this.mRightViewAdapterIndex;
    }

    private void drawEdgeGlow(Canvas canvas) {
        int save;
        int height;
        if (this.mEdgeGlowLeft != null && !this.mEdgeGlowLeft.isFinished() && isEdgeGlowEnabled()) {
            save = canvas.save();
            height = getHeight();
            canvas.rotate(-90.0f, 0.0f, 0.0f);
            canvas.translate((float) ((-height) + getPaddingBottom()), 0.0f);
            this.mEdgeGlowLeft.setSize(getRenderHeight(), getRenderWidth());
            if (this.mEdgeGlowLeft.draw(canvas)) {
                invalidate();
            }
            canvas.restoreToCount(save);
        } else if (this.mEdgeGlowRight != null && !this.mEdgeGlowRight.isFinished() && isEdgeGlowEnabled()) {
            save = canvas.save();
            height = getWidth();
            canvas.rotate(90.0f, 0.0f, 0.0f);
            canvas.translate((float) getPaddingTop(), (float) (-height));
            this.mEdgeGlowRight.setSize(getRenderHeight(), getRenderWidth());
            if (this.mEdgeGlowRight.draw(canvas)) {
                invalidate();
            }
            canvas.restoreToCount(save);
        }
    }

    private void drawDividers(Canvas canvas) {
        int childCount = getChildCount();
        Rect rect = this.mRect;
        this.mRect.top = getPaddingTop();
        this.mRect.bottom = this.mRect.top + getRenderHeight();
        for (int i = 0; i < childCount; i++) {
            if (i != childCount - 1 || !isLastItemInAdapter(this.mRightViewAdapterIndex)) {
                View childAt = getChildAt(i);
                rect.left = childAt.getRight();
                rect.right = childAt.getRight() + this.mDividerWidth;
                if (rect.left < getPaddingLeft()) {
                    rect.left = getPaddingLeft();
                }
                if (rect.right > getWidth() - getPaddingRight()) {
                    rect.right = getWidth() - getPaddingRight();
                }
                drawDivider(canvas, rect);
                if (i == 0 && childAt.getLeft() > getPaddingLeft()) {
                    rect.left = getPaddingLeft();
                    rect.right = childAt.getLeft();
                    drawDivider(canvas, rect);
                }
            }
        }
    }

    private void drawDivider(Canvas canvas, Rect rect) {
        if (this.mDivider != null) {
            this.mDivider.setBounds(rect);
            this.mDivider.draw(canvas);
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDividers(canvas);
    }

    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawEdgeGlow(canvas);
    }

    protected boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        this.mFlingTracker.fling(this.mNextX, 0, (int) (-f), 0, 0, this.mMaxX, 0, 0);
        setCurrentScrollState(ScrollState.SCROLL_STATE_FLING);
        requestLayout();
        return true;
    }

    protected boolean onDown(MotionEvent motionEvent) {
        this.mBlockTouchAction = this.mFlingTracker.isFinished() ^ true;
        this.mFlingTracker.forceFinished(true);
        setCurrentScrollState(ScrollState.SCROLL_STATE_IDLE);
        unpressTouchedChild();
        if (!this.mBlockTouchAction) {
            motionEvent = getChildIndex((int) motionEvent.getX(), (int) motionEvent.getY());
            if (motionEvent >= null) {
                this.mViewBeingTouched = getChildAt(motionEvent);
                if (this.mViewBeingTouched != null) {
                    this.mViewBeingTouched.setPressed(true);
                    refreshDrawableState();
                }
            }
        }
        return true;
    }

    private void unpressTouchedChild() {
        if (this.mViewBeingTouched != null) {
            this.mViewBeingTouched.setPressed(false);
            refreshDrawableState();
            this.mViewBeingTouched = null;
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 1) {
            if (this.mFlingTracker == null || this.mFlingTracker.isFinished()) {
                setCurrentScrollState(ScrollState.SCROLL_STATE_IDLE);
            }
            requestParentListViewToNotInterceptTouchEvents(Boolean.valueOf(false));
            releaseEdgeGlow();
        } else if (motionEvent.getAction() == 3) {
            unpressTouchedChild();
            releaseEdgeGlow();
            requestParentListViewToNotInterceptTouchEvents(Boolean.valueOf(false));
        }
        return super.onTouchEvent(motionEvent);
    }

    private void releaseEdgeGlow() {
        if (this.mEdgeGlowLeft != null) {
            this.mEdgeGlowLeft.onRelease();
        }
        if (this.mEdgeGlowRight != null) {
            this.mEdgeGlowRight.onRelease();
        }
    }

    public void setRunningOutOfDataListener(RunningOutOfDataListener runningOutOfDataListener, int i) {
        this.mRunningOutOfDataListener = runningOutOfDataListener;
        this.mRunningOutOfDataThreshold = i;
    }

    private void determineIfLowOnData() {
        if (this.mRunningOutOfDataListener != null && this.mAdapter != null && this.mAdapter.getCount() - (this.mRightViewAdapterIndex + 1) < this.mRunningOutOfDataThreshold && !this.mHasNotifiedRunningLowOnData) {
            this.mHasNotifiedRunningLowOnData = true;
            this.mRunningOutOfDataListener.onRunningOutOfData();
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    public void setOnScrollStateChangedListener(OnScrollStateChangedListener onScrollStateChangedListener) {
        this.mOnScrollStateChangedListener = onScrollStateChangedListener;
    }

    private void setCurrentScrollState(ScrollState scrollState) {
        if (!(this.mCurrentScrollState == scrollState || this.mOnScrollStateChangedListener == null)) {
            this.mOnScrollStateChangedListener.onScrollStateChanged(scrollState);
        }
        this.mCurrentScrollState = scrollState;
    }

    private void updateOverscrollAnimation(int i) {
        if (this.mEdgeGlowLeft != null) {
            if (this.mEdgeGlowRight != null) {
                int i2 = this.mCurrentX + i;
                if (this.mFlingTracker == null || this.mFlingTracker.isFinished()) {
                    if (i2 < 0) {
                        this.mEdgeGlowLeft.onPull(((float) Math.abs(i)) / ((float) getRenderWidth()));
                        if (this.mEdgeGlowRight.isFinished() == 0) {
                            this.mEdgeGlowRight.onRelease();
                        }
                    } else if (i2 > this.mMaxX) {
                        this.mEdgeGlowRight.onPull(((float) Math.abs(i)) / ((float) getRenderWidth()));
                        if (this.mEdgeGlowLeft.isFinished() == 0) {
                            this.mEdgeGlowLeft.onRelease();
                        }
                    }
                }
            }
        }
    }

    private boolean isEdgeGlowEnabled() {
        boolean z = false;
        if (this.mAdapter != null) {
            if (!this.mAdapter.isEmpty()) {
                if (this.mMaxX > 0) {
                    z = true;
                }
                return z;
            }
        }
        return false;
    }
}
