package com.yetistep.delivr.service.inf;

import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.enums.RatingReason;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.model.mobile.AddressDto;
import com.yetistep.delivr.model.mobile.dto.CheckOutDto;
import com.yetistep.delivr.model.mobile.dto.MyOrderDto;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 12:15 PM
 * To change this template use File | Settings | File Templates.
 */
public interface CustomerService {
    public void login(CustomerEntity customerEntity) throws Exception;

    public void saveCustomer(CustomerEntity customer, HeaderDto headerDto) throws Exception;

    public Integer addCustomerAddress(AddressDto address) throws Exception;

    public AddressDto verifyMobile(String mobile, Long facebookId) throws Exception;

    public void saveOrder(RequestJsonDto requestJson, HeaderDto headerDto) throws Exception;

    public CustomerEntity getCustomerByFbId(Long facebook_id) throws Exception;

    public void registerCustomer(UserEntity user, HeaderDto headerDto) throws Exception;

    public UserEntity getDeliveredAddress(Long facebookId) throws Exception;

    public void deleteDeliveredAddress(Integer addressId) throws Exception;

    public CheckOutDto getCheckOutInfo(Long facebookId, Integer addressId) throws Exception;

    public List<CategoryEntity> getDefaultCategories() throws Exception;

    public Map<String, Object> getCategoryBrands(Integer categoryId, Integer pageNo) throws Exception;

    public List<RatingReason> getRatingReasons() throws Exception;

    public List<MyOrderDto> getMyCurrentOrders(Long facebookId) throws Exception;

    public List<MyOrderDto> getMyPastOrders(Long facebookId) throws Exception;
}
