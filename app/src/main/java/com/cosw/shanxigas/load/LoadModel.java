package com.cosw.shanxigas.load;

import static com.cosw.shanxigas.load.LoadPresenter.QUERY_INFO_FAILED;
import static com.cosw.shanxigas.load.LoadPresenter.QUERY_INFO_SUCCESS;
import static com.cosw.shanxigas.util.Constant.BRANCH_NO;
import static com.cosw.shanxigas.util.Constant.CARD_INFO2;
import static com.cosw.shanxigas.util.Constant.CARD_INFO3;
import static com.cosw.shanxigas.util.Constant.CARD_INFO4;
import static com.cosw.shanxigas.util.Constant.CARD_INFO5;
import static com.cosw.shanxigas.util.Constant.CARD_PROVIDER;
import static com.cosw.shanxigas.util.Constant.GAP_COUNT;
import static com.cosw.shanxigas.util.Constant.GET_RANDOM_4;
import static com.cosw.shanxigas.util.Constant.GET_RANDOM_8;
import static com.cosw.shanxigas.util.Constant.LOAD_TRANS_NUM_DATA_FORMAT;
import static com.cosw.shanxigas.util.Constant.SERVER_URL;
import static com.cosw.shanxigas.util.Constant.SELECT_3F01;
import static com.cosw.shanxigas.util.Constant.TRANS_TIME_DATA_FORMAT;
import static com.cosw.shanxigas.util.net.RequestFactory.getRequestManager;

import android.os.Handler;
import com.cosw.protocol.enums.LoadStatusEnum;
import com.cosw.protocol.req.GapCashLoadReq;
import com.cosw.protocol.req.GapReversalReq;
import com.cosw.protocol.req.HeBaoWapPayReq;
import com.cosw.protocol.req.LoadResultNoticeReq;
import com.cosw.protocol.req.ObtainWriteCardInfoReq;
import com.cosw.protocol.req.QueryCardInfoForPriceReq;
import com.cosw.protocol.resp.GapCashLoadResp;
import com.cosw.protocol.resp.GapReversalResp;
import com.cosw.protocol.resp.HeBaoWapPayResp;
import com.cosw.protocol.resp.ObtainWriteCardInfoResp;
import com.cosw.protocol.resp.QueryCardInfoForPriceResp;
import com.cosw.protocol.util.DateUtil;
import com.cosw.shanxigas.app.MyApplication;
import com.cosw.shanxigas.card.CardException;
import com.cosw.shanxigas.card.CardUtil;
import com.cosw.shanxigas.card.ICard;
import com.cosw.shanxigas.util.DataUtil;
import com.cosw.shanxigas.util.LogUtils;
import com.cosw.shanxigas.util.net.IRequestManager.IRequestCallback;
import com.cosw.shanxigas.util.net.RequestFactory;
import com.google.gson.Gson;
import java.util.Date;

/**
 * Created by Ryan on 2017/1/11.
 */

public class LoadModel implements LoadContract.Model {

  private static final String TAG = "LoadModel";

  private MyApplication app;

  private ICard mCard;

  private CardUtil mCardUtil;

  private Gson mGson;

  private String reqJson;

  //00006400
  private String balance;

  //维管费起始日期
  private String wgStartTime;
  //维管费截止日期
  private String wgEndTime;
  //维管计费年数
  private int wgYears;
  //维管单价
  private int wgPrice;
  //限购金额
  private int maxPrice;
  //维管费
  private int wgCash;
  //当前购气单价
  private double price;
  //气量
  private int gapNum;
  //购气次数
  private int gapCount;
  //购气次数返回值
  private String gapCountStr;
  //购气金额
  private int gapCash;

  private String mCardNo;

  private LoadModel() {
    app = MyApplication.getInstance();
    mCardNo = app.getCardNo();
    mCard = app.getCard();
    mCardUtil = CardUtil.getInstance(mCard);
    mGson = DataUtil.getGson();
  }

  public static LoadModel getInstance() {
    return new LoadModel();
  }

  @Override
  public void heBaoWapPay(int payMoney, final HeBaoWapPayCallback callback) {
    HeBaoWapPayReq req = new HeBaoWapPayReq();
    //单位分
    payMoney = 1;
    req.setAmout(String.valueOf(payMoney));
    req.setMobileid(app.getAccount());
    req.setCardNo(mCardNo);
    reqJson = mGson.toJson(req);
    getRequestManager().post(SERVER_URL, reqJson, new IRequestCallback() {
      @Override
      public void onSuccess(String response) {
        HeBaoWapPayResp resp = mGson.fromJson(response, HeBaoWapPayResp.class);
        if (!DataUtil.checkResponseSuccess(resp)) {
          LogUtils.i(TAG, "heBaoWapPay: " + resp.getResponseDesc());
          callback.onPayFailed();
          return;
        }
        String url = resp.getUrl();
        String method = resp.getMethod();
        String sessionId = resp.getSessionId();
        callback.onGoToHeBaoWapPay(url, method, sessionId);
      }

      @Override
      public void onError(Throwable throwable) {
        LogUtils.e(TAG, "heBaoWapPay: ", throwable);
        callback.onPayFailed();
      }
    });
  }

