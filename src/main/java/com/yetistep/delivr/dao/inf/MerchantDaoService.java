package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.*;

import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 12:21 PM
 * To change this template use File | Settings | File Templates.
 */
public interface MerchantDaoService extends GenericDaoService<Integer, MerchantEntity>{

    public void saveStore(List<StoreEntity> values) throws Exception;

    public void updateStoresBrand(StoresBrandEntity value) throws Exception;

    public StoresBrandEntity getBrandByBrandName(String brandName) throws Exception;

    public CategoryEntity getCategoryById(Integer id) throws  Exception;

    public List<CategoryEntity> findParentCategories() throws Exception;

    public BrandsCategoryEntity getBrandsCategory(Integer brandId, Integer categoryId) throws Exception;

    public List<StoresBrandEntity> findStoresByMerchant(Integer merchant_id) throws Exception;

    public List<StoresBrandEntity> findStores() throws Exception;
}
