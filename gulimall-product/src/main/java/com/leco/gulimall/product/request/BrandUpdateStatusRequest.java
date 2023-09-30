package com.leco.gulimall.product.request;

import com.leco.common.valid.ListValue;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author greg
 * @version 2023/9/30
 **/
@Data
public class BrandUpdateStatusRequest {
    /**
     * 品牌id
     */
    @NotNull(message = "品牌id不能为空")
    private Long brandId;

    /**
     * 显示状态[0-不显示；1-显示]
     */
    @ListValue(vals = {0,1}, message = "状态值只能是0或1")
    private Integer showStatus;
}
