package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.DeliveryBoyDaoService;
import com.yetistep.delivr.enums.DBoyStatus;
import com.yetistep.delivr.enums.DeliveryStatus;
import com.yetistep.delivr.enums.JobOrderStatus;
import com.yetistep.delivr.model.DeliveryBoyEntity;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
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
                .add(Restrictions.isNotNull("latitude"))
                .add(Restrictions.isNotNull("longitude"))
                .add(Restrictions.and(Restrictions.eq("u.verifiedStatus", true), Restrictions.eq("u.mobileVerificationStatus", true))).list();
        return deliveryBoys;
    }

    @Override
    public Session getCurrentSession() throws Exception {
        Session session = sessionFactory.getCurrentSession();
        return session;
    }

    @Override
    public DeliveryBoyEntity getProfileInformation(Integer deliveryBoyId) throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(DeliveryBoyEntity.class);
        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("totalEarnings"), "totalEarnings")
                .add(Projections.property("activeOrderNo"), "activeOrderNo")
                .add(Projections.property("totalOrderTaken"), "totalOrderTaken")
                .add(Projections.property("previousDue"), "previousDue")
                .add(Projections.property("availableAmount"), "availableAmount")
                .add(Projections.property("walletAmount"), "walletAmount")
        ).setResultTransformer(Transformers.aliasToBean(DeliveryBoyEntity.class));
        criteria.add(Restrictions.eq("id", deliveryBoyId));
        return (DeliveryBoyEntity) criteria.uniqueResult();
    }

    @Override
    public Boolean checkForPendingOrders(Integer deliveryBoyId, Integer orderId) throws Exception {
        String sqlQuery = "SELECT count(db.id) FROM dboy_order_history db INNER JOIN orders o " +
                "on (o.id = db.order_id) WHERE db.delivery_status = :deliveryStatus AND " +
                "db.dboy_id = :deliveryBoyId AND o.order_status in " +
                "(:inRoutePickUp, :atStore, :inRouteToDelivery) AND o.id != :orderId";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
        query.setParameter("deliveryStatus", DeliveryStatus.PENDING.ordinal());
        query.setParameter("inRoutePickUp", JobOrderStatus.IN_ROUTE_TO_PICK_UP.ordinal());
        query.setParameter("atStore", JobOrderStatus.AT_STORE.ordinal());
        query.setParameter("inRouteToDelivery", JobOrderStatus.IN_ROUTE_TO_DELIVERY.ordinal());
        query.setParameter("deliveryBoyId", deliveryBoyId);
        query.setParameter("orderId", orderId);
        BigInteger count = (BigInteger) query.uniqueResult();
        if(count.intValue() == 0){
            return true;
        }
        return false;
    }
}
