package com.zhl.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BaseResponse extends AbstractResponse{

    public BaseResponse(ResponseCode code,String message){
        this.code  = code;
        this.message = message;
    }

    public static BaseResponse forbidden(String message){
        return new BaseResponse(ResponseCode.FORBIDDEN,message);
    }

    public static BaseResponse forbidden(){
        new BaseResponse(ResponseCode.FORBIDDEN,"");
        return new BaseResponse(ResponseCode.FORBIDDEN,ResponseCode.FORBIDDEN.getMessage());
    }

    public static BaseResponse error(String message){
        return new BaseResponse(ResponseCode.SERVER_ERROR,message);
    }

    public static BaseResponse error(){
        return new BaseResponse(ResponseCode.SERVER_ERROR,ResponseCode.SERVER_ERROR.getMessage());
    }

    public static BaseResponse success(String message){
        return new BaseResponse(ResponseCode.SUCCESS,message);
    }

    public static BaseResponse success(){
        return new BaseResponse(ResponseCode.SUCCESS,ResponseCode.SUCCESS.getMessage());
    }




}
