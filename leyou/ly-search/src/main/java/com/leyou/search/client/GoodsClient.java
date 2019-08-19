package com.leyou.search.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: taft
 * @Date: 2018-8-29 16:30
 */
@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {
}
