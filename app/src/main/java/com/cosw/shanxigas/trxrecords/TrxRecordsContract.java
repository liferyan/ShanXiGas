package com.cosw.shanxigas.trxrecords;

import com.cosw.shanxigas.base.BasePresenter;
import com.cosw.shanxigas.base.BaseView;
import com.cosw.shanxigas.entity.TrxDetail;
import com.cosw.shanxigas.entity.TrxRecords;
import java.util.List;

/**
 * Created by Ryan on 2017/1/16.
 */

public class TrxRecordsContract {

  interface View extends BaseView<Presenter> {

    void showLoading(String title);

    void hideLoading();

    void showMessage(String message);

    void setData(List<TrxRecords> trxRecordsList);

    void setDetail(TrxDetail detail);
  }

  interface Presenter extends BasePresenter {

    void getQueryOrderList(int hasLoadedCount);

    void getQueryOrderDetail(String orderNo);
  }

  interface Model {

    interface LoadQueryOrderListCallback {

      void onLoadQueryOrderListSuccess(List<TrxRecords> trxRecordsList);

      void onLoadQueryOrderListFailed();
    }

    interface LoadQueryOrderDetailCallback {

      void onLoadQueryOrderDetailSuccess(TrxDetail detail);

      void onLoadQueryOrderDetailFailed();
    }

    void loadQueryOrderList(int hasLoadedCount, LoadQueryOrderListCallback callback);

    void loadQueryOrderDetail(String orderNo, LoadQueryOrderDetailCallback callback);

  }
}
