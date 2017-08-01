package com.cosw.shanxigas.util;

/**
 * @author Administrator ATR判断是否存在检验字节 参考:<br/> (1)http://www.ruimtools.com/atr.php<br/>
 *         (2)https://en.wikipedia.org/wiki/Answer_to_reset<br/> ATR（TCK存在，需要校验，此时有的手机能拿到20校验位I，有的手机不行）<br/>
 *         华为p10 ------atr=3B9F95803FC7A08031E073FE211B633A104E83009000<br/> 华为 honor
 *         ------atr=3B9F95803FC7A08031E073FE211B633A104E83009000 20<br/> 中国移动M812C
 *         ------atr=3B9F95803FC7A08031E073FE211B633A104E83009000 20 atr:3B9194803FC3A0BC65<br/>
 *         ATR（TCK不存在，不需要校验）：3B9A96401E4100014308025054E20D<br/>
 */
public class ATRParseUtil {

  public static void main(String[] args) {
    /*System.out.println(parseATR(StringUtil
				.hexStringToByteArray("3B9194803FC3A0BC65")));*/

    try {
      int atrZero = Integer.valueOf("0000000000");
    } catch (Exception e) {
      System.out.println("不为全0");
    }
    System.out.println("全0");
  }

  // 历史字节数
  private static int historical_len = 0;
  // 已经校验的字节数
  private static int info_recv_len = 1;

  public static String parseATR(byte[] atr) {

    //(1)部分支持NFC机型获取ATR为null，可能底层没有缓存卡片ATR
    if (atr == null || atr.length == 0) {
      return null;
    }

    //(2)ATR为全0也无法做个人化，代表机型：
    //(1)OPPO N5207
    try {
      int atrZero = Integer.valueOf(StringUtil.byteArrayToHexString(atr));
      if (atrZero == 0) {
        System.out.println("获取ATR为全0,不支持");
        return null;
      }
    } catch (Exception e) {
    }

    int ta_b = 0;
    int tb_b = 0;
    int tc_b = 0;
    int td_b = 0;
    int tck_exist = 0;
    int ta1_exist = 0;

    // 每次计算之前从新初始化
    historical_len = 0;
    info_recv_len = 1;
    info_recv_len++;
    ta_b = (atr[info_recv_len - 1] >> 4) & 0x01;
    tb_b = (atr[info_recv_len - 1] >> 5) & 0x01;
    tc_b = (atr[info_recv_len - 1] >> 6) & 0x01;
    td_b = (atr[info_recv_len - 1] >> 7) & 0x01;
    // 格式字节T0 在范围[0..15] 中以其4个低位（第4个MSbit到第1个LSbit）编码历史字节T i的数量K
    // 示例1：3B9194803FC3A0BC65:91->10010001 ,此时历史字节数为1
    // 示例2：3B9F95803FC7A08031E073FE211B633A104E8300900020：9F->10011111,,此时历史字节数为15
    historical_len = atr[info_recv_len - 1] & 0x0F;
    System.out.println(atr[info_recv_len - 1]);

    if (ta_b != 0) {
      ta1_exist = 1;
    }

    boolean flag = isTCKExist(ta_b, tb_b, tc_b, td_b, ta1_exist, tck_exist,
        atr);
    System.out.println("是否存在TCK校验位：" + flag);
    System.out.println("info_recv_len：" + info_recv_len);
    System.out.println("historical_len：" + historical_len);
    if (flag) {// 存在TCK校验位
      byte[] atrCopy = new byte[historical_len + info_recv_len];
      System.arraycopy(atr, 0, atrCopy, 0, atrCopy.length);
      return StringUtil.byteArrayToHexString(atrCopy);
    } else {// 不存在TCK校验位
      return StringUtil.byteArrayToHexString(atr);
    }

  }

  /**
   * 是否存在TCK校验位
   */
  private static boolean isTCKExist(int ta_b, int tb_b, int tc_b, int td_b,
      int ta1_exist, int tck_exist, byte atr[]) {
    if (ta_b != 0) {
      ta_b = 0;
      info_recv_len++;
      if (ta1_exist == 1) {
        ta1_exist = 0;
      }
      return isTCKExist(ta_b, tb_b, tc_b, td_b, ta1_exist, tck_exist, atr);
    } else if (tb_b != 0) {
      tb_b = 0;
      info_recv_len++;
      return isTCKExist(ta_b, tb_b, tc_b, td_b, ta1_exist, tck_exist, atr);
    } else if (tc_b != 0) {
      tc_b = 0;
      info_recv_len++;
      return isTCKExist(ta_b, tb_b, tc_b, td_b, ta1_exist, tck_exist, atr);
    } else if (td_b != 0) {
      td_b = 0;
      info_recv_len++;
      System.out.println("td_b:"
          + StringUtil.byteToHexString(atr[info_recv_len - 1]));
      System.out.println("td_b:" + atr[info_recv_len - 1]);
      ta_b = (atr[info_recv_len - 1] >> 4) & 0x01;
      tb_b = (atr[info_recv_len - 1] >> 5) & 0x01;
      tc_b = (atr[info_recv_len - 1] >> 6) & 0x01;
      td_b = (atr[info_recv_len - 1] >> 7) & 0x01;
      System.out.println(atr[info_recv_len - 1] & 0x0F);
      if ((atr[info_recv_len - 1] & 0x0F) != 0x00) {
        tck_exist = 1;
      }
      return isTCKExist(ta_b, tb_b, tc_b, td_b, ta1_exist, tck_exist, atr);
    }
    return tck_exist != 0 ? true : false;
  }
}
