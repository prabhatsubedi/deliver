package com.yetistep.delivr.service.inf;

import com.yetistep.delivr.dto.HeaderDto;
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
    public void saveDeliveryBoy(DeliveryBoyEntity deliveryBoy, HeaderDto headerDto) throws Exception;

    public DeliveryBoyEntity findDeliveryBoyById(HeaderDto headerDto) throws Exception;

    public List<DeliveryBoyEntity> findAllDeliverBoy() throws Exception;

    public Boolean updateDeliveryBoy(DeliveryBoyEntity deliveryBoyEntity, HeaderDto headerDto) throws Exception;

    public Boolean updateDeliveryBoyStatus(DeliveryBoyEntity deliveryBoyEntity) throws Exception;

    public DeliveryBoyEntity dboyLogin(HeaderDto headerDto) throws Exception;

    public List<DeliveryBoyEntity> getAllCapableDeliveryBoys() throws Exception;

    public Boolean updateLocationOfDeliveryBoy(DeliveryBoyEntity deliveryBoyEntity) throws Exception;

    public Boolean acceptOrder(Integer deliveryBoyId, Integer orderId) throws Exception ;

}
