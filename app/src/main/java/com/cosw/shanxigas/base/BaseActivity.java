package com.cosw.shanxigas.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.cosw.shanxigas.util.LogUtils;
import com.cosw.shanxigas.widget.AlertDialogCallBack;
import com.cosw.shanxigas.widget.MyAlertDialog;
import com.cosw.shanxigas.widget.MyProgressDialog;

/**
 * Created by Ryan on 2017/1/16.
 */

public class BaseActivity extends AppCompatActivity {

  private static final String TAG = "BaseActivity";

  private MyAlertDialog alertDialog;
  private ProgressDialog loadingDialog;

  private Context mContext;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mContext = this;
    initViews();
  }

  @Override
  protected void onDestroy() {
    try {
      if (alertDialog != null && alertDialog.isShowing()) {
        alertDialog.dismiss();
      }
      if (loadingDialog != null && loadingDialog.isShowing()) {
        loadingDialog.dismiss();
      }
    } catch (Exception e) {
      LogUtils.e(TAG, "onDestroy", e);
    }
    super.onDestroy();
  }

  private void initViews() {
    alertDialog = new MyAlertDialog(mContext, true);
    loadingDialog = new MyProgressDialog(mContext);
  }

  public void showLoading(String title) {
    loadingDialog.show();
    loadingDialog.setTitle(title);
  }

  public void hideLoading() {
    if (loadingDialog.isShowing()) {
      loadingDialog.dismiss();
    }
  }

  public void showMessage(String message) {
    alertDialog.show();
    alertDialog.setTitle(message);
  }

  public void showMessage(String message, AlertDialogCallBack callBack) {
    alertDialog.setCallback(callBack);
    alertDialog.show();
    alertDialog.setTitle(message);
  }
}
