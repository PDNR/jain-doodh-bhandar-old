package com.jaindoodhbhandaaran.app;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    private static Context sContext;

    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }
}
