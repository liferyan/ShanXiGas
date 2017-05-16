package com.cosw.shanxigas.entity;

/**
 * Created by Ryan on 2017/1/16.
 * 交易明细
 */

public class TrxDetail {

  private String cardNo;
  private String orderNo;
  private String payNo;
  private String amount;
  private String redAmt;
  private String vchAmt;
  private String potChgAmt;
  private String mobile;
  private String wgCash;
  private String wgStartTime;
  private String wgEndTime;
  private String status;
  private String createTime;
  private String updateTime;

  public String getCardNo() {
    return cardNo;
  }

  public void setCardNo(String cardNo) {
    this.cardNo = cardNo;
  }

  public String getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(String orderNo) {
    this.orderNo = orderNo;
  }

  public String getPayNo() {
    return payNo;
  }

  public void setPayNo(String payNo) {
    this.payNo = payNo;
  }

  public String getAmount() {
    return amount;
  }

  public void setAmount(String amount) {
    this.amount = amount;
  }

  public String getRedAmt() {
    return redAmt;
  }

  public void setRedAmt(String redAmt) {
    this.redAmt = redAmt;
  }

  public String getVchAmt() {
    return vchAmt;
  }

  public void setVchAmt(String vchAmt) {
    this.vchAmt = vchAmt;
  }

  public String getPotChgAmt() {
    return potChgAmt;
  }

  public void setPotChgAmt(String potChgAmt) {
    this.potChgAmt = potChgAmt;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getWgCash() {
    return wgCash;
  }

  public void setWgCash(String wgCash) {
    this.wgCash = wgCash;
  }

  public String getWgStartTime() {
    return wgStartTime;
  }

  public void setWgStartTime(String wgStartTime) {
    this.wgStartTime = wgStartTime;
  }

  public String getWgEndTime() {
    return wgEndTime;
  }

  public void setWgEndTime(String wgEndTime) {
    this.wgEndTime = wgEndTime;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getCreateTime() {
    return createTime;
  }

  public void setCreateTime(String createTime) {
    this.createTime = createTime;
  }

  public String getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(String updateTime) {
    this.updateTime = updateTime;
  }
}
