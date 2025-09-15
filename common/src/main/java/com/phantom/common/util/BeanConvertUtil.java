package com.phantom.common.util;

import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Bean转换工具类
 * 用于各种数据对象之间的转换（DTO、VO、PO等）
 */
public class BeanConvertUtil {

    /**
     * 单个对象转换
     * @param source 源对象
     * @param targetClass 目标类型
     * @param <T> 目标类型泛型
     * @return 转换后的对象
     */
    public static <T> T convert(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("Bean转换失败: " + e.getMessage(), e);
        }
    }

    /**
     * 单个对象转换（使用Supplier创建目标对象）
     * @param source 源对象
     * @param targetSupplier 目标对象创建器
     * @param <T> 目标类型泛型
     * @return 转换后的对象
     */
    public static <T> T convert(Object source, Supplier<T> targetSupplier) {
        if (source == null) {
            return null;
        }
        T target = targetSupplier.get();
        BeanUtils.copyProperties(source, target);
        return target;
    }

    /**
     * 列表转换
     * @param sourceList 源对象列表
     * @param targetClass 目标类型
     * @param <S> 源类型泛型
     * @param <T> 目标类型泛型
     * @return 转换后的对象列表
     */
    public static <S, T> List<T> convertList(List<S> sourceList, Class<T> targetClass) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return Collections.emptyList();
        }
        List<T> targetList = new ArrayList<>(sourceList.size());
        for (S source : sourceList) {
            targetList.add(convert(source, targetClass));
        }
        return targetList;
    }

    /**
     * 列表转换（使用Supplier创建目标对象）
     * @param sourceList 源对象列表
     * @param targetSupplier 目标对象创建器
     * @param <S> 源类型泛型
     * @param <T> 目标类型泛型
     * @return 转换后的对象列表
     */
    public static <S, T> List<T> convertList(List<S> sourceList, Supplier<T> targetSupplier) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return Collections.emptyList();
        }
        List<T> targetList = new ArrayList<>(sourceList.size());
        for (S source : sourceList) {
            targetList.add(convert(source, targetSupplier));
        }
        return targetList;
    }

    /**
     * 对象转换并设置额外属性
     * @param source 源对象
     * @param targetClass 目标类型
     * @param extraSetter 额外属性设置器
     * @param <T> 目标类型泛型
     * @return 转换后的对象
     */
    public static <T> T convertWithExtra(Object source, Class<T> targetClass, 
                                        java.util.function.Consumer<T> extraSetter) {
        T target = convert(source, targetClass);
        if (target != null && extraSetter != null) {
            extraSetter.accept(target);
        }
        return target;
    }

    /**
     * 安全的属性拷贝，忽略指定属性
     * @param source 源对象
     * @param target 目标对象
     * @param ignoreProperties 忽略的属性名数组
     */
    public static void copyPropertiesIgnoring(Object source, Object target, String... ignoreProperties) {
        if (source == null || target == null) {
            return;
        }
        BeanUtils.copyProperties(source, target, ignoreProperties);
    }

    /**
     * 检查两个对象的指定属性是否相等
     * @param obj1 对象1
     * @param obj2 对象2
     * @param propertyName 属性名
     * @return 是否相等
     */
    public static boolean isPropertyEqual(Object obj1, Object obj2, String propertyName) {
        if (obj1 == null && obj2 == null) {
            return true;
        }
        if (obj1 == null || obj2 == null) {
            return false;
        }
        try {
            java.lang.reflect.Field field1 = obj1.getClass().getDeclaredField(propertyName);
            java.lang.reflect.Field field2 = obj2.getClass().getDeclaredField(propertyName);
            
            field1.setAccessible(true);
            field2.setAccessible(true);
            
            Object value1 = field1.get(obj1);
            Object value2 = field2.get(obj2);
            
            if (value1 == null && value2 == null) {
                return true;
            }
            if (value1 == null || value2 == null) {
                return false;
            }
            return value1.equals(value2);
        } catch (Exception e) {
            return false;
        }
    }
} 