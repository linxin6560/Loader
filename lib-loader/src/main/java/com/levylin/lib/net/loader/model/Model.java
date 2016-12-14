package com.levylin.lib.net.loader.model;

import com.levylin.lib.net.CacheStrategy;
import com.levylin.lib.net.LoadUtils;
import com.levylin.lib.net.listener.OnLoadListener;

import io.reactivex.disposables.Disposable;
import okhttp3.Request;
import retrofit2.Call;

/**
 * 数据模型
 * T:解析数据类型
 * Created by LinXin on 2016/6/21 10:21.
 */
public abstract class Model<T> {

    CacheStrategy<T> cacheStrategy;
    private boolean isManualRefresh = false;//手动刷新

    /**
     * 界面数据是否为空
     *
     * @return
     */
    public abstract boolean isEmpty();

    /**
     * 设置数据
     *
     * @param response
     */
    public abstract void setData(boolean isRefreshing, T response);

    /**
     * 是否手动刷新
     *
     * @return
     */
    public boolean isManualRefresh() {
        return isManualRefresh;
    }

    /**
     * 重置手动刷新为false
     */
    public void setManualRefresh(boolean isManualRefresh) {
        this.isManualRefresh = isManualRefresh;
    }

    /**
     * 刷新前要做的操作,一般用于改变缓存类型
     */
    public void preRefresh() {
        cacheStrategy.reset();//先重置状态，防止读取状态在别的地方已修改过
        cacheStrategy.setIsReadCache(false);
    }

    /**
     * 重新加载前要做的操作,一般用于改变缓存类型
     */
    public void preReLoad() {
        cacheStrategy.reset();//先重置状态，防止读取状态在别的地方已修改过
        cacheStrategy.setIsReadCache(false);
    }

    /**
     * 加载数据
     *
     * @param listener 加载监听
     */
    public final Disposable load(OnLoadListener<T> listener) {
        if (cacheStrategy == null) {
            Call<T> call = getModelCall();
            cacheStrategy = getCacheStrategy(call.request());
        }
        return LoadUtils.load(getModelCall(), cacheStrategy, listener);
    }

    /**
     * 获取数据模型请求
     *
     * @return
     */
    protected abstract Call<T> getModelCall();

    /**
     * 获取缓存策略,默认不需要缓存
     *
     * @return
     */
    protected CacheStrategy<T> getCacheStrategy(Request request) {
        return null;
    }
}
