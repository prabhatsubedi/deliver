package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.ValidateMobileDaoService;
import com.yetistep.delivr.hbn.AliasToBeanNestedResultTransformer;
import com.yetistep.delivr.model.ValidateMobileEntity;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 3/16/15
 * Time: 4:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class ValidateMobileDaoServiceImpl implements ValidateMobileDaoService{
    @Autowired
    SessionFactory sessionFactory;
    @Override
    public ValidateMobileEntity find(Integer id) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ValidateMobileEntity> findAll() throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean save(ValidateMobileEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean update(ValidateMobileEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean delete(ValidateMobileEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Session getCurrentSession() throws Exception {
        Session session = sessionFactory.getCurrentSession();
        return session;
    }


    @Override
    public ValidateMobileEntity getMobileCode(Integer userId, String mobileNo) throws Exception {
        String sql = "SELECT id, verification_code as verificationCode, verified_by_user as verifiedByUser FROM validate_mobile " +
                "WHERE user_id = :userId AND mobile_no = :mobileNo";

        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setParameter("userId", userId);
        sqlQuery.setParameter("mobileNo", mobileNo);
        sqlQuery.setResultTransformer(new AliasToBeanNestedResultTransformer(ValidateMobileEntity.class));
        ValidateMobileEntity code = sqlQuery.list().size() > 0 ? (ValidateMobileEntity) sqlQuery.list().get(0) : null;
        return code;
    }

    @Override
    public Boolean updateVerifiedByUser(Integer id) throws Exception {
        String sqlAtt = "UPDATE validate_mobile SET verified_by_user = :status WHERE id = :mobId";
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sqlAtt);
        sqlQuery.setParameter("status", true);
        sqlQuery.setParameter("mobId", id);

        sqlQuery.executeUpdate();
        return true;
    }
}
