package com.cosw.shanxigas.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.cosw.shanxigas.R;
import com.cosw.shanxigas.app.MyApplication;
import com.cosw.shanxigas.base.BaseActivity;
import com.cosw.shanxigas.hidden.HiddenActivity;
import com.cosw.shanxigas.util.StringUtil;
import com.cosw.shanxigas.widget.MyAlertDialog;

/**
 * Created by Ryan on 2017/1/4.
 */

public class SettingsActivity extends BaseActivity {

  private static final String TAG = "SettingsActivity";

  private TextView tvTitle;

  public static final String ACCOUNT = "account";


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.settings_act);

    initViews();
  }

  private void initViews() {
    tvTitle = (TextView) findViewById(R.id.tv_title);
    tvTitle.setText(R.string.settings_title);
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
  }

  public static class ShanXiGasPreferenceFragment extends PreferenceFragment
      implements Preference.OnPreferenceChangeListener {

    //打开暗门的点击次数
    private static final int SHOW_HIDDEN_CLICK_COUNT = 10;
    private int clickTimes;

    private MyApplication app;
    private boolean notFirstShow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.settings_main);

      app = MyApplication.getInstance();
      notFirstShow = false;

      clickTimes = 1;

      Preference defaultAccount = findPreference(getString(R.string.settings_default_account_key));
      bindPreferenceSummaryToValue(defaultAccount);
      /*Preference customerService = findPreference(
          getString(R.string.settings_customer_service_key));
      customerService.setSummary(getString(R.string.settings_customer_service_phone_number));*/
      Preference appVersion = findPreference(getString(R.string.settings_app_version_key));
      String version = MyApplication.getInstance().getAppVersion();
      appVersion.setSummary(version);
      appVersion.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
         /* Toast.makeText(getActivity(), String.valueOf(clickTimes), Toast.LENGTH_SHORT)
              .show();*/
          if (clickTimes >= SHOW_HIDDEN_CLICK_COUNT) {
            Intent intent = new Intent(getActivity(), HiddenActivity.class);
            startActivity(intent);
            clickTimes = 1;
            return false;
          }
          clickTimes++;
          return false;
        }
      });
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
      preference.setOnPreferenceChangeListener(this);
      SharedPreferences prefs = PreferenceManager
          .getDefaultSharedPreferences(preference.getContext());
      String prefStr = prefs.getString(preference.getKey(), "");
      onPreferenceChange(preference, prefStr);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
      String stringValue = value.toString();
      if (getString(R.string.settings_default_account_key).equals(preference.getKey())) {
        if (notFirstShow && !StringUtil.isMobileNO(stringValue)) {
          MyAlertDialog alertDialog = new MyAlertDialog(getActivity());
          alertDialog.show();
          alertDialog.setTitle(app.getString(R.string.settings_failed_to_check_mobile_msg));
          return false;
        }
        if (notFirstShow) {
          app.setAccount(stringValue);
        }
        notFirstShow = true;
      }
      preference.setSummary(stringValue);
      return true;
    }
  }
}
