package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.WalletTransactionDaoService;
import com.yetistep.delivr.model.WalletTransactionEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 4/1/15
 * Time: 10:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class WalletTransactionDaoServiceImpl implements WalletTransactionDaoService {
    @Autowired
    SessionFactory sessionFactory;

    @Override
    public WalletTransactionEntity find(Integer id) throws Exception {
        return (WalletTransactionEntity) getCurrentSession().get(WalletTransactionEntity.class, id);
    }

    @Override
    public List<WalletTransactionEntity> findAll() throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean save(WalletTransactionEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean update(WalletTransactionEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean delete(WalletTransactionEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Session getCurrentSession() throws Exception {
        Session session = sessionFactory.getCurrentSession();
        return session;
    }

    @Override
    public WalletTransactionEntity getLatestWalletTransaction(Integer customerId) throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(WalletTransactionEntity.class)
                .add(Restrictions.eq("customer.id", customerId))
                .addOrder(Order.desc("id"))
                .setMaxResults(1);
        WalletTransactionEntity walletTransactionEntity = (WalletTransactionEntity) criteria.uniqueResult();
        return walletTransactionEntity;
    }
}
