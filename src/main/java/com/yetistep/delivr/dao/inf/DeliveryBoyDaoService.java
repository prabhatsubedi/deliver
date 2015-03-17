package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.DeliveryBoyEntity;
import com.yetistep.delivr.model.Page;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 12:20 PM
 * To change this template use File | Settings | File Templates.
 */
public interface DeliveryBoyDaoService extends GenericDaoService<Integer, DeliveryBoyEntity> {
    public List<DeliveryBoyEntity> findAllCapableDeliveryBoys(Integer updateLocationTimeOut) throws Exception;

    public DeliveryBoyEntity getProfileInformation(Integer deliveryBoyId) throws Exception;

    public Boolean checkForPendingOrders(Integer deliveryBoyId, Integer orderId) throws Exception;

    public Integer getNumberOfAssignedOrders(Integer deliveryBoyId) throws Exception;

    public List<DeliveryBoyEntity> findAll(Page page) throws Exception;

    public Boolean canStartJob(Integer orderId, Integer deliveryBoyId) throws Exception;

    public Boolean updateAverageRating(BigDecimal averageRating, Integer dboyId) throws Exception;

    public Boolean checkIfLicenseNumberExists(String licenseNumber) throws Exception;

    public Integer getTotalNumberOfDboys() throws Exception;

    public Boolean updatePreviousDayDueAmount() throws Exception;

}
