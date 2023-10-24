package com.leco.gulimall.webcommon.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author greg
 * @version 2023/10/24
 **/
@Configuration
public class RedissonConfig {
    @Value("${spring.redis.host}")
    private String redisHost;
    @Value("${spring.redis.port:6379}")
    private String redisPort;

    @Bean(destroyMethod = "shutdown")
    RedissonClient redisson() throws IOException {
        Config config = new Config();
        config.useSingleServer().setAddress(String.format("redis://%s:%s", redisHost, redisPort));
        return Redisson.create(config);
    }
}
