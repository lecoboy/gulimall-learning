package com.leco.gulimall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author greg
 * @version 2023/9/23
 **/
@EnableFeignClients(basePackages = "com.leco.gulimall.member.feign")
@EnableTransactionManagement
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.leco.gulimall"})
public class GulimallMemberApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallMemberApplication.class, args);
    }
}
