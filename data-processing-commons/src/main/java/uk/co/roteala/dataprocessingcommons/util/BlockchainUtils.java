package uk.co.roteala.dataprocessingcommons.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BlockchainUtils {
    public String parseIpAddress(String address) {
        return address
                .substring(1)
                .split(":")[0];
    }
}
