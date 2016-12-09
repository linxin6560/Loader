package com.levylin.lib.net.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 类的工具类
 * Created by LinXin on 2016/8/2 15:26.
 */
public class ClassUtils {

    /**
     * 获取类的泛型
     *
     * @param clazz 类名
     * @param index 泛型位置
     * @return 泛型类型
     */
    public static Type getSuperClassGenricType(final Class clazz, final int index) {
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
