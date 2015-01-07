package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.DBoyOrderHistoryDaoService;
import com.yetistep.delivr.enums.DeliveryStatus;
import com.yetistep.delivr.model.DBoyOrderHistoryEntity;
import com.yetistep.delivr.model.Page;
import com.yetistep.delivr.model.mobile.dto.PastDeliveriesDto;
import com.yetistep.delivr.util.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 12/31/14
 * Time: 5:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class DBoyOrderHistoryDaoServiceImpl implements DBoyOrderHistoryDaoService {
    @Autowired
    SessionFactory sessionFactory;

    @Override
    public DBoyOrderHistoryEntity find(Integer id) throws Exception {
        return (DBoyOrderHistoryEntity) getCurrentSession().get(DBoyOrderHistoryEntity.class, id);
    }

    @Override
    public List<DBoyOrderHistoryEntity> findAll() throws Exception {
        return (List<DBoyOrderHistoryEntity>) getCurrentSession().createCriteria(DBoyOrderHistoryEntity.class).list();
    }

    @Override
    public Boolean save(DBoyOrderHistoryEntity value) throws Exception {
        getCurrentSession().save(value);
        return true;
    }

    @Override
    public Boolean update(DBoyOrderHistoryEntity value) throws Exception {
        getCurrentSession().update(value);
        return true;
    }

    @Override
    public Boolean delete(DBoyOrderHistoryEntity value) throws Exception {
        getCurrentSession().delete(value);
        return true;
    }

    @Override
    public Session getCurrentSession() throws Exception {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<PastDeliveriesDto> getPastOrders(Page page, Integer deliveryBoyId) throws Exception {
        //TODO Page Implementation
        List<DeliveryStatus> deliveryStatus = new ArrayList<DeliveryStatus>();
        deliveryStatus.add(DeliveryStatus.ASSIGNED_TO_OTHER);
        deliveryStatus.add(DeliveryStatus.CANCELLED);
        deliveryStatus.add(DeliveryStatus.SUCCESSFUL);

        Criteria criteria = getCurrentSession().createCriteria(DBoyOrderHistoryEntity.class);
        criteria.createAlias("order", "o");
        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("o.id"), "orderId")
                .add(Projections.property("o.orderStatus"), "orderStatus")
                .add(Projections.property("o.orderName"), "orderName")
                .add(Projections.property("distanceTravelled"), "distanceTravelled")
                .add(Projections.property("orderCompletedAt"), "completedAt")
                .add(Projections.property("jobStartedAt"), "jobStartedAt")
                .add(Projections.property("amountEarned"), "amountEarned")
        ).setResultTransformer(Transformers.aliasToBean(PastDeliveriesDto.class));
        criteria.add(Restrictions.eq("deliveryBoy.id", deliveryBoyId))
                .add(Restrictions.in("deliveryStatus", deliveryStatus))
                .addOrder(Order.desc("orderCompletedAt"));
        HibernateUtil.fillPaginationCriteria(criteria, page, DBoyOrderHistoryEntity.class);
        List<PastDeliveriesDto> pastDeliveries = criteria.list();
        return pastDeliveries;
    }

    @Override
    public Integer getTotalNumberOfPastDeliveries(Integer deliveryBoyId) throws Exception {
        List<DeliveryStatus> deliveryStatus = new ArrayList<DeliveryStatus>();
        deliveryStatus.add(DeliveryStatus.ASSIGNED_TO_OTHER);
        deliveryStatus.add(DeliveryStatus.CANCELLED);
        deliveryStatus.add(DeliveryStatus.SUCCESSFUL);
        Criteria criteriaCount = getCurrentSession().createCriteria(DBoyOrderHistoryEntity.class);
        criteriaCount.setProjection(Projections.rowCount())
                .add(Restrictions.eq("deliveryBoy.id", deliveryBoyId))
                .add(Restrictions.in("deliveryStatus", deliveryStatus));
        Long count = (Long) criteriaCount.uniqueResult();
        return (count != null) ? count.intValue() : null;
    }
}
