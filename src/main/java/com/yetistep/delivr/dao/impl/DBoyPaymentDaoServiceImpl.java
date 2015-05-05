package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.DBoyPaymentDaoService;
import com.yetistep.delivr.model.DBoyPaymentEntity;
import com.yetistep.delivr.model.InvoiceEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 5/5/15
 * Time: 1:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class DBoyPaymentDaoServiceImpl implements DBoyPaymentDaoService {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public DBoyPaymentEntity find(Integer id) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<DBoyPaymentEntity> findAll() throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean save(DBoyPaymentEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean update(DBoyPaymentEntity value) throws Exception {
        getCurrentSession().update(value);
        return true;
    }

    @Override
    public Boolean delete(DBoyPaymentEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Session getCurrentSession() throws Exception {
        return sessionFactory.getCurrentSession();
    }
}
