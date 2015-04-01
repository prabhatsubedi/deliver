package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.DBoySubmittedAmountDaoService;
import com.yetistep.delivr.model.DBoySubmittedAmountEntity;
import com.yetistep.delivr.model.DeliveryBoyEntity;
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
 * Date: 4/1/15
 * Time: 10:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class DBoySubmittedAmountDaoServiceImpl implements DBoySubmittedAmountDaoService {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public DeliveryBoyEntity find(Integer id) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<DeliveryBoyEntity> findAll() throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean save(DeliveryBoyEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean update(DeliveryBoyEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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

    @Override
    public Integer getTotalNumbersAcknowledgements(Integer dBoyId) throws Exception {
        String sqQuery =    "SELECT COUNT(sa.id) FROM dboy_submitted_amounts sa WHERE sa.dboy_id =:dBoyId";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqQuery);
        query.setParameter("dBoyId", dBoyId);

        BigInteger cnt = (BigInteger) query.uniqueResult();
        return cnt.intValue();
    }

    @Override
    public List<DBoySubmittedAmountEntity> getcknowledgements(Integer dBoyId, Page page) throws Exception {
        List<DBoySubmittedAmountEntity> merchants  = new ArrayList<DBoySubmittedAmountEntity>();
        Criteria criteria  = getCurrentSession().createCriteria(DBoySubmittedAmountEntity.class);
        criteria.add(Restrictions.eq("deliveryBoy.id", dBoyId));
        HibernateUtil.fillPaginationCriteria(criteria, page, DBoySubmittedAmountEntity.class);
        merchants = criteria.list();
        return merchants;
    }
}
