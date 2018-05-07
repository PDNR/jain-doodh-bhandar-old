package com.jaindoodhbhandaaran.activity.dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.jaindoodhbhandaaran.R;
import com.jaindoodhbhandaaran.activity.LoginActivity;
import com.jaindoodhbhandaaran.activity.QRCodeReaderActivity;
import com.jaindoodhbhandaaran.appfragment.hocker_fragment.HockerHomeFragment;
import com.jaindoodhbhandaaran.apppermission.PermissionCallback;
import com.jaindoodhbhandaaran.apppermission.PermissionUtil;
import com.jaindoodhbhandaaran.util.AppPref;
import com.jaindoodhbhandaaran.util.Config;

public class HockerDashboardActivity extends AppCompatActivity implements PermissionCallback {
    private static final int REQUEST_CAMERA_MIC_PERMISSIONS = 10;
    static FragmentManager fm;
    static Fragment fragment;
    static FragmentTransaction ft;
    public static ImageView hocker_tool_barBackButton;
    public static ImageView hocker_tool_search;
    static Context mContext;
    public static TextView tool_title;
    String TAG = getClass().getName();
    ActionBar actionBar;
    HockerDashboardActivity hockerDashboardActivity;
    PermissionCallback mPermissionCallback;
    Toolbar toolbar;

    public void onDeniedPermission(Object obj) {
    }

    public static void switchFragment(int i, Bundle bundle) {
        fragment = null;
        ft = fm.beginTransaction();
        if (i == 0) {
            fragment = HockerHomeFragment.getInstance(mContext);
            if (fragment != 0) {
                ft.replace(R.id.hocker_content_frame, fragment, Config.HockerHomeFragment_Tag);
            }
            ft.commit();
        }
    }

    public static void updateTitle(String str) {
        if (tool_title != null) {
            tool_title.setText(str);
        }
    }

    public static void showBackButton() {
        if (hocker_tool_barBackButton != null) {
            hocker_tool_barBackButton.setVisibility(0);
        }
    }

    public static void hideBackButton() {
        if (hocker_tool_barBackButton != null) {
            hocker_tool_barBackButton.setVisibility(8);
        }
    }

    public static void showTapToScan() {
        if (hocker_tool_search != null) {
            hocker_tool_search.setVisibility(0);
        }
    }

    public static void hideTapToScan() {
        if (hocker_tool_search != null) {
            hocker_tool_search.setVisibility(8);
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

    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_hocker);
        this.mPermissionCallback = this;
        fm = getSupportFragmentManager();
        mContext = this;
        this.hockerDashboardActivity = this;
        setActionBar();
        switchFragment(0, new Bundle());
    }

    public void checkCameraMicPermissions() {
        if (PermissionUtil.hasSelfPermission((Activity) this, new String[]{"android.permission.CAMERA"})) {
            this.mPermissionCallback.onGrantPermission(PermissionCallback.AllowCameraPermission);
            return;
        }
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.CAMERA"}, 10);
    }

    public void onGrantPermission(Object obj) {
        if (obj.toString().equals(PermissionCallback.AllowCameraPermission) != null) {
            startActivity(new Intent(mContext, QRCodeReaderActivity.class));
        }
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        if (i != 10) {
            super.onRequestPermissionsResult(i, strArr, iArr);
        } else if (PermissionUtil.verifyPermissions(iArr) != 0) {
            this.mPermissionCallback.onGrantPermission(PermissionCallback.AllowCameraPermission);
        } else {
            this.mPermissionCallback.onDeniedPermission(PermissionCallback.DeniedCameraPermission);
        }
    }

    private void setActionBar() {
        this.toolbar = (Toolbar) findViewById(R.id.hocker_tool_barId);
        setSupportActionBar(this.toolbar);
        this.actionBar = getSupportActionBar();
        this.actionBar.setDisplayShowTitleEnabled(false);
        tool_title = (TextView) findViewById(R.id.tool_title);
        hocker_tool_barBackButton = (ImageView) findViewById(R.id.hocker_tool_barBackButton);
        hocker_tool_search = (ImageView) findViewById(R.id.tool_search);
        hocker_tool_barBackButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (HockerDashboardActivity.fm != null) {
                    HockerDashboardActivity.fm.popBackStack();
                }
            }
        });
        hocker_tool_search.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                HockerDashboardActivity.this.checkCameraMicPermissions();
            }
        });
    }
}
