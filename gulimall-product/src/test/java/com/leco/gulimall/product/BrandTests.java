package com.leco.gulimall.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.leco.gulimall.product.entity.BrandEntity;
import com.leco.gulimall.product.service.BrandService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author greg
 * @version 2023/9/23
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class BrandTests {
    @Autowired
    BrandService brandService;

    @Test
    public void testSave() {
        BrandEntity entity = new BrandEntity();
        entity.setName("华为");
        brandService.save(entity);
        System.out.println("ok");
    }

    @Test
    public void testUpdate() {
        BrandEntity entity = new BrandEntity();
        entity.setBrandId(1L);
        entity.setDescript("华为描述");
        brandService.updateById(entity);
    }

    @Test
    public void testList() {
        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("name", "华为"));
        list.forEach(System.out::println);
    }
}
