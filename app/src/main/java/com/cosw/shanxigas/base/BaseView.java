package com.cosw.shanxigas.base;

/**
 * Created by Ryan on 2017/1/9.
 */

public interface BaseView<T> {

  void setPresenter(T presenter);

  void showLoading(String title);

  void hideLoading();

  void showMessage(String message);

}
