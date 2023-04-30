package uk.co.roteala.dataprocessingcommons;

import java.util.HashMap;
import java.util.Map;

public enum ProcessingMode {
    YEAR(1),
    MONTH(2),
    WEEK(3);

    private final int intValue;

    private static final Map<Integer, ProcessingMode> VALUES = new HashMap<>();

    static {
        for(ProcessingMode mode : values()) {
            VALUES.put(mode.intValue, mode);
        }
    }

    ProcessingMode(int value) {
        intValue = value;
    }

    public int getValue() {
        return intValue;
    }

    public static ProcessingMode getMode(int intValue) {
        return VALUES.get(intValue);
    }


}
