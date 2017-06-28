package com.inroids.irmultimedia;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import inroids.common.MyLog;

public class MainService extends Service {
    public AppPublic appMy; // update Check

    private MyBinder mBinder = new MyBinder();

    private Timer tmrMain = null;

    private int intUpdateTimes = 30;

    LinearLayout mFloatLayout;

    // 创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;
    WindowManager.LayoutParams mWmParams;
    TextView mTvOnline, mTvVer;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        this.appMy = ((AppPublic) getApplicationContext());
        inroids.common.System.hintWithToast(this,
                "IRMultimedia Service onCreate~~~");
        super.onCreate();
        creatFloatWindow();
        this.createService(); // Create service
    }

    @Override
    public void onDestroy() {
        inroids.common.System.hintWithToast(this,
                "IRMultimedia Service onDestroy~~~");
        super.onDestroy();
        if (mFloatLayout != null) {
            // 移除悬浮窗口
            mWindowManager.removeView(mFloatLayout);
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public class MyBinder extends Binder {
        MainService getService() {
            return MainService.this;
        }
    }

    // Create Service
    private void createService() {
        // Main Timer
        this.tmrMain = new Timer();
        // time update
        this.tmrMain.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                MainService.this.handler.sendMessage(message);
            }
        }, 0, 1000);
    }

    public void creatFloatWindow() {
        mWmParams = new WindowManager.LayoutParams();
        // 获取的是WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager) getApplication().getSystemService(
                getApplication().WINDOW_SERVICE);
        // 设置window type
        mWmParams.type = LayoutParams.TYPE_PHONE;
        // 设置图片格式，效果为背景透明
        mWmParams.format = PixelFormat.RGBA_8888;
        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        mWmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        // 调整悬浮窗显示的停靠位置为左侧置顶
        mWmParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        // mWmParams.x = 0;
        // mWmParams.y = 0;

        // 设置悬浮窗口长宽数据
        mWmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mWmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

		/*
         * // 设置悬浮窗口长宽数据 wmParams.width = 200; wmParams.height = 80;
		 */

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        // 获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.window_float,
                null);
        // 添加mFloatLayout
        mWindowManager.addView(mFloatLayout, mWmParams);
        mTvOnline = (TextView) mFloatLayout.findViewById(R.id.tv_wf_online);
        mTvVer = (TextView) mFloatLayout.findViewById(R.id.tv_wf_ver);

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        if (inroids.common.System.isNotNullString(appMy.strUrl))
            new NetworkTask(1).execute(appMy.strUrl);
    }

    // Update Check
    public void setWindowText(String result) {
        if (inroids.common.System.isNotNullString(result)) {
            try  {
                JSONObject j= new JSONObject(result);
                int r=j.getInt("r");
                if(r==0){
                    appMy.isOnline = true;
                }else{
                    appMy.isOnline = false;
                }
            }catch (Exception e){
            }
        }
        mTvOnline.setText("设备状态: " + (appMy.isOnline ? "在线" : "离线"));
//        mTvVer.setText("节目版本号: A" + appMy.getDataVersion() + " 程序版本号: A"
//                + appMy.apkVersion);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                if (!MainService.this.appMy.isUpdating) {
                    MainService.this.intUpdateTimes++;
                    if (MainService.this.intUpdateTimes > 86400)
                        MainService.this.intUpdateTimes = 0;
                    if (MainService.this.intUpdateTimes % 30 == 1) {
                        // System.out.println(MainService.this.intUpdateTimes +
                        // " , updateDeviceState");
                        new NetworkTask(1).execute(MainService.this.appMy.strUrl
                                + "/inter_json/updateHardwareOnline?device=" + MainService.this.appMy.strDeviceId);
                        // MyLog.e(MainService.this.appMy.getString(R.string.app_key),
                        // "action=updateDeviceState&sn="
                        // + MainService.this.appMy.strDeviceId);
                    }
                    if (MainService.this.intUpdateTimes % 120 == 13) {
                        if (inroids.common.AppManage.checkAppActivityState(
                                MainService.this, "com.inroids.irmultimedia") < 0) {
                            Intent intent = new Intent();
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setClass(getApplicationContext(),
                                    LoadingActivity.class);
                            startActivity(intent);
                        }
                    }
                    if (MainService.this.intUpdateTimes % 30 == 5) {
                        if (appMy.strUrl != null && appMy.strUrl.length() > 0)
                            new NetworkTask().execute(appMy.strUrl + File.separator + "inter_json");
                    }
                }
            } catch (Exception e) {
                MyLog.e(MainService.this.appMy.getString(R.string.app_key),
                        "MainService.handler:" + e.toString());
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
                case 1:
                    setWindowText(result);
                    break;
                case 2:
                    break;
            }
        }
    }

}
