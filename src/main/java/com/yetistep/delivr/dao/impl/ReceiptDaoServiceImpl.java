package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.ReceiptDaoService;
import com.yetistep.delivr.model.ReceiptEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 3/3/15
 * Time: 9:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class ReceiptDaoServiceImpl implements ReceiptDaoService {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public ReceiptEntity find(Integer id) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ReceiptEntity> findAll() throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean save(ReceiptEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean update(ReceiptEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean delete(ReceiptEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Session getCurrentSession() throws Exception {
        return sessionFactory.getCurrentSession();
    }
}
