package com.leco.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.leco.gulimall.common.utils.PageUtils;
import com.leco.gulimall.common.utils.Query;
import com.leco.gulimall.product.dao.CategoryDao;
import com.leco.gulimall.product.entity.CategoryEntity;
import com.leco.gulimall.product.service.CategoryBrandRelationService;
import com.leco.gulimall.product.service.CategoryService;
import com.leco.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private RedissonClient redisson;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1、查询出所有分类
        List<CategoryEntity> entities = super.baseMapper.selectList(null);

        //2、组装成父子的树形结构
        return entities.stream()
                //2.1)、找到所有一级分类
                .filter(e -> e.getParentCid() == 0)
                .peek((menu) -> menu.setChildren(getChildren(menu, entities)))
                .sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort())))
                .collect(Collectors.toList());
    }

    @Override
    public void removeCategoriesByIds(List<Long> ids) {
        //TODO 检查当前删除的分类，是否被别的地方引用
        baseMapper.deleteBatchIds(ids);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> path = new ArrayList<>();
        //递归查询是否还有父节点
        findParentPath(catelogId, path);
        return path.toArray(new Long[0]);
    }

    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        return this.baseMapper.selectList(
                new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
    }

    /**
     * 在循环中查库，效率极低
     *
     * @return
     */
