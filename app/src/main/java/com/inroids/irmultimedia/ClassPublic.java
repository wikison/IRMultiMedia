//ClassPublic.java
//Created by sealy on 2013-06-01.  
//Copyright 2013 Sealy, Inc. All rights reserved.

package com.inroids.irmultimedia;

import org.json.JSONException;
import org.json.JSONObject;
import android.graphics.Rect;
import android.text.StaticLayout;
import android.text.Layout.Alignment;
import android.text.TextPaint;
import android.widget.FrameLayout;
import inroids.common.MyLog;

public abstract class ClassPublic {
    private static final String TAG = "IRMultimedia";

    // (1)Image view-----------------------------------------------------------------------------
    // {"type":"1","content":"Ir31.jpg","x":"97","y":"48","w":"150","h":"150","radian":"0","enable":"0","openFile":"Ir14.jpg","event":"31","effect":"1"}
    /**
     * add Image View
     * 
     * @param ctx
     *            a context.
     * @param objT
     *            a JSON String.
     * @param strRes
     *            resource path.
     */
    public static void addImageView(FrameLayout frame, String strRes, String strT) {
        try {
            addImageView(frame, strRes, new JSONObject(strT));
        } catch (JSONException e) {
            MyLog.e(TAG, "ClassPublic.addImageView:" + e.toString());
        }
    }

    /**
     * add Image View
     * 
     * @param ctx
     *            a context.
     * @param objT
     *            a JSONObject.
     * @param strRes
     *            resource path.
     */
    public static void addImageView(FrameLayout frame, String strRes, JSONObject objT) {
        try {
            if (inroids.common.System.isNotNullString(ClassObject.getString(objT, "content"))) {
                IRImage vwT = new IRImage(frame.getContext());
                vwT.setTag(1);
                vwT.initData(frame, objT, strRes);
            }
        } catch (Exception e) {
            MyLog.e(TAG, "ClassPublic.addImageView:" + e.toString());
        }
    }

    // (2)Image view-----------------------------------------------------------------------------
    // {"type":"2","content":[["Ir4.mp4","Ir5.jpg"],["Ir8.jpg","Ir7.jpg"]…],"x":"701","y":"81","w":"208","h":"208","radian":"0","duration":"1","playMode":"0","effect":"0","bgColor":
    // "#ffffff","finishTurn":"0","loop":"0","mX":"701","mY":"81","mW":"208","mH":"208","enable":"0","openFile":"Ir14.jpg","event":"31","tEffect":"1"}
    /**
     * add Multi Image View
     * 
     * @param ctx
     *            a context.
     * @param objT
     *            a JSON String.
     * @param strRes
     *            resource path.
     */
    public static void addMultiImage(FrameLayout frame, String strRes, String strT) {
        try {
            addMultiImage(frame, strRes, new JSONObject(strT));
        } catch (JSONException e) {
            MyLog.e(TAG, "ClassPublic.addImageView:" + e.toString());
        }
    }

    /**
     * add Multi Image View
     * 
     * @param ctx
     *            a context.
     * @param objT
     *            a JSONObject.
     * @param strRes
     *            resource path.
     */
    public static void addMultiImage(FrameLayout frame, String strRes, JSONObject objT) {
        try {
            if (inroids.common.System.isNotNullString(ClassObject.getString(objT, "content"))) {
                // ViewMultiImage vwT=new ViewMultiImage(frame.getContext());
                // vwT.initData(frame,objT, strRes);
            }
        } catch (Exception e) {
            MyLog.e(TAG, "ClassPublic.addImageView:" + e.toString());
        }
    }

    // (2)Image view-----------------------------------------------------------------------------
    // {"type":"2","content":["Ir4.mp4","Ir5.jpg","Ir7.jpg"…],"x":"701","y":"81","w":"208","h":"208","radian":"0","duration":"1","playMode":"0","effect":"0","bgColor":
    // "#ffffff","finishTurn":"0","loop":"0","mX":"701","mY":"81","mW":"208","mH":"208","enable":"0","openFile":"Ir14.jpg","event":"31","tEffect":"1"}
    /**
     * add Multi Image View
     * 
     * @param ctx
     *            a context.
     * @param objT
     *            a JSON String.
     * @param strRes
     *            resource path.
     */
    public static void addMulti(FrameLayout frame, String strRes, String strT) {
        try {
            addMulti(frame, strRes, new JSONObject(strT));
        } catch (JSONException e) {
            MyLog.e(TAG, "ClassPublic.addImageView:" + e.toString());
        }
    }

