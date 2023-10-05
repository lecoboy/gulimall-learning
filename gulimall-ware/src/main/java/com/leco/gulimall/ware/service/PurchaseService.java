package com.leco.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leco.gulimall.common.utils.PageUtils;
import com.leco.gulimall.ware.entity.PurchaseEntity;
import com.leco.gulimall.ware.vo.MergeVO;
import com.leco.gulimall.ware.vo.PurchaseDoneVO;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author greg
 * @email lecoboy@163.com
 * @date 2023-09-23 18:20:11
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnreceive(Map<String, Object> params);

    void mergePurchase(MergeVO mergeVo);

    void received(List<Long> ids);

    void done(PurchaseDoneVO doneVo);
}

