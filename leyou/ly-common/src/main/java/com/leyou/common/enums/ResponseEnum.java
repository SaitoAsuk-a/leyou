package com.leyou.common.enums;

/**
 * @author liyu
 * @date 2019/8/14 18:10
 * @description
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ResponseEnum {
    USER_REGISTER_SUCCESS(200, "用户注册成功"),
    USER_REGISTER_FAILED(404, "用户注册失败"),
    USER_QUERY_SUCCESS(200, "用户查询成功"),
    USER_QUERY_FAILED(404, "用户查询失败"),
    MESSAGE_SEND_SUCCESS(200, "短信发送成功"),
    MESSAGE_SEND_FAILED(404, "短信发送失败"),
    USER_AUTH_SUCCESS(200,"认证成功"),
    USER_AUTH_FAILED(404,"认证失败"),
    ;

    private Integer code;
    private String msg;
}
