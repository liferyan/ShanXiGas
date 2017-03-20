package com.cosw.shanxigas.hidden;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.cosw.shanxigas.R;
import com.cosw.shanxigas.base.BaseActivity;

public class HiddenActivity extends BaseActivity implements OnClickListener {

  public static final int UNBIND_SUCCESS = 1;
  public static final int UNBIND_FAILED = 0;

  private HiddenModel mModel;

  private Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case UNBIND_SUCCESS:
          hideLoading();
          showMessage("解绑成功 !");
          break;
        case UNBIND_FAILED:
          hideLoading();
          showMessage("解绑失败 !");
          break;
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.hidden_act);

    mModel = HiddenModel.getInstance();

    Button btUnbind = (Button) findViewById(R.id.bt_unbind);
    btUnbind.setOnClickListener(this);
    Button btAppLog = (Button) findViewById(R.id.bt_app_log);
    btAppLog.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.bt_unbind:
        showLoading("解绑中...");
        mModel.unBind(mHandler);
        break;
      case R.id.bt_app_log:
        Intent intent = new Intent(this, AppLogActivity.class);
        startActivity(intent);
        break;
    }
  }
}
