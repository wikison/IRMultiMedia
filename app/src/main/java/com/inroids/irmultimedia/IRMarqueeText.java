package com.inroids.irmultimedia;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import inroids.common.MyLog;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

public class IRMarqueeText extends TextView implements Runnable {
    private int intSpeed[] = new int[]{
        35, 25, 15
    };

    private static final String TAG = "IRMultimedia";

    private Activity actMain = null;

    private int intWidth = 0, intHeight = 0, intTextWidth = 0, intTextHeight = 0, intPos = 0;

    private int intRunOri = 0; // 0:left 1:right 2:up 3:down

    private boolean isFirstDraw = false; // the fist Draw

    private int intSpeedTime = 25;

    // Create this control------------------------
    public IRMarqueeText(Context context) {
        super(context);
        this.setTag(9);
        // Main Activity
        this.actMain = (Activity)context;
    }

    // init data with JSON Object
    public void initData(FrameLayout frame, JSONObject objT) {
        try {
            this.setText(ClassObject.getString(objT, "content"));
            // set Font
            // TextColor
            ClassObject.setTextColor(this, objT, "textColor");
            // set font size
            ClassObject.setTextSize(this, objT, "fontSize");
            // set font bold
            ClassObject.setFakeBoldText(this, objT, "fontWeight");
            // set font style
            ClassObject.setTextSkew(this, objT, "fontStyle");
            // set Back
            GradientDrawable gd = ClassObject.getGradientDrawable(objT);
            if (gd != null)
                this.setBackgroundDrawable(gd);
            // width+height
            this.intWidth = ClassObject.getInt(objT, "w");
            this.intHeight = ClassObject.getInt(objT, "h");
            // 获取跑马灯方向
            this.setDirection(objT);
            // 设置运行速度
            this.setSpeedTime(objT);
            // Left+Right
            if (this.intRunOri <= 1) {
                this.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
                this.setSingleLine(true);
            } else {// up+down
                String strT = ClassObject.getString(objT, "alignment");
                if (inroids.common.System.isNotNullString(strT)) {
                    if (strT.equals("left")) {
                        this.setGravity(Gravity.TOP | Gravity.LEFT);
                    } else if (strT.equals("center")) {
                        this.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                    } else {
                        this.setGravity(Gravity.TOP | Gravity.RIGHT);
                    }
                }
            }

            // the first draw
            this.isFirstDraw = false;
            frame.addView(this, ClassObject.getParams(ClassObject.getRect(objT)));

            // 开始跑动
            this.removeCallbacks(this);
            this.postDelayed(this, 500);
        } catch (Exception e) {
            MyLog.e(this.actMain.getString(R.string.app_key), "IRMarqueeText.initData:" + e.toString());
        }
    }

    // 设置运行速度
    private void setSpeedTime(JSONObject objT) {
        // 获取跑马灯速度
        this.intSpeedTime = 25;
        int intS = ClassObject.getInt(objT, "speed");
        if (intS < 1)
            this.intSpeedTime = this.intSpeed[0];
        else if (intS == 1) {
            this.intSpeedTime = this.intSpeed[1];
        } else {
            this.intSpeedTime = this.intSpeed[2];
        }
    }

    // 设置跑马灯方向
    private void setDirection(JSONObject objT) {
        try {
            String strD = ClassObject.getString(objT, "direction");
            if (inroids.common.System.isNotNullString(strD)) {
                if (strD.equals("left")) {
                    this.intRunOri = 0;
                } else if (strD.equals("right")) {
                    this.intRunOri = 1;
                } else if (strD.equals("down")) {
                    this.intRunOri = 3;
                } else {
                    this.intRunOri = 2;
                }
            }
        } catch (Exception e) {
            MyLog.e(TAG, "IRMarqueeText.setDirection:" + e.toString());
        }
    }

    // 第一次绘画获取跑马灯宽高等数据---------------------------------
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!this.isFirstDraw) {// the first draw
            if (this.intRunOri <= 1) {// left+right
                this.intTextWidth = (int)this.getPaint().measureText(this.getText().toString());
            } else {// up+down
                int lineHeight = this.getLineHeight();
                int lineCount = this.getLineCount();// 总行数
                this.intTextHeight = lineHeight * lineCount;
            }
            this.isFirstDraw = true;
        }
    }

    // 跑马灯运行中...
    @Override
    public void run() {
        if (this.actMain.getTaskId() > 0) {
            // 更时钟
            if (this.isFirstDraw) {
                // Left+Right
                if (this.intRunOri <= 1) {// Left
                    if (this.intRunOri == 0) {
                        this.intPos += 1;
                        if (this.intPos > this.intTextWidth)
                            this.intPos = -5 - this.intWidth;
                    } else {// right
                        this.intPos -= 1;
                        if (this.intPos < this.intWidth * -1)
                            this.intPos = this.intTextWidth + 5;
                    }
                    scrollTo(this.intPos, 0);
                } else {// up+down
                    if (this.intRunOri == 2) {
                        this.intPos += 1;
                        if (this.intPos > this.intTextHeight)
                            this.intPos = -5 - this.intHeight;
                    } else {
                        this.intPos -= 1;
                        if (this.intPos < this.intHeight * -1)
                            this.intPos = this.intTextHeight + 5;
                    }
                    scrollTo(0, this.intPos);
                }
            }
            this.postDelayed(this, this.intSpeedTime);

        } else {
            this.removeCallbacks(this);
        }
    }
}
