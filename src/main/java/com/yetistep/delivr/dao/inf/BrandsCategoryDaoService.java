package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.BrandsCategoryEntity;
import com.yetistep.delivr.model.StoresBrandEntity;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 2/2/15
 * Time: 11:06 AM
 * To change this template use File | Settings | File Templates.
 */
public interface BrandsCategoryDaoService extends GenericDaoService<Integer, BrandsCategoryEntity>{
    public List<StoresBrandEntity> getCategoryBrands(Integer categoryId) throws Exception;
}
