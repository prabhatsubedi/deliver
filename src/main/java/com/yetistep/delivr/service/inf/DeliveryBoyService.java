package com.yetistep.delivr.service.inf;

import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.OrderSummaryDto;
import com.yetistep.delivr.dto.PaginationDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.enums.JobOrderStatus;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.model.mobile.dto.OrderInfoDto;
import com.yetistep.delivr.model.mobile.dto.PreferenceDto;

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

    public PaginationDto findAllDeliverBoy(RequestJsonDto requestJsonDto) throws Exception;

    public Boolean updateDeliveryBoy(DeliveryBoyEntity deliveryBoyEntity, HeaderDto headerDto) throws Exception;

    public Boolean updateDeliveryBoyStatus(DeliveryBoyEntity deliveryBoyEntity) throws Exception;

    public DeliveryBoyEntity dboyLogin(HeaderDto headerDto, UserDeviceEntity userDevice) throws Exception;

    public List<DeliveryBoyEntity> getAllCapableDeliveryBoys() throws Exception;

    public Boolean updateLocationOfDeliveryBoy(DeliveryBoyEntity deliveryBoyEntity) throws Exception;

    public List<OrderInfoDto> getActiveOrders(Integer deliveryBoyId) throws Exception;

    public Boolean changeOrderStatus(OrderEntity orderEntity, Integer deliveryBoyId) throws Exception;

    public PaginationDto getPastDeliveries(Page page, Integer deliveryBoyId) throws Exception;

    public Boolean changeDeliveryBoyStatus(DeliveryBoyEntity deliveryBoyEntity) throws Exception;

    public Boolean uploadBills(OrderEntity order, Integer deliveryBoyId) throws Exception;

    public DeliveryBoyEntity getProfileOfDeliveryBoy(Integer deliveryBoyId) throws Exception;

    public Boolean addNewItem(ItemsOrderEntity itemsOrderEntity) throws Exception;

    public Boolean updateOrders(List<ItemsOrderEntity> itemOrders, Integer orderId) throws Exception;

    public Boolean updateItemOrderByItemOrderId(ItemsOrderEntity itemOrder, Integer orderId) throws Exception;

    public Boolean cancelOrder(OrderEntity order, Integer userId) throws Exception;

    public OrderSummaryDto getShoppingList(Integer orderId) throws Exception;

    public OrderEntity getOrderById(Integer orderId, Integer deliveryBoyId) throws Exception;

    public JobOrderStatus getJobOrderStatusFromOrderId(Integer orderId) throws Exception;

    public List<ReasonDetailsEntity> getCancelReasonList() throws Exception;

    public ItemsOrderEntity getItemOrderById(Integer itemOrderId) throws Exception;

    public PaginationDto get_order_history(Integer dBoyId, RequestJsonDto requestJsonDto) throws Exception;

    public Boolean rejectOrder(Integer deliveryBoyId, Integer orderId) throws Exception;

    public PreferenceDto getAcceptanceDetails() throws Exception;

    public PaginationDto getAdvanceAmounts(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception;
}
