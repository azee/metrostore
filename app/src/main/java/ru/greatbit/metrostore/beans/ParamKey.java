package ru.greatbit.metrostore.beans;

/**
 * Created by azee on 24.07.15.
 */
public enum ParamKey {
    CONFIGURATION("CONFIGURATION")
    ;

    private final String value;

    ParamKey(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static ParamKey fromValue(int v) {
        for (ParamKey c: ParamKey.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(Integer.toString(v));
    }
}
