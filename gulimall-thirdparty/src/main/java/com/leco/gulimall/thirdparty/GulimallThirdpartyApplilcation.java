package com.leco.gulimall.thirdparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author greg
 * @version 2023/9/29
 **/
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallThirdpartyApplilcation {
    public static void main(String[] args) {
        SpringApplication.run(GulimallThirdpartyApplilcation.class, args);
    }
}
