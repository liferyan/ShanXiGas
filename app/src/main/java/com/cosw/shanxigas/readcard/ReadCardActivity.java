package com.cosw.shanxigas.readcard;

import static com.cosw.shanxigas.util.Constant.EXTRA_CARD_NO;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import com.cosw.shanxigas.R;
import com.cosw.shanxigas.activate.ActivateActivity;
import com.cosw.shanxigas.base.BaseActivity;
import com.cosw.shanxigas.readcard.ReadCardContract.Presenter;

public class ReadCardActivity extends BaseActivity implements ReadCardContract.View {

  private NfcAdapter adapter;

  private ReadCardContract.Presenter mPresenter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.read_card_act);

    nfcCheck();

    setPresenter(new ReadCardPresenter(this, ReadCardModel.getInstance()));
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (adapter != null) {
      // 构造PendingInent对象封装NFC标签信息
      PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
          new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
      // 创建Intent过滤器
      IntentFilter iso = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
      // 建立一个处理NFC标签技术的数组
      String[][] techLists = new String[][]{new String[]{IsoDep.class.getName()}};
      // 调用enableForegroundDispatch方法
      adapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[]{iso}, techLists);
    }
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    if (tag != null) {
      mPresenter.getCardNoFromTag(tag);
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    adapter.disableForegroundDispatch(this);
  }

  private void nfcCheck() {
    // 获取NfcAdapter
    adapter = NfcAdapter.getDefaultAdapter(getApplicationContext());
    if (adapter == null) {
      showMessage(getString(R.string.read_card_phone_no_nfc));
    } else if (!adapter.isEnabled()) {
      showMessage(getString(R.string.read_card_nfc_has_disabled));
    }
  }

  @Override
  public void setPresenter(Presenter presenter) {
    mPresenter = presenter;
  }

  @Override
  public void goToActivateActivity(String cardNo) {
    Intent intent = new Intent(this, ActivateActivity.class);
    intent.putExtra(EXTRA_CARD_NO, cardNo);
    startActivity(intent);
    finish();
  }
}
