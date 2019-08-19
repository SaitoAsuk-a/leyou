package com.leyou.search.client;

import com.leyou.item.api.SpecificationApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: taft
 * @Date: 2018-8-29 16:55
 */
@FeignClient("item-service")
public interface SpecificationClient extends SpecificationApi {
}
