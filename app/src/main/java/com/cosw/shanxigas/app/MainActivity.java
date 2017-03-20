package com.cosw.shanxigas.app;

import static com.cosw.shanxigas.util.Constant.INITIAL_ACCOUNT;
import static com.cosw.shanxigas.util.Constant.MAIN_FAILED_TO_CHECK_ACCOUNT_MSG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cosw.shanxigas.R;
import com.cosw.shanxigas.base.BaseActivity;
import com.cosw.shanxigas.card.CardException;
import com.cosw.shanxigas.card.CardUtil;
import com.cosw.shanxigas.load.LoadActivity;
import com.cosw.shanxigas.settings.SettingsActivity;
import com.cosw.shanxigas.trxrecords.TransactionRecordsActivity;
import com.cosw.shanxigas.util.LogUtils;
import com.cosw.shanxigas.widget.AlertDialogCallBack;

public class MainActivity extends BaseActivity implements OnClickListener {

  protected static final String TAG = "MainActivity";

  private LinearLayout llToLoad;
  private LinearLayout llToTrxRecords;
  private LinearLayout llToSettings;

  private TextView tvCardNo;
  private TextView tvCardBalance;

  private CardUtil mCardUtil;
  private MyApplication app;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_act);

    app = MyApplication.getInstance();
    mCardUtil = CardUtil.getInstance(app.getCard());

    initViews();
  }

  @Override
  protected void onResume() {
    super.onResume();
    tvCardNo.setText(app.getCardNo());
    tvCardBalance = (TextView) findViewById(R.id.tv_card_balance);
    String balanceStr = "";
    try {
      balanceStr = mCardUtil.getBalance() + "元";
    } catch (CardException e) {
      LogUtils.e(TAG, "onResume: ", e);
    }
    tvCardBalance.setText(balanceStr);
  }

  private void initViews() {
    llToLoad = (LinearLayout) findViewById(R.id.ll_to_load);
    llToLoad.setOnClickListener(this);
    llToTrxRecords = (LinearLayout) findViewById(R.id.ll_to_trx_records);
    llToTrxRecords.setOnClickListener(this);
    llToSettings = (LinearLayout) findViewById(R.id.ll_to_settings);
    llToSettings.setOnClickListener(this);
    tvCardNo = (TextView) findViewById(R.id.tv_card_no);
    tvCardBalance = (TextView) findViewById(R.id.tv_card_balance);
  }


  @Override
  public void onClick(View v) {
    Intent intent = null;
    switch (v.getId()) {
      case R.id.ll_to_load:
        if (TextUtils.isEmpty(app.getAccount()) || INITIAL_ACCOUNT.equals(app.getAccount())) {
          showMessage(MAIN_FAILED_TO_CHECK_ACCOUNT_MSG, new AlertDialogCallBack() {
            @Override
            public void onAlertDismiss() {
              Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
              startActivity(settingsIntent);
            }
          });
          return;
        }
        intent = new Intent(this, LoadActivity.class);
        break;
      case R.id.ll_to_trx_records:
        intent = new Intent(this, TransactionRecordsActivity.class);
        break;
      case R.id.ll_to_settings:
        intent = new Intent(this, SettingsActivity.class);
        break;
    }
    if (intent != null) {
      startActivity(intent);
    }
  }
}
