package com.jaindoodhbhandaaran.retrofitapi;

import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class APIController {
    public static final String TAG = "APIController";
    ApiInterface apiInterface = ((ApiInterface) ApiClient.getClient().create(ApiInterface.class));

    public void doLogin(JSONObject jSONObject, final MethodManagerListner methodManagerListner, final String str) {
        this.apiInterface.doLogin(jSONObject.toString()).enqueue(new Callback<String>() {
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() != null) {
                    methodManagerListner.onSuccess(((String) response.body()).toString(), str);
                    return;
                }
                methodManagerListner.onError(response.code(), response.message());
            }

            public void onFailure(Call<String> call, Throwable th) {
                methodManagerListner.onError(th.getMessage());
            }
        });
    }

    public void doHockerScan(JSONObject jSONObject, final MethodManagerListner methodManagerListner, final String str) {
        this.apiInterface.doHockerScan(jSONObject.toString()).enqueue(new Callback<String>() {
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() != null) {
                    methodManagerListner.onSuccess(((String) response.body()).toString(), str);
                    return;
                }
                methodManagerListner.onError(response.code(), response.message());
            }

            public void onFailure(Call<String> call, Throwable th) {
                methodManagerListner.onError(th.getMessage());
            }
        });
    }

    public void doMilkEntry(JSONObject jSONObject, final MethodManagerListner methodManagerListner, final String str) {
        this.apiInterface.doMilkEntry(jSONObject.toString()).enqueue(new Callback<String>() {
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() != null) {
                    methodManagerListner.onSuccess(((String) response.body()).toString(), str);
                    return;
                }
                methodManagerListner.onError(response.code(), response.message());
            }

            public void onFailure(Call<String> call, Throwable th) {
                methodManagerListner.onError(th.getMessage());
            }
        });
    }
}
