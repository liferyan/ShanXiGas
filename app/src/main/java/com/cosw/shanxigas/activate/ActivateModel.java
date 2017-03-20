package com.cosw.shanxigas.activate;

import static com.cosw.shanxigas.util.Constant.BRANCH_NO;
import static com.cosw.shanxigas.util.Constant.CARD_PROVIDER;
import static com.cosw.shanxigas.util.Constant.GET_RANDOM_4;
import static com.cosw.shanxigas.util.Constant.GET_RANDOM_8;
import static com.cosw.shanxigas.util.Constant.SERVER_URL;
import static com.cosw.shanxigas.util.Constant.READ_CARD_NO;
import static com.cosw.shanxigas.util.Constant.SELECT_3F01;
import static com.cosw.shanxigas.util.Constant.SELECT_CARD_NO_FILE;
import static com.cosw.shanxigas.util.Constant.TRANS_TIME_DATA_FORMAT;

import android.text.TextUtils;
import com.cosw.protocol.req.CardBindReq;
import com.cosw.protocol.req.QueryCardInfoForBindReq;
import com.cosw.protocol.req.QueryCardKeyReq;
import com.cosw.protocol.req.UserInfoInitReq;
import com.cosw.protocol.resp.CardBindResp;
import com.cosw.protocol.resp.QueryCardInfoForBindResp;
import com.cosw.protocol.resp.QueryCardKeyResp;
import com.cosw.protocol.resp.UserInfoInitResp;
import com.cosw.shanxigas.app.MyApplication;
import com.cosw.shanxigas.card.CardException;
import com.cosw.shanxigas.card.CardUtil;
import com.cosw.shanxigas.card.ICard;
import com.cosw.shanxigas.util.DataUtil;
import com.cosw.shanxigas.util.GapTypeEnum;
import com.cosw.shanxigas.util.LogUtils;
import com.cosw.shanxigas.util.net.IRequestManager.IRequestCallback;
import com.cosw.shanxigas.util.net.RequestFactory;
import com.google.gson.Gson;

/**
 * Created by Ryan on 2017/1/11.
 */

public class ActivateModel implements ActivateContract.Model {

  private static final String TAG = "ActivateModel";

  private static final int QUERY_CARD_KEY_COUNT = 7;

  private MyApplication app;

  private ICard mCard;

  private CardUtil mCardUtil;

  //密钥次数计数器
  private int mKeyCount;
  //密钥组号
  private String mKeyGroup;
  //随机数
  private String mRandomNum;
  //交易流水号
  private String mTransNum;

  private IRequestCallback requestCallback;

  private Gson mGson;

  private String reqJson;

  //通过NFC读取Tag方式获得的燃气卡号
  private String mCardNo;

  private String mUserName;

  private ActivateModel() {
    app = MyApplication.getInstance();
    mCard = app.getCard();
    mCardUtil = CardUtil.getInstance(mCard);
    mTransNum = DataUtil.generateTransNum(TRANS_TIME_DATA_FORMAT);
    mGson = DataUtil.getGson();
  }

  public static ActivateModel getInstance() {
    return new ActivateModel();
  }

  @Override
  public void loadCardInfo(String cardNo, final QueryCardInfoForBindCallback callback) {
    mCardNo = cardNo;
    QueryCardInfoForBindReq req = new QueryCardInfoForBindReq();
    req.setCardNo(mCardNo);
    req.setBranchNo(BRANCH_NO);
    //req.setPayAccount(app.getAccount());
    reqJson = mGson.toJson(req);
    RequestFactory.getRequestManager().post(SERVER_URL, reqJson, new IRequestCallback() {
      @Override
      public void onSuccess(String response) {
        QueryCardInfoForBindResp resp = mGson.fromJson(response, QueryCardInfoForBindResp.class);
        if (!DataUtil.checkResponseSuccess(resp)) {
          LogUtils.i(TAG, "loadCardInfo: " + resp.getResponseDesc());
          callback.onQueryCardInfoForBindFailed();
          return;
        }
        String cardNo = resp.getCardNo();
        mUserName = resp.getUserName();
        String address = resp.getStreet() + resp.getVillageName() + resp.getFloorNo()
            + resp.getUnitNo() + resp.getFloor() + resp.getRoomNo();
        String gapType = GapTypeEnum.getEnumName(resp.getGapType());
        callback.onQueryCardInfoForBindSuccess(cardNo, mUserName, address, gapType);
      }

      @Override
      public void onError(Throwable throwable) {
        LogUtils.e(TAG, "loadCardInfo: ", throwable);
        callback.onQueryCardInfoForBindFailed();
      }
    });
  }

