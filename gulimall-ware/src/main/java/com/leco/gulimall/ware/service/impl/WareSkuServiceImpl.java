package com.leco.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leco.gulimall.common.to.SkuHasStockTO;
import com.leco.gulimall.common.utils.PageUtils;
import com.leco.gulimall.common.utils.Query;
import com.leco.gulimall.common.utils.R;
import com.leco.gulimall.ware.dao.WareSkuDao;
import com.leco.gulimall.ware.dto.SkuStockDTO;
import com.leco.gulimall.ware.entity.WareSkuEntity;
import com.leco.gulimall.ware.feign.ProductFeignService;
import com.leco.gulimall.ware.service.WareSkuService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();

        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId) && !"0".equalsIgnoreCase(skuId)) {
            queryWrapper.eq("sku_id", skuId);
        }

        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId) && !"0".equalsIgnoreCase(wareId)) {
            queryWrapper.eq("ware_id", wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //1、判读如果没有这个库存记录新增
        List<WareSkuEntity> wareSkuEntities = this.baseMapper.selectList(
                new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));

        if (wareSkuEntities == null || wareSkuEntities.size() == 0) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStockLocked(0);
            // 查询skuName
            try {
                R r = productFeignService.info(skuId);
                if (r.getCode() == 0) {
                    Map<String, Object> skuInfo = (Map<String, Object>) r.get("skuInfo");
                    wareSkuEntity.setSkuName((String) skuInfo.get("skuName"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //添加库存信息
            this.baseMapper.insert(wareSkuEntity);
        } else {
            //修改库存信息
            this.baseMapper.addStock(skuId, wareId, skuNum);
        }
    }

    @Override
    public List<SkuHasStockTO> getSkuHasStock(List<Long> skuIds) {
        List<SkuStockDTO> skuStocks = this.baseMapper.getSkuStocks(skuIds);
        return skuStocks.stream().map(s -> {
            SkuHasStockTO skuHasStockTO = new SkuHasStockTO();
            skuHasStockTO.setSkuId(s.getSkuId());
            skuHasStockTO.setHasStock(s.getStock() > 0);
            return skuHasStockTO;
        }).collect(Collectors.toList());
    }

}