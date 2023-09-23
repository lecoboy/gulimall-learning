package com.leco.gulimall.order.dao;

import com.leco.gulimall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 * 
 * @author greg
 * @email lecoboy@163.com
 * @date 2023-09-23 18:14:27
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
	
}