  @Override
  public void queryCardInfoForPrice(final QueryCardInfoForPriceCallback callback) {
    String INFO2, INFO3, INFO4, INFO5;
    try {
      mCard.transmit(SELECT_3F01);
      //购气次数
      gapCountStr = mCard.transmit(GAP_COUNT).getData();
      gapCount = Integer.parseInt(gapCountStr, 16);
      //读余额 805c000204
      balance = mCardUtil.getBalance();
      //读阶梯单价 00b08A0089
      INFO2 = mCard.transmit(CARD_INFO2).getData();
      //读表参数信息 00b088000F
      INFO3 = mCard.transmit(CARD_INFO3).getData();
      //读表反写信息 00b08300B9
      INFO4 = mCard.transmit(CARD_INFO4).getData();
      //读累计消耗量 00b08500BD
      INFO5 = mCard.transmit(CARD_INFO5).getData();
    } catch (CardException e) {
      LogUtils.e(TAG, "queryCardInfoForPrice: ", e);
      callback.onQueryCardInfoForPriceFailed();
      return;
    }
    QueryCardInfoForPriceReq req = new QueryCardInfoForPriceReq();
    req.setCardNo(mCardNo);
    req.setBranchNo(BRANCH_NO);
    req.setPayAccount(app.getAccount());
    req.setInfo1(balance);
    req.setInfo2(INFO2);
    req.setInfo3(INFO3);
    req.setInfo4(INFO4);
    req.setInfo5(INFO5);
    reqJson = mGson.toJson(req);
    RequestFactory.getRequestManager().post(SERVER_URL, reqJson, new IRequestCallback() {
      @Override
      public void onSuccess(String response) {
        QueryCardInfoForPriceResp resp = mGson.fromJson(response, QueryCardInfoForPriceResp.class);
        if (!DataUtil.checkResponseSuccess(resp)) {
          LogUtils.i(TAG, "queryCardInfoForPrice: " + resp.getResponseDesc());
          callback.onQueryCardInfoForPriceFailed();
          return;
        }
        String priceStr = resp.getPrice();
        String loadLimitStr = String.valueOf(resp.getMaxPrice());
        price = Double.parseDouble(priceStr);
        wgStartTime = resp.getWgStartTime();
        wgEndTime = resp.getWgEndTime();
        wgYears = Integer.parseInt(resp.getWgYears());
        callback.onQueryCardInfoForPriceSuccess(priceStr, loadLimitStr);
      }

      @Override
      public void onError(Throwable throwable) {
        LogUtils.e(TAG, "queryCardInfoForPrice: ", throwable);
        callback.onQueryCardInfoForPriceFailed();
      }
    });
  }

  @Override
  public void queryCardInfoForPrice(Handler handler) {
    String INFO2, INFO3, INFO4, INFO5;
    try {
      mCard.transmit(SELECT_3F01);
      //购气次数
      gapCountStr = mCard.transmit(GAP_COUNT).getData();
      gapCount = Integer.parseInt(gapCountStr, 16);
      //读余额 805c000204
      balance = mCardUtil.getBalance();
      //读阶梯单价 00b08A0089
      INFO2 = mCard.transmit(CARD_INFO2).getData();
      //读表参数信息 00b088000F
      INFO3 = mCard.transmit(CARD_INFO3).getData();
      //读表反写信息 00b08300B9
      INFO4 = mCard.transmit(CARD_INFO4).getData();
      //读累计消耗量 00b08500BD
      INFO5 = mCard.transmit(CARD_INFO5).getData();
    } catch (CardException e) {
      LogUtils.e(TAG, "queryCardInfoForPrice: ", e);
      handler.obtainMessage(QUERY_INFO_FAILED).sendToTarget();
      return;
    }
    QueryCardInfoForPriceReq req = new QueryCardInfoForPriceReq();
    req.setCardNo(mCardNo);
    req.setBranchNo(BRANCH_NO);
    req.setPayAccount(app.getAccount());
    req.setInfo1(balance);
    req.setInfo2(INFO2);
    req.setInfo3(INFO3);
    req.setInfo4(INFO4);
    req.setInfo5(INFO5);
    reqJson = mGson.toJson(req);
    String respJson = RequestFactory.getSyncRequestManager().syncPost(SERVER_URL, reqJson);
    if (respJson == null) {
      handler.obtainMessage(QUERY_INFO_FAILED).sendToTarget();
      return;
    }
    QueryCardInfoForPriceResp resp = mGson.fromJson(respJson, QueryCardInfoForPriceResp.class);
    if (!DataUtil.checkResponseSuccess(resp)) {
      LogUtils.i(TAG, "queryCardInfoForPrice: " + resp.getResponseDesc());
      handler.obtainMessage(QUERY_INFO_FAILED).sendToTarget();
      return;
    }
    handler.obtainMessage(QUERY_INFO_SUCCESS, resp).sendToTarget();
  }

