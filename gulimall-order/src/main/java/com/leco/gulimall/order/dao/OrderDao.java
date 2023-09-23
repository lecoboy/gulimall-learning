package com.leco.gulimall.order.dao;

import com.leco.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author greg
 * @email lecoboy@163.com
 * @date 2023-09-23 18:14:27
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
