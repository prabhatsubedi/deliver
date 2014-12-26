package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.StoreDaoService;
import com.yetistep.delivr.dao.inf.StoresBrandDaoService;
import com.yetistep.delivr.model.StoreEntity;
import com.yetistep.delivr.model.StoresBrandEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

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
        criteria.add(Restrictions.not(Restrictions.in("storesBrand.id",ignoreBrand)));
        storeEntities = criteria.list();

        return storeEntities;
    }
}
