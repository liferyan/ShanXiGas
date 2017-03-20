package com.cosw.shanxigas.util.net;

/**
 * Created by Ryan on 2016/12/21.
 */

public class RequestFactory {

  public static IRequestManager getRequestManager() {
    return OkhttpRequestManager.getInstance();
  }

  public static IRequestManager getSyncRequestManager() {
    return OkhttpSyncRequestManager.getInstance();
  }
}
