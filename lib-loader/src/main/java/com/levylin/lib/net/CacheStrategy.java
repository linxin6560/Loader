package com.levylin.lib.net;

/**
 * 缓存策略
 * Created by LinXin on 2016/7/25 16:18.
 * edit by LinXin on 2016/8/11 9:30 去掉设置缓存及缓存时间的操作，改为只在构造函数中控制，目的是防止类型被改变，可通过设置加载状态判断是否加载与保存缓存
 */
public class CacheStrategy {
    private static final int STATUS_DEFAULT = -1;//默认状态，该状态下，通过cache type自行判断是否需要读取保和存缓存
    private static final int STATUS_TRUE = 1;//生效状态，不以cache type判断存取，改为以该状态为主
    private static final int STATUS_FALSE = 0;//不生效状态，不以cache type判断存取，改为以改状态为主
    //默认过期时间180秒
    public static final int DEFAULT_CACHE_TIME_OUT = 180;
    //缓存类型
    private CacheType cacheType = CacheType.READ_CACHE_ONLY_NOT_EXPIRES;
    //缓存过期时间
    private int cacheTimeOut = DEFAULT_CACHE_TIME_OUT;
    private int readCacheStatus = STATUS_DEFAULT, saveCacheStatus = STATUS_DEFAULT;

    public CacheStrategy() {
        this(CacheType.READ_CACHE_ONLY_NOT_EXPIRES);
    }

    public CacheStrategy(CacheType cacheType) {
        this(cacheType, DEFAULT_CACHE_TIME_OUT);
    }

    public CacheStrategy(CacheType cacheType, int cacheTimeOut) {
        this.cacheType = cacheType;
        this.cacheTimeOut = cacheTimeOut;
    }

    public CacheType getCacheType() {
        return cacheType;
    }

    public int getCacheTimeOut() {
        return cacheTimeOut;
    }

    /**
     * 设置是否需要读取缓存
     *
     * @param isReadCache 是否需要读取缓存
     */
    public void setIsReadCache(boolean isReadCache) {
        if (isReadCache) {
            readCacheStatus = STATUS_TRUE;
        } else {
            readCacheStatus = STATUS_FALSE;
        }
    }

    /**
     * 是否需要读取缓存
     *
     * @return 是否需要读取缓存
     */
    public boolean isReadCache() {
        if (readCacheStatus == STATUS_DEFAULT)
            return !cacheType.equals(CacheType.NO_CACHE);
        return readCacheStatus == STATUS_TRUE;
    }

    /**
     * 设置是否需要保存缓存
     *
     * @param isSaveCache 是否需要保存缓存
     */
    public void setIsSaveCache(boolean isSaveCache) {
        if (isSaveCache) {
            saveCacheStatus = STATUS_TRUE;
        } else {
            saveCacheStatus = STATUS_FALSE;
        }
    }

    /**
     * 是否需要保存缓存
     *
     * @return 是否需要保存缓存
     */
    public boolean isSaveCache() {
        if (saveCacheStatus == STATUS_DEFAULT)
            return !cacheType.equals(CacheType.NO_CACHE);
        return saveCacheStatus == STATUS_TRUE;
    }

    /**
     * 重置状态
     */
    public void reset() {
        readCacheStatus = STATUS_DEFAULT;
        saveCacheStatus = STATUS_DEFAULT;
    }
}