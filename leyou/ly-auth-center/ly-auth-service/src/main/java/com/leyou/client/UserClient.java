package com.leyou.client;

import com.leyou.user.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: taft
 * @Date: 2018-9-6 18:00
 */
@FeignClient("user-service")
public interface UserClient extends UserApi{
}
