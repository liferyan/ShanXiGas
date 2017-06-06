package com.cosw.shanxigas.app;

import static com.cosw.shanxigas.util.Constant.GAS_AID;
import static com.cosw.shanxigas.util.Constant.INITIAL_ACCOUNT;
import static com.cosw.shanxigas.util.Constant.INITIAL_CARD_NO;
import static com.cosw.shanxigas.util.Constant.NETWORK_ERROR_MSG;
import static com.cosw.shanxigas.util.Constant.READ_CARD_FAILED_TO_GET_CARD_BIND_STATUS_MSG;
import static com.cosw.shanxigas.util.Constant.READ_CARD_NO;
import static com.cosw.shanxigas.util.Constant.SELECT_3F01;
import static com.cosw.shanxigas.util.Constant.SELECT_CARD_NO_FILE;
import static com.cosw.shanxigas.util.Constant.SERVER_URL;

import android.accounts.NetworkErrorException;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.cosw.protocol.req.QueryCardBindStatusReq;
import com.cosw.protocol.resp.QueryCardBindStatusResp;
import com.cosw.shanxigas.R;
import com.cosw.shanxigas.card.CardException;
import com.cosw.shanxigas.card.CardFactory;
import com.cosw.shanxigas.card.ICard;
import com.cosw.shanxigas.settings.SettingsActivity;
import com.cosw.shanxigas.util.CrashHandler;
import com.cosw.shanxigas.util.DataUtil;
import com.cosw.shanxigas.util.LogUtils;
import com.cosw.shanxigas.util.net.RequestFactory;
import com.google.gson.Gson;
import java.io.IOException;
import org.simalliance.openmobileapi.SEService;
import org.simalliance.openmobileapi.SEService.CallBack;

/**
 * Created by Ryan on 2017/1/10.
 */

public class MyApplication extends Application {

  private static final String TAG = "APP";

  private static MyApplication app;

  private ICard mCard;

  private String mATR;
  private String mCardNo;
  private String mLoadTransNum;
  private String mOrderNo;
  private String mAppVersion;

  private SEService mSeService;

  public void setAccount(String account) {
    mAccount = account;
  }

  public String getAccount() {
    return mAccount;
  }

  private String mAccount;

  //是否连接到SE
  private boolean seHasConnected;

  public static MyApplication getInstance() {
    return app;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    app = this;

    CrashHandler crashHandler = CrashHandler.getInstance();
    crashHandler.init(this);

    LogUtils.i(TAG, "\r\r----------------------------应用开始启动------------------------\r\n");
    new SEService(this, new CallBack() {
      @Override
      public void serviceConnected(SEService seService) {
        mSeService = seService;
        seHasConnected = true;
        LogUtils.i(TAG, "SE Connected");
      }
    });
    LogUtils.i(TAG, "SE Start Connect ...");
  }

  public void initData(final Handler handler) {
    LogUtils.i(TAG, "\r\r----------------------------初始化数据------------------------\r\n");
    new Thread(new Runnable() {
      @Override
      public void run() {
        LogUtils.i(TAG, "initData: " + getString(R.string.application_begin_load_data));
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(app);
        mAccount = sharedPref.getString(SettingsActivity.ACCOUNT, INITIAL_ACCOUNT);
        LogUtils.i(TAG, "和包账号: " + mAccount);
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
          packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
          LogUtils.e(TAG, "initData: ", e);
        }
        if (packInfo != null) {
          mAppVersion = packInfo.versionName;
        }
        //等待SE建立连接
        try {
          Thread.sleep(200);
        } catch (InterruptedException e) {
          LogUtils.e(TAG, "sleep", e);
        }
        boolean hasBind = false;
        try {
          LogUtils.i(TAG, getString(R.string.application_start_connect_card_app));
          mCard = CardFactory.getCardManager(mSeService, GAS_AID);
          LogUtils.i(TAG, getString(R.string.application_end_connect_card_app));
          //读取SIM卡燃气卡号
          mCard.transmit(SELECT_3F01);
          mCard.transmit(SELECT_CARD_NO_FILE);
          mCardNo = mCard.transmit(READ_CARD_NO).getData();
          LogUtils.i(TAG, "SIM卡燃气卡号: " + mCardNo);
          if (!TextUtils.isEmpty(mCardNo) && !INITIAL_CARD_NO.equals(mCardNo)) {
            //读出燃气卡号,查询卡号绑定状态
            hasBind = queryCardBindStatus(mCardNo);
          }
        } catch (CardException e) {
          LogUtils.e(TAG, getString(R.string.splash_card_app_connect_failed), e);
          handler.obtainMessage(SplashActivity.LOAD_FAILED,
              getString(R.string.splash_card_app_connect_failed)).sendToTarget();
          return;
        } catch (NetworkErrorException | IOException e) {
          String errorMsg;
          if (e instanceof NetworkErrorException) {
            errorMsg = NETWORK_ERROR_MSG;
          } else {
            errorMsg = READ_CARD_FAILED_TO_GET_CARD_BIND_STATUS_MSG;
          }
          handler.obtainMessage(SplashActivity.LOAD_FAILED,
              errorMsg).sendToTarget();
          return;
        }
        LogUtils.i(TAG, "initData: " + getString(R.string.application_end_load_data));
        handler.obtainMessage(SplashActivity.LOAD_SUCCESS, hasBind).sendToTarget();
      }
    }).start();
  }

  public String getATR() {
    LogUtils.i(TAG, "SE getATR:" + mATR);
    return mATR;
  }

  public void setATR(String atr) {
    LogUtils.i(TAG, "SE setATR:" + atr);
    mATR = atr;
  }

  public String getCardNo() {
    return mCardNo;
  }

  public void setCardNo(String cardNo) {
    mCardNo = cardNo;
  }

  public String getLoadTransNum() {
    return mLoadTransNum;
  }

  public void setLoadTransNum(String loadTransNum) {
    mLoadTransNum = loadTransNum;
  }

  public String getOrderNo() {
    return mOrderNo;
  }

  public void setOrderNo(String orderNo) {
    mOrderNo = orderNo;
  }

  public ICard getCard() {
    if (!seHasConnected) {
      LogUtils.i(TAG, getString(R.string.application_se_connect_failed));
    }
    return mCard;
  }

  public String getAppVersion() {
    return mAppVersion;
  }

  private boolean queryCardBindStatus(String cardNo) throws NetworkErrorException, IOException {
    QueryCardBindStatusReq req = new QueryCardBindStatusReq();
    req.setCardNo(cardNo);
    Gson mGson = new Gson();
    String reqJson = mGson.toJson(req);
    String respJson = RequestFactory.getSyncRequestManager().syncPost(SERVER_URL, reqJson);
    if (respJson == null) {
      throw new NetworkErrorException();
    }
    QueryCardBindStatusResp resp = mGson.fromJson(respJson, QueryCardBindStatusResp.class);
    if (!DataUtil.checkResponseSuccess(resp)) {
      LogUtils.i(TAG, "queryCardBindStatus:" + resp.getResponseDesc());
      throw new IOException(resp.getResponseDesc());
    }
    return "01".equals(resp.getStatus());
  }

}