  @Override
  public void queryCardKey(QueryCardKeyCallback callback) {
    LogUtils.i(TAG, "---------------------------开始替换秘钥-------------------------");
    mKeyCount = 1;
    try {
      mCard.cardReset();
      mCard.transmit(SELECT_3F01);
      mKeyGroup = String.valueOf(mKeyCount);
      mRandomNum = mCard.transmit(GET_RANDOM_8).getData();
    } catch (CardException e) {
      LogUtils.e(TAG, "queryCardKey: ", e);
      callback.onActivateFailed();
      return;
    }
    QueryCardKeyReq req = new QueryCardKeyReq();
    req.setCardNo(mCardNo);
    req.setBranchNo(BRANCH_NO);
    req.setAtr(app.getATR());
    req.setKeyGroupNo(mKeyGroup);
    req.setRandomNum(mRandomNum);
    req.setTransNum(mTransNum);
    //req.setPayAccount(app.getAccount());
    reqJson = mGson.toJson(req);
    requestCallback = new QueryCardKeyRequestCallback(callback);
    RequestFactory.getRequestManager()
        .post(SERVER_URL, reqJson, requestCallback);
  }

  @Override
  public void userInfoInit(final UserInfoInitCallback callback) {
    LogUtils.i(TAG, "-------------------------开始用户信息初始化---------------------------");
    String random;
    try {
      //0084000008
      mCard.cardReset();
      mCard.transmit(SELECT_3F01);
      random = mCard.transmit(GET_RANDOM_8).getData();
    } catch (CardException e) {
      LogUtils.e(TAG, "userInfoInit: ", e);
      callback.onActivateFailed();
      return;
    }
    UserInfoInitReq req = new UserInfoInitReq();
    req.setCardNo(mCardNo);
    req.setBranchNo(BRANCH_NO);
    req.setAtr(app.getATR());
    req.setKeyGroupNo("1");
    req.setRandomNum(random);
    //req.setPayAccount(app.getAccount());
    reqJson = mGson.toJson(req);
    RequestFactory.getRequestManager().post(SERVER_URL, reqJson, new IRequestCallback() {
      @Override
      public void onSuccess(String response) {
        UserInfoInitResp resp = mGson.fromJson(response, UserInfoInitResp.class);
        if (!DataUtil.checkResponseSuccess(resp)) {
          LogUtils.i(TAG, "userInfoInit: " + resp.getResponseDesc());
          callback.onActivateFailed();
          return;
        }
        String[] apduList = resp.getApduStream().split("\\|");
        try {
          for (String apdu : apduList) {
            mCard.transmit(apdu);
          }
        } catch (CardException e) {
          LogUtils.e(TAG, "userInfoInit: ", e);
          callback.onActivateFailed();
          return;
        }
        try {
          //查询SIM卡燃气卡号
          mCard.cardReset();
          mCard.transmit(SELECT_3F01);
          mCard.transmit(SELECT_CARD_NO_FILE);
          String cardNo = mCard.transmit(READ_CARD_NO).getData();
          if (TextUtils.isEmpty(cardNo) || !cardNo.equals(mCardNo)) {
            throw new CardException("燃气卡号错误!");
          }
        } catch (CardException e) {
          LogUtils.e(TAG, "userInfoInit: ", e);
          callback.onActivateFailed();
          return;
        }
        callback.onUserInfoInitSuccess();
        LogUtils.i(TAG, "-------------------------用户信息初始化完成---------------------------");
      }

      @Override
      public void onError(Throwable throwable) {
        LogUtils.e(TAG, "userInfoInit: ", throwable);
        callback.onActivateFailed();
      }
    });

  }

