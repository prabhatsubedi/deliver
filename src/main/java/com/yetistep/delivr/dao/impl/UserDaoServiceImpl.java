package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.UserDaoService;
import com.yetistep.delivr.enums.Role;
import com.yetistep.delivr.enums.Status;
import com.yetistep.delivr.hbn.AliasToBeanNestedResultTransformer;
import com.yetistep.delivr.model.Page;
import com.yetistep.delivr.model.RoleEntity;
import com.yetistep.delivr.model.UserEntity;
import com.yetistep.delivr.util.HibernateUtil;
import org.hibernate.*;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/6/14
 * Time: 2:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserDaoServiceImpl implements UserDaoService {
    @Autowired
    SessionFactory sessionFactory;

    @Override
    public UserEntity findByUserName(String userName) {
        List<UserEntity> usersList = new ArrayList<>();
        try {
            Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserEntity.class);
            criteria.add(Restrictions.eq("username", userName));
            usersList = criteria.list();
        } catch (Exception e) {
            throw e;
        }

        return usersList.size() > 0 ? usersList.get(0) : null;
    }

    @Override
    public UserEntity findByVerificationCode(String code) throws Exception {
        List<UserEntity> usersList = new ArrayList<>();
        Criteria criteria = getCurrentSession().createCriteria(UserEntity.class);
        criteria.add(Restrictions.eq("verificationCode", code));
        usersList = criteria.list();
        return usersList.size() > 0 ? usersList.get(0) : null;
    }


    @Override
    public UserEntity find(Integer id) throws Exception {
        return (UserEntity) getCurrentSession().get(UserEntity.class, id);
    }

    @Override
    public List<UserEntity> findAll() throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<UserEntity> findManagers(Page page) throws Exception {
        List<UserEntity> usersList = new ArrayList<>();
        try {
            Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserEntity.class);
            criteria.add(Restrictions.eq("role.id", Role.ROLE_MANAGER.ordinal()));
            HibernateUtil.fillPaginationCriteria(criteria, page, UserEntity.class);
            usersList = criteria.list();
        } catch (Exception e) {
            throw e;
        }

        return usersList;
    }

    @Override
    public List<UserEntity> findAccountants(Page page) throws Exception {
        List<UserEntity> usersList = new ArrayList<>();
        try {
            Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserEntity.class);
            criteria.add(Restrictions.eq("role.id", Role.ROLE_ACCOUNTANT.ordinal()));
            HibernateUtil.fillPaginationCriteria(criteria, page, UserEntity.class);
            usersList = criteria.list();
        } catch (Exception e) {
            throw e;
        }

        return usersList;
    }


    @Override
    public Integer getTotalNumberManagers()throws Exception{
        String sqQuery =    "SELECT COUNT(u.id) FROM users u WHERE  u.role_id =:roleId";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqQuery);
        query.setParameter("roleId", Role.ROLE_MANAGER.ordinal());
        BigInteger cnt = (BigInteger) query.uniqueResult();
        return cnt.intValue();
    }

    @Override
    public Integer getTotalNumberAccountants()throws Exception{
        String sqQuery =    "SELECT COUNT(u.id) FROM users u WHERE  u.role_id =:roleId";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqQuery);
        query.setParameter("roleId", Role.ROLE_ACCOUNTANT.ordinal());
        BigInteger cnt = (BigInteger) query.uniqueResult();
        return cnt.intValue();
    }

    @Override
    public Boolean save(UserEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }


    @Override
    public Boolean update(UserEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean delete(UserEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Session getCurrentSession() throws Exception {
        Session session = getSessionFactory().getCurrentSession();
        return session;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public RoleEntity getRoleByRole(Role role) throws SQLException {
        Session session = sessionFactory.getCurrentSession();
        List<RoleEntity> roleList = new ArrayList<>();

        Criteria criteria = session.createCriteria(RoleEntity.class);
        criteria.add(Restrictions.eq("role", role));
        roleList = criteria.list();

        return roleList.size() > 0 ? roleList.get(0) : null;
    }

    @Override
    public List<RoleEntity> findAllRoles() throws SQLException {
        Session session = sessionFactory.getCurrentSession();
        List<RoleEntity> roleList = new ArrayList<>();
        Criteria criteria = session.createCriteria(RoleEntity.class);
        roleList = criteria.list();

        return roleList;
    }

    @Override
    public List<UserEntity> getUsers(){
        Session session = sessionFactory.getCurrentSession();
        List<UserEntity> userlist = new ArrayList<>();
        Criteria criteria = session.createCriteria(UserEntity.class);
        userlist = criteria.list();

        return userlist;
    }

    @Override
    public UserEntity find(String userName, String password) throws Exception {
        Criteria criteria = getCurrentSession().createCriteria(UserEntity.class);
        criteria.add(Restrictions.eq("username", userName));
        criteria.add(Restrictions.eq("password", password));

        List<UserEntity> userEntityList = criteria.list();

        return userEntityList.size() > 0 ? userEntityList.get(0) : null;
    }

    @Override
    public Boolean updateDeliveryContact(Integer userId, String mobileNo, String verificationCode) throws Exception {
        String sql = "UPDATE users SET last_address_mobile = :mobileNo, verification_code =:verificationCode WHERE id =:userId";
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setParameter("mobileNo", mobileNo);
        sqlQuery.setParameter("verificationCode", verificationCode);
        sqlQuery.setParameter("userId", userId);

        sqlQuery.executeUpdate();
        return true;

    }


    @Override
    public Boolean checkIfMobileNumberExists(String mobileNumber) throws Exception {
        String sql = "SELECT count(id) FROM users WHERE mobile_number = :mobileNumber";
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setParameter("mobileNumber", mobileNumber);
        BigInteger count = (BigInteger) sqlQuery.uniqueResult();
        return (count.intValue() > 0) ? true : false;
    }

    @Override
    public Boolean checkIfEmailExists(String emailAddress, Integer roleId) throws Exception {
        String sql = "SELECT count(id) FROM users WHERE email = :email AND role_id = :roleId";
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setParameter("email", emailAddress);
        sqlQuery.setParameter("roleId", roleId);

        BigInteger count = (BigInteger) sqlQuery.uniqueResult();
        return (count.intValue() > 0) ? true : false;
    }

    @Override
    public Boolean deactivateUser(Integer userId) throws Exception {
        String sql = "UPDATE users set status=:status, " +
                "inactivated_count = (CASE WHEN inactivated_count IS NULL THEN 1 ELSE inactivated_count + 1 END) " +
                "WHERE id = :userId";
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setParameter("status", Status.INACTIVE.ordinal());
        sqlQuery.setParameter("userId", userId);

        sqlQuery.executeUpdate();
        return true;
    }

    @Override
    public Boolean activateUser(Integer userId) throws Exception {
        String sql = "UPDATE users set status=:status, " +
                "activated_count = (CASE WHEN activated_count IS NULL THEN 1 ELSE activated_count + 1 END) " +
                "WHERE id = :userId";
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setParameter("status", Status.ACTIVE.ordinal());
        sqlQuery.setParameter("userId", userId);

        sqlQuery.executeUpdate();
        return true;
    }

    @Override
    public List<UserEntity> getInactivatedCustomers(Page page) throws Exception {
        ProjectionList projectionList = Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("emailAddress"), "emailAddress")
                .add(Projections.property("mobileNumber"), "mobileNumber")
                .add(Projections.property("fullName"), "fullName")
                .add(Projections.property("inactivatedCount"), "inactivatedCount")
                .add(Projections.property("activatedCount"), "activatedCount")
                .add(Projections.property("c.averageRating"), "customer.averageRating")
                .add(Projections.property("c.totalOrderDelivered"), "customer.totalOrderDelivered")
                .add(Projections.property("c.totalOrderPlaced"), "customer.totalOrderPlaced")
                .add(Projections.property("c.profileUrl"), "customer.profileUrl");

        Criteria criteria = getCurrentSession().createCriteria(UserEntity.class)
                .createAlias("customer", "c")
                .setProjection(projectionList)
                .setResultTransformer(new AliasToBeanNestedResultTransformer(UserEntity.class));
        criteria.add(Restrictions.eq("status", Status.INACTIVE));
        criteria.add(Restrictions.eq("role.id", Role.ROLE_CUSTOMER.toInt()));

        HibernateUtil.fillPaginationCriteria(criteria, page, UserEntity.class);
        List<UserEntity> users = criteria.list();
        return users;
    }


    @Override
    public Integer getTotalNumberInactiveCustomers() throws Exception{
        String sqQuery =    "SELECT COUNT(u.id) FROM users u WHERE u.role_id =:roleId AND u.status =:status";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqQuery);
        query.setParameter("roleId", Role.ROLE_CUSTOMER.toInt());
        query.setParameter("status", Status.INACTIVE.ordinal());

        BigInteger cnt = (BigInteger) query.uniqueResult();
        return cnt.intValue();
    }
}
