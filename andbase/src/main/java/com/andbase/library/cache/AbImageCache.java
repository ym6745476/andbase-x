package com.andbase.library.cache;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.andbase.library.app.global.AbAppConfig;
import com.andbase.library.utils.AbImageUtil;
import com.andbase.library.utils.AbLogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 图片缓存实现类
 */

public class AbImageCache {

    /**  单例. */
    private Context context;

    /**  单例. */
    private static AbImageCache imageCache;

    /**
     * LruCache.
     */
    private static LruCache<String, Bitmap> lruCache;

    /**
     * cache key.
     */
    private List<String> cacheKeyList;

    /**
     * 待释放的bitmap.
     */
    private static List<Bitmap> releaseBitmapList;

    /**
     * 磁盘缓存.
     */
    public AbDiskCache diskCache;

    /**
     * 构造方法.
     */
    public AbImageCache(Context context) {
        super();
        this.context = context;
        int maxSize = AbAppConfig.MAX_CACHE_SIZE_INBYTES;
        releaseBitmapList = new ArrayList<Bitmap>();
        cacheKeyList = new ArrayList<>();
        lruCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                AbLogUtil.e(AbImageCache.class, "entryRemoved key:" + key + " " +oldValue);
                releaseBitmapList.add(oldValue);
                cacheKeyList.remove(key);
                releaseRemovedBitmap();
            }

        };

        this.diskCache = AbDiskCache.getInstance(context);
    }

    /**
     * 构造方法.
     */
    public static AbImageCache getInstance(Context context) {
        if(imageCache == null){
            imageCache = new AbImageCache(context);
        }
        return imageCache;
    }

    /**
     * 根据key获取缓存中的Bitmap.
     * @param key the key
     * @return the bitmap
     */
    public Bitmap getBitmap(String key) {
        return lruCache.get(key);
    }

    /**
     * 增加一个Bitmap到缓存中.
     * @param key the key
     * @param bitmap   the bitmap
     */
    public void putBitmap(String key, Bitmap bitmap) {
        if(bitmap!=null){
            lruCache.put(key, bitmap);
        }
        cacheKeyList.add(key);
    }

    public void putKey(String key) {
        cacheKeyList.add(key);
    }

    public boolean  containKey(String key){
        return  cacheKeyList.contains(key);
    }

    /**
     * 获取用于缓存的Key.
     * @param url       the request url
     * @param desiredWidth  the max width
     * @param desiredHeight the max height
     * @return the cache key
     */
    public String getKey(String url, int desiredWidth, int desiredHeight) {
        return new StringBuilder(url.length() + 12).append("#W").append(desiredWidth)
                .append("#H").append(desiredHeight).append(url).toString();
    }

    /**
     * 获取Bitmap.
     * @param url
     * @param desiredWidth
     * @param desiredHeight
     * @return
     */
    public Bitmap getBitmap(String url, int desiredWidth, int desiredHeight,boolean lruCache) {
        Bitmap bitmap = null;
        try {
            final String cacheKey = getKey(url, desiredWidth, desiredHeight);
            //看磁盘
            AbCacheFile entry = diskCache.get(cacheKey);
            if (entry == null || entry.isExpired()) {
                if (entry == null) {
                    AbLogUtil.i(AbImageCache.class, "磁盘中没有这个图片");
                } else {
                    if (entry.isExpired()) {
                        AbLogUtil.i(AbImageCache.class, "磁盘中图片已经过期");
                    }
                }

                AbCacheHttpResponse response = diskCache.getHttpResponse(url, null);
                if (response != null && response.data != null && response.data.length > 0) {
                    bitmap = AbImageUtil.getBitmap(response.data, desiredWidth, desiredHeight);
                    if (bitmap != null) {
                        putBitmap(cacheKey, bitmap);
                        AbLogUtil.i(AbImageCache.class, "图片缓存成功");
                        diskCache.put(cacheKey, diskCache.parseCacheHeaders(response, AbAppConfig.DISK_CACHE_EXPIRES_TIME));
                    }
                }
            } else {
                //磁盘中有
                byte[] bitmapData = entry.data;
                bitmap = AbImageUtil.getBitmap(bitmapData, desiredWidth, desiredHeight);
                if(lruCache){
                    putBitmap(cacheKey, bitmap);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * 从缓存中删除一个Bitmap.
     * @param key the key
     */
    public void removeBitmap(String key) {
        lruCache.remove(key);
    }

    /**
     * 释放所有缓存.
     */
    public void clearBitmap() {
        lruCache.evictAll();
        cacheKeyList.clear();
    }

    /**
     * 释放已经被移除的Bitmap
     */
    public void releaseRemovedBitmap(){
        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AbImageUtil.releaseBitmapList(releaseBitmapList);
                releaseBitmapList.clear();
            }
        },500);

    }

}
