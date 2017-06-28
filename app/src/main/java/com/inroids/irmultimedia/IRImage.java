//ViewImage.java
//Created by sealy on 2013-06-01.  
//Copyright 2013 Sealy, Inc. All rights reserved.

package com.inroids.irmultimedia;

import java.io.File;
import java.io.InputStream;

import org.json.JSONObject;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import inroids.common.FileManage;
import inroids.common.Graphics;
import inroids.common.MyLog;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;

//{"type":"1","content":"Ir31.jpg",	"x":"97","y":"48","w":"150","h":"150","radian":"0","enable":"0","openFile":"Ir14.jpg","event":"31","effect":"1"}
public class IRImage extends ImageView implements OnTouchListener {
    private static final String TAG = "IRMultimedia";

    // JSON Data
    // private JSONObject objData=null;
    private String strThisFile = null, strOpenFile = null;

    private int intTurnId = 0, intEffect = 0;

    private boolean isUseTransparent = false, isEnableTouch = false;

    private GestureDetector gd = null;

    private FrameLayout frmMain = null;

    private MultiPlayActivity actMain = null;

    private Rect rctMain;

    private Bitmap bmpMain = null;

    // Create Image Control----------------------
    public IRImage(Context context) {
        super(context);
        this.setTag(1);
        // Main Activity
        this.actMain = (MultiPlayActivity)context;
        // Touch Event 
        this.setOnTouchListener(this);
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
            this.frmMain = frame;

            // Rect Main("x":"701","y":"81","w":"208","h":"208")
            this.rctMain = ClassObject.getRect(objT);
            this.frmMain.addView(this, ClassObject.getParams(ClassObject.getRect(objT)));

            this.setScaleType(ScaleType.FIT_XY);
            // Content
            this.strThisFile = strRes + File.separator + ClassObject.getString(objT, "content");
            if (FileManage.isExistsFile(strThisFile)) {
                // radian 
                int intRadian = ClassObject.getInt(objT, "radian");
                // Use Transparent button 
                this.isUseTransparent = ClassObject.getBoolean(objT, "transparent");
                // Load Image in thread
                new AsyncImage(this, intRadian, rctMain.width(), rctMain.height(), null, null)
                    .execute(this.strThisFile);
                // Is Double Click
            } else {
                this.setImageBitmap(Graphics.getImageFromAssetsFile(this.getContext(), "Overdue.png"));
            }

            boolean isEnable = ClassObject.getBoolean(objT, "enable");
            if (isEnable) {
                String strT = ClassObject.getString(objT, "openFile");
                if (inroids.common.System.isNotNullString(strT)) {
                    this.strOpenFile = strRes + "/" + strT;
                    this.isEnableTouch = true;
                }
                // event
                this.intTurnId = ClassObject.getInt(objT, "event");
                if (this.intTurnId > 0)
                    this.isEnableTouch = true;
                // effect
                this.intEffect = ClassObject.getInt(objT, "effect");
                if (this.isEnableTouch) {
                    this.gd = new GestureDetector(this.getContext(), this.gdClick);
                    this.gd.setIsLongpressEnabled(false);
                }
            } else {
                boolean isDoubleClick = ClassObject.getBoolean(objT, "doubleClick");
                if (isDoubleClick) {
                    this.isEnableTouch = true;
                    this.gd = new GestureDetector(this.getContext(), this.gdDouble);
                    this.gd.setIsLongpressEnabled(false);
                }
            }

        } catch (Exception e) {
            MyLog.e(this.actMain.getString(R.string.app_key), "IRImage.initData:" + e.toString());
        }
    }

    // Touch Event------------------------------------------
    // touch down
    private void touchDown() {
        try {
            this.setAlpha(64);
        } catch (Exception e) {
            MyLog.e(this.actMain.getString(R.string.app_key), "IRImage.touchDown:" + e.toString());
        }
    }

    // touch up
    private void touchUp() {
        try {
            Thread.sleep(250);
            this.setAlpha(255);
        } catch (InterruptedException e) {
            MyLog.e(this.actMain.getString(R.string.app_key), "IRImage.touchUp:" + e.toString());
        }
    }

    // Click Event
    // Touch Event
    @Override
    public boolean onTouch(View arg0, MotionEvent event) {
        this.actMain.clearCurPos();
        if (this.actMain.isCanTouch && this.isEnableTouch) {
            boolean isT = true;
            if (this.isUseTransparent) {
                if (this.bmpMain == null) {
                    this.bmpMain = ((BitmapDrawable)(this.getDrawable())).getBitmap();
                }
                if (this.bmpMain != null) { // check transparent point
                    if (event.getX() < bmpMain.getWidth() && event.getY() < bmpMain.getHeight() && event.getY() > 0)
                        isT = this.bmpMain.getPixel((int)(event.getX()), ((int)event.getY())) != 0;
                }
            }
            //
            if (isT) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    this.touchDown();
                }

                if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    this.touchUp();
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    this.touchUp();
                }
                if (this.gd != null) {
                    if (this.gd.onTouchEvent(event))
                        return true;
                    else
                        return false;
                }
            }
        }
        //
        return false;
    }

    // Single and double Event------------------------------
    // click event
    GestureDetector.SimpleOnGestureListener gdClick = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (inroids.common.System.isNotNullString(IRImage.this.strOpenFile)) {
                IRImage.this.actMain.openFile(IRImage.this.strOpenFile);
            } else {
                IRImage.this.actMain.isEventTurn = true;
                IRImage.this.actMain.turnPage(IRImage.this.intTurnId, IRImage.this.intEffect);
            }
            return false;
        }
    };

    // double click
    GestureDetector.SimpleOnGestureListener gdDouble = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            if (e.getAction() == MotionEvent.ACTION_UP
                && inroids.common.System.isNotNullString(IRImage.this.strThisFile)) {
                ClassPublic.addImageShow(IRImage.this.frmMain, IRImage.this.strThisFile);
            }
            return super.onDoubleTapEvent(e);
        }
    };

}
