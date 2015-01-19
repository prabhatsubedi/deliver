package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.AddressDaoService;
import com.yetistep.delivr.model.AddressEntity;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 1/19/15
 * Time: 12:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class AddressDaoServiceImpl implements AddressDaoService{
    @Autowired
    SessionFactory sessionFactory;
    @Override
    public AddressEntity find(Integer id) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<AddressEntity> findAll() throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean save(AddressEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean update(AddressEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean delete(AddressEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Session getCurrentSession() throws Exception {
        Session session = sessionFactory.getCurrentSession();
        return session;
    }

    @Override
    public Boolean findValidMobile(Integer userId, String mobileNo) throws Exception {
        String sql = "SELECT COUNT(*) FROM address " +
                "WHERE user_id = :userId AND mobile_no = :mobileNo AND verified = :verified";

        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setParameter("userId", userId);
        sqlQuery.setParameter("mobileNo", mobileNo);
        sqlQuery.setParameter("verified", true);
        Integer count = ((Number) sqlQuery.uniqueResult()).intValue();
        if(count > 0)
            return true;
        else
            return false;
    }
}
