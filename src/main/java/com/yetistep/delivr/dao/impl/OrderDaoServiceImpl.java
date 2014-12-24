package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.OrderDaoService;
import com.yetistep.delivr.model.OrderEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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
}
