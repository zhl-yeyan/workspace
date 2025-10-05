package com.zhl.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbstractResponse {

    public ResponseCode code;

    public String message;

    @JsonIgnore
    public boolean isSuccess(){
        return ResponseCode.SUCCESS.equals(code);
    }

    @JsonIgnore
    public boolean isError(){
        return !ResponseCode.SUCCESS.equals(code);
    }

}
