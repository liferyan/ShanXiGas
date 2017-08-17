package com.cosw.shanxigas.card;

import android.text.TextUtils;
import com.cosw.shanxigas.app.MyApplication;
import com.cosw.shanxigas.entity.APDUReturn;
import com.cosw.shanxigas.util.ATRParseUtil;
import com.cosw.shanxigas.util.LogUtils;
import com.cosw.shanxigas.util.StringUtil;
import org.simalliance.openmobileapi.Channel;
import org.simalliance.openmobileapi.Reader;
import org.simalliance.openmobileapi.SEService;
import org.simalliance.openmobileapi.Session;

/**
 * Created by Ryan on 2016/12/5.
 */

public class CardImplUseOpenMobile implements ICard {

  private String TAG = "APDU";

  private MyApplication app;

  private static CardImplUseOpenMobile INSTANCE;
  private SEService mSEService;
  private Reader[] readers;
  private Channel channel;
  private String aidStr;

  private CardImplUseOpenMobile(SEService seService, String aid) throws CardException {
    app = MyApplication.getInstance();
    aidStr = aid;
    mSEService = seService;
    connectToSim(mSEService, aidStr);
  }

  public static CardImplUseOpenMobile getInstance(SEService mSEService, String aid)
      throws CardException {
    if (INSTANCE == null) {
      INSTANCE = new CardImplUseOpenMobile(mSEService, aid);
    }
    return INSTANCE;
  }

  private void connectToSim(SEService seService, String aidStr) throws CardException {
    byte[] aid = StringUtil.hexStringToByteArray(aidStr);
    readers = seService.getReaders();
    if (readers.length == 0) {
      throw new CardException("没有可用的reader");
    }
    Reader simReader = null;
    for (Reader reader : readers) {
      LogUtils.i(TAG, "readers's name: " + reader.getName());
      if (reader.getName().startsWith("SIM")) {
        simReader = reader;
        break;
      }
    }
    if (simReader == null) {
      throw new CardException("No Sim Reader");
    }
    try {
      Session session = simReader.openSession();
      if (session == null) {
        throw new CardException("不能打开Session");
      }
      if (app.getATR() == null || app.getATR().length() == 0) {
        byte atr[] = session.getATR();
        String atrStr = ATRParseUtil.parseATR(atr);
        //atr未获取到 当前机型不支持天然气插件
        if (atrStr == null) {
          throw new CardException();
        }
        LogUtils.i(TAG, "atrStr:" + atrStr);
        if (TextUtils.isEmpty(atrStr)) {
          throw new CardException("获取卡片ATR失败");
        }
        app.setATR(atrStr);
      }
      if (channel != null && !channel.isClosed()) {
        channel.close();
      }
      channel = session.openLogicalChannel(aid);
    } catch (Exception e) {
      throw new CardException(e.getMessage());
    }
  }

  @Override
  public void cardReset() throws CardException {
    /*if (readers.length != 0) {
      readers[0].closeSessions();
    }
    connectToSim(mSEService, aidStr);*/
  }

  @Override
  public APDUReturn transmit(String apdu) throws CardException {
    if (channel == null) {
      throw new CardException("Channel create failed");
    }
    //LogUtils.i(TAG, "-> " + apdu);
    LogUtils.i(TAG, "-> " + apdu);
    byte[] rsp;
    APDUReturn apduReturn = new APDUReturn();
    try {
      rsp = channel.transmit(StringUtil.hexStringToByteArray(apdu));
    } catch (Exception e) {
      throw new CardException(e.getMessage());
    }
    if (rsp != null) {
      String response = StringUtil.byteArrayToHexString(rsp);
      //LogUtils.i(TAG, "<- " + response);
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
}
