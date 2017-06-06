package com.cosw.shanxigas.util.net;


import android.os.Handler;
import com.cosw.shanxigas.util.LogUtils;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Ryan on 2016/12/21.
 */

public class OkhttpRequestManager implements IRequestManager {

  private static final MediaType TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
  private static final String TAG = "OkhttpRequestManager";
  private static OkhttpRequestManager sOkhttpRequestManager;
  private OkHttpClient mOkHttpClient;
  private Handler mHander;

  private OkhttpRequestManager() {
    mOkHttpClient = new OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build();
    mHander = new Handler();
  }

  public static IRequestManager getInstance() {
    if (sOkhttpRequestManager == null) {
      sOkhttpRequestManager = new OkhttpRequestManager();
    }
    return sOkhttpRequestManager;
  }

  @Override
  public void get(String url, IRequestCallback requestCallback) {
    Request request = new Request.Builder()
        .url(url)
        .get()
        .build();
    addCallback(request, requestCallback);
  }

  @Override
  public void post(String url, String requestBodyJson, IRequestCallback requestCallback) {
    RequestBody requestBody = RequestBody.create(TYPE_JSON, requestBodyJson);
    Request request = new Request.Builder()
        .url(url)
        .post(requestBody)
        .build();
    addCallback(request, requestCallback);
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
      LogUtils.e(TAG, "syncPost: ", e);
    }
    return response;
  }

  @Override
  public void put(String url, String requestBodyJson, IRequestCallback requestCallback) {
    RequestBody requestBody = RequestBody.create(TYPE_JSON, requestBodyJson);
    Request request = new Request.Builder()
        .url(url)
        .put(requestBody)
        .build();
    addCallback(request, requestCallback);
  }

  @Override
  public void delete(String url, String requestBodyJson, IRequestCallback requestCallback) {
    RequestBody requestBody = RequestBody.create(TYPE_JSON, requestBodyJson);
    Request request = new Request.Builder()
        .url(url)
        .delete(requestBody)
        .build();
    addCallback(request, requestCallback);
  }

  private void addCallback(final Request request, final IRequestCallback requestCallback) {
    mOkHttpClient.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, final IOException e) {
        mHander.post(new Runnable() {
          @Override
          public void run() {
            requestCallback.onError(e);
          }
        });
      }

      @Override
      public void onResponse(final Call call, final Response response) {
        if (response.isSuccessful()) {
          final String json;
          try {
            json = response.body().string();
          } catch (IOException e) {
            LogUtils.e(TAG, "onResponse: ", e);
            return;
          }
          mHander.post(new Runnable() {
            @Override
            public void run() {
              requestCallback.onSuccess(json);
            }
          });
        } else {
          mHander.post(new Runnable() {
            @Override
            public void run() {
              requestCallback.onError(
                  new IOException(response.message() + ",url=" + call.request().url().toString()));
            }
          });
        }
      }
    });
  }
}
