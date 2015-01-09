package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.dao.inf.CartAttributesDaoService;
import com.yetistep.delivr.model.CartAttributesEntity;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 1/9/15
 * Time: 10:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class CartAttributesDaoServiceImpl implements CartAttributesDaoService{
    @Autowired
    SessionFactory sessionFactory;

    @Override
    public CartAttributesEntity find(Integer id) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<CartAttributesEntity> findAll() throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean save(CartAttributesEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean update(CartAttributesEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean delete(CartAttributesEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Session getCurrentSession() throws Exception {
        Session session = sessionFactory.getCurrentSession();
        return session;
    }

    @Override
    public List<Integer> findCartAttributes(List<Integer> carts) throws Exception {
        List<Integer> cartAttributes;
        String sql  = "SELECT id FROM cart_attributes WHERE cart_id IN(:carts)";
        SQLQuery query = getCurrentSession().createSQLQuery(sql);
        query.setParameterList("carts", carts);
        cartAttributes = query.list();
        return cartAttributes;
    }

    @Override
    public Boolean deleteCartAttributes(List<Integer> cartAttributes) throws Exception {
        String sqlAtt = "Delete from cart_attributes where id IN(:cartAttributes)";
        SQLQuery query = getCurrentSession().createSQLQuery(sqlAtt);
        query.setParameterList("cartAttributes", cartAttributes);
        query.executeUpdate();
        return true;
    }
}
