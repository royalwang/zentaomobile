package com.cnezsoft.zentao;

/**
 * Operate bundle with an value attribute
 * Created by Catouse on 2015/1/13.
 */
public class OperateBundle<T, V> extends OperateResult<T> {
    private V value;

    /**
     * Value getter
     * @return
     */
    public V getValue() {
        return value;
    }

    /**
     * Constructor with result and message
     * @param result
     * @param message
     */
    public OperateBundle(T result, String message) {
        super(result, message);
    }

    /**
     * Constructor with result
     * @param result
     */
    public OperateBundle(T result) {
        super(result);
    }

    /**
     * Constructor with result, message and value
     * @param result
     * @param message
     * @param value
     */
    public OperateBundle(T result, String message, V value) {
        super(result, message);
        this.value = value;
    }

    /**
     * Constructor with result and value
     * @param result
     * @param value
     */
    public OperateBundle(T result, V value) {
        super(result);
        this.value = value;
    }
}
