package com.jaindoodhbhandaaran.apppermission;

public interface PermissionCallback {
    public static final String AllowCameraPermission = "AllowCamera";
    public static final String DeniedCameraPermission = "DeniedCamera";

    void onDeniedPermission(Object obj);

    void onGrantPermission(Object obj);
}
