package com.inroids.irmultimedia;

import android.graphics.Bitmap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ImageMemoryCache {
    public Map<String, Bitmap> cache = Collections.synchronizedMap(new HashMap<String, Bitmap>());

    // public Map<String, Bitmap> cache = new ConcurrentHashMap<String, Bitmap>();

    private static final int HARD_CACHE_CAPACITY = 50;// 最大缓存数

    // 从缓存中获取图片
    public Bitmap get(String id) {
        if (!cache.containsKey(id))
            return null;
        return cache.get(id);
    }

    // 将图片存入缓存
    public void put(String id, Bitmap bitmap) {
        if (!cache.containsKey(id))
            return;
        this.checkHardCache();// 检查缓存是否满
        cache.put(id, bitmap);
    }

    // 检查缓存是否满
    private void checkHardCache() {
        if (cache.size() >= HARD_CACHE_CAPACITY) {
            Set<String> keySet = cache.keySet();
            for (String key : keySet) {
                cache.remove(key);
                break;// 移除最先缓存的图片
            }
        }
    }

    // 清除缓存
    public void clear() {
        cache.clear();
    }
}