  @Override
  public void obtainWriteCardInfo(int loadAmount, final ObtainWriteCardInfoCallback callback) {
    LogUtils.i(TAG, "------------------------开始获取写卡信息----------------------------");
//    app.setOrderNo("20170114110924608407");
    String random;
    try {
      mCard.transmit(SELECT_3F01);
      random = mCard.transmit(GET_RANDOM_8).getData();
    } catch (CardException e) {
      LogUtils.e(TAG, "obtainWriteCardInfo: ", e);
      callback.onLoadFailed();
      return;
    }
    ObtainWriteCardInfoReq req = new ObtainWriteCardInfoReq();
    gapCash = loadAmount;
    req.setCardNo(mCardNo);
    //req.setTeller(PAY_ACCOUNT);
    gapNum = (int) (loadAmount / price);
    //for test
    gapNum = 1;
    req.setGapNum(String.valueOf(gapNum));
    req.setGapCount(gapCountStr);
    req.setGapCash(loadAmount);
    req.setTransTime(DateUtil.getDateTime(TRANS_TIME_DATA_FORMAT, new Date()));
    req.setBranchNo(BRANCH_NO);
    req.setCardProvider(CARD_PROVIDER);
    req.setRandom(random);
    req.setCardSequence(app.getATR());
    req.setOrderNo(app.getOrderNo());
    req.setWgCash(wgCash);
    req.setWgStartTime(wgStartTime);
    req.setWgEndTime(wgEndTime);
    reqJson = mGson.toJson(req);
    getRequestManager().post(SERVER_URL, reqJson, new IRequestCallback() {
      @Override
      public void onSuccess(String response) {
        ObtainWriteCardInfoResp resp = mGson.fromJson(response, ObtainWriteCardInfoResp.class);
        if (!DataUtil.checkResponseSuccess(resp)) {
          LogUtils.i(TAG, "obtainWriteCardInfo: " + resp.getResponseDesc());
          callback.onLoadFailed();
          return;
        }
        String apduStream = resp.getApduStream();
        String[] apduList = apduStream.split("\\|");
        try {
          for (String apdu : apduList) {
            mCard.transmit(apdu);
          }
        } catch (CardException e) {
          LogUtils.e(TAG, "obtainWriteCardInfo: ", e);
          callback.onLoadFailed();
          return;
        }
        callback.onObtainWriteCardInfoSuccess();
        LogUtils.i(TAG, "------------------------获取写卡信息完成----------------------------");
      }

      @Override
      public void onError(Throwable throwable) {
        LogUtils.e(TAG, "obtainWriteCardInfo: ", throwable);
        callback.onLoadFailed();
      }
    });
  }


