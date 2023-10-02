package com.leco.gulimall.product.vo;

import lombok.Data;

/**
 * @author greg
 * @version 2023/10/2
 **/
@Data
public class AttrRespVO extends AttrVO {
    private String catelogName;

    private String groupName;

    private Long[] catelogPath;
}
