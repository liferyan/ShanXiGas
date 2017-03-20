package com.cosw.shanxigas.activate;

import com.cosw.shanxigas.base.BasePresenter;
import com.cosw.shanxigas.base.BaseView;

/**
 * Created by Ryan on 2017/1/10.
 */

public interface ActivateContract {

  interface View extends BaseView<Presenter> {

    void showLoading(String title);

    void hideLoading();

    void showCardInfo(String cardNo, String userName, String address, String gapType);

    void showMessage(String message);

    void goToMainActivity();
  }


  interface Presenter extends BasePresenter {

    void getCardInfo(String cardNo);

    void activate(boolean onlyBind);
  }

  interface Model {

    interface QueryCardInfoForBindCallback {

      void onQueryCardInfoForBindSuccess(String cardNo, String userName, String address,
          String gapType);

      void onQueryCardInfoForBindFailed();
    }

    interface ActivateBaseCallback {

      void onActivateFailed();
    }

    interface QueryCardKeyCallback extends ActivateBaseCallback {

      void onQueryCardKeySuccess();

    }

    interface UserInfoInitCallback extends ActivateBaseCallback {

      void onUserInfoInitSuccess();
    }

    interface CardBindCallback {

      void onCardBindFailed();

      void onCardBindSuccess();
    }

    void loadCardInfo(String cardNo, QueryCardInfoForBindCallback callback);

    void queryCardKey(QueryCardKeyCallback callback);

    void userInfoInit(UserInfoInitCallback callback);

    void cardBind(CardBindCallback callback);
  }

}
