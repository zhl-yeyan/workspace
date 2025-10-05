package com.zhl.enums;

import java.util.Arrays;

public interface BaseEnum {

    String getCode();

    String getMessage();

    static <T extends BaseEnum> T getEnum(Class<T> type, String code){
        T[] enumConstants = type.getEnumConstants();
        return Arrays.stream(enumConstants).filter(f-> f.getCode().equals(code)).findFirst().orElse(null);
    }
}
