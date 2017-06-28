package com.inroids.irmultimedia;

import inroids.common.FileManage;
import inroids.common.Graphics;
import inroids.common.MyLog;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.widget.ImageView;

public class IRMultiEntry extends android.widget.FrameLayout {
    private static final String TAG = "IRMultimedia";

    public ImageView imgBack = null, img1 = null;

    VideoLoad vdo1 = null;

    SwfLoad vid1 = null;

    public int intMultiKind = 0;

    public String strCurFile = null;

    private Activity actMain = null;

    private OnMyEvent mListener = null;

    public IRMultiEntry(Context context) {
        super(context);
        this.actMain = (Activity)context;
    }

    public int getFileKind(String strT) {
        strT = strT.toLowerCase();
        if (strT.endsWith(".png") || strT.endsWith(".jpg") || strT.endsWith(".bmp") || strT.endsWith(".gif"))
            return 1;
        if (strT.endsWith(".mp4") || strT.endsWith(".avi"))
            return 2;
        if (strT.endsWith(".swf") || strT.endsWith(".wmv") || strT.endsWith(".rmvb") || strT.endsWith(".rm")
            || strT.endsWith(".mov") || strT.endsWith(".flv"))
            return 3;
        return 0;
    }

    public void setOnMyListener(OnMyEvent listener) {
        this.mListener = listener;
    }

    public void initData(Rect rctTemp, Rect rctEntry, int iRadian, int iDuration, String sFile, String sKey,
        ImageMemoryCache imgCache) {
        try {
            Boolean isFind = false;
            if (inroids.common.System.isNotNullString(sFile)) {
                if (FileManage.isExistsFile(sFile)) {
                    this.strCurFile = sFile;
                    int iKind = this.getFileKind(sFile);
                    if (iKind == 1) {
                        isFind = true;
                        this.intMultiKind = iKind;
                        ImageLoad vwT = new ImageLoad(this.actMain);
                        this.addView(vwT, 0, ClassObject.getParams(rctEntry));
                        vwT.setOnMyListener(new OnMyEvent() {
                            // 0:ok -1:cancel 1:finish radian
                            @Override
                            public void onMy(Object objT, int iTag) {
                                if (IRMultiEntry.this.mListener != null) {
                                    IRMultiEntry.this.mListener.onMy(null, iTag);// finish Animation
                                }
                            }
                        });
                        vwT.initData(rctEntry, iRadian, iDuration, sFile, sKey, imgCache);

                    } else if (iKind == 2) {
                        isFind = true;
                        this.intMultiKind = iKind;
                        vdo1 = new VideoLoad(this.actMain);
                        this.addView(vdo1, 0, ClassObject.getParams(rctEntry));
                        vdo1.setOnMyListener(new OnMyEvent() {
                            // 0:ok -1:cancel 1:finish radian
                            @Override
                            public void onMy(Object objT, int iTag) {
                                if (IRMultiEntry.this.mListener != null) {
                                    IRMultiEntry.this.mListener.onMy(null, iTag);// finish Animation
                                }
                            }
                        });
                        vdo1.initData(rctEntry, sFile);
                    } else if (iKind == 3) {
                        if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(this.actMain)) {
                            if (IRMultiEntry.this.mListener != null) {
                                IRMultiEntry.this.mListener.onMy(null, -1);// finish Animation
                            }
                        } else {
                            isFind = true;
                            this.intMultiKind = iKind;
                            vid1 = new SwfLoad(this.actMain);
                            this.addView(vid1, 0, ClassObject.getParams(rctEntry));
                            vid1.setOnMyListener(new OnMyEvent() {
                                // 0:ok -1:cancel 1:finish radian
                                @Override
                                public void onMy(Object objT, int iTag) {
                                    if (IRMultiEntry.this.mListener != null) {
                                        IRMultiEntry.this.mListener.onMy(null, iTag);// finish Animation
                                    }
                                }
                            });
                            vid1.initData(rctEntry, sFile);
                        }
                    }
                } else {
                    ImageLoad vwT = new ImageLoad(this.actMain);
                    this.addView(vwT, 0, ClassObject.getParams(rctEntry));
                    vwT.setOnMyListener(new OnMyEvent() {
                        // 0:ok -1:cancel 1:finish radian
                        @Override
                        public void onMy(Object objT, int iTag) {
                            if (IRMultiEntry.this.mListener != null) {
                                IRMultiEntry.this.mListener.onMy(null, iTag);// finish Animation
                            }
                        }
                    });
                    vwT.initData(iDuration, Graphics.getImageFromAssetsFile(this.getContext(), "Overdue.png"));
                    // Graphics.addOverdueView(this.getContext(), this, rctEntry, "Overdue.png");
                }
            }
            if (!isFind) {
                if (IRMultiEntry.this.mListener != null) {
                    IRMultiEntry.this.mListener.onMy(null, -1);// finish Animation
                }
            }
        } catch (Exception e) {
            MyLog.e(TAG, "ViewMultiEntry.initData:" + e.toString());
        }
    }

    // replay
    public void replay() {
        if (this.intMultiKind == 2) {
            if (vdo1 != null)
                vdo1.replay();
        } else if (this.intMultiKind == 3) {
            if (vid1 != null)
                vid1.replay();
        }
    }
}
