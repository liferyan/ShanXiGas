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

  private TextView tvDialog;
  private TextView btnPositive;
  private TextView btnNegative;
  private AlertDialogCallBack mCallback;

  private boolean onlyPositive;

  Context mContext;

  public MyAlertDialog(Context context, boolean onlyPositive) {
    super(context, R.style.alert_dialog);
    mContext = context;
    this.onlyPositive = onlyPositive;
  }

  public void setCallback(AlertDialogCallBack callback) {
    mCallback = callback;
  }

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

  private void positive() {
    super.dismiss();
    if (mCallback != null) {
      mCallback.onPositive();
    }
  }

  private void negative() {
    super.dismiss();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.layout_dialog_alert);
    tvDialog = (TextView) findViewById(R.id.tv_dialog);
    tvDialog.setMovementMethod(ScrollingMovementMethod.getInstance());
    btnNegative = (TextView) findViewById(R.id.btn_negative);
    btnNegative.setOnClickListener(this);
    btnPositive = (TextView) findViewById(R.id.btn_positive);
    btnPositive.setOnClickListener(this);
    if (onlyPositive) {
      btnNegative.setVisibility(View.GONE);
    }
    setCancelable(false);
    setCanceledOnTouchOutside(false);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_positive:
        positive();
        break;
      case R.id.btn_negative:
        negative();
        break;
    }
  }


}
