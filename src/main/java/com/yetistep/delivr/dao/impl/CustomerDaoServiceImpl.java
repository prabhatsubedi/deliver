package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.CustomerDaoService;
import com.yetistep.delivr.model.AddressEntity;
import com.yetistep.delivr.model.CustomerEntity;
import com.yetistep.delivr.model.OrderEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 12:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomerDaoServiceImpl implements CustomerDaoService {

    @Autowired
    SessionFactory sessionFactory;


    @Override
    public CustomerEntity find(Integer id) throws Exception {
        return (CustomerEntity) getCurrentSession().get(CustomerEntity.class, id);
    }

    @Override
    public List<CustomerEntity> findAll() throws Exception {
        return (List<CustomerEntity>) getCurrentSession().createCriteria(CustomerEntity.class).list();
    }

    @Override
    public Boolean save(CustomerEntity value) throws Exception {
        getCurrentSession().save(value);
        return true;
    }

    @Override
    public Boolean update(CustomerEntity value) throws Exception {
        getCurrentSession().update(value);
        return true;
    }

    @Override
    public Boolean delete(CustomerEntity value) throws Exception {
        getCurrentSession().delete(value);
        return true;
    }

    @Override
    public Session getCurrentSession() throws Exception {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public AddressEntity findAddressById(Integer id) throws Exception {
        return (AddressEntity) getCurrentSession().get(AddressEntity.class, id);
    }

    @Override
    public void saveOrder(OrderEntity value) throws Exception {
        getCurrentSession().save(value);
    }

    @Override
    public CustomerEntity find(Long facebookId) throws Exception {
        List<CustomerEntity> customerEntities = null;
        Criteria criteria = getCurrentSession().createCriteria(CustomerEntity.class);
        criteria.add(Restrictions.eq("facebookId", facebookId));
        customerEntities = criteria.list();
        return customerEntities.size() > 0 ? customerEntities.get(0) : null;

    }
}


