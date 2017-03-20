package com.cosw.shanxigas.util;

/**
 * Created by Ryan on 2016/12/18.
 */

public enum GapTypeEnum {
  Ordinary("1", "普通表"),
  Special("2", "非接触金额表");

  private String code;
  private String name;

  GapTypeEnum(String code, String name) {
    this.code = code;
    this.name = name;
  }

  public static String getEnumName(String code) {
    GapTypeEnum[] arr = values();
    int len = arr.length;

    for (GapTypeEnum item : arr) {
      if (item.getCode().equals(code)) {
        return item.name;
      }
    }

    return "";
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
