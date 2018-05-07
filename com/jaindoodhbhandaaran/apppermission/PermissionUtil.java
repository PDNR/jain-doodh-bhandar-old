package com.jaindoodhbhandaaran.apppermission;

import android.app.Activity;
import android.os.Build.VERSION;

public abstract class PermissionUtil {
    public static boolean verifyPermissions(int[] iArr) {
        for (int i : iArr) {
            if (i != 0) {
                return false;
            }
        }
        return 1;
    }

    public static boolean hasSelfPermission(Activity activity, String[] strArr) {
        if (!isMNC()) {
            return true;
        }
        for (String checkSelfPermission : strArr) {
            if (activity.checkSelfPermission(checkSelfPermission) != 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasSelfPermission(Activity activity, String str) {
        boolean z = true;
        if (!isMNC()) {
            return true;
        }
        if (activity.checkSelfPermission(str) != null) {
            z = false;
        }
        return z;
    }

    public static boolean isMNC() {
        return VERSION.SDK_INT >= 23;
    }
}
