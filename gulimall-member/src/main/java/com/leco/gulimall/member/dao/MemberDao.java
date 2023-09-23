package com.leco.gulimall.member.dao;

import com.leco.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author greg
 * @email lecoboy@163.com
 * @date 2023-09-23 18:02:15
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
