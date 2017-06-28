package com.inroids.irmultimedia;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import inroids.common.DensityUtil;
import inroids.common.MyLog;

/**
 * Created by Wikison on 2016/7/22.
 */
public class IRRollImages extends RollImageViewInterface {
    private static final String TAG = "IRMultimedia";
    private MultiPlayActivity actMain = null;
    private LayoutInflater mInflater;
    private FrameLayout frmMain = null;
    ViewPager mViewPager;
    LinearLayout llIndexContainer;
    LinearLayout llIndexContainerNum;
    private JSONArray arrJson;
    private Rect rctMain, rctContext;
    private String strResource = null;
    int intDuration, intPlayMode;
    private List<ImageView> listImageViews = new ArrayList<>();
    int intTurnId = 0, intTEffect = 0;
    private Thread mThread;
    //是否停止线程
    private boolean isStopThread = false;
    //是否可以触摸
    private boolean isEnableTouch = false;
    //打开文件名称
    private String strOpenFile;
    private ScrollImagesAdapter mScrollImagesAdapter;
    private ImageManager mImageManager;
    boolean isShowPages = false;
    boolean isTwoPages = false;

    private static final int TYPE_CHANGE_IMAGE = 0;
    public static final int TYPE_INDICATOR_DOT = 110;
    public static final int TYPE_INDICATOR_NUM = 120;
    private int indicator_type = TYPE_INDICATOR_NUM;

    public IRRollImages(Context context) {
        super(context);

        this.actMain = (MultiPlayActivity) context;
        mInflater = LayoutInflater.from(context);
        mImageManager = new ImageManager(actMain);

    }

    private void dealWithTheView(JSONArray jsonArray) {
        try {
            listImageViews.clear();
            int size = jsonArray.length();
            for (int i = 0; i < size; i++) {
                listImageViews.add(createImageView(jsonArray.getString(i)));
            }
            if (size == 2) {
                isTwoPages = true;
                listImageViews.add(createImageView(jsonArray.getString(0)));
                listImageViews.add(createImageView(jsonArray.getString(1)));
            } else {
                isTwoPages = false;
            }

            mScrollImagesAdapter = new ScrollImagesAdapter(actMain, listImageViews);
            mViewPager.setAdapter(mScrollImagesAdapter);

            if (isShowPages) {
                llIndexContainer.setVisibility(View.GONE);
                llIndexContainerNum.setVisibility(View.VISIBLE);
                addIndicatorImageViews(size);
            } else {
                llIndexContainer.setVisibility(View.GONE);
                llIndexContainerNum.setVisibility(View.GONE);
            }

            setListener(size);
            if (intPlayMode == 0) {
                startPlay();
            }
        } catch (Exception e) {
        }
    }

    public void setData(JSONArray jsonArray) {
        stopPlay();
        dealWithTheView(jsonArray);
    }

