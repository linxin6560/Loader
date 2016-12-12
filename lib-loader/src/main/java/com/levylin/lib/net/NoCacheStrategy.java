package com.levylin.lib.net;

import okhttp3.Request;

/**
 * 无缓存的策略
 */
class NoCacheStrategy<T> extends CacheStrategy<T> {

    NoCacheStrategy() {
        super(CacheType.NO_CACHE);
    }

    @Override
    public boolean isTimeOut() {
        return false;
    }

    @Override
    public T readCache(Request request) {
        return null;
    }

    @Override
    public void saveCache(Request request, Object o) {

    }
}