package com.andbase.library.cache;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.SystemClock;

import com.andbase.library.app.global.AbAppConfig;
import com.andbase.library.utils.AbAppUtil;
import com.andbase.library.utils.AbDateUtil;
import com.andbase.library.utils.AbFileUtil;
import com.andbase.library.utils.AbLogUtil;
import com.andbase.library.utils.AbStrUtil;
import com.andbase.library.utils.AbStreamUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 磁盘缓存实现类
 */

public class AbDiskCache  {

    /**  单例. */
    private static AbDiskCache diskCache;

    /**  所有缓存文件. */
    private final Map<String, CacheHeader> mEntries = new LinkedHashMap<String, CacheHeader>(16, .75f, true);

    /** 当前缓存大小. */
    private long mTotalSize = 0;

    /** 缓存根目录. */
    private File cacheDir;

    /** 最大缓存字节数. */
    private final int mMaxCacheSizeInBytes;

    /**  缓存达到高水品的百分比. */
    private static final float HYSTERESIS_FACTOR = 0.9f;

    /** 文件头标识. */
    private static final int CACHE_MAGIC = 0x20120504;

    /**
     * 构造.
     */
    public AbDiskCache(Context context) {

        PackageInfo info = AbAppUtil.getPackageInfo(context);
        if(!AbFileUtil.isCanUseSD()){
            cacheDir = new File(context.getCacheDir(), info.packageName);
        }else{
            cacheDir = new File(AbFileUtil.getCacheDownloadDir(context));
        }
        mMaxCacheSizeInBytes =  AbAppConfig.MAX_DISK_USAGE_INBYTES;
        initialize();
    }

    /**
     *
     * 获得一个实例.
     * @param context
     * @return
     */
    public static AbDiskCache getInstance(Context context) {
        if(diskCache == null){
            diskCache = new AbDiskCache(context);
        }
        return diskCache;
    }