    /**
     * add Multi Image View
     * 
     * @param ctx
     *            a context.
     * @param objT
     *            a JSONObject.
     * @param strRes
     *            resource path.
     */
    public static void addMulti(FrameLayout frame, String strRes, JSONObject objT) {
        try {
            if (inroids.common.System.isNotNullString(ClassObject.getString(objT, "content"))) {
                IRMulti vwT = new IRMulti(frame.getContext());
                vwT.setTag(3);
                vwT.initData(frame, objT, strRes);
            }
        } catch (Exception e) {
            MyLog.e(TAG, "ClassPublic.addImageView:" + e.toString());
        }
    }

    // (4)Text view-----------------------------------------------------------------------------
    // {"type":"4","content":"中银协文件","x":"0","y":"220","w":"1060","h":"50","alignment":"mr","fontSize":"20px","textColor":"#000000","radian":"0","bgColor":"","fontFamily":"宋体","fontStyle":"0","fontWeight":"0","enable":"0","selectColor":"","openFile":"","event":"","effect":"0","singleLine":"1"},
    /**
     * add Images View
     * 
     * @param ctx
     *            a context.
     * @param objT
     *            a JSON String.
     * @param strRes
     *            resource path.
     */
    public static void addText(FrameLayout frame, String strRes, String strT) {
        try {
            addText(frame, strRes, new JSONObject(strT));
        } catch (JSONException e) {
            MyLog.e(TAG, "ClassPublic.addText:" + e.toString());
        }
    }

    /**
     * add Image View
     * 
     * @param ctx
     *            a context.
     * @param objT
     *            a JSONObject.
     * @param strRes
     *            resource path.
     */
    public static void addText(FrameLayout frame, String strRes, JSONObject objT) {
        try {
            if (inroids.common.System.isNotNullString(ClassObject.getString(objT, "content"))) {
                if (!ClassObject.getBoolean(objT, "enable")) {// Text
                    Rect rctT = ClassObject.getRect(objT);
                    TextPaint pntText = new TextPaint();
                    // set font size
                    int intFontSize = ClassObject.getFontSize(objT, "fontSize");
                    if (intFontSize > 0)
                        pntText.setTextSize(intFontSize);

                    // set font bold
                    int intBold = ClassObject.getInt(objT, "fontWeight");
                    if (intBold == 1)
                        pntText.setFakeBoldText(true);

                    // set font style
                    int intSkewX = ClassObject.getInt(objT, "fontStyle");
                    if (intSkewX == 1)
                        pntText.setTextSkewX(-0.5f);

                    StaticLayout layout =
                        new StaticLayout(objT.getString("content"), pntText, rctT.width(), Alignment.ALIGN_NORMAL,
                            1.0F, 0.0F, true);
                    if (layout.getHeight() >= rctT.height()) {
                        IRTexts vwT = new IRTexts(frame.getContext());
                        vwT.setTag(4);
                        vwT.initData(frame, objT);
                    } else {
                        IRText vwT = new IRText(frame.getContext());
                        vwT.setTag(4);
                        vwT.initData(frame, objT);
                    }

                } else {// Text Button
                    IRTextButton vwT = new IRTextButton(frame.getContext());
                    vwT.setTag(4);
                    vwT.initData(frame, objT, strRes);
                }

            }
        } catch (Exception e) {
            MyLog.e(TAG, "ClassPublic.addText:" + e.toString());
        }
    }

    // (5)Images view-----------------------------------------------------------------------------
    // {"type":"5","content":["Ir14.jpg","Ir13.jpg","Ir12.jpg","Ir11.jpg ","Ir8.jpg","Ir7.jpg"],"x":"672","y":"332","w":"566","h":"281","duration":"1","effect":"0","playMode":"0","radian":"0","loop":"0","enable":"0","openFile":"Ir14.jpg","event":"31","tEffect":"1"}
    /**
     * add Images View
     * 
     * @param ctx
     *            a context.
     * @param objT
     *            a JSON String.
     * @param strRes
     *            resource path.
     */
    public static void addImagesView(FrameLayout frame, String strRes, String strT) {
        try {
            addImagesView(frame, strRes, new JSONObject(strT));
        } catch (JSONException e) {
            MyLog.e(TAG, "ClassPublic.addImagesView:" + e.toString());
        }
    }

