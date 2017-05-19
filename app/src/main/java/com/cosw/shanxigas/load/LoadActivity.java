package com.cosw.shanxigas.load;

import static com.cosw.shanxigas.util.Constant.EXTRA_METHOD;
import static com.cosw.shanxigas.util.Constant.EXTRA_SESSION_ID;
import static com.cosw.shanxigas.util.Constant.EXTRA_URL;
import static com.cosw.shanxigas.util.Constant.LOAD_FAILED_TO_CHECK_MONEY_EMPTY_MSG;
import static com.cosw.shanxigas.util.Constant.LOAD_FAILED_TO_CHECK_MONEY_NOT_DIGIT_MSG;
import static com.cosw.shanxigas.util.Constant.LOAD_FAILED_TO_CHECK_MONEY_TOO_BIG_MSG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.cosw.shanxigas.R;
import com.cosw.shanxigas.app.MyApplication;
import com.cosw.shanxigas.base.BaseActivity;
import com.cosw.shanxigas.load.LoadContract.Presenter;

/**
 * Created by Ryan on 2017/1/4.
 */

public class LoadActivity extends BaseActivity implements LoadContract.View,
    View.OnClickListener {

  private static final String TAG = "LoadActivity";

  private MyApplication app;

  private TextView tvTitle;

  private Context mContext;
  private TextView tvBalance;
  private TextView tvCardNo;
  private TextView tvGasPrice;
  private TextView tvLoadLimit;
  private EditText etLoadMoney;
  private TextView tvTotalMoney;
  private Button btLoad;
  private View otherContent;
  private View tipsLoadSuccess;

  private ImageView ivTitleLeft;
  private TextView tvRefresh;

  private String priceStr = "0";
  private String loadLimitStr = "0";

  private String loadMoney;
  private int loadLimitAmount;

  private LoadContract.Presenter mPresenter;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.load_act);
    mContext = this;
    app = MyApplication.getInstance();
    initViews();

    setPresenter(new LoadPresenter(this, LoadModel.getInstance()));
  }

  private void initViews() {
    tvTitle = (TextView) findViewById(R.id.tv_title);
    tvTitle.setText(getString(R.string.load_title));
    otherContent = findViewById(R.id.ll_other);
    tipsLoadSuccess = findViewById(R.id.tv_tips_load_success);
    tvBalance = (TextView) findViewById(R.id.tv_balance);
    tvCardNo = (TextView) findViewById(R.id.tv_card_no);
    tvGasPrice = (TextView) findViewById(R.id.tv_gas_price);
    tvLoadLimit = (TextView) findViewById(R.id.tv_load_limit);
    tvTotalMoney = (TextView) findViewById(R.id.tv_total_money);
    etLoadMoney = (EditText) findViewById(R.id.et_load_money);
    btLoad = (Button) findViewById(R.id.bt_load);
    btLoad.setOnClickListener(this);
    btLoad.setEnabled(false);
    ivTitleLeft = (ImageView) findViewById(R.id.img_left);
    // 设置消息页面为初始页面
    ivTitleLeft.setVisibility(View.VISIBLE);
    ivTitleLeft.setImageResource(R.drawable.ic_arrow_back_white_24dp);
    ivTitleLeft.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
    tvRefresh = (TextView) findViewById(R.id.tv_refresh);
    tvRefresh.setVisibility(View.VISIBLE);
    tvRefresh.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        mPresenter.refreshBalance(true);
        etLoadMoney.setText("");
      }
    });
  }


  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.bt_load:
        loadMoney = etLoadMoney.getText().toString().trim();
        if (TextUtils.isEmpty(loadMoney)) {
          Toast.makeText(mContext, LOAD_FAILED_TO_CHECK_MONEY_EMPTY_MSG, Toast.LENGTH_SHORT).show();
          break;
        }
        if (!TextUtils.isDigitsOnly(loadMoney)) {
          Toast.makeText(mContext, LOAD_FAILED_TO_CHECK_MONEY_NOT_DIGIT_MSG, Toast.LENGTH_SHORT)
              .show();
          break;
        }
        int amount = Integer.parseInt(loadMoney);
        if (amount > loadLimitAmount) {
          Toast.makeText(mContext, LOAD_FAILED_TO_CHECK_MONEY_TOO_BIG_MSG, Toast.LENGTH_SHORT)
              .show();
          break;
        }
        mPresenter.pay(loadMoney);
        break;
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    mPresenter.start();
  }


  @Override
  public void setPresenter(Presenter presenter) {
    mPresenter = presenter;
  }

  @Override
  public void setCardBasicInfo(String balance, String cardNo) {
    setCardFullInfo(balance, cardNo, priceStr, loadLimitStr);
    setLoadEnable(true);
  }

  @Override
  public void setCardFullInfo(String balance, String cardNo, String price, String loadLimit) {
    priceStr = price;
    loadLimitStr = loadLimit;
    loadLimitAmount = Integer.parseInt(loadLimit);
    tvBalance.setText(balance);
    tvCardNo.setText(cardNo);
    tvGasPrice.setText(price);
    tvLoadLimit.setText(loadLimit);
  }

  @Override
  public void setLoadEnable(boolean hasBalance) {
    if (!hasBalance) {
      //余额为0
      otherContent.setVisibility(View.VISIBLE);
      tipsLoadSuccess.setVisibility(View.GONE);
      btLoad.setEnabled(true);
    } else {
      otherContent.setVisibility(View.GONE);
      tipsLoadSuccess.setVisibility(View.VISIBLE);
      btLoad.setEnabled(false);
    }
  }

  @Override
  public void goToPay(String url, String method, String sessionId) {
    Intent intent = new Intent(mContext, PayWebActivity.class);
    intent.putExtra(EXTRA_URL, url);
    intent.putExtra(EXTRA_METHOD, method);
    intent.putExtra(EXTRA_SESSION_ID, sessionId);
    startActivity(intent);
  }
}
