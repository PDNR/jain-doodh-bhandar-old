package com.jaindoodhbhandaaran.activity.dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.jaindoodhbhandaaran.R;
import com.jaindoodhbhandaaran.activity.LoginActivity;
import com.jaindoodhbhandaaran.appfragment.customerfragment.HomeFragment;
import com.jaindoodhbhandaaran.appfragment.customerfragment.LeftFrameLayout;
import com.jaindoodhbhandaaran.example.interfaces.FragCommunicationInterf;
import com.jaindoodhbhandaaran.example.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jaindoodhbhandaaran.example.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnCloseListener;
import com.jaindoodhbhandaaran.example.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.jaindoodhbhandaaran.example.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.jaindoodhbhandaaran.example.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenedListener;
import com.jaindoodhbhandaaran.example.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.jaindoodhbhandaaran.util.AppPref;
import com.jaindoodhbhandaaran.util.Config;

public class CustomerDashboardActivity extends SlidingFragmentActivity implements FragCommunicationInterf {
    static FragmentManager fm;
    static Fragment fragment;
    static FragmentTransaction ft;
    static Context mContext;
    public static ImageView tool_search;
    public static TextView tool_title;
    String TAG = getClass().getName();
    ActionBar actionBar;
    CustomerDashboardActivity dashboardActivity;
    OnCloseListener mOnCloseListener = new OnCloseListener() {
        public void onClose() {
        }
    };
    OnClosedListener mOnClosedListener = new OnClosedListener() {
        public void onClosed() {
        }
    };
    OnOpenListener mOnOpenListener = new OnOpenListener() {
        public void onOpen() {
        }
    };
    OnOpenedListener mOnOpenedListener = new OnOpenedListener() {
        public void onOpened() {
        }
    };
    SlidingMenu slidemenu;
    ImageView tool_barMenu;
    Toolbar toolbar;

    public void LeftMenuContentClickListner(String str, int i) {
    }

    public void otherAppClick(String str, String str2, String str3) {
    }

    public void restartFetchingAllData() {
    }

    public void slideMenu() {
    }

    public void switchToDetailContentListner() {
    }

    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_dashboard);
        fm = getSupportFragmentManager();
        mContext = this;
        this.dashboardActivity = this;
        setActionBar();
        setUpSlidingMenu();
        displayView(0, new Bundle());
    }

    public static void displayView(int i, Bundle bundle) {
        fragment = null;
        ft = fm.beginTransaction();
        if (i == 0) {
            fragment = HomeFragment.getInstance(mContext);
            if (fragment != 0) {
                ft.replace(R.id.content_frame, fragment, Config.HomeFragment_Tag);
            }
            ft.commit();
        }
    }

    public static void popBackStack() {
        if (fm != null) {
            fm.popBackStack();
        }
    }

    public static void logOut() {
        if (fm != null) {
            AppPref.clearSession();
            fm.popBackStack(null, 1);
            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.addFlags(268468224);
            mContext.startActivity(intent);
        }
    }

    public static void boldStyle() {
        if (tool_title != null) {
            tool_title.setTypeface(null, 1);
        }
    }

    public static void updateTitle(String str) {
        if (tool_title != null) {
            tool_title.setText(str);
        }
    }

    public static void hideSearchButton() {
        if (tool_search != null) {
            tool_search.setVisibility(8);
        }
    }

    public static void showSearchButton() {
        if (tool_search != null) {
            tool_search.setVisibility(0);
        }
    }

    private void setActionBar() {
        this.toolbar = (Toolbar) findViewById(R.id.tool_barId);
        setSupportActionBar(this.toolbar);
        this.actionBar = getSupportActionBar();
        this.actionBar.setDisplayShowTitleEnabled(false);
        this.tool_barMenu = (ImageView) findViewById(R.id.tool_barMenu);
        tool_search = (ImageView) findViewById(R.id.tool_search);
        tool_title = (TextView) findViewById(R.id.tool_title);
        this.tool_barMenu.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                CustomerDashboardActivity.this.slidemenu.toggle();
            }
        });
        tool_search.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
            }
        });
    }

    private void setUpSlidingMenu() {
        if (findViewById(R.layout.menu_frame) == null) {
            setBehindContentView((int) R.layout.menu_frame);
            getSlidingMenu().setSlidingEnabled(true);
            getSlidingMenu().setTouchModeAbove(0);
        }
        startLeftMenu();
        this.slidemenu = getSlidingMenu();
        this.slidemenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        this.slidemenu.setShadowDrawable((int) R.drawable.shadow);
        this.slidemenu.setBehindScrollScale(0.25f);
        this.slidemenu.setFadeDegree(0.25f);
        this.slidemenu.setBehindOffset((int) (((double) getWindowManager().getDefaultDisplay().getWidth()) / 3.2d));
        this.slidemenu.setBehindScrollScale(0.25f);
        this.slidemenu.setFadeDegree(0.25f);
        this.slidemenu.attachToActivity(this, 1);
    }

    private void startLeftMenu() {
        try {
            Fragment leftFrameLayout = new LeftFrameLayout();
            leftFrameLayout.setmContext(mContext);
            FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
            beginTransaction.replace(R.id.menu_frame1, leftFrameLayout, "ContentFrame");
            beginTransaction.commit();
        } catch (Exception e) {
            String str = this.TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(e.toString());
            Log.e(str, stringBuilder.toString());
        }
    }
}
