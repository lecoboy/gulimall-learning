package com.leco.gulimall.product.web;

import com.leco.gulimall.product.entity.CategoryEntity;
import com.leco.gulimall.product.service.CategoryService;
import com.leco.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author greg
 * @version 2023/10/17
 **/
@Controller
public class IndexController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedissonClient redisson;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping(value = {"/", "index.html"})
    private String indexPage(Model model) {

        //1、查出所有的一级分类
        List<CategoryEntity> categoryEntities = categoryService.getLevel1Categorys();
        model.addAttribute("categories", categoryEntities);

        return "index";
    }


    //index/json/catalog.json
    @GetMapping(value = "/index/catalog.json")
    @ResponseBody
    public Map<String, List<Catelog2Vo>> getCatalogJson() {

        return categoryService.getCatalogJson();

    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        RLock lock = redisson.getLock("my-lock");
        lock.lock();
        try {
            System.out.println("加锁成功，执行业务...");
            try { TimeUnit.SECONDS.sleep(20); } catch (InterruptedException e) { e.printStackTrace(); }
        } finally {
            lock.unlock();
        }

        return "hello";
    }

    @GetMapping(value = "/write")
    @ResponseBody
    public String writeValue() {
        String s = "";
        RReadWriteLock readWriteLock = redisson.getReadWriteLock("rw-lock");
        RLock rLock = readWriteLock.writeLock();
        try {
            //改数据加写锁，读数据加读锁
            rLock.lock();
            System.out.println("写锁加锁成功" + Thread.currentThread().getId());
            s = UUID.randomUUID().toString();
            stringRedisTemplate.opsForValue().set("writeValue",s);
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
            System.out.println("写锁释放" + Thread.currentThread().getId());
        }

        return s;
    }

    @GetMapping(value = "/read")
    @ResponseBody
    public String readValue() {
        String s = "";
        RReadWriteLock readWriteLock = redisson.getReadWriteLock("rw-lock");
        //加读锁
        RLock rLock = readWriteLock.readLock();
        try {
            rLock.lock();
            System.out.println("读锁加锁成功" + Thread.currentThread().getId());
            s = stringRedisTemplate.opsForValue().get("writeValue");
            TimeUnit.SECONDS.sleep(10);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
            System.out.println("读锁释放" + Thread.currentThread().getId());
        }

        return s;
    }

    /**
     * 车库停车
     * 3车位
     * 信号量也可以做分布式限流
     */
    @GetMapping(value = "/park")
    @ResponseBody
    public String park() {

        RSemaphore park = redisson.getSemaphore("park");
//        park.acquire();//获取一个信号、获取一个值,占一个车位
        boolean flag = park.tryAcquire();

        if (!flag) {
            return "error";
        }

        // 执行业务
        // biz code ...

        return "ok";
    }

    @GetMapping(value = "/go")
    @ResponseBody
    public String go() {
        RSemaphore park = redisson.getSemaphore("park");
        park.release();//释放一个车位
        return "ok";
    }
}