    /**
     * add Image View
     * 
     * @param ctx
     *            a context.
     * @param objT
     *            a JSONObject.
     * @param strRes
     *            resource path.
     */
    public static void addImagesView(FrameLayout frame, String strRes, JSONObject objT) {
        try {
            if (inroids.common.System.isNotNullString(ClassObject.getString(objT, "content"))) {
                IRRollImages vwT = new IRRollImages(frame.getContext());
                vwT.setTag(5);
                vwT.initData(frame, objT, strRes);
            }
        } catch (Exception e) {
            MyLog.e(TAG, "ClassPublic.addImagesView:" + e.toString());
        }
    }

    // (6)Image Text-----------------------------------------------------------------------------
    // {"type":"6","content":"Ir31.jpg","x":"678","y":"66","w":"152","h":"152","bgColor":"","radian":"0","openFile":"Ir14.jpg","event":"31","effect":"1","iVisible":"0","iX":"97","iY":"48","iW":"150","iH":"i150","iRadian":"0","iContent":"Ir31.jpg","tVisible":"0","tX":"678","tY":"66","tW":"152","tH":"152",""tAlignment":"mm","tContent
    // ":"文本区域sss","tFontSize":"12px","tTextColor":"#000000","tRadian":"0","tBgColor": "#ffffff","tFontFamily":"宋体","tFontStyle":"1","tFontWeight":"1","tSelectColor":"#000000"}
    /**
     * add Web View
     * 
     * @param ctx
     *            a context.
     * @param objT
     *            a JSON String.
     * @param strRes
     *            resource path.
     */
    public static void addImageText(FrameLayout frame, String strRes, String strT) {
        try {
            addImageText(frame, strRes, new JSONObject(strT));
        } catch (JSONException e) {
            MyLog.e(TAG, "ClassPublic.addImageText:" + e.toString());
        }
    }

    /**
     * add Web View
     * 
     * @param ctx
     *            a context.
     * @param objT
     *            a JSONObject.
     * @param strRes
     *            resource path.
     */
    public static void addImageText(FrameLayout frame, String strRes, JSONObject objT) {
        try {
            IRImageText vwT = new IRImageText(frame.getContext());
            vwT.setTag(6);
            vwT.initData(frame, objT, strRes);
        } catch (Exception e) {
            MyLog.e(TAG, "ClassPublic.addImageText:" + e.toString());
        }
    }

    // (7)TimeLabel-----------------------------------------------------------------------------
    // {"type":"7","x":"156","y":"635","w":"241","h":"27"," content ":"yyyy-MM-dd HH:mm:ss","fontSize":"18px","textColor":"#000000","bgColor":"","fontFamily":"宋体","fontStyle":"1","fontWeight":"1","radian":"0","alignment":"mm"}
    /**
     * add Time Label
     * 
     * @param ctx
     *            a context.
     * @param objT
     *            a JSON String.
     */
    public static void addTimeLabel(FrameLayout frame, String strT) {
        try {
            addTimeLabel(frame, new JSONObject(strT));
        } catch (JSONException e) {
            MyLog.e(TAG, "ClassPublic.addTimeLabel:" + e.toString());
        }
    }

    /**
     * add Time Label
     * 
     * @param ctx
     *            a context.
     * @param objT
     *            a JSONObject.
     */
    public static void addTimeLabel(FrameLayout frame, JSONObject objT) {
        try {
            if (inroids.common.System.isNotNullString(ClassObject.getString(objT, "content"))) {
                IRTimeLabel vwT = new IRTimeLabel(frame.getContext());
                vwT.setTag(7);
                vwT.initData(frame, objT);
            }
        } catch (Exception e) {
            MyLog.e(TAG, "ClassPublic.addTimeLabel:" + e.toString());
        }
    }