  @Override
  public void cardBind(final CardBindCallback callback) {
    LogUtils.i(TAG, "------------------------开始卡片绑定----------------------------");
    CardBindReq req = new CardBindReq();
    req.setCardNo(mCardNo);
    req.setBranchNo(BRANCH_NO);
    req.setCardProvider(CARD_PROVIDER);
    req.setIdNo("");
    req.setIdType("");
    req.setUsername("");
    req.setAtr(app.getATR());
    req.setTelephone("");
    req.setPayAccount("");
    reqJson = mGson.toJson(req);
    RequestFactory.getRequestManager().post(SERVER_URL, reqJson, new IRequestCallback() {
      @Override
      public void onSuccess(String response) {
        CardBindResp resp = mGson.fromJson(response, CardBindResp.class);
        if (!DataUtil.checkResponseSuccess(resp)) {
          LogUtils.i(TAG, "cardBind: " + resp.getResponseDesc());
          callback.onCardBindFailed();
          return;
        }
        //绑定成功后将卡号写入Application中,后续操作直接从Application中拿
        app.setCardNo(mCardNo);
        callback.onCardBindSuccess();
        LogUtils.i(TAG, "------------------------卡片绑定完成----------------------------");
      }

      @Override
      public void onError(Throwable throwable) {
        LogUtils.e(TAG, "cardBind: ", throwable);
        callback.onCardBindFailed();
      }
    });
  }

  /**
   * 查询卡密钥
   */
  private class QueryCardKeyRequestCallback implements
      IRequestCallback {

    private QueryCardKeyCallback mCallback;

    QueryCardKeyRequestCallback(QueryCardKeyCallback callback) {
      mCallback = callback;
    }

    @Override
    public void onSuccess(String response) {
      QueryCardKeyResp resp = mGson.fromJson(response, QueryCardKeyResp.class);
      if (!DataUtil.checkResponseSuccess(resp)) {
        LogUtils.i(TAG, "QueryCardKeyRequestCallback: " + resp.getResponseCode());
        mCallback.onActivateFailed();
        return;
      }
      String apdu = resp.getKey();
      try {
        mCard.transmit(apdu);
      } catch (CardException e) {
        LogUtils.e(TAG, "QueryCardKeyRequestCallback: ", e);
        mCallback.onActivateFailed();
        return;
      }
      mKeyCount++;
      if (mKeyCount <= QUERY_CARD_KEY_COUNT) {
        mKeyGroup = String.valueOf(mKeyCount);
        try {
          //两次取4字节随机数
          mRandomNum = mCard.transmit(GET_RANDOM_4).getData() +
              mCard.transmit(GET_RANDOM_4).getData();

          QueryCardKeyReq req = new QueryCardKeyReq();
          req.setCardNo(mCardNo);
          req.setBranchNo(BRANCH_NO);
          req.setAtr(app.getATR());
          req.setKeyGroupNo(mKeyGroup);
          req.setRandomNum(mRandomNum);
          req.setTransNum(mTransNum);
          //req.setPayAccount(app.getAccount());
          reqJson = mGson.toJson(req);
          RequestFactory.getRequestManager().post(SERVER_URL, reqJson, requestCallback);
        } catch (CardException e) {
          LogUtils.e(TAG, "QueryCardKeyRequestCallback: ", e);
          mCallback.onActivateFailed();
        }
      } else {
        mCallback.onQueryCardKeySuccess();
        LogUtils.i(TAG, "------------------------替换秘钥完成----------------------------");
      }
    }

    @Override
    public void onError(Throwable throwable) {
      LogUtils.e(TAG, "QueryCardKeyRequestCallback: ", throwable);
      mCallback.onActivateFailed();
    }
  }
}
