package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.CustomerDaoService;
import com.yetistep.delivr.enums.JobOrderStatus;
import com.yetistep.delivr.model.CustomerEntity;
import com.yetistep.delivr.model.OrderEntity;
import com.yetistep.delivr.model.UserEntity;
import com.yetistep.delivr.model.mobile.dto.MyOrderDto;
import org.hibernate.*;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 12:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomerDaoServiceImpl implements CustomerDaoService {

    @Autowired
    SessionFactory sessionFactory;


    @Override
    public CustomerEntity find(Integer id) throws Exception {
        return (CustomerEntity) getCurrentSession().get(CustomerEntity.class, id);
    }

    @Override
    public List<CustomerEntity> findAll() throws Exception {
        return (List<CustomerEntity>) getCurrentSession().createCriteria(CustomerEntity.class).list();
    }

    @Override
    public Boolean save(CustomerEntity value) throws Exception {
        getCurrentSession().save(value);
        return true;
    }

    @Override
    public Boolean update(CustomerEntity value) throws Exception {
        getCurrentSession().update(value);
        return true;
    }

    @Override
    public Boolean delete(CustomerEntity value) throws Exception {
        getCurrentSession().delete(value);
        return true;
    }

    @Override
    public Session getCurrentSession() throws Exception {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void saveOrder(OrderEntity value) throws Exception {
        getCurrentSession().save(value);
    }

    @Override
    public CustomerEntity find(Long facebookId) throws Exception {
        List<CustomerEntity> customerEntities = null;
        Criteria criteria = getCurrentSession().createCriteria(CustomerEntity.class);
        criteria.add(Restrictions.eq("facebookId", facebookId));
        customerEntities = criteria.list();
        return customerEntities.size() > 0 ? customerEntities.get(0) : null;

    }

    @Override
    public CustomerEntity findUser(Long facebookId) throws Exception {
       String sql = "SELECT c.id, u.id AS userId, u.mobile_number, u.verification_code, u.full_name, u.last_address_mobile FROM customers c " +
               "INNER JOIN users u ON (u.id = c.user_id) " +
               "WHERE c.facebook_id = :facebookId";
       SQLQuery query = getCurrentSession().createSQLQuery(sql);
       query.setParameter("facebookId", facebookId);
        List<Object[]> rows = query.list();
        CustomerEntity customerEntity = null;

        for (Object[] row : rows) {
            customerEntity = new CustomerEntity();
            customerEntity.setId(Integer.parseInt(row[0].toString()));
            UserEntity userEntity = new UserEntity();
            userEntity.setId(Integer.parseInt(row[1].toString()));
            userEntity.setMobileNumber(row[2]!=null ?row[2].toString(): null);
            userEntity.setVerificationCode(row[3]!=null ? row[3].toString() : null);
            userEntity.setFullName(row[4]!=null ? row[4].toString(): null);
            userEntity.setLastAddressMobile(row[5]!=null ? row[5].toString(): null);
            customerEntity.setUser(userEntity);
        }

       return customerEntity;
    }

    @Override
    public BigDecimal getRewardsPoint(Long facebookId) throws Exception {
        String sql = "SELECT rewards_earned FROM customers WHERE facebook_id = :facebookId";
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setParameter("facebookId", facebookId);
        String res = sqlQuery.uniqueResult()!=null ?sqlQuery.uniqueResult().toString() : "";
        BigDecimal rewardsEarned = BigDecimal.ZERO;
        if(!res.isEmpty())
            rewardsEarned = new BigDecimal(res);
        return rewardsEarned;
    }

    @Override
    public CustomerEntity getCustomerIdAndRewardFromFacebookId(Long facebookId) throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(CustomerEntity.class);
        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("rewardsEarned"), "rewardsEarned")
        ).setResultTransformer(Transformers.aliasToBean(CustomerEntity.class));
        criteria.add(Restrictions.eq("facebookId", facebookId));
        return (CustomerEntity) criteria.uniqueResult();
    }

    @Override
    public List<MyOrderDto> getCurrentOrdersByFacebookId(Long facebookId) throws Exception {
        String sqlQuery = "SELECT o.id as orderId, o.order_status as jobOrderStatus, " +
                "sb.brand_logo as brandLogo, sb.brand_name as brandName " +
                "FROM orders o, stores_brands sb, stores s, customers c " +
                "WHERE o.store_id = s.id AND s.stores_brand_id = sb.id AND " +
                "o.order_status not in (:delivered, :cancelled) AND " +
                "c.id = o.customer_id AND c.facebook_id =:facebookId";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
        query.setParameter("delivered", JobOrderStatus.DELIVERED.ordinal());
        query.setParameter("cancelled", JobOrderStatus.CANCELLED.ordinal());
        query.setParameter("facebookId", facebookId);
        query.setResultTransformer(Transformers.aliasToBean(MyOrderDto.class));
        List<MyOrderDto> currentOrders = query.list();
        return currentOrders;
    }

    @Override
    public List<MyOrderDto> getPastOrdersByFacebookId(Long facebookId) throws Exception {
        String sqlQuery = "SELECT o.id as orderId, o.order_status as jobOrderStatus, " +
                "sb.brand_logo as brandLogo, sb.brand_name as brandName " +
                "FROM orders o, stores_brands sb, stores s, customers c " +
                "WHERE o.store_id = s.id AND s.stores_brand_id = sb.id AND " +
                "o.order_status in (:delivered, :cancelled) AND " +
                "c.id = o.customer_id AND c.facebook_id =:facebookId";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
        query.setParameter("delivered", JobOrderStatus.DELIVERED.ordinal());
        query.setParameter("cancelled", JobOrderStatus.CANCELLED.ordinal());
        query.setParameter("facebookId", facebookId);
        query.setResultTransformer(Transformers.aliasToBean(MyOrderDto.class));
        List<MyOrderDto> pastOrders = query.list();
        return pastOrders;
    }
}


