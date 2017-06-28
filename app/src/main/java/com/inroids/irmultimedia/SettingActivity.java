//Main Activity
//Created by sealy on 2012-12-01.  
//Copyright 2012 Sealy, Inc. All rights reserved.

package com.inroids.irmultimedia;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import inroids.common.MyLog;

public class SettingActivity extends Activity {
    private static final String TAG = "IRMultimedia";

    private AppPublic appMy;

    private TextView txtDetail;

    private int intSingleChoiceID = -1;

    private String strUpdateTime[] = new String[]{
            "1", "3", "5", "10", "20", "30", "40", "50", "60"
    };

    String days = "", turnOnTime = "", turnOffTime = "";

    // Create and release control----------------------------
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.appMy = ((AppPublic) getApplicationContext());
        this.appMy.isUpdating = true;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        // create TextView
        this.createTextView(dm);
        // Create button
        this.createButton(dm);
        // get Device infomation
        this.getDeviceInfo();
    }

    // create text View
    private void createTextView(DisplayMetrics dm) {
        try {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(dm.widthPixels - 540 + 170, dm.heightPixels);
            params.setMargins(10, 0, dm.widthPixels - 540 + 170, dm.heightPixels);
            this.txtDetail = new TextView(this);
            this.txtDetail.setTextSize(24);
            this.txtDetail.setTextColor(inroids.common.Convert.stringToColor("#000000")); // Back Color
            this.txtDetail.setMovementMethod(ScrollingMovementMethod.getInstance());
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(inroids.common.Convert.stringToColor("#ffffff"));
            gd.setCornerRadii(new float[]{
                    10, 10, 10, 10, 10, 10, 10, 10
            });
            this.txtDetail.setBackgroundDrawable(gd);
            this.addContentView(this.txtDetail, params);
        } catch (Exception e) {
            MyLog.e(TAG, "ActivitySetting.createTextView:" + e.toString());
        }
    }

    // Create button
    private void createButton(DisplayMetrics dm) {
        this.addButton(dm.widthPixels - 340, 10 + 90 * 0, "获取设备信息", 1);
        this.addButton(dm.widthPixels - 170, 10 + 90 * 0, "测试服务器", 2);
        this.addButton(dm.widthPixels - 340, 10 + 90 * 1, "获取注册码", 3);
        this.addButton(dm.widthPixels - 170, 10 + 90 * 1, "输入注册码", 4);
        this.addButton(dm.widthPixels - 340, 10 + 90 * 2, "获取更新数据", 5);
        this.addButton(dm.widthPixels - 170, 10 + 90 * 2, "更新数据", 6);
        this.addButton(dm.widthPixels - 340, 10 + 90 * 3, "设置网址", 14);
        this.addButton(dm.widthPixels - 170, 10 + 90 * 3, "设置更新时间", 12);

        this.addButton(dm.widthPixels - 340, 10 + 90 * 5, "设置开关机时间", 11);
        this.addButton(dm.widthPixels - 170, 10 + 90 * 5, "获取服务器时间", 7);
        this.addButton(dm.widthPixels - 340, 10 + 90 * 6, "进入广告页面", 13);

        this.addButton(dm.widthPixels - 340, 10 + 90 * 8, "重启设备", 15);
        this.addButton(dm.widthPixels - 170, 10 + 90 * 8, "退出视图", 16);
    }

    // add button
    private void addButton(int left, int top, String aTitle, int tag) {
        try {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(160, 80);
            params.setMargins(left, top, left + 160, top + 80);
            Button btnT = new Button(this);
            btnT.setText(aTitle);
            btnT.setTextSize(16);
            btnT.setTag(tag);
            btnT.setOnClickListener(buttonClick);
            if (tag == 9) {
                btnT.setTextColor(Color.RED);
                btnT.getPaint().setFakeBoldText(true);
            }
            this.addContentView(btnT, params);
        } catch (Exception e) {
            MyLog.e(TAG, "ActivitySetting.addButton:" + e.toString());
        }
    }

    // button event-------------------------------------------------------------------
    // Button Click Listener
    private OnClickListener buttonClick = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            int intTag = Integer.valueOf(arg0.getTag().toString());
            String strUrl;
            switch (intTag) {
                case 2: // get data from web service
                    strUrl = SettingActivity.this.appMy.strUrl;
                    new NetworkRead(2, strUrl).execute(strUrl);
                    break;
                case 3:// get License
                    strUrl =
                            SettingActivity.this.appMy.strUrl + "/inter_json/getSNInfo?device="
                                    + SettingActivity.this.appMy.strDeviceId;
                    new NetworkRead(3, strUrl).execute(strUrl);
                    break;
                case 4: // update License
                    SettingActivity.this.updateLicense();
                    break;
                case 5: // update data
                    strUrl =
                            SettingActivity.this.appMy.strUrl + "/inter_json/getData_pad?device="
                                    + SettingActivity.this.appMy.strDeviceId;
                    new NetworkRead(5, strUrl).execute(strUrl);
                    break;
                case 6: // get data from web service
                    strUrl =
                            SettingActivity.this.appMy.strUrl + "/inter_json/getData_pad?device="
                                    + SettingActivity.this.appMy.strDeviceId;
                    new NetworkRead(6, strUrl).execute(strUrl);
                    break;
                case 7:
                    strUrl = SettingActivity.this.appMy.strUrl + "/system?action=getTime";
                    new NetworkRead(3, strUrl).execute(strUrl);
                    break;
                case 11:
                    SettingActivity.this.setTurnOnOffTime();
                    break;
                case 12: // 设置更新时间
                    SettingActivity.this.setDataUpdateTime();
                    break;
                case 13: // update License
                    SettingActivity.this.intoMultiPlay();
                    break;
                case 14:// update Service URL
                    SettingActivity.this.updateServiceURL();
                    break;
                case 15:// 重启设备
                    inroids.common.AppManage.reboot(SettingActivity.this.appMy.sRoot);
                    break;
                case 16: {// 退出当前视图
                    SettingActivity.this.finish();
                    break;
                }
                default:// 获取基本信息
                    SettingActivity.this.getDeviceInfo();
                    break;
            }
        }
    };

    // 获取设配信息(1)---------------------------
    private void getDeviceInfo() {
        try {
            String strT = " 序　　　号：" + this.appMy.strDeviceId;
            if (this.appMy.isRegister) {
                strT = strT + "\n 是 否  注 册：已注册";
            } else {
                strT = strT + "\n 是 否  注 册：未注册";
            }
            strT = strT + "\n 根　目　录：" + this.appMy.strRootPath;
            strT = strT + "\n 主　目　录：" + this.appMy.strMainPath;
            strT = strT + "\n 服务器地址：" + this.appMy.strUrl;
            strT = strT + "\n 版本号:" + inroids.common.AppManage.getAppVersionName(this);
            strT = strT + "\n PIC 时间：" + this.appMy.getPicCurrentTime();
            strT = strT + "\n 定时开机时间：" + this.appMy.getPicStartUpTime();
            strT = strT + "\n 定时关机时间：" + this.appMy.getPicShutDownTime();
            strT = strT + "\n 数据更新间隔时间：" + this.appMy.intDataUpdateTime + " 分钟";
            strT = strT + "\n 无  线  Mac：" + inroids.common.Hardware.getWifiMac(this);
            strT = strT + "\n 无线　　Ip：" + inroids.common.Hardware.getWifiIp(this);
            strT = strT + "\n 无线　网关：" + inroids.common.Hardware.getWifiGateway(this);
            strT = strT + "\n 有线　Mac：" + inroids.common.Hardware.getEthernetMac();
            strT = strT + "\n 有线　　Ip：" + inroids.common.Hardware.getEthernetIp();
            if (inroids.common.Network.isNetworkAvailable(this)) {
                strT = strT + "\n 网 络  连 接：成功...！";
            } else {
                strT = strT + "\n 网 络  连 接：失败，请检查网线、网络设备、网络电源等";
            }
            this.txtDetail.setEnabled(true);
            this.txtDetail.setText(strT);
        } catch (Exception e) {
            MyLog.e(TAG, "ActivitySetting.getDeviceInfo:" + e.toString());
        }
    }

    // 获取注册码(3)
    private void checkLicense(String strT) {
        try {
            if (inroids.common.System.isNotNullString(strT)) {
                String strKey = "\"SN\":\"";
                if (strT.contains(strKey)) {
                    int iStart = strT.indexOf(strKey);
                    int iEnd = strT.indexOf("\"", iStart + strKey.length());
                    if (iStart >= 0 && iEnd >= 0) {
                        String strK = strT.substring(iStart + strKey.length(), iEnd).toUpperCase();
                        this.appMy.isRegister = this.appMy.getIsRegister(strK);
                        if (this.appMy.isRegister) {
                            txtDetail.setText(txtDetail.getText().toString() + "\n 结果：注册成功！");
                            inroids.common.UserInfo.setString(this, "SN", strK);
                        } else {
                            txtDetail.setText(txtDetail.getText().toString() + "\n 结果：注册失败！");
                        }
                    }
                }
            }
        } catch (Exception e) {
            MyLog.e(TAG, "ActivitySetting.getLicense:" + e.toString());
        }
    }

    // 输入注册码(4)
    private void updateLicense() {
        try {
            final EditText edtNew = new EditText(this);
            edtNew.setText(inroids.common.UserInfo.getString(this, "SN"));
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("请输入注册码");
            builder.setIcon(android.R.drawable.ic_input_get);
            builder.setView(edtNew);
            // Ok Button
            builder.setNeutralButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String strT = edtNew.getText().toString().toUpperCase();
                    if (inroids.common.System.isNotNullString(strT)) {
                        SettingActivity.this.appMy.isRegister = SettingActivity.this.appMy.getIsRegister(strT);
                        if (SettingActivity.this.appMy.isRegister) {
                            inroids.common.UserInfo.setString(SettingActivity.this, "SN", strT);
                            SettingActivity.this.getDeviceInfo();
                        } else {
                            inroids.common.System.hintWithToast(SettingActivity.this, "注册码不正确，注册失败，请与银铼科技联系！");
                        }
                    }
                }
            });
            // Cancel button
            builder.setNegativeButton("取消", disMiss);
            builder.create().show();
        } catch (Exception e) {
            MyLog.e(TAG, "ActivitySetting.updateLicense:" + e.toString());
        }
    }

    // 设置定时开关机时间(11)
    private void setTurnOnOffTime() {
        final String onoffTime = inroids.common.UserInfo.getString(this, "onoffTime");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.turnon_dialog, null);
        final EditText etOnTime = (EditText) view.findViewById(R.id.td_et_kj);
        final EditText etOffTime = (EditText) view.findViewById(R.id.td_et_gj);
        final CheckBox cb1, cb2, cb3, cb4, cb5, cb6, cb7;
        cb1 = (CheckBox) view.findViewById(R.id.td_cb_xq1);
        cb2 = (CheckBox) view.findViewById(R.id.td_cb_xq2);
        cb3 = (CheckBox) view.findViewById(R.id.td_cb_xq3);
        cb4 = (CheckBox) view.findViewById(R.id.td_cb_xq4);
        cb5 = (CheckBox) view.findViewById(R.id.td_cb_xq5);
        cb6 = (CheckBox) view.findViewById(R.id.td_cb_xq6);
        cb7 = (CheckBox) view.findViewById(R.id.td_cb_xq7);
        try {
            JSONObject joDeviceTime = new JSONObject(onoffTime);
            if (joDeviceTime.getInt("r") == 0) {
                String sTime = joDeviceTime.getString("stime");
                String eTime = joDeviceTime.getString("etime");
                String days = joDeviceTime.getString("days");
                if (sTime.length() > 0)
                    etOnTime.setText(sTime.substring(0, 2) + sTime.substring(3, 5));
                if (eTime.length() > 0)
                    etOffTime.setText(eTime.substring(0, 2) + eTime.substring(3, 5));
                if (days.contains("1"))
                    cb1.setChecked(true);
                if (days.contains("2"))
                    cb2.setChecked(true);
                if (days.contains("3"))
                    cb3.setChecked(true);
                if (days.contains("4"))
                    cb4.setChecked(true);
                if (days.contains("5"))
                    cb5.setChecked(true);
                if (days.contains("6"))
                    cb6.setChecked(true);
                if (days.contains("7"))
                    cb7.setChecked(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        builder.setView(view);
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                days = "";
                turnOnTime = "";
                turnOffTime = "";
                if (cb1.isChecked())
                    days = days + "1,";
                if (cb2.isChecked())
                    days = days + "2,";
                if (cb3.isChecked())
                    days = days + "3,";
                if (cb4.isChecked())
                    days = days + "4,";
                if (cb5.isChecked())
                    days = days + "5,";
                if (cb6.isChecked())
                    days = days + "6,";
                if (cb7.isChecked())
                    days = days + "7,";
                if (days.length() > 1) {
                    days = days.substring(0, days.length() - 1);
                } else {
                    txtDetail.setText("未设置开关机策略");
                    // Toast.makeText(SettingActivity.this, "未设置策略", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etOnTime.getText().toString().length() == 4) {
                    turnOnTime =
                            etOnTime.getText().toString().substring(0, 2) + ":"
                                    + etOnTime.getText().toString().substring(2, 4) + ":00";
                    if (Integer.valueOf(turnOnTime.substring(0, 2)) > 23
                            || Integer.valueOf(turnOnTime.substring(3, 5)) > 59) {
                        // Toast.makeText(SettingActivity.this, "设置开机时间有误", Toast.LENGTH_SHORT).show();
                        txtDetail.setText("开机时间设置有误");
                        return;
                    }
                } else if (etOnTime.getText().toString().length() < 4 && etOnTime.getText().toString().length() > 0) {
                    // Toast.makeText(SettingActivity.this, "设置开机时间有误", Toast.LENGTH_SHORT).show();
                    txtDetail.setText("开机时间设置有误");
                    return;
                }
                if (etOffTime.getText().toString().length() == 4) {
                    turnOffTime =
                            etOffTime.getText().toString().substring(0, 2) + ":"
                                    + etOffTime.getText().toString().substring(2, 4) + ":00";
                    if (Integer.valueOf(turnOffTime.substring(0, 2)) > 23
                            || Integer.valueOf(turnOffTime.substring(3, 5)) > 59) {
                        Toast.makeText(SettingActivity.this, "设置关机时间有误", Toast.LENGTH_SHORT).show();
                        txtDetail.setText("关机时间设置有误");
                        return;
                    }
                } else if (etOffTime.getText().toString().length() < 4 && etOffTime.getText().toString().length() > 0) {
                    Toast.makeText(SettingActivity.this, "设置关机时间有误", Toast.LENGTH_SHORT).show();
                    return;
                }
                String strUrl =
                        SettingActivity.this.appMy.strUrl + "/system?action=setDeviceTime&sn="
                                + SettingActivity.this.appMy.strDeviceId + "&stime=" + turnOnTime + "&etime=" + turnOffTime
                                + "&days=" + days;
                new NetworkRead(11, strUrl).execute(strUrl);

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        builder.create().show();
    }

    private void checkTurnOnOff(String strT) {
        String onStateTxt = "", offStateTxt = "";
        try {
            JSONObject jo = new JSONObject(strT);
            int r = jo.getInt("r");
            if (r == 0) {
                String userInfo = "";
                if (turnOnTime.length() == 0 && turnOffTime.length() == 0) {
                    userInfo = "{\"r\":\"1\"}";
                } else {
                    userInfo =
                            "{\"r\":\"0\",\"stime\":\"" + turnOnTime + "\",\"etime\":\"" + turnOffTime + "\",\"days\":\""
                                    + days + "\"}";
                }
                inroids.common.UserInfo.setString(this, "onoffTime", userInfo);
                String result = inroids.common.UserInfo.getString(this, "onoffTime");
                Toast.makeText(SettingActivity.this, "开关机时间同步成功", Toast.LENGTH_SHORT).show();
                // 服务器同步成功, 设置本地
                this.appMy.getSetDeviceTime(result);

                txtDetail.setText("\n同步成功\n" + onStateTxt + "\n" + offStateTxt);

            } else if (r == 1) {
                txtDetail.setText("设置失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 获取默认更新时间(12)
    private int getDefaultUpdateTime() {
        try {
            for (int i = 0; i < this.strUpdateTime.length; i++) {
                int intT = Integer.valueOf(this.strUpdateTime[i]);
                if (this.appMy.intDataUpdateTime == intT) {
                    return i;
                }
            }
        } catch (Exception e) {
            MyLog.e(TAG, "ActivitySetting.getDefaultUpdateTime:" + e.toString());
        }
        return -1;
    }

    // 设置更新时间
    private void setDataUpdateTime() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("请数据更新时间");
            builder.setIcon(android.R.drawable.ic_input_get);
            this.intSingleChoiceID = this.getDefaultUpdateTime();
            builder.setSingleChoiceItems(this.strUpdateTime, this.intSingleChoiceID,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            SettingActivity.this.intSingleChoiceID = whichButton;
                        }
                    });
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String strT = null;
                    if (SettingActivity.this.intSingleChoiceID >= 0) {
                        strT = SettingActivity.this.strUpdateTime[SettingActivity.this.intSingleChoiceID];
                        SettingActivity.this.appMy.intDataUpdateTime = Integer.valueOf(strT);
                        SettingActivity.this.appMy.intDataUpdateTime =
                                SettingActivity.this.appMy.intDataUpdateTime > 0 ? SettingActivity.this.appMy.intDataUpdateTime
                                        : 5;
                        SettingActivity.this.appMy.intDataUpdateTimeSecond =
                                SettingActivity.this.appMy.intDataUpdateTime * 60;
                        inroids.common.UserInfo.setInt(SettingActivity.this, "dataUpdateTime",
                                SettingActivity.this.appMy.intDataUpdateTime);
                        SettingActivity.this.getDeviceInfo();
                    }
                }
            });
            builder.setNegativeButton(R.string.cancel, disMiss);
            builder.create().show();
        } catch (Exception e) {
            MyLog.e(TAG, "ActivitySetting.updateDisplay:" + e.toString());
        }
    }

    // 进入广告页面(13)
    private void intoMultiPlay() {
        // 未注册
        if (!this.appMy.isRegister) {
            this.getDeviceInfo();
        } else {
            if (this.appMy.refreshData()) {// 解析成功
                this.intoMultiPlayActivity();
                return;
            } else {
                this.txtDetail.setText("没有找到配置文件:" + this.appMy.strDataFile);
            }
        }
    }

    // 设置网址(14)
    private void updateServiceURL() {
        try {
            final EditText edtNew = new EditText(this);
            if (this.appMy.isUrl)
                edtNew.setText(this.appMy.strUrl);
            else
                edtNew.setText("http://");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("请输入服务器地址");
            builder.setIcon(android.R.drawable.ic_input_get);
            builder.setView(edtNew);
            // Ok Button
            builder.setNeutralButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String strT = edtNew.getText().toString();
                    //strT = "http://www.inroids.com/standardInroids";
                    if (inroids.common.System.isNotNullString(strT)) {
                        inroids.common.System.setStringFromSetting(SettingActivity.this.appMy.strSettingFile, "url",
                                strT);
                        SettingActivity.this.appMy.strUrl =
                                inroids.common.System
                                        .getStringFromSetting(SettingActivity.this.appMy.strSettingFile, "url"); // Get URL
                        // Address
                        SettingActivity.this.appMy.isUrl =
                                inroids.common.System.isNotNullString(SettingActivity.this.appMy.strUrl);
                        SettingActivity.this.getDeviceInfo();
                    }
                }
            });
            // Cancel button
            builder.setNegativeButton("取消", disMiss);
            builder.create().show();
        } catch (Exception e) {
            MyLog.e(TAG, "ActivitySetting.updateServiceURL:" + e.toString());
        }
    }

    // public subject--------------------------------------------------------
    // 进入广告界面
    private void intoMultiPlayActivity() {
        Intent intent = new Intent(this, MainService.class);
        startService(intent);
        intent = new Intent(this, MultiPlayActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        this.finish();
    }

    // 进入更新界面
    private void intoUpdateActivity() {
        Intent newIntent = new Intent(this, UpdateActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(newIntent);
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        this.finish();
    }

    // dismiss button
    private DialogInterface.OnClickListener disMiss = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {
            dialog.dismiss();
        }
    };

    // print web info
    private void printWebInfo(String url, String result, String strData) {
        try {
            if (inroids.common.System.isNotNullString(url)) {
                if (inroids.common.System.isNotNullString(result)) {
                    if (inroids.common.System.isNotNullString(strData))
                        txtDetail.setText(" 测试成功，数据如下：\n 地址：" + url + "\n " + strData + "\n 返回数据：" + result);
                    else
                        txtDetail.setText(" 测试成功，数据如下：\n 地址：" + url + " \n 返回数据：" + result);
                } else {
                    txtDetail.setText(" 测试失败!原因如下：" + "\n 地址:" + url + "\n 1、网址错误或服务器没有开启。");
                }
            } else {
                txtDetail.setText(" 服务器地址不存在：" + "\n 地址:" + url + "\n 1、请检查<根目录>是否存在<inroids.irs>文件"
                        + "\n 2、如果文件存在,请检查<inroids.irs>未设置网址");
            }
        } catch (Exception e) {
            MyLog.e(TAG, "ActivitySetting.printWebInfo:" + e.toString());
        }
    }

    // get information from service
    class NetworkRead extends AsyncTask<String, Integer, String> {
        int intTag;

        String strUrl;

        public ProgressDialog myDialog = null;

        public NetworkRead(int iTag, String sUrl) {
            intTag = iTag;
            strUrl = sUrl;
            myDialog = ProgressDialog.show(SettingActivity.this, "银铼科技", "网络访问中,请稍后...");
        }

        @Override
        protected String doInBackground(String... params) {
            return inroids.common.Network.getString(params[0]);
        }

        @Override
        protected void onCancelled() {
            myDialog.dismiss();
            super.onCancelled();

        }

        @Override
        protected void onPostExecute(String result) {
            myDialog.dismiss();
            if (inroids.common.System.isNotNullString(result)) {
                switch (intTag) {
                    case 2: // 测试服务器是否连通...
                        SettingActivity.this.printWebInfo(strUrl, result, null);
                        break;
                    case 3:// get License
                        SettingActivity.this.printWebInfo(strUrl, result, null);
                        SettingActivity.this.checkLicense(result);
                        break;
                    case 5: // 获取更新数据...
                        SettingActivity.this.printWebInfo(strUrl, result, null);
                        break;
                    case 7: // 获取服务器时间...
                        SettingActivity.this.printWebInfo(strUrl, result, null);
                        if (inroids.common.System.isNotNullString(result)) {
                            inroids.common.System.updateSystemTime(SettingActivity.this.appMy.sRoot, result);
//                        SettingActivity.this.appMy.PicService.SetPICDateTime(DateTime.formatCurTime("yyyyMMddHHmmss"));
                        }
                        break;
                    case 11:
                        SettingActivity.this.printWebInfo(strUrl, result, null);
                        SettingActivity.this.checkTurnOnOff(result);
                        break;
                    case 6: {// 更新数据
                        if (inroids.common.System.isNotNullString(result)) {
                            if (result.startsWith("{\"r\":\"0\"")) {
                                try {
                                    JSONObject jo = new JSONObject(result);
                                    int r = inroids.common.ClassObject.getInt(jo, "v");
                                    if (r != SettingActivity.this.appMy.getDataVersion()) {
                                        SettingActivity.this.appMy.strDownData = result;// 保留下载数据
                                        SettingActivity.this.intoUpdateActivity();// 进入数据更新界面
                                        return;
                                    }
                                } catch (Exception e) {

                                }

                            }
                        }
                    }
                    break;
                }
            } else {
                SettingActivity.this.printWebInfo(strUrl, result, null);
            }
        }
    }

}
