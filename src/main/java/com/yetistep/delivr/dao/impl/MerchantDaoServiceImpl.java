package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.MerchantDaoService;
import com.yetistep.delivr.model.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
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
        Integer i = 0;
        for (StoreEntity value: values) {
            getCurrentSession().save(value);
            if ( i % 20 == 0 ) { //20, same as the JDBC batch size
                //flush a batch of inserts and release memory:
                getCurrentSession().flush();
                getCurrentSession().clear();
            }
            i++;
        }
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
    public StoreEntity getStoreById(Integer id) throws Exception {
        return (StoreEntity) getCurrentSession().get(StoreEntity.class, id);
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
    public List<BrandsCategoryEntity> getBrandsCategory(Integer brandId) throws Exception {
        List<BrandsCategoryEntity> brandsCategories = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BrandsCategoryEntity.class);
        criteria.add(Restrictions.eq("storesBrand.id", brandId));
        brandsCategories = criteria.list();

        return brandsCategories.size() > 0 ? brandsCategories : null;

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


    @Override
    public List<StoreEntity> findStoreByBrand(Integer brandId) throws Exception {
        List<StoreEntity> stores = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StoreEntity.class);
        criteria.add(Restrictions.eq("storesBrand.id", brandId));
        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("street"), "street")
                .add(Projections.property("city"), "city")
                .add(Projections.property("state"), "state")
                .add(Projections.property("country"), "country")
                .add(Projections.property("latitude"), "latitude")
                .add(Projections.property("longitude"), "longitude")
                .add(Projections.property("id"), "id")
                .add(Projections.property("name"), "name")
        ).setResultTransformer(Transformers.aliasToBean(StoreEntity.class));
        stores = criteria.list();

        return stores.size() > 0 ? stores : null;
    }

    @Override
    public List<StoresBrandEntity> findBrandListByMerchant(Integer merchantId) throws Exception {
        List<StoresBrandEntity> stores = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StoresBrandEntity.class);
        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("brandName"), "brandName")
                .add(Projections.property("brandLogo"), "brandLogo")
                .add(Projections.property("brandImage"), "brandImage")
                .add(Projections.property("featured"), "featured")
                .add(Projections.property("openingTime"), "openingTime")
                .add(Projections.property("closingTime"), "closingTime")
                .add(Projections.property("merchant"), "merchant")
        ).setResultTransformer(Transformers.aliasToBean(StoresBrandEntity.class));
        criteria.add(Restrictions.eq("merchant.id", merchantId)) ;
        stores = criteria.list();

        //return stores;
        return stores;
    }

    @Override
    public List<StoresBrandEntity> findBrandList() throws Exception {
        List<StoresBrandEntity> stores = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StoresBrandEntity.class);
        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("brandName"), "brandName")
                .add(Projections.property("brandLogo"), "brandLogo")
                .add(Projections.property("brandImage"), "brandImage")
                .add(Projections.property("featured"), "featured")
                .add(Projections.property("priority"), "priority")
        ).setResultTransformer(Transformers.aliasToBean(StoresBrandEntity.class));

        stores = criteria.list();
        return stores.size() > 0 ? stores : null;
    }

    @Override
    public StoresBrandEntity findBrandDetail(Integer brandId) throws Exception {
        return (StoresBrandEntity) getCurrentSession().get(StoresBrandEntity.class, brandId);
    }

    @Override
    public List<CategoryEntity> findChildCategories(Integer parentId, Integer storeId) throws Exception {
        List<CategoryEntity> categories = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CategoryEntity.class);

        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("name"), "name")
        ).setResultTransformer(Transformers.aliasToBean(CategoryEntity.class));

        Criterion rest1 = Restrictions.and(Restrictions.isNull("storesBrand"), Restrictions.eq("parent.id", parentId));
        Criterion rest2 = Restrictions.and(Restrictions.eq("storesBrand.id", storeId), Restrictions.eq("parent.id", parentId));

        criteria.add(Restrictions.or(rest1, rest2));

        categories = criteria.list();
        return categories.size() > 0 ? categories : null;
    }

    @Override
    public void saveCategories(List<CategoryEntity> categories) throws Exception {
        Integer i = 0;
        for (CategoryEntity value: categories) {
            getCurrentSession().save(value);
            if ( i % 20 == 0 ) { //20, same as the JDBC batch size
                //flush a batch of inserts and release memory:
                getCurrentSession().flush();
                getCurrentSession().clear();
            }
            i++;
        }
    }

    @Override
    public void saveItem(ItemEntity value) throws Exception {
        getCurrentSession().save(value);
    }

    @Override
    public void saveItemImages(List<ItemsImageEntity> itemsImageEntities) throws Exception {
        Integer i = 0;
        for (ItemsImageEntity value: itemsImageEntities) {
            getCurrentSession().save(value);
            if ( i % 20 == 0 ) { //20, same as the JDBC batch size
                //flush a batch of inserts and release memory:
                getCurrentSession().flush();
                getCurrentSession().clear();
            }
            i++;
        }
    }

    @Override
    public List<ItemEntity> getCategoriesItems(Integer categoryId) throws Exception {
        List<ItemEntity> items = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ItemEntity.class);
        criteria.add(Restrictions.eq("category.id", categoryId));
        items = criteria.list();
        return items.size() > 0 ? items : null;
    }

    @Override
    public ItemEntity getItemDetail(Integer id) throws Exception {
        return (ItemEntity) getCurrentSession().get(ItemEntity.class, id);
    }


    @Override
    public List<ItemsStoreEntity> findItemsStores(Integer storeId) throws Exception {
        List<ItemsStoreEntity> itemsStores = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ItemsStoreEntity.class);
        criteria.add(Restrictions.eq("store.id", storeId));
        itemsStores = criteria.list();

        return itemsStores.size() > 0 ? itemsStores : null;
    }
}
