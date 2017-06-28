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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import inroids.common.FileManage;
import inroids.common.MyLog;

public class UpdateActivity extends Activity {
    public static final String strTag = "IRMultimedia";

    public AppPublic appMy = null;

    public ProgressBar barMain = null;

    public TextView vwText = null, tvHint = null;

    private JSONObject objMain = null;

    private JSONArray arrFile = null;

    private boolean isDownSuccess = true;

    private int intAllCount = 0, intPos = -1;

    private int downTimes = 0;

    // Create and release Activity-----------------------
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // 创建更新界面
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.appMy = ((AppPublic) getApplicationContext());
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        this.appMy.isUpdating = true;
        this.barMain = new ProgressBar(this);
        int intLeft = dm.widthPixels / 2 - 64;
        int intTop = dm.heightPixels / 2 - 64;
        this.addContentView(this.barMain, ClassObject.getParams(new Rect(intLeft, intTop, intLeft + 128, intTop + 128)));
        // 加入进度条文本框
        this.vwText = new TextView(this);
        this.vwText.setTextColor(Color.WHITE);
        this.vwText.setGravity(Gravity.CENTER);
        this.vwText.setTextSize(24);
        this.vwText.getPaint().setFakeBoldText(true);
        this.addContentView(this.vwText, ClassObject.getParams(new Rect(intLeft, intTop, intLeft + 128, intTop + 128)));

