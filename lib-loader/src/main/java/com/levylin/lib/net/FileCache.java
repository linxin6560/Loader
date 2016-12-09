package com.levylin.lib.net;

import com.levylin.lib.net.cache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

class FileCache {

    private DiskLruCache mDiskLruCache;
    private static FileCache mInstance;

    private FileCache() {
    }

    public static FileCache getInstance() {
        if (mInstance == null)
            mInstance = new FileCache();
        return mInstance;
    }

    public void init(String dir, int size) {
        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        try {
            mDiskLruCache = DiskLruCache.open(dirFile, 1, 1, size);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 把数据写入文件缓存
     *
     * @param cacheKey 缓存key
     * @param jsonStr  json数据
     */
    public void saveCacheFileData(String cacheKey, String jsonStr) {
        DiskLruCache.Editor editor = null;
        try {
            editor = mDiskLruCache.edit(cacheKey);
            editor.set(0, jsonStr);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (editor != null) {
                try {
                    editor.abort();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 优先获取缓存数据，如果缓存过期，则返回，不过期则读取缓存信息到brInfo
     *
     * @param cacheKey 缓存key
     */
    public String getCacheData(String cacheKey) {
        try {
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(cacheKey);
            if (snapshot != null) {
                return snapshot.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 验证缓存是否过期
     *
     * @param cacheKey     请求
     * @param cacheSeconds 缓存过期时间
     * @return 是否过期，true:过期或者文件不存在，false:未过期
     */
    public boolean isTimeOut(String cacheKey, int cacheSeconds) {
        final File f = new File(mDiskLruCache.getDirectory(), cacheKey + ".0");
        if (f.exists()) {
            long time = f.lastModified();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            cal.add(Calendar.SECOND, cacheSeconds);
            // 如果缓存过期,则返回空
            boolean isTimeOut = cal.before(Calendar.getInstance());
            if (isTimeOut) {//如果过期则删除
                f.delete();
            }
            return isTimeOut;
        }
        return true;
    }

    /**
     * 将缓存记录同步到journal文件中。
     */
    public void flushCache() {
        try {
            if (mDiskLruCache == null)
                return;
            mDiskLruCache.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
