package com.leco.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leco.common.utils.PageUtils;
import com.leco.gulimall.product.entity.AttrEntity;

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
}

