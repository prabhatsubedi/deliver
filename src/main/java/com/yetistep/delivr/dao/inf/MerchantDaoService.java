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

    public void saveItem(ItemEntity item) throws Exception;

    public void updateStoresBrand(StoresBrandEntity value) throws Exception;

    public StoresBrandEntity getBrandByBrandName(String brandName) throws Exception;

    public CategoryEntity getCategoryById(Integer id) throws  Exception;

    public StoreEntity getStoreById(Integer id) throws  Exception;

    public List<CategoryEntity> findParentCategories() throws Exception;

    public BrandsCategoryEntity getBrandsCategory(Integer brandId, Integer categoryId) throws Exception;

    public List<BrandsCategoryEntity> getBrandsCategory(Integer brandId) throws Exception;

    public List<StoreEntity> findStoreByBrand(Integer brandId) throws Exception;

    public List<StoresBrandEntity> findBrandListByMerchant(Integer merchantId) throws Exception;

    public List<StoresBrandEntity> findBrandList() throws Exception;

    public  StoresBrandEntity findBrandDetail(Integer brandId) throws Exception;

    public List<CategoryEntity> findChildCategories(Integer parentId, Integer storeId) throws Exception;

    public void saveCategories(List<CategoryEntity> categories) throws Exception;

    //public List<CategoryEntity> getCategoryList(Integer brandId) throws Exception;

    public void saveItemImages(List<ItemsImageEntity> itemsImageEntities) throws Exception;

    public List<ItemEntity> getCategoriesItems(Integer categoryId, Integer brandId) throws Exception;

    public ItemEntity getItemDetail(Integer itemId) throws Exception;

    public List<ItemsStoreEntity> findItemsStores(Integer storeId) throws Exception;

    public List<CategoryEntity> findFinalCategoryList(Integer brandId) throws Exception;

    public List<ItemEntity> findItemByCategory(List<Integer> categoryId) throws Exception;
}
