package com.leco.gulimall.search.controller;

import com.leco.gulimall.common.exception.BizCodeEnum;
import com.leco.gulimall.common.to.es.SkuEsModel;
import com.leco.gulimall.common.utils.R;
import com.leco.gulimall.search.service.ProductSaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @author greg
 * @version 2023/10/16
 **/
@RestController
@RequestMapping("/search/save")
public class ElasticSaveController {
    @Autowired
    private ProductSaveService productSaveService;


    /**
     * 上架商品
     * @param skuEsModels
     * @return
     */
    @PostMapping(value = "/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels) {
        boolean upSuccess;
        try {
            upSuccess = productSaveService.productStatusUp(skuEsModels);
        } catch (IOException e) {
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(),BizCodeEnum.PRODUCT_UP_EXCEPTION.getMessage());
        }

        if(upSuccess){
            return R.ok();
        }else {
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(),BizCodeEnum.PRODUCT_UP_EXCEPTION.getMessage());
        }

    }
}
