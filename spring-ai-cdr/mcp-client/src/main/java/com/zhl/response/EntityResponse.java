package com.zhl.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 具备对象的返回结果
 * @param <T>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityResponse<T> extends AbstractResponse{

    private T data;

    public EntityResponse(ResponseCode code, String message, T data){
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <M> EntityResponse<M> success(String message,M m){
        EntityResponse<M> entityResponse = new EntityResponse<>(m);
        entityResponse.setCode(ResponseCode.SUCCESS);
        entityResponse.setMessage(message);
        return entityResponse;

    }

    public static <M> EntityResponse<M> success(M m){
        EntityResponse<M> entityResponse = new EntityResponse<>(m);
        entityResponse.setCode(ResponseCode.SUCCESS);
        entityResponse.setMessage(ResponseCode.SUCCESS.getMessage());
        return entityResponse;
    }

    public static <M> EntityResponse<M> error(String message){
        EntityResponse<M> entityResponse = new EntityResponse<>();
        entityResponse.setCode(ResponseCode.FORBIDDEN);
        entityResponse.setMessage(message);
        return entityResponse;
    }

    public static <M> EntityResponse<M> error(ResponseCode code,String message){
        EntityResponse<M> entityResponse = new EntityResponse<>();
        entityResponse.setCode(code);
        entityResponse.setMessage(message);
        return entityResponse;

    }



}
