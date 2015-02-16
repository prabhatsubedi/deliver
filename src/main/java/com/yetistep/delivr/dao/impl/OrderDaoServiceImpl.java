package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.OrderDaoService;
import com.yetistep.delivr.enums.JobOrderStatus;
import com.yetistep.delivr.model.OrderEntity;
import com.yetistep.delivr.model.mobile.dto.OrderInfoDto;
import com.yetistep.delivr.model.mobile.dto.TrackOrderDto;
import com.yetistep.delivr.util.DateUtil;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.TypeLocatorImpl;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
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
        String sqlQuery = "SELECT o.id as id, o.order_name as orderName, o.order_status as orderStatus, o.order_date as orderDate, " +
                "dbs.paid_to_courier as paidToCourier, dbs.customer_chargeable_distance as customerChargeableDistance, " +
                "dbs.system_chargeable_distance as systemChargeableDistance, dbs.time_required as assignedTime, " +
                "dbs.total_time_required as remainingTime, dbh.order_accepted_at as orderAcceptedAt FROM " +
                "orders o INNER JOIN delivery_boys db on (db.id = o.delivery_boy_id) INNER JOIN " +
                "dboy_selections dbs on (dbs.order_id = o.id AND dbs.dboy_id = db.id) INNER JOIN " +
                "dboy_order_history dbh on(dbh.order_id = o.id) WHERE o.order_status in " +
                "(:orderAccepted, :inRouteToPickUp, :atStore, :inRouteToDelivery) AND o.delivery_boy_id = :deliveryBoyId";
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
        List<OrderInfoDto> orderEntities = query.list();
        return orderEntities;
    }

    @Override
    public List<OrderInfoDto> getAssignedOrders(Integer deliveryBoyId) throws Exception {
        String sqlQuery = "SELECT o.id as id, o.order_name as orderName, o.order_status as orderStatus, " +
                "o.order_date as orderDate, dbs.paid_to_courier as paidToCourier, dbs.customer_chargeable_distance " +
                "as customerChargeableDistance, dbs.system_chargeable_distance as systemChargeableDistance, " +
                "dbs.time_required as assignedTime, dbs.total_time_required as remainingTime FROM orders o INNER JOIN " +
                "dboy_selections dbs on (dbs.order_id = o.id) WHERE dbs.rejected = :rejected AND  o.order_status =:orderPlaced " +
                "AND dbs.dboy_id = :deliveryBoyId";
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
        List<OrderInfoDto> orderEntities = query.list();
        return orderEntities;
    }

    @Override
    public OrderEntity getLastActiveOrder(Integer deliverBoyId) throws Exception {
        List<JobOrderStatus> jobOrderStatusList = new ArrayList<JobOrderStatus>();
        jobOrderStatusList.add(JobOrderStatus.ORDER_PLACED);

        Criteria criteria = getCurrentSession().createCriteria(OrderEntity.class)
                .add(Restrictions.eq("deliveryBoy.id", deliverBoyId))
                .add(Restrictions.in("orderStatus", jobOrderStatusList))
                .addOrder(Order.desc("orderDate"))
                .setMaxResults(1);
        List<OrderEntity> orderEntities = criteria.list();
        return orderEntities.size() == 1 ? orderEntities.get(0) : null;
    }

    @Override
    public JobOrderStatus getJobOrderStatus(Integer orderId) throws Exception {
        String sqlQuery = "SELECT o.order_status FROM orders o where o.id =:orderId";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
        query.setParameter("orderId", orderId);
        Integer orderStatus = (Integer) query.uniqueResult();
        JobOrderStatus jobOrderStatus = JobOrderStatus.fromInt(orderStatus);
        return jobOrderStatus;
    }

    @Override
    public List<OrderEntity> getElapsedOrders(Integer timeDuration) throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(OrderEntity.class);
        criteria.add(Restrictions.eq("orderStatus", JobOrderStatus.ORDER_PLACED))
        .add(Restrictions.le("orderDate", DateUtil.subtractSeconds(DateUtil.getCurrentTimestampSQL(), timeDuration)));
        List<OrderEntity>  orderEntities = criteria.list();
        return orderEntities;
    }

    @Override
    public OrderEntity getNextPendingOrder() throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(OrderEntity.class);
        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("orderDate"), "orderDate")
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
    public List<Object> get_dBoy_order_history(Integer dBoyId) throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(OrderEntity.class, "order");
        List<JobOrderStatus> orderStatuses = new ArrayList<>();
        orderStatuses.add(JobOrderStatus.DELIVERED);
        orderStatuses.add(JobOrderStatus.CANCELLED);
        criteria.add(Restrictions.and(Restrictions.in("orderStatus", orderStatuses), Restrictions.eq("deliveryBoy.id", dBoyId)));

        List<Object>  orderEntities = criteria.list();
        return orderEntities;
    }

    @Override
    public TrackOrderDto getTrackOrderInfo(Integer orderId) throws Exception {
        String sqlQuery = "SELECT o.id as orderId, o.order_status as orderStatus, db.latitude as courierBoyLatitude, db.longitude " +
                "as courierBoyLongitude, db.vehicle_type as vehicleType, s.latitude as storeLatitude, s.longitude as storeLongitude, " +
                "u.full_name as courierBoyName, u.profile_image as courierBoyImage, u.mobile_number as courierBoyContactNo, " +
                "a.latitude as deliveryLatitude, a.longitude as deliveryLongitude FROM orders o INNER JOIN delivery_boys db " +
                "on (o.delivery_boy_id = db.id) INNER JOIN stores s on (s.id = o.store_id) INNER JOIN users u " +
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
        TrackOrderDto order = (TrackOrderDto) query.uniqueResult();
        return order;
    }
}
