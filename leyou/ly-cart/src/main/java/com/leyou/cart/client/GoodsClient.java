package com.leyou.cart.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: taft
 * @Date: 2018-9-7 18:00
 */
@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {
}
