package com.jaindoodhbhandaaran.appfragment.customerfragment;

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
import com.jaindoodhbhandaaran.activity.dashboard.CustomerDashboardActivity;

public class HomeFragment extends Fragment {
    private static Fragment fragment;
    private static Context mContext;
    String TAG = getClass().getName();
    OnClickListener addAboutUsListener = new OnClickListener() {
        public void onClick(View view) {
        }
    };
    OnClickListener addAccountListener = new OnClickListener() {
        public void onClick(View view) {
            view = new Bundle();
        }
    };
    OnClickListener addChangePasswordListener = new OnClickListener() {
        public void onClick(View view) {
            view = new Bundle();
        }
    };
    OnClickListener addLogoutListener = new OnClickListener() {
        public void onClick(View view) {
            HomeFragment.this.showLogoutDialog();
        }
    };
    OnClickListener addSettingListener = new OnClickListener() {
        public void onClick(View view) {
            view = new Bundle();
        }
    };
    TextView home_aboutUS;
    TextView home_account;
    TextView home_changePassword;
    TextView home_logout;
    TextView home_profile;
    TextView home_setting;
    OnClickListener viewProfileListener = new OnClickListener() {
        public void onClick(View view) {
            view = new Bundle();
        }
    };

    public static Fragment getInstance(Context context) {
        fragment = new HomeFragment();
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
        return layoutInflater.inflate(R.layout.fragment_home, viewGroup, false);
    }

    public void onViewCreated(View view, @Nullable Bundle bundle) {
        this.home_account = (TextView) view.findViewById(R.id.home_account);
        this.home_profile = (TextView) view.findViewById(R.id.home_profile);
        this.home_setting = (TextView) view.findViewById(R.id.home_setting);
        this.home_changePassword = (TextView) view.findViewById(R.id.home_changePassword);
        this.home_aboutUS = (TextView) view.findViewById(R.id.home_aboutUS);
        this.home_logout = (TextView) view.findViewById(R.id.home_logout);
        this.home_account.setOnClickListener(this.addAccountListener);
        this.home_profile.setOnClickListener(this.viewProfileListener);
        this.home_setting.setOnClickListener(this.addSettingListener);
        this.home_changePassword.setOnClickListener(this.addChangePasswordListener);
        this.home_aboutUS.setOnClickListener(this.addAboutUsListener);
        this.home_logout.setOnClickListener(this.addLogoutListener);
        CustomerDashboardActivity.updateTitle(mContext.getResources().getString(R.string.dashboard));
        CustomerDashboardActivity.showSearchButton();
    }

    public void onActivityCreated(@Nullable Bundle bundle) {
        super.onActivityCreated(bundle);
    }

    public void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    public void onStop() {
        super.onStop();
    }

    public void onDestroyView() {
        super.onDestroyView();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onDetach() {
        super.onDetach();
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
                CustomerDashboardActivity.logOut();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
