package com.levylin.lib.net;

import com.levylin.lib.net.cache.ICacheChecker;
import com.levylin.lib.net.converter.FastJsonConverterFactory;
import com.levylin.lib.net.listener.OnLoadListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.ResourceSubscriber;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 数据加载器
 * Created by LinXin on 2016/4/6 15:09.
 */
public class LoadUtils {

    private static final List<ICacheChecker> CHECKER_LIST = new ArrayList<>();

    /**
     * 初始化缓存，必须要执行，否则会造成崩溃
     * 可在Application中初始化
     *
     * @param cacheDir 缓存目录
     * @param size     缓存大小
     */
    public static void initCache(String cacheDir, int size) {
        FileCache cache = FileCache.getInstance();
        cache.init(cacheDir, size);
    }

    /**
     * //同步缓存日志文件
     */
    public static void flushCache() {
        FileCache.getInstance().flushCache();
    }

    /**
     * 生成接口类
     *
     * @param baseUrl Retrofit必须要设置的，但其实并没有什么卵用，因为这边主要是把URL和参数是通过@Url和@FieldMap设置的
     * @param clazz   接口类
     * @param <T>     接口类型
     * @return
     */
    public static <T> T create(String baseUrl, Class<T> clazz) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(15, TimeUnit.SECONDS);
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(loggingInterceptor);
        }
        OkHttpClient client = builder.build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(FastJsonConverterFactory.create())
                .build();
        return retrofit.create(clazz);
    }

    /**
     * 通过 call直接请求
     *
     * @param call     请求call
     * @param type     类型
     * @param listener 请求监听
     * @param <T>      数据模型类型
     * @return
     */
    public static <T> Disposable load(Call<T> call, Type type, LoadConfig config, final OnLoadListener<T> listener) {
        ResponseSubscriber<T> subscriber = new ResponseSubscriber<>(listener);
        Flowable.create(new RequestOnSubscribe<>(call, type, config), BackpressureStrategy.BUFFER)
                .materialize()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .<T>dematerialize()
                .subscribe(subscriber);
        return subscriber;
    }

    /**
     * 通过 call直接同步请求
     *
     * @param call     请求call
     * @param type     类型
     * @param listener 请求监听
     * @param <T>      数据模型类型
     * @return
     */
    public static <T> ResourceSubscriber loadSync(Call<T> call, Type type, LoadConfig config, OnLoadListener<T> listener) {
        ResponseSubscriber<T> subscriber = new ResponseSubscriber<>(listener);
        Flowable.create(new RequestOnSubscribe<>(call, type, config), BackpressureStrategy.BUFFER)
                .subscribe(subscriber);
        return subscriber;
    }

    public static void addChecker(ICacheChecker validator) {
        CHECKER_LIST.add(validator);
    }

    /**
     * 是否可以保存缓存,如果同时也是需要缓存，则保存，反之则不保存
     */
    static <T> boolean isCanCache(T t) {
        for (ICacheChecker validator : CHECKER_LIST) {
            if (validator.isInstanceOf(t))
                return validator.isCanCache(t);
        }
        return false;
    }
}
