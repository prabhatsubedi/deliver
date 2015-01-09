package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.CartDaoService;
import com.yetistep.delivr.model.CartAttributesEntity;
import com.yetistep.delivr.model.CartEntity;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
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
        getCurrentSession().save(value);
        return true;
    }

    @Override
    public Boolean update(CartEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean delete(CartEntity value) throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(CartEntity.class);
        criteria.add(Restrictions.eq("customer.facebookId", value.getCustomer().getFacebookId()));
        criteria.add(Restrictions.not(Restrictions.eq("storesBrand.id", value.getStoresBrand().getId())));
        List<CartEntity> carts = criteria.list();
        if(carts.size() > 0){
            for(CartEntity cartEntity : carts){
                if(cartEntity.getCartAttributes().size() > 0){
                    for(CartAttributesEntity cartAttributesEntity : cartEntity.getCartAttributes()){
                        String sqlAtt = "Delete from cart_attributes where id = "+ cartAttributesEntity.getId();
                        SQLQuery query = getCurrentSession().createSQLQuery(sqlAtt);
                        query.executeUpdate();
                    }
                }
                String sqlAtt = "Delete from cart where id = "+ cartEntity.getId();
                SQLQuery query = getCurrentSession().createSQLQuery(sqlAtt);
                query.executeUpdate();

            }
        }
        return true;
    }

    @Override
    public Session getCurrentSession() throws Exception {
        Session session = sessionFactory.getCurrentSession();
        return session;
    }

    @Override
    public List<Integer> findCarts(Long clientFBId, Integer brandId) throws Exception {
        List<Integer> carts = new ArrayList<>();
        String sql  = "SELECT id FROM cart WHERE customer_fb_id = :clientFBId AND brand_id != :brandId";
        SQLQuery query = getCurrentSession().createSQLQuery(sql);
        query.setParameter("clientFBId", clientFBId);
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


}
