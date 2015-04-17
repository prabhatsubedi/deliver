package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.WalletTransactionDaoService;
import com.yetistep.delivr.enums.PaymentMode;
import com.yetistep.delivr.model.Page;
import com.yetistep.delivr.model.WalletTransactionEntity;
import com.yetistep.delivr.util.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.ArrayList;
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

    @Override
    public List<WalletTransactionEntity> getWalletTransactions(Page page, Long fbId, List<PaymentMode> paymentModes) throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(WalletTransactionEntity.class);
        criteria.createAlias("customer", "c");
        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("transactionDate"), "transactionDate")
                .add(Projections.property("transactionAmount"), "transactionAmount")
                .add(Projections.property("remarks"), "remarks")
                .add(Projections.property("accountType"), "accountType")
                .add(Projections.property("paymentMode"), "paymentMode")
                .add(Projections.property("order.id"), "orderId")
        ).setResultTransformer(Transformers.aliasToBean(WalletTransactionEntity.class));
        criteria.add(Restrictions.eq("c.facebookId", fbId))
                .add(Restrictions.in("paymentMode", paymentModes))
                .addOrder(Order.desc("id"));
        HibernateUtil.fillPaginationCriteria(criteria, page, WalletTransactionEntity.class);
        List<WalletTransactionEntity> walletTransactionEntities = criteria.list();
        return walletTransactionEntities;

    }

    @Override
    public Integer getTotalNumberOfWalletTransactions(Long fbId, List<PaymentMode> paymentModes) throws Exception {
        String sqQuery = "SELECT COUNT(wt.id) FROM wallet_transaction wt INNER JOIN customers c " +
                "ON (c.id = wt.customer_id) WHERE c.facebook_id =:facebookId AND payment_mode in (:paymentMode)";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqQuery);
        List<Integer> paymentModeValues = new ArrayList<>();
        for(PaymentMode paymentMode: paymentModes){
            paymentModeValues.add(paymentMode.ordinal());
        }
        query.setParameter("facebookId", fbId);
        query.setParameterList("paymentMode", paymentModeValues);
        BigInteger cnt = (BigInteger) query.uniqueResult();
        return cnt.intValue();
    }
}
