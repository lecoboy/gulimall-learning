package com.leco.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leco.gulimall.common.utils.PageUtils;
import com.leco.gulimall.common.utils.Query;
import com.leco.gulimall.product.dao.AttrDao;
import com.leco.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.leco.gulimall.product.entity.AttrEntity;
import com.leco.gulimall.product.service.AttrAttrgroupRelationService;
import com.leco.gulimall.product.service.AttrService;
import com.leco.gulimall.product.vo.AttrVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Resource
    private AttrAttrgroupRelationService relationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveAttr(AttrVO attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        //1、保存基本数据
        this.save(attrEntity);

        //2、保存关联关系
        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        relationEntity.setAttrGroupId(attr.getAttrGroupId());
        relationEntity.setAttrId(attrEntity.getAttrId());
        relationDao.save(relationEntity);
    }

}