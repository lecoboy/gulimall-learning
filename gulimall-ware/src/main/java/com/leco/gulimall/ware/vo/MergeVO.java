package com.leco.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author greg
 * @version 2023/10/5
 **/
@Data
public class MergeVO {
    private Long purchaseId;

    private List<Long> items;
}
