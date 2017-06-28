package com.inroids.irmultimedia;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import inroids.common.MyLog;

public class ImageLoad extends android.widget.FrameLayout implements Runnable {
    private static final String strTag = "IRMultimedia";

    private Activity actMain = null;

    public ImageView imgMain = null;

    private int intCurPos = 0, intDuration = 0;

    private OnMyEvent mListener = null;

    public String strFile;

    private Boolean isRunFinish = false; // 是否加载完成

    // Create this control------------------------
    public ImageLoad(Context context) {
        super(context);
        this.actMain = (Activity)context;
    }

    // set listener
    public void setOnMyListener(OnMyEvent listener) {
        this.mListener = listener;
    }

    // 运行时钟控件
    @Override
    public void run() {
        if (this.actMain.getTaskId() > 0) {
            if (ImageLoad.this.isRunFinish) {
                ImageLoad.this.intCurPos++;
                if (ImageLoad.this.intCurPos > ImageLoad.this.intDuration) {
                    this.removeCallbacks(this);
                    // 返回监听
                    if (ImageLoad.this.mListener != null) {
                        ImageLoad.this.mListener.onMy(null, 1);// finish Duration
                    }
                } else {
                    this.postDelayed(this, 1000);
                }
            }
        } else {
            this.removeCallbacks(this);
        }
    }

    // 初始化控件
    public void initData(Rect rctTemp, int iRadian, int iDuration, String sFile, String sKey, ImageMemoryCache imgCache) {
        try {
            this.strFile = sFile;
            // Imageview control
            this.imgMain = new ImageView(this.actMain);
            this.imgMain.setScaleType(ScaleType.FIT_XY);
            this.addView(this.imgMain, 0);

            // 在缓存中查找图片
            Boolean isFind = false;
            if (imgCache != null && sKey != null) {
                Bitmap bitT = imgCache.get(sKey);
                if (bitT != null) {
                    this.imgMain.setImageBitmap(bitT);
                    ImageLoad.this.isRunFinish = true;
                    if (ImageLoad.this.mListener != null) {
                        ImageLoad.this.mListener.onMy(null, 0);// send load finish
                    }
                    isFind = true;
                }
            }
            // 图片不在缓存中，加载
            if (!isFind) {
                // async image
                AsyncImage imgAsync =
                    new AsyncImage(this.imgMain, iRadian, rctTemp.width(), rctTemp.height(), sKey, imgCache);
                imgAsync.execute(sFile);
                imgAsync.setOnMyAsyncListener(new OnMyEvent() {
                    @Override
                    public void onMy(Object objT, int iTag) {
                        ImageLoad.this.isRunFinish = true;// 加载
                        if (iTag == 0) {// ok
                            if (ImageLoad.this.mListener != null) {
                                ImageLoad.this.mListener.onMy(null, 0);// send load finish message
                            }
                        } else {
                            if (ImageLoad.this.mListener != null) {
                                ImageLoad.this.mListener.onMy(null, -1);// send failed message
                                System.out.println("图片加载失败3...");
                            }
                        }
                    }

                });
            }
            //
            if (iDuration > 0) {
                this.intDuration = iDuration;
                this.postDelayed(this, 1000);
            }
        } catch (Exception e) {
            MyLog.e(strTag, "ImageLoad.initData:" + e.toString());
        }
    }

    public void initData(int iDuration, Bitmap bitmap) {
        try {
            // Imageview control
            this.imgMain = new ImageView(this.actMain);
            this.imgMain.setScaleType(ScaleType.FIT_XY);
            this.addView(this.imgMain, 0);

            imgMain.setImageBitmap(bitmap);
            ImageLoad.this.isRunFinish = true;
            if (ImageLoad.this.mListener != null) {
                ImageLoad.this.mListener.onMy(null, 0);// send load finish
            }
            if (iDuration > 0) {
                this.intDuration = iDuration;
                this.postDelayed(this, 1000);
            }
        } catch (Exception e) {
            MyLog.e(strTag, "ImageLoad.initData:" + e.toString());
        }
    }
}
