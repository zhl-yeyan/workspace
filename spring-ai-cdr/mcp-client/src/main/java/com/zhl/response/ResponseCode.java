package com.zhl.response;

import com.zhl.enums.BaseEnum;
import lombok.Getter;

@Getter
public enum ResponseCode implements BaseEnum {

    SUCCESS("200","success"),
    WARN("300","warn"),
    FORBIDDEN("400","forbidden"),
    SERVER_ERROR("500","SeverError");

    private final String code;

    private final String message;

    ResponseCode(String code, String message){
        this.code = code;
        this.message = message;
    }
}
