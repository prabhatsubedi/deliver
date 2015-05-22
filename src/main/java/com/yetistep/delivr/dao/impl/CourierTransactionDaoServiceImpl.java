package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.CourierTransactionDaoService;
import com.yetistep.delivr.model.CourierTransactionEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/19/14
 * Time: 10:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class CourierTransactionDaoServiceImpl implements CourierTransactionDaoService{
    @Autowired
    SessionFactory sessionFactory;

    @Override
    public CourierTransactionEntity find(Integer id) throws Exception {
        return (CourierTransactionEntity) getCurrentSession().get(CourierTransactionEntity.class, id);
    }

    @Override
    public List<CourierTransactionEntity> findAll() throws Exception {
        return (List<CourierTransactionEntity>) getCurrentSession().createCriteria(CourierTransactionEntity.class).list();
    }

    @Override
    public Boolean save(CourierTransactionEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean update(CourierTransactionEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean delete(CourierTransactionEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Session getCurrentSession() throws Exception {
        return sessionFactory.getCurrentSession();
    }
}
