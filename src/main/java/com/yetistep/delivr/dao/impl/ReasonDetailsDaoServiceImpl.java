package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.ReasonDetailsDaoService;
import com.yetistep.delivr.model.ReasonDetailsEntity;
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
    public ReasonDetailsEntity find(Integer id) throws Exception {
        return (ReasonDetailsEntity) getCurrentSession().get(ReasonDetailsEntity.class, id);
    }

    @Override
    public List<ReasonDetailsEntity> findAll() throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(ReasonDetailsEntity.class);
        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("cancelReason"), "cancelReason")
        ).setResultTransformer(Transformers.aliasToBean(ReasonDetailsEntity.class));
        criteria.add(Restrictions.eq("status", true));
        List<ReasonDetailsEntity> reasonDetailsList = criteria.list();
        return reasonDetailsList;
    }

    @Override
    public Boolean save(ReasonDetailsEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean update(ReasonDetailsEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean delete(ReasonDetailsEntity value) throws Exception {
        getCurrentSession().delete(value);
        return true;
    }

    @Override
    public Session getCurrentSession() throws Exception {
        Session session = sessionFactory.getCurrentSession();
        return session;
    }
}
