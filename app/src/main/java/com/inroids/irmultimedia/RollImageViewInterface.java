package com.inroids.irmultimedia;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import org.json.JSONObject;

public abstract class RollImageViewInterface {

    Object tag;
    protected Context mContext;
    protected LayoutInflater mInflate;
    protected JSONObject objT;
    protected ImageManager mImageManager;

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public RollImageViewInterface(Context context) {
        this.mContext = context;
        mInflate = LayoutInflater.from(context);
        mImageManager = new ImageManager(context);
    }

    public void initData(FrameLayout frame, JSONObject objT, String strRes) {
        this.objT = objT;
        getView(frame, objT, strRes);
    }

    protected abstract void getView(FrameLayout frameLayout, JSONObject t, String strRes);
}
