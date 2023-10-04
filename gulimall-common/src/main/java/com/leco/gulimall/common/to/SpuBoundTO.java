package com.leco.gulimall.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author greg
 * @version 2023/10/4
 **/
@Data
public class SpuBoundTO {
    private Long spuId;

    private BigDecimal buyBounds;

    private BigDecimal growBounds;
}
