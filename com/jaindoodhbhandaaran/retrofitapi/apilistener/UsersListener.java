package com.jaindoodhbhandaaran.retrofitapi.apilistener;

import com.jaindoodhbhandaaran.model.ResponseModel;
import com.jaindoodhbhandaaran.model.UserModel;

public interface UsersListener {
    void onFailure(ResponseModel responseModel);

    void onFailure(String str);

    void onSuccess(UserModel userModel);
}
