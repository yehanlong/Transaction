package com.transaction.core.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
public class ResponseBean<T> implements Serializable {

    private static final long serialVersionUID = -5144198657132248467L;

    @Builder.Default
    public static final int SUCCESS = 200;

    @Builder.Default
    public static final int FAIL = 500;

    @Builder.Default
    private int responseCode = SUCCESS;

    @Builder.Default
    private String message = "success";

    private T data;

    public ResponseBean(T data) {
        responseCode = SUCCESS;
        message = "success";
        this.data = data;
    }

    public ResponseBean(int responseCode, String message, T data) {
        this.responseCode = responseCode;
        this.message = message;
        this.data = data;
    }

    public ResponseBean(Throwable e) {
        this.responseCode = FAIL;
        this.message = e.getMessage();
    }


    public static <T> ResponseBean<T> ok() {
        return ok("请求成功!", null);
    }

    public static <T> ResponseBean<T> ok(T t) {
        return ok("请求成功!", t);
    }

    public static <T> ResponseBean<T> ok(String msg) {
        return ok(msg, null);
    }

    public static <T> ResponseBean<T> ok(String msg, T t) {
        return new ResponseBean<>(SUCCESS, msg, t);
    }

    public static <T> ResponseBean<T> fail(String msg, T t) {
        return new ResponseBean<>(FAIL, msg, t);
    }

    public static <T> ResponseBean<T> fail(String msg) {
        return fail(msg, null);
    }

    public static ResponseBean<Throwable> fail(Throwable e) {
        return fail("请求失败!", e);
    }
}
