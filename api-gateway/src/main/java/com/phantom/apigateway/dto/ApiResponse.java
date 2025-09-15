package com.phantom.apigateway.dto;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;
    
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage("success");
        response.setData(data);
        return response;
    }
    
    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage(message);
        response.setData(data);
        return response;
    }
    
    public static <T> ApiResponse<T> failed(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(400);
        response.setMessage(message);
        response.setData(null);
        return response;
    }
    
    public static <T> ApiResponse<T> unauthorized(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(401);
        response.setMessage(message);
        response.setData(null);
        return response;
    }
} 