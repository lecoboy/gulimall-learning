package com.leco.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leco.gulimall.common.utils.PageUtils;
import com.leco.gulimall.product.entity.SpuInfoEntity;
import com.leco.gulimall.product.vo.SpuSaveVO;

import java.util.Map;

/**
 * spu信息
 *
 * @author greg
 * @email lecoboy@163.com
 * @date 2023-09-21 18:44:47
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVO vo);

    PageUtils queryPageByCondtion(Map<String, Object> params);
}

