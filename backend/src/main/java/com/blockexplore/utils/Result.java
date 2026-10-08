package com.blockexplore.utils;

public class Result<T> {
    private int code;        // 状态码
    private String message;   // 提示信息
    private T data;           // 返回的数据

    public Result() {}

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 构建成功的返回结果
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "Success", data);
    }

    // 构建失败的返回结果
    public static <T> Result<T> failure(String message) {
        return new Result<>(500, message, null);
    }

    // 构建自定义状态码和消息的返回结果
    public static <T> Result<T> custom(int code, String message, T data) {
        return new Result<>(code, message, data);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
