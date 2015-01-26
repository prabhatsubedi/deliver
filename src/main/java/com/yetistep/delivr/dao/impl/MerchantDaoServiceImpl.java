package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.MerchantDaoService;
import com.yetistep.delivr.enums.Status;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.util.HibernateUtil;
import org.hibernate.*;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.BigInteger;
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
        List<MerchantEntity> merchants  = new ArrayList<MerchantEntity>();
        Criteria criteria  = getCurrentSession().createCriteria(MerchantEntity.class, "merchant");
        merchants = criteria.list();
        return merchants;
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
    public void deleteBrandsCategory(BrandsCategoryEntity value) throws Exception {
        getCurrentSession().delete(value);
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
    public void updateStores(List<StoreEntity> values) throws Exception {
        Integer i = 0;
        for (StoreEntity value: values) {
            getCurrentSession().update(value);
            if ( i % 20 == 0 ) { //20, same as the JDBC batch size
                //flush a batch of inserts and release memory:
                getCurrentSession().flush();
                getCurrentSession().clear();
            }
            i++;
        }
    }




    @Override
    public Boolean updateStoresBrand(StoresBrandEntity value) throws Exception {
        getCurrentSession().merge(value);
        return true;
    }

    @Override
    public Boolean saveStoresBrand(StoresBrandEntity value) throws Exception {
        getCurrentSession().save(value);
        return true;
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
    public  List<CategoryEntity> getCategories() throws Exception {
        List<CategoryEntity> categories = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CategoryEntity.class);
        criteria.add(Restrictions.isNotNull("parent.id"));
        categories = criteria.list();

        return categories;
    }

    @Override
    public  List<CategoryEntity> getDefaultCategories() throws Exception {
        List<CategoryEntity> categories = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CategoryEntity.class);
        criteria.add(Restrictions.and(Restrictions.isNotNull("parent.id"), Restrictions.isNull("storesBrand.id")));
        categories = criteria.list();

        return categories;
    }

    @Override
    public MerchantEntity getCommissionAndVat(Integer merchantId) throws Exception {
        String sql = "SELECT commission_percentage AS commissionPercentage, service_fee AS serviceFee FROM merchants WHERE id = :merchantId";
        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setParameter("merchantId", merchantId);
//        sqlQuery.addScalar("commissionPercentage");
//        sqlQuery.addScalar("serviceFee");
        MerchantEntity merchantEntity = null;
        List<Object[]> rows = sqlQuery.list();
        for(Object[] row : rows){
            merchantEntity = new MerchantEntity();
            merchantEntity.setCommissionPercentage(new BigDecimal(row[0].toString()));
            merchantEntity.setServiceFee(new BigDecimal(row[1].toString()));
        }
        return merchantEntity;
    }

    @Override
    public StoreEntity getStoreById(Integer id) throws Exception {
        return (StoreEntity) getCurrentSession().get(StoreEntity.class, id);
    }

    @Override
    public void deleteStore(StoreEntity value) throws Exception{
        getCurrentSession().delete(value);
    }

    @Override
    public BrandsCategoryEntity getBrandsCategory(Integer brandId, Integer categoryId) throws Exception {
        List<BrandsCategoryEntity> brandsCategories = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BrandsCategoryEntity.class);
        criteria.add(Restrictions.and(Restrictions.eq("storesBrand.id", brandId), Restrictions.eq("category.id", categoryId)));
        brandsCategories = criteria.list();

        return brandsCategories.size() > 0 ? brandsCategories.get(0) : null;

    }

    @Override
    public List<BrandsCategoryEntity> getBrandsCategory(Integer brandId) throws Exception {
        List<BrandsCategoryEntity> brandsCategories = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BrandsCategoryEntity.class);
        criteria.add(Restrictions.eq("storesBrand.id", brandId));
        /*String hqlQuery = "SELECT bc.category FROM BrandsCategoryEntity bc LEFT JOIN bc.category c WHERE (bc.storesBrand.id = :brandId OR bc.storesBrand.id IS NULL) AND (c.storesBrand.id = :brandId OR c.storesBrand.id IS NULL)";
        Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery);
        query.setParameter("brandId", brandId);*/
        brandsCategories = criteria.list();

        return brandsCategories.size() > 0 ? brandsCategories : null;
    }

    @Override
    public List<CategoryEntity> findFinalCategoryList(Integer brandId) throws Exception {
        //"SELECT id FROM categories AS Cat WHERE Cat.id NOT IN (SELECT Cat.parent_id FROM categories Cat WHERE Cat.parent_id IS NOT NULL) && (brand_id = 1 OR brand_id IS NULL)";
        String hqlQuery = "FROM CategoryEntity cat WHERE cat.id NOT IN (SELECT cat.parent.id FROM CategoryEntity cat WHERE cat.parent IS NOT NULL) AND (cat.storesBrand.id = :brandId OR cat.storesBrand IS NULL)";

        Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery);
        query.setParameter("brandId", brandId);
        List<CategoryEntity> categories = query.list();
        return categories.size() > 0 ? categories : null;
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
        criteria.add(Restrictions.and(Restrictions.eq("storesBrand.id", brandId)));
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

        return stores;
    }

    @Override
    public List<StoreEntity> findActiveStoresByBrand(Integer brandId) throws Exception {
        List<StoreEntity> stores = new ArrayList<StoreEntity>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StoreEntity.class);
        criteria.add(Restrictions.and(Restrictions.eq("storesBrand.id", brandId)));
        criteria.add(Restrictions.and(Restrictions.eq("status", Status.ACTIVE)));
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

        return stores;
    }

    @Override
    public List<StoresBrandEntity> findBrandListByMerchant(Integer merchantId) throws Exception {
        List<StoresBrandEntity> stores = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StoresBrandEntity.class, "brand");
        //criteria.createCriteria("store").add(Restrictions.eq("status", Status.ACTIVE));
       /* criteria.setProjection(Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("brandName"), "brandName")
                .add(Projections.property("brandLogo"), "brandLogo")
                .add(Projections.property("brandImage"), "brandImage")
                .add(Projections.property("featured"), "featured")
                .add(Projections.property("priority"), "priority")
                .add(Projections.property("openingTime"), "openingTime")
                .add(Projections.property("closingTime"), "closingTime")
                .add(Projections.property("merchant"), "merchant")
        ).setResultTransformer(Transformers.aliasToBean(StoresBrandEntity.class));*/
        //criteria.createAlias("brand.store", "stores", Criteria.LEFT_JOIN);
        criteria.add(Restrictions.and(Restrictions.eq("merchant.id", merchantId)/*, Restrictions.eq("stores.status", Status.ACTIVE)*/)) ;
        stores = criteria.list();
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
                .add(Projections.property("openingTime"), "openingTime")
                .add(Projections.property("closingTime"), "closingTime")
                .add(Projections.property("merchant"), "merchant")
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
        //criteria.createAlias("child", "child");
       /* criteria.setProjection(Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("name"), "name")
        ).setResultTransformer(Transformers.aliasToBean(CategoryEntity.class));*/

        Criterion rest1 = Restrictions.and(Restrictions.isNull("storesBrand"), Restrictions.eq("parent.id", parentId));
        Criterion rest2 = Restrictions.and(Restrictions.eq("storesBrand.id", storeId), Restrictions.eq("parent.id", parentId)/*, Restrictions.eq("child.storesBrand.id", storeId)*/);

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
    public List<ItemEntity> getCategoriesItems(Integer categoryId, Integer brandId) throws Exception {
        List<ItemEntity> items = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ItemEntity.class);
        criteria.add(Restrictions.and(Restrictions.eq("category.id", categoryId), Restrictions.eq("storesBrand.id", brandId)));
        items = criteria.list();
        return items;
    }


    @Override
    public List<ItemEntity> getCategoriesItems(Integer categoryId) throws Exception {
        List<ItemEntity> items = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ItemEntity.class);
        criteria.add(Restrictions.and(Restrictions.eq("category.id", categoryId)));
        items = criteria.list();
        return items;
    }

    @Override
    public List<ItemEntity> findItemByCategory(List<Integer> categoryId, Integer brandId) throws Exception {
        List<ItemEntity> items = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ItemEntity.class);
        criteria.add(Restrictions.and(Restrictions.in("category.id", categoryId), Restrictions.eq("storesBrand.id", brandId)));
        criteria.add(Restrictions.sqlRestriction("1=1 order by rand()"));
        criteria.setMaxResults(2);
        items = criteria.list();
        return items;
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

        return itemsStores;
    }

    @Override
    public Boolean findPartnerShipStatusFromOrderId(Integer orderId) throws Exception {
        String sqlQuery = "SELECT m.partnership_status FROM merchants m " +
                "INNER JOIN stores_brands sb on sb.merchant_id = m.id " +
                "INNER JOIN stores s on s.stores_brand_id = sb.id " +
                "INNER JOIN orders o on o.store_id = s.id where o.id =:orderId";

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
        query.setParameter("orderId", orderId);
        Boolean partnerShipStatus = (Boolean) query.uniqueResult();
        return partnerShipStatus;
    }

    @Override
    public Boolean updateItem(ItemEntity value) throws Exception {
        getCurrentSession().update(value);
        return true;
    }

    @Override
    public Boolean updateItemImages(List<ItemsImageEntity> ItemsImages) throws Exception {
        Integer i = 0;
        for (ItemsImageEntity value: ItemsImages) {
            if(value.getId() == null){
                getCurrentSession().save(value);
            }else{
                getCurrentSession().update(value);
            }
            if ( i % 20 == 0 ) { //20, same as the JDBC batch size
                //flush a batch of inserts and release memory:
                getCurrentSession().flush();
                getCurrentSession().clear();
            }
            i++;
        }
        return true;
    }

    @Override
    public MerchantEntity getMerchantByOrderId(Integer orderId) throws Exception {
        String sqlQuery = "SELECT m.id as id, m.partnership_status as partnershipStatus, " +
                "m.service_fee as serviceFee, m.commission_percentage as commissionPercentage " +
                "FROM merchants m " +
                "INNER JOIN stores_brands sb on sb.merchant_id = m.id " +
                "INNER JOIN stores s on s.stores_brand_id = sb.id " +
                "INNER JOIN orders o on o.store_id = s.id where o.id =:orderId";

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery)
                .setResultTransformer( Transformers.aliasToBean(MerchantEntity.class));
        query.setParameter("orderId", orderId);
        MerchantEntity merchantEntity = (MerchantEntity) query.uniqueResult();
        return merchantEntity;
    }

    @Override
    public List<ItemEntity> getWebSearchItem(String searchString, List<Integer> categoryId, Integer storeId, Page page) throws Exception{
        List<ItemEntity> items = new ArrayList<>();

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ItemEntity.class);
        criteria.add(Restrictions.and(Restrictions.like("name", searchString, MatchMode.ANYWHERE), Restrictions.in("category.id", categoryId),  Restrictions.eq("storesBrand.id", storeId)));
        HibernateUtil.fillPaginationCriteria(criteria, page, ItemEntity.class);
        items = criteria.list();

        return items;
    }


    @Override
    public Integer getTotalNumberOfItems(String searchString, List<Integer> categoryId, Integer storeId) throws Exception {
        /*Criteria criteriaCount = getCurrentSession().createCriteria(ItemEntity.class);
        criteriaCount.setProjection(Projections.rowCount()).add(Restrictions.and(Restrictions.like("name", searchString), Restrictions.in("category.id", categoryId), Restrictions.eq("storesBrand.id", storeId)));
        Long count = (Long) criteriaCount.uniqueResult();
        return (count != null) ? count.intValue() : null;*/
        String sqQuery =    "SELECT COUNT(i.id) FROM Items i WHERE i.name LIKE '%"+searchString+"%' AND i.category_id IN "+categoryId.toString().replace("[", "(").replace("]", ")")+" AND i.brand_id =:storeId";

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqQuery);
        query.setParameter("storeId", storeId);

        BigInteger cnt = (BigInteger) query.uniqueResult();
        return cnt.intValue();
    }


}
