package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.AdminDaoService;
import com.yetistep.delivr.enums.DBoyStatus;
import com.yetistep.delivr.enums.DeliveryStatus;
import com.yetistep.delivr.enums.JobOrderStatus;
import com.yetistep.delivr.enums.Status;
import com.yetistep.delivr.model.OrderEntity;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.BigInteger;
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

    @Override
    public Session getCurrentSession() throws Exception {
        Session session = sessionFactory.getCurrentSession();
        return session;
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
    public Integer getNewUserByDayCount(Integer dayCount, Integer prev) throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -dayCount);
        Calendar calPrev = Calendar.getInstance();
        calPrev.add(Calendar.DATE, -prev);
        String sqQuery =    "SELECT COUNT(c.id) FROM customers c INNER JOIN users u ON c.user_id = u.id WHERE u.created_date <=:startTime && u.created_date >:endTime";
        Query query = getCurrentSession().createSQLQuery(sqQuery);
        query.setParameter("startTime", dateFormat.format(cal.getTime()));
        query.setParameter("endTime", dateFormat.format(calPrev.getTime()));

        BigInteger cnt = (BigInteger) query.uniqueResult();
        return cnt.intValue();
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
        String sqQuery = "SELECT COUNT(b.id) FROM stores_brands b INNER JOIN merchants m ON b.merchant_id = m.id WHERE m.partnership_status =:partnershipStatus";
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
    public Integer getOrderByDayCount(List<JobOrderStatus> orderStatuses, Integer dayCount, Integer prev) throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -dayCount);
        Calendar calPrev = Calendar.getInstance();
        calPrev.add(Calendar.DATE, -prev);
        String sqQuery = "SELECT COUNT(o.id) FROM orders o INNER JOIN dboy_order_history doh ON o.id = doh.order_id  WHERE doh.completed_at <= '"+cal.getTime()+"' && doh.completed_at > '"+calPrev.getTime()+"' && o.order_status IN (:orderStatuses)";
        Query query = getCurrentSession().createSQLQuery(sqQuery);
        query.setParameterList("orderStatuses", orderStatuses);

        BigInteger cnt = (BigInteger) query.uniqueResult();
        return cnt.intValue();
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
            return (Integer) cnt.intValue()/60;
        else return 0;
    }

    @Override
    public Integer getOnTimeCount(String type) throws Exception {
        String sqQuery = "SELECT COUNT(o.id) FROM orders o INNER JOIN dboy_order_history doh ON o.delivery_boy_id = doh.dboy_id && o.id = doh.order_id WHERE o.assigned_time "+type+" (doh.completed_at - doh.job_started_at)";
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
    public Integer getOrderByDayCount(Integer dayCount, Integer prev) throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -dayCount);
        Calendar calPrev = Calendar.getInstance();
        calPrev.add(Calendar.DATE, -prev);
        String sqQuery = "SELECT COUNT(o.id) FROM orders o INNER JOIN dboy_order_history doh ON o.delivery_boy_id = doh.dboy_id && o.id = doh.order_id WHERE o.order_status =:orderStatus && doh.completed_at <=:startTime  && doh.completed_at >:endTime";
        Query query = getCurrentSession().createSQLQuery(sqQuery);
        query.setParameter("orderStatus", JobOrderStatus.DELIVERED.ordinal());
        query.setParameter("startTime", dateFormat.format(cal.getTime()));
        query.setParameter("endTime", dateFormat.format(calPrev.getTime()));

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
            return (Integer) cnt.intValue()/60;
        else return 0;
    }


    @Override
    public Integer getOrderTotalTimeByDay(Integer dayCount, Integer prev) throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -dayCount);
        Calendar calPrev = Calendar.getInstance();
        calPrev.add(Calendar.DATE, -prev);
        String sqQuery = "SELECT SUM(UNIX_TIMESTAMP(completed_at)-UNIX_TIMESTAMP(job_started_at)) as time_count FROM dboy_order_history doh INNER JOIN orders o on doh.order_id = o.id WHERE o.order_status = 5 && doh.completed_at <= '"+dateFormat.format(cal.getTime())+"'  && doh.completed_at > '"+dateFormat.format(calPrev.getTime())+"'";
        Query query = getCurrentSession().createSQLQuery(sqQuery);

        Number cnt = (Number) query.uniqueResult();
        if(cnt != null)
            return cnt.intValue()/60;
        else return 0;
    }




}
