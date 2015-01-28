package com.cnezsoft.zentao;

/**
 * Operate result with result and message
 * Created by Catouse on 2015/1/13.
 */
public class OperateResult<T> {

    private T result;
    private String message;
    private int code;

    protected void setResult(T result) {
        this.result = result;
    }

    /**
     * Code getter
     * @return
     */
    public int getCode() {
        return code;
    }

    /**
     * Code setter
     * @param code
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * Result getter
     * @return
     */
    public T getResult() {
        return result;
    }

    /**
     * Message getter
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * Message setter
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Constructor with result and message
     * @param result
     * @param message
     */
    public OperateResult(T result, String message) {
        this.result = result;
        this.message = message;
    }

    /**
     * Constructor with result
     * @param result
     */
    public OperateResult(T result) {
        this.result = result;
    }

    /**
     * Convert to an "OperateBundle" object with a value
     * @param value
     * @param <V>
     * @return
     */
    public <V> OperateBundle<T, V> toOperateBundle(V value)
    {
        OperateBundle<T,V> operateBundle = new OperateBundle<>(this.result, this.message, value);
        operateBundle.setCode(code);
        return operateBundle;
    }

    @Override
    public String toString() {
        return "OperateResult: {result=" + result + ", message=" + message + ", code=" + code + "}";
    }
}
