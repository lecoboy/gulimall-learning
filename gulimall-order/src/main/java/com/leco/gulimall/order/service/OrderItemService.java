package com.leco.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leco.common.utils.PageUtils;
import com.leco.gulimall.order.entity.OrderItemEntity;

import java.util.Map;

/**
 * 订单项信息
 *
 * @author greg
 * @email lecoboy@163.com
 * @date 2023-09-23 18:14:27
 */
public interface OrderItemService extends IService<OrderItemEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

