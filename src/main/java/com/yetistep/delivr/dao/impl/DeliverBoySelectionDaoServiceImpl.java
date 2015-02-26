package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.DeliveryBoySelectionDaoService;
import com.yetistep.delivr.model.DeliveryBoySelectionEntity;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 12/24/14
 * Time: 11:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class DeliverBoySelectionDaoServiceImpl implements DeliveryBoySelectionDaoService {
    @Autowired
    SessionFactory sessionFactory;

    @Override
    public DeliveryBoySelectionEntity find(Integer id) throws Exception {
        return (DeliveryBoySelectionEntity) getCurrentSession().get(DeliveryBoySelectionEntity.class, id);
    }

    @Override
    public List<DeliveryBoySelectionEntity> findAll() throws Exception {
        return (List<DeliveryBoySelectionEntity>) getCurrentSession().createCriteria(DeliveryBoySelectionEntity.class).list();
    }

    @Override
    public Boolean save(DeliveryBoySelectionEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean update(DeliveryBoySelectionEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean delete(DeliveryBoySelectionEntity value) throws Exception {
        getCurrentSession().delete(value);
        return true;
    }

    @Override
    public Session getCurrentSession() throws Exception {
        Session session = sessionFactory.getCurrentSession();
        return session;
    }

    @Override
    public Boolean checkOrderAcceptedStatus(Integer orderId) throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(DeliveryBoySelectionEntity.class)
                .add(Restrictions.eq("order.id", orderId))
                .add(Restrictions.eq("accepted", true));
        List<DeliveryBoySelectionEntity> deliveryBoySelectionEntities = criteria.list();
        return deliveryBoySelectionEntities.size() == 0 ? true : false;
    }

    @Override
    public DeliveryBoySelectionEntity getSelectionDetails(Integer orderId, Integer deliveryBoyId) throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(DeliveryBoySelectionEntity.class)
                .add(Restrictions.eq("order.id", orderId))
                .add(Restrictions.eq("deliveryBoy.id", deliveryBoyId))
                .add(Restrictions.eq("rejected", false));
        DeliveryBoySelectionEntity deliveryBoySelectionEntity = (DeliveryBoySelectionEntity) criteria.uniqueResult();
        return deliveryBoySelectionEntity;
    }

    @Override
    public Integer getRemainingOrderSelections(Integer orderId) throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(DeliveryBoySelectionEntity.class)
                .add(Restrictions.eq("order.id", orderId))
                .add(Restrictions.eq("accepted", false))
                .add(Restrictions.eq("rejected", false));
        criteria.setProjection(Projections.rowCount());
        Long count = (Long) criteria.uniqueResult();
        return count.intValue();
    }

    @Override
    public Boolean updateAllSelectionToRejectMode(Integer orderId) throws Exception {
        String sqlQuery = "UPDATE dboy_selections SET rejected = true WHERE order_id = :orderId";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
        query.setParameter("orderId", orderId);
        query.executeUpdate();
        return true;
    }
}
