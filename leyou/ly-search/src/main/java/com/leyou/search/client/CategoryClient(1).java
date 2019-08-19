package com.leyou.search.client;

import com.leyou.item.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: taft
 * @Date: 2018-8-29 16:20
 */
@FeignClient("item-service")
public interface CategoryClient extends CategoryApi {

}
