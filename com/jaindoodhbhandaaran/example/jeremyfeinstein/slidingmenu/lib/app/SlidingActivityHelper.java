package com.jaindoodhbhandaaran.example.jeremyfeinstein.slidingmenu.lib.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import com.jaindoodhbhandaaran.R;
import com.jaindoodhbhandaaran.example.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class SlidingActivityHelper {
    private Activity mActivity;
    private boolean mBroadcasting = false;
    private boolean mEnableSlide = true;
    private boolean mOnPostCreateCalled = false;
    private SlidingMenu mSlidingMenu;
    private View mViewAbove;
    private View mViewBehind;

    public SlidingActivityHelper(Activity activity) {
        this.mActivity = activity;
    }

    public void onCreate(Bundle bundle) {
        this.mSlidingMenu = (SlidingMenu) LayoutInflater.from(this.mActivity).inflate(R.layout.slidingmenumain, null);
    }

    public void onPostCreate(Bundle bundle) {
        if (this.mViewBehind != null) {
            if (this.mViewAbove != null) {
                this.mOnPostCreateCalled = true;
                this.mSlidingMenu.attachToActivity(this.mActivity, 1 ^ this.mEnableSlide);
                boolean z = false;
                if (bundle != null) {
                    z = bundle.getBoolean("SlidingActivityHelper.open");
                    bundle = bundle.getBoolean("SlidingActivityHelper.secondary");
                } else {
                    bundle = null;
                }
                new Handler().post(new Runnable() {
                    public void run() {
                        if (!z) {
                            SlidingActivityHelper.this.mSlidingMenu.showContent(false);
                        } else if (bundle) {
                            SlidingActivityHelper.this.mSlidingMenu.showSecondaryMenu(false);
                        } else {
                            SlidingActivityHelper.this.mSlidingMenu.showMenu(false);
                        }
                    }
                });
                return;
            }
        }
        throw new IllegalStateException("Both setBehindContentView must be called in onCreate in addition to setContentView.");
    }

    public void setSlidingActionBarEnabled(boolean z) {
        if (this.mOnPostCreateCalled) {
            throw new IllegalStateException("enableSlidingActionBar must be called in onCreate.");
        }
        this.mEnableSlide = z;
    }

    public View findViewById(int i) {
        if (this.mSlidingMenu != null) {
            i = this.mSlidingMenu.findViewById(i);
            if (i != 0) {
                return i;
            }
        }
        return 0;
    }

    public void onSaveInstanceState(Bundle bundle) {
        bundle.putBoolean("SlidingActivityHelper.open", this.mSlidingMenu.isMenuShowing());
        bundle.putBoolean("SlidingActivityHelper.secondary", this.mSlidingMenu.isSecondaryMenuShowing());
    }

    public void registerAboveContentView(View view, LayoutParams layoutParams) {
        if (this.mBroadcasting == null) {
            this.mViewAbove = view;
        }
    }

    public void setContentView(View view) {
        this.mBroadcasting = true;
        this.mActivity.setContentView(view);
    }

    public void setBehindContentView(View view, LayoutParams layoutParams) {
        this.mViewBehind = view;
        this.mSlidingMenu.setMenu(this.mViewBehind);
    }

    public SlidingMenu getSlidingMenu() {
        return this.mSlidingMenu;
    }

    public void toggle() {
        this.mSlidingMenu.toggle();
    }

    public void showContent() {
        this.mSlidingMenu.showContent();
    }

    public void showMenu() {
        this.mSlidingMenu.showMenu();
    }

    public void showSecondaryMenu() {
        this.mSlidingMenu.showSecondaryMenu();
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (i != 4 || this.mSlidingMenu.isMenuShowing() == 0) {
            return false;
        }
        showContent();
        return true;
    }
}
