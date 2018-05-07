package com.jaindoodhbhandaaran.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import com.jaindoodhbhandaaran.R;
import com.jaindoodhbhandaaran.activity.dashboard.CustomerDashboardActivity;
import com.jaindoodhbhandaaran.activity.dashboard.HockerDashboardActivity;
import com.jaindoodhbhandaaran.util.AppPref;

public class SplashActivity extends AppCompatActivity {
    Intent I;
    Handler handler;
    Context mContext;
    int requestCode = 101;
    Runnable splashRunnable = new Runnable() {
        public void run() {
            if (AppPref.getUserId() == null || AppPref.getUserType() == null) {
                SplashActivity.this.moveToNextScreen(3);
            } else if (AppPref.getUserType().equals("Hocker")) {
                SplashActivity.this.moveToNextScreen(1);
            } else {
                SplashActivity.this.moveToNextScreen(2);
            }
        }
    };

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_splash);
        this.mContext = this;
        this.handler = new Handler();
        loadSplash();
    }

    private void moveToNextScreen(int i) {
        if (i == 1) {
            this.I = new Intent(this.mContext, HockerDashboardActivity.class);
        } else if (i == 2) {
            this.I = new Intent(this.mContext, CustomerDashboardActivity.class);
        } else if (i == 3) {
            this.I = new Intent(this.mContext, LoginActivity.class);
        }
        startActivityForResult(this.I, this.requestCode);
        finish();
    }

    private void loadSplash() {
        if (this.handler != null) {
            this.handler.postDelayed(this.splashRunnable, 2000);
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        if (this.handler != null) {
            this.handler.removeCallbacks(this.splashRunnable);
            this.handler = null;
        }
    }

    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
    }
}
