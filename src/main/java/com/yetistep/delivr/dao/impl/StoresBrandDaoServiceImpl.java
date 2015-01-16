package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.StoresBrandDaoService;
import com.yetistep.delivr.enums.Status;
import com.yetistep.delivr.model.Page;
import com.yetistep.delivr.model.StoresBrandEntity;
import com.yetistep.delivr.util.HibernateUtil;
import org.hibernate.*;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;

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
        criteria.add(Restrictions.eq("status", Status.ACTIVE.ordinal()));
        storesBrandEntities = criteria.list();
        return storesBrandEntities;

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
        criteria.add(Restrictions.isNull("featured"));
        criteria.add(Restrictions.eq("status", Status.ACTIVE.ordinal()));

        if(priority == null){
            //All Brands Null at bottom
            criteria.addOrder(Order.asc("priority").nulls(NullPrecedence.LAST));
        } else {
            //Only Priority set brands
            criteria.add(Restrictions.isNotNull("priority")) ;
            criteria.addOrder(Order.asc("priority"));
        }

        storesBrandEntities = criteria.list();
        return storesBrandEntities;
    }

    @Override
    public List<StoresBrandEntity> findPriorityBrands(String priority) throws Exception {
            return findPriorityExceptFeaturedBrands(priority);
    }

    @Override
    public List<StoresBrandEntity> findFeaturedAndPriorityBrands() throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(StoresBrandEntity.class);
        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("brandName"), "brandName")
                .add(Projections.property("brandLogo"), "brandLogo")
                .add(Projections.property("brandImage"), "brandImage")
                .add(Projections.property("brandUrl"), "brandUrl")
                .add(Projections.property("featured"), "featured")
                .add(Projections.property("priority"), "priority")
                .add(Projections.property("merchant.id"),"merchantId")
        ).setResultTransformer(Transformers.aliasToBean(StoresBrandEntity.class));
        criteria.add(Restrictions.or(Restrictions.eq("featured", true), Restrictions.gt("priority", 0)))
                    .addOrder(Order.asc("priority"));
        List<StoresBrandEntity> storesBrandEntities = criteria.list();
        return storesBrandEntities;
    }

    @Override
    public Integer getTotalNumberOfStoreBrands() throws Exception {
        Criteria criteriaCount = getCurrentSession().createCriteria(StoresBrandEntity.class);
        criteriaCount.setProjection(Projections.rowCount())
                .add(Restrictions.and(Restrictions.isNull("priority"),
                        Restrictions.or(Restrictions.eq("featured", false), Restrictions.isNull("featured"))));
        Long count = (Long) criteriaCount.uniqueResult();
        return (count != null) ? count.intValue() : null;
    }

    @Override
    public List<StoresBrandEntity> findExceptFeaturedAndPriorityBrands(Page page) throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(StoresBrandEntity.class);
        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("brandName"), "brandName")
                .add(Projections.property("brandLogo"), "brandLogo")
                .add(Projections.property("brandImage"), "brandImage")
                .add(Projections.property("brandUrl"), "brandUrl")
                .add(Projections.property("featured"), "featured")
                .add(Projections.property("priority"), "priority")
                .add(Projections.property("merchant.id"),"merchantId")
        ).setResultTransformer(Transformers.aliasToBean(StoresBrandEntity.class));
        criteria.add(Restrictions.and(Restrictions.isNull("priority"),
                Restrictions.or(Restrictions.eq("featured", false), Restrictions.isNull("featured"))));
        HibernateUtil.fillPaginationCriteria(criteria, page, StoresBrandEntity.class);

        List<StoresBrandEntity> storesBrandEntities = criteria.list();
        return storesBrandEntities;
    }

    @Override
    public Boolean updateFeatureAndPriorityOfStoreBrands(List<StoresBrandEntity> storeBrands) throws Exception {
        String updateHQL = "UPDATE StoresBrandEntity SET ";
        if (storeBrands.size() > 0) {
            String priorityLoop = "priority = CASE id";
            String featureLoop = ", featured = CASE id";
            /* This two lines are required if no store brands with priorityLoop or featuredLoop found */
            priorityLoop += " WHEN -1 THEN 7 ";
            featureLoop += " WHEN -1 THEN false ";
            for (StoresBrandEntity storeBrand : storeBrands) {
                priorityLoop += " WHEN " + storeBrand.getId() + " THEN " + storeBrand.getPriority();
                featureLoop += " WHEN " + storeBrand.getId() + " THEN " + storeBrand.getFeatured();
            }
            priorityLoop += " ELSE NULL END";
            featureLoop += " ELSE NULL END";
            updateHQL += priorityLoop + featureLoop;
        } else {
            updateHQL += "featured = NULL, priority = NULL";
        }
        System.err.println("HQL:"+updateHQL);
        Query query = getCurrentSession().createQuery(updateHQL);
        query.executeUpdate();
        return true;
    }
}
