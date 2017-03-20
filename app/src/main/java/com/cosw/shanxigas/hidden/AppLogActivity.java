package com.cosw.shanxigas.hidden;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ScrollView;
import android.widget.TextView;
import com.cosw.shanxigas.R;
import com.cosw.shanxigas.base.BaseActivity;

public class AppLogActivity extends BaseActivity {

  public static final int GET_LOG_SUCCESS = 1;
  public static final int GET_LOG_FAILED = 0;

  private HiddenModel mModel;

  private TextView logTxt;
  private ScrollView scrollView;

  private Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case GET_LOG_SUCCESS:
          hideLoading();
          logTxt.setText((String) msg.obj);
          //将ScrollView滚动到最底部
          mHandler.post(new Runnable() {
            @Override
            public void run() {
              scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
          });
          break;
        case GET_LOG_FAILED:
          hideLoading();
          showMessage("读取日志失败 !");
          break;
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.app_log_act);

    mModel = HiddenModel.getInstance();
    mModel.getAppLog(mHandler);
    logTxt = (TextView) findViewById(R.id.tv_app_log);
    scrollView = (ScrollView) findViewById(R.id.scrollView);
    showLoading("加载日志...");
  }
}
