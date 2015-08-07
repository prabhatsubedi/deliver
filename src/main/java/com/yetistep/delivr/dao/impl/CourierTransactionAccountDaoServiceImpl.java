package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.CourierTransactionAccountDaoService;
import com.yetistep.delivr.model.CourierTransactionAccountEntity;
import com.yetistep.delivr.model.Page;
import com.yetistep.delivr.util.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 8/5/15
 * Time: 4:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class CourierTransactionAccountDaoServiceImpl implements CourierTransactionAccountDaoService {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public CourierTransactionAccountEntity find(Integer id) throws Exception {
        return (CourierTransactionAccountEntity) getCurrentSession().get(CourierTransactionAccountEntity.class, id);
    }

    @Override
    public List<CourierTransactionAccountEntity> findAll() throws Exception {
        Criteria criteria  = getCurrentSession().createCriteria(CourierTransactionAccountEntity.class, "merchant");
        criteria.addOrder(Order.desc("desc"));
        return (List<CourierTransactionAccountEntity>) criteria.list();
    }

    @Override
    public Boolean save(CourierTransactionAccountEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean update(CourierTransactionAccountEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean delete(CourierTransactionAccountEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Session getCurrentSession() throws Exception {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<CourierTransactionAccountEntity> findAllCTA(Integer shopperId, Page page) throws Exception {
        Criteria criteria  = getCurrentSession().createCriteria(CourierTransactionAccountEntity.class, "merchant");
        criteria.add(Restrictions.eq("deliveryBoy.id", shopperId));
        criteria.addOrder(Order.desc("id"));
        HibernateUtil.fillPaginationCriteria(criteria, page, CourierTransactionAccountEntity.class);
        return (List<CourierTransactionAccountEntity>) criteria.list();
    }


    @Override
    public Integer getTotalNumberOfRows(Integer shopperId) throws Exception {
        String sqQuery =    "SELECT COUNT(cta.id) FROM courier_transaction_account cta where deliveryBoy_id=:shopperId";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqQuery);
        query.setParameter("shopperId",shopperId);
        BigInteger cnt = (BigInteger) query.uniqueResult();
        return cnt.intValue();
    }
}
