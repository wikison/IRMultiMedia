package com.inroids.irmultimedia;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import inroids.common.Network;

public class IESetActivity extends Activity {
    private static final String TAG = "IRMultimedia.IESetActivity";
    private AppPublic appMy;
    WifiManager mWifiManager;
    String strWebsite = "http://www.baidu.com";
    WebView mWebView;
    Button mBtnWifiSet, mBtnClose;
    TextView mTvWifiHint, mTvWebsite;
    boolean isWifiEnabled = false;
    Timer tmrMain;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.appMy = ((AppPublic) getApplicationContext());
        this.setContentView(R.layout.ie_set);
        this.initViews();
        this.initData();
        this.setListeners();

        this.tmrMain = new Timer();
        // time update(1S)
        this.tmrMain.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                IESetActivity.this.handler.sendMessage(message);
            }
        }, 0, 1000);
    }

    private void initViews() {
        mWebView = (WebView) this.findViewById(R.id.wv_is);
        mBtnWifiSet = (Button) this.findViewById(R.id.btn_wifi);
        mTvWifiHint = (TextView) this.findViewById(R.id.tv_wifihint);
        mBtnClose = (Button) this.findViewById(R.id.btn_is_close);
        mTvWebsite = (TextView) this.findViewById(R.id.tv_is_website);

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Network.isNetworkAvailable(context)) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (cm != null) {
                    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                    if (networkInfo != null
                            && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        //停止计时器
                        tmrMain.cancel();
                        tmrMain = null;

                        //显示wifi信息
                        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
                        mTvWifiHint.setText("WIFI已连接至:" + wifiInfo.getSSID());

                        mTvWebsite.setText(strWebsite);
                        //加载网页界面
                        setWebInfo();
                    }
                } else {
                    mTvWifiHint.setText("当前wifi未连接");
                }
            } else {
                mTvWifiHint.setText("当前wifi未设置");
            }

            super.handleMessage(msg);
        }
    };

    private void initData() {
        this.context = getApplicationContext();
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

    }

    private void setWebInfo() {
        //mTvWebsite.setText(strWebsite);
        mWebView.getSettings().setJavaScriptEnabled(true); // 启用JavaScript
        //mWebView.getSettings().setPluginsEnabled(true); // 启用插件
        // 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                mTvWebsite.setText(url);
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view,
                                           SslErrorHandler handler, SslError error) {
                // super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                Toast.makeText(IESetActivity.this,
                        errorCode + "," + description, Toast.LENGTH_LONG)
                        .show();

                // super.onReceivedError(view, errorCode, description,
                // failingUrl);
            }

            @Override
            public void onReceivedLoginRequest(WebView view, String realm,
                                               String account, String args) {
                super.onReceivedLoginRequest(view, realm, account, args);

            }

            @Override
            public void onReceivedHttpAuthRequest(WebView view,
                                                  HttpAuthHandler handler, String host, String realm) {
                super.onReceivedHttpAuthRequest(view, handler, host, realm);
                mTvWebsite.setText(host);
            }
        });
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebView.loadUrl(strWebsite);
    }

    private void setListeners() {
        mBtnWifiSet.setOnClickListener(l);
        mBtnClose.setOnClickListener(l);
    }

    private View.OnClickListener l = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_wifi:
                    break;
                case R.id.btn_is_close:
                    if (Network.isNetworkAvailable(context)) {
                        //判断是否连上外网
                        if (Network.ping()) {
                            Intent i = new Intent();
                            i.setClass(IESetActivity.this, LoadingActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            IESetActivity.this.finish();
                        } else {
                            showDailog("当前网络无法连接至外网", true);
                        }
                    } else {
                        //Toast.makeText(context, "当前未连上外网, 请设置", Toast.LENGTH_LONG).show();
                        showDailog("Wifi不可用, 请前往设置", false);

                    }

                    break;
            }
        }
    };


    private void showDailog(String msg, boolean isConnectWifi) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("注意!");
        builder.setIcon(android.R.drawable.ic_input_get);
        builder.setMessage(msg);
        // Ok Button
        if (!isConnectWifi) {
            builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    if (Build.VERSION.SDK_INT > 14) {
                        startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                    } else {
                        startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                    }
                }
            });
        } else {
            builder.setNegativeButton("确定", disMiss);
        }
        builder.create().show();
    }

    private DialogInterface.OnClickListener disMiss = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {
            dialog.dismiss();
        }
    };
}
