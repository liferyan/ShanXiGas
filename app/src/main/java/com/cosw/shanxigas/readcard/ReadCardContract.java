package com.cosw.shanxigas.readcard;

import android.nfc.Tag;
import com.cosw.shanxigas.base.BasePresenter;
import com.cosw.shanxigas.base.BaseView;

/**
 * Created by Ryan on 2017/1/11.
 */

public interface ReadCardContract {

  interface View extends BaseView<Presenter> {

    void showLoading(String title);

    void hideLoading();

    void showMessage(String message);

    void goToActivateActivity(String cardNo);
  }

  interface Presenter extends BasePresenter {

    void getCardNoFromTag(Tag tag);
  }

  interface Model {

    interface ReadCardCallback {

      void onReadCardFailed();

      void onReadCardSuccess(String cardNo);
    }

    interface QueryCardBindStatusCallback {

      void onQueryCardBindStatusFailed();

      void onHasBind(boolean hasBind);
    }

    void readCardFromTag(Tag tag, ReadCardCallback callback);

    void queryCardBindStatus(String cardNo, QueryCardBindStatusCallback callback);
  }

}
