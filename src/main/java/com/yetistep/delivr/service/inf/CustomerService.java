package com.yetistep.delivr.service.inf;

import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.model.AddressEntity;
import com.yetistep.delivr.model.CustomerEntity;
import com.yetistep.delivr.model.UserEntity;
import com.yetistep.delivr.model.mobile.AddressDto;

import java.util.List;

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
}
