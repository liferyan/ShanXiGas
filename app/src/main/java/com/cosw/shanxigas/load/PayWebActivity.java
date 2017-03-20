package com.cosw.shanxigas.load;

import static com.cosw.shanxigas.util.Constant.EXTRA_DESC;
import static com.cosw.shanxigas.util.Constant.EXTRA_METHOD;
import static com.cosw.shanxigas.util.Constant.EXTRA_ORDER_NO;
import static com.cosw.shanxigas.util.Constant.EXTRA_SESSION_ID;
import static com.cosw.shanxigas.util.Constant.EXTRA_STATUS;
import static com.cosw.shanxigas.util.Constant.EXTRA_URL;
import static com.cosw.shanxigas.util.Constant.PAY_LOADING_TO_WEB_PAGE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import com.cosw.shanxigas.R;
import com.cosw.shanxigas.base.BaseActivity;
import com.cosw.shanxigas.util.LogUtils;

public class PayWebActivity extends BaseActivity {

  private static final String TAG = "PayWebActivity";

  @SuppressLint("SetJavaScriptEnabled")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.pay_web_act);

    showLoading(PAY_LOADING_TO_WEB_PAGE);

    TextView tvTitle = (TextView) findViewById(R.id.tv_title);
    tvTitle.setText(getString(R.string.load_pay_title));
    ImageView img_left = (ImageView) findViewById(R.id.img_left);
    // 设置消息页面为初始页面
    img_left.setVisibility(View.VISIBLE);
    img_left.setImageResource(R.drawable.ic_arrow_back_white_24dp);
    img_left.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });

    WebView mPayWebView = (WebView) findViewById(R.id.pay_webview);
    mPayWebView.getSettings().setJavaScriptEnabled(true);
    mPayWebView.addJavascriptInterface(this, "heBaoService");
    mPayWebView.setWebViewClient(new WebViewClient() {
      @Override
      public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed();
      }

      @Override
      public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        hideLoading();
      }
    });

    Intent intent = getIntent();
    String url = intent.getStringExtra(EXTRA_URL);
    String method = intent.getStringExtra(EXTRA_METHOD);
    String sessionId = intent.getStringExtra(EXTRA_SESSION_ID);

    url = url + "?SESSIONID=" + sessionId + "&method=" + method;
    LogUtils.i(TAG, "PayWebActivity: " + url);
    mPayWebView.loadUrl(url);
  }

  @JavascriptInterface
  public void idVerify(String orderNo, String status, String desc) {
    Intent intent = new Intent(this, PayResultActivity.class);
    intent.putExtra(EXTRA_ORDER_NO, orderNo);
    intent.putExtra(EXTRA_STATUS, status);
    intent.putExtra(EXTRA_DESC, desc);
    startActivity(intent);
    finish();
  }
}
