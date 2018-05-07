package com.jaindoodhbhandaaran.retrofitapi;

import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiClient {
    public static final String BASE_URL = "http://thecoolingsolutions.com";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Builder().baseUrl(BASE_URL).addConverterFactory(ScalarsConverterFactory.create()).build();
        }
        return retrofit;
    }
}
