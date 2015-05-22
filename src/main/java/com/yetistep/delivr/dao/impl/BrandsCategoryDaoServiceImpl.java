package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.BrandsCategoryDaoService;
import com.yetistep.delivr.enums.Status;
import com.yetistep.delivr.model.BrandsCategoryEntity;
import com.yetistep.delivr.model.StoresBrandEntity;
import com.yetistep.delivr.model.mobile.dto.ItemDto;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 2/2/15
 * Time: 11:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class BrandsCategoryDaoServiceImpl implements BrandsCategoryDaoService{
    @Autowired
    SessionFactory sessionFactory;

    @Override
    public BrandsCategoryEntity find(Integer id) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<BrandsCategoryEntity> findAll() throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean save(BrandsCategoryEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean update(BrandsCategoryEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean delete(BrandsCategoryEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Session getCurrentSession() throws Exception {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<StoresBrandEntity> getCategoryBrands(Integer categoryId) throws Exception {
        String sql = "SELECT DISTINCT(bc.stores_brand_id) AS id, sb.featured, sb.priority FROM brands_categories bc " +
                "INNER JOIN stores_brands sb ON(bc.stores_brand_id = sb.id AND sb.status=:status) " +
                "WHERE bc.category_id =:categoryId";
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setParameter("status", Status.ACTIVE.ordinal());
        sqlQuery.setParameter("categoryId", categoryId);
        sqlQuery.setResultTransformer(Transformers.aliasToBean(StoresBrandEntity.class));
        return (List<StoresBrandEntity>) sqlQuery.list();
    }
}
