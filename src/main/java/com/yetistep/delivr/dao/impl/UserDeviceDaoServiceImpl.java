package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.UserDeviceDaoService;
import com.yetistep.delivr.enums.Role;
import com.yetistep.delivr.model.UserDeviceEntity;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
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
        getCurrentSession().persist(value);
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean update(UserDeviceEntity value) throws Exception {
        getCurrentSession().persist(value);
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

    @Override
    public Boolean updateUserDeviceToken(Long facebookId, String deviceToken) throws Exception {
        String sql = "UPDATE user_device ud, customers c SET device_token = :deviceToken " +
                "WHERE c.user_id = ud.user_id AND c.facebook_id =:facebookId";
        SQLQuery query = getCurrentSession().createSQLQuery(sql);
        query.setParameter("deviceToken", deviceToken);
        query.setParameter("facebookId", facebookId);
        query.executeUpdate();
        return true;
    }

    @Override
    public Boolean updateUserDeviceTokenFromUserID(Integer userId, String deviceToken) throws Exception {
        String sql = "UPDATE user_device SET device_token = :deviceToken WHERE user_id = :userId";
        SQLQuery query = getCurrentSession().createSQLQuery(sql);
        query.setParameter("deviceToken", deviceToken);
        query.setParameter("userId", userId);
        query.executeUpdate();
        return true;
    }

    @Override
    public List<String> getDeviceTokenFromDeliveryBoyId(List<Integer> deliveryBoysId) throws Exception {
        String sql = "SELECT ud.device_token FROM user_device ud INNER JOIN users u ON (u.id = ud.user_id) INNER JOIN delivery_boys db ON (db.user_id = u.id) WHERE db.id IN (:deliveryBoyId)";
        SQLQuery query = getCurrentSession().createSQLQuery(sql);
        query.setParameterList("deliveryBoyId", deliveryBoysId);
        List<String> deviceTokens = query.list();
        return deviceTokens;
    }

    @Override
    public UserDeviceEntity getUserDeviceInfoFromOrderId(Integer orderId) throws Exception {
        String sql = "SELECT ud.device_token as deviceToken, ud.family as family FROM orders o " +
                "INNER JOIN customers c on (o.customer_id = c.id) INNER JOIN users u on(u.id = c.user_id) " +
                "INNER JOIN user_device ud ON (ud.user_id = u.id) WHERE o.id = :orderId";
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setParameter("orderId", orderId);
        sqlQuery.setResultTransformer(Transformers.aliasToBean(UserDeviceEntity.class));

        UserDeviceEntity userDevice = (UserDeviceEntity) sqlQuery.uniqueResult();
        return userDevice;
    }

    @Override
    public Boolean removeInformationForSameDevice(String uuid, Integer userId) throws Exception {
        String sql = "DELETE FROM user_device WHERE id in " +
                "(SELECT id FROM (SELECT DISTINCT ud.id FROM user_device ud INNER JOIN users u on " +
                "(u.id=ud.user_id) INNER JOIN roles r on (u.role_id = r.id) WHERE ud.uuid = :uuid AND " +
                "r.id = :roleId AND user_id != :userId) AS temp)";
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setParameter("uuid", uuid);
        sqlQuery.setParameter("roleId", Role.ROLE_DELIVERY_BOY.toInt());
        sqlQuery.setParameter("userId", userId);
        sqlQuery.executeUpdate();
        return true;
    }

    @Override
    public List<String> getDeviceTokensExceptAcceptedDeliveryBoy(Integer orderId, Integer exceptDeliveryBoyId) throws Exception {
        String sql = "SELECT ud.device_token FROM dboy_selections dbs INNER JOIN delivery_boys db " +
                "on (dbs.dboy_id = db.id) INNER JOIN user_device ud on (db.user_id = ud.user_id) " +
                "WHERE dbs.accepted = :accepted AND dbs.rejected = :rejected AND dbs.order_id = :orderId " +
                "AND dbs.dboy_id != :deliveryBoyId";
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setParameter("accepted", false);
        sqlQuery.setParameter("rejected", false);
        sqlQuery.setParameter("orderId", orderId);
        sqlQuery.setParameter("deliveryBoyId", exceptDeliveryBoyId);
        List<String> deviceTokens = sqlQuery.list();
        return deviceTokens;
    }

    @Override
    public List<String> getDeviceTokensOfAssignedDeliveryBoy(Integer orderId) throws Exception {
        String sql = "SELECT ud.device_token FROM dboy_selections dbs INNER JOIN delivery_boys db " +
                "on (dbs.dboy_id = db.id) INNER JOIN user_device ud on (db.user_id = ud.user_id) " +
                "WHERE dbs.accepted = :accepted AND dbs.rejected = :rejected AND dbs.order_id = :orderId";
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setParameter("accepted", false);
        sqlQuery.setParameter("rejected", false);
        sqlQuery.setParameter("orderId", orderId);
        List<String> deviceTokens = sqlQuery.list();
        return deviceTokens;
    }

    @Override
    public List<String> getAllDeviceTokensForFamilyAndRole(Role role, String family) throws Exception {
        String sql = "SELECT ud.device_token FROM user_device ud INNER JOIN users u " +
                "ON (u.id = ud.user_id) AND u.role_id = :roleId AND ud.device_token IS NOT NULL " +
                "AND family LIKE :family";
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setParameter("roleId", role.toInt());
        sqlQuery.setParameter("family", "%"+family+"%");
        List<String> deviceTokens = sqlQuery.list();
        return deviceTokens;
    }
}
