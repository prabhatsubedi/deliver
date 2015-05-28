package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.StoreEntity;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/24/14
 * Time: 3:00 PM
 * To change this template use File | Settings | File Templates.
 */
public interface StoreDaoService extends GenericDaoService<Integer, StoreEntity>{
    public List<StoreEntity> findStores(List<Integer> ignoreBrand) throws Exception;

    public List<StoreEntity> findActiveStores(List<Integer> ignoreBrand) throws Exception;

//    public List<CategoryEntity> findItemCategory(Integer brandId) throws Exception;

    public Integer getActiveStores(Integer brandId) throws Exception;

    public List<StoreEntity> findStores(Integer brandId) throws Exception;

    public List<StoreEntity> findActiveStores(Integer brandId) throws Exception;

    public List<StoreEntity> findSearchStores(List<Integer> searchBrands) throws Exception;

}
