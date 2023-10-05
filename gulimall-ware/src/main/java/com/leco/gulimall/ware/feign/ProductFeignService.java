package com.leco.gulimall.ware.feign;

import com.leco.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author greg
 * @version 2023/10/5
 **/
@FeignClient("gulimall-product")
public interface ProductFeignService {
    /**
     * sku信息
     * @param skuId
     * @return
     */
    @GetMapping("/product/skuinfo/info/{skuId}")
    //@RequiresPermissions("product:skuinfo:info")
    R info(@PathVariable("skuId") Long skuId);
}
