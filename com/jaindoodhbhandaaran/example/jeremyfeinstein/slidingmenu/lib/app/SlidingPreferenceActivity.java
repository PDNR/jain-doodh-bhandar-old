package com.jaindoodhbhandaaran.example.jeremyfeinstein.slidingmenu.lib.app;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import com.jaindoodhbhandaaran.example.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class SlidingPreferenceActivity extends PreferenceActivity implements SlidingActivityBase {
    private SlidingActivityHelper mHelper;

    public void onCreate(Bundle bundle) {
        this.mHelper = new SlidingActivityHelper(this);
        super.onCreate(bundle);
        this.mHelper.onCreate(bundle);
    }

    public void onPostCreate(Bundle bundle) {
        super.onPostCreate(bundle);
        this.mHelper.onPostCreate(bundle);
    }

    public View findViewById(int i) {
        View findViewById = super.findViewById(i);
        if (findViewById != null) {
            return findViewById;
        }
        return this.mHelper.findViewById(i);
    }

    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        this.mHelper.onSaveInstanceState(bundle);
    }

    public void setContentView(int i) {
        setContentView(getLayoutInflater().inflate(i, null));
    }

    public void setContentView(View view) {
        setContentView(view, new LayoutParams(-1, -1));
    }

    public void setContentView(View view, LayoutParams layoutParams) {
        super.setContentView(view, layoutParams);
        this.mHelper.registerAboveContentView(view, layoutParams);
    }

    public void setBehindContentView(int i) {
        setBehindContentView(getLayoutInflater().inflate(i, null));
    }

    public void setBehindContentView(View view) {
        setBehindContentView(view, new LayoutParams(-1, -1));
    }

    public void setBehindContentView(View view, LayoutParams layoutParams) {
        this.mHelper.setBehindContentView(view, layoutParams);
    }

    public SlidingMenu getSlidingMenu() {
        return this.mHelper.getSlidingMenu();
    }

    public void toggle() {
        this.mHelper.toggle();
    }

    public void showContent() {
        this.mHelper.showContent();
    }

    public void showMenu() {
        this.mHelper.showMenu();
    }

    public void showSecondaryMenu() {
        this.mHelper.showSecondaryMenu();
    }

    public void setSlidingActionBarEnabled(boolean z) {
        this.mHelper.setSlidingActionBarEnabled(z);
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        boolean onKeyUp = this.mHelper.onKeyUp(i, keyEvent);
        if (onKeyUp) {
            return onKeyUp;
        }
        return super.onKeyUp(i, keyEvent);
    }
}
