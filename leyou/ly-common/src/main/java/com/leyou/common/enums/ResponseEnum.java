package com.leyou.common.enums;

/**
 * @author liyu
 * @date 2019/8/14 18:10
 * @description
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ResponseEnum {
    REGISTER_USER_SUCCESS(200, "用户注册成功"),
    USER_QUERY_SUCCESS(200, "用户查询成功"),
    USER_QUERY_FAILED(404, "用户查询失败");
    private int code;
    private String msg;
}
