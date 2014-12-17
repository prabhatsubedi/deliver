package com.yetistep.delivr.service.inf;

import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.model.CategoryEntity;
import com.yetistep.delivr.model.MerchantEntity;
import com.yetistep.delivr.model.StoreEntity;
import com.yetistep.delivr.model.StoresBrandEntity;

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

    public List<MerchantEntity> getMerchants() throws Exception;

    public void saveStore(RequestJsonDto requestJson, HeaderDto headerDto) throws Exception;

    public List<CategoryEntity> getParentCategories() throws Exception;

    public MerchantEntity getMerchantById(HeaderDto headerDto) throws Exception;

    public Boolean updateMerchant(MerchantEntity merchantEntity) throws Exception;

    public List<StoresBrandEntity> findBrandList(HeaderDto headerDto) throws Exception;

    public StoresBrandEntity findBrandBrandDetail(HeaderDto headerDto) throws Exception;

    public void saveItem(RequestJsonDto requestJson, HeaderDto headerDto) throws Exception;

    public List<StoreEntity> findStoresByBrand(HeaderDto headerDto) throws Exception;
}
