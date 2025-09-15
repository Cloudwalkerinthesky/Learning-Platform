package com.phantom.common.bean.vo;

import com.phantom.common.constant.Code;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果封装类
 * @param <T> 数据类型
 */
@Data
public class R<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** 响应状态码 */
    private int code;
    
    /** 响应消息 */
    private String msg;
    
    /** 响应数据 */
    private T data;
    
    /** 时间戳 */
    private long timestamp;
    
    public R() {
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * 成功响应（无数据）
     */
    public static <T> R<T> ok() {
        return restResult(null, Code.SUCCESS, "操作成功");
    }
    
    /**
     * 成功响应（带数据）
     */
    public static <T> R<T> ok(T data) {
        return restResult(data, Code.SUCCESS, "操作成功");
    }
    
    /**
     * 成功响应（带数据和消息）
     */
    public static <T> R<T> ok(T data, String msg) {
        return restResult(data, Code.SUCCESS, msg);
    }
    
    /**
     * 成功响应（带消息）
     */
    public static <T> R<T> ok(String msg) {
        return restResult(null, Code.SUCCESS, msg);
    }
    
    /**
     * 失败响应（无数据）
     */
    public static <T> R<T> failed() {
        return restResult(null, Code.FAILURE, "操作失败");
    }
    
    /**
     * 失败响应（带消息）
     */
    public static <T> R<T> failed(String msg) {
        return restResult(null, Code.FAILURE, msg);
    }
    
    /**
     * 失败响应（带数据）
     */
    public static <T> R<T> failed(T data) {
        return restResult(data, Code.FAILURE, "操作失败");
    }
    
    /**
     * 失败响应（带数据和消息）
     */
    public static <T> R<T> failed(T data, String msg) {
        return restResult(data, Code.FAILURE, msg);
    }
    
    /**
     * 自定义响应（带数据、状态码和消息）
     */
    public static <T> R<T> failed(T data, int code, String msg) {
        return restResult(data, code, msg);
    }
    
    /**
     * 根据条件返回成功或失败
     */
    public static <T> R<T> result(boolean success, T data, String msg) {
        return success ? ok(data, msg) : failed(data, msg);
    }
    
    /**
     * 根据条件返回成功或失败（无数据）
     */
    public static <T> R<T> result(boolean success, String msg) {
        return success ? ok(msg) : failed(msg);
    }
    
    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return this.code == Code.SUCCESS;
    }
    
    /**
     * 判断是否失败
     */
    public boolean isFailed() {
        return !isSuccess();
    }
    
    /**
     * 构建响应结果
     */
    private static <T> R<T> restResult(T data, int code, String msg) {
        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }
}