package com.levylin.lib.net;

/**
 * 缓存的枚举类型
 *
 * @author LinXin
 * @version 1.0.0
 * @date 2016-04-19
 */
public enum CacheType {
    //不读缓存
    NO_CACHE,
    //优先读缓存，判断缓存是否过期，如果过期则去服务器上读数据
    READ_CACHE_ONLY_NOT_EXPIRES,
    //先读缓存，然后把数据返回给UI层，并且判断缓存是否过期，如果过期，则去服务器上读数据并返回给UI层
    READ_CACHE_UPDATE_UI_THEN_NET_WHEN_EXPIRES,
    //先读缓存，然后把数据返回给UI层，然后不管缓存是否过期，都去服务器上读数据并返回给UI层
    READ_CACHE_UPDATE_UI_THEN_NET
}
