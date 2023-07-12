package uk.co.roteala.dataprocessingcommons.storages;

import java.util.HashMap;
import java.util.Map;

public enum StorageTypes {
    WALLET("W"),
    MEMPOOL("M"),
    BLOCKS("B"),
    TX("T"),
    PEERS("P");

    private String code;

    private static final Map<String, StorageTypes> VALUES = new HashMap<>();

    StorageTypes(String code) {
        this.code = code;
    }

    static {
        for (StorageTypes type : values()) {
            VALUES.put(type.code, type);
        }
    }

    public String getCode() {
        return this.code;
    }

    public static StorageTypes valueOfCode(String code) {
        return VALUES.get(code);
    }
}
