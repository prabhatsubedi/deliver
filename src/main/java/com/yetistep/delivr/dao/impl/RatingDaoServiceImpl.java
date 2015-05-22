package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.RatingDaoService;
import com.yetistep.delivr.model.RatingEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 2/20/15
 * Time: 10:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class RatingDaoServiceImpl implements RatingDaoService {
    @Autowired
    SessionFactory sessionFactory;

    @Override
    public RatingEntity find(Integer id) throws Exception {
        return (RatingEntity) getCurrentSession().get(RatingEntity.class, id);
    }

    @Override
    public List<RatingEntity> findAll() throws Exception {
        return (List<RatingEntity>) getCurrentSession().createCriteria(RatingEntity.class).list();
    }

    @Override
    public Boolean save(RatingEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean update(RatingEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean delete(RatingEntity value) throws Exception {
        return null;
    }

    @Override
    public Session getCurrentSession() throws Exception {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public RatingEntity getCustomerSideRatingFromOrderId(Integer orderId) throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(RatingEntity.class);
        criteria.add(Restrictions.eq("order.id", orderId));
        return (RatingEntity) criteria.uniqueResult();
    }
}
