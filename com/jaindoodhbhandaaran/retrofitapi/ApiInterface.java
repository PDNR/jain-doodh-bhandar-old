package com.jaindoodhbhandaaran.retrofitapi;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {
    @POST("/milk_product/index.php/api/user/hocker_scan")
    @Headers({"Content-Type: application/json"})
    Call<String> doHockerScan(@Body String str);

    @POST("/milk_product/index.php/api/user/signin")
    @Headers({"Content-Type: application/json"})
    Call<String> doLogin(@Body String str);

    @POST("/milk_product/index.php/api/user/milk_entry")
    @Headers({"Content-Type: application/json"})
    Call<String> doMilkEntry(@Body String str);
}
