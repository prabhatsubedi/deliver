package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.DBoyOrderHistoryEntity;
import com.yetistep.delivr.model.Page;
import com.yetistep.delivr.model.mobile.dto.PastDeliveriesDto;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 12/31/14
 * Time: 5:15 PM
 * To change this template use File | Settings | File Templates.
 */
public interface DBoyOrderHistoryDaoService extends GenericDaoService<Integer, DBoyOrderHistoryEntity> {

    public List<DBoyOrderHistoryEntity> find(List<Integer> id) throws Exception;

    public List<PastDeliveriesDto> getPastOrders(Page page, Integer deliveryBoyId) throws Exception;

    public Integer getTotalNumberOfPastDeliveries(Integer deliveryBoyId) throws Exception;

    public DBoyOrderHistoryEntity getOrderHistory(Integer orderId, Integer dboyId) throws Exception;
}
