package com.cosw.shanxigas.load;

import static com.cosw.shanxigas.util.Constant.EXTRA_DESC;
import static com.cosw.shanxigas.util.Constant.EXTRA_LOAD_AMOUNT;
import static com.cosw.shanxigas.util.Constant.EXTRA_ORDER_NO;
import static com.cosw.shanxigas.util.Constant.EXTRA_STATUS;
import static com.cosw.shanxigas.util.Constant.PAY_SUCCESS_STATUS_CODE;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.cosw.shanxigas.R;
import com.cosw.shanxigas.app.MyApplication;
import com.cosw.shanxigas.base.BaseActivity;
import com.cosw.shanxigas.load.LoadContract.Presenter;
import com.cosw.shanxigas.util.LogUtils;
import java.text.NumberFormat;

public class PayResultActivity extends BaseActivity implements LoadContract.View {

  private static final String TAG = "PayResultActivity";

  private TextView tvCardNo;
  private TextView tvBalance;

  private boolean paySuccess;

  private LoadContract.Presenter mPresenter;
  private MyApplication app;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.pay_result_act);

    app = MyApplication.getInstance();

    initViews();

    setPresenter(new LoadPresenter(this, LoadModel.getInstance()));
    getPayResult();
  }

  private void getPayResult() {
    Intent intent = getIntent();
    String orderNo = intent.getStringExtra(EXTRA_ORDER_NO);
    app.setOrderNo(orderNo);
    String status = intent.getStringExtra(EXTRA_STATUS);
    String desc = intent.getStringExtra(EXTRA_DESC);
    String loadAmount = intent.getStringExtra(EXTRA_LOAD_AMOUNT);
    int loadMoney = 0;
    if (!TextUtils.isEmpty(loadAmount)) {
      try {
        loadMoney = Integer.parseInt(loadAmount);
      } catch (NumberFormatException e) {
        loadMoney = 1;
      }
    }
    LogUtils
        .i(TAG, "PayResultActivity : " + orderNo + "|" + status + "|" + desc + "|" + loadAmount);
    if (PAY_SUCCESS_STATUS_CODE.equals(status)) {
      paySuccess = true;
      if (loadMoney != 0) {
        mPresenter.setLoadAmount(loadMoney);
      }
      app.setOrderNo(orderNo);
    } else {
      mPresenter.onPayFailed();
    }
  }

  private void initViews() {
    TextView tvTitle = (TextView) findViewById(R.id.tv_title);
    tvTitle.setText(getString(R.string.load_title));
    tvCardNo = (TextView) findViewById(R.id.tv_card_no);
    tvCardNo.setText(app.getCardNo());
    tvBalance = (TextView) findViewById(R.id.tv_balance);
    tvBalance.setText(NumberFormat.getCurrencyInstance().format(0) + "元");
    // 设置消息页面为初始页面
    ImageView ivTitleLeft = (ImageView) findViewById(R.id.img_left);
    ivTitleLeft.setVisibility(View.VISIBLE);
    ivTitleLeft.setImageResource(R.drawable.ic_arrow_back_white_24dp);
    ivTitleLeft.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
    TextView tvRefresh = (TextView) findViewById(R.id.tv_refresh);
    tvRefresh.setVisibility(View.VISIBLE);
    tvRefresh.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        mPresenter.refreshBalance(true);
      }
    });
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (paySuccess) {
      mPresenter.load();
      paySuccess = false;
    } else {
      mPresenter.refreshBalance(false);
    }
  }

  @Override
  public void setPresenter(Presenter presenter) {
    mPresenter = presenter;
  }


  @Override
  public void setCardBasicInfo(String balance, String cardNo) {
    tvCardNo.setText(cardNo);
    tvBalance.setText(balance);
  }

  @Override
  public void setCardFullInfo(String balance, String cardNo, String price, String loadLimit) {
    setCardBasicInfo(balance, cardNo);
  }

  @Override
  public void setLoadEnable(boolean hasBalance) {

  }

  @Override
  public void goToPay(String url, String method, String sessionId) {
  }
}
