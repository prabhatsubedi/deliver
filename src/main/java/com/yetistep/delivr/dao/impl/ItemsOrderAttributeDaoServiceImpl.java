package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.ItemsOrderAttributeDaoService;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 2/13/15
 * Time: 2:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class ItemsOrderAttributeDaoServiceImpl implements ItemsOrderAttributeDaoService {
    @Autowired
    SessionFactory sessionFactory;

    @Override
    public ItemsOrderAttributeDaoService find(Integer id) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ItemsOrderAttributeDaoService> findAll() throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean save(ItemsOrderAttributeDaoService value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean update(ItemsOrderAttributeDaoService value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean delete(ItemsOrderAttributeDaoService value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Session getCurrentSession() throws Exception {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<Integer> getSelectedAttributesOfItemOrder(Integer itemOrderId) throws Exception {
        String sql = "SELECT items_attribute_id FROM items_order_attribute WHERE item_order_id = :itemOrderId";
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setParameter("itemOrderId", itemOrderId);
        List<Integer> selectedAttributes = sqlQuery.list();
        return selectedAttributes;
    }
}
