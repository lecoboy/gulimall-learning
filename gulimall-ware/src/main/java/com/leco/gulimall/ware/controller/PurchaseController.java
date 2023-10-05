package com.leco.gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.leco.gulimall.ware.vo.MergeVO;
import com.leco.gulimall.ware.vo.PurchaseDoneVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.leco.gulimall.ware.entity.PurchaseEntity;
import com.leco.gulimall.ware.service.PurchaseService;
import com.leco.gulimall.common.utils.PageUtils;
import com.leco.gulimall.common.utils.R;

import javax.validation.Valid;


/**
 * 采购信息
 *
 * @author greg
 * @email lecoboy@163.com
 * @date 2023-09-23 18:20:11
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("ware:purchase:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }

    ///ware/purchase/unreceive/list
    @GetMapping(value = "/unreceive/list")
    public R unreceiveList(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPageUnreceive(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:purchase:info")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:purchase:save")
    public R save(@RequestBody PurchaseEntity purchase){
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 合并整单
     * @param mergeVo
     * @return
     */
    ///ware/purchase/merge
    @PostMapping(value = "/merge")
    public R merge(@RequestBody MergeVO mergeVo) {

        purchaseService.mergePurchase(mergeVo);

        return R.ok();
    }

    /**
     * 领取采购单
     * @param ids
     * @return
     */
    @PostMapping(value = "/received")
    public R received(@RequestBody List<Long> ids) {

        purchaseService.received(ids);

        return R.ok();
    }

    /**
     * 完成采购单
     * @param doneVo
     * @return
     */
    @PostMapping(value = "/done")
    public R finish(@Valid @RequestBody PurchaseDoneVO doneVo) {

        purchaseService.done(doneVo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:purchase:update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:purchase:delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
