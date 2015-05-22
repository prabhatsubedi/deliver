package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.CartDaoService;
import com.yetistep.delivr.hbn.AliasToBeanNestedResultTransformer;
import com.yetistep.delivr.model.CartEntity;
import com.yetistep.delivr.util.DateUtil;
import org.hibernate.*;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

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
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean update(CartEntity value) throws Exception {
        String sql = "UPDATE cart SET note = :note, order_quantity = :quantity, modified_date = :modifiedDate WHERE id = :cartId";
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setParameter("note", value.getNote()!=null ? value.getNote() : null);
        sqlQuery.setParameter("quantity", value.getOrderQuantity());
        sqlQuery.setParameter("cartId", value.getId());
        sqlQuery.setParameter("modifiedDate", DateUtil.getCurrentTimestampSQL());
        sqlQuery.executeUpdate();
        return true;
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
                .add(Projections.property("sb.brandImage"), "storesBrand.brandImage")
                .add(Projections.property("sb.brandLogo"), "storesBrand.brandLogo")
                .add(Projections.property("sb.openingTime"), "storesBrand.openingTime")
                .add(Projections.property("sb.closingTime"), "storesBrand.closingTime")
                .add(Projections.property("sb.status"), "storesBrand.status")
                .add(Projections.property("sb.minOrderAmount"), "storesBrand.minOrderAmount")
                .add(Projections.property("i.id"), "item.id")
                .add(Projections.property("i.name"), "item.name")
                .add(Projections.property("i.unitPrice"), "item.unitPrice")
                .add(Projections.property("i.serviceCharge"), "item.serviceCharge")
                .add(Projections.property("i.vat"), "item.vat")
                .add(Projections.property("i.status"), "item.status")
                .add(Projections.property("i.minOrderQuantity"), "item.minOrderQuantity")
                .add(Projections.property("i.maxOrderQuantity"), "item.maxOrderQuantity");


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
    public List<CartEntity> getMyCustomItemCarts(Long facebookId) throws Exception {
        ProjectionList projectionList = Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("note"), "note")
                .add(Projections.property("orderQuantity"), "orderQuantity")
                .add(Projections.property("sb.id"), "storesBrand.id")
                .add(Projections.property("sb.brandName"), "storesBrand.brandName")
                .add(Projections.property("sb.brandImage"), "storesBrand.brandImage")
                .add(Projections.property("sb.brandLogo"), "storesBrand.brandLogo")
                .add(Projections.property("sb.openingTime"), "storesBrand.openingTime")
                .add(Projections.property("sb.closingTime"), "storesBrand.closingTime")
                .add(Projections.property("sb.status"), "storesBrand.status")
                .add(Projections.property("sb.minOrderAmount"), "storesBrand.minOrderAmount")
                .add(Projections.property("cci.id"), "cartCustomItem.id")
                .add(Projections.property("cci.name"), "cartCustomItem.name");


        Criteria criteria = getCurrentSession().createCriteria(CartEntity.class)
                .createAlias("storesBrand", "sb")
                .createAlias("cartCustomItem", "cci")
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
    public List<Integer> findCarts (Long fbId, Integer brandId, String note) throws Exception {
        List<Integer> carts = new ArrayList<>();

        String sql = "SELECT DISTINCT(c.id) FROM cart c LEFT JOIN cart_custom_items cci ON (c.id = cci.cart_id) WHERE c.customer_fb_id = :fbId  AND c.brand_id = :brandId ";
        if(note == null)
            sql = sql + "AND c.note IS NULL";
        else
            sql = sql + "AND LOWER(c.note) = LOWER(:note)";

        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);

        sqlQuery.setParameter("fbId", fbId);
        sqlQuery.setParameter("brandId", brandId);
        if(note !=null)
            sqlQuery.setParameter("note", note);

        carts = sqlQuery.list();
        return carts;
    }

    @Override
    public Boolean updateOrderQuantity(Integer cartId, Integer additionalQuantity) throws Exception {
        String sql = "UPDATE cart SET order_quantity = order_quantity + :additionalQuantity,modified_date = :modifiedDate WHERE id = :cartId";
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setParameter("additionalQuantity", additionalQuantity);
        sqlQuery.setParameter("cartId", cartId);
        sqlQuery.setParameter("modifiedDate", DateUtil.getCurrentTimestampSQL());
        sqlQuery.executeUpdate();
        return true;
    }

    @Override
    public Boolean updateMinOrderQuantity(Integer cartId, Integer minQn) throws Exception {
        String sql = "UPDATE cart SET order_quantity = :minQn WHERE id = :cartId";
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setParameter("minQn", minQn);
        sqlQuery.setParameter("cartId", cartId);
        sqlQuery.setParameter("modifiedDate", DateUtil.getCurrentTimestampSQL());
        sqlQuery.executeUpdate();
        return true;
    }

    @Override
    public CartEntity findCart(Integer cartId) throws Exception {
        ProjectionList projectionList = Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("note"), "note")
                .add(Projections.property("orderQuantity"), "orderQuantity")
                .add(Projections.property("sb.id"), "storesBrand.id")
                .add(Projections.property("sb.brandName"), "storesBrand.brandName")
                .add(Projections.property("i.id"), "item.id");


        Criteria criteria = getCurrentSession().createCriteria(CartEntity.class)
                .createAlias("storesBrand", "sb")
                .createAlias("item", "i")
                .setProjection(projectionList)
                .setResultTransformer(new AliasToBeanNestedResultTransformer(CartEntity.class));
        criteria.add(Restrictions.eq("id", cartId));
        CartEntity cart = criteria.list().size()>0 ? (CartEntity) criteria.list().get(0) : null;

        return cart;
    }

    @Override
    public CartEntity findCustomCart(Integer cartId) throws Exception {
        ProjectionList projectionList = Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("note"), "note")
                .add(Projections.property("orderQuantity"), "orderQuantity")
                .add(Projections.property("sb.id"), "storesBrand.id")
                .add(Projections.property("sb.brandName"), "storesBrand.brandName");


        Criteria criteria = getCurrentSession().createCriteria(CartEntity.class)
                .createAlias("storesBrand", "sb")
                .setProjection(projectionList)
                .setResultTransformer(new AliasToBeanNestedResultTransformer(CartEntity.class));
        criteria.add(Restrictions.eq("id", cartId));
        CartEntity cart = criteria.list().size()>0 ? (CartEntity) criteria.list().get(0) : null;

        return cart;
    }

    @Override
    public Integer getAvailableOrderItem(Integer cartId) throws Exception {
        String sql = "SELECT i.max_order_quantity-c.order_quantity AS qty FROM cart c " +
                "INNER JOIN items i ON(c.item_id = i.id) " +
                "WHERE c.id = :cartId";
        SQLQuery query = getCurrentSession().createSQLQuery(sql);
        query.setParameter("cartId", cartId);
        Integer availableQty = ((Number) query.uniqueResult()).intValue();
        return availableQty;
    }

    @Override
    public Boolean checkCartExist(Long facebookId) throws Exception {
        String sql = "SELECT count(id) FROM cart WHERE customer_fb_id = :facebookId";
        SQLQuery query = getCurrentSession().createSQLQuery(sql);
        query.setParameter("facebookId", facebookId);
        Integer availableQty = ((Number) query.uniqueResult()).intValue();
        return !availableQty.equals(0);
    }
}
