package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.enums.JobOrderStatus;
import com.yetistep.delivr.model.OrderEntity;
import com.yetistep.delivr.model.mobile.dto.OrderInfoDto;
import com.yetistep.delivr.model.mobile.dto.TrackOrderDto;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 12/24/14
 * Time: 10:34 AM
 * To change this template use File | Settings | File Templates.
 */
public interface OrderDaoService extends GenericDaoService<Integer, OrderEntity> {
    public List<OrderInfoDto> getActiveOrdersList(Integer deliverBoyId) throws Exception;

    public List<OrderInfoDto> getAssignedOrders(Integer deliveryBoyId) throws Exception;

    public OrderEntity getLastActiveOrder(Integer deliverBoyId) throws Exception;

    public JobOrderStatus getJobOrderStatus(Integer orderId) throws Exception;

    public List<OrderEntity> getElapsedOrders(Integer timeDuration) throws Exception;

    public OrderEntity getNextPendingOrder() throws Exception;

    public List<Object> get_dBoy_order_history(Integer dBoyId) throws Exception;

    public TrackOrderDto getTrackOrderInfo(Integer orderId) throws Exception;

}
