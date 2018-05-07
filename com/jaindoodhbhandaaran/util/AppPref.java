package com.jaindoodhbhandaaran.util;

import android.content.SharedPreferences.Editor;
import com.jaindoodhbhandaaran.app.MyApplication;
import com.jaindoodhbhandaaran.model.UserModel;

public class AppPref {
    private static final String SaveUser = "SaveUser";
    private static final String UserId_KEY = "id";
    private static final String VerifyStatus_KEY = "type";

    public static void saveUser(UserModel userModel) {
        Editor edit = MyApplication.getContext().getSharedPreferences(SaveUser, 0).edit();
        edit.putString(UserId_KEY, userModel.getId());
        edit.putString(VerifyStatus_KEY, userModel.getType());
        edit.commit();
    }

    public static String getUserId() {
        return MyApplication.getContext().getSharedPreferences(SaveUser, 0).getString(UserId_KEY, null);
    }

    public static String getUserType() {
        return MyApplication.getContext().getSharedPreferences(SaveUser, 0).getString(VerifyStatus_KEY, null);
    }

    public static void clearSession() {
        MyApplication.getContext().getSharedPreferences(SaveUser, 0).edit().clear().commit();
    }
}
