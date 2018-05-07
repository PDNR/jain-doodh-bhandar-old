package com.jaindoodhbhandaaran.util;

import android.content.Context;
import android.net.ConnectivityManager;

public class ConnectionDetector {
    public static boolean isNetworkAvailable(Context context) {
        context = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return (context == null || context.isConnected() == null) ? null : true;
    }
}
