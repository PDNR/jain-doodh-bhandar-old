package com.jaindoodhbhandaaran.retrofitapi.apilistener;

import com.jaindoodhbhandaaran.model.ResponseModel;

public interface UtilMessageListener {
    void onFailure(ResponseModel responseModel);

    void onUtilMessage(int i, String str, String str2);
}
