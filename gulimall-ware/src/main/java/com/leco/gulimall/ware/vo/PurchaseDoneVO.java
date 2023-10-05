package com.leco.gulimall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author greg
 * @version 2023/10/5
 **/
@Data
public class PurchaseDoneVO {
    @NotNull(message = "id不能为空")
    private Long id;

    private List<PurchaseItemDoneVO> items;
}
