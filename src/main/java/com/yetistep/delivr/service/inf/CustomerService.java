package com.yetistep.delivr.service.inf;

import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.model.AddressEntity;
import com.yetistep.delivr.model.CustomerEntity;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 12:15 PM
 * To change this template use File | Settings | File Templates.
 */
public interface CustomerService {
    public void saveCustomer(CustomerEntity customer, HeaderDto headerDto) throws Exception;

    public void addCustomerAddress(HeaderDto headerDto, List<AddressEntity> addresses) throws Exception;

    public void setMobileCode(HeaderDto headerDto) throws Exception;
}
