package com.cosw.shanxigas.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import com.cosw.shanxigas.R;

/**
 * Created by Ryan on 2016/12/29.
 */

public class MyProgressDialog extends ProgressDialog {

  private TextView tvLoadDialog;

  public MyProgressDialog(Context context) {
    super(context, R.style.loading_dialog);
  }

  public MyProgressDialog(Context context, int theme) {
    super(context, theme);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    init(getContext());
  }

  @Override
  public void setTitle(CharSequence title) {
    tvLoadDialog.setText(title);
  }

  private void init(Context context) {
    //设置不可取消，点击其他区域不能取消，实际中可以抽出去封装供外包设置
    setCancelable(false);
    setCanceledOnTouchOutside(false);

    setContentView(R.layout.layout_dialog_loading);
    WindowManager.LayoutParams params = getWindow().getAttributes();
    params.width = WindowManager.LayoutParams.WRAP_CONTENT;
    params.height = WindowManager.LayoutParams.WRAP_CONTENT;
    getWindow().setAttributes(params);
    tvLoadDialog = (TextView) findViewById(R.id.tv_load_dialog);
  }

  @Override
  public void show() {
    super.show();
  }
}
