package com.leco.gulimall.member.feign;

import com.leco.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author greg
 * @version 2023/9/24
 **/
@FeignClient("gulimall-coupon")
public interface CouponFeignService {
    @GetMapping("/coupon/coupon/member/list")
    R memberCoupons();
}
