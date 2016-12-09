package com.levylin.lib.net.cache;

/**
 * 校验器,某些数据要在服务器端返回结果是正确的情况下才能保存，可以使用该接口，在LoadUtils中，添加Checker即可
 * Created by LinXin on 2016/12/7 11:00.
 */
public interface ICacheChecker {

    //校验是否是某个类
    boolean isInstanceOf(Object o);

    //是否可以保存缓存
    boolean isCanCache(Object o);
}
