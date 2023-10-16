package com.leco.gulimall.common.to;

import lombok.Data;

/**
 * @author greg
 * @version 2023/10/16
 **/
@Data
public class SkuHasStockTO {
    private Long skuId;
    private Boolean hasStock;
}
