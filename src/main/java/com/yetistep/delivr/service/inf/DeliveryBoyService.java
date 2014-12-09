package com.yetistep.delivr.service.inf;

import com.yetistep.delivr.enums.DBoyStatus;
import com.yetistep.delivr.model.DeliveryBoyEntity;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */
public interface DeliveryBoyService {
    public void saveDeliveryBoy(DeliveryBoyEntity deliveryBoy) throws Exception;

    public DeliveryBoyEntity findDeliveryBoyById(Integer id) throws Exception;

    public List<DeliveryBoyEntity> findAllDeliverBoy() throws Exception;

    public Boolean updateDeliveryBoy(DeliveryBoyEntity deliveryBoyEntity) throws Exception;

    public Boolean updateDeliveryBoyStatus(Integer id, DBoyStatus dBoyStatus) throws Exception;
}
