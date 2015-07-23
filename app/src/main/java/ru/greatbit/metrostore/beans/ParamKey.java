package ru.greatbit.metrostore.beans;

/**
 * Created by azee on 24.07.15.
 */
public enum ParamKey {
    MAIN_ACTIVITY(1),
    LIST_ACTIVITY(2)
    ;

    private final int value;

    ParamKey(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static ParamKey fromValue(int v) {
        for (ParamKey c: ParamKey.values()) {
            if (c.value == v) {
                return c;
            }
        }
        throw new IllegalArgumentException(Integer.toString(v));
    }
}
