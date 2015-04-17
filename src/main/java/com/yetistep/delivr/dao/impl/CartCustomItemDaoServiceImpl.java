package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.CartCustomItemDaoService;
import com.yetistep.delivr.model.CartCustomItemEntity;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 4/13/15
 * Time: 1:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class CartCustomItemDaoServiceImpl implements CartCustomItemDaoService {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public CartCustomItemEntity find(Integer id) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<CartCustomItemEntity> findAll() throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public CartCustomItemEntity findCustomItem(Integer cartId) throws Exception{
        List<CartCustomItemEntity> customItems  = new ArrayList<CartCustomItemEntity>();
        Criteria criteria  = getCurrentSession().createCriteria(CartCustomItemEntity.class);
        criteria.add(Restrictions.eq("cart.id", cartId));
        criteria.addOrder(Order.desc("id"));
        customItems = criteria.list();
        return customItems.size()>0?customItems.get(0):null;

    }

    @Override
    public Boolean deleteCartCustomItems(List<Integer> cartCustomItems) throws Exception {
        String sqlAtt = "Delete from cart_custom_items where id IN(:cartCustomItems)";
        SQLQuery query = getCurrentSession().createSQLQuery(sqlAtt);
        query.setParameterList("cartCustomItems", cartCustomItems);
        query.executeUpdate();
        return true;
    }

    @Override
    public List<Integer> findCartCustomItems(List<Integer> carts) throws Exception {
        List<Integer> cartAttributes = new ArrayList<>();
        String sql  = "SELECT id FROM cart_custom_items WHERE cart_id IN(:carts)";
        SQLQuery query = getCurrentSession().createSQLQuery(sql);
        query.setParameterList("carts", carts);
        cartAttributes = query.list();
        return cartAttributes;
    }

    @Override
    public Boolean save(CartCustomItemEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean update(CartCustomItemEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean delete(CartCustomItemEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Session getCurrentSession() throws Exception {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public Boolean deleteCartCustomItem(Integer cartId) throws Exception {
        String sqlAtt = "Delete from cart_custom_items where cart_id=:cartId";
        SQLQuery query = getCurrentSession().createSQLQuery(sqlAtt);
        query.setParameter("cartId", cartId);
        query.executeUpdate();
        return true;
    }
}
