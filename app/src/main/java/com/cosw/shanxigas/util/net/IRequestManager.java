package com.cosw.shanxigas.util.net;

/**
 * Created by Ryan on 2016/12/21.
 */

public interface IRequestManager {

  interface IRequestCallback {

    void onSuccess(String response);

    void onError(Throwable throwable);
  }

  void get(String url, IRequestCallback requestCallback);

  void post(String url, String requestBodyJson, IRequestCallback requestCallback);

  String syncPost(String url, String requestBodyJson);

  void put(String url, String requestBodyJson, IRequestCallback requestCallback);

  void delete(String url, String requestBodyJson, IRequestCallback requestCallback);
}
