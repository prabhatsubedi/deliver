package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.OrderEntity;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 12/24/14
 * Time: 10:34 AM
 * To change this template use File | Settings | File Templates.
 */
public interface OrderDaoService extends GenericDaoService<Integer, OrderEntity> {
    public List<OrderEntity> getActiveOrdersList(Integer deliverBoyId) throws Exception;

    public OrderEntity getLastActiveOrder(Integer deliverBoyId) throws Exception;
}
