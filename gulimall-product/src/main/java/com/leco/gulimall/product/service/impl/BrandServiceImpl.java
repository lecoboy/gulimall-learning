package com.leco.gulimall.product.service.impl;

import com.leco.gulimall.product.service.CategoryBrandRelationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leco.gulimall.common.utils.PageUtils;
import com.leco.gulimall.common.utils.Query;

import com.leco.gulimall.product.dao.BrandDao;
import com.leco.gulimall.product.entity.BrandEntity;
import com.leco.gulimall.product.service.BrandService;
import org.springframework.transaction.annotation.Transactional;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        //1、获取key
        String key = (String) params.get("key");
        QueryWrapper<BrandEntity> wrapper = new QueryWrapper<>();
        //如果传过来的数据不是空的，就进行多参数查询
        if (!StringUtils.isEmpty(key)) {
            wrapper.eq("brand_id", key).or().like("name", key);
        }

        return new PageUtils(this.page(new Query<BrandEntity>().getPage(params), wrapper));
    }

    @Transactional
    @Override
    public void updateDetail(BrandEntity brand) {
        //保证冗余字段的数据一致
        baseMapper.updateById(brand);

        if (!StringUtils.isEmpty(brand.getName())) {
            //同步更新其他关联表中的数据
            categoryBrandRelationService.updateBrand(brand.getBrandId(), brand.getName());

            //TODO 更新其他关联
        }
    }

}