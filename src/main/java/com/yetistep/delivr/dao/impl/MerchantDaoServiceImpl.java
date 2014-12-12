package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.MerchantDaoService;
import com.yetistep.delivr.model.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 12:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchantDaoServiceImpl implements MerchantDaoService {

    @Autowired
    SessionFactory sessionFactory;


    @Override
    public MerchantEntity find(Integer id) throws Exception {
        return (MerchantEntity) getCurrentSession().get(MerchantEntity.class, id);
    }

    @Override
    public List<MerchantEntity> findAll() throws Exception {
        return (List<MerchantEntity>) getCurrentSession().createCriteria(MerchantEntity.class).list();
    }

    @Override
    public Boolean save(MerchantEntity value) throws Exception {
        getCurrentSession().save(value);
        return true;
    }

    @Override
    public Boolean update(MerchantEntity value) throws Exception {
        getCurrentSession().update(value);
        return true;
    }

    @Override
    public Boolean delete(MerchantEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Session getCurrentSession() throws Exception {
        Session session = sessionFactory.getCurrentSession();
        return session;
    }

    @Override
    public void saveStore(List<StoreEntity> values) throws Exception {

        for (StoreEntity value: values)
            getCurrentSession().save(value);

    }

    @Override
    public void updateStoresBrand(StoresBrandEntity value) throws Exception {

        getCurrentSession().update(value);

    }



    @Override
    public StoresBrandEntity getBrandByBrandName(String brandName) throws Exception {

        List<StoresBrandEntity> storeBrandList = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StoresBrandEntity.class);
        criteria.add(Restrictions.eq("brandName", brandName));
        storeBrandList = criteria.list();

        return storeBrandList.size() > 0 ? storeBrandList.get(0) : null;
    }

    @Override
    public CategoryEntity getCategoryById(Integer id) throws Exception {
        return (CategoryEntity) getCurrentSession().get(CategoryEntity.class, id);
    }

    @Override
    public BrandsCategoryEntity getBrandsCategory(Integer brandId, Integer categoryId) throws Exception {
        List<BrandsCategoryEntity> brandsCategories = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BrandsCategoryEntity.class);
        criteria.add(Restrictions.eq("storesBrand.id", brandId));
        criteria.add(Restrictions.eq("category.id", categoryId));
        brandsCategories = criteria.list();

        return brandsCategories.size() > 0 ? brandsCategories.get(0) : null;

    }

    @Override
    public List<CategoryEntity> findParentCategories() throws Exception {
        List<CategoryEntity> categories = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CategoryEntity.class);
        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("name"), "name"))
                .setResultTransformer(Transformers.aliasToBean(CategoryEntity.class));
        criteria.add(Restrictions.isNull("parent")) ;
        categories = criteria.list();

        return categories;
    }
}
