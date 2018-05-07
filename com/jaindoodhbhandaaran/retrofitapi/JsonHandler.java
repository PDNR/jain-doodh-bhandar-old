package com.jaindoodhbhandaaran.retrofitapi;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jaindoodhbhandaaran.model.ResponseModel;
import com.jaindoodhbhandaaran.model.ScannerDetailsModel;
import com.jaindoodhbhandaaran.model.UserModel;

public class JsonHandler {
    public static final String TAG = "JsonHandler";
    Gson gson = this.gsonBuilder.create();
    GsonBuilder gsonBuilder = new GsonBuilder();

    public String toJsonResult(Object obj) {
        return this.gson.toJson(obj);
    }

    public ResponseModel getResponse(String str) {
        try {
            return (ResponseModel) this.gson.fromJson(str, ResponseModel.class);
        } catch (String str2) {
            String str3 = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(str2.toString());
            Log.d(str3, stringBuilder.toString());
            return null;
        }
    }

    public UserModel getUserResult(Object obj) {
        try {
            return (UserModel) this.gson.fromJson(toJsonResult(obj), UserModel.class);
        } catch (Object obj2) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(obj2.toString());
            Log.d(str, stringBuilder.toString());
            return null;
        }
    }

    public ScannerDetailsModel getHockerScan(Object obj) {
        try {
            return (ScannerDetailsModel) this.gson.fromJson(toJsonResult(obj), ScannerDetailsModel.class);
        } catch (Object obj2) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(obj2.toString());
            Log.d(str, stringBuilder.toString());
            return null;
        }
    }
}
