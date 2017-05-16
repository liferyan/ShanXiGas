package com.cosw.shanxigas.trxrecords;

import static com.cosw.shanxigas.util.Constant.TRX_FAILED_TO_LOADING_MSG;
import static com.cosw.shanxigas.util.Constant.TRX_LOADING_QUERY_ORDER;

import android.os.Handler;
import com.cosw.shanxigas.entity.TrxDetail;
import com.cosw.shanxigas.entity.TrxRecords;
import java.util.List;

/**
 * Created by Ryan on 2017/1/16.
 */

public class TrxRecordPresenter implements TrxRecordsContract.Presenter,
    TrxRecordsContract.Model.LoadQueryOrderListCallback,
    TrxRecordsContract.Model.LoadQueryOrderDetailCallback {

  private TrxRecordsContract.View mView;
  private TrxRecordsContract.Model mModel;

  public TrxRecordPresenter(TrxRecordsContract.View view, TrxRecordsContract.Model model) {
    mView = view;
    mModel = model;
  }

  @Override
  public void start() {
    getQueryOrderList(0);
  }

  private void setMessage(String message) {
    mView.hideLoading();
    mView.showMessage(message);
  }

  @Override
  public void getQueryOrderList(int hasLoadedCount) {
    if (hasLoadedCount == 0) {
      mView.showLoading(TRX_LOADING_QUERY_ORDER);
    }
    mModel.loadQueryOrderList(hasLoadedCount, this);
  }

  @Override
  public void getQueryOrderDetail(String orderNo) {
    mView.showLoading(TRX_LOADING_QUERY_ORDER);
    mModel.loadQueryOrderDetail(orderNo, this);
  }

  @Override
  public void onLoadQueryOrderDetailSuccess(TrxDetail detail) {
    mView.hideLoading();
    mView.setDetail(detail);
  }

  @Override
  public void onLoadQueryOrderListSuccess(List<TrxRecords> trxRecordsList) {
    mView.hideLoading();
    mView.setData(trxRecordsList);
  }

  @Override
  public void onLoadQueryOrderDetailFailed() {
    setMessage(TRX_FAILED_TO_LOADING_MSG);
  }

  @Override
  public void onLoadQueryOrderListFailed() {
    setMessage(TRX_FAILED_TO_LOADING_MSG);
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        mView.setData(null);
      }
    }, 500);
  }
}
