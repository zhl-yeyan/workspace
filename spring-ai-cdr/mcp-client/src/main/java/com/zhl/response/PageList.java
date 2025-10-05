package com.zhl.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageList<T> {

    private Integer pageNo;

    private Integer length;

    private Integer total;

    private List<T> data;

    public boolean isEmpty(){
        return CollectionUtils.isEmpty(data);
    }

}
