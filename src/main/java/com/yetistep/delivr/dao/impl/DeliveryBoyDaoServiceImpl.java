package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.DeliveryBoyDaoService;
import com.yetistep.delivr.model.DeliveryBoyEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 12:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeliveryBoyDaoServiceImpl implements DeliveryBoyDaoService {

    @Autowired
    SessionFactory sessionFactory;


    @Override
    public DeliveryBoyEntity find(Integer id) throws Exception {
        return null;
    }

    @Override
    public List<DeliveryBoyEntity> findAll() throws Exception {
        return null;
    }

    @Override
    public Boolean save(DeliveryBoyEntity value) throws Exception {
        getCurrentSession().save(value);
        return true;
    }

    @Override
    public Boolean update(DeliveryBoyEntity value) throws Exception {
        getCurrentSession().update(value);
        return true;
    }

    @Override
    public Boolean delete(DeliveryBoyEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Session getCurrentSession() throws Exception {
        Session session = sessionFactory.getCurrentSession();
        return session;
    }
}
