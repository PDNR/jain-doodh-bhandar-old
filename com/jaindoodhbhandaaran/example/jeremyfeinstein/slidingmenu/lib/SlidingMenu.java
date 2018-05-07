package com.jaindoodhbhandaaran.example.jeremyfeinstein.slidingmenu.lib;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.jaindoodhbhandaaran.R;
import com.jaindoodhbhandaaran.example.jeremyfeinstein.slidingmenu.lib.CustomViewAbove.OnPageChangeListener;

public class SlidingMenu extends RelativeLayout {
    public static final int LEFT = 0;
    public static final int LEFT_RIGHT = 2;
    public static final int RIGHT = 1;
    public static final int SLIDING_CONTENT = 1;
    public static final int SLIDING_WINDOW = 0;
    private static final String TAG = "SlidingMenu";
    public static final int TOUCHMODE_FULLSCREEN = 1;
    public static final int TOUCHMODE_MARGIN = 0;
    public static final int TOUCHMODE_NONE = 2;
    private boolean mActionbarOverlay;
    private OnCloseListener mCloseListener;
    private OnOpenListener mOpenListener;
    private OnOpenListener mSecondaryOpenListner;
    private CustomViewAbove mViewAbove;
    private CustomViewBehind mViewBehind;

    public interface CanvasTransformer {
        void transformCanvas(Canvas canvas, float f);
    }

    public interface OnCloseListener {
        void onClose();
    }

    public interface OnClosedListener {
        void onClosed();
    }

    public interface OnOpenListener {
        void onOpen();
    }

    public interface OnOpenedListener {
        void onOpened();
    }

    public static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        private final int mItem;

        public SavedState(Parcelable parcelable, int i) {
            super(parcelable);
            this.mItem = i;
        }

        private SavedState(Parcel parcel) {
            super(parcel);
            this.mItem = parcel.readInt();
        }

