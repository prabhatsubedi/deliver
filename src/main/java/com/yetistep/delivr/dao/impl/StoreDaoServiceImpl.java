package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.StoreDaoService;
import com.yetistep.delivr.enums.Status;
import com.yetistep.delivr.model.CategoryEntity;
import com.yetistep.delivr.model.StoreEntity;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/24/14
 * Time: 3:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class StoreDaoServiceImpl implements StoreDaoService{
    @Autowired
    SessionFactory sessionFactory;


    @Override
    public StoreEntity find(Integer id) throws Exception {
        return (StoreEntity) getCurrentSession().get(StoreEntity.class, id);
    }

    @Override
    public List<StoreEntity> findAll() throws Exception {
        return (List<StoreEntity>) getCurrentSession().createCriteria(StoreEntity.class).list();
    }

    @Override
    public Boolean save(StoreEntity value) throws Exception {

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean update(StoreEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean delete(StoreEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Session getCurrentSession() throws Exception {
        Session session = sessionFactory.getCurrentSession();
        return session;
    }

    @Override
    public List<StoreEntity> findStores(List<Integer> ignoreBrand) throws Exception {
        List<StoreEntity> storeEntities = null;
        Criteria criteria = getCurrentSession().createCriteria(StoreEntity.class);
        if (ignoreBrand.size() > 0)
            criteria.add(Restrictions.not(Restrictions.in("storesBrand.id", ignoreBrand)));
        criteria.add(Restrictions.eq("status", Status.ACTIVE));

        storeEntities = criteria.list();

        return storeEntities;
    }

    @Override
    public Integer getActiveStores(Integer brandId) throws Exception {
        String sql = "SELECT COUNT(*) FROM stores WHERE stores_brand_id = :brandId AND status = :status";
        SQLQuery query = getCurrentSession().createSQLQuery(sql);
        query.setParameter("brandId", brandId);
        query.setParameter("status", Status.ACTIVE.ordinal());

        Integer activeStore = ((Number) query.uniqueResult()).intValue();

        return activeStore;
    }

//    @Override
//    public List<CategoryEntity> findItemCategory(Integer brandId) throws Exception {
//        List<CategoryEntity> categoryEntities = new ArrayList<>();
//
//        String sql1 = "SELECT DISTINCT DISTINCT(cat.id), cat.name, cat.parent_id as parentId FROM stores store " +
//                "INNER JOIN items_stores ist ON(ist.store_id = store.id) " +
//                "INNER JOIN items it ON(it.id = ist.item_id) " +
//                "INNER JOIN categories cat ON(cat.id = it.category_id) " +
//                "WHERE store.stores_brand_id = " + brandId;
//        SQLQuery query =  getCurrentSession().createSQLQuery(sql1);
////        query.addEntity(CategoryEntity.class);
//
//        query.setResultTransformer(Transformers.aliasToBean(CategoryEntity.class));
//        categoryEntities = query.list();
//
//        return categoryEntities;
//    }
}
