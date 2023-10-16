package com.leco.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leco.gulimall.common.to.SkuHasStockTO;
import com.leco.gulimall.common.utils.PageUtils;
import com.leco.gulimall.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author greg
 * @email lecoboy@163.com
 * @date 2023-09-23 18:20:11
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockTO> getSkuHasStock(List<Long> skuIds);
}

