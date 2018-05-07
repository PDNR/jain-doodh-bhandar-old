package com.jaindoodhbhandaaran.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.jaindoodhbhandaaran.R;

public class MessageDialog extends Dialog {
    public String message;
    public okOnClickListener okListener;

    public interface okOnClickListener {
        void onButtonClick();
    }

    public MessageDialog(Context context, int i, String str, okOnClickListener com_jaindoodhbhandaaran_dialog_MessageDialog_okOnClickListener) {
        super(context, i);
        this.okListener = com_jaindoodhbhandaaran_dialog_MessageDialog_okOnClickListener;
        this.message = str;
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        setContentView(R.layout.message_dialog_xml);
        TextView textView = (TextView) findViewById(R.id.message_dialog_accept);
        ((TextView) findViewById(R.id.message_dialog_message)).setText(this.message);
        textView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MessageDialog.this.okListener.onButtonClick();
            }
        });
    }
}
