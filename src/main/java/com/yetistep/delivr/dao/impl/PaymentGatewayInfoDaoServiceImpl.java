package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.PaymentGatewayInfoDaoService;
import com.yetistep.delivr.model.PaymentGatewayInfoEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 4/21/15
 * Time: 12:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class PaymentGatewayInfoDaoServiceImpl implements PaymentGatewayInfoDaoService {
    @Autowired
    SessionFactory sessionFactory;

    @Override
    public PaymentGatewayInfoEntity find(Integer id) throws Exception {
        return (PaymentGatewayInfoEntity) getCurrentSession().get(PaymentGatewayInfoEntity.class, id);
    }

    @Override
    public List<PaymentGatewayInfoEntity> findAll() throws Exception {
        return (List<PaymentGatewayInfoEntity>) getCurrentSession().createCriteria(PaymentGatewayInfoEntity.class).list();
    }

    @Override
    public Boolean save(PaymentGatewayInfoEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean update(PaymentGatewayInfoEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean delete(PaymentGatewayInfoEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Session getCurrentSession() throws Exception {
        return sessionFactory.getCurrentSession();
    }
}
