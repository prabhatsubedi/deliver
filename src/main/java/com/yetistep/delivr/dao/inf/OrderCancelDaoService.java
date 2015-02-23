package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.OrderCancelEntity;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 2/23/15
 * Time: 12:41 PM
 * To change this template use File | Settings | File Templates.
 */
public interface OrderCancelDaoService extends GenericDaoService<Integer, OrderCancelEntity> {
    public OrderCancelEntity getOrderCancelInfoFromOrderId(Integer orderId) throws Exception;
}
