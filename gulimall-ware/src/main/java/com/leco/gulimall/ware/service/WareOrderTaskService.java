package com.leco.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leco.common.utils.PageUtils;
import com.leco.gulimall.ware.entity.WareOrderTaskEntity;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author greg
 * @email lecoboy@163.com
 * @date 2023-09-23 18:20:11
 */
public interface WareOrderTaskService extends IService<WareOrderTaskEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

