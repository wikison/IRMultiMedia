//
//  AppPublic.java
//  Public Variable + Public Function
//  
//  Created by sealy on 01/09/2012.  
//  Copyright 2012 Sealy, Inc. All rights reserved.  

//  
//  ....
//  <application android:name=".AppPublic">
//  .....
//   
//  Use Method: MyApp appState = ((AppPublic)getApplicationContext());

package com.inroids.irmultimedia;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import inroids.common.Convert;
import inroids.common.DateTime;
import inroids.common.FileManage;
import inroids.common.MyLog;
import inroids.common.Shell;

public class AppPublic extends Application {
    public String strDeviceId = ""; // DeviceId

    public String strRootPath = "";

    public String strMainPath = ""; // program main path

    public String strResPath = "";

    public String strUrl = null; // Server Address

    public String strWebsite = null; // 外网连接地址

    public boolean isUrl = false;

    public String strKey = "IRMultimedia";
//	public String strKey = "IRTest";

    public String strSettingFile = null; //

    public boolean isRegister = false, isUpdating = false;

    public Shell sRoot;

    public String strDataFile = null;

    public JSONArray arrJson = null;

    public int intJSONId = 0;

    public int intJSONPos = 0;

    public int intPos = 0; // Version Number

    public int intCurPos = 0; // 当前触摸时间

    public String strDownData = null;

    private Timer tmrMain = null;

    public int homePageId = -1, menuPageId = 0;

    public String apkFilePath = "";

    public int apkVersion = 0;

    // public ActivityMultiPlay actMain=null;
    public JSONTokener jsonParse;

    public int intDataUpdateTime = 5, intClockUpdateTime = 30; //

    public int intDataUpdateTimeSecond = 300, intClockUpdateTimeSecond = 300;

    public MultiPlayActivity actMain = null;

    //public picService PicService = null;

    public boolean isOnline = false;

    private String strInroidsServer = "http://www.inroids.com/standardInroids";

    // Class Create
    @Override
    public void onCreate() {
        super.onCreate();
        this.initData();
    }

    // init data
    public void initData() {
        //this.PicService = new picService();
        this.sRoot = new Shell();
        this.strRootPath = inroids.common.System.getRootPath(false); // Get Root
        // Path
        this.strSettingFile = this.strRootPath + "/inroids.irs"; // Get Inroids
        // Setting
        // file
        this.strDeviceId = inroids.common.Hardware.getSN(this); // Get Device ID
        this.strMainPath = inroids.common.System.getMainPath(this.strRootPath,
                this.strKey); // Get this Apk Path
        this.strResPath = inroids.common.System.getMainPath(this.strMainPath,
                "res"); // Get this Apk Path
        this.strDataFile = this.strMainPath + "/data.irs"; // get JSON data in
        // Main path
        // 获取注册码
        this.isRegister = this.getIsRegister(inroids.common.UserInfo.getString(
                this, "SN"));
        // 获取版本号
        this.apkVersion = inroids.common.UserInfo.getInt(this, "apkVersion");
        this.apkVersion = this.apkVersion == 0 ? 1 : this.apkVersion;
        // 数据更新时间
        this.intDataUpdateTime = inroids.common.UserInfo.getInt(this,
                "dataUpdateTime");
        this.intDataUpdateTime = this.intDataUpdateTime > 0 ? this.intDataUpdateTime
                : 5;
        this.intDataUpdateTimeSecond = this.intDataUpdateTime * 60;

    }

    // 检查注册与更新----------------------------------------------------------
    // 检查注册
    public boolean getIsRegister(String strIniSN) {
        if (inroids.common.System.isNotNullString(strIniSN)) {
            String strK = inroids.common.Security.encryptByMD5(
                    "Inroids" + strKey + this.strDeviceId + "Zhong")
                    .toUpperCase();
            String strSn = strK.substring(0, 4) + "-" + strK.substring(4, 8)
                    + "-" + strK.substring(8, 12) + "-"
                    + strK.substring(12, 16);
            return strSn.equals(strIniSN);
        }
        return false;
    }

    // 获取数据版本
    public int getDataVersion() {
        return inroids.common.UserInfo.getInt(this, "DataVersion");
    }

    // 设置数据版本
    public void setDataVersion(int value) {
        inroids.common.UserInfo.setInt(this, "DataVersion", value);
    }

    // update Data
    public void getURL() {
        inroids.common.System.setStringFromSetting(this.strSettingFile, "url",
                strInroidsServer);
        try {
            if (!this.isUrl) {
                this.strUrl = inroids.common.System.getStringFromSetting(
                        this.strSettingFile, "url"); // Get URL Address
                this.isUrl = inroids.common.System.isNotNullString(this.strUrl);
                this.startTime();// 启动
            }
        } catch (Exception e) {
            MyLog.e(this.getString(R.string.app_key), "AppPublic.updateData:"
                    + e.toString());
        }
    }

