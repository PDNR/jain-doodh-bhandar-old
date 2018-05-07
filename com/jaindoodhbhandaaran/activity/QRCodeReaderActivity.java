package com.jaindoodhbhandaaran.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import com.jaindoodhbhandaaran.R;
import com.jaindoodhbhandaaran.applistener.DialogListener;
import com.jaindoodhbhandaaran.dialog.AddMilkDialog;
import com.jaindoodhbhandaaran.dialog.MessageDialog;
import com.jaindoodhbhandaaran.dialog.MessageDialog.okOnClickListener;
import com.jaindoodhbhandaaran.dialog.ProgressDialogService;
import com.jaindoodhbhandaaran.model.ResponseModel;
import com.jaindoodhbhandaaran.model.ScannerDetailsModel;
import com.jaindoodhbhandaaran.retrofitapi.APIController;
import com.jaindoodhbhandaaran.retrofitapi.MethodManagerListner;
import com.jaindoodhbhandaaran.retrofitapi.ResponseManager;
import com.jaindoodhbhandaaran.retrofitapi.apilistener.ApisConfig;
import com.jaindoodhbhandaaran.retrofitapi.apilistener.UtilResponseListener;
import com.jaindoodhbhandaaran.util.AppPref;
import java.util.Map;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import me.dm7.barcodescanner.zbar.ZBarScannerView.ResultHandler;
import org.json.JSONObject;

public class QRCodeReaderActivity extends AppCompatActivity implements ResultHandler, MethodManagerListner, UtilResponseListener, OnClickListener, DialogListener {
    String TAG = getClass().getName();
    AddMilkDialog addMilkDialog;
    APIController apiController;
    FrameLayout cameraPreview;
    DialogListener dialogListener;
    Context mContext;
    private ZBarScannerView mScannerView;
    MessageDialog messageDialog;
    MethodManagerListner methodManagerListner;
    ResponseManager responseManager;
    UtilResponseListener utilResponseListener;

    public void onClick(View view) {
    }

    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_qrcode_reader);
        this.mContext = this;
        this.methodManagerListner = this;
        this.utilResponseListener = this;
        this.dialogListener = this;
        this.apiController = new APIController();
        this.responseManager = new ResponseManager();
        initView();
    }

    protected void onResume() {
        super.onResume();
        this.mScannerView.setResultHandler(this);
        this.mScannerView.startCamera();
    }

    protected void onPause() {
        super.onPause();
        this.mScannerView.stopCamera();
    }

    private void initView() {
        this.cameraPreview = (FrameLayout) findViewById(R.id.cameraPreview);
        this.mScannerView = new ZBarScannerView(this.mContext);
        this.cameraPreview.addView(this.mScannerView);
    }

    private void showCustomerInfo(ScannerDetailsModel scannerDetailsModel) {
        addMilkDialog(scannerDetailsModel);
    }

    private void addMilkDialog(ScannerDetailsModel scannerDetailsModel) {
        this.addMilkDialog = new AddMilkDialog(this.mContext, R.style.DialogTheme, scannerDetailsModel, this.dialogListener);
        this.addMilkDialog.show();
    }

    private void dismissMilkDialog() {
        if (this.addMilkDialog != null) {
            this.addMilkDialog.dismiss();
        }
    }

    private void showErrorDialog(String str) {
        this.messageDialog = new MessageDialog(this.mContext, 16973939, str, new okOnClickListener() {
            public void onButtonClick() {
                QRCodeReaderActivity.this.messageDialog.dismiss();
                QRCodeReaderActivity.this.finish();
            }
        });
        this.messageDialog.setCancelable(false);
        this.messageDialog.setCanceledOnTouchOutside(false);
        this.messageDialog.show();
    }

    private void callHockerDetails(String str) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(ApisConfig.CustomerId_Key, str);
            ProgressDialogService.showProgressDialog(this.mContext);
            this.apiController.doHockerScan(jSONObject, this.methodManagerListner, ApisConfig.HockerScanToggle);
        } catch (String str2) {
            String str3 = this.TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(str2.toString());
            Log.e(str3, stringBuilder.toString());
            ProgressDialogService.hideProgressDialog();
        }
    }

    private void callMilk_Entry(Map<String, String> map) {
        JSONObject jSONObject = new JSONObject();
        try {
            if (AppPref.getUserId() != null) {
                jSONObject.put(ApisConfig.HockerId_Key, AppPref.getUserId());
                jSONObject.put(ApisConfig.CustomerId_Key, map.get(ApisConfig.CustomerId_Key));
                jSONObject.put(ApisConfig.MilkQty_Key, map.get(ApisConfig.MilkQty_Key));
                jSONObject.put(ApisConfig.Price_Key, map.get(ApisConfig.Price_Key));
                ProgressDialogService.showProgressDialog(this.mContext);
                this.apiController.doMilkEntry(jSONObject, this.methodManagerListner, ApisConfig.MilkEnteryToggle);
            }
        } catch (Map<String, String> map2) {
            String str = this.TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(map2.toString());
            Log.e(str, stringBuilder.toString());
            ProgressDialogService.hideProgressDialog();
        }
    }

    public void handleResult(Result result) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                QRCodeReaderActivity.this.mScannerView.resumeCameraPreview(QRCodeReaderActivity.this);
            }
        }, 2000);
        callHockerDetails(result.getContents());
    }

    public void onError(String str) {
        ProgressDialogService.hideProgressDialog();
        showErrorDialog(str);
    }

    public void onError(int i, String str) {
        ProgressDialogService.hideProgressDialog();
        showErrorDialog(str);
    }

    public void onSuccess(String str, String str2) {
        if (str2.equals(ApisConfig.HockerScanToggle)) {
            this.responseManager.ScannerDetailsResponse(str, this.utilResponseListener, str2);
        }
        if (str2.equals(ApisConfig.MilkEnteryToggle)) {
            this.responseManager.addMilkQty(str, this.utilResponseListener, str2);
        }
    }

    public void onFailure(String str) {
        ProgressDialogService.hideProgressDialog();
        showErrorDialog(str);
    }

    public void onFailure(ResponseModel responseModel) {
        ProgressDialogService.hideProgressDialog();
        showErrorDialog(responseModel.getMessage());
    }

    public void onSuccessResult(String str, Object obj) {
        ProgressDialogService.hideProgressDialog();
        if (str.equals(ApisConfig.HockerScanToggle)) {
            showCustomerInfo((ScannerDetailsModel) obj);
        }
        if (str.equals(ApisConfig.MilkEnteryToggle) != null) {
            showErrorDialog((String) obj);
        }
    }

    public void successDialog(Object obj) {
        dismissMilkDialog();
        Map map = (Map) obj;
        if (map != null && map.containsKey(ApisConfig.MilkQty_Key) && map.containsKey(ApisConfig.Price_Key)) {
            callMilk_Entry(map);
        }
    }

    public void cancelDialog(Object obj) {
        dismissMilkDialog();
    }
}
