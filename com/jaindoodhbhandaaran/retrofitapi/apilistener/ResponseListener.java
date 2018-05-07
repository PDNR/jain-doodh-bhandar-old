package com.jaindoodhbhandaaran.retrofitapi.apilistener;

import com.jaindoodhbhandaaran.model.ResponseModel;

public interface ResponseListener<T> {
    void onFailure(int i, String str, ResponseModel responseModel);

    void onSuccess(ResponseModel responseModel, T t);
}
