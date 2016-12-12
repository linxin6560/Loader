package com.levylin.lib.net.loader.model;

import com.levylin.lib.net.CacheType;
import com.levylin.lib.net.LoadConfig;
import com.levylin.lib.net.LoadUtils;
import com.levylin.lib.net.listener.OnLoadListener;
import com.levylin.lib.net.utils.ClassUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.disposables.Disposable;
import retrofit2.Call;

/**
 * 数据模型
 * T:解析数据类型
 * Created by LinXin on 2016/6/21 10:21.
 */
public abstract class Model<T> {

    private Type mType;
    LoadConfig loadConfig;
    private boolean isManualRefresh = false;//手动刷新

    protected Model() {
        mType = getSuperClassGenricType(getClass(), 0);
        loadConfig = new LoadConfig(getCacheType(), getCacheTimeOut());
    }

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
        loadConfig.reset();//先重置状态，防止读取状态在别的地方已修改过
        loadConfig.setIsReadCache(false);
    }

    /**
     * 重新加载前要做的操作,一般用于改变缓存类型
     */
    public void preReLoad() {
        loadConfig.reset();//先重置状态，防止读取状态在别的地方已修改过
        loadConfig.setIsReadCache(false);
    }

    /**
     * 设置缓存类型
     */
    protected CacheType getCacheType() {
        return CacheType.NO_CACHE;
    }

    /**
     * 设置缓存时间
     */
    protected int getCacheTimeOut() {
        return 0;
    }

    /**
     * 加载数据
     *
     * @param listener 加载监听
     */
    public final Disposable load(OnLoadListener<T> listener) {
        return LoadUtils.load(getModelCall(), mType, loadConfig, listener);
    }

    /**
     * 获取数据模型请求
     *
     * @return
     */
    protected abstract Call<T> getModelCall();

    /**
     * 获取类的泛型
     *
     * @param clazz 类名
     * @param index 泛型位置
     * @return 泛型类型
     */
    private static Type getSuperClassGenricType(final Class clazz, final int index) {
        //返回表示此 Class 所表示的实体（类、接口、基本类型或 void）的直接超类的 Type。
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        //返回表示此类型实际类型参数的 Type 对象的数组。
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            return Object.class;
        }
        return params[index];
    }
}
