package com.leco.gulimall.product.vo;


import lombok.Data;

import java.math.BigDecimal;

/**
 * @author greg
 * @version 2023/10/3
 **/
@Data
public class MemberPrice {

    private Long id;
    private String name;
    private BigDecimal price;

}