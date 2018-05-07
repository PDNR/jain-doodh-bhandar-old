package com.jaindoodhbhandaaran.util;

import android.content.Context;
import android.provider.Settings.Secure;

public class DeviceInformation {
    public static String deviceType = "1";

    public static String getDeviceId(Context context) {
        return Secure.getString(context.getContentResolver(), "android_id");
    }
}
