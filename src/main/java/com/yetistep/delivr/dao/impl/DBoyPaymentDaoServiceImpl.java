package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.DBoyPaymentDaoService;
import com.yetistep.delivr.model.DBoyPaymentEntity;
import com.yetistep.delivr.model.InvoiceEntity;
import com.yetistep.delivr.model.Page;
import com.yetistep.delivr.util.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 5/5/15
 * Time: 1:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class DBoyPaymentDaoServiceImpl implements DBoyPaymentDaoService {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public DBoyPaymentEntity find(Integer id) throws Exception {
        return (DBoyPaymentEntity) getCurrentSession().get(DBoyPaymentEntity.class, id);
    }

    @Override
    public List<DBoyPaymentEntity> findAll() throws Exception {
       return (List<DBoyPaymentEntity>) getCurrentSession().createCriteria(DBoyPaymentEntity.class).list();
    }

    @Override
    public List<DBoyPaymentEntity> findAllPayStatementsOfShopper(Page page, Integer dBoyId) throws Exception {
        /*Criteria criteria  = getCurrentSession().createCriteria(DBoyPaymentEntity.class);
        criteria.add(Restrictions.eq("deliveryBoy.id", dBoyId));
        HibernateUtil.fillPaginationCriteria(criteria, page, DBoyPaymentEntity.class);
        return (List<DBoyPaymentEntity>) criteria.list();*/
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DBoyPaymentEntity.class);
        criteria.add(Restrictions.eq("deliveryBoy.id", dBoyId));
        HibernateUtil.fillPaginationCriteria(criteria, page, DBoyPaymentEntity.class);
        return (List<DBoyPaymentEntity>) criteria.list();
    }


    @Override
    public Boolean save(DBoyPaymentEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean update(DBoyPaymentEntity value) throws Exception {
        getCurrentSession().update(value);
        return true;
    }

    @Override
    public Boolean delete(DBoyPaymentEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Session getCurrentSession() throws Exception {
        return sessionFactory.getCurrentSession();
    }


    @Override
    public Integer getTotalNumberOfPayStatements(Integer dBoyId) throws Exception {
        String sqQuery =    "SELECT COUNT(dp.id) FROM dboy_payment dp WHERE dp.dboy_id =:dBoyId";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqQuery);
        query.setParameter("dBoyId", dBoyId);

        BigInteger cnt = (BigInteger) query.uniqueResult();
        return cnt.intValue();
    }
}
