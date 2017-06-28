package com.inroids.irmultimedia;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import inroids.common.Graphics;
import inroids.common.MyLog;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

//{"type":"5","content":["Ir14.jpg","Ir13.jpg","Ir12.jpg","Ir11.jpg ","Ir8.jpg","Ir7.jpg"],
//"x":"672","y":"332","w":"566","h":"281","duration":"1","effect":"0","playMode":"0","radian":"0","loop":"0",
//"enable":"0","openFile":"Ir14.jpg","event":"31","tEffect":"1"}

public class IRMulti extends android.widget.FrameLayout implements OnTouchListener {
    private static final String TAG = "IRMultimedia";

    private MultiPlayActivity actMain = null;

    private String strOpenFile = null;

    private int intRadian = 0, intTurnId = 0, intEffect = 0, intTEffect = 0, intDuration = 0, intPlayMode = 0,
            intCurArray = 0, intCurDuration = 0, intArrayId = -1;

    // private Handler hndEvent; //更新 Handler
    private boolean isScroll = false, isLeft = false, isDieLoop = false;

    private boolean isEnableTouch = false, isLoop = false, isDoubleClick = false, isFinishTurn = false,
            isHidePage = false;

    private Rect rctMain, rctEntry, rctContext;

    private JSONArray arrJson;

    private Timer tmrMain = null;

    private String strResource = null;

    private android.widget.FrameLayout frmMain = null;

    private GestureDetector gd;

    private FramePage frmPage = null;

    private FrameLayout frmContext = null;

    private IRMultiEntry vw0 = null;

