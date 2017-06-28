package com.inroids.irmultimedia;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import inroids.common.Graphics;
import inroids.common.MyLog;

public class IRImages extends android.widget.FrameLayout implements OnTouchListener {
    private static final String TAG = "IRMultimedia";

    // JSON Data
    // private JSONObject objData=null;
    private MultiPlayActivity actMain = null;

    private String strOpenFile = null, strCurFile = null;

    private int intRadian = 0, intTurnId = 0, intEffect = 0, intTEffect = 0, intDuration = 0, intPlayMode = 0,
            intCurArray = 0, intCurPos = 0, intArrayId = -1;

    private boolean isScroll = false, isLeft = false, isEnableTouch = false;

    private boolean isLoop = false, isDoubleClick = false, isHidePage = false;

    private Rect rctMain, rctContext;

    private JSONArray arrJson;

    private Timer tmrMain = null;

    private String strResource = null;

    private android.widget.FrameLayout frmMain = null;

    private GestureDetector gd = null;

    private ImageLoad img0 = null;

    private FramePage frmPage = null;

    private FrameLayout frmContext = null;

    private Boolean isFinishTurn = false, isDieLoop = true;

    // private ImageView imgT;
    // Create Images Control----------------------
    public IRImages(Context context) {
        super(context);
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
            // Resource Path
            this.strResource = strRes;
            // FrameLayout frame
            if (this.frmMain == null)
                this.frmMain = frame;

            // content
            this.arrJson = objT.getJSONArray("content");
            // Rect Main("x":"701","y":"81","w":"208","h":"208")
            this.rctMain = ClassObject.getRect(objT);
            // Context Rect
            this.rctContext = new Rect(0, 0, this.rctMain.width(), this.rctMain.height());
            // duration
            this.intDuration = ClassObject.getInt(objT, "duration");
            // effect
            this.intEffect = ClassObject.getInt(objT, "effect");
            // radian
            this.intRadian = ClassObject.getInt(objT, "radian");
            // Play Mode
            this.intPlayMode = ClassObject.getInt(objT, "playMode");

            // enable
            boolean isEnable = ClassObject.getBoolean(objT, "enable");
            if (isEnable) {
                // openFile
                String strT = ClassObject.getString(objT, "openFile");
                if (inroids.common.System.isNotNullString(strT)) {
                    this.strOpenFile = strRes + File.separator + strT;
                    this.isEnableTouch = true;
                }
                // event
                this.intTurnId = ClassObject.getInt(objT, "event");
                if (this.intTurnId > 0)
                    this.isEnableTouch = true;
                // effect
                this.intTEffect = ClassObject.getInt(objT, "tEffect");
            }

            if (this.intPlayMode == 0) {
                // finish Turn
                this.isFinishTurn = ClassObject.getBoolean(objT, "finishTurn");
                if (this.isFinishTurn)
                    this.actMain.intFinishValue++;
                if (this.intDuration > 0 && this.arrJson.length() > 0) {
                    this.intCurArray = -1;
                    this.autoUpdateImage();
                }
            } else {
                boolean isShowPages = ClassObject.getBoolean(objT, "showPages");
                // left +right button
                String strButtonPosition = ClassObject.getString(objT, "buttonPosition");
                // 显示页码或左右按钮
                if (isShowPages
                        || (inroids.common.System.isNotNullString(strButtonPosition) && !strButtonPosition.equals("0"))) {
                    this.frmPage = new FramePage(this.actMain, this.rctContext, objT);
                    this.addView(this.frmPage, 0);
                    this.frmPage.setOnMyEventListener(new OnMyEvent() {
                        @Override
                        public void onMy(Object objT, int iTag) {
                            IRImages.this.isLeft = iTag != 0;
                            IRImages.this.handAnimation();
                        }
                    });
                }
                this.isLoop = ClassObject.getBoolean(objT, "loop");
                this.frmContext = new FrameLayout(this.actMain);
                this.addView(this.frmContext, 0);
                this.isDoubleClick = ClassObject.getBoolean(objT, "doubleClick");
                this.intCurArray = 1;
                this.handUpdateImage();
            }
            // Main Control
            frame.addView(this, ClassObject.getParams(this.rctMain));

            // Event
            if (this.isEnableTouch || this.intPlayMode != 0) {
                this.gd = new GestureDetector(this.actMain, new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                        IRImages.this.actMain.clearCurPos();
                        // TODO Auto-generated method stub
                        return super.onFling(e1, e2, velocityX, velocityY);
                    }

                    @Override
                    public boolean onDown(MotionEvent e) {
                        IRImages.this.actMain.clearCurPos();
                        IRImages.this.intCurPos = 0;
                        if (IRImages.this.isHidePage) {
                            if (IRImages.this.frmPage != null)
                                IRImages.this.frmPage.setVisibility(View.VISIBLE);
                            IRImages.this.isHidePage = false;
                        }
                        return true;
                    }

                    // 滑动时触发
                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                        IRImages.this.actMain.clearCurPos();
                        IRImages.this.intCurPos = 0;
                        if (IRImages.this.intPlayMode != 0) {
                            IRImages.this.isScroll = true;
                            if (IRImages.this.img0 != null)
                                IRImages.this.img0.setLeft(IRImages.this.img0.getLeft() - (int)distanceX);
                        } else if (IRImages.this.intPlayMode == 0) {
                            if (IRImages.this.isEnableTouch) {
                                if (inroids.common.System.isNotNullString(IRImages.this.strOpenFile)) {
                                    IRImages.this.actMain.openFile(IRImages.this.strOpenFile);
                                } else {
                                    IRImages.this.actMain.isEventTurn = true;
                                    IRImages.this.actMain.turnPage(IRImages.this.intTurnId, IRImages.this.intTEffect);
                                }
                            }
                            // return false;
                        }
                        return super.onScroll(e1, e2, distanceX, distanceY);
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {
                        IRImages.this.actMain.clearCurPos();
                        if (IRImages.this.intPlayMode == 0) {
                            if (IRImages.this.isEnableTouch) {
                                if (inroids.common.System.isNotNullString(IRImages.this.strOpenFile)) {
                                    IRImages.this.actMain.openFile(IRImages.this.strOpenFile);
                                } else {
                                    IRImages.this.actMain.isEventTurn = true;
                                    IRImages.this.actMain.turnPage(IRImages.this.intTurnId, IRImages.this.intTEffect);
                                }
                            }
                            // return false;
                        }
                        // super.onLongPress(e);
                    }

                    public boolean onDoubleTapEvent(MotionEvent e) {
                        IRImages.this.actMain.clearCurPos();
                        if (e.getAction() == MotionEvent.ACTION_UP) {
                            if (IRImages.this.isDoubleClick) {
                                ClassPublic.addImageShow(IRImages.this.frmMain, IRImages.this.strCurFile);
                            }
                        }
                        return super.onDoubleTapEvent(e);
                    }

                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        // Log.e(strTag,"ViewImages.onSingleTapConfirmed:");
                        if (IRImages.this.isEnableTouch) {
                            if (inroids.common.System.isNotNullString(IRImages.this.strOpenFile)) {
                                IRImages.this.actMain.openFile(IRImages.this.strOpenFile);
                            } else {
                                IRImages.this.actMain.isEventTurn = true;
                                IRImages.this.actMain.turnPage(IRImages.this.intTurnId, IRImages.this.intTEffect);
                            }
                        }
                        return false;
                    }

                });
                this.gd.setIsLongpressEnabled(true);

                this.tmrMain = new Timer();
                // time update
                this.tmrMain.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (IRImages.this.actMain.getTaskId() < 0) {
                            IRImages.this.tmrMain.cancel();
                            IRImages.this.tmrMain = null;
                        } else {
                            Message message = new Message();
                            message.what = 1;
                            IRImages.this.hndDuration.sendMessage(message);
                        }
                    }
                }, 0, 1000);
            }
        } catch (Exception e) {
            MyLog.e(TAG, "ViewImages.initData:" + e.toString());
        }
    }

    Handler hndDuration = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            IRImages.this.intCurPos++;
            if (!IRImages.this.isHidePage && IRImages.this.intCurPos >= 8) {
                IRImages.this.intCurPos = 0;
                IRImages.this.isHidePage = true;
                if (IRImages.this.frmPage != null)
                    IRImages.this.frmPage.setVisibility(View.GONE);
            }
            super.handleMessage(msg);
        }
    };

    // auto play
    private void autoUpdateImage() {
        try {
            this.intCurArray++;
            if (this.intCurArray >= this.arrJson.length()) {
                if (this.isFinishTurn) {
                    if (this.actMain.finishTurn())
                        return;
                }
                // kill die Round(Not find data)
                if (this.isDieLoop) {
                    MyLog.e(TAG, "ViewImages.autoUpdateImage: is die loop");
                    return;
                }
                this.intCurArray = 0;
            }

            if (this.intArrayId != this.intCurArray) {
                String strT = this.arrJson.getString(this.intCurArray);
                //
                if (inroids.common.System.isNotNullString(strT)) {
                    String strF = this.strResource + "/" + strT;
                    if (inroids.common.FileManage.isExistsFile(strF)) {// file is Exists
                        this.isDieLoop = false;
                        this.removeAllView();
                        ImageLoad imgT = new ImageLoad(this.actMain);
                        this.addView(imgT, 0);
                        imgT.setOnMyListener(new OnMyEvent() {
                            // 0:ok -1:cancel 1:finish radian
                            @Override
                            public void onMy(Object objT, int iTag) {
                                // System.out.println("tag:"+iTag);
                                if (iTag == 1) {// finish load
                                    IRImages.this.autoUpdateImage();
                                } else {
                                    IRImages.this.autoAnimation();
                                }

                            }
                        });
                        imgT.initData(this.rctMain, this.intRadian, this.intDuration, strF, strT, this.actMain.imgCache);
                        this.intArrayId = this.intCurArray;
                    } else {
                        // file is Exists
                        this.isDieLoop = false;
                        this.removeAllView();
                        ImageLoad imgT = new ImageLoad(this.actMain);
                        this.addView(imgT, 0);
                        imgT.setOnMyListener(new OnMyEvent() {
                            // 0:ok -1:cancel 1:finish radian
                            @Override
                            public void onMy(Object objT, int iTag) {
                                // System.out.println("tag:"+iTag);
                                if (iTag == 1) {// finish load
                                    IRImages.this.autoUpdateImage();
                                } else {
                                    IRImages.this.autoAnimation();
                                }

                            }
                        });
                        imgT.initData(this.intDuration,
                                Graphics.getImageFromAssetsFile(this.getContext(), "Overdue.png"));
                        this.intArrayId = this.intCurArray;

                        // this.autoUpdateImage();
                    }
                } else {
                    this.autoUpdateImage();
                }
            }
        } catch (Exception e) {
            MyLog.e(TAG, "ViewImages.autoUpdateImage:" + e.toString());
        }

    }

    // remove AllView
    private void removeAllView() {
        int intCount = this.getChildCount();
        for (int i = 1; i < intCount; i++) {
            FrameLayout imgA = (FrameLayout)IRImages.this.getChildAt(0);
            imgA.removeAllViews();
            this.removeViewAt(0);
        }
        System.gc();
    }

    //
    private void autoAnimation() {
        try {
            int intCount = this.getChildCount();
            if (intCount > 1) {
                int iA = 1;
                if (this.intEffect == 0 || this.intEffect > 3) {
                    Intent intent = this.actMain.getIntent();
                    int value = intent.getIntExtra("max", 3);
                    iA = Integer.valueOf((int)(Math.random() * value));
                } else
                    iA = this.intEffect;
                FrameLayout imgT = (FrameLayout)this.getChildAt(1);
                if (iA == 0) {
                    AlphaAnimation aa = new AlphaAnimation(1.0f, 0.00f);
                    aa.setDuration(500);
                    imgT.startAnimation(aa);
                    aa.setAnimationListener(new AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            FrameLayout imgA = (FrameLayout)IRImages.this.getChildAt(1);
                            imgA.removeAllViews();
                            IRImages.this.removeViewAt(1);
                        }
                    });
                } else {
                    int intEnd = 0;
                    if (iA == 1) {
                        intEnd = -1 * this.rctMain.width();
                    } else {
                        intEnd = this.rctMain.width();
                    }
                    TranslateAnimation aa = new TranslateAnimation(0, intEnd, 0, 0);//
                    aa.setDuration(500);
                    imgT.startAnimation(aa);
                    aa.setAnimationListener(new AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            FrameLayout imgA = (FrameLayout)IRImages.this.getChildAt(1);
                            imgA.removeAllViews();
                            IRImages.this.removeViewAt(1);
                        }
                    });
                }
            }
        } catch (Exception e) {
            MyLog.e(TAG, "ViewImages.autoAnimation:" + e.toString());
        }
    }

    public boolean onTouch(View arg0, MotionEvent event) {
        if (this.actMain.isCanTouch) {
            this.intCurPos = 0;
            this.actMain.clearCurPos();
            if (this.intPlayMode != 0 || this.isEnableTouch) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (this.img0 != null && this.isScroll) {
                        int intV = this.img0.getLeft();
                        if (intV > 160 || intV < -160) {

                            this.isLeft = intV < 0;
                            if (!this.isLoop) {
                                if (this.isLeft && this.intCurArray >= this.arrJson.length() - 1) {
                                    this.returnAnimation();
                                } else if (!this.isLeft && this.intCurArray <= 0) {
                                    this.returnAnimation();
                                } else {
                                    this.handAnimation();
                                }
                            } else {
                                this.handAnimation();
                            }

                        } else {
                            this.returnAnimation();
                        }
                    }
                    this.isScroll = false;
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    this.isScroll = false;
                }
                return this.gd.onTouchEvent(event);
            }
        } else {
            MyLog.e(TAG, "ViewImages.onTouch not:");
        }
        return false;
    }

    private void updateButton() {
        try {
            if (this.frmPage != null) {
                if (!this.isLoop) {
                    if (this.intCurArray <= 0) {
                        this.frmPage.setButtonVisibility(true, false);
                    } else {
                        this.frmPage.setButtonVisibility(true, true);
                    }
                    if (this.intCurArray >= this.arrJson.length() - 1) {
                        this.frmPage.setButtonVisibility(false, false);
                    } else {
                        this.frmPage.setButtonVisibility(false, true);
                    }
                } else {
                    this.frmPage.setButtonVisibility(true, true);
                    this.frmPage.setButtonVisibility(false, true);
                }
            }
        } catch (Exception e) {
            MyLog.e(TAG, "ViewImages.updateButton:" + e.toString());
        }
    }

    // auto Udpate
    private void handUpdateImage() {
        try {
            if (this.isLeft)
                this.intCurArray++;
            else
                this.intCurArray--;

            if (!this.isLoop) {
                if (this.intCurArray < 0) {
                    this.intCurArray = 0;
                    return;
                }
                if (this.intCurArray >= this.arrJson.length()) {
                    this.intCurArray = this.arrJson.length() - 1;
                    return;
                }
            }
            if (this.intCurArray >= this.arrJson.length()) {
                this.intCurArray = 0;
            } else if (this.intCurArray < 0) {
                this.intCurArray = this.arrJson.length() - 1;
            }
            if (this.frmPage != null)
                this.frmPage.updatePage(this.intCurArray, this.arrJson.length());
            this.updateButton();
            // this.vwText.setText("")
            if (this.intArrayId != this.intCurArray) {
                String strT = this.arrJson.getString(this.intCurArray);
                //
                if (inroids.common.System.isNotNullString(strT)) {
                    this.strCurFile = this.strResource + "/" + strT;
                    if (inroids.common.FileManage.isExistsFile(this.strCurFile)) {// file is Exists
                        this.img0 = new ImageLoad(this.actMain);
                        this.frmContext.addView(this.img0, 0);
                        this.img0.initData(this.rctContext, this.intRadian, 0, this.strCurFile, strT,
                                this.actMain.imgCache);
                    } else {
                        this.img0 = new ImageLoad(this.actMain);
                        this.frmContext.addView(this.img0, 0);
                        this.img0.initData(this.intDuration,
                                Graphics.getImageFromAssetsFile(this.getContext(), "Overdue.png"));
                    }
                    this.img0.setOnMyListener(new OnMyEvent() {
                        // 0:ok -1:cancel 1:finish radian
                        @Override
                        public void onMy(Object objT, int iTag) {
                            if (iTag == -1) {
                                IRImages.this.handUpdateImage();
                            }

                        }
                    });

                    this.intArrayId = this.intCurArray;

                } else {
                    this.handUpdateImage();
                }
            }
        } catch (Exception e) {
            MyLog.e(TAG, "ViewImages.handUpdateImage:" + e.toString());
        }

    }

    // @Override
    // public boolean onTouchEvent(MotionEvent ev) {
    private void returnAnimation() {
        TranslateAnimation aa = new TranslateAnimation(this.img0.getLeft(), 0, 0, 0);//
        aa.setDuration(250);
        this.img0.startAnimation(aa);
        this.img0.setLeft(0);
    }

    // animation-------------------------------------------------
    //
    private void handAnimation() {
        try {
            if (IRImages.this.frmContext.getChildCount() > 0) {
                int intEnd = 0;
                if (this.isLeft) {
                    intEnd = -1 * this.rctMain.width();
                } else {
                    intEnd = this.rctMain.width();
                }
                TranslateAnimation aa = new TranslateAnimation(this.img0.getLeft(), intEnd, 0, 0);//
                aa.setDuration(250);
                this.img0.startAnimation(aa);
                aa.setAnimationListener(new AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        IRImages.this.frmContext.removeViewAt(0);
                        IRImages.this.handUpdateImage();
                    }
                });
            }
        } catch (Exception e) {
            MyLog.e(TAG, "ViewImages.handAnimation:" + e.toString());
        }
    }
}
