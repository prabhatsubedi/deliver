package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.MerchantDaoService;
import com.yetistep.delivr.model.MerchantEntity;
import com.yetistep.delivr.model.StoreEntity;
import com.yetistep.delivr.model.StoresBrandsEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 12:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchantDaoServiceImpl implements MerchantDaoService {

    @Autowired
    SessionFactory sessionFactory;


    @Override
    public MerchantEntity find(Integer id) throws Exception {
        return (MerchantEntity) getCurrentSession().get(MerchantEntity.class, id);
    }

    @Override
    public List<MerchantEntity> findAll() throws Exception {
        return (List<MerchantEntity>) getCurrentSession().createCriteria(MerchantEntity.class).list();
    }

    @Override
    public Boolean save(MerchantEntity value) throws Exception {
        getCurrentSession().save(value);
        return true;
    }

    @Override
    public Boolean update(MerchantEntity value) throws Exception {
        getCurrentSession().update(value);
        return true;
    }

    @Override
    public Boolean delete(MerchantEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Session getCurrentSession() throws Exception {
        Session session = sessionFactory.getCurrentSession();
        return session;
    }

    @Override
    public void saveStore(List<StoreEntity> values) throws Exception {

        for (StoreEntity store: values){
            getCurrentSession().save(store);
        }

    }

    @Override
    public StoresBrandsEntity getBrandByBrandName(String brandName) throws Exception {

        List<StoresBrandsEntity> storeBrandList = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StoresBrandsEntity.class);
        criteria.add(Restrictions.eq("brandName", brandName));
        storeBrandList = criteria.list();

        return storeBrandList.size() > 0 ? storeBrandList.get(0) : null;
    }
}