    /**
     * 初始化磁盘缓存文件.
     */
    public synchronized void initialize() {
        if (!cacheDir.exists()) {
            if (!cacheDir.mkdirs()) {
                AbLogUtil.e(AbDiskCache.class,"缓存目录创建失败，"+cacheDir.getAbsolutePath());
            }
            return;
        }

        File[] files = cacheDir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                CacheHeader entry = CacheHeader.readHeader(fis);
                entry.size = file.length();
                putEntry(entry.key, entry);
            } catch (Exception e) {
                if (file != null) {
                   file.delete();
                }
            } finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                } catch (Exception e) { 
                }
            }
        }
    }

    /**
     * 清空所有磁盘缓存.
     */
    public synchronized void clear() {
        File[] files = cacheDir.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
        mEntries.clear();
        mTotalSize = 0;
        AbLogUtil.d(AbDiskCache.class,"Cache cleared.");
    }

    /**
     * 获取缓存实体.
     *
     * @param key the key
     * @return the entry
     */
    public synchronized AbCacheFile get(String key) {
        CacheHeader entry = mEntries.get(key);
        // if the entry does not exist, return.
        if (entry == null) {
            return null;
        }

        File file = getFileForKey(key);
        AbLogUtil.d(AbDiskCache.class, "想要从缓存中获取文件"+file.getAbsolutePath());
        CountingInputStream cis = null;
        try {
            cis = new CountingInputStream(new FileInputStream(file));
            CacheHeader.readHeader(cis); // eat header
            byte[] data = AbStreamUtil.stream2Bytes(cis, (int) (file.length() - cis.bytesRead));
            return entry.toCacheEntry(data);
        } catch (Exception e) {
        	e.printStackTrace();
            remove(key);
            return null;
        } finally {
            if (cis != null) {
                try {
                    cis.close();
                } catch (Exception ioe) {
                	ioe.printStackTrace();
                    return null;
                }
            }
        }
    }

    /**
     * 添加实体到缓存.
     *
     * @param key the key
     * @param entry the entry
     */
    public synchronized void put(String key, AbCacheFile entry) {
        pruneIfNeeded(entry.data.length);
        File file = getFileForKey(key);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            CacheHeader e = new CacheHeader(key, entry);
            e.writeHeader(fos);
            fos.write(entry.data);
            fos.close();
            putEntry(key, e);
            return;
        } catch (IOException e) {
        }
        boolean deleted = file.delete();
        if (!deleted) {
            AbLogUtil.d(AbDiskCache.class,"缓存文件删除失败"+file.getAbsolutePath());
        }
    }

    /**
     * 从缓存中移除实体.
     * @param key the key
     */
    public synchronized void remove(String key) {
        boolean deleted = getFileForKey(key).delete();
        removeEntry(key);
        if (!deleted) {
            AbLogUtil.d(AbDiskCache.class,"缓存文件删除失败");
        }
    }

    /**
     * 从key中生成文件名.
     * @param key The key to generate a file name for.
     * @return A pseudo-unique filename.
     */
    private String getFileNameForKey(String key) {
        int firstHalfLength = key.length() / 2;
        String localFilename = String.valueOf(key.substring(0, firstHalfLength).hashCode());
        localFilename += String.valueOf(key.substring(firstHalfLength).hashCode());
        return localFilename;
    }

    /**
     * 从key中得到文件.
     *
     * @param key the key
     * @return the file for key
     */
    public File getFileForKey(String key) {
        return new File(cacheDir, getFileNameForKey(key));
    }

    /**
     * Prunes the cache to fit the amount of bytes specified.
     * @param neededSpace The amount of bytes we are trying to fit into the cache.
     */
    private void pruneIfNeeded(int neededSpace) {
    	//可以缓存
        if ((mTotalSize + neededSpace) < mMaxCacheSizeInBytes) {
            return;
        }

        //释放部分空间
        long before = mTotalSize;
        int prunedFiles = 0;
        long startTime = SystemClock.elapsedRealtime();

        Iterator<Map.Entry<String, CacheHeader>> iterator = mEntries.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, CacheHeader> entry = iterator.next();
            CacheHeader e = entry.getValue();
            //删除
            boolean deleted = getFileForKey(e.key).delete();
            if (deleted) {
                mTotalSize -= e.size;
            } else {
               AbLogUtil.d(AbDiskCache.class,"Could not delete cache entry for key=%s, filename=%s",
                       e.key, getFileNameForKey(e.key));
            }
            iterator.remove();
            prunedFiles++;
            
            //删除缓存到这个级别
            if ((mTotalSize + neededSpace) < mMaxCacheSizeInBytes * HYSTERESIS_FACTOR) {
                break;
            }
        }

        if (AbLogUtil.D) {
        	AbLogUtil.d(AbDiskCache.class,"pruned %d files, %d bytes, %d ms",
                    prunedFiles, (mTotalSize - before), SystemClock.elapsedRealtime() - startTime);
        }
    }

    /**
     * 将实体加入到缓存中.
     * @param key The key to identify the entry by.
     * @param entry The entry to cache.
     */
    private void putEntry(String key, CacheHeader entry) {
        if (!mEntries.containsKey(key)) {
            mTotalSize += entry.size;
        } else {
            CacheHeader oldEntry = mEntries.get(key);
            mTotalSize += (entry.size - oldEntry.size);
        }
        mEntries.put(key, entry);
    }

    /**
     * 从缓存中移除某个实体.
     *
     * @param key the key
     */
    private void removeEntry(String key) {
        CacheHeader entry = mEntries.get(key);
        if (entry != null) {
            mTotalSize -= entry.size;
            mEntries.remove(key);
        }
    }

    /**
     * 获取用于缓存的Key.
     * @param url
     * @return
     */
    public String getCacheKey(String url) {
        return new StringBuilder(url.length()).append(url).toString();
    }

    /**
     * 缓存头部信息.
     */
    static class CacheHeader {
    	
        /** 内容大小 */
        public long size;

        /** 实体的key. */
        public String key;

        /** ETag仅仅是一个和文件相关的标记. */
        public String etag;

        /** 缓存时间 总毫秒数. */
        public long serverTimeMillis;

        /** 失效日期 总毫秒数. */
        public long expiredTimeMillis;

        /** 响应头信息. */
        public Map<String, String> responseHeaders;

        /**
         * 构造.
         */
        private CacheHeader() { }

        /**
         * 构造.
         *
         * @param key The key that identifies the cache entry
         * @param entry The cache entry.
         */
        public CacheHeader(String key, AbCacheFile entry) {
            this.key = key;
            this.size = entry.data.length;
            this.etag = entry.etag;
            this.serverTimeMillis = entry.serverTimeMillis;
            this.expiredTimeMillis = entry.expiredTimeMillis;
            this.responseHeaders = entry.responseHeaders;
        }

        /**
         * Reads the header off of an InputStream and returns a CacheHeader object.
         *
         * @param is The InputStream to read from.
         * @return the cache header
         * @throws IOException Signals that an I/O exception has occurred.
         */
        public static CacheHeader readHeader(InputStream is) throws Exception {
            CacheHeader entry = new CacheHeader();
            int magic = AbStreamUtil.readInt(is);
            if (magic != CACHE_MAGIC) {
                // don't bother deleting, it'll get pruned eventually
                throw new IOException();
            }
            entry.key = AbStreamUtil.readString(is);
            entry.etag = AbStreamUtil.readString(is);
            if (entry.etag.equals("")) {
                entry.etag = null;
            }
            entry.serverTimeMillis = AbStreamUtil.readLong(is);
            entry.expiredTimeMillis = AbStreamUtil.readLong(is);
            entry.responseHeaders = AbStreamUtil.readStringStringMap(is);
            return entry;
        }

        /**
         * Creates a cache entry for the specified data.
         *
         * @param data the data
         * @return the entry
         */
        public AbCacheFile toCacheEntry(byte[] data) {
            AbCacheFile e = new AbCacheFile();
            e.data = data;
            e.etag = etag;
            e.serverTimeMillis = serverTimeMillis;
            e.expiredTimeMillis = expiredTimeMillis;
            e.responseHeaders = responseHeaders;
            return e;
        }


        /**
         * Writes the contents of this CacheHeader to the specified OutputStream.
         *
         * @param os the os
         * @return true, if successful
         */
        public boolean writeHeader(OutputStream os) {
            try {
            	AbStreamUtil.writeInt(os, CACHE_MAGIC);
            	AbStreamUtil.writeString(os, key);
            	AbStreamUtil.writeString(os, etag == null ? "" : etag);
            	AbStreamUtil.writeLong(os, serverTimeMillis);
            	AbStreamUtil.writeLong(os, expiredTimeMillis);
            	AbStreamUtil.writeStringStringMap(responseHeaders, os);
                os.flush();
                return true;
            } catch (IOException e) {
                AbLogUtil.d(AbDiskCache.class,"%s", e.toString());
                return false;
            }
        }

    }

    /**
     * 从连接中获取响应信息.
     * @param url the url
     * @return the response
     */
    public AbCacheHttpResponse getHttpResponse(String url, String sessionId){
        URLConnection con = null;
        InputStream is = null;
        AbCacheHttpResponse response = null;
        try {
            URL imageURL = new URL(url);
            con = imageURL.openConnection();
            con.setConnectTimeout(AbAppConfig.DEFAULT_CONNECT_TIMEOUT);
            con.setReadTimeout(AbAppConfig.DEFAULT_READ_TIMEOUT);
            con.setDoInput(true);

            con.setRequestProperty("SecurityCode",AbAppConfig.httpSecurityCode);
            if(!AbStrUtil.isEmpty(sessionId)){
                con.setRequestProperty("Cookie", "JSESSIONID="+sessionId);
            }

            con.connect();
            is = con.getInputStream();

            byte [] data = AbStreamUtil.stream2Bytes(is);
            Map<String, List<String>> headers = con.getHeaderFields();
            Map<String,String> mapHeaders = new HashMap<String,String>();
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {

                String key = entry.getKey();
                List<String> values = entry.getValue();
                if(AbStrUtil.isEmpty(key)){
                    key = "andbase";
                }
                mapHeaders.put(key, values.get(0));

            }

			/*key = null and value = [HTTP/1.1 200 OK]
		    key = Accept-Ranges and value = [bytes]
			key = Connection and value = [Keep-Alive]
			key = Content-Length and value = [4357]
			key = Content-Type and value = [image/png]
			key = Date and value = [Thu, 02 Apr 2015 10:42:54 GMT]
			key = ETag and value = ["620e07-1105-4f5d6331a2300"]
			key = Keep-Alive and value = [timeout=15, max=97]
			key = Last-Modified and value = [Sun, 30 Mar 2014 17:23:56 GMT]
			key = Server and value = [Apache]
			key = X-Android-Received-Millis and value = [1427971373392]
			key = X-Android-Sent-Millis and value = [1427971373356]*/

            response = new AbCacheHttpResponse(data, mapHeaders);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    /**
     *
     * 将响应解析成缓存实体.
     * @param response
     * @return
     */
    public AbCacheFile parseCacheHeaders(AbCacheHttpResponse response, long cacheTimeMillis) {

        //{andbase=HTTP/1.1 200 OK, ETag="620e0d-dae-4f5d6331a2300", Date=Fri, 26 Jun 2015 02:17:54 GMT,
        //Content-Length=3502, Last-Modified=Sun, 30 Mar 2014 17:23:56 GMT, X-Android-Received-Millis=1435285072907,
        //Keep-Alive=timeout=15, max=100, Content-Type=image/png, Connection=Keep-Alive, Accept-Ranges=bytes,
        //Server=Apache, Cache-Control=max-age=600000, X-Android-Sent-Millis=1435285072809}
        Map<String, String> headers = response.headers;
        long serverTimeMillis = 0;
        long expiredTimeMillis = 0;
        long maxAge = 0;
        boolean hasCacheControl = false;
        String serverEtag = null;
        String headerValue;

        //获取响应的内容的时间
        headerValue = headers.get("Date");
        if (headerValue != null) {
            try {
                serverTimeMillis = AbDateUtil.getDateByFormat(headerValue,AbDateUtil.PATTERN_RFC1123, Locale.ENGLISH).getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //服务器的时间可能小于当前时间，会导致始终过期
        if(serverTimeMillis == 0  || serverTimeMillis < System.currentTimeMillis()){
            serverTimeMillis = System.currentTimeMillis();
        }

        //Cache-Control有值才使用缓存超时的设置
        headerValue = headers.get("Cache-Control");
        if (headerValue != null) {
            hasCacheControl = true;
            String[] tokens = headerValue.split(",");
            for (int i = 0; i < tokens.length; i++) {
                String token = tokens[i].trim();
                if (token.equals("no-cache") || token.equals("no-store")) {
                    break;
                } else if (token.startsWith("max-age=")) {
                    try {
                        maxAge = Long.parseLong(token.substring(8));
                    } catch (Exception e) {
                    }
                } else if (token.equals("must-revalidate") || token.equals("proxy-revalidate")) {
                    maxAge = 0;
                }
            }
        }

        //服务端未设置Header缓存，才使用app的设置
        if(maxAge==0 && cacheTimeMillis > 0){
            hasCacheControl = true;
            maxAge = cacheTimeMillis;
        }

        serverEtag = headers.get("ETag");

        if (hasCacheControl) {
            expiredTimeMillis = serverTimeMillis + maxAge;
        }

        AbCacheFile entry = new AbCacheFile();
        entry.data = response.data;
        entry.etag = serverEtag;
        entry.serverTimeMillis = serverTimeMillis;
        entry.expiredTimeMillis = expiredTimeMillis;
        entry.responseHeaders = headers;

        return entry;

    }

    /**
     * 计数.
     */
    private static class CountingInputStream extends FilterInputStream {
        
        /** The bytes read. */
        private int bytesRead = 0;

        private CountingInputStream(InputStream in) {
            super(in);
        }
        
        @Override
        public int read() throws IOException {
            int result = super.read();
            if (result != -1) {
                bytesRead++;
            }
            return result;
        }
        
        @Override
        public int read(byte[] buffer, int offset, int count) throws IOException {
            int result = super.read(buffer, offset, count);
            if (result != -1) {
                bytesRead += result;
            }
            return result;
        }
    }

}
