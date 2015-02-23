package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.OrderCancelDaoService;
import com.yetistep.delivr.model.OrderCancelEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 2/23/15
 * Time: 12:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class OrderCancelDaoServiceImpl implements OrderCancelDaoService {
    @Autowired
    SessionFactory sessionFactory;

    @Override
    public OrderCancelEntity find(Integer id) throws Exception {
        return (OrderCancelEntity) getCurrentSession().get(OrderCancelEntity.class, id);
    }

    @Override
    public List<OrderCancelEntity> findAll() throws Exception {
        return (List<OrderCancelEntity>) getCurrentSession().createCriteria(OrderCancelEntity.class).list();
    }

    @Override
    public Boolean save(OrderCancelEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean update(OrderCancelEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean delete(OrderCancelEntity value) throws Exception {
        return null;
    }

    @Override
    public Session getCurrentSession() throws Exception {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public OrderCancelEntity getOrderCancelInfoFromOrderId(Integer orderId) throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(OrderCancelEntity.class)
                .add(Restrictions.eq("order.id", orderId));
        return (OrderCancelEntity) criteria.uniqueResult();
    }
}
