package com.cosw.shanxigas.card;

import android.nfc.tech.IsoDep;
import com.cosw.shanxigas.entity.APDUReturn;
import com.cosw.shanxigas.util.LogUtils;
import com.cosw.shanxigas.util.StringUtil;
import java.io.IOException;

/**
 * Created by Ryan on 2016/12/22.
 */

public class CardImplUseNfcTag implements ICard {

  private static final String TAG = "CardImplUseNfcTag";

  // 声明ISO-DEP协议的Tag操作实例
  private final IsoDep tag;

  public CardImplUseNfcTag(IsoDep tag) throws IOException {
    // 初始化ISO-DEP协议的Tag操作类实例
    this.tag = tag;
    tag.setTimeout(5000);
    tag.connect();
  }

  @Override
  public APDUReturn transmit(String apdu) throws CardException {
    LogUtils.i(TAG, "-> " + apdu);
    byte[] apdus = StringUtil.hexStringToByteArray(apdu);
    byte[] resp;
    APDUReturn apduReturn = new APDUReturn();
    try {
      resp = tag.transceive(apdus);
    } catch (IOException e) {
      LogUtils.e(TAG, "sendAPDU: ", e);
      throw new CardException(e.getMessage());
    }
    if (resp != null) {
      String response = StringUtil.byteArrayToHexString(resp).replace(" ", "");
      LogUtils.i(TAG, "<- " + response);
      String data, swStr;
      int off = response.length() - 4;
      if (off >= 0) {
        data = response.substring(0, off).replace(" ", "");
        apduReturn.setData(data);
        swStr = response.substring(off, response.length()).replace(" ", "");
      } else {
        swStr = response.replace(" ", "");
      }
      apduReturn.setSw(Integer.parseInt(swStr, 16));
      if (apduReturn.getSw() != 0x9000) {
        throw new CardException("指令未返回9000");
      }
    }
    return apduReturn;
  }

  @Override
  public void cardReset() throws CardException {
    throw new CardException("Stub");
  }
}
