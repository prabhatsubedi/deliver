package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.UserDeviceDaoService;
import com.yetistep.delivr.model.UserDeviceEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/30/14
 * Time: 2:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserDeviceDaoServiceImpl implements UserDeviceDaoService {
    @Autowired
    SessionFactory sessionFactory;
    @Override
    public UserDeviceEntity find(Integer id) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<UserDeviceEntity> findAll() throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean save(UserDeviceEntity value) throws Exception {
        getCurrentSession().saveOrUpdate(value);
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean update(UserDeviceEntity value) throws Exception {
        getCurrentSession().update(value);
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean delete(UserDeviceEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Session getCurrentSession() throws Exception {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public UserDeviceEntity find(BigInteger customerId, String uuid) throws Exception {
//        List<UserDeviceEntity> customerDeviceEntities = new ArrayList<>();
//        Criteria criteria = getCurrentSession().createCriteria(UserDeviceEntity.class);
////        criteria.add(Restrictions.eq("customerId", customerId));
////        criteria.add(Restrictions.eq("uuid", uuid));
//        customerDeviceEntities = criteria.list();
//
//        return customerDeviceEntities.size()> 0 ? customerDeviceEntities.get(0) :null;
        return null;
    }
}