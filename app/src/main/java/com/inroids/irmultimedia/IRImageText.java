package com.inroids.irmultimedia;

import java.io.File;
import org.json.JSONObject;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import inroids.common.ClassObject;
import inroids.common.Graphics;
import inroids.common.MyLog;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class IRImageText extends FrameLayout implements OnTouchListener {
    private static final String TAG = "IRMultimedia";

    // JSON Data
    private MultiPlayActivity actMain = null;

    private TextView vwText = null;

    private ImageView imgBack = null, imgButton = null;

    private int intSelectColor = 0, intNormalColor = 0;

    private Rect rctMain;

    private String strOpenFile = null;

    private int intTurnId = 0, intEffect = 0;

    private GestureDetector gd = null;

    private Boolean isEnableTouch = false;

    // Create Text Control----------------------
    public IRImageText(Context context) {
        super(context);
        // Main Activity
        if (this.actMain == null)
            this.actMain = (MultiPlayActivity)context;
        // set Touch Event
        this.setOnTouchListener(this);
    }

    // {"type":"6","content":"Ir31.jpg","x":"678","y":"66","w":"152","h":"152","bgColor":"","radian":"0","openFile":"Ir14.jpg","event":"31","effect":"1",
    // "iVisible":"0","iX":"97","iY":"48","iW":"150","iH":"150","iRadian":"0","iContent":"Ir31.jpg",
    // "tVisible":"0","tX":"678","tY":"66","tW":"152","tH":"152","tAlignment":"mm","tContent":"文本区域sss","tFontSize":"12px","tTextColor":"#000000","tRadian":"0","tBgColor":
    // "#ffffff","tFontFamily":"宋体","tFontStyle":"1","tFontWeight":"1","tSelectColor":"#000000"}
    public void initData(FrameLayout frame, JSONObject objT, String strRes) {
        try {
            // Rect Main("x":"701","y":"81","w":"208","h":"208")
            this.rctMain = ClassObject.getRect(objT);

            String sBackImage = ClassObject.getString(objT, "content");
            // backImage is
            if (inroids.common.System.isNotNullString(sBackImage)) {
                String sFileName = strRes + File.separator + sBackImage;
                if (inroids.common.FileManage.isExistsFile(sFileName)) {
                    this.imgBack = new ImageView(this.actMain);
                    this.addView(this.imgBack);
                    new AsyncImage(this.imgBack, ClassObject.getInt(objT, "radian"), this.rctMain.width(),
                        this.rctMain.height(), null, null).execute(sFileName);
                } else {
                    Graphics.addOverdueView(this.getContext(), this, ClassObject.getRect(objT), "Overdue.png");
                }
            } else {
                GradientDrawable gb = ClassObject.getGradientDrawable(objT);
                if (gb != null)
                    this.setBackgroundDrawable(gb);
            }

            //
            String strT = objT.getString("openFile");
            if (inroids.common.System.isNotNullString(strT)) {
                this.strOpenFile = strRes + File.separator + strT;
                this.isEnableTouch = true;
            }

            // event
            this.intTurnId = objT.getInt("event");
            if (this.intTurnId > 0)
                this.isEnableTouch = true;
            // effect
            this.intEffect = objT.getInt("effect");
            // button image
            if (ClassObject.getBoolean(objT, "iVisible")) {
                String sImage = ClassObject.getString(objT, "iContent");
                if (inroids.common.System.isNotNullString(sImage)) {
                    String sFileName = strRes + File.separator + sImage;
                    if (inroids.common.FileManage.isExistsFile(sFileName)) {
                        this.imgButton = new ImageView(this.actMain);
                        this.addView(this.imgButton,
                            ClassObject.getParams(ClassObject.getRect(objT, "iX", "iY", "iW", "iH")));
                        new AsyncImage(this.imgButton, ClassObject.getInt(objT, "intIRadian"), this.rctMain.width(),
                            this.rctMain.height(), null, null).execute(sFileName);
                    }

                }
            }
            // button title
            int tVisible = ClassObject.getInt(objT, "tVisible");
            if (tVisible == 1) {
                String strTContent = ClassObject.getString(objT, "tContent");
                if (inroids.common.System.isNotNullString(strTContent)) {
                    this.vwText = new TextView(this.actMain);
                    this.vwText.setText(strTContent);
                    // set Back
                    GradientDrawable gb = ClassObject.getGradientDrawable(objT, "tBgColor", "tRadian");
                    if (gb != null)
                        this.vwText.setBackgroundDrawable(gb);
                    // text Color
                    // set Font
                    this.intNormalColor = ClassObject.getColor(objT, "tTextColor");
                    // Select Color
                    this.intSelectColor = ClassObject.getColor(objT, "tSelectColor");
                    // TextColor
                    ClassObject.setTextColor(this.vwText, objT, "tTextColor");
                    // set font size
                    ClassObject.setTextSize(this.vwText, objT, "tFontSize");
                    // set font bold
                    ClassObject.setFakeBoldText(this.vwText, objT, "tFontWeight");
                    // set font style
                    ClassObject.setTextSkew(this.vwText, objT, "tFontStyle");
                    // set Alignment style
                    ClassObject.setGravity(this.vwText, objT, "tAlignment");
                    // single line
                    this.vwText.setSingleLine(ClassObject.getBoolean(objT, "tSingleLine"));
                    this.addView(this.vwText, ClassObject.getParams(ClassObject.getRect(objT, "tX", "tY", "tW", "tH")));
                }
            }
            if (this.isEnableTouch) {
                this.gd = new GestureDetector(this.getContext(), this.gdS);
                this.gd.setIsLongpressEnabled(false);
            }
            // add content View
            frame.addView(this, ClassObject.getParams(ClassObject.getRect(objT)));
        } catch (Exception e) {
            MyLog.e(TAG, "ViewImageText.initData:" + e.toString());
        }
    }

    GestureDetector.SimpleOnGestureListener gdS = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // Log.e(strTag,"ViewTextButton.onSingleTapConfirmed:");
            if (inroids.common.System.isNotNullString(IRImageText.this.strOpenFile)) {
                IRImageText.this.actMain.openFile(IRImageText.this.strOpenFile);
            } else {
                IRImageText.this.actMain.isEventTurn = true;
                IRImageText.this.actMain.turnPage(IRImageText.this.intTurnId, IRImageText.this.intEffect);
            }
            return false;
        }
    };

    // update Check
    // touch down event
    private void touchDown() {
        try {
            if (this.imgButton != null)
                this.imgButton.setAlpha(64);
            if (this.imgBack != null)
                this.imgBack.setAlpha(64);
            else
                this.setAlpha(64);

            if (this.vwText != null && this.intSelectColor < 0)
                this.vwText.setTextColor(this.intSelectColor);
        } catch (Exception e) {
            MyLog.e(TAG, "ViewImageText.touchDown:" + e.toString());
        }

    }

    // touch up event
    private void touchUp() {
        try {
            Thread.sleep(250);
            if (this.vwText != null && this.intNormalColor < 0)
                this.vwText.setTextColor(this.intNormalColor);

            if (this.imgButton != null)
                this.imgButton.setAlpha(255);

            if (this.imgBack != null)
                this.imgBack.setAlpha(255);
            else
                this.setAlpha(255);
        } catch (Exception e) {
            MyLog.e(TAG, "ViewTextButton.touchUp:" + e.toString());
        }
    }

    // Touch Event
    @Override
    public boolean onTouch(View arg0, MotionEvent event) {
        this.actMain.clearCurPos();// clear zero
        if (this.actMain.isCanTouch && this.isEnableTouch) {
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
        return false;
    }
}
