package com.cosw.shanxigas.load;

import static com.cosw.shanxigas.util.Constant.LOAD_FAILED_MSG;
import static com.cosw.shanxigas.util.Constant.LOAD_FAILED_TO_GET_CARD_INFO_MSG;
import static com.cosw.shanxigas.util.Constant.LOAD_FAILED_TO_PAY;
import static com.cosw.shanxigas.util.Constant.LOAD_LOADING_GET_CARD_INFO;
import static com.cosw.shanxigas.util.Constant.LOAD_LOADING_LOAD;
import static com.cosw.shanxigas.util.Constant.LOAD_LOADING_PAY;
import static com.cosw.shanxigas.util.Constant.LOAD_SUCCESS_MSG;
import static com.cosw.shanxigas.util.Constant.REFRESH_BALANCE_NOT_ZERO;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import com.cosw.protocol.enums.LoadStatusEnum;
import com.cosw.protocol.resp.QueryCardInfoForPriceResp;
import com.cosw.shanxigas.app.MyApplication;
import java.text.NumberFormat;

/**
 * Created by Ryan on 2017/1/9.
 */

public class LoadPresenter implements LoadContract.Presenter,
    LoadContract.Model.QueryCardInfoForPriceCallback,
    LoadContract.Model.ObtainWriteCardInfoCallback,
    LoadContract.Model.GapCashLoadCallback,
    LoadContract.Model.GapReversalCallback,
    LoadContract.Model.HeBaoWapPayCallback {

  static final int QUERY_INFO_SUCCESS = 1;
  static final int QUERY_INFO_FAILED = 0;

  private boolean hasLoadCardPriceInfo;

  private LoadContract.View mLoadView;
  private LoadContract.Model mModel;

  @Override
  public void setLoadAmount(int loadAmount) {
    LoadPresenter.loadAmount = loadAmount;
  }

  private static int loadAmount;
  private int balance;
  private String mCardNo;
  private MyApplication app;

  private Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case QUERY_INFO_SUCCESS:
          hasLoadCardPriceInfo = true;
          QueryCardInfoForPriceResp resp = (QueryCardInfoForPriceResp) msg.obj;
          app.setPrice(Double.parseDouble(resp.getPrice()));
          mLoadView.hideLoading();
          mLoadView
              .setCardFullInfo(NumberFormat.getCurrencyInstance().format(balance) + "元", mCardNo,
                  resp.getPrice(),
                  String.valueOf(resp.getMaxPrice()));
          mLoadView.setLoadEnable(balance != 0);
          break;
        case QUERY_INFO_FAILED:
          setMessage(LOAD_FAILED_TO_GET_CARD_INFO_MSG);
          break;
      }
    }
  };

  public LoadPresenter(LoadContract.View loadView, LoadContract.Model model) {
    mLoadView = loadView;
    mModel = model;
    app = MyApplication.getInstance();
    mCardNo = app.getCardNo();
  }

  @Override
  public void start() {
    balance = mModel.getBalance();
    if (balance != 0) {
      String balanceStr = NumberFormat.getCurrencyInstance().format(balance) + "元";
      mLoadView.setCardBasicInfo(balanceStr, mCardNo);
    } else {
      if (!hasLoadCardPriceInfo) {
        mLoadView.showLoading(LOAD_LOADING_GET_CARD_INFO);
        new Thread(new Runnable() {
          @Override
          public void run() {
            mModel.queryCardInfoForPrice(mHandler);
          }
        }).start();
      }
    }
  }

  @Override
  public void refreshBalance(boolean forceRefresh) {
    mLoadView.showLoading(LOAD_LOADING_GET_CARD_INFO);
    balance = mModel.getBalance();
    if (balance == 0) {
      mModel.queryCardInfoForPrice(this);
    } else {
      String balanceStr = NumberFormat.getCurrencyInstance().format(balance) + "元";
      mLoadView.setCardBasicInfo(balanceStr, mCardNo);
      if (forceRefresh) {
        mLoadView.showMessage(REFRESH_BALANCE_NOT_ZERO);
      }
      mLoadView.hideLoading();
    }
  }

  private void setMessage(String message) {
    mLoadView.hideLoading();
    mLoadView.showMessage(message);
  }

  @Override
  public void onQueryCardInfoForPriceFailed() {
    setMessage(LOAD_FAILED_TO_GET_CARD_INFO_MSG);
  }

  @Override
  public void onQueryCardInfoForPriceSuccess(String price, String loadLimit) {
    hasLoadCardPriceInfo = true;
    mLoadView.hideLoading();
    mLoadView
        .setCardFullInfo(NumberFormat.getCurrencyInstance().format(balance) + "元", mCardNo, price,
            loadLimit);
    mLoadView.setLoadEnable(balance != 0);
  }

  @Override
  public void pay(final String loadMoney) {
    mLoadView.showLoading(LOAD_LOADING_PAY);
    //单位为元
    if (!TextUtils.isEmpty(loadMoney) && TextUtils.isDigitsOnly(loadMoney)) {
      loadAmount = Integer.parseInt(loadMoney);
      mModel.heBaoWapPay(loadAmount * 100, this);
    }
  }

  @Override
  public void onGoToHeBaoWapPay(String url, String method, String sessionId) {
    mLoadView.hideLoading();
    mLoadView.goToPay(url, method, sessionId);
  }

  @Override
  public void load() {
    mLoadView.showLoading(LOAD_LOADING_LOAD);
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        mModel.obtainWriteCardInfo(loadAmount, LoadPresenter.this);
      }
    }, 3000);

  }

  @Override
  public void onObtainWriteCardInfoSuccess() {
    mModel.gapCashLoad(this);
  }

  @Override
  public void onLoadSuccess() {
    //圈存成功,刷新余额
    refreshBalance(false);
    setMessage(LOAD_SUCCESS_MSG);
    mModel.loadResultNotice(LoadStatusEnum.LoadSuccess);
  }

  @Override
  public void onLoadFailed(boolean sendResultNotice) {
    setMessage(LOAD_FAILED_MSG);
    //发圈存失败结果通知
    if (sendResultNotice) {
      mModel.loadResultNotice(LoadStatusEnum.LoadFaild);
    }
  }

  @Override
  public void onLoadFailedGapReversal() {
    setMessage(LOAD_FAILED_MSG);
    //发冲正请求
    mModel.gapReversal(this);
  }

  @Override
  public void onPayFailed() {
    setMessage(LOAD_FAILED_TO_PAY);
  }

  @Override
  public void onGapReversalSuccess() {
    mModel.loadResultNotice(LoadStatusEnum.ReverseSuccess);
  }

  @Override
  public void onGapReversalFailed() {
    mModel.loadResultNotice(LoadStatusEnum.ReverseFaild);
  }
}