//    @Override
//    public Map<String, List<Catelog2Vo>> getCatalogJson_old() {
//
//        //将数据库的多次查询变为一次
//        List<CategoryEntity> selectList = this.baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
//
//        //封装数据
//        return selectList.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
//            //1、每一个的一级分类,查到这个一级分类的二级分类
//            List<CategoryEntity> categoryEntities = this.baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
//
//            //2、封装上面的结果
//            List<Catelog2Vo> catelog2Vos = null;
//            if (categoryEntities != null) {
//                catelog2Vos = categoryEntities.stream().map(l2 -> {
//                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName().toString());
//
//                    //1、找当前二级分类的三级分类封装成vo
//                    List<CategoryEntity> level3Catelog = this.baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", l2.getCatId()));
//
//                    if (level3Catelog != null) {
//                        List<Catelog2Vo.Category3Vo> category3Vos = level3Catelog.stream().map(l3 -> {
//                            //2、封装成指定格式
//                            return new Catelog2Vo.Category3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
//                        }).collect(Collectors.toList());
//                        catelog2Vo.setCatalog3List(category3Vos);
//                    }
//
//                    return catelog2Vo;
//                }).collect(Collectors.toList());
//            }
//
//            return catelog2Vos == null ? Lists.newArrayList() : catelog2Vos;
//        }));
//    }
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        String cacheCatalogs = stringRedisTemplate.opsForValue().get("getCatalogJson");
        if (!StringUtils.isEmpty(cacheCatalogs)) {
            return JSON.parseObject(cacheCatalogs, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
        }
        System.out.println("缓存未命中，准备查数据库。。。");
        return getCatalogJsonWithRedisson();
    }

    public Map<String, List<Catelog2Vo>> getCatalogJsonWithRedisson() {
        RLock lock = redisson.getLock("com.leco.gulimall.product.service.impl.CategoryServiceImpl.getCatalogJsonWithRedisson-lock");
        lock.lock();
        Map<String, List<Catelog2Vo>> catalogs;
        try {
            catalogs = getCatalogJsonFromDb();
        } finally {
            lock.unlock();
        }
        return catalogs;

    }

    private void simpleWatchDog(Timer timer, String key, String value) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                String script = "if (redis.call('get', KEYS[1])==ARGV[2]) then " +
                        "redis.call('expire', KEYS[1], ARGV[1]); " +
                        "return 1; " +
                        "end; " +
                        "return 0;";
                Long ret = stringRedisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Collections.singletonList(key), "30", value);
                System.out.println("续期..." + ret);
            }
        }, 10000, 10000);
    }

    private Map<String, List<Catelog2Vo>> getCatalogJsonWithRedis() {
        String lockName = "com.leco.gulimall.product.service.impl.CategoryServiceImpl.getCatalogJsonWithRedis-lock";
        String uuid = UUID.randomUUID().toString();
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent(lockName, uuid, 30, TimeUnit.SECONDS);
        if (lock != null && lock) {
            // 一个简单的锁续期逻辑
            Timer timer = new Timer();
            simpleWatchDog(timer, lockName, uuid);

            Map<String, List<Catelog2Vo>> catalogs;
            try {
                catalogs = getCatalogJsonFromDb();
            } finally {
                timer.cancel();
                String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then return redis.call(\"del\",KEYS[1]) else return 0 end";
                Long ret = stringRedisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Collections.singletonList(lockName), uuid);
                System.out.println("解锁..." + ret);
            }
            return catalogs;
        } else {
            try {
                Thread.sleep(200);
            } catch (Exception ignored) {
            }
            return getCatalogJsonWithRedis();
        }
    }

    public Map<String, List<Catelog2Vo>> getCatalogJsonWithSync() {
        synchronized (this) {
            // 加个锁，防止缓存失效的时候，瞬间大量请求查询数据库
            // 这样处理，排队中的请求，只有第一个会查数据库，其他的都可以拿缓存
            // 但是本地锁在分布式环境下，还是会放多个请求去查数据库
            return getCatalogJsonFromDb();
        }
    }

    /**
     * 查一次库，在内存中处理数据
     *
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDb() {
        String cacheCatalogs = stringRedisTemplate.opsForValue().get("getCatalogJson");
        if (!StringUtils.isEmpty(cacheCatalogs)) {
            return JSON.parseObject(cacheCatalogs, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
        }
        System.out.println("查询了数据库。。。");
        //将数据库的多次查询变为一次
        List<CategoryEntity> selectList = this.baseMapper.selectList(null);

        // 分组
        Map<Long, List<CategoryEntity>> childrenMap = selectList.stream().collect(Collectors.groupingBy(CategoryEntity::getParentCid));

        //1、查出所有分类
        //1、1）查出所有一级分类
        //        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
        List<CategoryEntity> level1Categorys = childrenMap.get(0L);

        //封装数据
        Map<String, List<Catelog2Vo>> resultMap = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1、每一个的一级分类,查到这个一级分类的二级分类
            List<CategoryEntity> categoryEntities = childrenMap.get(v.getCatId());

            //2、封装上面的结果
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName().toString());

                    //1、找当前二级分类的三级分类封装成vo
                    List<CategoryEntity> level3Catelog = childrenMap.get(l2.getCatId());

                    if (level3Catelog != null) {
                        List<Catelog2Vo.Category3Vo> category3Vos = level3Catelog.stream().map(l3 -> {
                            //2、封装成指定格式
                            return new Catelog2Vo.Category3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(category3Vos);
                    }

                    return catelog2Vo;
                }).collect(Collectors.toList());
            }

            return catelog2Vos == null ? Lists.newArrayList() : catelog2Vos;
        }));
        stringRedisTemplate.opsForValue().set("getCatalogJson", JSON.toJSONString(resultMap), 3600, TimeUnit.SECONDS);
        return resultMap;
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, long parentCid) {
        return selectList.stream().filter(item -> item.getParentCid().equals(parentCid)).collect(Collectors.toList());
    }

    private void findParentPath(Long catelogId, List<Long> path) {
        //根据当前分类id查询信息
        CategoryEntity byId = this.getById(catelogId);
        //如果当前不是父分类
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), path);
        }

        path.add(catelogId);
    }

    //递归查找所有菜单的子菜单
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {

        return all.stream().filter(categoryEntity -> categoryEntity.getParentCid().equals(root.getCatId())).peek(categoryEntity -> {
            //1、找到子菜单(递归)
            categoryEntity.setChildren(getChildren(categoryEntity, all));
        }).sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort()))).collect(Collectors.toList());

    }


}