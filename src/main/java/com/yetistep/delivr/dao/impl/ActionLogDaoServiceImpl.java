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
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public void saveAll(List<ActionLogEntity> values) throws Exception {
        Integer i = 0;
        for (ActionLogEntity value: values) {
            getCurrentSession().persist(value);
            if ( i % 20 == 0 ) { //20, same as the JDBC batch size
                //flush a batch of inserts and release memory:
                getCurrentSession().flush();
                getCurrentSession().clear();
            }
            i++;
        }
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
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<ActionLogEntity> findActionLogPaginated(Page page) throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(ActionLogEntity.class);
        HibernateUtil.fillPaginationCriteria(criteria, page, ActionLogEntity.class);
        return (List<ActionLogEntity>) criteria.list();
    }

    @Override
    public Integer getTotalNumberOfActionLogs() throws Exception {
        Criteria criteriaCount = getCurrentSession().createCriteria(ActionLogEntity.class);
        criteriaCount.setProjection(Projections.rowCount());
        Long count = (Long) criteriaCount.uniqueResult();
        return (count != null) ? count.intValue() : null;
    }
}
