package com.leyou.user.controller;

import com.leyou.common.enums.ResponseEnum;
import com.leyou.common.vo.ResponseModel;
import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Author: taft
 * @Date: 2018-9-4 17:05
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 校验的目的仅仅在于看此用户名有没有注册过
     * @param data
     * @param type
     * @return
     */
    @GetMapping("check/{data}/{type}")
    public ResponseEntity<ResponseModel> check(@PathVariable("data") String data, @PathVariable("type") Integer type){

        Boolean bool = userService.checkData(data,type);
        if (null==bool){
            return new ResponseEntity<>(new ResponseModel<>(ResponseEnum.USER_QUERY_FAILED,null),HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(new ResponseModel<>(ResponseEnum.USER_QUERY_SUCCESS,bool));
    }

    @PostMapping("code")
    public ResponseModel sendSms(@RequestParam("phone") String phone){
        Boolean bool = userService.sendSms(phone);
        if (null==bool || !bool){
            return new ResponseModel<>(ResponseEnum.MESSAGE_SEND_FAILED,null);
        }

        return new ResponseModel<>(ResponseEnum.MESSAGE_SEND_SUCCESS,null);
    }

    @PostMapping("register")
    public ResponseModel register(@Valid User user, @RequestParam("code") String code){
        Boolean bool = userService.register(user,code);
        if (null==bool||!bool){
            return new ResponseModel<>(ResponseEnum.USER_REGISTER_FAILED,null);
        }

        return new ResponseModel<>(ResponseEnum.USER_REGISTER_SUCCESS,null);
    }

    @GetMapping("queryUser")
    public ResponseModel query(String username,String password){

        User storeUser = this.userService.queryUserByUsernameAndPassword(username,password);

        if (null==storeUser){
            return new ResponseModel<>(ResponseEnum.USER_QUERY_FAILED,null);
        }
        return new ResponseModel<>(ResponseEnum.USER_QUERY_SUCCESS,storeUser);
    }
}
