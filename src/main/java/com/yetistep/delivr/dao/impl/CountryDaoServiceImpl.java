package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.CountryDaoService;
import com.yetistep.delivr.model.CountryEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 12/3/14
 * Time: 3:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class CountryDaoServiceImpl implements CountryDaoService {
    @Autowired
    SessionFactory sessionFactory;

    @Override
    public CountryEntity find(Integer id) throws Exception {
        return (CountryEntity) getCurrentSession().get(CountryEntity.class, id);
    }

    @Override
    public List<CountryEntity> findAll() throws Exception {
        return (List<CountryEntity>) getCurrentSession().createCriteria(CountryEntity.class).list();
    }

    @Override
    public Boolean save(CountryEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean update(CountryEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean delete(CountryEntity value) throws Exception {
        getCurrentSession().delete(value);
        return true;
    }

    @Override
    public Session getCurrentSession() throws Exception {
        return sessionFactory.getCurrentSession();
    }
}
