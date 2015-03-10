package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.enums.DeliveryStatus;
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

    public Boolean saveStoresBrand(StoresBrandEntity value) throws Exception;

    public void saveItem(ItemEntity item) throws Exception;

    public Boolean updateStoresBrand(StoresBrandEntity value) throws Exception;

    public StoresBrandEntity getBrandByBrandName(String brandName) throws Exception;

    public CategoryEntity getCategoryById(Integer id) throws  Exception;

    public StoreEntity getStoreById(Integer id) throws  Exception;

    public List<CategoryEntity> findParentCategories() throws Exception;

    public BrandsCategoryEntity getBrandsCategory(Integer brandId, Integer categoryId) throws Exception;

    public  List<CategoryEntity> getCategories() throws Exception;

    public List<BrandsCategoryEntity> getBrandsCategory(Integer brandId) throws Exception;

    public List<BrandsCategoryEntity> getBrandsCategory(List<Integer> brandId) throws Exception;

    public List<StoreEntity> findStoreByBrand(Integer brandId) throws Exception;

    public List<StoreEntity> findActiveStoresByBrand(Integer brandId) throws Exception;

    public List<StoresBrandEntity> findBrandListByMerchant(Integer merchantId) throws Exception;

    public List<StoresBrandEntity> findBrandList() throws Exception;

    public List<StoresBrandEntity> findBrandList(Integer merchantId) throws Exception;

    public  StoresBrandEntity findBrandDetail(Integer brandId) throws Exception;

    public List<CategoryEntity> findChildCategories(Integer parentId, Integer storeId) throws Exception;

    public void saveCategories(List<CategoryEntity> categories) throws Exception;

    //public List<CategoryEntity> getCategoryList(Integer brandId) throws Exception;

    public void saveItemImages(List<ItemsImageEntity> itemsImageEntities) throws Exception;

    public List<ItemEntity> getCategoriesItems(Integer categoryId, Integer brandId, Integer itemCount) throws Exception;

    public List<ItemEntity> getCategoriesItems(Integer categoryId, Integer brandId, Page page) throws Exception;

    public List<ItemEntity> getCategoriesItems(Integer categoryId) throws Exception;

    public ItemEntity getItemDetail(Integer itemId) throws Exception;

    public List<ItemsStoreEntity> findItemsStores(Integer storeId) throws Exception;

    public List<CategoryEntity> findFinalCategoryList(Integer brandId) throws Exception;

    public List<CategoryEntity> findFinalCategoryList(List<Integer> brandId) throws Exception;

    public List<ItemEntity> findItemByCategory(List<Integer> categoryId, Integer brandId) throws Exception;

    public List<ItemEntity> findItemByCategory(List<Integer> categoryId, Integer brandId, Integer itemCount) throws Exception;

    public Boolean findPartnerShipStatusFromOrderId(Integer orderId) throws Exception;

    public void updateStores(List<StoreEntity> values) throws Exception;

    public Boolean updateItem(ItemEntity Item) throws Exception;

    public Boolean updateItemImages(List<ItemsImageEntity> ItemsImages) throws Exception;

    public MerchantEntity getMerchantByOrderId(Integer orderId) throws Exception;

    public List<ItemEntity> getWebSearchItem(String searchString, List<Integer> categoryId, List<Integer> storeId, Page page) throws Exception;

    public Integer getTotalNumberOfItems(String searchString, List<Integer> categoryId, List<Integer>  storeId) throws Exception;

    public Integer getTotalNumberOfItems(Integer categoryId, Integer storeId) throws Exception;

    public  List<CategoryEntity> getDefaultCategories() throws Exception;

    public MerchantEntity getCommissionAndVat(Integer merchantId) throws Exception;

    public MerchantEntity getCommissionVatPartnerShipStatus(Integer storeBrandId) throws Exception;

    public List<Integer> getBrandIdList(Integer merchantId) throws Exception;

    public List<Integer> getStoreIdList(List<Integer> brandId) throws Exception;

    public List<OrderEntity> getOrders(List<Integer> storeId, Page page) throws Exception;

    public List<OrderEntity> getOrders(List<Integer> storeId, Page page, DeliveryStatus status) throws Exception;

    public Integer getTotalNumbersOfOrders(List<Integer> storeId) throws Exception;

    public Integer getTotalNumbersOfOrders(List<Integer> storeId, DeliveryStatus status) throws Exception;

    public List<Integer> getBrandIdList() throws Exception;

    public List<OrderEntity> getPurchaseHistory(List<Integer> storeId, Page page) throws Exception;

    public Integer getTotalNumbersOfPurchases(List<Integer> storeId) throws Exception;

    public List<ItemsOrderEntity> getOrdersItems(Integer orderId) throws Exception;

    public Boolean checkEmailExistence(String email) throws Exception;
}
