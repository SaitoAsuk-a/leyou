package com.leyou.common.vo;

import com.leyou.common.enums.ResponseEnum;
import lombok.Data;

/**
 * @author liyu
 * @date 2019/8/14 17:29
 * @description
 */
@Data
public class ResponseModel<T>{
    private int status;
    private String message;
    private T data;

    public ResponseModel(ResponseEnum responseEnum, T data) {
        this.status = responseEnum.getCode();
        this.message = responseEnum.getMsg();
        this.data = data;
    }
}
