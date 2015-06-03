package com.yetistep.delivr.service.inf;

import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.PaginationDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.model.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 12:15 PM
 * To change this template use File | Settings | File Templates.
 */
public interface MerchantService {

    public void saveMerchant(MerchantEntity merchant, HeaderDto headerDto) throws Exception;

    public void activateMerchant(MerchantEntity merchantEntity) throws Exception;

    public PaginationDto getMerchants(RequestJsonDto requestJsonDto) throws Exception;

    public void saveStore(RequestJsonDto requestJson, HeaderDto headerDto) throws Exception;

    public List<CategoryEntity> getParentCategories() throws Exception;

    public MerchantEntity getMerchantById(HeaderDto headerDto) throws Exception;

    public Boolean updateMerchant(MerchantEntity merchantEntity, HeaderDto headerDto) throws Exception;

    public List<Object> findBrandList(HeaderDto headerDto) throws Exception;

    public PaginationDto findBrands(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception;

    public StoresBrandEntity findBrandDetail(HeaderDto headerDto) throws Exception;

    public void saveItem(RequestJsonDto requestJson, HeaderDto headerDto) throws Exception;

    public List<StoreEntity> findStoresByBrand(HeaderDto headerDto) throws Exception;

    public List<CategoryEntity> findCategoriesByBrand(HeaderDto headerDto) throws Exception;

    public List<CategoryEntity> findBrandCategoryList(HeaderDto headerDto) throws Exception;

    public List<CategoryEntity> findParentCategoriesByBrand(HeaderDto headerDto) throws Exception;

    public List<CategoryEntity> findChildCategories(RequestJsonDto requestJson) throws Exception;

    public PaginationDto findCategoriesItems(RequestJsonDto requestJson) throws Exception;

    public ItemEntity getItemDetail(HeaderDto headerDto) throws Exception;

    public List<ItemEntity> findStoresItems(HeaderDto headerDto) throws Exception;

    public List<CategoryEntity> findMerchantsDefaultCategory(HeaderDto headerDto) throws Exception;

    public List<CategoryEntity> findParentCategoriesItems(RequestJsonDto requestJson) throws Exception;

    public void updateStore(RequestJsonDto requestJson) throws Exception;

    public void updateItem(RequestJsonDto requestJson) throws Exception;

    public boolean changeStatus(RequestJsonDto requestJsonDto, HeaderDto headerDto) throws Exception;

    public PaginationDto getWebItemSearch(RequestJsonDto requestJsonDto) throws Exception;

    public PaginationDto getOrders(HeaderDto headerDto, RequestJsonDto requestJson) throws Exception;

    public PaginationDto getPurchaseHistory(HeaderDto headerDto, RequestJsonDto requestJson) throws Exception;

    public PaginationDto getOrderItems(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception;

    public void addItemsImages(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception;

    public PaginationDto getInvoices(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception;

    public List<StoresBrandEntity> findSearchBrands(HeaderDto headerDto) throws Exception;

    public List<MerchantEntity> getAllMerchants() throws Exception;

    public void addItemsTags(HeaderDto headerDto, RequestJsonDto requestJson) throws Exception;

    public PaginationDto getInvoices(RequestJsonDto requestJsonDto) throws Exception;
}
