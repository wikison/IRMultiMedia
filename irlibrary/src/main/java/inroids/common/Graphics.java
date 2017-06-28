/*
 * Do Graphics
 * 
 * Created by sealy on 2013-07-01. 
 * Copyright 2013 Sealy, Inc. All rights reserved.
 */
package inroids.common;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import java.io.File;
import java.io.InputStream;

/**
 * Graphics
 * 
 * @author Sealy
 */
public abstract class Graphics {
    private static final String sTag = "IRLibrary";

    /**
     * get new bitmap
     * 
     * @param bData
     *            the ole bitmap
     * @param fRound
     *            the Round value
     * @param iWidth
     *            new bitmap width
     * @param iHeight
     *            new bitmap height
     * @return new bitmap or null
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bData, float fRound, int iWidth, int iHeight) {
        try {
            if (iWidth <= 0) {
                iWidth = bData.getWidth();
            }
            if (iHeight <= 0) {
                iHeight = bData.getHeight();
            }
            // Create Bitmap
            Bitmap bOutput = Bitmap.createBitmap(iWidth, iHeight, Config.ARGB_8888);
            // Create Canvas
            Canvas canvas = new Canvas(bOutput);
            final int iColor = 0xff424242;
            final Paint pntT = new Paint();
            final Rect rctSrc = new Rect(0, 0, bData.getWidth(), bData.getHeight());
            final Rect rctDst = new Rect(0, 0, iWidth, iHeight);
            final RectF rctF = new RectF(rctDst);
            pntT.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            pntT.setColor(iColor);
            canvas.drawRoundRect(rctF, fRound, fRound, pntT);
            pntT.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bData, rctSrc, rctDst, pntT);
            return bOutput;
        } catch (Exception e) {
            MyLog.e(sTag, "Graphics.getRoundedCornerBitmap:" + e.toString());
        }
        return null;
    }

    /**
     * create Scaled Bitmap
     * 
     * @param source
     *            the ole bitmap
     * @param width
     *            new bitmap width
     * @param height
     *            new bitmap height
     * @param isS
     *            是否等比缩放
     * */
    public static Bitmap createScaledBitmap(Bitmap source, int width, int height, boolean isS) {
        int intWidth = width, intHeight = height;
        double sx = (double)width / source.getWidth();
        double sy = (double)height / source.getHeight();
        // 等比缩放
        if (isS) {
            if (sx > sy) {
                sx = sy;
                intWidth = (int)(sx * source.getWidth());
            } else {
                sy = sx;
                intHeight = (int)(sy * source.getHeight());
            }
        }
        Rect rctSource = new Rect(0, 0, source.getWidth(), source.getHeight());
        Rect rctTarget = new Rect(0, 0, intWidth, intHeight);
        Bitmap bmpScaled = Bitmap.createBitmap(intWidth, intHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas(bmpScaled);
        canvas.drawBitmap(source, rctSource, rctTarget, new Paint(Paint.FILTER_BITMAP_FLAG));
        return bmpScaled;
    }

    /**
     * 灰度值计算
     * 
     * @param pixels
     *            像素
     * @return int 灰度值
     */
    public static int rgbToGray(int pixels) {
        // int _alpha = (pixels >> 24) & 0xFF;
        int _red = (pixels >> 16) & 0xFF;
        int _green = (pixels >> 8) & 0xFF;
        int _blue = (pixels) & 0xFF;
        return (int)(0.3 * _red + 0.59 * _green + 0.11 * _blue);
    }

    /**
     * 计算数组的平均值
     * 
     * @param pixels
     *            数组
     * @return int 平均值
     */
    public static int average(int[] pixels) {
        float m = 0;
        for (int i = 0; i < pixels.length; ++i) {
            m += pixels[i];
        }
        m = m / pixels.length;
        return (int)m;
    }

    /**
     * 计算"汉明距离"（Hamming distance）。 如果不相同的数据位不超过5，就说明两张图片很相似；如果大于10，就说明这是两张不同的图片。
     * 
     * @param sourceHashCode
     *            源hashCode
     * @param hashCode
     *            与之比较的hashCode
     */
    public static int hammingDistance(String sourceHashCode, String hashCode) {
        int difference = 0;
        int len = sourceHashCode.length();

        for (int i = 0; i < len; i++) {
            if (sourceHashCode.charAt(i) != hashCode.charAt(i)) {
                difference++;
            }
        }
        return difference;
    }

    /**
     * 生成图片指纹
     * 
     * @param source
     *            文件名
     * @return 图片指纹
     */
    public static String produceFingerPrint(Bitmap source) {
        int width = 8;
        int height = 8;

        // 第一步，缩小尺寸。
        // 将图片缩小到8x8的尺寸，总共64个像素。这一步的作用是去除图片的细节，只保留结构、明暗等基本信息，摒弃不同尺寸、比例带来的图片差异。
        Bitmap thumb = createScaledBitmap(source, width, height, false);

        // 第二步，简化色彩。
        // 将缩小后的图片，转为64级灰度。也就是说，所有像素点总共只有64种颜色。
        int[] pixels = new int[width * height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixels[i * height + j] = rgbToGray(thumb.getPixel(i, j));
            }
        }

        // 第三步，计算平均值。
        // 计算所有64个像素的灰度平均值。
        int avgPixel = average(pixels);

        // 第四步，比较像素的灰度。
        // 将每个像素的灰度，与平均值进行比较。大于或等于平均值，记为1；小于平均值，记为0。
        int[] comps = new int[width * height];
        for (int i = 0; i < comps.length; i++) {
            // comps[i]=pixels[i];

            if (pixels[i] >= avgPixel) {
                comps[i] = 1;
            } else {
                comps[i] = 0;
            }
        }

        // 第五步，计算哈希值。
        // 将上一步的比较结果，组合在一起，就构成了一个64位的整数，这就是这张图片的指纹。组合的次序并不重要，只要保证所有图片都采用同样次序就行了。
        StringBuffer hashCode = new StringBuffer();
        for (int i = 0; i < comps.length; i += 4) {
            int result =
                comps[i] * (int)Math.pow(2, 3) + comps[i + 1] * (int)Math.pow(2, 2) + comps[i + 2]
                    * (int)Math.pow(2, 1) + comps[i + 3];
            hashCode.append(Convert.binaryToHex(result));
        }

        // 得到指纹以后，就可以对比不同的图片，看看64位中有多少位是不一样的。
        return hashCode.toString();
    }

    public static Bitmap getImageFromAssetsFile(Context context, String fileName) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            MyLog.e(sTag, "Graphics.getImageFromAssetsFile:" + e.toString());
        }
        return image;
    }

    public static Bitmap getImageFromSDCard(String fileName) {
        try {
            File img = new File(fileName);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inDither = false;
            options.inSampleSize = 1;
            return BitmapFactory.decodeFile(img.getAbsolutePath(), options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public static void addOverdueView(Context context, FrameLayout frame, Rect rect, String fileName) {
        try {
            ImageView iImageView = new ImageView(context);
            iImageView.setScaleType(ScaleType.FIT_XY);
            iImageView.setImageBitmap(Graphics.getImageFromAssetsFile(context, fileName));
            frame.addView(iImageView, ClassObject.getParams(rect));
        } catch (Exception e) {
            MyLog.e(sTag, "Graphics.addOverdueView:" + e.toString());
        }

    }
}
