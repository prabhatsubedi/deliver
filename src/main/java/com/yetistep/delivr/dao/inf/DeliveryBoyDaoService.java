package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.DeliveryBoyEntity;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 12:20 PM
 * To change this template use File | Settings | File Templates.
 */
public interface DeliveryBoyDaoService extends GenericDaoService<Integer, DeliveryBoyEntity> {
    public List<DeliveryBoyEntity> findAllCapableDeliveryBoys() throws Exception;

    public DeliveryBoyEntity getProfileInformation(Integer deliveryBoyId) throws Exception;

    public Boolean checkForPendingOrders(Integer deliveryBoyId, Integer orderId) throws Exception;
}
