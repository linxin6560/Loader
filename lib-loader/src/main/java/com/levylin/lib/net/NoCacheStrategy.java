package com.levylin.lib.net;

/**
 * 无缓存的策略
 */
class NoCacheStrategy<T> extends CacheStrategy<T> {

    NoCacheStrategy() {
        super(null, CacheType.NO_CACHE, 0);
    }

    @Override
    public boolean isTimeOut() {
        return false;
    }

    @Override
    public T readCache() {
        return null;
    }

    @Override
    public void saveCache(Object o) {

    }
}