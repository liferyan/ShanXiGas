package com.cosw.shanxigas.trxrecords;

import static com.cosw.shanxigas.R.id.status;
import static com.cosw.shanxigas.util.Constant.EXTRA_LOAD_AMOUNT;
import static com.cosw.shanxigas.util.Constant.EXTRA_ORDER_NO;
import static com.cosw.shanxigas.util.Constant.EXTRA_STATUS;
import static com.cosw.shanxigas.util.Constant.HAS_CANCEL;
import static com.cosw.shanxigas.util.Constant.PAY_SUCCESS_STATUS_CODE;
import static com.cosw.shanxigas.util.Constant.TRX_STATUS_LOAD_SUCCESS;
import static com.cosw.shanxigas.util.Constant.TRX_STATUS_REVERSAL_SUCCESS;
import static com.cosw.shanxigas.util.Constant.WAIT_LOAD;
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
import com.cosw.shanxigas.app.MyApplication;
import com.cosw.shanxigas.base.BaseActivity;
import com.cosw.shanxigas.card.CardException;
import com.cosw.shanxigas.card.CardUtil;
import com.cosw.shanxigas.entity.TrxDetail;
import com.cosw.shanxigas.entity.TrxRecords;
import com.cosw.shanxigas.load.PayResultActivity;
import com.cosw.shanxigas.trxrecords.TrxRecordsContract.Presenter;
import java.util.List;

public class TransactionDetailActivity extends BaseActivity implements TrxRecordsContract.View {

  private String orderNo;
  private String orderStatus;
  private String loadAmount;

  private TextView tvCardNo;
  private TextView tvOrderNo;
  private TextView tvPayNo;
  private TextView tvAmount;
  private TextView tvRedAmt;
  private TextView tvVchAmt;
  private TextView tvPotChgAmt;
  private TextView tvMobile;
  private TextView tvWgCash;
  private TextView tvWgStartTime;
  private TextView tvWgEndTime;
  private TextView tvStatus;
  private TextView tvCreateTime;
  private TextView tvUpdateTime;
  private TextView btOrderLoad;
  private TextView btOrderCancel;
  private TextView btOrderBack;
  private View loadTips;

  private CardUtil mCardUtil;
  private MyApplication app;


  private TrxRecordsContract.Presenter mPresenter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.transaction_detail_act);

    initViews();

    app = MyApplication.getInstance();
    mCardUtil = CardUtil.getInstance(app.getCard());

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
    tvMobile = (TextView) findViewById(R.id.mobile);
    tvWgCash = (TextView) findViewById(R.id.wgCash);
    tvWgStartTime = (TextView) findViewById(R.id.wgStartTime);
    tvWgEndTime = (TextView) findViewById(R.id.wgEndTime);
    tvStatus = (TextView) findViewById(status);
    tvCreateTime = (TextView) findViewById(R.id.createTime);
    tvUpdateTime = (TextView) findViewById(R.id.updateTime);
    loadTips = findViewById(R.id.detail_load_tips);

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
    btOrderLoad = (Button) findViewById(R.id.bt_order_load);
    btOrderLoad.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(TransactionDetailActivity.this, PayResultActivity.class);
        intent.putExtra(EXTRA_ORDER_NO, orderNo);
        intent.putExtra(EXTRA_STATUS, PAY_SUCCESS_STATUS_CODE);
        intent.putExtra(EXTRA_LOAD_AMOUNT, loadAmount);
        startActivity(intent);
      }
    });
    btOrderCancel = (Button) findViewById(R.id.bt_order_cancel);
    btOrderCancel.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        mPresenter.orderCancel(orderNo);
      }
    });
    btOrderBack = (Button) findViewById(R.id.bt_back);
    btOrderBack.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
  }

  @Override
  protected void onResume() {
    super.onResume();
    mPresenter.getQueryOrderDetail(orderNo);
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
    loadAmount = detail.getAmount();
    tvAmount.setText(loadAmount);
    tvRedAmt.setText(detail.getRedAmt());
    tvVchAmt.setText(detail.getVchAmt());
    tvPotChgAmt.setText(detail.getPotChgAmt());
    tvMobile.setText(detail.getMobile());
    tvWgCash.setText(detail.getWgCash());
    tvWgStartTime.setText(detail.getWgStartTime());
    tvWgEndTime.setText(detail.getWgEndTime());
    orderStatus = detail.getStatus();
    tvStatus.setText(orderStatus);

    if ((TRX_STATUS_LOAD_SUCCESS.equals(orderStatus) || TRX_STATUS_REVERSAL_SUCCESS
        .equals(orderStatus))) {
      tvStatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
    } else {
      tvStatus.setTextColor(Color.RED);
    }

    tvCreateTime.setText(detail.getCreateTime());
    tvUpdateTime.setText(detail.getUpdateTime());
    if (WAIT_LOAD.equals(orderStatus)) {
      //卡内有余额不能圈存
      int balance;
      try {
        balance = Integer.parseInt(mCardUtil.getBalance());
      } catch (CardException e) {
        balance = 0;
      }
      if (balance != 0) {
        btOrderLoad.setEnabled(false);
        loadTips.setVisibility(View.VISIBLE);
      }

      btOrderBack.setVisibility(View.GONE);
      btOrderCancel.setVisibility(View.GONE);
      btOrderLoad.setVisibility(View.VISIBLE);
    } else if (WAIT_PAY.equals(orderStatus)) {
      btOrderBack.setVisibility(View.GONE);
      btOrderCancel.setVisibility(View.VISIBLE);
      btOrderLoad.setVisibility(View.GONE);
      loadTips.setVisibility(View.GONE);
    } else {
      btOrderBack.setVisibility(View.VISIBLE);
      btOrderCancel.setVisibility(View.GONE);
      btOrderLoad.setVisibility(View.GONE);
      loadTips.setVisibility(View.GONE);
    }
  }

  @Override
  public void setCancelSuccess() {
    tvStatus.setText(HAS_CANCEL);
    btOrderCancel.setVisibility(View.GONE);
    btOrderBack.setVisibility(View.VISIBLE);
  }
}
