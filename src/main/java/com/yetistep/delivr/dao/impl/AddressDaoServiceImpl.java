package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.AddressDaoService;
import com.yetistep.delivr.model.AddressEntity;
import com.yetistep.delivr.model.CategoryEntity;
import com.yetistep.delivr.util.CommonConstants;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
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
        String sql  = "UPDATE address SET d_flag = :flag where id = :id";
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setParameter("flag", CommonConstants.DELETE_FLAG);
        sqlQuery.setParameter("id", value.getId());
        sqlQuery.executeUpdate();
        return true;
    }

    @Override
    public Session getCurrentSession() throws Exception {
        Session session = sessionFactory.getCurrentSession();
        return session;
    }

    @Override
    public String getMobileCode(Integer userId, String mobileNo) throws Exception {
        String code = null;
        String sql = "SELECT verification_code FROM address " +
                "WHERE user_id = :userId AND mobile_no = :mobileNo AND verified = :verified";

        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setParameter("userId", userId);
        sqlQuery.setParameter("mobileNo", mobileNo);
        sqlQuery.setParameter("verified", true);
        code =  sqlQuery.list().size() > 0 ? (String)sqlQuery.list().get(0) : null;
       return code;
    }

    @Override
    public List<AddressEntity> getDeliveredAddress(Integer userId) throws Exception {
        String sql = "SELECT " +
                "id,city,country,country_code AS countryCode,latitude,longitude,state,street,full_name AS fullName,mobile_no AS mobileNumber,notes" +
                " FROM address WHERE user_id = :userId AND verified = :verified AND (d_flag != :flag || d_flag IS NULL)";

        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setParameter("userId", userId);
        sqlQuery.setParameter("verified", true);
        sqlQuery.setParameter("flag", CommonConstants.DELETE_FLAG);

        sqlQuery.setResultTransformer(Transformers.aliasToBean(AddressEntity.class));
        List<AddressEntity> addressEntities = sqlQuery.list();
        return addressEntities;
    }
}
