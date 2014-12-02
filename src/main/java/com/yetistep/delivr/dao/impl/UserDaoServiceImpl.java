package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.UserDaoService;
import com.yetistep.delivr.enums.Role;
import com.yetistep.delivr.model.RoleEntity;
import com.yetistep.delivr.model.UserEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

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

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void save(UserEntity user) throws SQLException {

        Session session = sessionFactory.getCurrentSession();
        session.save(user);

    }

    @Override
    public void saveRole(RoleEntity role) throws SQLException {

        Session session = sessionFactory.getCurrentSession();
        session.save(role);

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

}
