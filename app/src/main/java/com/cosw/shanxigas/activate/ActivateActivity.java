package com.cosw.shanxigas.activate;

import static com.cosw.shanxigas.util.Constant.EXTRA_CARD_NO;
import static com.cosw.shanxigas.util.Constant.EXTRA_ONLY_BIND;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.cosw.shanxigas.R;
import com.cosw.shanxigas.activate.ActivateContract.Presenter;
import com.cosw.shanxigas.app.MainActivity;
import com.cosw.shanxigas.base.BaseActivity;

public class ActivateActivity extends BaseActivity implements ActivateContract.View,
    OnClickListener {

  private TextView tvTitle;
  private TextView tvCardNo;
  private TextView tvUserName;
  private TextView tvAddress;
  private TextView tvGapType;
  private Button btActivate;

  private ActivateContract.Presenter mPresenter;
  private String mCardNo;
  //仅绑定
  private boolean onlyBind;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activate_act);

    mCardNo = getIntent().getStringExtra(EXTRA_CARD_NO);
    onlyBind = getIntent().getBooleanExtra(EXTRA_ONLY_BIND, false);

    initViews();
    setPresenter(new ActivatePresenter(this, ActivateModel.getInstance()));
  }

  @Override
  protected void onResume() {
    super.onResume();
    mPresenter.getCardInfo(mCardNo);
  }

  private void initViews() {
    tvTitle = (TextView) findViewById(R.id.tv_title);
    tvTitle.setText(getString(R.string.activate_title));
    tvCardNo = (TextView) findViewById(R.id.tv_card_no);
    tvUserName = (TextView) findViewById(R.id.tv_gas_price);
    tvAddress = (TextView) findViewById(R.id.tv_address);
    tvGapType = (TextView) findViewById(R.id.tv_gap_type);
    btActivate = (Button) findViewById(R.id.bt_activate);
    btActivate.setOnClickListener(this);
    btActivate.setEnabled(false);
  }


  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.bt_activate:
        mPresenter.activate(onlyBind);
        break;
    }
  }

  @Override
  public void showCardInfo(String cardNo, String userName, String address, String gapType) {
    tvCardNo.setText(cardNo);
    tvUserName.setText(userName);
    tvAddress.setText(address);
    tvGapType.setText(gapType);
    btActivate.setEnabled(true);
  }

  @Override
  public void goToMainActivity() {
    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent);
    finish();
  }

  @Override
  public void setPresenter(Presenter presenter) {
    mPresenter = presenter;
  }
}
