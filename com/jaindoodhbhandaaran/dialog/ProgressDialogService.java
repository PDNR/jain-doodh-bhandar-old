package com.jaindoodhbhandaaran.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import com.jaindoodhbhandaaran.R;

public class ProgressDialogService {
    public static final String TAG = "ProgressDialogService";
    public static ProgressDialog progress;

    public static void showProgressDialog(Context context) {
        try {
            progress = ProgressDialog.show(context, null, null);
            progress.getWindow().setBackgroundDrawableResource(17170445);
            progress.setContentView(R.layout.progress_loader);
        } catch (Context context2) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(context2.toString());
            Log.e(str, stringBuilder.toString());
        }
    }

    public static void hideProgressDialog() {
        try {
            if (progress != null) {
                progress.dismiss();
            }
        } catch (Exception e) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(e.toString());
            Log.e(str, stringBuilder.toString());
        }
    }
}
