package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.DBoyAdvanceAmountDaoService;
import com.yetistep.delivr.model.DBoyAdvanceAmountEntity;
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
 * Time: 10:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class DBoyAdvanceAmountDaoServiceImpl implements DBoyAdvanceAmountDaoService {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public DBoyAdvanceAmountEntity find(Integer id) throws Exception {
        return  (DBoyAdvanceAmountEntity) getCurrentSession().get(DBoyAdvanceAmountEntity.class, id);
    }

    @Override
    public List<DBoyAdvanceAmountEntity> findAll() throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean save(DBoyAdvanceAmountEntity value) throws Exception {
        getCurrentSession().save(value);
        return true;
    }

    @Override
    public Boolean update(DBoyAdvanceAmountEntity value) throws Exception {
        getCurrentSession().update(value);
        return true;
    }

    @Override
    public Boolean delete(DBoyAdvanceAmountEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Session getCurrentSession() throws Exception {
        Session session = sessionFactory.getCurrentSession();
        return session;
    }

    @Override
    public Integer getTotalNumbersOfAdvanceAmounts(Integer dBoyId) throws Exception{
        String sqQuery =    "SELECT COUNT(aa.id) FROM dboy_advance_amounts aa WHERE aa.dboy_id =:dBoyId";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqQuery);
        query.setParameter("dBoyId", dBoyId);

        BigInteger cnt = (BigInteger) query.uniqueResult();
        return cnt.intValue();
    }


    @Override
    public List<DBoyAdvanceAmountEntity> getAdvanceAmounts(Integer dBoyId, Page page) throws Exception{
        List<DBoyAdvanceAmountEntity> merchants  = new ArrayList<DBoyAdvanceAmountEntity>();
        Criteria criteria  = getCurrentSession().createCriteria(DBoyAdvanceAmountEntity.class);
        criteria.add(Restrictions.eq("deliveryBoy.id", dBoyId));
        HibernateUtil.fillPaginationCriteria(criteria, page, DBoyAdvanceAmountEntity.class);
        merchants = criteria.list();
        return merchants;
    }
}
