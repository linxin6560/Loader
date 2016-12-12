package com.levylin.lib.net;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.levylin.lib.net.exception.HttpException;

import java.io.IOException;
import java.lang.reflect.Type;

import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Cancellable;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Response;

/**
 * 网络请求OnSubscribe
 * Created by LinXin on 2016/4/12 18:46.
 */
class RequestOnSubscribe<T> implements FlowableOnSubscribe<T> {

    //请求Call
    private Call<T> call;
    //数据类型
    private Type type;
    //缓存key
    private String cacheKey;
    //文件缓存
    private FileCache mFileCache;
    //是否需要保存缓存
    private boolean isNeedSaveCache;
    //加载配置
    private CacheStrategy cacheStrategy;

    RequestOnSubscribe(Call<T> call, Type type, CacheStrategy strategy) {
        Request request = call.request();
        HttpUrl httpUrl = request.url();
        StringBuilder sb = new StringBuilder();
        String url = httpUrl.toString();
        sb.append(url);
        if (request.method().equals("POST")) {
            String params = bodyToString(request);
            if (!TextUtils.isEmpty(params)) {
                sb.append(url.contains("?") ? "&" : "?");
                sb.append(params);
            }
        }
        url = sb.toString();
        this.cacheStrategy = strategy;
        this.call = call;
        this.type = type;
        this.mFileCache = FileCache.getInstance();
        this.cacheKey = String.valueOf(url.hashCode());
    }

    @Override
    public void subscribe(FlowableEmitter<T> emitter) throws Exception {
        Request request = call.request();
        if (request.method().equalsIgnoreCase("POST")) {//POST走不读缓存,不存缓存
            cacheStrategy.setIsReadCache(false);
            cacheStrategy.setIsSaveCache(false);
        }
        emitter.setCancellable(new Cancellable() {
            @Override
            public void cancel() throws Exception {
                if (!call.isCanceled()) {
                    call.cancel();
                }
            }
        });
        isNeedSaveCache = ensureIsNeedSaveCache(request);
        if (!cacheStrategy.isReadCache()) {
            requestFromNet(emitter);
            cacheStrategy.reset();//重置状态
            onComplete(emitter);
            return;
        }
        switch (cacheStrategy.getCacheType()) {
            case NO_CACHE:
                requestFromNet(emitter);
                break;
            case READ_CACHE_ONLY_NOT_EXPIRES:
                requestReadCacheFirst(emitter);
                break;
            case READ_CACHE_UPDATE_UI_THEN_NET_WHEN_EXPIRES:
                requestReadCacheFirstAndNet(emitter);
                break;
            case READ_CACHE_UPDATE_UI_THEN_NET:
                requestReadCacheAndNet(emitter);
                break;
        }
        cacheStrategy.reset();//重置状态
        onComplete(emitter);
    }

    /**
     * 确保是否需要保存缓存
     *
     * @return true:要保存,false:不保存
     */
    private boolean ensureIsNeedSaveCache(Request request) {
        return !request.method().equalsIgnoreCase("POST") //Post类型不保存缓存
                && cacheStrategy.isSaveCache();//缓存配置中的是否需要保存
    }

    /**
     * 先读取缓存，若缓存过期则读取网络，最后刷新UI
     *
     * @param emitter the emitter
     */
    private void requestReadCacheFirst(FlowableEmitter<? super T> emitter) {
        String result = mFileCache.getCacheData(cacheKey);
        if (!mFileCache.isTimeOut(cacheKey, cacheStrategy.getCacheTimeOut()) && !TextUtils.isEmpty(result)) {
            readFromCache(result, emitter);
        } else {
            //过期则重新从网络读取数据
            requestFromNet(emitter);
        }
    }

    /**
     * 先读取缓存并刷新界面，若缓存过期，则读取网络并刷新界面
     *
     * @param emitter the subscriber
     */
    private void requestReadCacheFirstAndNet(FlowableEmitter<? super T> emitter) {
        String result = mFileCache.getCacheData(cacheKey);
        if (!TextUtils.isEmpty(result)) {
            readFromCache(result, emitter);
        }
        if (mFileCache.isTimeOut(cacheKey, cacheStrategy.getCacheTimeOut())) {
            //过期则重新从网络读取数据
            requestFromNet(emitter);
        }
    }

    /**
     * 先读取缓存并刷新界面，再读取网络并刷新界面
     *
     * @param emitter the subscriber
     */
    private void requestReadCacheAndNet(FlowableEmitter<? super T> emitter) {
        String result = mFileCache.getCacheData(cacheKey);
        if (!TextUtils.isEmpty(result)) {
            readFromCache(result, emitter);
        }
        requestFromNet(emitter);
    }

    /**
     * 读取网络
     *
     * @param emitter the subscriber
     */
    private void requestFromNet(FlowableEmitter<? super T> emitter) {
        try {
            if (call.isExecuted()) {
                call = call.clone();
            }
            Response<T> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                T t = response.body();
                if (isNeedSaveCache && LoadUtils.isCanCache(t)) {
                    mFileCache.saveCacheFileData(cacheKey, JSON.toJSONString(t));
                }
                onNext(emitter, t);
                return;
            }
            onError(emitter, new HttpException(response));
        } catch (IOException e) {
            e.printStackTrace();
            onError(emitter, e);
        }
    }

    /**
     * 解析并根据缓存类型缓存数据
     *
     * @param s 数据内容
     */
    private void readFromCache(String s, FlowableEmitter<? super T> emitter) {
        if (TextUtils.isEmpty(s))
            return;
        String data = JSONObject.parseObject(s).getString("d");
        T t = JSON.parseObject(data, type);
        onNext(emitter, t);
    }

    /**
     * 判断请求是否被取消
     */
    private void onNext(FlowableEmitter<? super T> emitter, T response) {
        if (emitter.isCancelled())
            return;
        emitter.onNext(response);
    }

    /**
     * 判断请求是否被取消
     */
    private void onError(FlowableEmitter<? super T> emitter, Throwable throwable) {
        if (emitter.isCancelled())
            return;
        emitter.onError(throwable);
    }

    /**
     * 判断请求是否被取消
     */
    private void onComplete(FlowableEmitter<? super T> emitter) {
        if (emitter.isCancelled())
            return;
        emitter.onComplete();
    }

    /**
     * 网络请求post参数转为string
     *
     * @param request 请求
     * @return 参数string
     */
    private String bodyToString(final Request request) {
        try {
            Request copy = request.newBuilder().build();
            Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final Exception e) {
            return null;
        }
    }
}
