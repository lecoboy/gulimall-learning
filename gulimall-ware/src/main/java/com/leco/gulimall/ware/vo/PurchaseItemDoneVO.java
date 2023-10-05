package com.leco.gulimall.ware.vo;

import lombok.Data;

/**
 * @author greg
 * @version 2023/10/5
 **/
@Data
public class PurchaseItemDoneVO {
    private Long itemId;

    private Integer status;

    private String reason;
}
