package com.inroids.irmultimedia;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import cn.trinea.android.common.util.PackageUtils;
import cn.trinea.android.common.util.ToastUtils;
import inroids.common.MyLog;

public class LoadingActivity extends Activity {
    public static final String TAG = "IRMultiMedia";

    public AppPublic appMy = null;

    private TextView txtLoading = null;

    private Timer tmrMain = null;

    private int intCurPos = 0;

    // Create and Destroy activity-------------------------
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.appMy = ((AppPublic) getApplicationContext());
        this.appMy.isUpdating = true;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        // Create
        View vwT = new View(this);
        vwT.setBackgroundColor(Color.WHITE);
        this.setContentView(vwT);
        // 提示文本
        this.txtLoading = new TextView(this);
        this.txtLoading.setTextColor(Color.RED);
        this.txtLoading.setShadowLayer(4, 2, 2, Color.BLACK);
        this.txtLoading.setGravity(Gravity.CENTER);
        this.txtLoading.setTextSize(56); // 字体大小
        this.txtLoading.getPaint().setFakeBoldText(true); // 斜体
        this.txtLoading.setText("检查SDCard...");
        this.addContentView(this.txtLoading, ClassObject.getParams(new Rect(0,
                0, dm.widthPixels, dm.heightPixels)));
        this.appMy.getURL();// get URL String
        // 循环检查sdcard是否加载
        // new Timer
        this.tmrMain = new Timer();
        // time update(1S)
        this.tmrMain.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                LoadingActivity.this.checkSDCard.sendMessage(message);
            }
        }, 0, 1000);
    }

    // 公共函数-------------------------------------------------------
    // 进入设置页面
    private void intoSettingActivity() {
        Intent newIntent = new Intent(this, SettingActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(newIntent);
        this.overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
        this.finish();
    }

    // 进入数据更新页面
    private void intoUpdateActivity() {
        Intent newIntent = new Intent(this, UpdateActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(newIntent);
        this.overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
        this.finish();
    }

    // 进入广告播放页面
    private void intoPlayActivity() {
        Intent intent = new Intent(this, MainService.class);
        startService(intent);
        intent = new Intent(this, MultiPlayActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);
        this.overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
        this.finish();
    }

    //
    private void intoAppUpdateActivity() {
        Intent newIntent = new Intent(this, AppUpdateActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(newIntent);
        this.overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
        this.finish();
    }

    // 检查SDCard(第一步)---------------------------------------------
    Handler checkSDCard = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                if (inroids.common.FileManage
                        .isExistsFolder(LoadingActivity.this.appMy.strMainPath)) {
                    LoadingActivity.this.appMy.getURL();// get URL String
                    LoadingActivity.this.tmrMain.cancel();
                    LoadingActivity.this.tmrMain = null;
                    LoadingActivity.this.sdCardLoadSuccess();// sdCard 加载成功!
                }
            } catch (Exception e) {
                MyLog.e(TAG, "LoadingActivity.checkSDCard:" + e.toString());
            }
            super.handleMessage(msg);
        }
    };

    Handler delayHandler = new Handler();

    Runnable delayRunnable = new Runnable() {
        @Override
        public void run() {
            if (LoadingActivity.this.appMy.isRegister) {
                // this.txtLoading.setText("程序已注册，从服务器上获取最新数据...");
                if (LoadingActivity.this.appMy.isUrl) {
                    // 如果没有检查过 检查APP是否有更新
                    new NetworkTask(5)
                            .execute(LoadingActivity.this.appMy.strUrl
                                    + "/inter_json/getsoftwareinfo?device="
                                    + LoadingActivity.this.appMy.strDeviceId + "&softNumber="
                                    + LoadingActivity.this.appMy.strKey);

                } else
                    LoadingActivity.this.checkLocalData();
            } else {
                if (LoadingActivity.this.appMy.isUrl) {
                    LoadingActivity.this.txtLoading
                            .setText("程序未注册，从服务器上获取注册码...");
                    // 获取注册码
                    new NetworkTask(1)
                            .execute(LoadingActivity.this.appMy.strUrl
                                    + "/inter_json/getSNInfo?device="
                                    + LoadingActivity.this.appMy.strDeviceId
                            );
                } else {
                    LoadingActivity.this.txtLoading
                            .setText("程序未注册且没有网址，进入设置页面!");
                    LoadingActivity.this.intoSettingActivity();// 进入设置页面
                }
            }

        }
    };

    // 加载成功，检查注册码
    private void sdCardLoadSuccess() {
        this.txtLoading.setText("");
        delayHandler.postDelayed(delayRunnable, 100);

        // if (this.appMy.isRegister) {
        // // this.txtLoading.setText("程序已注册，从服务器上获取最新数据...");
        // if (this.appMy.isUrl) {
        // // 如果没有检查过 检查APP是否有更新
        // if (!this.appMy.hasCheckedAppUpdate) {
        // new NetworkTask(5).execute(this.appMy.strUrl +
        // "/system?action=getAppInfo&s="
        // + this.appMy.strDeviceId);
        // } else
        // new NetworkTask(2).execute(this.appMy.strUrl +
        // "/system?action=getTime");
        // } else
        // this.checkLocalData();
        // } else {
        // if (this.appMy.isUrl) {
        // this.txtLoading.setText("程序未注册，从服务器上获取注册码...");
        // // 获取注册码
        // new NetworkTask(1).execute(this.appMy.strUrl +
        // "/system?action=getLicense&d=" + this.appMy.strDeviceId
        // + "&s=" + this.appMy.strKey);
        // } else {
        // this.txtLoading.setText("程序未注册且没有网址，进入设置页面!");
        // this.intoSettingActivity();// 进入设置页面
        // }
        // }
    }

    private void getAppInfo(String result) {
        boolean isContain = false;
        String address = "";
        int version = 0;
        if (inroids.common.System.isNotNullString(result)) {
            try {
                JSONObject jo = new JSONObject(result);
                int r = inroids.common.ClassObject.getInt(jo, "r");
                if (r == 0) {
                    JSONArray ja = jo.getJSONArray("data");
                    for (int i = 0; i < ja.length(); i++) {
                        if (ja.getJSONObject(i).getString("key")
                                .equalsIgnoreCase(this.appMy.strKey)) {
                            address = ja.getJSONObject(i).getString("url");
                            version = ja.getJSONObject(i).getInt("ver");
                            isContain = true;
                            System.out.println("version=" + version);
                            break;
                        }

                    }
                    if (isContain) {
                        if (version > PackageUtils.getAppVersionCode(this)) {
                            this.appMy.apkVersion = version;
                            this.appMy.apkFilePath = address;
                            this.intoAppUpdateActivity();
                            return;
                        } else {
                            ToastUtils.show(this, "程序为最新版本");
                            new NetworkTask(2).execute(this.appMy.strUrl + "/inter_json/completeSoftUpdate?device="
                                    + LoadingActivity.this.appMy.strDeviceId + "&softNumber="
                                    + LoadingActivity.this.appMy.strKey + "&ver=" + PackageUtils.getAppVersionCode(this));
                        }
                    } else {
                        new NetworkTask(2).execute(this.appMy.strUrl + "/inter_json/completeSoftUpdate?device="
                                + LoadingActivity.this.appMy.strDeviceId + "&softNumber="
                                + LoadingActivity.this.appMy.strKey + "&ver=" + PackageUtils.getAppVersionCode(this));
                    }
                } else {
                    new NetworkTask(2).execute(this.appMy.strUrl + "/inter_json/completeSoftUpdate?device="
                            + LoadingActivity.this.appMy.strDeviceId + "&softNumber="
                            + LoadingActivity.this.appMy.strKey + "&ver=" + PackageUtils.getAppVersionCode(this));
                }
            } catch (Exception e) {

            }
        }

    }

    private void updateSystemClock(String strT) {
//        if (inroids.common.System.isNotNullString(strT)) {
//            inroids.common.System.updateSystemTime(
//                    LoadingActivity.this.appMy.sRoot, strT);
//            String picNow = DateTime.formatCurTime("yyyyMMddHHmmss");
//            //this.appMy.PicService.SetPICDateTime(picNow);
//        }
//        this.txtLoading.setText("程序已注册，获取定时开关机时间...");
//        new NetworkTask(4).execute(this.appMy.strUrl
//                + "/system?action=getDeviceTime&sn=" + this.appMy.strDeviceId);

        getDeviceAutoTime(strT);
    }

    // 设置开关机时间
    private void getDeviceAutoTime(String result) {
        if (inroids.common.System.isNotNullString(result))
            this.appMy.getSetDeviceTime(result);
        this.txtLoading.setText("程序已注册，从服务器上获取最新数据...");
        new NetworkTask(3).execute(this.appMy.strUrl + "/inter_json/getData_pad?device="
                + this.appMy.strDeviceId);
    }

    // 分析从服务器获取注册码（第二步)-----------------------------------------
    // strT:从网上获取的注册码数据.
    private void checkSN(String strT) {
        if (inroids.common.System.isNotNullString(strT)) {
            String strKey = "\"SN\":\"";
            if (strT.contains(strKey)) {
                int iStart = strT.indexOf(strKey);
                int iEnd = strT.indexOf("\"", iStart + strKey.length());
                if (iStart >= 0 && iEnd >= 0) {
                    // 获取注册码
                    String strK = strT
                            .substring(iStart + strKey.length(), iEnd)
                            .toUpperCase();
                    this.appMy.isRegister = this.appMy.getIsRegister(strK);
                    if (this.appMy.isRegister) {
                        this.txtLoading.setText(" 结果：注册成功！");
                        inroids.common.UserInfo.setString(this, "SN", strK);// 保存注册码
                        // 从服务器获取最新数据(第三步)
                        this.txtLoading.setText("程序已注册，检查程序版本...");
                        new NetworkTask(5)
                                .execute(LoadingActivity.this.appMy.strUrl
                                        + "/inter_json/getsoftwareinfo?device="
                                        + LoadingActivity.this.appMy.strDeviceId + "&softNumber="
                                        + LoadingActivity.this.appMy.strKey);
                    } else {
                        this.txtLoading.setText(" 结果：注册失败！");
                    }
                }
            }
        }
        // 注册失败进入设置页面
        if (!this.appMy.isRegister) {
            this.intoSettingActivity();
        }

    }

    // 分析从服务器获取注册码（第三步)-----------------------------------------
    // strT:从网上获取的数据.
    private void checkTheLastData(String strT) {
        if (inroids.common.System.isNotNullString(strT)) {
            if (strT.startsWith("{\"r\":\"0\"")) {
                try {
                    JSONObject jo = new JSONObject(strT);
                    int r = inroids.common.ClassObject.getInt(jo, "v");
                    if (r != this.appMy.getDataVersion()) {
                        this.appMy.strDownData = strT;// 保留下载数据
                        this.intoUpdateActivity();// 进入数据更新界面
                        return;
                    }
                } catch (Exception e) {
                }

            }
        }
        this.txtLoading.setText("没有数据更新，分析本地数据...");
        this.checkLocalData();
    }

    // 分析本地数据是否存在（第四步)-----------------------------------------
    private void checkLocalData() {
        // 本地数据存在
        if (this.appMy.isRegister
                && inroids.common.System
                .isNotNullString(this.appMy.strDataFile)
                && inroids.common.FileManage
                .isExistsFile(this.appMy.strDataFile)) {
            // new Timer
            this.tmrMain = new Timer();
            this.createButton();
            this.intCurPos = 0;
            this.txtLoading.setText("本地数据检测成功，留有5秒时间，您可以选择进入设置");
            this.tmrMain.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    LoadingActivity.this.checkPlay.sendMessage(message);
                }
            }, 0, 1000);

        } else {// 不存在进入设置页面
            this.intoSettingActivity();
        }
    }

    // Create button
    private void createButton() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        this.addButton(dm.widthPixels - 170, 10 + 90 * 0, "进入设置页面", 1);

    }

    // add button
    private void addButton(int left, int top, String aTitle, int tag) {
        try {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(160,
                    80);
            params.setMargins(left, top, left + 160, top + 80);
            Button btnT = new Button(this);
            btnT.setText(aTitle);
            btnT.setTextSize(16);
            btnT.setTag(tag);
            btnT.setOnClickListener(buttonClick);
            this.addContentView(btnT, params);
        } catch (Exception e) {
            MyLog.e(TAG, "ActivitySetting.addButton:" + e.toString());
        }
    }

    // 用户点击按钮（停止时钟->进入设置页面)
    private OnClickListener buttonClick = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            if (LoadingActivity.this.tmrMain != null) {
                LoadingActivity.this.tmrMain.cancel();
                LoadingActivity.this.tmrMain = null;
            }
            LoadingActivity.this.intoSettingActivity();
        }
    };

    // 等待5秒时间，让用户做选择(第五步)--------------------------------
    Handler checkPlay = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                LoadingActivity.this.intCurPos++;
                if (LoadingActivity.this.intCurPos > 4) {

                    if (LoadingActivity.this.tmrMain != null) {
                        LoadingActivity.this.tmrMain.cancel();
                        LoadingActivity.this.tmrMain = null;
                    }
                    LoadingActivity.this.checkDataLoading();
                }
            } catch (Exception e) {
                MyLog.e(TAG, "ActivityLoading.checkPlay:" + e.toString());
            }
            super.handleMessage(msg);
        }
    };

    // 检查本地数据加载(第六步)---------------------------------------
    private void checkDataLoading() {
        if (this.appMy.refreshData()) {// 检查解析是否成功{
            this.intoPlayActivity();
        } else {
            this.txtLoading.setText("本地数据存在，但数据有错误，请检查！");
        }
    }

    // 网络数据同步--------------------------------------------------------
    class NetworkTask extends AsyncTask<String, Integer, String> {
        int intTag = 0;

        public NetworkTask(int iTag) {
            intTag = iTag;// 1:从服务器上获取注册码 3:从服务器获取最新数据
        }

        @Override
        protected String doInBackground(String... params) {
            return inroids.common.Network.getString(params[0]);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            LoadingActivity.this.intoSettingActivity();// 失败进入设置页面
        }

        @Override
        protected void onPostExecute(String result) {
            // 分析注册码情况
            if (intTag == 1)
                LoadingActivity.this.checkSN(result);
            if (intTag == 2)
                LoadingActivity.this.updateSystemClock(result);
            // 检查数据更新结果
            if (intTag == 3)
                LoadingActivity.this.checkTheLastData(result);
            if (intTag == 4)
                LoadingActivity.this.getDeviceAutoTime(result);
            if (intTag == 5)
                LoadingActivity.this.getAppInfo(result);
        }
    }

}
