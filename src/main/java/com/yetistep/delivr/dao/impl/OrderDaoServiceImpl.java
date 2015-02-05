package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.OrderDaoService;
import com.yetistep.delivr.enums.JobOrderStatus;
import com.yetistep.delivr.model.OrderEntity;
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
        getCurrentSession().save(value);
        return true;
    }

    @Override
    public Boolean update(OrderEntity value) throws Exception {
        getCurrentSession().update(value);
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
    public List<OrderEntity> getActiveOrdersList(Integer deliverBoyId) throws Exception {
        List<JobOrderStatus> jobOrderStatusList = new ArrayList<JobOrderStatus>();
        jobOrderStatusList.add(JobOrderStatus.ORDER_ACCEPTED);
        jobOrderStatusList.add(JobOrderStatus.IN_ROUTE_TO_PICK_UP);
        jobOrderStatusList.add(JobOrderStatus.AT_STORE);
        jobOrderStatusList.add(JobOrderStatus.IN_ROUTE_TO_DELIVERY);

        Criteria criteria = getCurrentSession().createCriteria(OrderEntity.class);
        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("orderName"), "orderName")
                .add(Projections.property("orderStatus"), "orderStatus")
                .add(Projections.property("customerChargeableDistance"), "customerChargeableDistance")
                .add(Projections.property("systemChargeableDistance"), "systemChargeableDistance")
                .add(Projections.property("orderDate"), "orderDate")
                .add(Projections.property("assignedTime"), "assignedTime")
                .add(Projections.property("remainingTime"), "remainingTime")
        ).setResultTransformer(Transformers.aliasToBean(OrderEntity.class));
        criteria.add(Restrictions.eq("deliveryBoy.id", deliverBoyId))
                .add(Restrictions.in("orderStatus", jobOrderStatusList));
        List<OrderEntity> orderEntities = criteria.list();
        return orderEntities;
    }

    @Override
    public List<OrderEntity> getAssignedOrders(Integer deliveryBoyId) throws Exception {
        String sqlQuery = "SELECT o.id as id, o.order_name as orderName, o.order_status as orderStatus, " +
                "o.order_date as orderDate, db.customer_chargeable_distance as customerChargeableDistance, " +
                "db.system_chargeable_distance as systemChargeableDistance, db.total_time_required as assignedTime, " +
                "db.total_time_required as remainingTime " +
                "FROM orders o, dboy_selections db WHERE o.id = db.order_id AND " +
                "o.order_status = :orderStatus and db.dboy_id = :deliveryBoyId";
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
                .addScalar("customerChargeableDistance", BigDecimalType.INSTANCE)
                .addScalar("systemChargeableDistance", BigDecimalType.INSTANCE)
                .addScalar("assignedTime", IntegerType.INSTANCE)
                .addScalar("remainingTime", IntegerType.INSTANCE);

        query.setParameter("orderStatus", JobOrderStatus.ORDER_PLACED.ordinal());
        query.setParameter("deliveryBoyId", deliveryBoyId);
        query.setResultTransformer(Transformers.aliasToBean(OrderEntity.class));
        List<OrderEntity> orderEntities = query.list();
        return orderEntities;
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
}
