//ViewTextButton.java
//Created by sealy on 2013-06-01.  
//Copyright 2013 Sealy, Inc. All rights reserved.
package com.inroids.irmultimedia;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.File;

import inroids.common.MyLog;

public class IRTextButton extends TextView implements OnTouchListener {
    private static final String TAG = "IRMultimedia";

    private MultiPlayActivity actMain = null;

    private boolean isEnableTouch = false;

    private int intNormalColor = 0, intSelectColor = 0;

    private String strOpenFile = null;

    private int intTurnId = 0, intEffect = 0;

    private GestureDetector gd = null;

    // Create Text Control--------------------------------------------
    public IRTextButton(Context context) {
        super(context);
        // Main Activity
        this.actMain = (MultiPlayActivity)context;
        // set Touch Event
        this.setOnTouchListener(this);
    }

    // init data with JSONObject
    public void initData(FrameLayout frame, JSONObject objT, String strRes) {
        try {
            // set text
            this.setText(objT.getString("content"));
            // set Font
            this.intNormalColor = ClassObject.getColor(objT, "textColor");
            // TextColor
            ClassObject.setTextColor(this, objT, "textColor");
            // set font size
            ClassObject.setTextSize(this, objT, "fontSize");
            // set font bold
            ClassObject.setFakeBoldText(this, objT, "fontWeight");
            // set font style
            ClassObject.setTextSkew(this, objT, "fontStyle");
            // set Back
            GradientDrawable gb = ClassObject.getGradientDrawable(objT);
            if (gb != null)
                this.setBackgroundDrawable(gb);
            // set Alignment style
            ClassObject.setGravity(this, objT, "alignment");
            // single Line
            this.setSingleLine(ClassObject.getBoolean(objT, "singleLine"));
            // enable click event
            Boolean isEnable = ClassObject.getBoolean(objT, "enable");
            if (isEnable) {
                //
                String strT = ClassObject.getString(objT, "openFile");
                if (inroids.common.System.isNotNullString(strT)) {
                    this.strOpenFile = strRes + File.separator + strT;
                    this.isEnableTouch = true;
                }
                // event
                this.intTurnId = ClassObject.getInt(objT, "event");
                // effect
                this.intEffect = ClassObject.getInt(objT, "effect");

                if (this.intTurnId > 0)
                    this.isEnableTouch = true;
                // set Select Color
                this.intSelectColor = ClassObject.getColor(objT, "selectColor");
            }

            if (this.isEnableTouch) {
                // this.hndEvent= new Handler();
                this.gd = new GestureDetector(this.getContext(), this.gdS);
                // cancel Longpress
                this.gd.setIsLongpressEnabled(false);
            }
            frame.addView(this, ClassObject.getParams(ClassObject.getRect(objT)));
        } catch (Exception e) {
            MyLog.e(TAG, "ViewTextButton.initData:" + e.toString());
        }
    }

    // 响应单击事件---------------------------------
    GestureDetector.SimpleOnGestureListener gdS = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (inroids.common.System.isNotNullString(IRTextButton.this.strOpenFile)) {
                IRTextButton.this.actMain.openFile(IRTextButton.this.strOpenFile);
            } else {
                IRTextButton.this.actMain.isEventTurn = true;
                IRTextButton.this.actMain.turnPage(IRTextButton.this.intTurnId, IRTextButton.this.intEffect);
            }
            return false;
        }
    };

    // 触摸事件----------------------------------------------------------------
    @Override
    public boolean onTouch(View arg0, MotionEvent event) {
        this.actMain.clearCurPos(); // clear zero

        if (this.actMain.isCanTouch && this.isEnableTouch) {
            // action down
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                this.touchDown();
            }
            // action cancel
            if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                this.touchUp();
            }
            // action up
            if (event.getAction() == MotionEvent.ACTION_UP) {
                this.touchUp();
            }
            //
            if (this.gd != null) {
                if (this.gd.onTouchEvent(event))
                    return true;
            }
        }
        return false;
    }

    // touch down
    private void touchDown() {
        this.setTextColor(this.intSelectColor);
    }

    // touch up
    private void touchUp() {
        try {
            Thread.sleep(250);
            this.setTextColor(this.intNormalColor);
        } catch (Exception e) {
            MyLog.e(TAG, "ViewTextButton.touchUp:" + e.toString());
        }
    }
}