        public int getItem() {
            return this.mItem;
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.mItem);
        }
    }

    public SlidingMenu(Context context) {
        this(context, null);
    }

    public SlidingMenu(Activity activity, int i) {
        this((Context) activity, null);
        attachToActivity(activity, i);
    }

    public SlidingMenu(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SlidingMenu(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mActionbarOverlay = false;
        LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -1);
        this.mViewBehind = new CustomViewBehind(context);
        addView(this.mViewBehind, layoutParams);
        layoutParams = new RelativeLayout.LayoutParams(-1, -1);
        this.mViewAbove = new CustomViewAbove(context);
        addView(this.mViewAbove, layoutParams);
        this.mViewAbove.setCustomViewBehind(this.mViewBehind);
        this.mViewBehind.setCustomViewAbove(this.mViewAbove);
        this.mViewAbove.setOnPageChangeListener(new OnPageChangeListener() {
            public static final int POSITION_CLOSE = 1;
            public static final int POSITION_OPEN = 0;
            public static final int POSITION_SECONDARY_OPEN = 2;

            public void onPageScrolled(int i, float f, int i2) {
            }

            public void onPageSelected(int i) {
                if (i == 0 && SlidingMenu.this.mOpenListener != null) {
                    SlidingMenu.this.mOpenListener.onOpen();
                } else if (i == 1 && SlidingMenu.this.mCloseListener != null) {
                    SlidingMenu.this.mCloseListener.onClose();
                } else if (i == 2 && SlidingMenu.this.mSecondaryOpenListner != 0) {
                    SlidingMenu.this.mSecondaryOpenListner.onOpen();
                }
            }
        });
        attributeSet = context.obtainStyledAttributes(attributeSet, R.styleable.SlidingMenu);
        setMode(attributeSet.getInt(8, 0));
        int resourceId = attributeSet.getResourceId(15, -1);
        if (resourceId != -1) {
            setContent(resourceId);
        } else {
            setContent(new FrameLayout(context));
        }
        resourceId = attributeSet.getResourceId(16, -1);
        if (resourceId != -1) {
            setMenu(resourceId);
        } else {
            setMenu(new FrameLayout(context));
        }
        setTouchModeAbove(attributeSet.getInt(13, 0));
        setTouchModeBehind(attributeSet.getInt(14, 0));
        context = (int) attributeSet.getDimension(3, -1.0f);
        resourceId = (int) attributeSet.getDimension(5, -1.0f);
        if (context == -1 || resourceId == -1) {
            if (context != -1) {
                setBehindOffset(context);
            } else if (resourceId != -1) {
                setBehindWidth(resourceId);
            } else {
                setBehindOffset(0);
            }
            setBehindScrollScale(attributeSet.getFloat(4, 0.33f));
            int resourceId2 = attributeSet.getResourceId(11, -1);
            if (resourceId2 != -1) {
                setShadowDrawable(resourceId2);
            }
            setShadowWidth((int) attributeSet.getDimension(12, 0.0f));
            setFadeEnabled(attributeSet.getBoolean(7, true));
            setFadeDegree(attributeSet.getFloat(6, 0.33f));
            setSelectorEnabled(attributeSet.getBoolean(10, false));
            context = attributeSet.getResourceId(9, -1);
            if (context != -1) {
                setSelectorDrawable(context);
            }
            attributeSet.recycle();
            return;
        }
        throw new IllegalStateException("Cannot set both behindOffset and behindWidth for a SlidingMenu");
    }

    public void attachToActivity(Activity activity, int i) {
        attachToActivity(activity, i, false);
    }

    public void attachToActivity(Activity activity, int i, boolean z) {
        if (i != 0 && i != 1) {
            throw new IllegalArgumentException("slideStyle must be either SLIDING_WINDOW or SLIDING_CONTENT");
        } else if (getParent() != null) {
            throw new IllegalStateException("This SlidingMenu appears to already be attached");
        } else {
            TypedArray obtainStyledAttributes = activity.getTheme().obtainStyledAttributes(new int[]{16842836});
            int resourceId = obtainStyledAttributes.getResourceId(0, 0);
            obtainStyledAttributes.recycle();
            ViewGroup viewGroup;
            View view;
            switch (i) {
                case 0:
                    this.mActionbarOverlay = false;
                    viewGroup = (ViewGroup) activity.getWindow().getDecorView();
                    view = (ViewGroup) viewGroup.getChildAt(0);
                    view.setBackgroundResource(resourceId);
                    viewGroup.removeView(view);
                    viewGroup.addView(this);
                    setContent(view);
                    return;
                case 1:
                    this.mActionbarOverlay = z;
                    viewGroup = (ViewGroup) activity.findViewById(16908290);
                    view = viewGroup.getChildAt(0);
                    viewGroup.removeView(view);
                    viewGroup.addView(this);
                    setContent(view);
                    if (view.getBackground() == null) {
                        view.setBackgroundResource(resourceId);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    public void setContent(int i) {
        setContent(LayoutInflater.from(getContext()).inflate(i, null));
    }

    public void setContent(View view) {
        this.mViewAbove.setContent(view);
        showContent();
    }

    public View getContent() {
        return this.mViewAbove.getContent();
    }

    public void setMenu(int i) {
        setMenu(LayoutInflater.from(getContext()).inflate(i, null));
    }

    public void setMenu(View view) {
        this.mViewBehind.setContent(view);
    }

    public View getMenu() {
        return this.mViewBehind.getContent();
    }

    public void setSecondaryMenu(int i) {
        setSecondaryMenu(LayoutInflater.from(getContext()).inflate(i, null));
    }

    public void setSecondaryMenu(View view) {
        this.mViewBehind.setSecondaryContent(view);
    }

    public View getSecondaryMenu() {
        return this.mViewBehind.getSecondaryContent();
    }

    public void setSlidingEnabled(boolean z) {
        this.mViewAbove.setSlidingEnabled(z);
    }

    public boolean isSlidingEnabled() {
        return this.mViewAbove.isSlidingEnabled();
    }

    public void setMode(int i) {
        if (i == 0 || i == 1 || i == 2) {
            this.mViewBehind.setMode(i);
            return;
        }
        throw new IllegalStateException("SlidingMenu mode must be LEFT, RIGHT, or LEFT_RIGHT");
    }

    public int getMode() {
        return this.mViewBehind.getMode();
    }

    public void setStatic(boolean z) {
        if (z) {
            setSlidingEnabled(false);
            this.mViewAbove.setCustomViewBehind(null);
            this.mViewAbove.setCurrentItem(1);
            return;
        }
        this.mViewAbove.setCurrentItem(1);
        this.mViewAbove.setCustomViewBehind(this.mViewBehind);
        setSlidingEnabled(true);
    }

    public void showMenu() {
        showMenu(true);
    }

    public void showMenu(boolean z) {
        this.mViewAbove.setCurrentItem(0, z);
    }

    public void showSecondaryMenu() {
        showSecondaryMenu(true);
    }

    public void showSecondaryMenu(boolean z) {
        this.mViewAbove.setCurrentItem(2, z);
    }

    public void showContent() {
        showContent(true);
    }

    public void showContent(boolean z) {
        this.mViewAbove.setCurrentItem(1, z);
    }

    public void toggle() {
        toggle(true);
    }

    public void toggle(boolean z) {
        if (isMenuShowing()) {
            showContent(z);
        } else {
            showMenu(z);
        }
    }

    public boolean isMenuShowing() {
        if (this.mViewAbove.getCurrentItem() != 0) {
            if (this.mViewAbove.getCurrentItem() != 2) {
                return false;
            }
        }
        return true;
    }

    public boolean isSecondaryMenuShowing() {
        return this.mViewAbove.getCurrentItem() == 2;
    }

    public int getBehindOffset() {
        return ((RelativeLayout.LayoutParams) this.mViewBehind.getLayoutParams()).rightMargin;
    }

    public void setBehindOffset(int i) {
        this.mViewBehind.setWidthOffset(i);
    }

    public void setBehindOffsetRes(int i) {
        setBehindOffset((int) getContext().getResources().getDimension(i));
    }

    public void setAboveOffset(int i) {
        this.mViewAbove.setAboveOffset(i);
    }

    public void setAboveOffsetRes(int i) {
        setAboveOffset((int) getContext().getResources().getDimension(i));
    }

    public void setBehindWidth(int r8) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
        /*
        r7 = this;
        r0 = r7.getContext();
        r1 = "window";
        r0 = r0.getSystemService(r1);
        r0 = (android.view.WindowManager) r0;
        r0 = r0.getDefaultDisplay();
        r1 = android.view.Display.class;	 Catch:{ Exception -> 0x002f }
        r2 = 1;	 Catch:{ Exception -> 0x002f }
        r3 = new java.lang.Class[r2];	 Catch:{ Exception -> 0x002f }
        r4 = android.graphics.Point.class;	 Catch:{ Exception -> 0x002f }
        r5 = 0;	 Catch:{ Exception -> 0x002f }
        r3[r5] = r4;	 Catch:{ Exception -> 0x002f }
        r4 = new android.graphics.Point;	 Catch:{ Exception -> 0x002f }
        r4.<init>();	 Catch:{ Exception -> 0x002f }
        r6 = "getSize";	 Catch:{ Exception -> 0x002f }
        r1 = r1.getMethod(r6, r3);	 Catch:{ Exception -> 0x002f }
        r2 = new java.lang.Object[r2];	 Catch:{ Exception -> 0x002f }
        r2[r5] = r4;	 Catch:{ Exception -> 0x002f }
        r1.invoke(r0, r2);	 Catch:{ Exception -> 0x002f }
        r1 = r4.x;	 Catch:{ Exception -> 0x002f }
        goto L_0x0033;
    L_0x002f:
        r1 = r0.getWidth();
    L_0x0033:
        r1 = r1 - r8;
        r7.setBehindOffset(r1);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.jaindoodhbhandaaran.example.jeremyfeinstein.slidingmenu.lib.SlidingMenu.setBehindWidth(int):void");
    }

    public void setBehindWidthRes(int i) {
        setBehindWidth((int) getContext().getResources().getDimension(i));
    }

    public int getTouchmodeMarginThreshold() {
        return this.mViewBehind.getMarginThreshold();
    }

    public void setTouchmodeMarginThreshold(int i) {
        this.mViewBehind.setMarginThreshold(i);
    }

    public void setBehindScrollScale(float f) {
        if (f >= 0.0f || f <= 1.0f) {
            this.mViewBehind.setScrollScale(f);
            return;
        }
        throw new IllegalStateException("ScrollScale must be between 0 and 1");
    }

    public void setBehindCanvasTransformer(CanvasTransformer canvasTransformer) {
        this.mViewBehind.setCanvasTransformer(canvasTransformer);
    }

    public int getTouchModeAbove() {
        return this.mViewAbove.getTouchMode();
    }

    public void setTouchModeAbove(int i) {
        if (i == 1 || i == 0 || i == 2) {
            this.mViewAbove.setTouchMode(i);
            return;
        }
        throw new IllegalStateException("TouchMode must be set to eitherTOUCHMODE_FULLSCREEN or TOUCHMODE_MARGIN or TOUCHMODE_NONE.");
    }

    public void setTouchModeBehind(int i) {
        if (i == 1 || i == 0 || i == 2) {
            this.mViewBehind.setTouchMode(i);
            return;
        }
        throw new IllegalStateException("TouchMode must be set to eitherTOUCHMODE_FULLSCREEN or TOUCHMODE_MARGIN or TOUCHMODE_NONE.");
    }

    public void setShadowDrawable(int i) {
        setShadowDrawable(getContext().getResources().getDrawable(i));
    }

    public void setShadowDrawable(Drawable drawable) {
        this.mViewBehind.setShadowDrawable(drawable);
    }

    public void setSecondaryShadowDrawable(int i) {
        setSecondaryShadowDrawable(getContext().getResources().getDrawable(i));
    }

    public void setSecondaryShadowDrawable(Drawable drawable) {
        this.mViewBehind.setSecondaryShadowDrawable(drawable);
    }

    public void setShadowWidthRes(int i) {
        setShadowWidth((int) getResources().getDimension(i));
    }

    public void setShadowWidth(int i) {
        this.mViewBehind.setShadowWidth(i);
    }

    public void setFadeEnabled(boolean z) {
        this.mViewBehind.setFadeEnabled(z);
    }

    public void setFadeDegree(float f) {
        this.mViewBehind.setFadeDegree(f);
    }

    public void setSelectorEnabled(boolean z) {
        this.mViewBehind.setSelectorEnabled(true);
    }

    public void setSelectedView(View view) {
        this.mViewBehind.setSelectedView(view);
    }

    public void setSelectorDrawable(int i) {
        this.mViewBehind.setSelectorBitmap(BitmapFactory.decodeResource(getResources(), i));
    }

    public void setSelectorBitmap(Bitmap bitmap) {
        this.mViewBehind.setSelectorBitmap(bitmap);
    }

    public void addIgnoredView(View view) {
        this.mViewAbove.addIgnoredView(view);
    }

    public void removeIgnoredView(View view) {
        this.mViewAbove.removeIgnoredView(view);
    }

    public void clearIgnoredViews() {
        this.mViewAbove.clearIgnoredViews();
    }

    public void setOnOpenListener(OnOpenListener onOpenListener) {
        this.mOpenListener = onOpenListener;
    }

    public void setSecondaryOnOpenListner(OnOpenListener onOpenListener) {
        this.mSecondaryOpenListner = onOpenListener;
    }

    public void setOnCloseListener(OnCloseListener onCloseListener) {
        this.mCloseListener = onCloseListener;
    }

    public void setOnOpenedListener(OnOpenedListener onOpenedListener) {
        this.mViewAbove.setOnOpenedListener(onOpenedListener);
    }

    public void setOnClosedListener(OnClosedListener onClosedListener) {
        this.mViewAbove.setOnClosedListener(onClosedListener);
    }

    protected Parcelable onSaveInstanceState() {
        return new SavedState(super.onSaveInstanceState(), this.mViewAbove.getCurrentItem());
    }

    protected void onRestoreInstanceState(Parcelable parcelable) {
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.mViewAbove.setCurrentItem(savedState.getItem());
    }

    @SuppressLint({"NewApi"})
    protected boolean fitSystemWindows(Rect rect) {
        int i = rect.left;
        int i2 = rect.right;
        int i3 = rect.top;
        rect = rect.bottom;
        if (!this.mActionbarOverlay) {
            Log.v(TAG, "setting padding!");
            setPadding(i, i3, i2, rect);
        }
        return true;
    }

    @TargetApi(11)
    public void manageLayers(float f) {
        if (VERSION.SDK_INT >= 11) {
            int i = 0;
            f = (f <= 0.0f || f >= 1.0f) ? 0.0f : Float.MIN_VALUE;
            if (f != null) {
                i = 2;
            }
            if (i != getContent().getLayerType()) {
                getHandler().post(new Runnable() {
                    public void run() {
                        String access$400 = SlidingMenu.TAG;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("changing layerType. hardware? ");
                        stringBuilder.append(i == 2);
                        Log.v(access$400, stringBuilder.toString());
                        SlidingMenu.this.getContent().setLayerType(i, null);
                        SlidingMenu.this.getMenu().setLayerType(i, null);
                        if (SlidingMenu.this.getSecondaryMenu() != null) {
                            SlidingMenu.this.getSecondaryMenu().setLayerType(i, null);
                        }
                    }
                });
            }
        }
    }
}
