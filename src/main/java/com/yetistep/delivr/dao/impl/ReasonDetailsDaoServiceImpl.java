package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.ReasonDetailsDaoService;
import com.yetistep.delivr.model.ReasonDetails;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 2/2/15
 * Time: 4:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReasonDetailsDaoServiceImpl implements ReasonDetailsDaoService {
    @Autowired
    SessionFactory sessionFactory;

    @Override
    public ReasonDetails find(Integer id) throws Exception {
        return (ReasonDetails) getCurrentSession().get(ReasonDetails.class, id);
    }

    @Override
    public List<ReasonDetails> findAll() throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(ReasonDetails.class);
        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("cancelReason"), "cancelReason")
        ).setResultTransformer(Transformers.aliasToBean(ReasonDetails.class));
        criteria.add(Restrictions.eq("status", true));
        List<ReasonDetails> reasonDetailsList = criteria.list();
        return reasonDetailsList;
    }

    @Override
    public Boolean save(ReasonDetails value) throws Exception {
        getCurrentSession().save(value);
        return true;
    }

    @Override
    public Boolean update(ReasonDetails value) throws Exception {
        getCurrentSession().save(value);
        return true;
    }

    @Override
    public Boolean delete(ReasonDetails value) throws Exception {
        getCurrentSession().delete(value);
        return true;
    }

    @Override
    public Session getCurrentSession() throws Exception {
        Session session = sessionFactory.getCurrentSession();
        return session;
    }
}
