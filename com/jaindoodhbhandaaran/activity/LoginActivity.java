package com.jaindoodhbhandaaran.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.jaindoodhbhandaaran.R;
import com.jaindoodhbhandaaran.activity.dashboard.CustomerDashboardActivity;
import com.jaindoodhbhandaaran.activity.dashboard.HockerDashboardActivity;
import com.jaindoodhbhandaaran.app.MyApplication;
import com.jaindoodhbhandaaran.dialog.MessageDialog;
import com.jaindoodhbhandaaran.dialog.MessageDialog.okOnClickListener;
import com.jaindoodhbhandaaran.dialog.ProgressDialogService;
import com.jaindoodhbhandaaran.model.ResponseModel;
import com.jaindoodhbhandaaran.model.UserModel;
import com.jaindoodhbhandaaran.retrofitapi.APIController;
import com.jaindoodhbhandaaran.retrofitapi.MethodManagerListner;
import com.jaindoodhbhandaaran.retrofitapi.ResponseManager;
import com.jaindoodhbhandaaran.retrofitapi.apilistener.ApisConfig;
import com.jaindoodhbhandaaran.retrofitapi.apilistener.UsersListener;
import com.jaindoodhbhandaaran.util.AppPref;
import com.jaindoodhbhandaaran.util.validation.ValidationService;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements OnClickListener, MethodManagerListner, UsersListener {
    String TAG = getClass().getName();
    APIController apiController;
    Intent intent;
    Button login_LogIn;
    TextView login_forgotPassword;
    EditText login_password;
    EditText login_phoneNumber;
    Context mContext;
    MessageDialog messageDialog;
    MethodManagerListner methodManagerListner;
    ResponseManager responseManager;
    UsersListener usersListener;

    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_login);
        this.mContext = this;
        this.methodManagerListner = this;
        this.usersListener = this;
        this.apiController = new APIController();
        this.responseManager = new ResponseManager();
        this.login_LogIn = (Button) findViewById(R.id.login_LogIn);
        this.login_phoneNumber = (EditText) findViewById(R.id.login_phoneNumber);
        this.login_password = (EditText) findViewById(R.id.login_password);
        this.login_forgotPassword = (TextView) findViewById(R.id.login_forgotPassword);
        this.login_LogIn.setOnClickListener(this);
    }

    private void errorbackground(EditText editText, String str) {
        int color;
        if (VERSION.SDK_INT >= 23) {
            color = ContextCompat.getColor(getApplicationContext(), R.color.white);
        } else {
            color = getResources().getColor(R.color.white);
        }
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(color);
        CharSequence spannableStringBuilder = new SpannableStringBuilder(str);
        spannableStringBuilder.setSpan(foregroundColorSpan, 0, str.length(), 0);
        editText.setError(spannableStringBuilder);
    }

    private void callLogin() {
        JSONObject jSONObject = new JSONObject();
        try {
            if (ValidationService.isValidPhone(this.login_phoneNumber.getText().toString()) && this.login_password.getText().toString().equals("")) {
                errorbackground(this.login_phoneNumber, MyApplication.getContext().getResources().getString(R.string.empty_mobile_number));
                errorbackground(this.login_password, MyApplication.getContext().getResources().getString(R.string.empty_password));
            } else if (ValidationService.isValidPhone(this.login_phoneNumber.getText().toString())) {
                errorbackground(this.login_phoneNumber, MyApplication.getContext().getResources().getString(R.string.empty_mobile_number));
            } else if (this.login_password.getText().toString().equals("")) {
                errorbackground(this.login_password, MyApplication.getContext().getResources().getString(R.string.empty_password));
            } else {
                jSONObject.put(ApisConfig.Mobile_Key, this.login_phoneNumber.getText().toString());
                jSONObject.put(ApisConfig.Password_Key, this.login_password.getText().toString());
                ProgressDialogService.showProgressDialog(this.mContext);
                this.apiController.doLogin(jSONObject, this.methodManagerListner, ApisConfig.LoginToggle);
            }
        } catch (JSONException e) {
            String str = this.TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(e.toString());
            Log.d(str, stringBuilder.toString());
        }
    }

    private void moveToActivity(int i) {
        if (i == 1) {
            this.intent = new Intent(this.mContext, HockerDashboardActivity.class);
        }
        if (i == 2) {
            this.intent = new Intent(this.mContext, CustomerDashboardActivity.class);
        }
        startActivity(this.intent);
        finish();
    }

    private void showErrorDialog(String str) {
        this.messageDialog = new MessageDialog(this.mContext, 16973939, str, new okOnClickListener() {
            public void onButtonClick() {
                LoginActivity.this.messageDialog.dismiss();
            }
        });
        this.messageDialog.setCancelable(false);
        this.messageDialog.setCanceledOnTouchOutside(false);
        this.messageDialog.show();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.login_LogIn) {
            callLogin();
        }
    }

    public void onError(String str) {
        ProgressDialogService.hideProgressDialog();
    }

    public void onError(int i, String str) {
        ProgressDialogService.hideProgressDialog();
    }

    public void onSuccess(String str, String str2) {
        if (this.responseManager != null) {
            this.responseManager.UsersResponse(str, this.usersListener);
        }
    }

    public void onSuccess(UserModel userModel) {
        ProgressDialogService.hideProgressDialog();
        if (!ValidationService.isStringEmpty(userModel.getType())) {
            AppPref.saveUser(userModel);
            if (userModel.getType().equals("Customer")) {
                moveToActivity(2);
            } else if (userModel.getType().equals("Hocker") != null) {
                moveToActivity(1);
            }
        }
    }

    public void onFailure(ResponseModel responseModel) {
        ProgressDialogService.hideProgressDialog();
        showErrorDialog(responseModel.getMessage());
    }

    public void onFailure(String str) {
        ProgressDialogService.hideProgressDialog();
        showErrorDialog(str);
    }
}
