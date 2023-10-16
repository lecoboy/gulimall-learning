package com.leco.gulimall.search.service;

import com.leco.gulimall.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @author greg
 * @version 2023/10/16
 **/
public interface ProductSaveService {
    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
