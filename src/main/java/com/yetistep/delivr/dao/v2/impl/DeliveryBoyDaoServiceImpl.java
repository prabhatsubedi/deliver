package com.yetistep.delivr.dao.v2.impl;

import com.yetistep.delivr.dao.v2.inf.DeliveryBoyDaoService;
import com.yetistep.delivr.model.DeliveryBoyEntity;
import com.yetistep.delivr.model.Page;
import com.yetistep.delivr.util.HibernateUtil;
import org.hibernate.*;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
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
        return (DeliveryBoyEntity) getCurrentSession().get(DeliveryBoyEntity.class, id);
    }

    @Override
    public List<DeliveryBoyEntity> findAll() throws Exception {
        return (List<DeliveryBoyEntity>) getCurrentSession().createCriteria(DeliveryBoyEntity.class).list();
    }


    @Override
    public Boolean save(DeliveryBoyEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean update(DeliveryBoyEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean delete(DeliveryBoyEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Session getCurrentSession() throws Exception {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<DeliveryBoyEntity> findAll(Page page) throws Exception {
        List<DeliveryBoyEntity> deliveryBoys  = new ArrayList<DeliveryBoyEntity>();
        Criteria criteria  = getCurrentSession().createCriteria(DeliveryBoyEntity.class);
        HibernateUtil.fillPaginationCriteria(criteria, page, DeliveryBoyEntity.class);
        deliveryBoys = criteria.list();
        return deliveryBoys;
    }

    @Override
    public Integer getTotalNumberOfDboys(Page page) throws Exception {
        /*String sqQuery =    "SELECT COUNT(db.id) FROM delivery_boys db";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqQuery);
        BigInteger cnt = (BigInteger) query.uniqueResult();
        return cnt.intValue();*/

        Criteria criteria  = getCurrentSession().createCriteria(DeliveryBoyEntity.class);
        HibernateUtil.fillPaginationCriteria(criteria, page, DeliveryBoyEntity.class);
        return (int) (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
    }


}
