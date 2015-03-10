package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.InvoiceDaoService;
import com.yetistep.delivr.model.InvoiceEntity;
import com.yetistep.delivr.model.StoresBrandEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
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
    public List<InvoiceEntity> findInvoicesByMerchant(Integer merchantId) throws Exception {
        List<InvoiceEntity> invoiceEntities = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(InvoiceEntity.class);
        criteria.add(Restrictions.and(Restrictions.eq("merchant.id", merchantId))) ;
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


}
