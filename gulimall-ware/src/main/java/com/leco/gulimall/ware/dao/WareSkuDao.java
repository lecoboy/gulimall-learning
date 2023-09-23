package com.leco.gulimall.ware.dao;

import com.leco.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 * 
 * @author greg
 * @email lecoboy@163.com
 * @date 2023-09-23 18:20:11
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	
}