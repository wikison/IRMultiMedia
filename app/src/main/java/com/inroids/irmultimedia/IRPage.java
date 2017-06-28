package com.inroids.irmultimedia;

import java.io.File;

import org.json.JSONObject;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import inroids.common.FileManage;
import inroids.common.Graphics;
import inroids.common.MyLog;
import android.widget.FrameLayout;
import android.widget.ImageView;

//{"type":"100","bgImage":"Ir3.jpg","bgMusic":"","bgColor":"","turnTime":"0","turnId":"0","effect":"0"}
public class IRPage extends ImageView implements Runnable {
    // private JSONObject objData=null;
    private MultiPlayActivity actMain = null;

    private int intTurnId = 0, intTurnTime = 0, intEffect = 0;

    public MediaPlayer mp3Main = null;

    // Create View Page Control----------------------
    public IRPage(Context context) {
        super(context);
        // Main Activity
        this.actMain = (MultiPlayActivity)context;
        // Touch event
        this.setOnTouchListener(this.actMain);
    }

    /**
     * initial Data with JSONObject
     * 
     * @param objT
     *            a JSON object.
     * @param strRes
     *            this program resource path object.
     */
    public void initData(FrameLayout frame, JSONObject objT, String strRes) {
        try {
            // 背景颜色
            String strContent = ClassObject.getString(objT, "bgImage");
            if (inroids.common.System.isNotNullString(strContent)) {
                if (FileManage.isExistsFile(strRes + File.separator + strContent)) {
                    this.setScaleType(ScaleType.FIT_XY);
                    // 加载图片但不缓存
                    new AsyncImage(this, 0, 0, 0, null, null).execute(strRes + File.separator + strContent);
                } else {
                    this.setImageBitmap(Graphics.getImageFromAssetsFile(this.getContext(), "Overdue.png"));
                }
            } else {
                int intBgColor = ClassObject.getColor(objT, "bgColor");
                if (intBgColor < 0)
                    this.setBackgroundColor(intBgColor);
                else
                    this.setBackgroundColor(Color.WHITE);
            }
            // play Mp3
            String strMusic = ClassObject.getString(objT, "bgMusic");
            if (inroids.common.System.isNotNullString(strMusic)) {
                String strFile = strRes + File.separator + strMusic;
                // check file exists
                if (inroids.common.FileManage.isExistsFile(strFile)) {
                    if (mp3Main == null)
                        mp3Main = MediaPlayer.create(this.actMain, Uri.parse(strFile));// 创建Mp3播放控件
                    mp3Main.setLooping(true);
                    mp3Main.start();
                }
            }
            intTurnTime = ClassObject.getInt(objT, "turnTime");
            intTurnId = ClassObject.getInt(objT, "turnId");
            intEffect = ClassObject.getInt(objT, "effect");

            this.actMain.intTurnId = this.intTurnId;
            this.actMain.intEffect = this.intEffect;
            frame.addView(this, 0);
            this.postDelayed(this, 50);
        } catch (Exception e) {
            MyLog.e(this.actMain.getString(R.string.app_key), "ViewPage.initData:" + e.toString());
        }
    }

    // 启用时钟
    @Override
    public void run() {
        if (this.actMain.isPause == false) {
            if (this.actMain.getTaskId() > 0) {
                this.actMain.isCanTouch = true;
                boolean isPost = false;
                if (this.intTurnId > 0 && this.intTurnTime > 0) {
                    isPost = true;
                    this.actMain.intCurPos++;
//                    System.out.println(this.actMain.intTag + " run~~~");
                    if (this.actMain.intCurPos > this.intTurnTime) {
                        this.actMain.turnPage(this.intTurnId, this.intEffect);
                    }
                } else if (this.mp3Main != null) {
                    isPost = true;
                }
                if (isPost) {
                    this.postDelayed(this, 1000);
                }
            } else {
                if (this.mp3Main != null) {
                    this.mp3Main.release();
                    this.mp3Main = null;
                }
                this.removeCallbacks(this);
            }
        } else {
            this.removeCallbacks(this);
        }
    }

}
