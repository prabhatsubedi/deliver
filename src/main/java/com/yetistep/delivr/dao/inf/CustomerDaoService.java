package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.CustomerEntity;
import com.yetistep.delivr.model.OrderEntity;
import com.yetistep.delivr.model.Page;
import com.yetistep.delivr.model.TestEntity;
import com.yetistep.delivr.model.mobile.dto.MyOrderDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 12:21 PM
 * To change this template use File | Settings | File Templates.
 */
public interface CustomerDaoService extends GenericDaoService<Integer, CustomerEntity> {

    public void saveOrder(OrderEntity order) throws Exception;

    public CustomerEntity find(Long facebookId) throws Exception;

    public CustomerEntity findUser(Long facebookId) throws Exception;

    public BigDecimal getRewardsPoint(Long facebookId) throws Exception;

    public CustomerEntity getCustomerIdAndRewardFromFacebookId(Long facebookId) throws Exception;

    public List<MyOrderDto> getCurrentOrdersByFacebookId(Long facebookId) throws Exception;

    public Integer getNumberOfPastOrdersByFacebookId(Long facebookId) throws Exception;

    public List<MyOrderDto> getPastOrdersByFacebookId(Long facebookId, Page page) throws Exception;

    public CustomerEntity getLatLong(Long facebookId) throws Exception;

    public CustomerEntity getCustomerProfile(Long facebookId) throws Exception;

    public Boolean updateLatLong(String lat, String lon, Long facebookId) throws Exception;

    public Boolean updateAverageRating(BigDecimal averageRating, Integer customerId) throws Exception;

    public CustomerEntity getCustomerStatus(Long facebookId) throws Exception;

    public Boolean saveTest(TestEntity testEntity) throws Exception;

    public TestEntity findTest(Integer id) throws Exception;
}
