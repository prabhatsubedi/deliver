package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.ItemsOrderDaoService;
import com.yetistep.delivr.model.ItemsOrderEntity;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 1/6/15
 * Time: 6:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class ItemsOrderDaoServiceImpl implements ItemsOrderDaoService {
    @Autowired
    SessionFactory sessionFactory;

    @Override
    public ItemsOrderEntity find(Integer id) throws Exception {
        return (ItemsOrderEntity) getCurrentSession().get(ItemsOrderEntity.class, id);
    }

    @Override
    public List<ItemsOrderEntity> findAll() throws Exception {
        return (List<ItemsOrderEntity>) getCurrentSession().createCriteria(ItemsOrderEntity.class).list();
    }

    @Override
    public Boolean save(ItemsOrderEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean update(ItemsOrderEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean delete(ItemsOrderEntity value) throws Exception {
        return null;
    }

    @Override
    public Session getCurrentSession() throws Exception {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public Integer getNumberOfUnprocessedItems(Integer orderId) throws Exception {
        String sqlQuery = "SELECT count(id) FROM items_orders WHERE order_id = :orderId AND purchase_status IS NULL";
        SQLQuery query = getCurrentSession().createSQLQuery(sqlQuery);
        query.setParameter("orderId", orderId);
        return  ((Number) query.uniqueResult()).intValue();
    }
}
