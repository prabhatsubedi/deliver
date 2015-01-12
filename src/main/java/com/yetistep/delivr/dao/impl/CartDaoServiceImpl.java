package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.CartDaoService;
import com.yetistep.delivr.hbn.AliasToBeanNestedResultTransformer;
import com.yetistep.delivr.model.CartAttributesEntity;
import com.yetistep.delivr.model.CartEntity;
import org.hibernate.*;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanConstructorResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 1/8/15
 * Time: 12:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class CartDaoServiceImpl implements CartDaoService{
    @Autowired
    SessionFactory sessionFactory;

    @Override
    public CartEntity find(Integer id) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<CartEntity> findAll() throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean save(CartEntity value) throws Exception {
        getCurrentSession().save(value);
        return true;
    }

    @Override
    public Boolean update(CartEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean delete(CartEntity value) throws Exception {
       return null;
    }

    @Override
    public Session getCurrentSession() throws Exception {
        Session session = sessionFactory.getCurrentSession();
        return session;
    }

    @Override
    public List<Integer> findCarts(Long clientFBId, Integer brandId) throws Exception {
        List<Integer> carts = new ArrayList<>();
        String sql  = "SELECT id FROM cart WHERE customer_fb_id = :clientFBId ";
        if(brandId!=null)
            sql = sql + "AND brand_id != :brandId";

        SQLQuery query = getCurrentSession().createSQLQuery(sql);
        query.setParameter("clientFBId", clientFBId);
        if(brandId!=null)
            query.setParameter("brandId", brandId);
        carts = query.list();
        return carts;
    }

    @Override
    public Boolean deleteCarts(List<Integer> carts) throws Exception {
        String sqlAtt = "Delete from cart where id IN(:carts)";
        SQLQuery query = getCurrentSession().createSQLQuery(sqlAtt);
        query.setParameterList("carts", carts);
        query.executeUpdate();
        return true;
    }

    @Override
    public List<CartEntity> getMyCarts(Long facebookId) throws Exception {
        ProjectionList projectionList = Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("note"), "note")
                .add(Projections.property("orderQuantity"), "orderQuantity")
                .add(Projections.property("sb.id"), "storesBrand.id")
                .add(Projections.property("sb.brandName"), "storesBrand.brandName")
                .add(Projections.property("sb.brandLogo"), "storesBrand.brandLogo")
                .add(Projections.property("i.id"), "item.id")
                .add(Projections.property("i.name"), "item.name")
                .add(Projections.property("i.unitPrice"), "item.unitPrice");


        Criteria criteria = getCurrentSession().createCriteria(CartEntity.class)
                .createAlias("storesBrand", "sb")
                .createAlias("item", "i")
                .setProjection(projectionList)
                .setResultTransformer(new AliasToBeanNestedResultTransformer(CartEntity.class));
        criteria.add(Restrictions.eq("customer.facebookId", facebookId));
        List<CartEntity> cartEntities = criteria.list();

        return cartEntities;
    }

    @Override
    public List<Integer> findCarts (Long fbId, Integer itemId, Integer brandId, String note) throws Exception {
        List<Integer> carts = new ArrayList<>();

        String sql = "SELECT id FROM cart c WHERE c.customer_fb_id = :fbId AND c.item_id = :itemId AND c.brand_id = :brandId ";
        if(note == null)
            sql = sql + "AND c.note IS NULL";
        else
            sql = sql + "AND LOWER(c.note) = LOWER(:note)";

        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);

        sqlQuery.setParameter("fbId", fbId);
        sqlQuery.setParameter("itemId", itemId);
        sqlQuery.setParameter("brandId", brandId);
        if(note !=null)
            sqlQuery.setParameter("note", note);

        carts = sqlQuery.list();
        return carts;
    }

    @Override
    public Boolean updateOrderQuantity(Integer cartId, Integer additionalQuantity) throws Exception {
        String sql = "UPDATE cart SET order_quantity = order_quantity + :additionalQuantity WHERE id = :cartId";
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setParameter("additionalQuantity", additionalQuantity);
        sqlQuery.setParameter("cartId", cartId);

        sqlQuery.executeUpdate();
        return true;
    }


}
