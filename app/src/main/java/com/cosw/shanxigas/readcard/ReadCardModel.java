package com.cosw.shanxigas.readcard;

import static com.cosw.shanxigas.util.Constant.SERVER_URL;
import static com.cosw.shanxigas.util.Constant.READ_CARD_NO;
import static com.cosw.shanxigas.util.Constant.SELECT_3F01;
import static com.cosw.shanxigas.util.Constant.SELECT_CARD_NO_FILE;

import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import com.cosw.protocol.req.QueryCardBindStatusReq;
import com.cosw.protocol.resp.QueryCardBindStatusResp;
import com.cosw.shanxigas.app.MyApplication;
import com.cosw.shanxigas.card.CardException;
import com.cosw.shanxigas.card.CardImplUseNfcTag;
import com.cosw.shanxigas.card.ICard;
import com.cosw.shanxigas.util.DataUtil;
import com.cosw.shanxigas.util.LogUtils;
import com.cosw.shanxigas.util.net.IRequestManager.IRequestCallback;
import com.cosw.shanxigas.util.net.RequestFactory;
import com.google.gson.Gson;
import java.io.IOException;

/**
 * Created by Ryan on 2017/1/11.
 */

public class ReadCardModel implements ReadCardContract.Model {

  private static final String TAG = "ReadCardModel";

  private MyApplication app;

  private Gson mGson;

  private String reqJson;

  private ReadCardModel() {
    app = MyApplication.getInstance();
    mGson = DataUtil.getGson();
  }

  public static ReadCardModel getInstance() {
    return new ReadCardModel();
  }

  @Override
  public void readCardFromTag(Tag tag, ReadCardCallback callback) {
    try {
      //将燃气卡靠近手机背面 NFC读取卡号 读取成功进入激活绑定界面
      ICard mCard = new CardImplUseNfcTag(IsoDep.get(tag));
      mCard.transmit(SELECT_3F01);
      mCard.transmit(SELECT_CARD_NO_FILE);
      String cardNoStr = mCard.transmit(READ_CARD_NO).getData();
      callback.onReadCardSuccess(cardNoStr);
    } catch (CardException | IOException e) {
      LogUtils.e(TAG, "readCardFromTag: ", e);
      callback.onReadCardFailed();
    }
  }

  @Override
  public void queryCardBindStatus(String cardNo,
      final QueryCardBindStatusCallback callback) {
    QueryCardBindStatusReq req = new QueryCardBindStatusReq();
    req.setCardNo(cardNo);
    reqJson = mGson.toJson(req);
    RequestFactory.getRequestManager().post(SERVER_URL, reqJson, new IRequestCallback() {
      @Override
      public void onSuccess(String response) {
        QueryCardBindStatusResp resp = mGson.fromJson(response, QueryCardBindStatusResp.class);
        if (!DataUtil.checkResponseSuccess(resp)) {
          LogUtils.i(TAG, "queryCardBindStatus: " + resp.getResponseDesc());
          callback.onQueryCardBindStatusFailed();
        } else {
          if ("01".equals(resp.getStatus())) {
            //hasBind
            callback.onHasBind(true);
          } else {
            callback.onHasBind(false);
          }
        }
      }

      @Override
      public void onError(Throwable throwable) {
        LogUtils.e(TAG, "queryCardBindStatus: ", throwable);
        callback.onQueryCardBindStatusFailed();
      }
    });
  }
}
