package com.cosw.shanxigas.widget;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.cosw.shanxigas.R;

/**
 * Created by Ryan on 2017/8/1.
 */

public class CustomDialog extends Dialog {

  private OnDialogClickListener mListener;

  public final EditText input;

  public void setOnDialogClickListener(OnDialogClickListener listener) {
    mListener = listener;
  }

  public void setPhone(String phone) {
    input.setText(phone);
    input.setSelection(phone.length());
  }

  public CustomDialog(final Context context) {
    super(context);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.dialog_layout);

    TextView ok = (TextView) findViewById(R.id.btn_ok);
    TextView cancel = (TextView) findViewById(R.id.btn_cancel);
    input = (EditText) findViewById(R.id.edt_input);

    ok.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String phone = input.getText().toString().trim();
        if (TextUtils.isEmpty(phone) || phone.length() != 11) {
          Toast.makeText(context, "请输入合法手机号！", Toast.LENGTH_SHORT).show();
          return;
        }
        dismiss();
        if (mListener != null) {
          mListener.onSuccessInput(phone);
        }
      }
    });

    cancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
        if (mListener != null) {
          mListener.onFailInput();
        }
      }
    });

    setCanceledOnTouchOutside(false);
  }
}
