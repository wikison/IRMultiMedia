package com.inroids.irmultimedia;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by Wikison on 2016/7/23.
 */
public class ImageManager {
    private Context mContext;

    public ImageManager(Context context) {
        this.mContext = context;
    }

    // 加载本地图片
    public void loadLocalImage(String path, ImageView imageView) {
        Glide.with(mContext)
                .load("file://" + path)
                .placeholder(R.color.font_black_e0)
                .error(R.color.font_black_e0)
                .crossFade()
                .into(imageView);
    }
}