  @Override
  public void gapCashLoad(final GapCashLoadCallback callback) {
    LogUtils.i(TAG, "------------------------开始圈存----------------------------");
    //100 -> 000064
    String gapCashHexStr = Integer.toHexString(gapCash);
    int zeroLen = 6 - gapCashHexStr.length();
    gapCashHexStr = "000000".substring(0, zeroLen) + gapCashHexStr;
    String initForLoadAPDU = "805000020B01" + gapCashHexStr + "00112233445566";
    String randomStr;
    String loadResult;
    try {
      randomStr = mCard.transmit(GET_RANDOM_8).getData();
      loadResult = mCard.transmit(initForLoadAPDU).getData();
    } catch (CardException e) {
      LogUtils.e(TAG, "gapCashLoad: ", e);
      callback.onLoadFailed();
      return;
    }
    GapCashLoadReq req = new GapCashLoadReq();
    req.setCardNo(mCardNo);
    req.setBranchNo(BRANCH_NO);
    req.setAtr(app.getATR());
    req.setWgCash(wgCash);
    req.setOrderNo(app.getOrderNo());
    req.setLoadResult(loadResult);
    req.setRandomNum(randomStr);
    //req.setPayAccount(PAY_ACCOUNT);
    String loadTransNum = DataUtil.generateTransNum(LOAD_TRANS_NUM_DATA_FORMAT);
    app.setLoadTransNum(loadTransNum);
    req.setLoadTransNum(loadTransNum);
    req.setGapDate(DateUtil.getDateTime(TRANS_TIME_DATA_FORMAT, new Date()));
    req.setGapCash(gapCash);
    req.setWgCash(wgCash);
    req.setWgStartTime(wgStartTime);
    req.setWgEndTime(wgEndTime);
    req.setGapNum(gapNum);
    reqJson = mGson.toJson(req);
    getRequestManager().post(SERVER_URL, reqJson, new IRequestCallback() {
      @Override
      public void onSuccess(String response) {
        GapCashLoadResp resp = mGson.fromJson(response, GapCashLoadResp.class);
        if (!DataUtil.checkResponseSuccess(resp)) {
          LogUtils.i(TAG, "gapCashLoad: " + resp.getResponseDesc());
          callback.onLoadFailed();
          return;
        }
        String creditForLoadAPDU = resp.getLoadData();
        try {
          mCard.transmit(creditForLoadAPDU);
        } catch (CardException e) {
          callback.onLoadFailedGapReversal();
          return;
        }
        callback.onLoadSuccess();
        LogUtils.i(TAG, "------------------------圈存完成----------------------------");
      }

      @Override
      public void onError(Throwable throwable) {
        LogUtils.e(TAG, "gapCashLoad: ", throwable);
        callback.onLoadFailed();
      }
    });
  }

  @Override
  public void gapReversal(final GapReversalCallback callback) {
    GapReversalReq gapReversalReq = new GapReversalReq();
    gapReversalReq.setLoadTransNum(app.getLoadTransNum());
    gapReversalReq.setCardNo(mCardNo);
    //gapReversalReq.setTeller(PAY_ACCOUNT);
    gapReversalReq.setGapNum(gapNum);
    gapReversalReq.setGapCount(gapCountStr);
    gapReversalReq.setGapCash(gapCash);
    gapReversalReq.setTransTime(DateUtil.getDateTime(TRANS_TIME_DATA_FORMAT, new Date()));
    gapReversalReq.setBranchNo(BRANCH_NO);
    gapReversalReq.setCardProvider(CARD_PROVIDER);
    try {
      gapReversalReq.setRandomNum(mCard.transmit(GET_RANDOM_4).getData());
    } catch (CardException e) {
      LogUtils.e(TAG, "gapReversal: ", e);
      return;
    }
    gapReversalReq.setWgCash(wgCash);
    gapReversalReq.setWgStartTime(wgStartTime);
    gapReversalReq.setWgEndTime(wgEndTime);
    gapReversalReq.setCardSequence(app.getATR());
    reqJson = mGson.toJson(gapReversalReq);
    //冲正请求
    getRequestManager().post(SERVER_URL, reqJson, new IRequestCallback() {
      @Override
      public void onSuccess(String response) {
        GapReversalResp resp = mGson.fromJson(response, GapReversalResp.class);
        if (!DataUtil.checkResponseSuccess(resp)) {
          LogUtils.i(TAG, "gapReversal: " + resp.getResponseDesc());
          callback.onGapReversalFailed();
          return;
        }
        //String cardInfo = resp.getCardInfo();
        //写卡失败
        //sendLoadResult(LoadStatusEnum.ReverseFaild);
        callback.onGapReversalSuccess();
      }

      @Override
      public void onError(Throwable throwable) {
        LogUtils.e(TAG, "gapReversal: ", throwable);
      }
    });
  }

  @Override
  public void loadResultNotice(LoadStatusEnum loadState) {
    LoadResultNoticeReq loadResultNoticeReq = new LoadResultNoticeReq();
    loadResultNoticeReq.setLoadTransNum(app.getLoadTransNum());
    loadResultNoticeReq.setLoadState(loadState.getCode());
    reqJson = mGson.toJson(loadResultNoticeReq);
    //圈存结果通知
    getRequestManager().post(SERVER_URL, reqJson, new IRequestCallback() {
      @Override
      public void onSuccess(String response) {
      }

      @Override
      public void onError(Throwable throwable) {
      }
    });
  }

  @Override
  public int getBalance() {
    int balance = -1;
    try {
      balance = Integer.parseInt(mCardUtil.getBalance());
    } catch (CardException e) {
      LogUtils.e(TAG, "getBalance: ", e);
    }
    return balance;
  }

}
