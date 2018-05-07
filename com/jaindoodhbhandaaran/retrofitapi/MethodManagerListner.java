package com.jaindoodhbhandaaran.retrofitapi;

public interface MethodManagerListner {
    void onError(int i, String str);

    void onError(String str);

    void onSuccess(String str, String str2);
}
