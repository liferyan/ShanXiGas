package com.cosw.shanxigas.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import com.cosw.shanxigas.R;

/**
 * Created by dingr on 17-1-5.
 */
public class MyAlertDialog extends Dialog implements View.OnClickListener {

  private TextView tvTitle;
  private TextView tvDialog;
  private TextView btnDialog;
  private AlertDialogCallBack mCallback;

  Context mContext;

  public MyAlertDialog(Context context) {
    super(context, R.style.alert_dialog);
    mContext = context;
  }

  public void setCallback(AlertDialogCallBack callback) {
    mCallback = callback;
  }

  /*public MyAlertDialog(Context context, int themeResId) {
    super(context, themeResId);
    mContext = context;
  }*/

  @Override
  protected void onStart() {
    super.onStart();
    getWindow().setBackgroundDrawableResource(android.R.color.transparent);
  }

  @Override
  public void show() {
    super.show();
  }

  @Override
  public void setTitle(CharSequence title) {
    tvDialog.setText(title);
  }

  @Override
  public void dismiss() {
    super.dismiss();
    if (mCallback != null) {
      mCallback.onAlertDismiss();
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.layout_dialog_alert);
    tvTitle = (TextView) findViewById(R.id.tv_dialog_title);
    tvDialog = (TextView) findViewById(R.id.tv_dialog);
    tvDialog.setMovementMethod(ScrollingMovementMethod.getInstance());
    btnDialog = (TextView) findViewById(R.id.btn_dialog);
    btnDialog.setOnClickListener(this);
    setCancelable(false);
    setCanceledOnTouchOutside(false);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_dialog:
        dismiss();
        break;
    }
  }
}
