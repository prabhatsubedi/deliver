package com.yetistep.delivr.service.inf;

import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.PaginationDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.model.CategoryEntity;
import com.yetistep.delivr.model.DeliveryBoyEntity;
import com.yetistep.delivr.model.Page;
import com.yetistep.delivr.model.StoresBrandEntity;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/25/14
 * Time: 10:37 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ManagerService {
    public PaginationDto getActionLog(Page page) throws Exception;

    public DeliveryBoyEntity updateDboyAccount(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception;

    public DeliveryBoyEntity ackSubmittedAmount(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception;

    public DeliveryBoyEntity walletSubmittedAmount(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception;

    public List<StoresBrandEntity> findFeaturedAndPrioritizedStoreBrands() throws Exception;

    public PaginationDto findNonFeaturedAndPrioritizedStoreBrands(Page page) throws Exception;

    public Boolean updateFeatureAndPriorityOfStoreBrands(List<StoresBrandEntity> storesBrands) throws Exception;

    public CategoryEntity saveCategory(CategoryEntity category, HeaderDto headerDto) throws Exception;

    public CategoryEntity updateCategory(CategoryEntity category, HeaderDto headerDto) throws Exception;

    public List<CategoryEntity> getDefaultCategories() throws Exception;

    public CategoryEntity getCategory(HeaderDto headerDto) throws Exception;
}
