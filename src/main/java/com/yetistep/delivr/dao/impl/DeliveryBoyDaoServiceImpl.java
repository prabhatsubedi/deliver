package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.DeliveryBoyDaoService;
import com.yetistep.delivr.enums.DBoyStatus;
import com.yetistep.delivr.enums.DeliveryStatus;
import com.yetistep.delivr.enums.JobOrderStatus;
import com.yetistep.delivr.enums.Status;
import com.yetistep.delivr.model.DeliveryBoyEntity;
import com.yetistep.delivr.model.Page;
import com.yetistep.delivr.util.DateUtil;
import com.yetistep.delivr.util.HibernateUtil;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.IntegerType;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 12:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeliveryBoyDaoServiceImpl implements DeliveryBoyDaoService {

    @Autowired
    SessionFactory sessionFactory;


    @Override
    public DeliveryBoyEntity find(Integer id) throws Exception {
        return (DeliveryBoyEntity) getCurrentSession().get(DeliveryBoyEntity.class, id);
    }

    @Override
    public DeliveryBoyEntity findDBoyById(Integer id) throws Exception {
         List<DeliveryBoyEntity> deliveryBoys =   new ArrayList<>();
         Criteria criteria   = getCurrentSession().createCriteria(DeliveryBoyEntity.class);
         criteria.add(Restrictions.eq("id", id));
         deliveryBoys = criteria.list();
         return deliveryBoys.size()>0?deliveryBoys.get(0):null;

    }

    @Override
    public List<DeliveryBoyEntity> findAll() throws Exception {
        return (List<DeliveryBoyEntity>) getCurrentSession().createCriteria(DeliveryBoyEntity.class).list();
    }

    @Override
    public List<DeliveryBoyEntity> findAll(Page page) throws Exception {
        List<DeliveryBoyEntity> deliveryBoys  = new ArrayList<DeliveryBoyEntity>();
        Criteria criteria  = getCurrentSession().createCriteria(DeliveryBoyEntity.class);
        HibernateUtil.fillPaginationCriteria(criteria, page, DeliveryBoyEntity.class);
        deliveryBoys = criteria.list();
        return deliveryBoys;
    }

    @Override
    public Integer getTotalNumberOfDboys() throws Exception {
        String sqQuery =    "SELECT COUNT(db.id) FROM delivery_boys db";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqQuery);
        BigInteger cnt = (BigInteger) query.uniqueResult();
        return cnt.intValue();
    }

    @Override
    public Boolean save(DeliveryBoyEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean update(DeliveryBoyEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean delete(DeliveryBoyEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<DeliveryBoyEntity> findAllCapableDeliveryBoys(Integer updateLocationTimeOut) throws Exception {
        return (List<DeliveryBoyEntity>) getCurrentSession().createCriteria(DeliveryBoyEntity.class)
                .createAlias("user", "u")
                .add(Restrictions.or(Restrictions.eq("availabilityStatus", DBoyStatus.FREE), Restrictions.eq("availabilityStatus", DBoyStatus.BUSY)))
                .add(Restrictions.lt("activeOrderNo", 3))
                .add(Restrictions.isNotNull("latitude"))
                .add(Restrictions.isNotNull("longitude"))
                .add(Restrictions.gt("lastLocationUpdate", DateUtil.getTimestampLessThanNMinutes(updateLocationTimeOut)))
                .add(Restrictions.and(Restrictions.eq("u.verifiedStatus", true), Restrictions.eq("u.mobileVerificationStatus", true), Restrictions.eq("u.status", Status.ACTIVE))).list();
    }

    @Override
    public Session getCurrentSession() throws Exception {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public DeliveryBoyEntity getProfileInformation(Integer deliveryBoyId) throws Exception {
        String sqlQuery = "SELECT (SELECT count(o.id) FROM orders o WHERE o.delivery_boy_id = :deliveryBoyId AND " +
                "o.order_status NOT IN (:pastStatusList)) + (SELECT count(dbs.id) FROM dboy_selections dbs INNER JOIN " +
                "orders o on (o.id = dbs.order_id) WHERE o.order_status = :orderPlaced AND dboy_id = :deliveryBoyId AND dbs.rejected = :rejected) as activeOrderNo, " +
                "(SELECT count(o.id) FROM orders o WHERE o.delivery_boy_id = :deliveryBoyId AND o.order_status in (:pastStatusList)) " +
                "as totalOrderTaken, id, total_earnings as totalEarnings, previous_due as previousDue, " +
                "wallet_amount as walletAmount, average_rating as averageRating, " +
                "available_amount  as availableAmount FROM delivery_boys WHERE id = :deliveryBoyId";
        List<Integer> pastStatusList = new ArrayList<Integer>();
        pastStatusList.add(JobOrderStatus.DELIVERED.ordinal());
        pastStatusList.add(JobOrderStatus.CANCELLED.ordinal());
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery)
                .addScalar("id", IntegerType.INSTANCE)
                .addScalar("activeOrderNo", IntegerType.INSTANCE)
                .addScalar("totalOrderTaken", IntegerType.INSTANCE)
                .addScalar("totalEarnings", BigDecimalType.INSTANCE)
                .addScalar("previousDue", BigDecimalType.INSTANCE)
                .addScalar("walletAmount", BigDecimalType.INSTANCE)
                .addScalar("availableAmount", BigDecimalType.INSTANCE)
                .addScalar("averageRating", BigDecimalType.INSTANCE);

        query.setParameterList("pastStatusList", pastStatusList);
        query.setParameter("deliveryBoyId", deliveryBoyId);
        query.setParameter("orderPlaced",JobOrderStatus.ORDER_PLACED.ordinal());
        query.setParameter("rejected", false);
        query.setResultTransformer(Transformers.aliasToBean(DeliveryBoyEntity.class));
        return (DeliveryBoyEntity) query.uniqueResult();
    }

    @Override
    public Boolean checkForPendingOrders(Integer deliveryBoyId, Integer orderId) throws Exception {
        String sqlQuery = "SELECT count(db.id) FROM dboy_order_history db INNER JOIN orders o " +
                "on (o.id = db.order_id) WHERE db.delivery_status = :deliveryStatus AND " +
                "db.dboy_id = :deliveryBoyId AND o.order_status in " +
                "(:inRoutePickUp, :atStore, :inRouteToDelivery) AND o.id != :orderId";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
        query.setParameter("deliveryStatus", DeliveryStatus.PENDING.ordinal());
        query.setParameter("inRoutePickUp", JobOrderStatus.IN_ROUTE_TO_PICK_UP.ordinal());
        query.setParameter("atStore", JobOrderStatus.AT_STORE.ordinal());
        query.setParameter("inRouteToDelivery", JobOrderStatus.IN_ROUTE_TO_DELIVERY.ordinal());
        query.setParameter("deliveryBoyId", deliveryBoyId);
        query.setParameter("orderId", orderId);
        BigInteger count = (BigInteger) query.uniqueResult();
        return count.intValue() == 0;
    }

    @Override
    public Integer getNumberOfAssignedOrders(Integer deliveryBoyId) throws Exception {
        String sqlQuery = "SELECT count(dbs.id) FROM dboy_selections dbs INNER JOIN orders o ON " +
                "(o.id = dbs.order_id) WHERE o.order_status = :orderPlaced AND dboy_id =:deliveryBoyId";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
        query.setParameter("orderPlaced", JobOrderStatus.ORDER_PLACED.ordinal());
        query.setParameter("deliveryBoyId", deliveryBoyId);
        BigInteger count = (BigInteger) query.uniqueResult();
       return count.intValue();
    }

    @Override
    public Boolean canStartJob(Integer orderId, Integer deliveryBoyId) throws Exception {
//        String sqlQuery = "SELECT count(id) FROM orders WHERE delivery_boy_id = :deliveryBoyId " +
//                "AND order_status IN(:runningOrderList) AND id < :orderId";
        String sqlQuery = "SELECT count(o.id) FROM orders o INNER JOIN dboy_order_history dbh on (dbh.order_id = o.id) " +
                "WHERE o.delivery_boy_id = :deliveryBoyId AND o.order_status IN(:runningOrderList) AND " +
                "dbh.order_accepted_at < (SELECT order_accepted_at FROM dboy_order_history WHERE order_id = :orderId " +
                "AND dboy_id = :deliveryBoyId)";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
        List<Integer> runningOrderList = new ArrayList<Integer>();
        runningOrderList.add(JobOrderStatus.ORDER_ACCEPTED.ordinal());
        runningOrderList.add(JobOrderStatus.IN_ROUTE_TO_PICK_UP.ordinal());
        runningOrderList.add(JobOrderStatus.AT_STORE.ordinal());
        runningOrderList.add(JobOrderStatus.IN_ROUTE_TO_DELIVERY.ordinal());
        query.setParameterList("runningOrderList", runningOrderList);
        query.setParameter("deliveryBoyId", deliveryBoyId);
        query.setParameter("orderId", orderId);
        BigInteger count = (BigInteger) query.uniqueResult();
        return count.intValue() == 0;
    }

    @Override
    public Boolean updateAverageRating(BigDecimal averageRating, Integer dboyId) throws Exception {
        String sql = "UPDATE delivery_boys SET average_rating = :averageRating WHERE id = :dboyId";
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setParameter("averageRating", averageRating);
        sqlQuery.setParameter("dboyId", dboyId);

        sqlQuery.executeUpdate();
        return true;
    }

    @Override
    public Boolean checkIfLicenseNumberExists(String licenseNumber) throws Exception {
        String sql = "SELECT count(id) FROM delivery_boys WHERE license_number = :licenseNumber";
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setParameter("licenseNumber", licenseNumber);
        BigInteger count = (BigInteger) sqlQuery.uniqueResult();
        return (count.intValue() > 0);
    }

    @Override
    public Boolean checkIfLicenseNumberExists(String licenseNumber, Integer dBoyId) throws Exception {
        String sql = "SELECT count(id) FROM delivery_boys WHERE license_number = :licenseNumber AND id !=:dBoyId";
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setParameter("licenseNumber", licenseNumber);
        sqlQuery.setParameter("dBoyId", dBoyId);
        BigInteger count = (BigInteger) sqlQuery.uniqueResult();
        return (count.intValue() > 0);
    }

    @Override
    public Boolean updatePreviousDayDueAmount() throws Exception {
        String sql = "UPDATE delivery_boys SET previous_due = wallet_amount";
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.executeUpdate();
        return true;
    }

    @Override
    public Integer getNumberOfActiveOrders(Integer deliveryBoyId) throws Exception {
        List<Integer> jobOrderStatusList = new ArrayList<Integer>();
        jobOrderStatusList.add(JobOrderStatus.ORDER_PLACED.ordinal());
        jobOrderStatusList.add(JobOrderStatus.ORDER_ACCEPTED.ordinal());
        jobOrderStatusList.add(JobOrderStatus.IN_ROUTE_TO_PICK_UP.ordinal());
        jobOrderStatusList.add(JobOrderStatus.AT_STORE.ordinal());
        jobOrderStatusList.add(JobOrderStatus.IN_ROUTE_TO_DELIVERY.ordinal());
        String sql = "SELECT count(id) FROM orders WHERE order_status IN (:orderStatus) AND delivery_boy_id = :dBoyId";
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setParameter("dBoyId", deliveryBoyId);
        sqlQuery.setParameterList("orderStatus", jobOrderStatusList);
        BigInteger count = (BigInteger) sqlQuery.uniqueResult();
        return count.intValue();
    }


}
