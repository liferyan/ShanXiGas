package com.cosw.shanxigas.app;

import static com.cosw.shanxigas.util.Constant.EXTRA_CARD_NO;
import static com.cosw.shanxigas.util.Constant.EXTRA_ONLY_BIND;
import static com.cosw.shanxigas.util.Constant.INITIAL_CARD_NO;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.cosw.shanxigas.R;
import com.cosw.shanxigas.activate.ActivateActivity;
import com.cosw.shanxigas.base.BaseActivity;
import com.cosw.shanxigas.readcard.ReadCardActivity;

/**
 * Created by Ryan on 2017/1/10.
 */

public class SplashActivity extends BaseActivity {

  private static final String TAG = "SplashActivity";

  //Splash界面显示的最短时间
  public static final int SHOW_TIME_MIN = 1000;

  public static final int LOAD_SUCCESS = 1;

  public static final int LOAD_FAILED = 0;

  private MyApplication app;

  private long mStartTime;

  private boolean hasBind;

  private Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case LOAD_SUCCESS:
          hasBind = (boolean) msg.obj;
          long loadingTime = System.currentTimeMillis() - mStartTime;
          if (loadingTime < SHOW_TIME_MIN) {
            mHandler.postDelayed(goToActivity, SHOW_TIME_MIN - loadingTime);
          } else {
            mHandler.post(goToActivity);
          }
          break;
        case LOAD_FAILED:
          String message = (String) msg.obj;
          //hideLoading();
          showMessage(message);
          break;
      }
    }
  };

  private Runnable goToActivity = new Runnable() {
    @Override
    public void run() {
      if (app.getCard() == null) {
        showMessage(getString(R.string.splash_card_app_connect_failed));
      } else {
        String cardNo = app.getCardNo();
        Intent intent;
        if (TextUtils.isEmpty(cardNo) || INITIAL_CARD_NO.equals(cardNo)) {
          //没有卡号,进入读卡界面
          intent = new Intent(SplashActivity.this, ReadCardActivity.class);
        } else {
          //有卡号判断 是否绑定
          if (hasBind) {
            //已绑定 直接进入主界面
            intent = new Intent(SplashActivity.this, MainActivity.class);
          } else {
            //有卡号 未绑定(之前个人化成功,绑定失败) -> 直接去激活界面绑定
            intent = new Intent(SplashActivity.this, ActivateActivity.class);
            intent.putExtra(EXTRA_CARD_NO, cardNo);
            intent.putExtra(EXTRA_ONLY_BIND, true);
          }
        }
        SplashActivity.this.startActivity(intent);
        finish();
      }
    }
  };

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.splash_act);

    //showLoading("loading...");

    app = MyApplication.getInstance();
    mStartTime = System.currentTimeMillis();
    //开始加载数据
    app.initData(mHandler);
  }
}
