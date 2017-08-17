package com.cosw.shanxigas.util;

public final class Constant {

  //Network Related
  /*URL= "http://120.192.246.66:9092/airdeposit-trans/service/biz.htm";*/
  private static final String SERVER_IP_PORT = "120.192.246.66:9092";
  //  private static final String SERVER_IP_PORT = "192.168.199.185:9092";
  public static final String SERVER_URL =
      "http://" + SERVER_IP_PORT + "/airdeposit-trans/service/biz.htm";

  //Card Related
  //public static final String GAS_AID = "315041592E5359532E4444463033";
  public static final String GAS_AID = "112233445566778899";
  public static final String GET_RANDOM_4 = "0084000004";
  public static final String GET_RANDOM_8 = "0084000008";
  public static final String SELECT_3F01 = "00a40000023f01";
  public static final String SELECT_CARD_NO_FILE = "00a40000020021";
  public static final String READ_CARD_NO = "00b0000005";
  //读余额
  public static final String CARD_INFO1 = "805c000204";
  //读阶梯单价
  public static final String CARD_INFO2 = "00b08A0089";
  //读表参数信息
  public static final String CARD_INFO3 = "00b088000F";
  //读表反写信息
  public static final String CARD_INFO4 = "00b08300B9";
  //读累计消耗量
  public static final String CARD_INFO5 = "00b08500BD";
  //读购气次数
  public static final String GAP_COUNT = "00b0840201";

  public static final String CARD_PROVIDER = "04";
  public static final String BRANCH_NO = "YZF01";

  public static final String TRANS_TIME_DATA_FORMAT = "yyyy-MM-dd HH:mm:ss";
  public static final String LOAD_TRANS_NUM_DATA_FORMAT = "yyyyMMddHHmmss";

  public static final String WAIT_PAY = "待支付";
  public static final String HAS_CANCEL = "已撤销";

  public static final String INITIAL_CARD_NO = "0000000000";
  public static final String INITIAL_ACCOUNT = "00000000000";


  public static final String EXTRA_ONLY_BIND = "com.cosw.shanxigas.EXTRA_ONLY_BIND";
  public static final String EXTRA_CARD_NO = "com.cosw.shanxigas.EXTRA_CARD_NO";
  public static final String EXTRA_URL = "com.cosw.shanxigas.EXTRA_URL";
  public static final String EXTRA_METHOD = "com.cosw.shanxigas.EXTRA_METHOD";
  public static final String EXTRA_SESSION_ID = "com.cosw.shanxigas.EXTRA_SESSION_ID";
  public static final String EXTRA_ORDER_NO = "com.cosw.shanxigas.EXTRA_ORDER_NO";
  public static final String EXTRA_STATUS = "com.cosw.shanxigas.EXTRA_STATUS";
  public static final String EXTRA_LOAD_AMOUNT = "com.cosw.shanxigas.EXTRA_LOAD_AMOUNT";
  public static final String EXTRA_DESC = "com.cosw.shanxigas.EXTRA_DESC";

  public static final String PAY_SUCCESS_STATUS_CODE = "SUCCESS";

  public static final String NETWORK_ERROR_MSG = "服务器连接失败,请稍后再试 !";

  public static final String READ_CARD_FAILED_TO_READ_CARD_MSG = "请使用秦华天然气卡 !";
  public static final String READ_CARD_FAILED_TO_GET_CARD_BIND_STATUS_MSG = "获取卡绑定状态失败,请稍后再试 !";
  public static final String READ_CARD_FAILED_CARD_HAS_BIND_MSG = "请使用本人燃气应用SIM卡 !";

  public static final String ACTIVATE_LOADING_GET_CARD_INFO = "查询卡信息...";
  public static final String ACTIVATE_LOADING_ACTIVATE = "卡片激活中...";
  public static final String ACTIVATE_FAILED_TO_GET_CARD_INFO_MSG = "查询用户信息失败 !";
  public static final String ACTIVATE_FAILED_TO_ACTIVATE_MSG = "激活失败,请与秦华天然气公司联系,谢谢 !";
  public static final String ACTIVATE_FAILED_TO_BIND_MSG = "激活失败,请稍后再试 !";

  public static final String MAIN_FAILED_TO_CHECK_ACCOUNT_MSG = "请先设置默认支付账号 !";

  public static final String LOAD_FAILED_TO_CHECK_MONEY_EMPTY_MSG = "请输入购气金额 !";
  public static final String LOAD_FAILED_TO_CHECK_MONEY_NOT_DIGIT_MSG = "购气金额输入错误,请重新输入 !";
  public static final String LOAD_FAILED_TO_CHECK_MONEY_TOO_BIG_MSG = "购起金额超过限额,请重新输入 !";
  public static final String LOAD_FAILED_TO_GET_CARD_INFO_MSG = "获取卡信息失败 !";
  public static final String LOAD_LOADING_GET_CARD_INFO = "获取卡信息...";
  public static final String LOAD_LOADING_PAY = "圈存中...";
  public static final String LOAD_FAILED_TO_PAY = "圈存失败 !";
  public static final String LOAD_LOADING_LOAD = "圈存中...";
  public static final String LOAD_SUCCESS_MSG = "圈存成功 !";
  public static final String LOAD_FAILED_MSG = "圈存失败 !";
  public static final String PAY_LOADING_TO_WEB_PAGE = "页面跳转...";

  public static final String TRX_STATUS_LOAD_SUCCESS = "圈存成功";
  public static final String TRX_STATUS_REVERSAL_SUCCESS = "冲正成功";
  public static final String TRX_LOADING_QUERY_ORDER = "加载中...";
  public static final String TRX_LOADING_ORDER_CANCEL = "撤销中...";
  public static final String TRX_FAILED_TO_LOADING_MSG = "加载失败 !";
  public static final String TRX_ORDER_CANCEL_SUCCESS_MSG = "撤销成功 !";
  public static final String TRX_ORDER_CANCEL_FAILED_MSG = "撤销失败 !";

  public static final String REFRESH_BALANCE_NOT_ZERO = "请先上表再操作！";
}
