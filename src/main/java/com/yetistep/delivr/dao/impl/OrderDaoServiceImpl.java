package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.OrderDaoService;
import com.yetistep.delivr.enums.JobOrderStatus;
import com.yetistep.delivr.enums.PaymentMode;
import com.yetistep.delivr.model.OrderEntity;
import com.yetistep.delivr.model.Page;
import com.yetistep.delivr.model.RatingEntity;
import com.yetistep.delivr.model.mobile.dto.OrderInfoDto;
import com.yetistep.delivr.model.mobile.dto.TrackOrderDto;
import com.yetistep.delivr.util.DateUtil;
import com.yetistep.delivr.util.HibernateUtil;
import org.hibernate.*;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.TypeLocatorImpl;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 12/24/14
 * Time: 10:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class OrderDaoServiceImpl implements OrderDaoService {
    @Autowired
    SessionFactory sessionFactory;

    @Override
    public OrderEntity find(Integer id) throws Exception {
        return (OrderEntity) getCurrentSession().get(OrderEntity.class, id);
    }

    @Override
    public OrderEntity findOrderById(Integer id) throws Exception {
        List<OrderEntity> orders = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OrderEntity.class);
        criteria.add(Restrictions.eq("id", id));
        orders = criteria.list();
        return orders.size() > 0 ? orders.get(0) : null;
    }

    @Override
    public List<OrderEntity> find(List<Integer> id) throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(OrderEntity.class);
        criteria.add(Restrictions.in("id", id));
        return (List<OrderEntity>) criteria.list();
    }

    @Override
    public List<OrderEntity> findAll() throws Exception {
        return (List<OrderEntity>) getCurrentSession().createCriteria(OrderEntity.class).list();
    }

    @Override
    public Boolean save(OrderEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean update(OrderEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean delete(OrderEntity value) throws Exception {
        getCurrentSession().delete(value);
        return true;
    }

    @Override
    public Session getCurrentSession() throws Exception {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<OrderInfoDto> getActiveOrdersList(Integer deliverBoyId) throws Exception {
        String sqlQuery = "SELECT DISTINCT o.id as id, o.order_name as orderName, o.order_status as orderStatus, o.order_date as orderDate, " +
                "dbs.paid_to_courier as paidToCourier, dbs.customer_chargeable_distance as customerChargeableDistance, " +
                "dbs.system_chargeable_distance as systemChargeableDistance, dbs.time_required as assignedTime, " +
                "dbs.total_time_required as remainingTime, dbh.order_accepted_at as orderAcceptedAt FROM " +
                "orders o INNER JOIN delivery_boys db on (db.id = o.delivery_boy_id) INNER JOIN " +
                "dboy_selections dbs on (dbs.order_id = o.id AND dbs.dboy_id = db.id) INNER JOIN " +
                "dboy_order_history dbh on(dbh.order_id = o.id) WHERE o.order_status in " +
                "(:orderAccepted, :inRouteToPickUp, :atStore, :inRouteToDelivery) AND dbs.rejected = false " +
                "AND o.delivery_boy_id = :deliveryBoyId order by orderStatus desc , dbh.order_accepted_at asc";
        Properties params = new Properties();
        params.put("enumClass", "com.yetistep.delivr.enums.JobOrderStatus");
        /*type 12 instructs to use the String representation of enum value*/
        params.put("type", "12");
        Type myEnumType = new TypeLocatorImpl(new TypeResolver()).custom(org.hibernate.type.EnumType.class, params);

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery)
                .addScalar("id", IntegerType.INSTANCE)
                .addScalar("orderName", StringType.INSTANCE)
                .addScalar("orderStatus", myEnumType)
                .addScalar("orderDate", TimestampType.INSTANCE)
                .addScalar("paidToCourier", BigDecimalType.INSTANCE)
                .addScalar("customerChargeableDistance", BigDecimalType.INSTANCE)
                .addScalar("systemChargeableDistance", BigDecimalType.INSTANCE)
                .addScalar("assignedTime", IntegerType.INSTANCE)
                .addScalar("remainingTime", IntegerType.INSTANCE)
                .addScalar("orderAcceptedAt", TimestampType.INSTANCE);
        query.setParameter("orderAccepted", JobOrderStatus.ORDER_ACCEPTED.ordinal());
        query.setParameter("inRouteToPickUp", JobOrderStatus.IN_ROUTE_TO_PICK_UP.ordinal());
        query.setParameter("atStore", JobOrderStatus.AT_STORE.ordinal());
        query.setParameter("inRouteToDelivery", JobOrderStatus.IN_ROUTE_TO_DELIVERY.ordinal());
        query.setParameter("deliveryBoyId", deliverBoyId);
        query.setResultTransformer(Transformers.aliasToBean(OrderInfoDto.class));
        return (List<OrderInfoDto>) query.list();
    }

    @Override
    public List<OrderInfoDto> getAssignedOrders(Integer deliveryBoyId) throws Exception {
        String sqlQuery = "SELECT o.id as id, o.order_name as orderName, o.order_status as orderStatus, " +
                "o.order_date as orderDate, dbs.paid_to_courier as paidToCourier, dbs.customer_chargeable_distance " +
                "as customerChargeableDistance, dbs.system_chargeable_distance as systemChargeableDistance, " +
                "dbs.time_required as assignedTime, dbs.total_time_required as remainingTime FROM orders o INNER JOIN " +
                "dboy_selections dbs on (dbs.order_id = o.id) WHERE dbs.rejected = :rejected AND  o.order_status =:orderPlaced " +
                "AND dbs.dboy_id = :deliveryBoyId order by id asc";
        Properties params = new Properties();
        params.put("enumClass", "com.yetistep.delivr.enums.JobOrderStatus");
        /*type 12 instructs to use the String representation of enum value*/
        params.put("type", "12");
        Type myEnumType = new TypeLocatorImpl(new TypeResolver()).custom(org.hibernate.type.EnumType.class, params);

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery)
                .addScalar("id", IntegerType.INSTANCE)
                .addScalar("orderName", StringType.INSTANCE)
                .addScalar("orderStatus", myEnumType)
                .addScalar("orderDate", TimestampType.INSTANCE)
                .addScalar("paidToCourier", BigDecimalType.INSTANCE)
                .addScalar("customerChargeableDistance", BigDecimalType.INSTANCE)
                .addScalar("systemChargeableDistance", BigDecimalType.INSTANCE)
                .addScalar("assignedTime", IntegerType.INSTANCE)
                .addScalar("remainingTime", IntegerType.INSTANCE);
        query.setParameter("rejected", false);
        query.setParameter("orderPlaced", JobOrderStatus.ORDER_PLACED.ordinal());
        query.setParameter("deliveryBoyId", deliveryBoyId);
        query.setResultTransformer(Transformers.aliasToBean(OrderInfoDto.class));
        return (List<OrderInfoDto>) query.list();
    }

    @Override
    public OrderEntity getLastActiveOrder(Integer deliverBoyId) throws Exception {
        List<JobOrderStatus> jobOrderStatusList = new ArrayList<JobOrderStatus>();
        jobOrderStatusList.add(JobOrderStatus.ORDER_ACCEPTED);
        jobOrderStatusList.add(JobOrderStatus.IN_ROUTE_TO_PICK_UP);
        jobOrderStatusList.add(JobOrderStatus.AT_STORE);
        jobOrderStatusList.add(JobOrderStatus.IN_ROUTE_TO_DELIVERY);

        Criteria criteria = getCurrentSession().createCriteria(OrderEntity.class)
                .add(Restrictions.eq("deliveryBoy.id", deliverBoyId))
                .add(Restrictions.in("orderStatus", jobOrderStatusList))
                .addOrder(Order.desc("id"))
                .setMaxResults(1);
        return (OrderEntity) criteria.uniqueResult();
    }

    @Override
    public JobOrderStatus getJobOrderStatus(Integer orderId) throws Exception {
        String sqlQuery = "SELECT o.order_status FROM orders o where o.id =:orderId";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
        query.setParameter("orderId", orderId);
        Integer orderStatus = (Integer) query.uniqueResult();
        return JobOrderStatus.fromInt(orderStatus);
    }

    @Override
    public List<OrderEntity> getElapsedOrders(Integer timeDuration) throws Exception {
//        Criteria criteria = getCurrentSession().createCriteria(OrderEntity.class);
//        criteria.add(Restrictions.eq("orderStatus", JobOrderStatus.ORDER_PLACED))
//        .add(Restrictions.le("orderDate", DateUtil.subtractSeconds(DateUtil.getCurrentTimestampSQL(), timeDuration)));
//        List<OrderEntity>  orderEntities = criteria.list();

        String sql = "SELECT id, order_date as orderDate, reprocess_time as reprocessTime FROM orders " +
                "WHERE order_status = :orderPlaced AND order_date <= :orderDate";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
        query.setParameter("orderPlaced", JobOrderStatus.ORDER_PLACED.ordinal());
        query.setParameter("orderDate", DateUtil.subtractSeconds(DateUtil.getCurrentTimestampSQL(), timeDuration));
        query.setResultTransformer(Transformers.aliasToBean(OrderEntity.class));
        return (List<OrderEntity>)  query.list();
    }

    @Override
    public OrderEntity getNextPendingOrder() throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(OrderEntity.class);
        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("orderDate"), "orderDate")
                .add(Projections.property("reprocessTime"), "reprocessTime")
        ).setResultTransformer(Transformers.aliasToBean(OrderEntity.class));
        criteria.add(Restrictions.eq("orderStatus", JobOrderStatus.ORDER_PLACED))
                .addOrder(Order.asc("orderDate"));
        List<OrderEntity>  orderEntities = criteria.list();
        if(orderEntities.size() > 0){
            return orderEntities.get(0);
        }
        return null;
    }


    /*
    Created by Sagar Sapkota
    * */
    @Override
    public List<Object> get_dBoy_order_history(Integer dBoyId, Page page) throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(OrderEntity.class, "order");
        List<JobOrderStatus> orderStatuses = new ArrayList<>();
        orderStatuses.add(JobOrderStatus.DELIVERED);
        orderStatuses.add(JobOrderStatus.CANCELLED);
        criteria.add(Restrictions.and(Restrictions.in("orderStatus", orderStatuses), Restrictions.eq("deliveryBoy.id", dBoyId)));
        HibernateUtil.fillPaginationCriteria(criteria, page, OrderEntity.class);
        return (List<Object>) criteria.list();
    }

    @Override
    public List<Object> get_dBoy_order_history(Integer dBoyId, Date fromDate, Date toDate, Page page) throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(OrderEntity.class, "order");
        List<JobOrderStatus> orderStatuses = new ArrayList<>();
        orderStatuses.add(JobOrderStatus.DELIVERED);
        orderStatuses.add(JobOrderStatus.CANCELLED);
        criteria.add(Restrictions.and(Restrictions.in("orderStatus", orderStatuses), Restrictions.eq("deliveryBoy.id", dBoyId), Restrictions.between("orderDate", fromDate, toDate)));
        HibernateUtil.fillPaginationCriteria(criteria, page, OrderEntity.class);
        return (List<Object>) criteria.list();
    }

    @Override
    public Integer getTotalNumberOrderHostory(Integer dBoyId, Date fromDate, Date toDate) {
        String sqQuery;
        if(fromDate != null && toDate != null){
            sqQuery =    "SELECT COUNT(o.id) FROM orders o WHERE  o.delivery_boy_id =:dBoyId && o.order_date >:fromDate && o.order_date <=:toDate && o.order_status in(:orderStatus) ";
        }else{
            sqQuery =    "SELECT COUNT(o.id) FROM orders o WHERE  o.delivery_boy_id =:dBoyId && o.order_status in(:orderStatus) ";
        }
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqQuery);
        List<Integer> orderStatuses = new ArrayList<>();
        orderStatuses.add(JobOrderStatus.DELIVERED.ordinal());
        orderStatuses.add(JobOrderStatus.CANCELLED.ordinal());
        query.setParameter("dBoyId", dBoyId);

        if(fromDate != null && toDate != null){
            query.setParameter("fromDate", fromDate);
            query.setParameter("toDate", toDate);
        }

        query.setParameterList("orderStatus", orderStatuses);
        BigInteger cnt = (BigInteger) query.uniqueResult();
        return cnt.intValue();
    }

    @Override
    public TrackOrderDto getTrackOrderInfo(Integer orderId) throws Exception {
        String sqlQuery = "SELECT o.id as orderId, o.order_status as orderStatus, db.latitude as courierBoyLatitude, db.longitude " +
                "as courierBoyLongitude, db.vehicle_type as vehicleType, s.latitude as storeLatitude, s.longitude as storeLongitude, " +
                "u.full_name as courierBoyName, u.profile_image as courierBoyImage, u.mobile_number as courierBoyContactNo, " +
                "a.latitude as deliveryLatitude, a.longitude as deliveryLongitude FROM orders o LEFT JOIN delivery_boys db " +
                "on (o.delivery_boy_id = db.id) INNER JOIN stores s on (s.id = o.store_id) LEFT JOIN users u " +
                "on (u.id = db.user_id)  INNER JOIN address a on(o.address_id = a.id) where o.id = :orderID";
        Properties params = new Properties();
        params.put("enumClass", "com.yetistep.delivr.enums.JobOrderStatus");
        /*type 12 instructs to use the String representation of enum value*/
        params.put("type", "12");
        Type myEnumType = new TypeLocatorImpl(new TypeResolver()).custom(org.hibernate.type.EnumType.class, params);

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery)
                .addScalar("orderId", IntegerType.INSTANCE)
                .addScalar("orderStatus", myEnumType)
                .addScalar("courierBoyName", StringType.INSTANCE)
                .addScalar("courierBoyImage", StringType.INSTANCE)
                .addScalar("courierBoyContactNo", StringType.INSTANCE)
                .addScalar("courierBoyLatitude", StringType.INSTANCE)
                .addScalar("courierBoyLongitude", StringType.INSTANCE)
                .addScalar("vehicleType", IntegerType.INSTANCE)
                .addScalar("storeLatitude", StringType.INSTANCE)
                .addScalar("storeLongitude", StringType.INSTANCE)
                .addScalar("deliveryLatitude", StringType.INSTANCE)
                .addScalar("deliveryLongitude", StringType.INSTANCE);
        query.setParameter("orderID", orderId);
        query.setResultTransformer(Transformers.aliasToBean(TrackOrderDto.class));
        return (TrackOrderDto) query.uniqueResult();
    }

    @Override
    public List<OrderEntity> getStoresOrders(Integer storeId) throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(OrderEntity.class);
        criteria.add(Restrictions.eq("orderStatus", JobOrderStatus.DELIVERED))
                .add(Restrictions.eq("store.id", storeId));
        return (List<OrderEntity>) criteria.list();
    }

    @Override
    public List<OrderEntity> getStoresOrders(Integer storeId, String fromDate, String toDate) throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(OrderEntity.class);
        criteria.add(Restrictions.and(Restrictions.eq("orderStatus", JobOrderStatus.DELIVERED), Restrictions.eq("store.id", storeId), Restrictions.gt("orderDate", new SimpleDateFormat("yyyy-MM-dd").parse(fromDate)), Restrictions.le("orderDate", new SimpleDateFormat("yyyy-MM-dd").parse(toDate))));
        return (List<OrderEntity>) criteria.list();
    }

    @Override
    public List<OrderEntity> getDBoyOrders(Integer dBoyId, String fromDate, String toDate) throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(OrderEntity.class);
        criteria.add(Restrictions.and(Restrictions.eq("orderStatus", JobOrderStatus.DELIVERED), Restrictions.eq("deliveryBoy.id", dBoyId), Restrictions.gt("orderDate", new SimpleDateFormat("yyyy-MM-dd").parse(fromDate)), Restrictions.le("orderDate", new SimpleDateFormat("yyyy-MM-dd").parse(toDate))));
        return (List<OrderEntity>) criteria.list();
    }


    @Override
    public List<OrderEntity> getCustomersOrders(Integer customerId) throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(OrderEntity.class);
        criteria.add(Restrictions.eq("orderStatus", JobOrderStatus.DELIVERED))
                .add(Restrictions.eq("customer.id", customerId));
        return (List<OrderEntity>) criteria.list();
    }

    @Override
    public List<OrderEntity> getShoppersLiveOrders(Integer shopperId) throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(OrderEntity.class);
        List<JobOrderStatus> activeStatuses = new ArrayList<>();
        activeStatuses.add(JobOrderStatus.AT_STORE);
        activeStatuses.add(JobOrderStatus.ORDER_ACCEPTED);
        activeStatuses.add(JobOrderStatus.IN_ROUTE_TO_DELIVERY);
        activeStatuses.add(JobOrderStatus.IN_ROUTE_TO_PICK_UP);

        criteria.add(Restrictions.in("orderStatus", activeStatuses))
                .add(Restrictions.eq("deliveryBoy.id", shopperId));
        return (List<OrderEntity>) criteria.list();
    }


    @Override
    public List<Integer> getCustomerRatings(Integer customerId) throws Exception {
        String sql = "SELECT r.customer_rating FROM orders o " +
                "INNER JOIN ratings r ON(r.order_id = o.id AND r.customer_rating IS NOT NULL) " +
                "WHERE o.customer_id =:customerId " +
                "ORDER BY o.order_date DESC LIMIT 10";

        SQLQuery query = getCurrentSession().createSQLQuery(sql);
        query.setParameter("customerId", customerId);
        return (List<Integer>) query.list();
    }



    @Override
    public List<Integer> getDboyRatings(Integer dboyId) throws Exception {
        String sql = "SELECT r.dboy_rating FROM orders o " +
                "INNER JOIN ratings r ON(r.order_id = o.id AND r.dboy_rating IS NOT NULL) " +
                "WHERE o.delivery_boy_id =:dboyId " +
                "ORDER BY o.order_date DESC LIMIT 10";

        SQLQuery query = getCurrentSession().createSQLQuery(sql);
        query.setParameter("dboyId", dboyId);
        return (List<Integer>) query.list();
    }

    public List<RatingEntity> getDboyRatingDetails(Integer dboyId) throws Exception {

        String sql = "SELECT r.dboy_rating AS deliveryBoyRating, r.order_id AS orderId FROM orders o " +
                "INNER JOIN ratings r ON(r.order_id = o.id AND r.dboy_rating IS NOT NULL) " +
                "WHERE o.delivery_boy_id =:dboyId " +
                "ORDER BY o.order_date DESC LIMIT 10";

        SQLQuery query = getCurrentSession().createSQLQuery(sql);
        query.setParameter("dboyId", dboyId);
        query.setResultTransformer(Transformers.aliasToBean(RatingEntity.class));
        return (List<RatingEntity>) query.list();
    }

    @Override
    public Integer hasCustomerRunningOrders(Integer customerId) throws Exception {
           return hasRunningOrders(customerId, null);
    }

    @Override
    public Integer hasDboyRunningOrders(Integer dboyId) throws Exception {
        return hasRunningOrders(null, dboyId);
    }

    private Integer hasRunningOrders(Integer customerId, Integer dboyId) throws Exception{
        String sql = "SELECT COUNT(id) FROM orders WHERE order_status NOT IN(:jobOrderList) ";
        if(customerId != null)
            sql = sql +  "AND customer_id =:customerId";
        else if(dboyId != null)
            sql = sql +  "AND delivery_boy_id =:dboyId";


        SQLQuery query = getCurrentSession().createSQLQuery(sql);

        if(customerId != null)
            query.setParameter("customerId", customerId);
        else if(dboyId != null)
            query.setParameter("dboyId", dboyId);

        List<Integer> jobOrderList = new ArrayList<>();
        jobOrderList.add(JobOrderStatus.DELIVERED.ordinal());
        jobOrderList.add(JobOrderStatus.CANCELLED.ordinal());
        query.setParameterList("jobOrderList", jobOrderList);
        return ((Number) query.uniqueResult()).intValue();
    }

    @Override
    public List<OrderEntity> getWalletUnpaidOrders(Integer customerId, Integer orderId) throws Exception {
        List<JobOrderStatus> jobOrderStatusList = new ArrayList<JobOrderStatus>();
        jobOrderStatusList.add(JobOrderStatus.ORDER_PLACED);
        jobOrderStatusList.add(JobOrderStatus.ORDER_ACCEPTED);
        jobOrderStatusList.add(JobOrderStatus.IN_ROUTE_TO_PICK_UP);
        jobOrderStatusList.add(JobOrderStatus.AT_STORE);
        jobOrderStatusList.add(JobOrderStatus.IN_ROUTE_TO_DELIVERY);

        Criteria criteria = getCurrentSession().createCriteria(OrderEntity.class);
        criteria.add(Restrictions.in("orderStatus", jobOrderStatusList))
                .add(Restrictions.eq("paymentMode", PaymentMode.WALLET))
                .add(Restrictions.gt("paidFromCOD", BigDecimal.ZERO))
                .add(Restrictions.eq("customer.id", customerId))
                .add(Restrictions.not(Restrictions.eq("id", orderId)));
        return (List<OrderEntity>) criteria.list();
    }

    @Override
    public List<OrderEntity> getAllWalletUnpaidOrdersOfCustomer(Integer customerId) throws Exception {
        List<JobOrderStatus> jobOrderStatusList = new ArrayList<JobOrderStatus>();
        jobOrderStatusList.add(JobOrderStatus.IN_ROUTE_TO_DELIVERY);

        Criteria criteria = getCurrentSession().createCriteria(OrderEntity.class);
        criteria.add(Restrictions.in("orderStatus", jobOrderStatusList))
                .add(Restrictions.gt("paidFromCOD", BigDecimal.ZERO))
                .add(Restrictions.eq("customer.id", customerId));
        return (List<OrderEntity>) criteria.list();
    }

    @Override
    public List<OrderEntity> getAllShortFallOrdersOfCustomer(Integer customerId) throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(OrderEntity.class);
        criteria.add(Restrictions.gt("shortFallAmount", BigDecimal.ZERO))
                .add(Restrictions.eq("customer.id", customerId));
        return (List<OrderEntity>) criteria.list();
    }

    @Override
    public OrderEntity getPaidFromCashOnDeliveryAmount(Integer orderId) throws Exception {
        String sql = "SELECT id, paid_from_cod as paidFromCOD FROM orders WHERE id = :orderId";
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setParameter("orderId", orderId);
        sqlQuery.setResultTransformer(Transformers.aliasToBean(OrderEntity.class));
        return (OrderEntity) sqlQuery.uniqueResult();
    }

    @Override
    public List<OrderEntity> getCancelledPurchasedOrder(Integer dBoyId, Timestamp date) throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(OrderEntity.class, "order");
        criteria.add(Restrictions.gt("totalCost", BigDecimal.ZERO));
        criteria.add(Restrictions.eq("deliveryBoy.id", dBoyId));
        if(date != null)
            criteria.add(Restrictions.gt("orderDate", date));
        criteria.add(Restrictions.eq("orderStatus", JobOrderStatus.CANCELLED));
        return (List<OrderEntity>) criteria.list();
    }


    @Override
    public List<OrderEntity> getAllProcessedOrders(List<JobOrderStatus> orderStatuses) throws Exception{
        Criteria criteria = getCurrentSession().createCriteria(OrderEntity.class, "order");
        criteria.createAlias("order.store", "store");
        criteria.createAlias("store.storesBrand", "storesBrand");
        criteria.createAlias("storesBrand.merchant", "merchant");
        criteria.add(Restrictions.in("orderStatus", orderStatuses));
        //criteria.add(Restrictions.eq("merchant.partnershipStatus", false));
        return (List<OrderEntity>) criteria.list();
    }

    @Override
    public Integer getRefereesDeliveredOrders(List<Integer> refereesFacebookId) throws Exception {
        String sql = "SELECT COUNT(DISTINCT customer_id) FROM orders WHERE customer_id IN(:refereesFacebookId) && order_status = 5";
        SQLQuery query = getCurrentSession().createSQLQuery(sql);
        query.setParameterList("refereesFacebookId", refereesFacebookId);
        return ((Number) query.uniqueResult()).intValue();
    }

    @Override
    public void deleteOrderCancel(Integer orderId) throws Exception {
        String sql = "delete from order_cancel where order_id =:orderId";
        SQLQuery query = getCurrentSession().createSQLQuery(sql);
        query.setParameter("orderId", orderId);
        query.executeUpdate();
    }

    @Override
    public BigDecimal getCurrentOrdersWalletAmount(Integer customerId) throws Exception {
        List<Integer> jobOrderStatusList = new ArrayList<Integer>();
        jobOrderStatusList.add(JobOrderStatus.ORDER_PLACED.ordinal());
        jobOrderStatusList.add(JobOrderStatus.ORDER_ACCEPTED.ordinal());
        jobOrderStatusList.add(JobOrderStatus.IN_ROUTE_TO_PICK_UP.ordinal());
        jobOrderStatusList.add(JobOrderStatus.AT_STORE.ordinal());
        String sql = "SELECT SUM(paid_from_wallet) FROM orders WHERE order_status IN(:jobOrderStatusList) AND customer_id =:customerId";
        SQLQuery query = getCurrentSession().createSQLQuery(sql);
        query.setParameter("customerId", customerId);
        query.setParameterList("jobOrderStatusList", jobOrderStatusList);
        Number walletAmount = (Number) query.uniqueResult();
        return walletAmount != null? new BigDecimal(walletAmount.intValue()):BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getCurrentOrdersCodAmount(Integer customerId) throws Exception {
        List<Integer> jobOrderStatusList = new ArrayList<Integer>();
        jobOrderStatusList.add(JobOrderStatus.ORDER_PLACED.ordinal());
        jobOrderStatusList.add(JobOrderStatus.ORDER_ACCEPTED.ordinal());
        jobOrderStatusList.add(JobOrderStatus.IN_ROUTE_TO_PICK_UP.ordinal());
        jobOrderStatusList.add(JobOrderStatus.AT_STORE.ordinal());
        String sql = "SELECT SUM(paid_from_cod) FROM orders WHERE order_status IN(:jobOrderStatusList) AND customer_id =:customerId";
        SQLQuery query = getCurrentSession().createSQLQuery(sql);
        query.setParameter("customerId", customerId);
        query.setParameterList("jobOrderStatusList", jobOrderStatusList);
        Number codAmount = (Number) query.uniqueResult();
        return codAmount != null? new BigDecimal(codAmount.intValue()):BigDecimal.ZERO;
    }

    @Override
    public Integer getTotalNumbersOfOrdersTransactions(List<Integer> storeId) throws Exception {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OrderEntity.class);
        criteria.createAlias("itemsOrder", "itemsOrder");
        List<JobOrderStatus> purchaseOrders = new ArrayList<JobOrderStatus>();
        purchaseOrders.add(JobOrderStatus.DELIVERED);
        purchaseOrders.add(JobOrderStatus.CANCELLED);
        criteria.add(Restrictions.and(Restrictions.in("store.id", storeId), Restrictions.in("orderStatus", purchaseOrders), Restrictions.isNotNull("itemsOrder.purchaseStatus"), Restrictions.ne("itemsOrder.purchaseStatus", false)));

        List<OrderEntity> orders = criteria.list();
        return orders.size();
    }

    @Override
    public List<OrderEntity> getOrdersTransactionReport(List<Integer> storeId, Page page) throws Exception {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OrderEntity.class);
        criteria.createAlias("itemsOrder", "itemsOrder");
        List<JobOrderStatus> purchaseOrders = new ArrayList<JobOrderStatus>();
        purchaseOrders.add(JobOrderStatus.DELIVERED);
        purchaseOrders.add(JobOrderStatus.CANCELLED);
        criteria.add(Restrictions.and(Restrictions.in("store.id", storeId), Restrictions.in("orderStatus", purchaseOrders), Restrictions.isNotNull("itemsOrder.purchaseStatus"), Restrictions.ne("itemsOrder.purchaseStatus", false)));

        HibernateUtil.fillPaginationCriteria(criteria, page, OrderEntity.class);
        List<OrderEntity> orders = criteria.list();
        return orders;
    }
}
