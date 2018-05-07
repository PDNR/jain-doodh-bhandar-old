package com.jaindoodhbhandaaran.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jaindoodhbhandaaran.R;
import com.jaindoodhbhandaaran.app.MyApplication;
import com.jaindoodhbhandaaran.applistener.DialogListener;
import com.jaindoodhbhandaaran.model.ScannerDetailsModel;
import com.jaindoodhbhandaaran.retrofitapi.apilistener.ApisConfig;
import com.jaindoodhbhandaaran.util.validation.ValidationService;
import java.util.HashMap;
import java.util.Map;

public class AddMilkDialog extends Dialog implements OnClickListener {
    private Button addMilk_addBtn;
    private LinearLayout addMilk_linearLayout;
    private EditText add_milkQty;
    private EditText customer_UIID;
    private EditText customer_address;
    private Button customer_cancelBtn;
    private EditText customer_milkQty;
    private EditText customer_name;
    private EditText customer_phone;
    private EditText customer_price;
    private EditText customer_work;
    private DialogListener dialogListener;
    private TextView dialog_addMilkBtn;
    private ScannerDetailsModel model;

    public AddMilkDialog(@NonNull Context context, int i, ScannerDetailsModel scannerDetailsModel, DialogListener dialogListener) {
        super(context, i);
        this.model = scannerDetailsModel;
        this.dialogListener = dialogListener;
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        setContentView(R.layout.dialog_addmilk);
        initView();
    }

    private void initView() {
        this.dialog_addMilkBtn = (TextView) findViewById(R.id.dialog_addMilkBtn);
        this.addMilk_linearLayout = (LinearLayout) findViewById(R.id.addMilk_linearLayout);
        this.customer_name = (EditText) findViewById(R.id.customer_name);
        this.customer_phone = (EditText) findViewById(R.id.customer_phone);
        this.customer_UIID = (EditText) findViewById(R.id.customer_UIID);
        this.customer_work = (EditText) findViewById(R.id.customer_work);
        this.customer_address = (EditText) findViewById(R.id.customer_address);
        this.customer_milkQty = (EditText) findViewById(R.id.customer_milkQty);
        this.add_milkQty = (EditText) findViewById(R.id.add_milkQty);
        this.addMilk_addBtn = (Button) findViewById(R.id.addMilk_addBtn);
        this.customer_cancelBtn = (Button) findViewById(R.id.customer_cancelBtn);
        this.customer_price = (EditText) findViewById(R.id.customer_price);
        this.dialog_addMilkBtn.setOnClickListener(this);
        this.addMilk_addBtn.setOnClickListener(this);
        this.customer_cancelBtn.setOnClickListener(this);
        this.customer_name.setText(this.model.getName());
        this.customer_phone.setText(this.model.getPhone());
        this.customer_UIID.setText(this.model.getAadhar());
        this.customer_work.setText(this.model.getWork());
        this.customer_address.setText(this.model.getAddress());
        this.customer_milkQty.setText(this.model.getMilk_quality());
    }

    private void errorbackground(EditText editText, String str) {
        int color;
        if (VERSION.SDK_INT >= 23) {
            color = ContextCompat.getColor(MyApplication.getContext(), R.color.white);
        } else {
            color = MyApplication.getContext().getResources().getColor(R.color.white);
        }
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(color);
        CharSequence spannableStringBuilder = new SpannableStringBuilder(str);
        spannableStringBuilder.setSpan(foregroundColorSpan, 0, str.length(), 0);
        editText.setError(spannableStringBuilder);
    }

    private void addMilk() {
        if (ValidationService.isStringEmpty(this.customer_price.getText().toString())) {
            errorbackground(this.customer_price, MyApplication.getContext().getResources().getString(R.string.empty_milk_price));
        } else if (!ValidationService.isStringEmpty(this.model.getId())) {
            Map hashMap = new HashMap();
            hashMap.put(ApisConfig.MilkQty_Key, this.add_milkQty.getText().toString());
            hashMap.put(ApisConfig.Price_Key, this.customer_price.getText().toString());
            hashMap.put(ApisConfig.CustomerId_Key, this.model.getId());
            this.dialogListener.successDialog(hashMap);
        }
    }

    public void onClick(View view) {
        view = view.getId();
        if (view == R.id.addMilk_addBtn) {
            addMilk();
        } else if (view == R.id.customer_cancelBtn) {
            this.dialogListener.cancelDialog(null);
        } else if (view == R.id.dialog_addMilkBtn) {
            if (this.addMilk_linearLayout.getVisibility() == null) {
                this.addMilk_linearLayout.setVisibility(8);
                this.dialog_addMilkBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.drop_down, 0);
                return;
            }
            this.addMilk_linearLayout.setVisibility(0);
            this.dialog_addMilkBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.up_arrow, 0);
        }
    }
}
