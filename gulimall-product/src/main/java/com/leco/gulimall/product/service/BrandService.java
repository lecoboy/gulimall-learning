package com.leco.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leco.common.utils.PageUtils;
import com.leco.gulimall.product.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author greg
 * @email lecoboy@163.com
 * @date 2023-09-21 18:44:47
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

