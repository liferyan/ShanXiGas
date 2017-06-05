package com.cosw.shanxigas.settings;

import static com.cosw.shanxigas.util.Constant.SERVER_URL;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.cosw.protocol.req.CardUnBindReq;
import com.cosw.protocol.resp.CardUnBindResp;
import com.cosw.shanxigas.R;
import com.cosw.shanxigas.app.MyApplication;
import com.cosw.shanxigas.base.BaseActivity;
import com.cosw.shanxigas.hidden.HiddenActivity;
import com.cosw.shanxigas.util.DataUtil;
import com.cosw.shanxigas.util.StringUtil;
import com.cosw.shanxigas.util.net.RequestFactory;
import com.cosw.shanxigas.widget.AlertDialogCallBack;
import com.cosw.shanxigas.widget.MyAlertDialog;
import com.google.gson.Gson;

/**
 * Created by Ryan on 2017/1/4.
 */

public class SettingsActivity extends BaseActivity {

  private static final String TAG = "SettingsActivity";

  public static final int UNBIND_SUCCESS = 1;
  public static final int UNBIND_FAILED = 0;

  private TextView tvTitle;

  public static final String ACCOUNT = "account";

  private static SettingsActivity activity;

  private MyApplication app;

  private Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case UNBIND_SUCCESS:
          hideLoading();
          showMessage(getString(R.string.settings_unbind_success), new AlertDialogCallBack() {
            @Override
            public void onPositive() {
              //跳转App初始界面
              final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
              startActivity(intent);
            }
          });
          break;
        case UNBIND_FAILED:
          hideLoading();
          showMessage(getString(R.string.settings_unbind_failed));
          break;
      }
    }
  };


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.settings_act);

    initViews();
    activity = this;
    app = MyApplication.getInstance();
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

  public static void unbind() {
    MyAlertDialog alertDialog = new MyAlertDialog(activity, false);
    alertDialog.setCallback(new AlertDialogCallBack() {
      @Override
      public void onPositive() {
        activity.showLoading(activity.getString(R.string.settings_unbinding));
        activity.unBind(activity.mHandler);
      }
    });
    alertDialog.show();
    alertDialog.setTitle(R.string.settings_unbind_title);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    activity = null;
  }

  public void unBind(final Handler handler) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        CardUnBindReq req = new CardUnBindReq();
        req.setCardNo(app.getCardNo());
        req.setAtr(app.getATR());
        //req.setTelephone("");
        req.setPayAccount(app.getAccount());
        final Gson mGson = new Gson();
        String reqJson = mGson.toJson(req);
        String respJson = RequestFactory.getSyncRequestManager().syncPost(SERVER_URL, reqJson);
        if (respJson == null) {
          handler.obtainMessage(UNBIND_FAILED).sendToTarget();
          return;
        }
        CardUnBindResp resp = mGson.fromJson(respJson, CardUnBindResp.class);
        if (!DataUtil.checkResponseSuccess(resp)) {
          handler.obtainMessage(UNBIND_FAILED).sendToTarget();
          return;
        }
        handler.obtainMessage(UNBIND_SUCCESS).sendToTarget();
      }
    }).start();
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
      Preference unbind = findPreference(getString(R.string.settings_unbind_key));
      unbind.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
          unbind();
          return false;
        }
      });
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
          MyAlertDialog alertDialog = new MyAlertDialog(getActivity(), true);
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
