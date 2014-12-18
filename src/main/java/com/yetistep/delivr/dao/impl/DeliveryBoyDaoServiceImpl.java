package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.DeliveryBoyDaoService;
import com.yetistep.delivr.enums.DBoyStatus;
import com.yetistep.delivr.model.DeliveryBoyEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 12:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeliveryBoyDaoServiceImpl implements DeliveryBoyDaoService {

    @Autowired
    SessionFactory sessionFactory;


    @Override
    public DeliveryBoyEntity find(Integer id) throws Exception {
        return (DeliveryBoyEntity) getCurrentSession().get(DeliveryBoyEntity.class, id);
    }

    @Override
    public List<DeliveryBoyEntity> findAll() throws Exception {
        return (List<DeliveryBoyEntity>) getCurrentSession().createCriteria(DeliveryBoyEntity.class).list();
    }

    @Override
    public Boolean save(DeliveryBoyEntity value) throws Exception {
        getCurrentSession().save(value);
        return true;
    }

    @Override
    public Boolean update(DeliveryBoyEntity value) throws Exception {
        getCurrentSession().update(value);
        return true;
    }

    @Override
    public Boolean delete(DeliveryBoyEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<DeliveryBoyEntity> findAllCapableDeliveryBoys() throws Exception {
        List<DeliveryBoyEntity> deliveryBoys = getCurrentSession().createCriteria(DeliveryBoyEntity.class)
                .createAlias("user", "u")
                .add(Restrictions.or(Restrictions.eq("availabilityStatus", DBoyStatus.FREE), Restrictions.eq("availabilityStatus", DBoyStatus.BUSY)))
                .add(Restrictions.lt("activeOrderNo", 3))
                .add(Restrictions.and(Restrictions.eq("u.verifiedStatus", true), Restrictions.eq("u.mobileVerificationStatus", true))).list();
        return deliveryBoys;
    }

    @Override
    public Session getCurrentSession() throws Exception {
        Session session = sessionFactory.getCurrentSession();
        return session;
    }

}
