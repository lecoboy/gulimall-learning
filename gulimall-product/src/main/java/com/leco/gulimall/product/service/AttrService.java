package com.leco.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leco.gulimall.common.utils.PageUtils;
import com.leco.gulimall.product.entity.AttrEntity;
import com.leco.gulimall.product.vo.AttrGroupRelationVO;
import com.leco.gulimall.product.vo.AttrRespVO;
import com.leco.gulimall.product.vo.AttrVO;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author greg
 * @email lecoboy@163.com
 * @date 2023-09-21 18:44:47
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVO attr);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType);

    AttrRespVO getAttrInfo(Long attrId);

    void updateAttrById(AttrVO attr);

    List<AttrEntity> getRelationAttr(Long attrgroupId);

    void deleteRelation(AttrGroupRelationVO[] vos);

    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);

    List<Long> selectSearchAttrs(List<Long> attrIds);
}

