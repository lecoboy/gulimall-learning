package com.leco.gulimall.product.feign;

import com.leco.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author greg
 * @version 2023/10/16
 **/
@FeignClient("gulimall-ware")
public interface WareFeignService {
    @PostMapping(value = "/ware/waresku/hasStock")
    R getSkuHasStock(@RequestBody List<Long> skuIds);
}
