package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.StoresBrandDaoService;
import com.yetistep.delivr.model.StoresBrandEntity;
import org.hibernate.*;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/23/14
 * Time: 5:25 PM
 * To change this template use File | Settings | File Templates.
 */

public class StoresBrandDaoServiceImpl implements StoresBrandDaoService{
    @Autowired
    SessionFactory sessionFactory;

    @Override
    public StoresBrandEntity find(Integer id) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<StoresBrandEntity> findAll() throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean save(StoresBrandEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean update(StoresBrandEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean delete(StoresBrandEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Session getCurrentSession() throws Exception {
        Session session = sessionFactory.getCurrentSession();
        return session;
    }

    @Override
    public List<StoresBrandEntity> findFeaturedBrands() throws Exception {
        List<StoresBrandEntity> storesBrandEntities = null;
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StoresBrandEntity.class);
        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("openingTime"), "openingTime")
                .add(Projections.property("closingTime"), "closingTime")
                .add(Projections.property("brandName"), "brandName")
                .add(Projections.property("brandLogo"), "brandLogo")
                .add(Projections.property("brandImage"), "brandImage")
                .add(Projections.property("brandUrl"), "brandUrl")
                .add(Projections.property("featured"), "featured")
                .add(Projections.property("priority"), "priority")
        ).setResultTransformer(Transformers.aliasToBean(StoresBrandEntity.class));
        criteria.add(Restrictions.isNotNull("featured")) ;
        criteria.addOrder(Order.asc("priority"));
        storesBrandEntities = criteria.list();
        return storesBrandEntities.size() > 0 ? storesBrandEntities : null;

    }

    public List<StoresBrandEntity> findPriorityExceptFeaturedBrands(String priority) throws Exception {
        List<StoresBrandEntity> storesBrandEntities = null;
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StoresBrandEntity.class);
        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("openingTime"), "openingTime")
                .add(Projections.property("closingTime"), "closingTime")
                .add(Projections.property("brandName"), "brandName")
                .add(Projections.property("brandLogo"), "brandLogo")
                .add(Projections.property("brandImage"), "brandImage")
                .add(Projections.property("brandUrl"), "brandUrl")
                .add(Projections.property("featured"), "featured")
                .add(Projections.property("priority"), "priority")
        ).setResultTransformer(Transformers.aliasToBean(StoresBrandEntity.class));
        criteria.add(Restrictions.isNull("featured")) ;

        if(priority == null){
            //All Brands Null at bottom
            criteria.addOrder(Order.asc("priority").nulls(NullPrecedence.LAST));
        } else {
            //Only Priority set brands
            criteria.add(Restrictions.isNotNull("priority")) ;
            criteria.addOrder(Order.asc("priority"));
        }

        storesBrandEntities = criteria.list();
        return storesBrandEntities.size() > 0 ? storesBrandEntities : null;
    }

    @Override
    public List<StoresBrandEntity> findPriorityBrands(String priority) throws Exception {
            return findPriorityExceptFeaturedBrands(priority);
    }
}
