package com.leyou.auth.controller;

import com.alibaba.fastjson.JSONObject;
import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.service.AuthService;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.enums.ResponseEnum;
import com.leyou.common.utils.CookieUtils;
import com.leyou.common.vo.ResponseModel;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: liyu
 * @Date: 2019-8-6 17:50
 */
@Slf4j
@RestController
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private AuthService authService;

    /**
     * 授权内容为token，校验内容为username，password，校验方式采用远程服务调用请求user-service查询，查到了封装token
     *
     * token保存到cookie中
     * @param username
     * @param password
     * @param request
     * @param response
     * @return
     */

    @PostMapping("accredit")
    public ResponseEntity<ResponseModel> accredit(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletRequest request,
            HttpServletResponse response
    ){
        // 登录校验,先去查询数据库
        String token = this.authService.authentication(username, password);
        if (null==token){
            //没有认证
            return new ResponseEntity<>(new ResponseModel<>(ResponseEnum.USER_AUTH_FAILED,null),HttpStatus.UNAUTHORIZED);
        }

        //controller得到token要把token回送到客户端
        CookieUtils.setCookie(request, response, jwtProperties.getCookieName(),
                token, jwtProperties.getCookieMaxAge(), null, true);
        log.info("token="+token);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("token",token);

        return ResponseEntity.ok(new ResponseModel<>(ResponseEnum.USER_AUTH_SUCCESS,jsonObject));
    }

    @GetMapping("verify")
    public ResponseModel verify(
            @CookieValue("LY_TOKEN")String token,
            HttpServletRequest request,
            HttpServletResponse response){
        try {
            UserInfo user = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());

            //刷新token   用户预测  缓式写入  20min
            String newToken = JwtUtils.generateToken(user, jwtProperties.getPrivateKey(), jwtProperties.getExpire());

            CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),newToken,jwtProperties.getCookieMaxAge(),null,true);
            if (user==null){
                return new ResponseModel<>(ResponseEnum.AUTH_FAILED,null);
            }
            return new ResponseModel<>(ResponseEnum.AUTH_SUCCESS,user);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return new ResponseModel<>(ResponseEnum.AUTH_FAILED,null);

    }
}
