package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.AddressEntity;
import com.yetistep.delivr.model.CustomerEntity;
import com.yetistep.delivr.model.OrderEntity;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 12:21 PM
 * To change this template use File | Settings | File Templates.
 */
public interface CustomerDaoService extends GenericDaoService<Integer, CustomerEntity> {

    public AddressEntity findAddressById(Integer id) throws Exception;

    public void saveOrder(OrderEntity order) throws Exception;

    public CustomerEntity find(Long facebookId) throws Exception;

    public CustomerEntity findUser(Long facebookId) throws Exception;

    public BigDecimal getRewardsPoint(Long facebookId) throws Exception;
}
