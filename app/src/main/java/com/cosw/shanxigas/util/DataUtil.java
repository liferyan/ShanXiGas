package com.cosw.shanxigas.util;

import com.cosw.protocol.resp.MessageResponse;
import com.cosw.protocol.util.DateUtil;
import com.google.gson.Gson;
import java.util.Date;
import java.util.Random;

/**
 * Created by Ryan on 2017/1/11.
 */

public class DataUtil {

  private static Gson sGson;

  private DataUtil() {
  }

  public static boolean checkResponseSuccess(MessageResponse response) {
    return "00".equals(response.getResponseCode());
  }

  private static String randomNumeric(int numLen) {
    final int maxNum;
    int ranNum;
    int count = 0;
    char[] str = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    maxNum = str.length;
    StringBuilder sb = new StringBuilder();
    Random random = new Random();
    while (count < numLen) {
      ranNum = Math.abs(random.nextInt(maxNum));
      if (ranNum >= 0 && ranNum < str.length) {
        sb.append(str[ranNum]);
        count++;
      }
    }
    return sb.toString();
  }

  /**
   * 生成交易流水号
   */
  public static String generateTransNum(String dataFormat) {
    return DateUtil.getDateTime(dataFormat, new Date()) + randomNumeric(10);
  }

  public static Gson getGson() {
    if (sGson == null) {
      sGson = new Gson();
    }
    return sGson;
  }

}