    // 创建要显示的ImageView
    private ImageView createImageView(final String filePath) {
        ImageView imageView = new ImageView(actMain);
        imageView.setLayoutParams(ClassObject.getParams(rctContext));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEnableTouch) {
                    if (inroids.common.System.isNotNullString(strOpenFile)) {
                        IRRollImages.this.actMain.openFile(strOpenFile);
                    } else {
                        IRRollImages.this.actMain.isEventTurn = true;
                        IRRollImages.this.actMain.turnPage(intTurnId, intTEffect);
                    }
                }
            }
        });
        mImageManager.loadLocalImage(strResource + File.separator + filePath, imageView);
        return imageView;
    }


    // 为ViewPager设置监听器
    private void setListener(final int size) {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (listImageViews != null && listImageViews.size() > 0) {
                    int newPosition = position % size;
                    System.out.println("newPosition--------------->" + newPosition);

                    if (indicator_type == TYPE_INDICATOR_DOT) {
                        for (int i = 0; i < size; i++) {
                            llIndexContainer.getChildAt(i).setEnabled(false);
                            if (i == newPosition) {
                                llIndexContainer.getChildAt(i).setEnabled(true);
                            }
                        }
                    } else if (indicator_type == TYPE_INDICATOR_NUM) {
                        setNum(newPosition + 1);
                    }


                }
            }

            @Override
            public void onPageScrolled(int position, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            private boolean moved;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    moved = false;
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    moved = true;
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (!moved) {
                        {
                            //view.performClick();
                        }
                    }
                }

                return false;
            }
        });

        mViewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEnableTouch) {
                    if (inroids.common.System.isNotNullString(strOpenFile)) {
                        IRRollImages.this.actMain.openFile(strOpenFile);
                    } else {
                        IRRollImages.this.actMain.isEventTurn = true;
                        IRRollImages.this.actMain.turnPage(intTurnId, intTEffect);
                    }
                }
            }
        });

    }

    // 添加指示图标
    public void addIndicatorImageViews(int size) {
        // 只有一张图片时不显示指示器
        if (size == 1) {
            llIndexContainer.setVisibility(View.GONE);
            llIndexContainerNum.setVisibility(View.GONE);
            return;
        }
        if (indicator_type == TYPE_INDICATOR_DOT) {
            llIndexContainer.setVisibility(View.VISIBLE);
            llIndexContainerNum.setVisibility(View.GONE);
            llIndexContainer.removeAllViews();
            for (int i = 0; i < size; i++) {
                ImageView iv = new ImageView(actMain);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(DensityUtil.dip2px(actMain, 5), DensityUtil.dip2px(actMain, 5));
                if (i != 0) {
                    lp.leftMargin = DensityUtil.dip2px(actMain, 7);
                }
                iv.setLayoutParams(lp);
                iv.setBackgroundResource(R.drawable.xml_round_orange_grey_sel);
                iv.setEnabled(false);
                if (i == 0) {
                    iv.setEnabled(true);
                }
                llIndexContainer.addView(iv);
            }
        } else if (indicator_type == TYPE_INDICATOR_NUM) {
            llIndexContainer.setVisibility(View.GONE);
            llIndexContainerNum.setVisibility(View.VISIBLE);
            setNum(1);
        }
    }

    public void setNum(int currentNum) {
        llIndexContainerNum.removeAllViews();
        TextView tv = new TextView(actMain);
        tv.setPadding(DensityUtil.dip2px(actMain, 10), DensityUtil.dip2px(actMain, 2), DensityUtil.dip2px(actMain, 10), DensityUtil.dip2px(actMain, 2));
        tv.setTextColor(0xffffffff);
        if (listImageViews.size() == 1) {
            tv.setText("1");
        } else {
            if (isTwoPages) {
                tv.setText(currentNum+ "/" + (listImageViews.size() - 2));
            } else {
                tv.setText(currentNum + "/" + listImageViews.size());
            }

        }
        tv.setBackgroundResource(R.drawable.xml_oval_half_transparent_bg);
        llIndexContainerNum.addView(tv);
    }

    @Override
    protected void getView(FrameLayout frameLayout, JSONObject t, String strRes) {
        try {
            // Resource Path
            this.strResource = strRes;
            // FrameLayout frame
            if (this.frmMain == null) {
                this.frmMain = frameLayout;
            }

            // 图片数组
            this.arrJson = objT.getJSONArray("content");
            isShowPages = ClassObject.getBoolean(objT, "showPages");
            // duration
            this.intDuration = ClassObject.getInt(objT, "duration");
            //间隔大于0, 设置成毫秒
            if (intDuration > 0)
                intDuration = Integer.valueOf(String.valueOf(intDuration) + "000");
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

            // Rect Main("x":"701","y":"81","w":"208","h":"208")
            //幻灯片控件位置及大小
            this.rctMain = ClassObject.getRect(objT);
            //幻灯片控件中内容的位置及大小--即ViewPager
            this.rctContext = new Rect(0, 0, this.rctMain.width(), this.rctMain.height());

            View view = mInflater.inflate(R.layout.view_scroll_image, this.frmMain, false);
            view.setLayoutParams(ClassObject.getParams(rctContext));

            mViewPager = (ViewPager) view.findViewById(R.id.vp_images);
            llIndexContainer = (LinearLayout) view.findViewById(R.id.ll_index_container);
            llIndexContainerNum = (LinearLayout) view.findViewById(R.id.ll_index_container_num);

            dealWithTheView(arrJson);
            //设置ViewPager数据

            frameLayout.addView(view, ClassObject.getParams(rctMain));

        } catch (Exception e) {
            MyLog.e(TAG, "ViewImages.initData:" + e.toString());
        }
    }


    public class ScrollImagesAdapter extends PagerAdapter {

        private Context mContext;
        private List<ImageView> ivList; // ImageView的集合
        private int count = 1; // 图片数量

        public ScrollImagesAdapter(Context context, List<ImageView> ivList) {
            super();
            this.mContext = context;

            this.ivList = ivList;

            if (ivList != null && ivList.size() > 0) {
                count = ivList.size();
            }
        }

        @Override
        public int getCount() {
            if (count == 1) {
                return count;
            } else {
                return Integer.MAX_VALUE;
            }
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int newPosition = position % count;
            if (position < 0) {
                newPosition = ivList.size() + position;
            }
            // 先移除再添加，更新图片在container中的位置（把iv放至container末尾）
            ImageView iv = ivList.get(newPosition);
            ViewParent vp = iv.getParent();
            if (vp != null) {
                ViewGroup parent = (ViewGroup) vp;
                parent.removeView(iv);
            }
            container.addView(iv);
            return iv;
        }
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == TYPE_CHANGE_IMAGE) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
            }
        }
    };

    // 启动循环广告的线程
    private void startPlay() {
        isStopThread = false;
        // 一个广告的时候不用转
        if (listImageViews == null || listImageViews.size() <= 1) {
            return;
        }
        if (mThread == null) {
            mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    // 当没离开该页面时一直转
                    while (!isStopThread) {
                        // 每隔5秒转一次
                        SystemClock.sleep(intDuration);
                        // 在主线程更新界面
                        mHandler.sendEmptyMessage(TYPE_CHANGE_IMAGE);
                    }
                }
            });
            mThread.start();
        }
    }

    // 停止循环广告的线程，清空消息队列
    private void stopPlay() {
        isStopThread = true;
        if (mHandler != null && mHandler.hasMessages(TYPE_CHANGE_IMAGE)) {
            mHandler.removeMessages(TYPE_CHANGE_IMAGE);
        }
    }

}
