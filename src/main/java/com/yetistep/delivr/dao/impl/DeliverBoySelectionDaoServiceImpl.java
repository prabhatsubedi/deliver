package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.DeliveryBoySelectionDaoService;
import com.yetistep.delivr.model.DeliveryBoySelectionEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 12/24/14
 * Time: 11:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class DeliverBoySelectionDaoServiceImpl implements DeliveryBoySelectionDaoService {
    @Autowired
    SessionFactory sessionFactory;

    @Override
    public DeliveryBoySelectionEntity find(Integer id) throws Exception {
        return (DeliveryBoySelectionEntity) getCurrentSession().get(DeliveryBoySelectionEntity.class, id);
    }

    @Override
    public List<DeliveryBoySelectionEntity> findAll() throws Exception {
        return (List<DeliveryBoySelectionEntity>) getCurrentSession().createCriteria(DeliveryBoySelectionEntity.class).list();
    }

    @Override
    public Boolean save(DeliveryBoySelectionEntity value) throws Exception {
        getCurrentSession().save(value);
        return true;
    }

    @Override
    public Boolean update(DeliveryBoySelectionEntity value) throws Exception {
        getCurrentSession().update(value);
        return true;
    }

    @Override
    public Boolean delete(DeliveryBoySelectionEntity value) throws Exception {
        getCurrentSession().delete(value);
        return true;
    }

    @Override
    public Session getCurrentSession() throws Exception {
        Session session = sessionFactory.getCurrentSession();
        return session;
    }
}
