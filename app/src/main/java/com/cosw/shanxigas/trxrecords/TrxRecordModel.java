package com.cosw.shanxigas.trxrecords;

import static com.cosw.shanxigas.util.Constant.SERVER_URL;

import com.cosw.protocol.req.OrderCancelReq;
import com.cosw.protocol.req.QueryOrderDetailReq;
import com.cosw.protocol.req.QueryOrderListReq;
import com.cosw.protocol.resp.OrderCancelResp;
import com.cosw.protocol.resp.QueryOrderDetailResp;
import com.cosw.protocol.resp.QueryOrderListResp;
import com.cosw.shanxigas.app.MyApplication;
import com.cosw.shanxigas.entity.TrxDetail;
import com.cosw.shanxigas.entity.TrxRecords;
import com.cosw.shanxigas.util.DataUtil;
import com.cosw.shanxigas.util.LogUtils;
import com.cosw.shanxigas.util.net.IRequestManager.IRequestCallback;
import com.cosw.shanxigas.util.net.RequestFactory;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Ryan on 2017/1/16.
 */

public class TrxRecordModel implements TrxRecordsContract.Model {

  private static final String TAG = "TrxRecordModel";

  private static TrxRecordModel INSTANCE;
  private MyApplication app;

  private Gson mGson;

  private String reqJson;

  private TrxRecordModel() {
    app = MyApplication.getInstance();
    mGson = new Gson();
  }

  public static TrxRecordModel getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new TrxRecordModel();
    }
    return INSTANCE;
  }

  @Override
  public void loadQueryOrderList(int hasLoadedCount, final LoadQueryOrderListCallback callback) {
    QueryOrderListReq req = new QueryOrderListReq();
    req.setCardNo(app.getCardNo());
    req.setCurrentNum(hasLoadedCount);
    reqJson = mGson.toJson(req);
    RequestFactory.getRequestManager().post(SERVER_URL, reqJson, new IRequestCallback() {
      @Override
      public void onSuccess(String response) {
        QueryOrderListResp resp = mGson.fromJson(response, QueryOrderListResp.class);
        if (!DataUtil.checkResponseSuccess(resp)) {
          LogUtils.i(TAG, "loadQueryOrderList: " + resp.getResponseDesc());
          callback.onLoadQueryOrderListFailed();
          return;
        }
        List<Map<String, Object>> orderMapList = resp.getOrders();
        int orderSize = orderMapList.size();
        //LogUtils.i(TAG, "加载List数量: " + orderSize);
        List<TrxRecords> trxRecordsList = new ArrayList<>();
        TrxRecords trxRecords;
        for (Map<String, Object> map : orderMapList) {
          trxRecords = new TrxRecords();
          trxRecords.setAmount((String) map.get("amount"));
          trxRecords.setOrderNo((String) map.get("orderNo"));
          trxRecords.setCreateTime((String) map.get("createTime"));
          trxRecords.setPayStatus((String) map.get("status"));
          trxRecordsList.add(trxRecords);
        }
        callback.onLoadQueryOrderListSuccess(trxRecordsList);
      }

      @Override
      public void onError(Throwable throwable) {
        LogUtils.e(TAG, "loadQueryOrderList: ", throwable);
        callback.onLoadQueryOrderListFailed();
      }
    });
  }


  @Override
  public void loadQueryOrderDetail(String orderNo, final LoadQueryOrderDetailCallback callback) {
    QueryOrderDetailReq req = new QueryOrderDetailReq();
    req.setCardNo(app.getCardNo());
    req.setOrderNo(orderNo);
    reqJson = mGson.toJson(req);
    RequestFactory.getRequestManager().post(SERVER_URL, reqJson, new IRequestCallback() {
      @Override
      public void onSuccess(String response) {
        QueryOrderDetailResp resp = mGson.fromJson(response, QueryOrderDetailResp.class);
        if (!DataUtil.checkResponseSuccess(resp)) {
          LogUtils.i(TAG, "loadQueryOrderDetail: " + resp.getResponseDesc());
          callback.onLoadQueryOrderDetailFailed();
          return;
        }
        TrxDetail detail = new TrxDetail();
        detail.setAmount(resp.getAmount());
        detail.setBankName(resp.getBankName());
        detail.setCardNo(resp.getCardNo());
        detail.setContractName(resp.getContractName());
        detail.setCreateTime(resp.getCreateTime());
        detail.setInvoiceTitle(resp.getInvoiceTitle());
        detail.setMobile(resp.getMobile());
        detail.setPayNo(resp.getPayNo());
        detail.setPotChgAmt(resp.getPotChgAmt());
        detail.setRedAmt(resp.getRedAmt());
        detail.setVchAmt(resp.getVchAmt());
        detail.setStatus(resp.getStatus());
        detail.setUpdateTime(resp.getUpdateTime());
        detail.setOrderNo(resp.getOrderNo());
        detail.setWgCash(resp.getWgCash());
        detail.setWgStartTime(resp.getWgStartTime());
        detail.setWgEndTime(resp.getWgEndTime());
        callback.onLoadQueryOrderDetailSuccess(detail);
      }

      @Override
      public void onError(Throwable throwable) {
        LogUtils.e(TAG, "loadQueryOrderDetail: ", throwable);
        callback.onLoadQueryOrderDetailFailed();
      }
    });
  }

  @Override
  public void orderCancel(String orderNo, final OrderCancelCallback callback) {
    OrderCancelReq req = new OrderCancelReq();
    req.setOrderNo(orderNo);
    req.setCardNo(app.getCardNo());
    reqJson = mGson.toJson(req);
    RequestFactory.getRequestManager().post(SERVER_URL, reqJson, new IRequestCallback() {
      @Override
      public void onSuccess(String response) {
        OrderCancelResp resp = mGson.fromJson(response, OrderCancelResp.class);
        if (!DataUtil.checkResponseSuccess(resp)) {
          LogUtils.i(TAG, "orderCancel: " + resp.getResponseDesc());
          callback.onOrderCancelFailed();
          return;
        }
        callback.onOrderCancelSuccess();
      }

      @Override
      public void onError(Throwable throwable) {
        LogUtils.e(TAG, "orderCancel: ", throwable);
        callback.onOrderCancelFailed();
      }
    });
  }
}
