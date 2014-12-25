package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.StoresBrandEntity;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/23/14
 * Time: 5:24 PM
 * To change this template use File | Settings | File Templates.
 */
public interface StoresBrandDaoService extends GenericDaoService<Integer, StoresBrandEntity>{
    public List<StoresBrandEntity> findFeaturedBrands() throws Exception;
    public List<StoresBrandEntity> findPriorityBrands(String priority) throws Exception;

}
