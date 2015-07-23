package ru.greatbit.metrostore.beans;

/**
 * Created by azee on 24.07.15.
 */
public enum ResponseCode {
    MAIN_ACTIVITY(1),
    LIST_ACTIVITY(2)
    ;

    private final int value;

    ResponseCode(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static ResponseCode fromValue(int v) {
        for (ResponseCode c: ResponseCode.values()) {
            if (c.value == v) {
                return c;
            }
        }
        throw new IllegalArgumentException(Integer.toString(v));
    }
}
