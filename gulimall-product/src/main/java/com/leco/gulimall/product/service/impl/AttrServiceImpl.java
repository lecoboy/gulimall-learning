package com.leco.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leco.gulimall.common.utils.PageUtils;
import com.leco.gulimall.common.utils.Query;
import com.leco.gulimall.product.dao.AttrDao;
import com.leco.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.leco.gulimall.product.entity.AttrEntity;
import com.leco.gulimall.product.entity.AttrGroupEntity;
import com.leco.gulimall.product.entity.CategoryEntity;
import com.leco.gulimall.product.service.AttrAttrgroupRelationService;
import com.leco.gulimall.product.service.AttrGroupService;
import com.leco.gulimall.product.service.AttrService;
import com.leco.gulimall.product.service.CategoryService;
import com.leco.gulimall.product.vo.AttrRespVO;
import com.leco.gulimall.product.vo.AttrVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Resource
    private AttrAttrgroupRelationService relationService;
    @Resource
    private AttrGroupService attrGroupService;
    @Resource
    private CategoryService categoryService;

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
        relationService.save(relationEntity);
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>()
                .eq("attr_type", "base".equalsIgnoreCase(attrType) ?
                        1 : 0);

        //根据catelogId查询信息
        if (catelogId != 0) {
            queryWrapper.eq("catelog_id", catelogId);
        }

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            //attr_id attr_name
            queryWrapper.and((wrapper) -> {
                wrapper.eq("attr_id", key).or().like("attr_name", key);
            });
        }

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );

        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();

        List<AttrRespVO> respVos = records.stream().map((attrEntity) -> {
            AttrRespVO attrRespVo = new AttrRespVO();
            BeanUtils.copyProperties(attrEntity, attrRespVo);

            //设置分组名
            if ("base".equalsIgnoreCase(attrType)) {
                AttrAttrgroupRelationEntity relationEntity =
                        relationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
                if (relationEntity != null && relationEntity.getAttrGroupId() != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupService.getById(relationEntity.getAttrGroupId());
                    //TODO 这里有个问题，分组和属性到底是多对多还是一对多？如果是多对多，那么这里就应该是个list；
                    // 如果是一对多，那么attr表里加上一个attrGroupId直接查即可。
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }

            }
            // 设置分类名
            CategoryEntity categoryEntity = categoryService.getById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                attrRespVo.setCatelogName(categoryEntity.getName());

            }
            return attrRespVo;
        }).collect(Collectors.toList());

        pageUtils.setList(respVos);
        return pageUtils;
    }

    @Override
    public AttrRespVO getAttrInfo(Long attrId) {
        //查询详细信息
        AttrEntity attrEntity = this.getById(attrId);

        //查询分组信息
        AttrRespVO respVo = new AttrRespVO();
        BeanUtils.copyProperties(attrEntity,respVo);

        //判断是否是基本类型
        if (attrEntity.getAttrType() == 1) {
            //1、设置分组信息
            AttrAttrgroupRelationEntity relationEntity = relationService.getOne(
                    (new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId)));
            if (relationEntity != null) {
                respVo.setAttrGroupId(relationEntity.getAttrGroupId());
                //获取分组名称
                AttrGroupEntity attrGroupEntity = attrGroupService.getById(relationEntity.getAttrGroupId());
                if (attrGroupEntity != null) {
                    respVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }

        //2、设置分类信息
        Long catelogId = attrEntity.getCatelogId();
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);

        respVo.setCatelogPath(catelogPath);
        CategoryEntity categoryEntity = categoryService.getById(catelogId);
        if (categoryEntity != null) {
            respVo.setCatelogName(categoryEntity.getName());
        }

        return respVo;
    }

}