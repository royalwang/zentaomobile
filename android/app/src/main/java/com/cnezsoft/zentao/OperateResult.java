package com.cnezsoft.zentao;

/**
 * Created by Catouse on 2015/1/13.
 */
public class OperateResult<T> {

    private T result;
    private String message;
    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public OperateResult(T result, String message) {
        this.result = result;
        this.message = message;
    }

    public OperateResult(T result) {
        this.result = result;
    }

    public <V> OperateBundle<T, V> toOperateBundle(V value)
    {
        return new OperateBundle<>(this.result, this.message, value);
    }
}