    // private ImageView imgT;
    // Create Images Control----------------------
    public IRMulti(Context context) {
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
            // content
            this.arrJson = objT.getJSONArray("content");
            // FrameLayout frame
            if (this.frmMain == null)
                this.frmMain = frame;
            // Main Rect
            this.rctMain = ClassObject.getRect(objT);
            // Context Rect
            this.rctContext = new Rect(0, 0, this.rctMain.width(), this.rctMain.height());
            // duration
            this.intDuration = ClassObject.getInt(objT, "duration");
            // effect
            this.intEffect = ClassObject.getInt(objT, "effect");
            // radian h
            this.intRadian = ClassObject.getInt(objT, "radian");
            // Play Mode
            this.intPlayMode = ClassObject.getInt(objT, "playMode");
            // set background color
            int intBgColor = ClassObject.getColor(objT, "bgColor");
            if (intBgColor < 0)
                this.setBackgroundColor(intBgColor);
            // enable
            if (ClassObject.getBoolean(objT, "enable")) {
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
                // Finish Turn
                this.isFinishTurn = ClassObject.getBoolean(objT, "finishTurn");
                if (this.isFinishTurn)
                    this.actMain.intFinishValue++;
                if (this.intDuration > 0) {
                    this.intCurArray = -1;
                    this.autoPlay();
                }
                if (this.isEnableTouch) {
                    this.gd = new GestureDetector(this.getContext(), this.gdClick);
                    this.gd.setIsLongpressEnabled(false);
                }
            } else {
                // //loop
                // this.isLoop=ClassObject.getBoolean(objT, "loop");
                // this.isDoubleClick=ClassObject.getBoolean(objT, "doubleClick");
                // //页码
                // if(!ClassObject.getString(objT, "buttonPosition").equals("0") || ClassObject.getBoolean(objT,
                // "tVisible")){
                // this.frmPage=new FramePage(this.actMain, this.rctContext, objT);
                // this.addView(this.frmPage, 0);
                // this.frmPage.setOnMyEventListener(new OnMyEvent(){
                // @Override
                // public void onMy(Object objT, int iTag) {
                // IRMulti.this.isLeft=iTag!=0;
                // IRMulti.this.handAnimation();
                // }});
                // //隐藏页码
                // this.tmrMain= new Timer();
                // //time update
                // this.tmrMain.schedule(new TimerTask() {
                // @Override
                // public void run() {
                // if(IRMulti.this.actMain.getTaskId()<0){
                // IRMulti.this.tmrMain.cancel();
                // IRMulti.this.tmrMain=null;
                // }
                // else{
                // Message message = new Message();
                // message.what = 1;
                // IRMulti.this.hndDuration.sendMessage(message);
                // }
                // }
                // }, 0, 1000);
                // }
                // //
                // this.frmContext=new FrameLayout(this.actMain);
                // this.addView(this.frmContext, 0);
                // this.isLeft=false;
                // this.intCurArray=1;
                // this.handPlay();
                //
                // this.gd = new GestureDetector(this.getContext(),this.gdHandPlay);
                // this.gd.setIsLongpressEnabled(false);
            }

            // Main Control
            frame.addView(this, ClassObject.getParams(this.rctMain));

        } catch (Exception e) {
            MyLog.e(TAG, "IRMulti.initData:" + e.toString());
        }
    }

    // 自动播放-----------------------------------
    private void autoPlay() {
        try {
            this.intCurArray++;
            if (this.intCurArray >= this.arrJson.length()) {
                if (this.isFinishTurn) {
                    if (this.actMain.finishTurn())
                        return;
                }
                // kill die Round
                if (this.isDieLoop) {
                    MyLog.e(TAG, "ViewImages.autoUpdateImage: is die loop");
                    return;
                }
                this.intCurArray = 0;
            }

            if (this.intArrayId != this.intCurArray) {
                String strT = arrJson.getString(this.intCurArray);
                if (inroids.common.System.isNotNullString(strT)) {
                    String strF = this.strResource + "/" + strT;
                    if (inroids.common.FileManage.isExistsFile(strF)) {
                        this.isDieLoop = false;
                        if (this.getChildCount() > 1) {
                            this.removeViewAt(0);
                        }
                        IRMultiEntry imgT = new IRMultiEntry(this.actMain);
                        this.addView(imgT, 0);
                        imgT.setOnMyListener(new OnMyEvent() {
                            // 0:ok -1:cancel 1:finish radian
                            @Override
                            public void onMy(Object objT, int iTag) {
                                if (iTag == 1) {
                                    IRMulti.this.autoPlay();
                                } else {
                                    IRMulti.this.autoPlayAnimation();
                                }
                            }
                        });
                        imgT.initData(this.rctMain, this.rctContext, this.intRadian, this.intDuration, strF,
                                arrJson.getString(this.intCurArray), this.actMain.imgCache);
                        this.intArrayId = this.intCurArray;
                    } else {
                        // file is Exists
                        this.isDieLoop = false;
                        if (this.getChildCount() > 1) {
                            this.removeViewAt(0);
                        }
                        ImageLoad imgT = new ImageLoad(this.actMain);
                        this.addView(imgT, 0);
                        imgT.setOnMyListener(new OnMyEvent() {
                            // 0:ok -1:cancel 1:finish radian
                            @Override
                            public void onMy(Object objT, int iTag) {
                                // System.out.println("tag:"+iTag);
                                if (iTag == 1) {// finish load
                                    IRMulti.this.autoPlay();
                                } else {
                                    IRMulti.this.autoPlayAnimation();
                                }

                            }
                        });
                        imgT.initData(this.intDuration,
                                Graphics.getImageFromAssetsFile(this.getContext(), "Overdue.png"));
                        this.intArrayId = this.intCurArray;

                        // this.autoUpdateImage();

                    }
                } else {
                    this.autoPlay();
                }

            } else {// 单节目循环
                if (this.getChildCount() > 0) {
                    IRMultiEntry v = (IRMultiEntry)this.getChildAt(0);
                    v.replay();
                }
            }
        } catch (Exception e) {
            MyLog.e(TAG, "IRMulti.autoUpdateImage:" + e.toString());
        }
    }

    // 自动播放动画
    private void autoPlayAnimation() {
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
                            IRMulti.this.removeViewAt(1);
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
                            IRMulti.this.removeViewAt(1);
                        }
                    });
                }
            }
        } catch (Exception e) {
            MyLog.e(TAG, "IRMulti.autoAnimation:" + e.toString());
        }
    }

    // Single and double Event------------------------------
    GestureDetector.SimpleOnGestureListener gdClick = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (inroids.common.System.isNotNullString(IRMulti.this.strOpenFile)) {
                IRMulti.this.actMain.openFile(IRMulti.this.strOpenFile);
            } else {
                IRMulti.this.actMain.isEventTurn = true;
                IRMulti.this.actMain.turnPage(IRMulti.this.intTurnId, IRMulti.this.intEffect);
            }
            return false;
        }
    };

    // 手动播放-----------------------------
    private void handPlay() {
        try {
            // 当前位置
            this.intCurArray = this.isLeft ? this.intCurArray++ : this.intCurArray--;
            // 不循环
            if (!this.isLoop) {
                //
                if (this.intCurArray < 0) {
                    this.intCurArray = 0;
                    return;
                } else if (this.intCurArray >= this.arrJson.length()) {
                    this.intCurArray = this.arrJson.length() - 1;
                    return;
                }
            }
            // 检查是否开始循环
            if (this.intCurArray >= this.arrJson.length()) {
                this.intCurArray = 0;
            } else if (this.intCurArray < 0) {
                this.intCurArray = this.arrJson.length() - 1;
            }
            // 更新页码
            if (this.frmPage != null)
                this.frmPage.updatePage(this.intCurArray, this.arrJson.length());
            // 更新页码控件
            this.updatePageButton();

            // this.vwText.setText("")
            if (this.intArrayId != this.intCurArray) {
                IRMulti.this.intCurDuration = 0;
                String strT = arrJson.getString(this.intCurArray);
                if (inroids.common.System.isNotNullString(strT)) {
                    String strF = this.strResource + File.separator + strT;
                    if (inroids.common.FileManage.isExistsFile(strF)) {
                        this.vw0 = new IRMultiEntry(this.actMain);
                        this.frmContext.addView(this.vw0, 0);
                        this.vw0.initData(this.rctMain, this.rctContext, this.intRadian, 0, strF,
                                arrJson.getString(this.intCurArray), this.actMain.imgCache);
                        this.vw0.setOnMyListener(new OnMyEvent() {
                            // 0:ok -1:cancel 1:finish radian
                            @Override
                            public void onMy(Object objT, int iTag) {
                                if (iTag == -1) {
                                    IRMulti.this.handPlay();
                                }

                            }
                        });
                        this.intArrayId = this.intCurArray;
                    } else {
                        this.handPlay();
                    }

                } else {
                    this.handPlay();
                }
            }
        } catch (Exception e) {
            MyLog.e(TAG, "IRMultiImage.handUpdateImage:" + e.toString());
        }

    }

    // 更新页码控件
    private void updatePageButton() {
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
            MyLog.e(TAG, "IRMultiImage.updateButton:" + e.toString());
        }
    }

    // 定时检查页码显示
    Handler hndDuration = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            IRMulti.this.intCurDuration++;//
            if (!IRMulti.this.isHidePage && IRMulti.this.intCurDuration >= 8) {
                IRMulti.this.intCurDuration = 0;
                IRMulti.this.isHidePage = true;
                if (IRMulti.this.frmPage != null)
                    IRMulti.this.frmPage.setVisibility(8);
            }
            super.handleMessage(msg);
        }
    };

    // Single and double Event------------------------------
    GestureDetector.SimpleOnGestureListener gdHandPlay = new GestureDetector.SimpleOnGestureListener() {
        // 按下
        @Override
        public boolean onDown(MotionEvent e) {
            // 显示页码
            IRMulti.this.intCurDuration = 0;
            if (IRMulti.this.isHidePage) {
                if (IRMulti.this.frmPage != null)
                    IRMulti.this.frmPage.setVisibility(0);
                IRMulti.this.isHidePage = false;
            }
            return true;
        }

        // 滑动
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (IRMulti.this.intPlayMode != 0) {
                IRMulti.this.isScroll = true;
                if (IRMulti.this.vw0 != null)
                    IRMulti.this.vw0.setLeft(IRMulti.this.vw0.getLeft() - (int)distanceX);
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        public boolean onDoubleTapEvent(MotionEvent e) {
            if (e.getAction() == MotionEvent.ACTION_UP) {
                // 双击
                if (IRMulti.this.isDoubleClick && IRMulti.this.vw0 != null && IRMulti.this.vw0.intMultiKind == 1) {
                    ClassPublic.addImageShow(IRMulti.this.frmMain, IRMulti.this.vw0.strCurFile);
                }
            }
            return super.onDoubleTapEvent(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (IRMulti.this.isEnableTouch) {
                if (inroids.common.System.isNotNullString(IRMulti.this.strOpenFile)) {
                    IRMulti.this.actMain.openFile(IRMulti.this.strOpenFile);
                } else {
                    IRMulti.this.actMain.isEventTurn = true;
                    IRMulti.this.actMain.turnPage(IRMulti.this.intTurnId, IRMulti.this.intTEffect);
                }
            }
            return false;
        }
    };

    // @Override
    // public boolean onTouchEvent(MotionEvent ev) {
    private void returnAnimation() {
        try {
            TranslateAnimation aa = new TranslateAnimation(this.vw0.getLeft(), 0, 0, 0);//
            aa.setDuration(250);
            this.vw0.startAnimation(aa);
            this.vw0.setLeft(0);
        } catch (Exception e) {
            MyLog.e(TAG, "IRMultiImage.returnAnimation:" + e.toString());
        }
    }

    // }*/
    // Event------------------------------------------
    // Touch Event
    @Override
    public boolean onTouch(View arg0, MotionEvent event) {
        try {
            this.intCurDuration = 0;
            this.actMain.clearCurPos();

            if (this.actMain.isCanTouch) {
                if (this.intPlayMode != 0 || this.isEnableTouch) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (this.vw0 != null && this.isScroll) {
                            int intV = this.vw0.getLeft();
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
                        // this.img0=(ImageView)this.getChildAt(0);
                        this.isScroll = false;
                    }
                    return this.gd.onTouchEvent(event);
                }
            }
        } catch (Exception e) {
            MyLog.e(TAG, "IRMultiImage.onTouch:" + e.toString());
        }
        return false;
    }

    // animation-------------------------------------------------
    private void handAnimation() {
        try {
            if (IRMulti.this.frmContext.getChildCount() > 0) {
                int intEnd = 0;
                if (this.isLeft) {
                    intEnd = -1 * this.rctMain.width();
                } else {
                    intEnd = this.rctMain.width();
                }
                TranslateAnimation aa = new TranslateAnimation(this.vw0.getLeft(), intEnd, 0, 0);//
                aa.setDuration(250);
                this.vw0.startAnimation(aa);
                aa.setAnimationListener(new AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        IRMulti.this.frmContext.removeViewAt(0);
                        IRMulti.this.handPlay();
                    }
                });
            }

        } catch (Exception e) {
            MyLog.e(TAG, "IRMultiImage.handAnimation:" + e.toString());
        }
    }

}
