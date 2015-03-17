package com.yetistep.delivr.service.inf;

import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.PaginationDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.enums.NotifyTo;
import com.yetistep.delivr.model.*;

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

    public void saveManagerOrAccountant(UserEntity user, HeaderDto headerDto) throws Exception;

    public Boolean updateManagerOrAccountant(UserEntity user, HeaderDto headerDto) throws Exception;

    public UserEntity findUserById(HeaderDto headerDto) throws Exception;

    public List<UserEntity> findAllManagers() throws Exception;

    public List<UserEntity> findAllAccountants() throws Exception;

    public DeliveryBoyEntity updateDboyAccount(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception;

    public DeliveryBoyEntity ackSubmittedAmount(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception;

    public DeliveryBoyEntity walletSubmittedAmount(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception;

    public List<StoresBrandEntity> findFeaturedAndPrioritizedStoreBrands() throws Exception;

    public PaginationDto findNonFeaturedAndPrioritizedStoreBrands(Page page) throws Exception;

    public Boolean updateFeatureAndPriorityOfStoreBrands(List<StoresBrandEntity> storesBrands) throws Exception;

    public Object saveCategory(CategoryEntity category, HeaderDto headerDto) throws Exception;

    public Object updateCategory(CategoryEntity category, HeaderDto headerDto) throws Exception;

    public List<CategoryEntity> getDefaultCategories() throws Exception;

    public CategoryEntity getCategory(HeaderDto headerDto) throws Exception;

    public PaginationDto findInactiveStoreBrands(Page page) throws Exception;

    public Boolean changeOrderSettlement(HeaderDto headerDto) throws Exception;

    public PaginationDto getInactivatedCustomers(RequestJsonDto requestJsonDto) throws Exception;

    public Boolean activateUser(HeaderDto headerDto) throws Exception;

    public Boolean sendPushMessageTo(List<NotifyTo> notifyToList, String message) throws Exception;
}
