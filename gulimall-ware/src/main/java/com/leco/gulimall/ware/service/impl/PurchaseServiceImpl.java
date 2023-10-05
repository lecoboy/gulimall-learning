package com.leco.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leco.gulimall.common.constant.WareConstant;
import com.leco.gulimall.common.utils.PageUtils;
import com.leco.gulimall.common.utils.Query;
import com.leco.gulimall.ware.dao.PurchaseDao;
import com.leco.gulimall.ware.entity.PurchaseDetailEntity;
import com.leco.gulimall.ware.entity.PurchaseEntity;
import com.leco.gulimall.ware.service.PurchaseDetailService;
import com.leco.gulimall.ware.service.PurchaseService;
import com.leco.gulimall.ware.service.WareSkuService;
import com.leco.gulimall.ware.vo.MergeVO;
import com.leco.gulimall.ware.vo.PurchaseDoneVO;
import com.leco.gulimall.ware.vo.PurchaseItemDoneVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;
    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceive(Map<String, Object> params) {
        QueryWrapper<PurchaseEntity> queryWrapper = new QueryWrapper<PurchaseEntity>()
                .eq("status", 0).or().eq("status", 1);

        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void mergePurchase(MergeVO mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();

        //没有选择任何采购单，将自动创建新单进行合并。
        if (purchaseId == null) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            //设置采购单的默认状态
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());

            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            this.save(purchaseEntity);

            //获取新建采购单的id
            purchaseId = purchaseEntity.getId();

        } else {
            PurchaseEntity purchase = this.getById(purchaseId);
            // 采购需求只能合并到新建和已分配的采购单
            if (!(purchase.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode()
                    || purchase.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode())) {
                throw new IllegalArgumentException("采购需求只能合并到新建和已分配的采购单");
            }
        }

        List<Long> items = mergeVo.getItems();

        // 只有新建和已分配的采购需求可以被合并
        purchaseDetailService.listByIds(items).forEach(e -> {
            if (!(e.getStatus() == WareConstant.PurchaseDetailStatusEnum.CREATED.getCode()
                    || e.getStatus() == WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode())) {
                throw new IllegalArgumentException("只有新建和已分配的采购需求可以被合并");
            }
        });

        Long finalPurchaseId = purchaseId;

        List<PurchaseDetailEntity> collect = items.stream()
                .map(i -> {
                    PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                    purchaseDetailEntity.setId(i);
                    purchaseDetailEntity.setPurchaseId(finalPurchaseId);
                    purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
                    return purchaseDetailEntity;
                }).collect(Collectors.toList());

        //批量修改
        purchaseDetailService.updateBatchById(collect);

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }

    @Override
    public void received(List<Long> ids) {
        //1、确认当前采购单是新建或者是已分配状态
        List<PurchaseEntity> collect = this.baseMapper.selectBatchIds(ids).stream()
                .filter(
                        item -> item.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() ||
                                item.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode())
                .peek(item -> {
                    //改变完状态的采购单
                    item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
                    item.setUpdateTime(new Date());
                }).collect(Collectors.toList());

        //2、改变采购单的状态
        this.updateBatchById(collect);

        //3、改变采购项的状态
        collect.forEach((item) -> {
            List<PurchaseDetailEntity> list = purchaseDetailService.listDetailByPurchaseId(item.getId());
            List<PurchaseDetailEntity> detailEntities = list.stream().map(entity -> {

                PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                purchaseDetailEntity.setId(entity.getId());
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                return purchaseDetailEntity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(detailEntities);

        });
    }

    @Override
    public void done(PurchaseDoneVO doneVo) {
        Long id = doneVo.getId();

        //1、改变采购项的状态
        boolean flag = true;
        List<PurchaseItemDoneVO> items = doneVo.getItems();
        List<PurchaseDetailEntity> updates = new ArrayList<>();

        for (PurchaseItemDoneVO item : items) {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            if (item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()) {
                flag = false;
                purchaseDetailEntity.setReason(item.getReason());
            } else {
                //3、将成功采购的进行入库
                //查出当前采购项的详细信息
                PurchaseDetailEntity entity = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(entity.getSkuId(), entity.getWareId(), entity.getSkuNum());

            }
            purchaseDetailEntity.setId(item.getItemId());
            purchaseDetailEntity.setStatus(item.getStatus());

            updates.add(purchaseDetailEntity);
        }

        //批量更新
        purchaseDetailService.updateBatchById(updates);

        //2、改变采购单状态
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(id);
        purchaseEntity.setStatus(flag ? WareConstant.PurchaseStatusEnum.FINISH.getCode() : WareConstant.PurchaseStatusEnum.HASERROR.getCode());
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }

}