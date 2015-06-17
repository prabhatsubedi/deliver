package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.IDGeneratorDaoService;
import com.yetistep.delivr.model.IDGeneratorEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 6/16/15
 * Time: 4:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class IDGeneratorDaoServiceImpl implements IDGeneratorDaoService {
    @Autowired
    SessionFactory sessionFactory;

    @Override
    public IDGeneratorEntity find(Integer id) throws Exception {
        return (IDGeneratorEntity) getCurrentSession().get(IDGeneratorEntity.class, id);
    }

    @Override
    public List<IDGeneratorEntity> findAll() throws Exception {
        return (List<IDGeneratorEntity>) getCurrentSession().createCriteria(IDGeneratorEntity.class).list();
    }

    @Override
    public Boolean save(IDGeneratorEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean update(IDGeneratorEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean delete(IDGeneratorEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Session getCurrentSession() throws Exception {
        return sessionFactory.getCurrentSession();
    }
}