    // refresh data
    public Boolean refreshData() {
        // this.arrJson.
        if (inroids.common.System.isNotNullString(this.strDataFile)
                && inroids.common.FileManage.isExistsFile(this.strDataFile)) {
            String strData = inroids.common.FileManage
                    .getStringFromFile(this.strDataFile);
            // System.out.println(strData);
            if (inroids.common.System.isNotNullString(strData)) {
                try {
                    JSONObject objMain = new JSONObject(strData);
                    // this.jsonParse = new JSONTokener(this.strDownData);
                    // objMain = (JSONObject)jsonParse.nextValue();
                    if (!objMain.isNull("p")) {
                        this.arrJson = objMain.getJSONArray("p");
                        if (this.arrJson.length() > 0) {
                            this.changeFileIfOverdue(strData);
                            return true;
                        }
                    }
                } catch (JSONException e) {
                    MyLog.e(this.getString(R.string.app_key),
                            "AppPublic.refreshData:" + e.toString());
                }
            }
        }
        return false;
    }

    public void changeFileIfOverdue(String strData) {
        JSONObject objFile = null;
        String path = "", eTime = "";
        try {
            JSONObject objMain = new JSONObject(strData);
            if (!objMain.isNull("f")) {
                JSONArray arrFile = objMain.getJSONArray("f");
                for (int i = 0, size = arrFile.length(); i < size; i++) {
                    objFile = arrFile.getJSONObject(i);
                    path = ClassObject.getString(objFile, "rpath");
                    eTime = ClassObject.getString(objFile, "edate");
                    // System.out.println(eTime);

                    if (DateTime.dateOverdue(eTime, "yyyy-MM-dd")) {
                        String fileOrFolderName = FileManage
                                .getFileNameFromPath(path);
                        if (path.endsWith(".zip")) {
                            // 删除过期zip包及解压zip包后的文件夹中的文件
                            FileManage.delFile(this.strResPath + File.separator
                                    + fileOrFolderName);
                            FileManage.delAllFile(this.strResPath
                                    + File.separator + fileOrFolderName);
                        } else {
                            // 删除Res中的过期文件
                            FileManage.delFile(this.strResPath + File.separator
                                    + fileOrFolderName);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPicCurrentTime() {
        String result = "";
        StringBuffer sb = new StringBuffer();
//		int tag = this.PicService.GetPICCurrentTime(sb);
//		result = tag == 0 ? sb.toString() : "";
        return result;
    }

    public String getPicStartUpTime() {
        String result = "";
        StringBuffer sb = new StringBuffer();
//		int tag = this.PicService.GetPICStartUpTime(sb);
//		result = tag == 0 ? sb.toString() : "";
        return result;
    }

    public String getPicShutDownTime() {
        String result = "";
        StringBuffer sb = new StringBuffer();
//		int tag = this.PicService.GetPICShutDownTime(sb);
//		result = tag == 0 ? sb.toString() : "";
        return result;
    }

    public int shutDown(String eTime) {
        int hint = -1;
//		hint = this.PicService.SetPICShutDownTime(eTime);
        return hint;
    }

    public int startUp(String eTime) {
        int hint = -1;
//		hint = this.PicService.SetPICStartUpTime(eTime);
        return hint;
    }

    public void startTime() {
        try {
            if (this.isUrl && this.tmrMain == null) {
                this.tmrMain = new Timer();
                // time update
                this.tmrMain.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = 1;
                        AppPublic.this.handler.sendMessage(message);
                    }
                }, 0, 1000);
            }
        } catch (Exception e) {
            MyLog.e(this.getString(R.string.app_key), "AppPublic.startTime:"
                    + e.toString());
        }
    }

    private int mVisitId = -1;

    public void updateVisitCount(int turnId) {
        this.mVisitId = turnId;
        handler.obtainMessage(2).sendToTarget();
    }

    // update time
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                if (msg.what == 1) {
                    if (!AppPublic.this.isUpdating) {
                        AppPublic.this.intPos++;
                        if (AppPublic.this.intPos > 3600)
                            AppPublic.this.intPos = 0;
                        if (AppPublic.this.intPos
                                % AppPublic.this.intDataUpdateTimeSecond == 30) {
                            new NetworkTask(1).execute(AppPublic.this.strUrl
                                    + "/inter_json/getData_pad?device=" + AppPublic.this.strDeviceId);
                        }
                    }
                } else if (msg.what == 2) {
//					new NetworkTask().execute(AppPublic.this.strUrl
//							+ "/system?action=getBillPageHistory&pid="
//							+ AppPublic.this.mVisitId);
                }
            } catch (Exception e) {
                MyLog.e(AppPublic.this.getString(R.string.app_key),
                        "AppPublic.handler:" + e.toString());
            }
            super.handleMessage(msg);
        }
    };

