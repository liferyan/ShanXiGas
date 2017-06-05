package com.cosw.shanxigas.load;

import android.os.Handler;
import com.cosw.protocol.enums.LoadStatusEnum;
import com.cosw.shanxigas.base.BasePresenter;
import com.cosw.shanxigas.base.BaseView;

/**
 * Created by Ryan on 2017/1/9.
 */

public interface LoadContract {

  interface View extends BaseView<Presenter> {

    void setCardBasicInfo(String balance, String cardNo);

    void setCardFullInfo(String balance, String cardNo, String price, String loadLimit);

    void setLoadEnable(boolean hasBalance);

    void goToPay(String url, String method, String sessionId);
  }

  interface Presenter extends BasePresenter {

    void setLoadAmount(int loadAmount);

    void pay(String loadMoney);

    void load();

    void refreshBalance(boolean forceRefresh);

    void onPayFailed();

  }

  interface Model {

    interface QueryCardInfoForPriceCallback {

      void onQueryCardInfoForPriceFailed();

      void onQueryCardInfoForPriceSuccess(String price, String loadLimit);
    }

    interface LoadBaseCallback {

      void onLoadFailed(boolean sendResultNotice);
    }

    interface HeBaoWapPayCallback {

      void onPayFailed();

      void onGoToHeBaoWapPay(String url, String method, String sessionId);
    }


    interface ObtainWriteCardInfoCallback extends LoadBaseCallback {

      void onObtainWriteCardInfoSuccess();
    }

    interface GapCashLoadCallback extends LoadBaseCallback {

      void onLoadSuccess();

      void onLoadFailedGapReversal();
    }

    interface GapReversalCallback {

      void onGapReversalSuccess();

      void onGapReversalFailed();
    }

    void heBaoWapPay(int payMoney, HeBaoWapPayCallback callback);

    void queryCardInfoForPrice(QueryCardInfoForPriceCallback callback);

    void queryCardInfoForPrice(Handler handler);

    void obtainWriteCardInfo(int loadAmount, ObtainWriteCardInfoCallback callback);

    void gapCashLoad(GapCashLoadCallback callback);

    void gapReversal(GapReversalCallback callback);

    void loadResultNotice(LoadStatusEnum loadState);

    int getBalance();
  }

}
