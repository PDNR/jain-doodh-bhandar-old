package android.support.v7.widget;

import android.content.Context;
import android.os.Build.VERSION;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.annotation.StyleRes;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.appcompat.R;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.PopupWindow;
import java.lang.reflect.Field;

class AppCompatPopupWindow extends PopupWindow {
    private static final boolean COMPAT_OVERLAP_ANCHOR = (VERSION.SDK_INT < 21);
    private static final String TAG = "AppCompatPopupWindow";
    private boolean mOverlapAnchor;

    public AppCompatPopupWindow(@NonNull Context context, @Nullable AttributeSet attributeSet, @AttrRes int i) {
        super(context, attributeSet, i);
        init(context, attributeSet, i, 0);
    }

    public AppCompatPopupWindow(@NonNull Context context, @Nullable AttributeSet attributeSet, @AttrRes int i, @StyleRes int i2) {
        super(context, attributeSet, i, i2);
        init(context, attributeSet, i, i2);
    }

    private void init(Context context, AttributeSet attributeSet, int i, int i2) {
        context = TintTypedArray.obtainStyledAttributes(context, attributeSet, R.styleable.PopupWindow, i, i2);
        if (context.hasValue(R.styleable.PopupWindow_overlapAnchor) != null) {
            setSupportOverlapAnchor(context.getBoolean(R.styleable.PopupWindow_overlapAnchor, 0));
        }
        setBackgroundDrawable(context.getDrawable(R.styleable.PopupWindow_android_popupBackground));
        attributeSet = VERSION.SDK_INT;
        if (!(i2 == 0 || attributeSet >= 11 || context.hasValue(R.styleable.PopupWindow_android_popupAnimationStyle) == null)) {
            setAnimationStyle(context.getResourceId(R.styleable.PopupWindow_android_popupAnimationStyle, -1));
        }
        context.recycle();
        if (VERSION.SDK_INT < 14) {
            wrapOnScrollChangedListener(this);
        }
    }

    public void showAsDropDown(View view, int i, int i2) {
        if (COMPAT_OVERLAP_ANCHOR && this.mOverlapAnchor) {
            i2 -= view.getHeight();
        }
        super.showAsDropDown(view, i, i2);
    }

    public void showAsDropDown(View view, int i, int i2, int i3) {
        if (COMPAT_OVERLAP_ANCHOR && this.mOverlapAnchor) {
            i2 -= view.getHeight();
        }
        super.showAsDropDown(view, i, i2, i3);
    }

    public void update(View view, int i, int i2, int i3, int i4) {
        if (COMPAT_OVERLAP_ANCHOR && this.mOverlapAnchor) {
            i2 -= view.getHeight();
        }
        super.update(view, i, i2, i3, i4);
    }

    private static void wrapOnScrollChangedListener(final PopupWindow popupWindow) {
        try {
            final Field declaredField = PopupWindow.class.getDeclaredField("mAnchor");
            declaredField.setAccessible(true);
            Field declaredField2 = PopupWindow.class.getDeclaredField("mOnScrollChangedListener");
            declaredField2.setAccessible(true);
            final OnScrollChangedListener onScrollChangedListener = (OnScrollChangedListener) declaredField2.get(popupWindow);
            declaredField2.set(popupWindow, new OnScrollChangedListener() {
                public void onScrollChanged() {
                    /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
                    /*
                    r2 = this;
                    r0 = r0;	 Catch:{ IllegalAccessException -> 0x001a }
                    r1 = r4;	 Catch:{ IllegalAccessException -> 0x001a }
                    r0 = r0.get(r1);	 Catch:{ IllegalAccessException -> 0x001a }
                    r0 = (java.lang.ref.WeakReference) r0;	 Catch:{ IllegalAccessException -> 0x001a }
                    if (r0 == 0) goto L_0x0019;	 Catch:{ IllegalAccessException -> 0x001a }
                L_0x000c:
                    r0 = r0.get();	 Catch:{ IllegalAccessException -> 0x001a }
                    if (r0 != 0) goto L_0x0013;	 Catch:{ IllegalAccessException -> 0x001a }
                L_0x0012:
                    goto L_0x0019;	 Catch:{ IllegalAccessException -> 0x001a }
                L_0x0013:
                    r0 = r1;	 Catch:{ IllegalAccessException -> 0x001a }
                    r0.onScrollChanged();	 Catch:{ IllegalAccessException -> 0x001a }
                    goto L_0x001a;
                L_0x0019:
                    return;
                L_0x001a:
                    return;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.AppCompatPopupWindow.1.onScrollChanged():void");
                }
            });
        } catch (PopupWindow popupWindow2) {
            Log.d(TAG, "Exception while installing workaround OnScrollChangedListener", popupWindow2);
        }
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public void setSupportOverlapAnchor(boolean z) {
        if (COMPAT_OVERLAP_ANCHOR) {
            this.mOverlapAnchor = z;
        } else {
            PopupWindowCompat.setOverlapAnchor(this, z);
        }
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public boolean getSupportOverlapAnchor() {
        if (COMPAT_OVERLAP_ANCHOR) {
            return this.mOverlapAnchor;
        }
        return PopupWindowCompat.getOverlapAnchor(this);
    }
}
