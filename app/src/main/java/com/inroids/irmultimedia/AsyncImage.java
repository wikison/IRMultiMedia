package com.inroids.irmultimedia;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.File;

import inroids.common.MyLog;

public class AsyncImage extends AsyncTask<String, Integer, Bitmap> {
    private static final String TAG = "IRMultimedia";

    private ImageView imvMain = null;

    private int intRadian = 0, intWidth = 0, intHeight = 0;

    // listener Image
    private OnMyEvent mListener = null;

    private String strKey = null;

    private ImageMemoryCache imgCache = null;

    // set Listener 
    public void setOnMyAsyncListener(OnMyEvent listener) {
        this.mListener = listener;
    }

    public AsyncImage(ImageView imvT, int intR, int intW, int intH, String sKey, ImageMemoryCache iCache) {
        super();
        imvMain = imvT; // 图片视图
        intRadian = intR; // 圆角角度
        intWidth = intW; // 宽度
        intHeight = intH; // 高度
        strKey = sKey; // 缓存关键字
        imgCache = iCache; // 缓存对象
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap result = null;
        result = BitmapFactory.decodeFile(params[0], this.setBitmapOption(params[0]));
        return result;
        // return BitmapFactory.decodeFile(params[0]);
    }

    private BitmapFactory.Options setBitmapOption(String sFile) {
        File file = new File(sFile);
        BitmapFactory.Options opt = new BitmapFactory.Options();
        // opt.inJustDecodeBounds = true;
        // 设置只是解码图片的边距，此操作目的是度量图片的实际宽度和高度
        // BitmapFactory.decodeFile(sFile, opt);
        opt.inDither = false;
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        // 设置加载图片的颜色数为16bit，默认是RGB_8888，表示24bit颜色和透明通道，但一般用不上
        // 设置缩放比,1表示原比例，2表示原来的四分之一....
        if (file.length() / 1024 / 1024 < 2) {
            opt.inSampleSize = 1;
        } else if (file.length() / 1024 / 1024 > 2 && file.length() / 1024 / 1024 < 3) {
            opt.inSampleSize = 2;
        } else if (file.length() / 1024 / 1024 > 3 && file.length() / 1024 / 1024 < 8) {
            opt.inSampleSize = 4;
        } else if (file.length() / 1024 / 1024 > 8) {
            opt.inSampleSize = 8;
        }

        // 计算缩放比
        // opt.inJustDecodeBounds = false;
        return opt;
    }

    @Override
    protected void onCancelled() {
        // 加载失败
        if (AsyncImage.this.mListener != null) {
            AsyncImage.this.mListener.onMy(null, -1);
            System.out.println("图片加载失败1...");
        }
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            try {
                System.gc(); // 内存释放
                //
                if (imvMain != null) {
                    Bitmap bitM = null;
                    // show image
                    if (intRadian > 0) {// 图片圆角
                        bitM = inroids.common.Graphics.getRoundedCornerBitmap(result, intRadian, intWidth, intHeight);
                        imvMain.setImageBitmap(bitM);
                    } else {
                        bitM = result;
                        imvMain.setImageBitmap(bitM);
                    }
                    // 缓存图片
                    if (imgCache != null && strKey != null) {
                        imgCache.put(strKey, bitM);
                    }
                    // send show success
                    if (AsyncImage.this.mListener != null) {
                        AsyncImage.this.mListener.onMy(null, 0);
                    }
                }
            } catch (Exception e) {
                if (!result.isRecycled())
                    result.recycle();
                MyLog.e(TAG, "loadBitmap.onPostExecute:" + e.toString());
            }
        } else {
            // 加载失败并发送失败值 
            if (AsyncImage.this.mListener != null) {
                AsyncImage.this.mListener.onMy(null, -1);
                System.out.println("图片加载失败2...");
            }
        }
    }

}
