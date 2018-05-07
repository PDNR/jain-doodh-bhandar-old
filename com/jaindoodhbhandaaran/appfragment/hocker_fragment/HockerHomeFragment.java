package com.jaindoodhbhandaaran.appfragment.hocker_fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.jaindoodhbhandaaran.R;
import com.jaindoodhbhandaaran.activity.dashboard.HockerDashboardActivity;

public class HockerHomeFragment extends Fragment implements OnClickListener {
    static Fragment fragment;
    static Context mContext;
    String TAG = getClass().getName();
    TextView hocker_home_aboutUs;
    TextView hocker_home_changePassword;
    TextView hocker_home_logout;
    TextView hocker_home_payNow;
    TextView hocker_home_profile;
    TextView hocker_home_report;
    TextView hocker_home_setting;

    public static Fragment getInstance(Context context) {
        fragment = new HockerHomeFragment();
        mContext = context;
        return fragment;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
    }

    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        return layoutInflater.inflate(R.layout.fragment_hocker_home, viewGroup, false);
    }

    public void onViewCreated(View view, @Nullable Bundle bundle) {
        initView(view);
    }

    public void onActivityCreated(@Nullable Bundle bundle) {
        super.onActivityCreated(bundle);
    }

    public void onStart() {
        super.onStart();
    }

    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        super.onResume();
    }

    public void onStop() {
        super.onStop();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onDestroyView() {
        super.onDestroyView();
    }

    public void onDetach() {
        super.onDetach();
    }

    private void initView(View view) {
        HockerDashboardActivity.updateTitle(mContext.getResources().getString(R.string.dashboard));
        HockerDashboardActivity.hideBackButton();
        HockerDashboardActivity.showTapToScan();
        this.hocker_home_profile = (TextView) view.findViewById(R.id.hocker_home_profile);
        this.hocker_home_payNow = (TextView) view.findViewById(R.id.hocker_home_payNow);
        this.hocker_home_report = (TextView) view.findViewById(R.id.hocker_home_report);
        this.hocker_home_changePassword = (TextView) view.findViewById(R.id.hocker_home_changePassword);
        this.hocker_home_setting = (TextView) view.findViewById(R.id.hocker_home_setting);
        this.hocker_home_aboutUs = (TextView) view.findViewById(R.id.hocker_home_aboutUs);
        this.hocker_home_logout = (TextView) view.findViewById(R.id.hocker_home_logout);
        this.hocker_home_profile.setOnClickListener(this);
        this.hocker_home_payNow.setOnClickListener(this);
        this.hocker_home_report.setOnClickListener(this);
        this.hocker_home_changePassword.setOnClickListener(this);
        this.hocker_home_setting.setOnClickListener(this);
        this.hocker_home_aboutUs.setOnClickListener(this);
        this.hocker_home_logout.setOnClickListener(this);
    }

    private void showLogoutDialog() {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(1);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.logout_dialog);
        Button button = (Button) dialog.findViewById(R.id.logout_done);
        ((Button) dialog.findViewById(R.id.logout_cancel)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                HockerDashboardActivity.logOut();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.hocker_home_aboutUs:
                return;
            case R.id.hocker_home_changePassword:
                view = new Bundle();
                return;
            case R.id.hocker_home_logout:
                showLogoutDialog();
                return;
            case R.id.hocker_home_payNow:
                view = new Bundle();
                return;
            case R.id.hocker_home_profile:
                view = new Bundle();
                return;
            case R.id.hocker_home_report:
                view = new Bundle();
                return;
            case R.id.hocker_home_setting:
                view = new Bundle();
                return;
            default:
                return;
        }
    }
}
