package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.AdminDaoService;
import com.yetistep.delivr.enums.*;
import com.yetistep.delivr.model.DeliveryBoyEntity;
import com.yetistep.delivr.model.OrderEntity;
import com.yetistep.delivr.service.inf.SystemPropertyService;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 2/3/15
 * Time: 2:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class AdminDaoServiceImpl implements AdminDaoService {

    @Autowired
    SessionFactory sessionFactory;

    @Autowired
    SystemPropertyService systemPropertyService;

    @Override
    public Session getCurrentSession() throws Exception {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public Integer getRegisteredUserCount() throws Exception {
        String sqQuery =    "SELECT COUNT(c.id) FROM customers c";
        Query query = getCurrentSession().createSQLQuery(sqQuery);

        BigInteger cnt = (BigInteger) query.uniqueResult();
        return cnt.intValue();
    }

    @Override
    public Integer getNewUserCount(Integer dayCount) throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -dayCount);
        String sqQuery =    "SELECT COUNT(c.id) FROM customers c INNER JOIN users u ON c.user_id = u.id WHERE u.created_date >:createdDate";
        Query query = getCurrentSession().createSQLQuery(sqQuery);
        query.setParameter("createdDate", dateFormat.format(cal.getTime()));
        BigInteger cnt = (BigInteger) query.uniqueResult();
        return cnt.intValue();
    }

    @Override
    public Map<String, Integer> getNewUserByDayCount(Integer dayCount) throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, Integer> graphData = new TreeMap<>();

        for (Integer i=0; i<dayCount; i++ ){
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -(i+1));
            Calendar calPrev = Calendar.getInstance();
            calPrev.add(Calendar.DATE, -i);
            String sqQuery =    "SELECT COUNT(c.id) FROM customers c INNER JOIN users u ON c.user_id = u.id WHERE u.created_date >:startTime && u.created_date <=:endTime";
            Query query = getCurrentSession().createSQLQuery(sqQuery);
            query.setParameter("startTime", dateFormat.format(cal.getTime()));
            query.setParameter("endTime", dateFormat.format(calPrev.getTime()));

            BigInteger cnt = (BigInteger) query.uniqueResult();
            graphData.put(dayFormat.format(cal.getTime()), cnt.intValue());
        }
        return graphData;
    }

    @Override
    public Integer getActiveUserCount() throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -14);
        String sqQuery =    "SELECT COUNT(c.id) FROM customers c INNER JOIN users u ON c.user_id = u.id WHERE u.last_activity_date >:lastActivity";
        Query query = getCurrentSession().createSQLQuery(sqQuery);
        query.setParameter("lastActivity", dateFormat.format(cal.getTime()));

        BigInteger cnt = (BigInteger) query.uniqueResult();
        return cnt.intValue();
    }

    @Override
    public Integer getPartnerStoreCount() throws Exception {
        String sqQuery = "SELECT COUNT(sb.id) FROM stores_brands sb WHERE sb.partnership_status =:partnershipStatus";
        Query query = getCurrentSession().createSQLQuery(sqQuery);
        query.setParameter("partnershipStatus", Boolean.TRUE);

        BigInteger cnt = (BigInteger) query.uniqueResult();
        return cnt.intValue();
    }


    @Override
    public Integer getDBoyCount(List<Integer> statuses) throws Exception {
        String sqQuery = "SELECT COUNT(db.id) FROM delivery_boys db INNER JOIN users u ON db.user_id = u.id WHERE db.availability_status in (:dBoyStatus) && u.status =:status";
        Query query = getCurrentSession().createSQLQuery(sqQuery);
        query.setParameterList("dBoyStatus", statuses);
        query.setParameter("status", Status.ACTIVE.ordinal());
        BigInteger cnt = (BigInteger) query.uniqueResult();
        return cnt.intValue();
    }

    @Override
    public List<DeliveryBoyEntity> getOnDutyDBoy(List<DBoyStatus> statuses) throws Exception {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DeliveryBoyEntity.class);
        criteria.add(Restrictions.in("availabilityStatus", statuses));
        return (List<DeliveryBoyEntity>) criteria.list();
    }

    @Override
    public Integer getOrderCount(List<Integer> statuses) throws Exception {
        String sqQuery = "SELECT COUNT(o.id) FROM orders o WHERE o.order_status IN (:orderStatus)";
        Query query = getCurrentSession().createSQLQuery(sqQuery);
        query.setParameterList("orderStatus", statuses);

        BigInteger cnt = (BigInteger) query.uniqueResult();
        return cnt.intValue();
    }

    @Override
    public Integer getOrderCount(Integer status) throws Exception {
        String sqQuery = "SELECT COUNT(o.id) FROM orders o WHERE o.order_status =:orderStatus";
        Query query = getCurrentSession().createSQLQuery(sqQuery);
        query.setParameter("orderStatus", status);

        BigInteger cnt = (BigInteger) query.uniqueResult();
        return cnt.intValue();
    }

    @Override
    public Map<String, Integer> getOrderByDayCount(List<Integer> orderStatuses, Integer dayCount) throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, Integer> orderCount = new TreeMap<>();

        for (Integer i=0; i<dayCount; i++){
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -(i+1));
            Calendar calPrev = Calendar.getInstance();
            calPrev.add(Calendar.DATE, -i);
            String sqQuery = "SELECT COUNT(o.id) FROM orders o INNER JOIN dboy_order_history doh ON o.id = doh.order_id WHERE o.order_status IN(:orderStatuses) && doh.completed_at <=:endTime  && doh.completed_at >:startTime";
            Query query = getCurrentSession().createSQLQuery(sqQuery);
            query.setParameterList("orderStatuses", orderStatuses);
            query.setParameter("startTime",dateFormat.format(cal.getTime()));
            query.setParameter("endTime", dateFormat.format(calPrev.getTime()));

            BigInteger cnt = (BigInteger) query.uniqueResult();
            orderCount.put(dayFormat.format(cal.getTime()), cnt.intValue());
        }
        return orderCount;
    }

    @Override
    public List<OrderEntity> getOrderRoute(List<JobOrderStatus> status) throws Exception {
        List<OrderEntity> orders = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OrderEntity.class);
        criteria.add(Restrictions.in("orderStatus", status));
        orders = criteria.list();

        return orders;
    }

    @Override
    public Integer getOrderTotalTime() throws Exception {
        String sqQuery = "SELECT SUM(UNIX_TIMESTAMP(completed_at)-UNIX_TIMESTAMP(job_started_at)) as time_count FROM dboy_order_history doh INNER JOIN orders o on doh.order_id = o.id WHERE o.order_status =:orderStatus";
        Query query = getCurrentSession().createSQLQuery(sqQuery);
        query.setParameter("orderStatus", JobOrderStatus.DELIVERED.ordinal());

        Number cnt = (Number) query.uniqueResult();
        if(cnt != null)
            return cnt.intValue()/60;
        else return 0;
    }

    @Override
    public Integer getOnTimeCount(String type) throws Exception {
        String sqQuery = "SELECT COUNT(o.id) FROM orders o INNER JOIN dboy_order_history doh ON o.delivery_boy_id = doh.dboy_id && o.id = doh.order_id WHERE o.assigned_time "+type+" ((UNIX_TIMESTAMP(doh.completed_at) - UNIX_TIMESTAMP(doh.job_started_at))/60)";
        Query query = getCurrentSession().createSQLQuery(sqQuery);

        BigInteger cnt = (BigInteger) query.uniqueResult();
        return cnt.intValue();
    }

    @Override
    public Integer getTodayOrderCount() throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String sqQuery = "SELECT COUNT(o.id) FROM orders o INNER JOIN dboy_order_history doh ON o.delivery_boy_id = doh.dboy_id && o.id = doh.order_id WHERE o.order_status =:orderStatus && doh.completed_at >:completedDate";
        Query query = getCurrentSession().createSQLQuery(sqQuery);
        query.setParameter("orderStatus", JobOrderStatus.DELIVERED.ordinal());
        query.setParameter("completedDate", dateFormat.format(cal.getTime()));

        BigInteger cnt = (BigInteger) query.uniqueResult();
        return cnt.intValue();
    }



    @Override
    public Integer getTodayOrderTotalTime() throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String sqQuery = "SELECT SUM(UNIX_TIMESTAMP(completed_at)-UNIX_TIMESTAMP(job_started_at)) as time_count FROM dboy_order_history doh INNER JOIN orders o on doh.order_id = o.id WHERE o.order_status =:orderStatus && doh.completed_at >:completedDate";
        Query query = getCurrentSession().createSQLQuery(sqQuery);
        query.setParameter("orderStatus", JobOrderStatus.DELIVERED.ordinal());
        query.setParameter("completedDate", dateFormat.format(cal.getTime()));

        Number cnt = (Number) query.uniqueResult();
        if(cnt != null)
            return cnt.intValue()/60;
        else return 0;
    }

    @Override
    public Map<String, Integer> getOrderByDayCount(Integer dayCount) throws Exception {
        Map<String, Integer> deliveryByDate = new TreeMap<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
        for(Integer i=0; i<dayCount; i++){
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -(i+1));
            Calendar calPrev = Calendar.getInstance();
            calPrev.add(Calendar.DATE, -i);
            String sqQuery = "SELECT COUNT(o.id) FROM orders o INNER JOIN dboy_order_history doh ON o.id = doh.order_id WHERE o.order_status =:orderStatus && doh.completed_at <=:endTime  && doh.completed_at >:startTime";
            Query query = getCurrentSession().createSQLQuery(sqQuery);
            query.setParameter("orderStatus", JobOrderStatus.DELIVERED.ordinal());
            query.setParameter("startTime", dateFormat.format(cal.getTime()));
            query.setParameter("endTime", dateFormat.format(calPrev.getTime()));

            BigInteger cnt = (BigInteger) query.uniqueResult();
            deliveryByDate.put(dayFormat.format(cal.getTime()), cnt.intValue());
        }
        return deliveryByDate;
    }

    @Override
    public Map<String, Integer> onTimeDeliveryCount(Integer dayCount, String type) throws Exception {
        Map<String, Integer> deliveryByDate = new TreeMap<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
        for(Integer i=0; i<dayCount; i++){
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -(i+1));
            Calendar calPrev = Calendar.getInstance();
            calPrev.add(Calendar.DATE, -i);
            String sqQuery = "SELECT COUNT(o.id) FROM orders o INNER JOIN dboy_order_history doh ON o.id = doh.order_id WHERE o.order_status =:orderStatus && doh.completed_at <=:endTime  && doh.completed_at >:startTime && o.assigned_time "+type+" ((UNIX_TIMESTAMP(doh.completed_at)-UNIX_TIMESTAMP(doh.job_started_at))/60)";
            Query query = getCurrentSession().createSQLQuery(sqQuery);
            query.setParameter("orderStatus", JobOrderStatus.DELIVERED.ordinal());
            query.setParameter("startTime", dateFormat.format(cal.getTime()));
            query.setParameter("endTime", dateFormat.format(calPrev.getTime()));

            BigInteger cnt = (BigInteger) query.uniqueResult();
            deliveryByDate.put(dayFormat.format(cal.getTime()), cnt.intValue());
        }
        return deliveryByDate;
    }


    @Override
    public Map<String, Integer> getOrderTotalTimeByDay(Integer dayCount) throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, Integer> totalTime = new TreeMap<>();

        for (Integer i=0; i< dayCount; i++){
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -(i+1));
            Calendar calPrev = Calendar.getInstance();
            calPrev.add(Calendar.DATE, -i);
            String sqQuery = "SELECT SUM(UNIX_TIMESTAMP(completed_at)-UNIX_TIMESTAMP(job_started_at)) as time_count FROM dboy_order_history doh INNER JOIN orders o on doh.order_id = o.id WHERE o.order_status =:jobOrderStatus && doh.completed_at <=:endTime  && doh.completed_at >:startTime";
            Query query = getCurrentSession().createSQLQuery(sqQuery);
            query.setParameter("jobOrderStatus", JobOrderStatus.DELIVERED.ordinal());
            query.setParameter("startTime", dateFormat.format(cal.getTime()));
            query.setParameter("endTime", dateFormat.format(calPrev.getTime()));

            Number cnt = (Number) query.uniqueResult();
            if(cnt != null){
                totalTime.put(dayFormat.format(cal.getTime()), cnt.intValue()/60);
            }else{
                totalTime.put(dayFormat.format(cal.getTime()), 0);
            }
        }
       return  totalTime;
    }


    @Override
    public Integer getCountOutOfReachDBoy() throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Integer prefValueInMillis = Integer.parseInt(systemPropertyService.readPrefValue(PreferenceType.LOCATION_UPDATE_TIMEOUT_IN_MIN))*60*1000;

        String preferredTime = dateFormat.format(System.currentTimeMillis()-prefValueInMillis);

        String sqQuery = "SELECT count(id) FROM delivery_boys  WHERE last_location_update<:preferredTime";
        Query query = getCurrentSession().createSQLQuery(sqQuery);
        query.setParameter("preferredTime", preferredTime);
        Number cnt = (Number) query.uniqueResult();

        return cnt.intValue();

    }



}
