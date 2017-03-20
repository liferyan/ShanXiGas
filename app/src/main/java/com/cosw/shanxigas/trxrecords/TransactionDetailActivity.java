package com.cosw.shanxigas.trxrecords;

import static com.cosw.shanxigas.util.Constant.EXTRA_ORDER_NO;
import static com.cosw.shanxigas.util.Constant.HAS_CANCEL;
import static com.cosw.shanxigas.util.Constant.TRX_STATUS_LOAD_SUCCESS;
import static com.cosw.shanxigas.util.Constant.TRX_STATUS_REVERSAL_SUCCESS;
import static com.cosw.shanxigas.util.Constant.WAIT_PAY;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.cosw.shanxigas.R;
import com.cosw.shanxigas.base.BaseActivity;
import com.cosw.shanxigas.entity.TrxDetail;
import com.cosw.shanxigas.entity.TrxRecords;
import com.cosw.shanxigas.trxrecords.TrxRecordsContract.Presenter;
import java.util.List;

public class TransactionDetailActivity extends BaseActivity implements TrxRecordsContract.View {

  private String orderNo;

  private TextView tvCardNo;
  private TextView tvOrderNo;
  private TextView tvPayNo;
  private TextView tvAmount;
  private TextView tvRedAmt;
  private TextView tvVchAmt;
  private TextView tvPotChgAmt;
  private TextView tvContractName;
  private TextView tvInvoiceTitle;
  private TextView tvBankName;
  private TextView tvMobile;
  private TextView tvWgCash;
  private TextView tvWgStartTime;
  private TextView tvWgEndTime;
  private TextView tvStatus;
  private TextView tvCreateTime;
  private TextView tvUpdateTime;
  private TextView btOrderCancel;


  private TrxRecordsContract.Presenter mPresenter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.transaction_detail_act);

    initViews();

    Intent intent = getIntent();
    orderNo = intent.getStringExtra(EXTRA_ORDER_NO);

    setPresenter(new TrxRecordPresenter(this, TrxRecordModel.getInstance()));
    mPresenter.getQueryOrderDetail(orderNo);
  }

  private void initViews() {
    TextView tvTitle = (TextView) findViewById(R.id.tv_title);
    tvTitle.setText(R.string.transaction_records_detail_title);
    tvCardNo = (TextView) findViewById(R.id.cardNo);
    tvOrderNo = (TextView) findViewById(R.id.orderNo);
    tvPayNo = (TextView) findViewById(R.id.payNo);
    tvAmount = (TextView) findViewById(R.id.amount);
    tvRedAmt = (TextView) findViewById(R.id.redAmt);
    tvVchAmt = (TextView) findViewById(R.id.vchAmt);
    tvPotChgAmt = (TextView) findViewById(R.id.potChgAmt);
    tvContractName = (TextView) findViewById(R.id.contractName);
    tvInvoiceTitle = (TextView) findViewById(R.id.invoiceTitle);
    tvBankName = (TextView) findViewById(R.id.bankName);
    tvMobile = (TextView) findViewById(R.id.mobile);
    tvWgCash = (TextView) findViewById(R.id.wgCash);
    tvWgStartTime = (TextView) findViewById(R.id.wgStartTime);
    tvWgEndTime = (TextView) findViewById(R.id.wgEndTime);
    tvStatus = (TextView) findViewById(R.id.status);
    tvCreateTime = (TextView) findViewById(R.id.createTime);
    tvUpdateTime = (TextView) findViewById(R.id.updateTime);

    ImageView ivTitleLeft = (ImageView) findViewById(R.id.img_left);
    // 设置消息页面为初始页面
    ivTitleLeft.setVisibility(View.VISIBLE);
    ivTitleLeft.setImageResource(R.drawable.ic_arrow_back_white_24dp);
    ivTitleLeft.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
    btOrderCancel = (Button) findViewById(R.id.bt_order_cancel);
    btOrderCancel.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        mPresenter.orderCancel(orderNo);
      }
    });
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  public void setPresenter(Presenter presenter) {
    mPresenter = presenter;
  }

  @Override
  public void setData(List<TrxRecords> trxRecordsList) {
  }

  @Override
  public void setDetail(TrxDetail detail) {
    tvCardNo.setText(detail.getCardNo());
    tvOrderNo.setText(detail.getOrderNo());
    tvPayNo.setText(detail.getPayNo());
    tvAmount.setText(detail.getAmount());
    tvRedAmt.setText(detail.getRedAmt());
    tvVchAmt.setText(detail.getVchAmt());
    tvPotChgAmt.setText(detail.getPotChgAmt());
    tvContractName.setText(detail.getContractName());
    tvInvoiceTitle.setText(detail.getInvoiceTitle());
    tvBankName.setText(detail.getBankName());
    tvMobile.setText(detail.getMobile());
    tvWgCash.setText(detail.getWgCash());
    tvWgStartTime.setText(detail.getWgStartTime());
    tvWgEndTime.setText(detail.getWgEndTime());
    String payStatus = detail.getStatus();
    tvStatus.setText(payStatus);

    if ((TRX_STATUS_LOAD_SUCCESS.equals(payStatus) || TRX_STATUS_REVERSAL_SUCCESS
        .equals(payStatus))) {
      tvStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
    } else {
      tvStatus.setTextColor(Color.RED);
    }

    tvCreateTime.setText(detail.getCreateTime());
    tvUpdateTime.setText(detail.getUpdateTime());
    if (WAIT_PAY.equals(detail.getStatus())) {
      btOrderCancel.setVisibility(View.VISIBLE);
    } else {
      btOrderCancel.setVisibility(View.GONE);
    }
  }

  @Override
  public void setCancelSuccess() {
    tvStatus.setText(HAS_CANCEL);
    btOrderCancel.setVisibility(View.GONE);
  }
}
