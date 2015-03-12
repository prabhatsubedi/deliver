package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.InvoiceDaoService;
import com.yetistep.delivr.model.InvoiceEntity;
import com.yetistep.delivr.model.Page;
import com.yetistep.delivr.model.StoresBrandEntity;
import com.yetistep.delivr.util.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 2/23/15
 * Time: 11:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class InvoiceDaoServiceImpl implements InvoiceDaoService {

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public InvoiceEntity find(Integer id) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<InvoiceEntity> findAll() throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<InvoiceEntity> findInvoicesByMerchant(Integer merchantId, Page page) throws Exception {
        List<InvoiceEntity> invoiceEntities = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(InvoiceEntity.class);
        criteria.add(Restrictions.and(Restrictions.eq("merchant.id", merchantId))) ;
        HibernateUtil.fillPaginationCriteria(criteria, page, InvoiceEntity.class);
        invoiceEntities = criteria.list();
        return invoiceEntities;
    }

    @Override
    public List<InvoiceEntity> findInvoicesByMerchant(Integer merchantId, Page page, Date fromDate, Date toDate) throws Exception {
        List<InvoiceEntity> invoiceEntities = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(InvoiceEntity.class);
        criteria.add(Restrictions.and(Restrictions.eq("merchant.id", merchantId), Restrictions.ge("generatedDate", fromDate), Restrictions.lt("generatedDate", toDate)));
        HibernateUtil.fillPaginationCriteria(criteria, page, InvoiceEntity.class);
        invoiceEntities = criteria.list();
        return invoiceEntities;
    }

    @Override
    public Boolean save(InvoiceEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean update(InvoiceEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean delete(InvoiceEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Session getCurrentSession() throws Exception {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public Integer getTotalNumberOfInvoices(Integer merchantId) throws Exception {
        String sqQuery =    "SELECT COUNT(i.id) FROM invoices i where i.merchant_id =:merchantId";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqQuery);
        query.setParameter("merchantId", merchantId);
        BigInteger cnt = (BigInteger) query.uniqueResult();
        return cnt.intValue();
    }


}
