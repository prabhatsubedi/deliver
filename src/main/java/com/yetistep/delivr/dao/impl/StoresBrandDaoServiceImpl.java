package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.StoresBrandDaoService;
import com.yetistep.delivr.enums.Status;
import com.yetistep.delivr.model.Page;
import com.yetistep.delivr.model.StoresBrandEntity;
import com.yetistep.delivr.util.CommonConstants;
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
        return sessionFactory.getCurrentSession();
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
                .add(Projections.property("minOrderAmount"), "minOrderAmount")
        ).setResultTransformer(Transformers.aliasToBean(StoresBrandEntity.class));
        criteria.add(Restrictions.isNotNull("featured")) ;
        criteria.add(Restrictions.eq("status", Status.ACTIVE));
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
                .add(Projections.property("minOrderAmount"), "minOrderAmount")
        ).setResultTransformer(Transformers.aliasToBean(StoresBrandEntity.class));
        criteria.add(Restrictions.isNull("featured"));
        criteria.add(Restrictions.eq("status", Status.ACTIVE));

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
        /*criteria.setProjection(Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("brandName"), "brandName")
                .add(Projections.property("brandLogo"), "brandLogo")
                .add(Projections.property("brandImage"), "brandImage")
                .add(Projections.property("brandUrl"), "brandUrl")
                .add(Projections.property("featured"), "featured")
                .add(Projections.property("priority"), "priority")
                .add(Projections.property("merchant.id"),"merchantId")
        ).setResultTransformer(Transformers.aliasToBean(StoresBrandEntity.class));*/
        criteria.add(Restrictions.or(Restrictions.eq("featured", true), Restrictions.gt("priority", 0)))
                .add(Restrictions.eq("status", Status.ACTIVE))
                    .addOrder(Order.asc("priority"));
        return (List<StoresBrandEntity>) criteria.list();
    }

    @Override
    public Integer getTotalNumberOfStoreBrands() throws Exception {
        Criteria criteriaCount = getCurrentSession().createCriteria(StoresBrandEntity.class);
        criteriaCount.setProjection(Projections.rowCount())
                .add(Restrictions.and(Restrictions.isNull("priority"),
                        Restrictions.or(Restrictions.eq("featured", false), Restrictions.isNull("featured"))))
                .add(Restrictions.eq("status", Status.ACTIVE));
        Long count = (Long) criteriaCount.uniqueResult();
        return (count != null) ? count.intValue() : null;
    }

    @Override
    public List<StoresBrandEntity> findExceptFeaturedAndPriorityBrands(Page page) throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(StoresBrandEntity.class);
        /*criteria.setProjection(Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("brandName"), "brandName")
                .add(Projections.property("brandLogo"), "brandLogo")
                .add(Projections.property("brandImage"), "brandImage")
                .add(Projections.property("brandUrl"), "brandUrl")
                .add(Projections.property("featured"), "featured")
                .add(Projections.property("priority"), "priority")
                .add(Projections.property("merchant.id"),"merchantId")
        ).setResultTransformer(Transformers.aliasToBean(StoresBrandEntity.class));*/
        criteria.add(Restrictions.and(Restrictions.isNull("priority"),
                Restrictions.or(Restrictions.eq("featured", false), Restrictions.isNull("featured"))))
                .add(Restrictions.eq("status", Status.ACTIVE));
        HibernateUtil.fillPaginationCriteria(criteria, page, StoresBrandEntity.class);

        return (List<StoresBrandEntity>) criteria.list();
    }

    @Override
    public Integer getTotalNumberOfInactiveStoreBrands() throws Exception {
        Criteria criteriaCount = getCurrentSession().createCriteria(StoresBrandEntity.class);
        criteriaCount.setProjection(Projections.rowCount())
                .add(Restrictions.eq("status", Status.INACTIVE));
        Long count = (Long) criteriaCount.uniqueResult();
        return (count != null) ? count.intValue() : null;
    }

    @Override
    public List<StoresBrandEntity> findInactiveStoreBrands(Page page) throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(StoresBrandEntity.class);
        /*criteria.setProjection(Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("brandName"), "brandName")
                .add(Projections.property("brandLogo"), "brandLogo")
                .add(Projections.property("brandImage"), "brandImage")
                .add(Projections.property("brandUrl"), "brandUrl")
                .add(Projections.property("featured"), "featured")
                .add(Projections.property("priority"), "priority")
                .add(Projections.property("merchant.id"),"merchantId")
        ).setResultTransformer(Transformers.aliasToBean(StoresBrandEntity.class));*/
        criteria.add(Restrictions.eq("status", Status.INACTIVE));
        HibernateUtil.fillPaginationCriteria(criteria, page, StoresBrandEntity.class);

        return (List<StoresBrandEntity>) criteria.list();
    }

    @Override
    public List<StoresBrandEntity> findStoresBrand(Boolean isPriority, Integer... brandId) throws Exception {
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
                .add(Projections.property("minOrderAmount"), "minOrderAmount")
        ).setResultTransformer(Transformers.aliasToBean(StoresBrandEntity.class));
        criteria.add(Restrictions.in("id", brandId));
        if(isPriority)
            criteria.addOrder(Order.asc("priority"));
        return  (List<StoresBrandEntity>) criteria.list();
    }

    @Override
    public List<Integer> getSearchBrands(String word, Integer limit) throws Exception {
        List<Integer> brandIds;
        String sql = "SELECT id FROM stores_brands " +
                "WHERE brand_name LIKE :word AND status = :status LIMIT :limit";

        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setParameter("word", CommonConstants.DELIMITER+word+CommonConstants.DELIMITER);
        sqlQuery.setParameter("status", Status.ACTIVE.ordinal());
        sqlQuery.setParameter("limit", limit);
        brandIds = sqlQuery.list();
        return brandIds;
    }

    @Override
    public List<Integer> getActiveSearchBrands(String word, Integer limit) throws Exception {
        List<Integer> brandIds;
        String sql = "SELECT sb.id as id FROM stores_brands sb INNER JOIN merchants m on (sb.merchant_id = m.id) " +
                "INNER JOIN users u on(m.user_id = u.id) WHERE sb.brand_name LIKE " +
                ":word AND sb.status = :status AND u.status = :userStatus LIMIT :limit";

        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setParameter("word", CommonConstants.DELIMITER+word+CommonConstants.DELIMITER);
        sqlQuery.setParameter("status", Status.ACTIVE.ordinal());
        sqlQuery.setParameter("userStatus", Status.ACTIVE.ordinal());
        sqlQuery.setParameter("limit", limit);
        brandIds = sqlQuery.list();
        return brandIds;
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

    @Override
    public Integer getMerchantId(Integer brandsId) throws Exception {
        String sql = "SELECT merchant_id FROM stores_brands WHERE id = :brandsId";
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setParameter("brandsId", brandsId);

        return ((Number)sqlQuery.uniqueResult()).intValue();
    }

    @Override
    public List<StoresBrandEntity> findActiveFeaturedBrands() throws Exception {
        List<StoresBrandEntity> storesBrandEntities = null;
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StoresBrandEntity.class);
        criteria.createAlias("merchant", "m");
        criteria.createAlias("m.user", "u");
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
                .add(Projections.property("minOrderAmount"), "minOrderAmount")
        ).setResultTransformer(Transformers.aliasToBean(StoresBrandEntity.class));
        criteria.add(Restrictions.isNotNull("featured")) ;
        criteria.add(Restrictions.eq("status", Status.ACTIVE));
        criteria.add(Restrictions.eq("u.status", Status.ACTIVE));
        storesBrandEntities = criteria.list();
        return storesBrandEntities;
    }

    @Override
    public List<StoresBrandEntity> findActivePriorityExceptFeaturedBrands(String priority) throws Exception {
        List<StoresBrandEntity> storesBrandEntities = null;
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StoresBrandEntity.class);
        criteria.createAlias("merchant", "m");
        criteria.createAlias("m.user", "u");
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
                .add(Projections.property("minOrderAmount"), "minOrderAmount")
        ).setResultTransformer(Transformers.aliasToBean(StoresBrandEntity.class));
        criteria.add(Restrictions.isNull("featured"));
        criteria.add(Restrictions.eq("status", Status.ACTIVE));
        criteria.add(Restrictions.eq("u.status", Status.ACTIVE));

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
    public List<StoresBrandEntity> findActivePriorityBrands(String priority) throws Exception {
        return findActivePriorityExceptFeaturedBrands(priority);
    }
}
