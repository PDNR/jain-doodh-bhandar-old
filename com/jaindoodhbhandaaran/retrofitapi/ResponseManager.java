package com.jaindoodhbhandaaran.retrofitapi;

import com.jaindoodhbhandaaran.model.ResponseModel;
import com.jaindoodhbhandaaran.retrofitapi.apilistener.UsersListener;
import com.jaindoodhbhandaaran.retrofitapi.apilistener.UtilResponseListener;

public class ResponseManager {
    public static final String Error = "error";
    public static final String Failed = "Failed";
    public static final String Success = "Success";
    JsonHandler jsonHandler = new JsonHandler();

    public void UsersResponse(String str, UsersListener usersListener) {
        ResponseModel response = this.jsonHandler.getResponse(str);
        if (response == null) {
            usersListener.onFailure(str);
        } else if (response.getStatus().equals(Success) != null) {
            usersListener.onSuccess(this.jsonHandler.getUserResult(response.getData()));
        } else if (response.getStatus().equals(Error) != null) {
            usersListener.onFailure(response);
        }
    }

    public void ScannerDetailsResponse(String str, UtilResponseListener utilResponseListener, String str2) {
        ResponseModel response = this.jsonHandler.getResponse(str);
        if (response == null) {
            utilResponseListener.onFailure(str);
        } else if (response.getStatus().equals(Success) != null) {
            utilResponseListener.onSuccessResult(str2, this.jsonHandler.getHockerScan(response.getData()));
        } else if (response.getStatus().equals(Error) != null) {
            utilResponseListener.onFailure(response);
        } else if (response.getStatus().equals(Failed) != null) {
            utilResponseListener.onFailure(response);
        }
    }

    public void addMilkQty(String str, UtilResponseListener utilResponseListener, String str2) {
        ResponseModel response = this.jsonHandler.getResponse(str);
        if (response == null) {
            utilResponseListener.onFailure(str);
        } else if (response.getStatus().equals(Success) != null) {
            utilResponseListener.onSuccessResult(str2, response.getMessage());
        } else if (response.getStatus().equals(Error) != null) {
            utilResponseListener.onFailure(response);
        }
    }
}
