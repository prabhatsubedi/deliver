package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.enums.JobOrderStatus;
import com.yetistep.delivr.model.OrderEntity;
import com.yetistep.delivr.model.Page;
import com.yetistep.delivr.model.RatingEntity;
import com.yetistep.delivr.model.mobile.dto.OrderInfoDto;
import com.yetistep.delivr.model.mobile.dto.TrackOrderDto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
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

    public List<Object> get_dBoy_order_history(Integer dBoyId, Page page) throws Exception;

    public List<Object> get_dBoy_order_history(Integer dBoyId, Date fromDate, Date toDate, Page page) throws Exception;

    public Integer getTotalNumberOrderHostory(Integer dBoyId, Date fromDate, Date toDate);

    public TrackOrderDto getTrackOrderInfo(Integer orderId) throws Exception;

    public List<OrderEntity> getStoresOrders(Integer storeId) throws Exception;

    public List<OrderEntity> getStoresOrders(Integer storeId, String fromDate, String toDate) throws Exception;

    public List<Integer> getCustomerRatings(Integer customerId) throws Exception;

    public List<Integer> getDboyRatings(Integer dboyId) throws Exception;

    public Integer hasCustomerRunningOrders(Integer customerId) throws Exception;

    public Integer hasDboyRunningOrders(Integer dboyId) throws Exception;

    public List<OrderEntity> getCustomersOrders(Integer customerId) throws Exception;

    public List<OrderEntity> find(List<Integer> id) throws Exception;

    public List<RatingEntity> getDboyRatingDetails(Integer dboyId) throws Exception;

    public List<OrderEntity> getWalletUnpaidOrders(Integer customerId, Integer orderId) throws Exception;

    public List<OrderEntity> getAllWalletUnpaidOrdersOfCustomer(Integer customerId) throws Exception;

    public OrderEntity findOrderById(Integer id) throws Exception;

    public List<OrderEntity> getAllShortFallOrdersOfCustomer(Integer customerId) throws Exception;

    public List<OrderEntity> getDBoyOrders(Integer dBoyId, String fromDate, String toDate) throws Exception;

    public OrderEntity getPaidFromCashOnDeliveryAmount(Integer orderId) throws Exception;

    public List<OrderEntity> getCancelledPurchasedOrder(Integer dBoyId, Timestamp date) throws Exception;

    public List<OrderEntity> getAllProcessedOrders(List<JobOrderStatus> orderStatuses) throws Exception;

    public Integer getRefereesDeliveredOrders(List<Integer> refereesFacebookId) throws Exception;

    public List<OrderEntity> getShoppersLiveOrders(Integer shopperId) throws Exception;

    public void deleteOrderCancel(Integer orderId) throws Exception;

    public BigDecimal getCurrentOrdersWalletAmount(Integer customerId) throws Exception;

    public BigDecimal getCurrentOrdersCodAmount(Integer customerId) throws Exception;
}
