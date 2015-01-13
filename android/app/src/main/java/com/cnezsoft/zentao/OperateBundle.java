package com.cnezsoft.zentao;

/**
 * Created by Catouse on 2015/1/13.
 */
public class OperateBundle<T, V> extends OperateResult<T> {
    private V value;

    public V getValue() {
        return value;
    }

    public OperateBundle(T result, String message) {
        super(result, message);
    }

    public OperateBundle(T result) {
        super(result);
    }

    public OperateBundle(T result, String message, V value) {
        super(result, message);
        this.value = value;
    }

    public OperateBundle(T result, V value) {
        super(result);
        this.value = value;
    }
}
