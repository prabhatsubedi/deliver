package com.yetistep.delivr.service.inf;

import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.model.DeliveryBoyEntity;
import com.yetistep.delivr.model.ItemsOrderEntity;
import com.yetistep.delivr.model.OrderEntity;
import com.yetistep.delivr.model.Page;
import com.yetistep.delivr.model.mobile.dto.PastDeliveriesDto;

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

    public List<OrderEntity> getActiveOrders(Integer deliveryBoyId) throws Exception;

    public Boolean changeOrderStatus(OrderEntity orderEntity, Integer deliveryBoyId) throws Exception;

    public List<PastDeliveriesDto> getPastDeliveries(Page page, Integer deliveryBoyId) throws Exception;

    public Boolean changeDeliveryBoyStatus(DeliveryBoyEntity deliveryBoyEntity) throws Exception;

    public Boolean uploadBills(OrderEntity order, Integer deliveryBoyId) throws Exception;

    public DeliveryBoyEntity getProfileOfDeliveryBoy(Integer deliveryBoyId) throws Exception;

    public Boolean addNewItem(ItemsOrderEntity itemsOrderEntity) throws Exception;
}
