package com.zhl.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class PageListResponse<T> extends AbstractResponse{

    private Integer pageNo;

    private Integer length;

    private Integer total;

    private List<T> data;

    public PageListResponse(ResponseCode code, String message, Integer pageNo, Integer length, Integer total, List<T> data){
        this.code = code;
        this.message = message;
        this.pageNo = pageNo;
        this.length = length;
        this.total = total;
        this.data = data;
    }

    public PageListResponse(ResponseCode code, String message, PageList<T> pageList){
        this.code = code;
        this.message = message;
        this.pageNo = pageList.getPageNo();
        this.length = pageList.getLength();
        this.total = pageList.getTotal();
        this.data = pageList.getData();
    }

    public static <M> PageListResponse<M> success(Integer pageNo, Integer length, Integer total, List<M> data){
        return new PageListResponse<>(ResponseCode.SUCCESS,ResponseCode.SUCCESS.getMessage(),pageNo,length,total,data);
    }

    public static <M> PageListResponse<M> error(Integer pageNo, Integer length, Integer total, List<M> data){
        return new PageListResponse<>(ResponseCode.FORBIDDEN,ResponseCode.FORBIDDEN.getMessage(),pageNo,length,total,data);
    }

    public static <M> PageListResponse<M> success(String message, Integer pageNo, Integer length, Integer total, List<M> data){
        return new PageListResponse<>(ResponseCode.SUCCESS,message,pageNo,length,total,data);
    }

    public static <M> PageListResponse<M> error(String message, Integer pageNo, Integer length, Integer total, List<M> data){
        return new PageListResponse<>(ResponseCode.FORBIDDEN,message,pageNo,length,total,data);
    }

    public static <M> PageListResponse<M> success(String message, PageList<M> pageList){
        return new PageListResponse<>(ResponseCode.SUCCESS,message,pageList);
    }

    public static <M> PageListResponse<M> error(String message, PageList<M> pageList){
        return new PageListResponse<>(ResponseCode.FORBIDDEN,message,pageList);
    }

    public static <M> PageListResponse<M> emptyList(Integer pageNo, Integer length, Integer total){
        return new PageListResponse<>(ResponseCode.SUCCESS,ResponseCode.SUCCESS.getMessage(), pageNo,length,total, Collections.emptyList());
    }

    public static <M> PageListResponse<M> emptyList(String message, Integer pageNo, Integer length, Integer total){
        return new PageListResponse<>(ResponseCode.SUCCESS,message,pageNo,length,total,Collections.emptyList());
    }



}
