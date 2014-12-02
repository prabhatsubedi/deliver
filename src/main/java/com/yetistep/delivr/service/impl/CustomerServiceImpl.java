package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.dao.inf.CustomerDaoService;
import com.yetistep.delivr.dao.inf.UserDaoService;
import com.yetistep.delivr.enums.Role;
import com.yetistep.delivr.model.CustomerEntity;
import com.yetistep.delivr.model.RoleEntity;
import com.yetistep.delivr.service.inf.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    CustomerDaoService customerDaoService;

    @Autowired
    UserDaoService userDaoService;

    @Override
    public void saveCustomer(CustomerEntity customer) throws Exception{

            RoleEntity userRole = userDaoService.getRoleByRole(Role.ROLE_CUSTOMER);
            customer.getUser().setRole(userRole);
            //customer.getUser().setCreatedDate(new Date());
            customerDaoService.saveCustomer(customer);


    }
}
