package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.DeliveryBoySelectionEntity;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 12/24/14
 * Time: 11:42 AM
 * To change this template use File | Settings | File Templates.
 */
public interface DeliveryBoySelectionDaoService extends GenericDaoService<Integer, DeliveryBoySelectionEntity> {
    /* Can accept if returned true, i.e. No body has accepted . */
    public Boolean checkOrderAcceptedStatus(Integer orderId) throws Exception;

    public DeliveryBoySelectionEntity getSelectionDetails(Integer orderId, Integer deliveryBoyId) throws Exception;

    public Integer getRemainingOrderSelections(Integer orderId) throws Exception;

    public Boolean updateAllSelectionToRejectMode(Integer orderId) throws Exception;

    public List<DeliveryBoySelectionEntity> getAllSelectionDetails(Integer orderId, Integer deliveryBoyId) throws Exception;

    /* This method returns number of delivery boys who are assigned given order other than given delivery boy */
    public Integer getCountOfDeliveryBoyForOrder(Integer orderId, Integer deliveryBoyId) throws Exception;
}
