package com.cosw.shanxigas.util.net;

import android.os.Handler;
import com.cosw.shanxigas.util.LogUtils;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Ryan on 2017/1/18.
 */

public class OkhttpSyncRequestManager implements IRequestManager {

  private static final String TAG = "HTTP";

  public static final MediaType TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
  private static OkhttpSyncRequestManager sOkhttpSyncRequestManager;
  private OkHttpClient mOkHttpClient;
  private Handler mHander;

  private OkhttpSyncRequestManager() {
    mOkHttpClient = new OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .build();
  }

  public static IRequestManager getInstance() {
    if (sOkhttpSyncRequestManager == null) {
      sOkhttpSyncRequestManager = new OkhttpSyncRequestManager();
    }
    return sOkhttpSyncRequestManager;
  }

  @Override
  public void get(String url, IRequestCallback requestCallback) {
  }

  @Override
  public void post(String url, String requestBodyJson, IRequestCallback requestCallback) {
  }

  @Override
  public String syncPost(String url, String requestBodyJson) {
    RequestBody requestBody = RequestBody.create(TYPE_JSON, requestBodyJson);
    Request request = new Request.Builder()
        .url(url)
        .post(requestBody)
        .build();
    String response = null;
    try {
      response = mOkHttpClient.newCall(request).execute().body().string();
    } catch (IOException e) {
      LogUtils.e(TAG, "Network Error: ", e);
    }
    return response;
  }

  @Override
  public void put(String url, String requestBodyJson, IRequestCallback requestCallback) {
  }

  @Override
  public void delete(String url, String requestBodyJson, IRequestCallback requestCallback) {
  }
}
