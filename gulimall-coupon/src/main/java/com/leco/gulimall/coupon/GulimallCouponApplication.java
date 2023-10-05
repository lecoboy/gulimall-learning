package com.leco.gulimall.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author greg
 * @version 2023/9/23
 **/
@EnableTransactionManagement
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.leco.gulimall"})
public class GulimallCouponApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallCouponApplication.class, args);
    }
}
