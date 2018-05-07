package com.jaindoodhbhandaaran.retrofitapi.apilistener;

import com.jaindoodhbhandaaran.model.ResponseModel;

public interface UtilResponseListener {
    void onFailure(ResponseModel responseModel);

    void onFailure(String str);

    void onSuccessResult(String str, Object obj);
}
