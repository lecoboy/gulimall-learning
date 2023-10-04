package com.leco.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leco.gulimall.common.to.SkuReductionTO;
import com.leco.gulimall.common.utils.PageUtils;
import com.leco.gulimall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author greg
 * @email lecoboy@163.com
 * @date 2023-09-23 17:37:57
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuReduction(SkuReductionTO skuReductionTo);
}

