package com.zhl.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListResponse<T> extends AbstractResponse{

    private List<T> data;

    public ListResponse(ResponseCode code, String message, List<T> data){
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <M> ListResponse<M> success(String message, List<M> m){
        ListResponse<M> entityResponse = new ListResponse<>(m);
        entityResponse.setCode(ResponseCode.SUCCESS);
        entityResponse.setMessage(message);
        return entityResponse;

    }

    public static <M> ListResponse<M> success(List<M> m){
        ListResponse<M> entityResponse = new ListResponse<>(m);
        entityResponse.setCode(ResponseCode.SUCCESS);
        entityResponse.setMessage(ResponseCode.SUCCESS.getMessage());
        return entityResponse;
    }

    public static <M> ListResponse<M> error(){
        ListResponse<M> entityResponse = new ListResponse<>();
        entityResponse.setCode(ResponseCode.FORBIDDEN);
        entityResponse.setMessage(ResponseCode.FORBIDDEN.getMessage());
        entityResponse.setData(Collections.emptyList());
        return entityResponse;
    }

    public static <M> ListResponse<M> error(String message){
        ListResponse<M> entityResponse = new ListResponse<>();
        entityResponse.setCode(ResponseCode.FORBIDDEN);
        entityResponse.setMessage(message);
        entityResponse.setData(Collections.emptyList());
        return entityResponse;
    }

    public static <M> ListResponse<M> error(ResponseCode responseCode,String message){
        ListResponse<M> entityResponse = new ListResponse<>();
        entityResponse.setCode(responseCode);
        entityResponse.setMessage(message);
        entityResponse.setData(Collections.emptyList());
        return entityResponse;
    }

    public boolean isEmpty(){
        return CollectionUtils.isEmpty(data);
    }

}