    class NetworkTask extends AsyncTask<String, Integer, String> {

        int iTag;

        NetworkTask(int iTag) {
            this.iTag = iTag;
        }

        NetworkTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            return inroids.common.Network.getString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            switch (iTag) {
                // 检查更新
                case 1:
                    checkDataUpdate(result);
                    break;
                case 2:
                    break;
            }
        }
    }

    private void checkDataUpdate(String result) {
        if (inroids.common.System.isNotNullString(result)) {
            if (result.startsWith("{\"r\":\"0\"")) {
                try {
                    JSONObject jo = new JSONObject(result);
                    int v = inroids.common.ClassObject.getInt(jo, "v");
                    if (v != this.getDataVersion()) {
                        AppPublic.this.strDownData = result;
                        Intent newIntent = new Intent(AppPublic.this.actMain,
                                UpdateActivity.class);
                        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        AppPublic.this.actMain.startActivity(newIntent);
                        AppPublic.this.actMain.overridePendingTransition(
                                android.R.anim.fade_in, android.R.anim.fade_out);
                        AppPublic.this.actMain.finish();
                    }
                } catch (Exception e) {
                }
            }
        }
    }


    public void getSetDeviceTime(String result) {
        String sFormatDate = "yyyy-MM-dd";
        String sFormatTime = "yyyy-MM-dd HH:mm:ss";
        String beforeCurrentTime = "";
        inroids.common.UserInfo.setString(this, "onoffTime", result);
        System.out
                .println(inroids.common.UserInfo.getString(this, "onoffTime"));
        Date sDate = new Date();
        Date eDate = new Date();
        String picCurrentTime = this.getPicCurrentTime();
        if (inroids.common.System.isNotNullString(picCurrentTime)) {
            beforeCurrentTime = Convert.dateToString(
                    DateTime.getDateFromDay(Convert.stringToDate(
                            picCurrentTime, "yyyyMMddHHmmss"), -1),
                    "yyyyMMddHHmmss");
        } else {
            beforeCurrentTime = Convert.dateToString(
                    DateTime.getDateFromDay(new Date(), -1), "yyyyMMddHHmmss");
        }
        try {
            JSONObject joDeviceTime = new JSONObject(result);
            if (joDeviceTime.getInt("r") == 0) {
                String sTime = joDeviceTime.getString("stime");
                String eTime = joDeviceTime.getString("etime");
                int whichDay = DateTime.dayOfWeek(DateTime
                        .formatCurTime(sFormatDate));
                // 判断当天是不是在设置的策略中
                if (joDeviceTime.getString("days").contains(
                        String.valueOf(whichDay))) {
                    // 开关机时间都不为空
                    if (inroids.common.System.isNotNullString(eTime)
                            && inroids.common.System.isNotNullString(sTime)) {
                        String dateETime = DateTime.formatCurTime(sFormatDate)
                                + " " + eTime;
                        eDate = Convert.stringToDate(dateETime, sFormatTime);
                        if (DateTime.dateOverdue(dateETime, sFormatTime)) {
                            int inteval = DateTime.getIntervalDays(whichDay,
                                    joDeviceTime.getString("days"));
                            eDate = DateTime.getDateFromDay(Convert
                                            .stringToDate(dateETime, sFormatTime),
                                    inteval);
                        } else {
                        }

                        eTime = Convert.dateToString(eDate, "yyyyMMddHHmmss");
                        System.out.println("eTime=" + eTime);
                        this.shutDown(eTime);

                        String dateSTime = Convert.dateToString(eDate,
                                sFormatDate) + " " + sTime;
                        sDate = Convert.stringToDate(dateSTime, sFormatTime);
                        // 开机时间在关机时间之前
                        if (sDate.before(eDate)) {
                            // 设为数组中的"下一次"
                            int eWhichDay = DateTime.dayOfWeek(Convert
                                    .dateToString(eDate, sFormatTime));
                            int inteval = DateTime.getIntervalDays(eWhichDay,
                                    joDeviceTime.getString("days"));
                            sDate = DateTime.getDateFromDay(Convert
                                            .stringToDate(dateSTime, sFormatTime),
                                    inteval);
                        } else {
                        }
                        sTime = Convert.dateToString(sDate, "yyyyMMddHHmmss");
                        System.out.println("stime=" + sTime);
                        this.startUp(sTime);
                    }
                    // 关机时间不为空,开机时间为空
                    else if (inroids.common.System.isNotNullString(eTime)
                            && !inroids.common.System.isNotNullString(sTime)) {
                        String dateETime = DateTime.formatCurTime(sFormatDate)
                                + " " + eTime;
                        eDate = Convert.stringToDate(dateETime, sFormatTime);
                        if (DateTime.dateOverdue(dateETime, sFormatTime)) {
                            int inteval = DateTime.getIntervalDays(whichDay,
                                    joDeviceTime.getString("days"));
                            eDate = DateTime.getDateFromDay(Convert
                                            .stringToDate(dateETime, sFormatTime),
                                    inteval);
                        } else {
                        }
                        eTime = Convert.dateToString(eDate, "yyyyMMddHHmmss");
                        System.out.println("eTime=" + eTime);
                        this.shutDown(eTime);
                        this.startUp(beforeCurrentTime);
                    }
                    // 关机时间为空, 开机时间不为空
                    else if (!inroids.common.System.isNotNullString(eTime)
                            && inroids.common.System.isNotNullString(sTime)) {
                        String dateSTime = DateTime.formatCurTime(sFormatDate)
                                + " " + sTime;
                        sDate = Convert.stringToDate(dateSTime, sFormatTime);
                        if (DateTime.dateOverdue(dateSTime, sFormatTime)) {
                            int inteval = DateTime.getIntervalDays(whichDay,
                                    joDeviceTime.getString("days"));
                            sDate = DateTime.getDateFromDay(Convert
                                            .stringToDate(dateSTime, sFormatTime),
                                    inteval);
                        } else {
                        }
                        sTime = Convert.dateToString(sDate, "yyyyMMddHHmmss");
                        System.out.println("sTime=" + sTime);
                        this.startUp(sTime);
                        this.shutDown(beforeCurrentTime);
                    }
                } else {
                    if (inroids.common.System.isNotNullString(eTime)
                            && inroids.common.System.isNotNullString(sTime)) {
                        String dateETime = DateTime.formatCurTime(sFormatDate)
                                + " " + eTime;
                        eDate = Convert.stringToDate(dateETime, sFormatTime);
                        int inteval = DateTime.getIntervalDays(whichDay,
                                joDeviceTime.getString("days"));
                        eDate = DateTime.getDateFromDay(
                                Convert.stringToDate(dateETime, sFormatTime),
                                inteval);
                        eTime = Convert.dateToString(eDate, "yyyyMMddHHmmss");
                        System.out.println("eTime=" + eTime);
                        this.shutDown(eTime);
                        String dateSTime = Convert.dateToString(eDate,
                                sFormatDate) + " " + sTime;
                        sDate = Convert.stringToDate(dateSTime, sFormatTime);
                        // 开机时间在关机时间之前
                        if (sDate.before(eDate)) {
                            // 设为数组中的"下一次"
                            int eWhichDay = DateTime.dayOfWeek(Convert
                                    .dateToString(eDate, sFormatTime));
                            int intevalEnd = DateTime.getIntervalDays(
                                    eWhichDay, joDeviceTime.getString("days"));
                            sDate = DateTime.getDateFromDay(Convert
                                            .stringToDate(dateSTime, sFormatTime),
                                    intevalEnd);
                        } else {
                        }
                        sTime = Convert.dateToString(sDate, "yyyyMMddHHmmss");
                        System.out.println("sTime=" + sTime);
                        this.startUp(sTime);
                    } else if (inroids.common.System.isNotNullString(eTime)
                            && !inroids.common.System.isNotNullString(sTime)) {
                        String dateETime = DateTime.formatCurTime(sFormatDate)
                                + " " + eTime;
                        eDate = Convert.stringToDate(dateETime, sFormatTime);
                        int inteval = DateTime.getIntervalDays(whichDay,
                                joDeviceTime.getString("days"));
                        eDate = DateTime.getDateFromDay(
                                Convert.stringToDate(dateETime, sFormatTime),
                                inteval);
                        eTime = Convert.dateToString(eDate, "yyyyMMddHHmmss");
                        System.out.println("eTime=" + eTime);
                        this.shutDown(eTime);
                        this.startUp(beforeCurrentTime);
                    } else if (!inroids.common.System.isNotNullString(eTime)
                            && inroids.common.System.isNotNullString(sTime)) {
                        String dateSTime = DateTime.formatCurTime(sFormatDate)
                                + " " + sTime;
                        sDate = Convert.stringToDate(dateSTime, sFormatTime);
                        int inteval = DateTime.getIntervalDays(whichDay,
                                joDeviceTime.getString("days"));
                        sDate = DateTime.getDateFromDay(
                                Convert.stringToDate(dateSTime, sFormatTime),
                                inteval);
                        sTime = Convert.dateToString(sDate, "yyyyMMddHHmmss");
                        System.out.println("sTime=" + sTime);
                        this.startUp(sTime);
                        this.shutDown(beforeCurrentTime);
                    }
                }
            } else if (joDeviceTime.getInt("r") == 1) {
                // 将开关机时间设为当前时间之前
                this.shutDown(beforeCurrentTime);
                this.startUp(beforeCurrentTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}