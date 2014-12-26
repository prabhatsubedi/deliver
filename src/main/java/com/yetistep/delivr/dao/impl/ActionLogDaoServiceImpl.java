package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.ActionLogDaoService;
import com.yetistep.delivr.model.ActionLogEntity;
import com.yetistep.delivr.model.Page;
import com.yetistep.delivr.util.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 12/2/14
 * Time: 12:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActionLogDaoServiceImpl implements ActionLogDaoService {
    @Autowired
    SessionFactory sessionFactory;

    @Override
    public ActionLogEntity find(Long id) throws Exception {
        return (ActionLogEntity) getCurrentSession().get(ActionLogEntity.class, id);
    }

    @Override
    public List<ActionLogEntity> findAll() throws Exception {
        return (List<ActionLogEntity>) getCurrentSession().createCriteria(ActionLogEntity.class).list();
    }

    @Override
    public Boolean save(ActionLogEntity value) throws Exception {
        getCurrentSession().save(value);
        return true;
    }

    @Override
    public Boolean update(ActionLogEntity value) throws Exception {
        // TODO
        return null;
    }

    @Override
    public Boolean delete(ActionLogEntity value) throws Exception {
        getCurrentSession().delete(value);
        return true;
    }

    @Override
    public Session getCurrentSession() throws Exception {
        Session session = sessionFactory.getCurrentSession();
        return session;
    }

    @Override
    public List<ActionLogEntity> findActionLogPaginated(Page page) throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(ActionLogEntity.class);
        HibernateUtil.fillPaginationCriteria(criteria, page, ActionLogEntity.class);
        List<ActionLogEntity> actionLogEntities = criteria.list();
        return actionLogEntities;
    }

    @Override
    public Integer getTotalNumberOfActionLogs() throws Exception {
        Criteria criteriaCount = getCurrentSession().createCriteria(ActionLogEntity.class);
        criteriaCount.setProjection(Projections.rowCount());
        Long count = (Long) criteriaCount.uniqueResult();
        return (count != null) ? count.intValue() : null;
    }
}
