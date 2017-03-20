package com.cosw.shanxigas.util;

import android.content.Context;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

  private static final String HexCode[] = {"0", "1", "2", "3", "4", "5", "6",
      "7", "8", "9", "A", "B", "C", "D", "E", "F"};

  public static byte[] getBCD(String phone) {
    if (phone.length() % 2 > 0) {
      phone = phone + "F";
    }
    byte[] result = new byte[phone.length() / 2];
    for (int i = 0; i < result.length; ++i) {
      int x = Integer.parseInt(phone.substring(i * 2, i * 2 + 2), 16);
      result[i] = x <= 127 ? (byte) x : (byte) (x - 256);
    }
    return result;
  }

  public static String byteArrayToHexString(byte b[], int offset, int size) {
    if (b == null) {
      return null;
    }
    String result = "";
    for (int i = offset; i < offset + size; i++) {
      result = result + byteToHexString(b[i]);
    }
    return result;
  }

  public static String byteArrayToHexString(byte b[]) {
    if (b == null) {
      return null;
    }
    String result = "";
    for (byte aB : b) {
      result = result + byteToHexString(aB);
    }
    return result;
  }

  public static String byteArrayWithLenToHexString(byte b[], int off, int len) {
    if (b == null) {
      return null;
    }
    String result = "";
    for (int i = 0; i < len; i++) {
      result = result + byteToHexString(b[off + i]);
    }
    return result;
  }

  public static byte[] hexStringToByteArray(String text) {
    if (text == null) {
      return null;
    }
    byte[] result = new byte[text.length() / 2];
    for (int i = 0; i < result.length; ++i) {
      int x = Integer.parseInt(text.substring(i * 2, i * 2 + 2), 16);
      result[i] = x <= 127 ? (byte) x : (byte) (x - 256);
    }
    return result;
  }

  public static byte[] hexStringToAsciiByteArray(String text, int byteLen) {
    if (text == null) {
      return null;
    }
    byte[] result = new byte[(byteLen > text.length()) ? byteLen : text
        .length()];

    for (int i = 0; i < text.length(); i++) {
      int x = Integer.parseInt(text.substring(i, i + 1), 16);
      result[i] = (byte) ((byte) 0x30 + x);
    }

    return result;
  }

  public static String hexStringToString(String hexString, String charSet) {
    if (hexString == null) {
      return null;
    }
    String result = "";
    try {
      result = new String(hexStringToByteArray(hexString), charSet);
    } catch (Exception ex) {
    }
    return result;
  }

  public static String hexStringToAsciiString(String hexString) {
    return hexStringToString(hexString, "ASCII");
  }

  public static String byteToHexString(byte b) {
    int n = b;
    if (n < 0) {
      n = 256 + n;
    }
    int d1 = n / 16;
    int d2 = n % 16;
    return HexCode[d1] + HexCode[d2];
  }

  public static String longMoney2String(long money) {
    float m = ((float) money) / 100;
    return String.valueOf(m) + "元";
  }

  /**
   * "3231" -> 21
   */
  public static String ascNumStr2HexStr(String ascStr) {
    StringBuilder sb = new StringBuilder();

    if ((ascStr.length() % 2) == 0) {
      for (int i = 0; i < ascStr.length(); i += 2) {
        sb.append(ascStr.substring(i + 1, i + 2));
      }
    }

    return sb.toString();
  }

  /*
   * 电信号段:133/153/180/181/189/177； 联通号段:130/131/132/155/156/185/186/145/176；
   * 移动号段
   * ：134/135/136/137/138/139/150/151/152/157/158/159/182/183/184/187/188/147
   * /178。
   */
  public static boolean isMobileNO(String mobiles) {
    Pattern p = Pattern
        .compile(
            "^((1[3,8]\\d{9}$)|(15[0,1,2,3,5,6,7,8,9]\\d{8}$)|(14[5,7]\\d{8}$)|(17[6,7,8]\\d{8}$))");

    Matcher m = p.matcher(mobiles);

    return m.matches();
  }

  public static String formateDateNow(String formate) {
    SimpleDateFormat sdf = new SimpleDateFormat(formate, Locale.CHINA);
    return sdf.format(new Date());
  }

  public static int dip2px(Context context, float dipValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dipValue * scale + 0.5f);
  }

  /**
   * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
   */
  public static int px2dip(Context context, float pxValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (pxValue / scale + 0.5f);
  }

  public static boolean isStringEmpty(String str) {
    if (str != null && str.length() > 0) {
      if (str.equals("null")) {
        return true;
      } else {
        return false;
      }
    }

    return true;
  }

  /**
   * 若str长度超过showLen, 显示str前showLen字符，后面补...
   */
  public static String strOmitLast3Chars(String str, int showLen) {
    if (str.length() <= showLen) {
      return str;
    } else {
      return str.substring(0, (showLen > 3) ? (showLen - 3) : showLen)
          + "...";
    }
  }

  /**
   * 15000680047 -> 150****0047
   */
  public static String strOmitMiddleChars(String str, int lastId, int omits) {
    if (str.length() < (lastId + omits)) {
      return str;
    } else { // xxxx***xxx
      StringBuilder sb = new StringBuilder();
      sb.append(str.substring(0, str.length() - lastId - omits));

      for (int i = 0; i < omits; i++) {
        sb.append("*");
      }

      sb.append(str.substring(str.length() - lastId));

      return sb.toString();
    }
  }
}
