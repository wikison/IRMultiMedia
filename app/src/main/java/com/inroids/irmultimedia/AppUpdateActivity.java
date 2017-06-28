package com.inroids.irmultimedia;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.util.Timer;

import cn.trinea.android.common.util.PackageUtils;
import inroids.common.AppManage;
import inroids.common.MyLog;

/**
 * @author yf-lc
 * @date 2014-04-01 10:25
 */
public class AppUpdateActivity extends Activity {
    public static final String TAG = "IRMultiMedia";

    private String apkPath = "";

    public AppPublic appMy = null;

    public ProgressBar barMain = null;

    public TextView vwText = null;

    private boolean isDownSuccess = true;

    private int intPos = -1;

    String strSaveFile = "";

    Timer tmrMain = null;
    private static int UPDATE_TAG = 0;
    private static int UPDATE_SUCCESS_TAG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.appMy = ((AppPublic) getApplicationContext());

        // Create Views
        this.setupViews();

        this.apkPath = inroids.common.System.getMainPath(this.appMy.strMainPath, "apk");

        this.downFile();
    }

    private void setupViews() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        this.barMain = new ProgressBar(this);
        int intLeft = dm.widthPixels / 2 - 64;
        int intTop = dm.heightPixels / 2 - 64;
        this.addContentView(this.barMain, ClassObject.getParams(new Rect(intLeft, intTop, intLeft + 128, intTop + 128)));
        // 加入进度条文本框
        this.vwText = new TextView(this);
        this.vwText.setTextColor(Color.WHITE);
        this.vwText.setGravity(Gravity.CENTER);
        this.vwText.setTextSize(20);
        this.vwText.getPaint().setFakeBoldText(true);
        this.addContentView(this.vwText,
                ClassObject.getParams(new Rect(intLeft - 48, intTop, intLeft + 158, intTop + 300)));

    }

    private void intoLoadActivity() {
        Intent newIntent = new Intent(this, LoadingActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(newIntent);
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        this.finish();
    }

    private void downFile() {
        this.intPos++;
        this.vwText.setText("下载更新程序中...");// 标明当前下载进度

        if (this.intPos >= 1) {// 所有文件已经全部下载
            if (this.isDownSuccess) {
                MyLog.i(this.appMy.strKey + "-AppUpdateActivity", "Download susscess");
                this.downSuccess(); // 下载成功
            } else {
                MyLog.i(this.appMy.strKey + "-AppUpdateActivity", "Download failed");
                //this.appMy.hasCheckedAppUpdate = true;
                this.downFailed(); // 下载失败
            }
        } else {
            // 获取下载文件路径 InroidsAPP/APP/3/3.apk
            String strFile = this.appMy.apkFilePath;
            // 网址 http://192.168.0.111:8080/inroids/InroidsAPP/APP/3/3.apk
            //String strUrl = this.appMy.strUrl + File.separator + strFile;
            String strUrl = strFile;
            // 下载文件名irmultimedia.apk
            String strFileName = this.appMy.strKey + ".apk";
            // 保存地址inroids/apk/irmultimedia.apk
            strSaveFile = this.apkPath + File.separator + strFileName;
            if (inroids.common.FileManage.isExistsFile(strSaveFile)) {
                // 文件存在删除后下载
                inroids.common.FileManage.delFile(strSaveFile);
                new NetworkTask(strSaveFile, 0).execute(strUrl, strSaveFile);
            } else {
                new NetworkTask(strSaveFile, 0).execute(strUrl, strSaveFile);
            }
        }
    }

    private void downSuccess() {
        // 安装新app
        if (AppManage.getPackageInfo(this, strSaveFile).equals("com.inroids.irmultimedia")) {
            installApp();
        } else {
            this.intoLoadActivity();
        }

    }

    private void downFailed() {
        this.intoLoadActivity();
    }

    class NetworkTask extends AsyncTask<String, Integer, String> {
        String strFile;
        int intTag;
        String result = "";

        public NetworkTask(int intTag) {
            this.intTag = intTag;
        }

        public NetworkTask(String sFile, int intTag) {
            strFile = sFile;
            this.intTag = intTag;
        }


        @Override
        protected String doInBackground(String... params) {
            if (intTag == 0)
                result = inroids.common.Network.getFile(params[0], params[1] + ".temp");

            return result;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            AppUpdateActivity.this.isDownSuccess = false;
            inroids.common.FileManage.delFile(strFile + ".temp");
            AppUpdateActivity.this.downFile();// 下一个文件
        }

        @Override
        protected void onPostExecute(String result) {
            switch (intTag) {
                case 0:
                    if (inroids.common.System.isNotNullString(result)) {
                        if (!inroids.common.FileManage.reName(strFile + ".temp", strFile)) {// 重命名
                            AppUpdateActivity.this.isDownSuccess = false;
                            inroids.common.FileManage.delFile(strFile + ".temp");
                        }
                        AppUpdateActivity.this.downFile();
                    } else {
                        AppUpdateActivity.this.isDownSuccess = false;
                        inroids.common.FileManage.delFile(strFile + ".temp");
                        AppUpdateActivity.this.downFile();
                    }
                    break;
            }
        }
    }


    private void installApp() {
        //保存此次更新的版本号
        //inroids.common.UserInfo.setInt(this, "apkVersion", this.appMy.apkVersion);
        // 下载成功安装并打开
        boolean b = PackageUtils.installNormal(this, this.strSaveFile);
//        inroids.common.AppManage.installStartApp(this.appMy.sRoot, this.strSaveFile, "com.inroids.irmultimedia",
//                ".IESetActivity");
        if (!b) {
            intoLoadActivity();
        }
    }


}