    // (9)Marquee view-----------------------------------------------------------------------------
    // {"type":"9","x":"62","y":"159","w":"573","h":"453","fontSize":"18px","textColor":"#000000","bgColor":"","fontFamily":"宋体","content":"android textview怎样实现文字跑马灯效果(垂直移动)","direction":"up","radian":"0","alignment":"left","speed":"1","fontStyle":"0","fontWeight":"0"}
    /**
     * add Marquee View
     * 
     * @param ctx
     *            a context.
     * @param objT
     *            a JSON String.
     */
    public static void addMarqueeView(FrameLayout frame, String strT) {
        try {
            addMarqueeView(frame, new JSONObject(strT));
        } catch (JSONException e) {
            MyLog.e(TAG, "ClassPublic.addMarqueeView:" + e.toString());
        }
    }

    /**
     * add Marquee View
     * 
     * @param ctx
     *            a context.
     * @param objT
     *            a JSONObject.
     */
    public static void addMarqueeView(FrameLayout frame, JSONObject objT) {
        try {
            if (inroids.common.System.isNotNullString(ClassObject.getString(objT, "content"))) {
                IRMarqueeText vwT = new IRMarqueeText(frame.getContext());
                vwT.setTag(9);
                vwT.initData(frame, objT);
            }
        } catch (Exception e) {
            MyLog.e(TAG, "ClassPublic.addMarqueeView:" + e.toString());
        }
    }

    // (10)Web view-----------------------------------------------------------------------------
    // {"type":"10","x":"62","y":"159","w":"573","h":"453","bgColor":"","content":"","openFile":""}
    /**
     * add Web View
     * 
     * @param ctx
     *            a context.
     * @param objT
     *            a JSON String.
     * @param strRes
     *            resource path.
     */
    public static void addWebView(FrameLayout frame, String strRes, String strT) {
        try {
            addWebView(frame, strRes, new JSONObject(strT));
        } catch (JSONException e) {
            MyLog.e(TAG, "ClassPublic.addWebView:" + e.toString());
        }
    }

    /**
     * add Web View
     * 
     * @param ctx
     *            a context.
     * @param objT
     *            a JSONObject.
     * @param strRes
     *            resource path.
     */
    public static void addWebView(FrameLayout frame, String strRes, JSONObject objT) {
        try {
            if (inroids.common.System.isNotNullString(ClassObject.getString(objT, "content"))
                || inroids.common.System.isNotNullString(ClassObject.getString(objT, "openFile"))) {
                IRWebView vwT = new IRWebView(frame.getContext());
                vwT.setTag(10);
                vwT.initData(frame, objT, strRes);
            }
        } catch (Exception e) {
            MyLog.e(TAG, "ClassPublic.addWebView:" + e.toString());
        }
    }

    // (100)View Page-----------------------------------------------------------------------------
    // {"type":"100","bgImage":"Ir3.jpg","bgMusic":"","bgColor":"","turnTime":"0","turnId":"0"","effect":"0"}
    /**
     * add Page View
     * 
     * @param ctx
     *            a context.
     * @param strRes
     *            resource path.
     * @param mp3Main
     *            mp3 player.
     * @param objT
     *            a JSON String.
     */
    public static void addPageView(FrameLayout frame, String strRes, String strT) {
        try {
            addPageView(frame, strRes, new JSONObject(strT));
        } catch (Exception e) {
            MyLog.e(TAG, "ClassPublic.addPageView:" + e.toString());
        }
    }

    /**
     * add Page View
     * 
     * @param ctx
     *            a context.
     * @param strRes
     *            resource path.
     * @param mp3Main
     *            mp3 player.
     * @param objT
     *            a JSONObject.
     */
    public static void addPageView(FrameLayout frame, String strRes, JSONObject objT) {
        try {
            IRPage vwT = new IRPage(frame.getContext());
            vwT.setTag(100);
            vwT.initData(frame, objT, strRes);
        } catch (Exception e) {
            MyLog.e(TAG, "ClassPublic.addPageView:" + e.toString());
        }
    }

    /**
     * add Image View
     * 
     * @param ctx
     *            a context.
     * @param objT
     *            a JSONObject.
     * @param strRes
     *            resource path.
     */
    public static void addImageShow(FrameLayout frame, String strfile) {
        try {
            // ViewImageShow vwT=new ViewImageShow(frame.getContext());
            // vwT.initData(frame,strfile);
        } catch (Exception e) {
            MyLog.e(TAG, "ClassPublic.addImageView:" + e.toString());
        }
    }

}