        this.tvHint = new TextView(this);
        this.tvHint.setTextColor(Color.WHITE);
        this.tvHint.setGravity(Gravity.CENTER);
        this.tvHint.setTextSize(20);
        this.tvHint.getPaint().setFakeBoldText(true);
        this.addContentView(this.tvHint,
                ClassObject.getParams(new Rect(intLeft - 20, intTop, intLeft + 128, intTop + 300)));
        // 解析
        // JSONTokener jsonParser = new JSONTokener(this.appMy.strDownData.replace(",\"f\":[]", ""));
        // JSONTokener jsonParser = new JSONTokener(this.appMy.strDownData);
        try {
            this.objMain = new JSONObject(this.appMy.strDownData);
            if (!this.objMain.isNull("f")) {
                this.arrFile = this.objMain.getJSONArray("f");
                this.intAllCount = this.arrFile.length();
                if (this.intAllCount > 0)
                    this.downFile(); // 开始下载文件
                else {
                    this.downSuccess(false);// 没有文件要下载
                }
            } else {
                this.intoSettingActivity();// 进入设置界面
            }
        } catch (JSONException e) {
            MyLog.e(strTag, "ActivityDataUpdate.onCreate:" + e.toString());
            this.intoLoadActivity();// 进入加载界面
        }
    }

    // 公共函数----------------------------------
    // 进入加载界面
    private void intoLoadActivity() {
        Intent newIntent = new Intent(this, LoadingActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(newIntent);
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        this.finish();
    }

    // 进入设置界面
    private void intoSettingActivity() {
        Intent newIntent = new Intent(this, SettingActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(newIntent);
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        this.finish();
    }

    // 进入广告界面
    private void intoMultiPlayActivity() {
        this.appMy.intJSONId = 0;
        this.appMy.intJSONPos = 0;
        Intent newIntent = new Intent(this, MultiPlayActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(newIntent);
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        this.finish();
    }

    // 下载后，处理-------------------------------
    // 下载成功，保存数据
    // isDelFile:是否删除旧文件
    public void downSuccess(Boolean isDelFile) {
        if (isDelFile) {
            // 删除本地旧文件
            this.delFileIsNotExists();
            // 解压
            this.unzipAll();
        }
        // 获取最新数据版本
        this.appMy.setDataVersion(ClassObject.getInt(this.objMain, "v"));
        // 获取最新JSON字符串
        String strJson = ClassObject.getString(this.objMain, "p").replace("&nbsp;", " ");
        inroids.common.FileManage.saveStringToFile(this.appMy.strDataFile, this.objMain.toString());
        // inroids.common.FileManage.saveStringToFile(this.appMy.strDataFile, strJson);
        if (this.appMy.isRegister) {
            if (this.appMy.refreshData()) {// 解析成功
                //发送更新成功至后台
                new UpdateSuccessTask().execute(UpdateActivity.this.appMy.strUrl
                        + "/inter_json/updateSuccess?device=" + this.appMy.strDeviceId + "&updateType=1");

            }
        }
        this.intoSettingActivity();
    }

    // 删除不在列表中文件
    public boolean delFileIsNotExists() {
        try {
            // 扫描目录
            File filT = new File(this.appMy.strResPath);
            if (filT.exists() && filT.isDirectory()) {
                File files[] = filT.listFiles(); // 获取文件列表
                for (int i = 0; i < files.length; i++) {
                    String strFileName = files[i].getName();
                    if (files[i].isFile()) {// 处理文件
                        Boolean isFind = false;
                        JSONObject objFile = null;
                        for (int j = 0; j < this.arrFile.length(); j++) {
                            objFile = arrFile.getJSONObject(j);
                            if (objFile.getString("rpath").endsWith(strFileName)) {
                                isFind = true;
                                break;
                            }
                        }
                        if (!isFind) {// 在下载列表中没有找到此文件
                            files[i].delete();
                        }
                    } else {// 处理目录
                        Boolean isFind = false;
                        String sFile = strFileName + ".zip";
                        JSONObject objFile = null;
                        for (int j = 0; j < this.arrFile.length(); j++) {
                            objFile = arrFile.getJSONObject(j);
                            if (objFile.getString("rpath").endsWith(sFile)) {
                                isFind = true;
                                break;
                            }
                        }
                        if (!isFind) {// 在下载列表没有找到此zip文件
                            inroids.common.FileManage.delAllFile(this.appMy.strResPath + File.separator + strFileName);// 删除所有文件
                            files[i].delete();// 删除目录
                        }
                    }
                }
                return true;
            }
        } catch (Exception e) {
            MyLog.e(strTag, "FileManage.delFileIsNotExists:" + e.toString());
        }
        return false;
    }

    // 解压新文件
    public void unzipAll() {
        try {
            for (int i = 0; i < this.arrFile.length(); i++) {
                if (this.arrFile.getJSONObject(i).getString("rpath").endsWith(".zip")) {
                    // String strFolder =
                    // this.appMy.strResPath + File.separator + this.arrFile.getString(i).replace(".zip", "");
                    String rpath = this.arrFile.getJSONObject(i).getString("rpath");
                    String strFolder =
                            this.appMy.strResPath + File.separator
                                    + FileManage.getFileNameFromPath(rpath).replace(".zip", "");
                    if (!inroids.common.FileManage.isExistsFolder(strFolder)) {// 目录不存
                        inroids.common.FileManage.createFolder(strFolder);
                        inroids.common.ZipFile.UnZipFolder(
                                this.appMy.strResPath + File.separator + FileManage.getFileNameFromPath(rpath), strFolder);
                    }
                }
            }
        } catch (Exception e) {
            MyLog.e(strTag, "FileManage.delFileIsNotExists:" + e.toString());
        }
    }

    // 下载失败
    public void dowfailed() {
        this.downTimes++;
        if (this.downTimes > 100) {
            if (this.appMy.refreshData()) {// 检查解析是否成功{
                this.intoMultiPlayActivity();
                return;
            }
            // 数据错误进入设置
            this.intoSettingActivity();
        } else {
            this.intPos = -1;
            this.downFile();
        }

    }

    // 下载开始-----------------------------------------
    private void downFile() {
        this.intPos++;
        this.tvHint.setText("更新资源文件...");
        this.vwText.setText(this.intPos + " / " + this.intAllCount);// 标明当前下载进度

        if (this.intPos >= this.arrFile.length()) {// 所有文件已经全部下载
            if (this.isDownSuccess) {
                this.downSuccess(true); // 下载成功
            } else {
                this.dowfailed(); // 下载失败（可能是某个文件下载失败了）
            }
        } else {
            // 获取下载文件名称
            String strFile = "";
            try {
                strFile = this.arrFile.getJSONObject(intPos).getString("rpath");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String strUrl = this.appMy.strUrl + File.separator + strFile; // 网址
            String strFileName = inroids.common.FileManage.getFileNameFromPath(strFile);
            String strSaveFile = this.appMy.strResPath + File.separator + strFileName;// 保存地址
            if (inroids.common.FileManage.isExistsFile(strSaveFile)) { // 文件存在
                this.downFile();
            } else {
                new NetworkTask(strSaveFile).execute(strUrl, strSaveFile); //
            }
        }
    }

    // network Task----------------------------------
    class NetworkTask extends AsyncTask<String, Integer, String> {
        String strFile;

        public NetworkTask(String sFile) {
            strFile = sFile;
        }

        @Override
        protected String doInBackground(String... params) {
            return inroids.common.Network.getFile(params[0], params[1] + ".temp");
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            UpdateActivity.this.isDownSuccess = false;
            inroids.common.FileManage.delFile(strFile + ".temp");
            UpdateActivity.this.downFile();// 下一个文件
        }

        @Override
        protected void onPostExecute(String result) {
            if (inroids.common.System.isNotNullString(result)) {
                if (!inroids.common.FileManage.reName(strFile + ".temp", strFile)) {// 重命名
                    UpdateActivity.this.isDownSuccess = false;
                    inroids.common.FileManage.delFile(strFile + ".temp");
                }
                UpdateActivity.this.downFile();
            } else {
                UpdateActivity.this.isDownSuccess = false;
                System.out.println("downfiled:" + strFile);
                inroids.common.FileManage.delFile(strFile + ".temp");
                UpdateActivity.this.downFile();
            }
        }
    }


    class UpdateSuccessTask extends AsyncTask<String, Integer, String> {
        int iTag;

        UpdateSuccessTask(int iTag) {
            this.iTag = iTag;
        }

        UpdateSuccessTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            return inroids.common.Network.getString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (inroids.common.System.isNotNullString(result)) {
                UpdateActivity.this.intoMultiPlayActivity();
            }
        }
    }
}
