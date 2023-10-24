package com.leco.gulimall.product;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author greg
 * @version 2023/10/24
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductTests {
    @Autowired
    RedissonClient redissonClient;

    @Test
    public void testRedisson() {
        System.out.println(redissonClient);
    }
}
