package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.Page;
import com.yetistep.delivr.model.StoresBrandEntity;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/23/14
 * Time: 5:24 PM
 * To change this template use File | Settings | File Templates.
 */
public interface StoresBrandDaoService extends GenericDaoService<Integer, StoresBrandEntity> {
    public List<StoresBrandEntity> findFeaturedBrands() throws Exception;

    public List<StoresBrandEntity> findPriorityBrands(String priority) throws Exception;

    public List<StoresBrandEntity> findFeaturedAndPriorityBrands() throws Exception;

    public Integer getTotalNumberOfStoreBrands() throws Exception;

    public List<StoresBrandEntity> findExceptFeaturedAndPriorityBrands(Page page) throws Exception;

    public Boolean updateFeatureAndPriorityOfStoreBrands(List<StoresBrandEntity> storeBrands) throws Exception;

    public Integer getMerchantId(Integer brandsId) throws Exception;

    public Integer getTotalNumberOfInactiveStoreBrands() throws Exception;

    public List<StoresBrandEntity> findInactiveStoreBrands(Page page) throws Exception;

    public List<StoresBrandEntity> findStoresBrand(Boolean isPriority, Integer... brandId) throws Exception;

    public List<Integer> getSearchBrands(String word, Integer limit) throws Exception;

    public List<Integer> getActiveSearchBrands(String word, Integer limit) throws Exception;

    public List<StoresBrandEntity> findActiveFeaturedBrands() throws Exception;

    public List<StoresBrandEntity> findActivePriorityBrands(String priority) throws Exception;

    public List<StoresBrandEntity> findActivePriorityExceptFeaturedBrands(String priority) throws Exception;

}
