package com.leco.gulimall.product.vo;

import com.leco.gulimall.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

/**
 * @author greg
 * @version 2023/10/3
 **/
@Data
public class AttrGroupWithAttrsVO {
    /**
     * 分组id
     */
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    private List<AttrEntity> attrs;
}
