package com.cosw.shanxigas.hidden;

import static com.cosw.shanxigas.hidden.AppLogActivity.GET_LOG_FAILED;
import static com.cosw.shanxigas.hidden.AppLogActivity.GET_LOG_SUCCESS;
import static com.cosw.shanxigas.hidden.HiddenActivity.UNBIND_FAILED;
import static com.cosw.shanxigas.hidden.HiddenActivity.UNBIND_SUCCESS;
import static com.cosw.shanxigas.util.Constant.SERVER_URL;

import android.os.Environment;
import android.os.Handler;
import com.cosw.protocol.req.CardUnBindReq;
import com.cosw.protocol.resp.CardUnBindResp;
import com.cosw.shanxigas.app.MyApplication;
import com.cosw.shanxigas.util.DataUtil;
import com.cosw.shanxigas.util.LogUtils;
import com.cosw.shanxigas.util.net.RequestFactory;
import com.google.gson.Gson;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

/**
 * Created by Ryan on 2017/1/18.
 */

public class HiddenModel {

  private static final String TAG = "HiddenModel";

  private static HiddenModel INSTANCE;

  private String logPath;

  private MyApplication app;

  private static final String ROOT = Environment.getExternalStorageDirectory()
      .getPath() + "/shanxigas/"; // SD卡中的根目录
  private static final String PATH_LOG_INFO = ROOT + "info/";

  private HiddenModel() {
    app = MyApplication.getInstance();
  }

  public static HiddenModel getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new HiddenModel();
    }
    return INSTANCE;
  }

  void unBind(final Handler handler) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        CardUnBindReq req = new CardUnBindReq();
        req.setCardNo(app.getCardNo());
        req.setAtr(app.getATR());
        //req.setTelephone("");
        req.setPayAccount(app.getAccount());
        final Gson mGson = new Gson();
        String reqJson = mGson.toJson(req);
        String respJson = RequestFactory.getSyncRequestManager().syncPost(SERVER_URL, reqJson);
        if (respJson == null) {
          handler.obtainMessage(UNBIND_FAILED).sendToTarget();
          return;
        }
        CardUnBindResp resp = mGson.fromJson(respJson, CardUnBindResp.class);
        if (!DataUtil.checkResponseSuccess(resp)) {
          handler.obtainMessage(UNBIND_FAILED).sendToTarget();
          return;
        }
        handler.obtainMessage(UNBIND_SUCCESS).sendToTarget();
      }
    }).start();
  }

  void getAppLog(final Handler handler) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        FileInputStream inputStream = null;
        Scanner sc = null;
        try {
          Date date = new Date();
          SimpleDateFormat dateFormat = new SimpleDateFormat("",
              Locale.SIMPLIFIED_CHINESE);
          dateFormat.applyPattern("yyyy");
          logPath = PATH_LOG_INFO + dateFormat.format(date) + "-";
          dateFormat.applyPattern("MM");
          logPath += dateFormat.format(date) + "-";
          dateFormat.applyPattern("dd");
          logPath += dateFormat.format(date) + ".log";
          inputStream = new FileInputStream(logPath);
          sc = new Scanner(inputStream, "UTF-8");
          StringBuilder sb = new StringBuilder();
          while (sc.hasNextLine()) {
            String line = sc.nextLine();
            sb.append(line).append("\r\n");
          }
          String logStr = sb.toString();
          handler.obtainMessage(GET_LOG_SUCCESS, logStr).sendToTarget();
        } catch (FileNotFoundException e) {
          LogUtils.e(TAG, "getAppLog", e);
          handler.obtainMessage(GET_LOG_FAILED).sendToTarget();
        } finally {
          if (inputStream != null) {
            try {
              inputStream.close();
            } catch (IOException e) {
              LogUtils.e(TAG, "getAppLog", e);
            }
          }
          if (sc != null) {
            sc.close();
          }
        }
      }
    }).start();
  }
}
