package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.dao.inf.CartAttributesDaoService;
import com.yetistep.delivr.model.CartAttributesEntity;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
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
        getCurrentSession().save(value);
        return true;
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

    @Override
    public BigDecimal findAttributesPrice(Integer cartId) throws Exception {
        String sql = "SELECT COALESCE(SUM(ia.unit_price), 0) AS atPrice FROM cart_attributes ca " +
                "INNER JOIN items_attributes ia ON(ia.id = ca.items_attribute_id) " +
                "WHERE ca.cart_id =:cartId";
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setParameter("cartId", cartId);
        long value = ((Number)sqlQuery.uniqueResult()).longValue();
        return new BigDecimal(value);
    }

    @Override
    public List<Integer> findCartAttributes(Integer cartId) throws Exception {
        List<Integer> cartAttributes;
        String sql  = "SELECT items_attribute_id FROM cart_attributes WHERE cart_id =:cartId";
        SQLQuery query = getCurrentSession().createSQLQuery(sql);
        query.setParameter("cartId", cartId);
        cartAttributes = query.list();
        return cartAttributes;
    }

    @Override
    public Boolean deleteCartAttributes(Integer cartId) throws Exception {
        String sqlAtt = "Delete from cart_attributes where cart_id=:cartId";
        SQLQuery query = getCurrentSession().createSQLQuery(sqlAtt);
        query.setParameter("cartId", cartId);
        query.executeUpdate();
        return true;
    }
}
