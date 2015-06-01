package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.MerchantDaoService;
import com.yetistep.delivr.enums.DeliveryStatus;
import com.yetistep.delivr.enums.JobOrderStatus;
import com.yetistep.delivr.enums.Role;
import com.yetistep.delivr.enums.Status;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.util.HibernateUtil;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
        Criteria criteria  = getCurrentSession().createCriteria(MerchantEntity.class, "merchant");
        criteria.addOrder(Order.desc("id"));
        return (List<MerchantEntity>) criteria.list();
    }

    @Override
    public List<MerchantEntity> findAll(Page page) throws Exception {
        Criteria criteria  = getCurrentSession().createCriteria(MerchantEntity.class);
        HibernateUtil.fillPaginationCriteria(criteria, page, MerchantEntity.class);
        return (List<MerchantEntity>) criteria.list();
    }

    @Override
    public Boolean save(MerchantEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean update(MerchantEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean delete(MerchantEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Session getCurrentSession() throws Exception {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void saveStore(List<StoreEntity> values) throws Exception {
        Integer i = 0;
        for (StoreEntity value: values) {
            getCurrentSession().persist(value);
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
            getCurrentSession().persist(value);
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
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean saveStoresBrand(StoresBrandEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public StoresBrandEntity getBrandByBrandName(String brandName) throws Exception {

        List<StoresBrandEntity> storeBrandList = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StoresBrandEntity.class);
        criteria.add(Restrictions.eq("brandName", brandName));
        criteria.addOrder(Order.desc("id"));
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
        criteria.addOrder(Order.desc("id"));
        categories = criteria.list();

        return categories;
    }

    @Override
    public  List<CategoryEntity> getDefaultCategories() throws Exception {
        List<CategoryEntity> categories = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CategoryEntity.class);
        criteria.add(Restrictions.and(Restrictions.isNotNull("parent.id"), Restrictions.isNull("storesBrand.id")));
        criteria.addOrder(Order.asc("priority"));
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
            merchantEntity.setCommissionPercentage(row[0] == null ? BigDecimal.ZERO : new BigDecimal(row[0].toString()));
            merchantEntity.setServiceFee(row[1] == null ? BigDecimal.ZERO : new BigDecimal(row[1].toString()));
        }
        return merchantEntity;
    }

    @Override
    public StoreEntity getStoreById(Integer id) throws Exception {
        return (StoreEntity) getCurrentSession().get(StoreEntity.class, id);
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
        criteria.addOrder(Order.desc("id"));
        brandsCategories = criteria.list();

        return brandsCategories;
    }

    @Override
    public List<BrandsCategoryEntity> getBrandsCategory(List<Integer> brandId) throws Exception {
        List<BrandsCategoryEntity> brandsCategories = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BrandsCategoryEntity.class);
        criteria.add(Restrictions.in("storesBrand.id", brandId));
        brandsCategories = criteria.list();

        return brandsCategories;
    }

    @Override
    public List<CategoryEntity> findFinalCategoryList(Integer brandId) throws Exception {
        //"SELECT id FROM categories AS Cat WHERE Cat.id NOT IN (SELECT Cat.parent_id FROM categories Cat WHERE Cat.parent_id IS NOT NULL) && (brand_id = 1 OR brand_id IS NULL)";
        String hqlQuery = "FROM CategoryEntity cat WHERE cat.id NOT IN (SELECT cat.parent.id FROM CategoryEntity cat WHERE cat.parent IS NOT NULL) AND (cat.storesBrand.id = :brandId OR cat.storesBrand IS NULL)";

        Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery);
        query.setParameter("brandId", brandId);
        return (List<CategoryEntity>) query.list();
    }

    @Override
    public List<CategoryEntity> findFinalCategoryList(List<Integer> brandId) throws Exception {
        //"SELECT id FROM categories AS Cat WHERE Cat.id NOT IN (SELECT Cat.parent_id FROM categories Cat WHERE Cat.parent_id IS NOT NULL) && (brand_id = 1 OR brand_id IS NULL)";
        String hqlQuery = "FROM CategoryEntity cat WHERE cat.id NOT IN (SELECT cat.parent.id FROM CategoryEntity cat WHERE cat.parent IS NOT NULL) AND (cat.storesBrand.id IN "+brandId.toString().replace("[", "(").replace("]", ")")+" OR cat.storesBrand IS NULL)";

        Query query = sessionFactory.getCurrentSession().createQuery(hqlQuery);
        //query.setParameter("brandId", brandId);
        return (List<CategoryEntity>) query.list();
    }




    @Override
    public List<CategoryEntity> findParentCategories() throws Exception {
        List<CategoryEntity> categories = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CategoryEntity.class);
        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("name"), "name")
                .add(Projections.property("imageUrl"), "imageUrl"))
                .setResultTransformer(Transformers.aliasToBean(CategoryEntity.class));
        criteria.add(Restrictions.isNull("parent")) ;
        criteria.addOrder(Order.asc("priority"));
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
        criteria.addOrder(Order.desc("id"));
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
    public List<StoresBrandEntity> findBrandListByMerchant(Integer merchantId, Page page) throws Exception {
        List<StoresBrandEntity> stores = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StoresBrandEntity.class);
        criteria.add(Restrictions.and(Restrictions.eq("merchant.id", merchantId))) ;
        HibernateUtil.fillPaginationCriteria(criteria, page, StoresBrandEntity.class);
        stores = criteria.list();
        return stores;
    }


    @Override
    public List<StoresBrandEntity> findBrandListByMerchant(Integer merchantId) throws Exception {
        List<StoresBrandEntity> stores = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StoresBrandEntity.class, "brand");
        criteria.add(Restrictions.and(Restrictions.eq("merchant.id", merchantId))) ;
        criteria.addOrder(Order.desc("id"));
        stores = criteria.list();
        return stores;
    }


    @Override
     public Integer getTotalNumberOfBrandByMerchant(Integer merchantId, Page page) throws Exception{
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StoresBrandEntity.class);
        criteria.add(Restrictions.and(Restrictions.eq("merchant.id", merchantId))) ;
        HibernateUtil.fillPaginationCriteria(criteria, page, StoresBrandEntity.class);
        return (int) (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
    }

    @Override
    public Integer getTotalNumberOfBrand(Page page) throws Exception {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StoresBrandEntity.class);
        HibernateUtil.fillPaginationCriteria(criteria, page, StoresBrandEntity.class);
        return (int) (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
    }

    @Override
    public List<StoresBrandEntity> findBrandList(Integer merchantId) throws Exception {
        List<StoresBrandEntity> stores = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StoresBrandEntity.class, "brand");
        criteria.createAlias("brand.merchant", "merchant");
        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("brandName"), "brandName")
                .add(Projections.property("brandLogo"), "brandLogo")
                .add(Projections.property("brandImage"), "brandImage")
                .add(Projections.property("featured"), "featured")
                .add(Projections.property("priority"), "priority")
                .add(Projections.property("status"), "status")
                .add(Projections.property("openingTime"), "openingTime")
                .add(Projections.property("closingTime"), "closingTime")
        ).setResultTransformer(Transformers.aliasToBean(StoresBrandEntity.class));
        criteria.add(Restrictions.and(Restrictions.eq("merchant.id", merchantId))) ;
        criteria.addOrder(Order.desc("id"));
        stores = criteria.list();
        return stores;
    }

    @Override
    public List<StoresBrandEntity> findBrandList(Page page) throws Exception {
        List<StoresBrandEntity> stores = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StoresBrandEntity.class);
        HibernateUtil.fillPaginationCriteria(criteria, page, StoresBrandEntity.class);
        stores = criteria.list();
        return stores;
    }

    @Override
    public List<StoresBrandEntity> findBrandList() throws Exception {
        List<StoresBrandEntity> stores = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StoresBrandEntity.class);
        criteria.addOrder(Order.desc("id"));
        stores = criteria.list();
        return stores;
    }

    @Override
    public StoresBrandEntity findBrandDetail(Integer brandId) throws Exception {
        return (StoresBrandEntity) getCurrentSession().get(StoresBrandEntity.class, brandId);
    }

    @Override
    public List<CategoryEntity> findChildCategories(Integer parentId, Integer storeId) throws Exception {
        List<CategoryEntity> categories = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CategoryEntity.class);
        Criterion rest1 = Restrictions.and(Restrictions.isNull("storesBrand"), Restrictions.eq("parent.id", parentId));
        Criterion rest2 = Restrictions.and(Restrictions.eq("storesBrand.id", storeId), Restrictions.eq("parent.id", parentId));
        criteria.add(Restrictions.or(rest1, rest2));
        categories = criteria.list();
        return categories;
    }


    @Override
    public List<CategoryEntity> findChildCategories(Integer parentId, Integer storeId, Page page) throws Exception {
        List<CategoryEntity> categories = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CategoryEntity.class);
        Criterion rest1 = Restrictions.and(Restrictions.isNull("storesBrand"), Restrictions.eq("parent.id", parentId));
        Criterion rest2 = Restrictions.and(Restrictions.eq("storesBrand.id", storeId), Restrictions.eq("parent.id", parentId));
        criteria.add(Restrictions.or(rest1, rest2));
        HibernateUtil.fillPaginationCriteria(criteria, page, CategoryEntity.class);
        categories = criteria.list();
        return categories;
    }


    @Override
    public Integer getTotalNumberOfChildCategory(Integer parentId, Integer storeId) throws Exception {
        String sqQuery =    "SELECT COUNT(c.id) FROM categories c WHERE c.parent_id =:parentId AND c.brand_id =:storeId";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqQuery);
        query.setParameter("parentId", parentId);
        query.setParameter("storeId", storeId);

        BigInteger cnt = (BigInteger) query.uniqueResult();
        return cnt.intValue();
    }

    @Override
    public void saveCategories(List<CategoryEntity> categories) throws Exception {
        Integer i = 0;
        for (CategoryEntity value: categories) {
            if(value.getId() == null && value.getName() !=null){
                getCurrentSession().save(value);
            }
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
        getCurrentSession().persist(value);
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
    public List<ItemEntity> getCategoriesItems(Integer categoryId, Integer brandId, Integer itemCount) throws Exception {
        List<ItemEntity> items = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ItemEntity.class);
        criteria.add(Restrictions.and(Restrictions.eq("category.id", categoryId), Restrictions.eq("storesBrand.id", brandId)));
        criteria.setMaxResults(itemCount);
        criteria.addOrder(Order.asc("status"));
        items = criteria.list();
        return items;
    }

    @Override
    public List<ItemEntity> getCategoriesItems(Integer categoryId, Integer brandId, Page page) throws Exception {
        List<ItemEntity> items = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ItemEntity.class);
        criteria.add(Restrictions.and(Restrictions.eq("category.id", categoryId), Restrictions.eq("storesBrand.id", brandId)));
        criteria.addOrder(Order.asc("status"));
        HibernateUtil.fillPaginationCriteria(criteria, page, ItemEntity.class);
        items = criteria.list();
        return items;
    }


    @Override
    public List<ItemEntity> getCategoriesItems(Integer categoryId) throws Exception {
        List<ItemEntity> items = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ItemEntity.class);
        criteria.add(Restrictions.and(Restrictions.eq("category.id", categoryId)));
        criteria.addOrder(Order.asc("status"));
        items = criteria.list();
        return items;
    }

    @Override
    public List<ItemEntity> findItemByCategory(List<Integer> categoryId, Integer brandId, Integer itemCount) throws Exception {
        List<ItemEntity> items = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ItemEntity.class);
        criteria.add(Restrictions.and(Restrictions.in("category.id", categoryId), Restrictions.eq("storesBrand.id", brandId)));
        criteria.add(Restrictions.sqlRestriction("1=1 order by rand()"));
        criteria.setMaxResults(itemCount);
        items = criteria.list();
        return items;
    }

    @Override
    public List<ItemEntity> findItemByCategory(List<Integer> categoryId, Integer brandId) throws Exception {
        List<ItemEntity> items = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ItemEntity.class);
        criteria.add(Restrictions.and(Restrictions.in("category.id", categoryId), Restrictions.eq("storesBrand.id", brandId)));
        criteria.add(Restrictions.sqlRestriction("1=1 order by rand()"));
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
        criteria.addOrder(Order.asc("status"));
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
        return  (Boolean) query.uniqueResult();
    }

    @Override
    public Boolean updateItem(ItemEntity value) throws Exception {
        getCurrentSession().merge(value);
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
        return  (MerchantEntity) query.uniqueResult();
    }

    @Override
    public List<ItemEntity> getWebSearchItem(String searchString, List<Integer> categoryId, List<Integer> storeId, Page page) throws Exception{
        List<ItemEntity> items = new ArrayList<>();

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ItemEntity.class);
        String[] splitString = searchString.split(",");
        List<String> finalSearchString = new ArrayList<>();
        String implodeString = "";
        for (String s: splitString){
            finalSearchString.add(s);
            implodeString+=s;
        }

        String[] explodeSting = implodeString.split("(?!^)");
        String genImplodeArray =  explodeSting[0];
        List<String> genSearchString = new ArrayList<>();
        Integer cnt = 0;
        for (String s: explodeSting){
            if(cnt>0){
                genImplodeArray+=s;
                if(cnt>2){
                    genSearchString.add(genImplodeArray);
                }
            }
            cnt++;
        }

        Collections.reverse(genSearchString);
        finalSearchString.addAll(genSearchString);

        Disjunction disjunction = Restrictions.disjunction();
        for (String s: finalSearchString){
            disjunction.add(Restrictions.like("name", s, MatchMode.ANYWHERE));
            disjunction.add(Restrictions.like("tags", s, MatchMode.ANYWHERE));
        }
        criteria.add(Restrictions.and(disjunction, Restrictions.in("category.id", categoryId),  Restrictions.in("storesBrand.id", storeId)));
        HibernateUtil.fillPaginationCriteria(criteria, page, ItemEntity.class);
        items = criteria.list();

        return items;
    }


    @Override
    public Integer getTotalNumberOfItems(String searchString, List<Integer> categoryId, List<Integer> storeId) throws Exception {
        /*Criteria criteriaCount = getCurrentSession().createCriteria(ItemEntity.class);
        criteriaCount.setProjection(Projections.rowCount()).add(Restrictions.and(Restrictions.like("name", searchString), Restrictions.in("category.id", categoryId), Restrictions.eq("storesBrand.id", storeId)));
        Long count = (Long) criteriaCount.uniqueResult();
        return (count != null) ? count.intValue() : null;*/
        String[] splitString = searchString.split(",");

        List<String> finalSearchString = new ArrayList<>();
        String implodeString = "";
        for (String s: splitString){
            finalSearchString.add(s);
            implodeString+=s;
        }

        String[] explodeSting = implodeString.split("(?!^)");
        String genImplodeArray =  explodeSting[0];
        List<String> genSearchString = new ArrayList<>();
        Integer cnt = 0;
        for (String s: explodeSting){
            if(cnt>0){
                genImplodeArray+=s;
                if(cnt>2){
                    genSearchString.add(genImplodeArray);
                }
            }
            cnt++;
        }
        Collections.reverse(genSearchString);
        finalSearchString.addAll(genSearchString);

        String queryString = "";
        Integer i = 0;
        for (String s: finalSearchString){
            if(i == 0)
                queryString =  queryString+ "i.name LIKE '%"+s+"%' OR i.tags LIKE '%"+s+"%'";
            else
                queryString =  queryString+ "OR i.name LIKE '%"+s+"%' OR i.tags LIKE '%"+s+"%'";
            i++;
        }

        String sqQuery =    "SELECT COUNT(i.id) FROM items i WHERE "+queryString+" AND i.category_id IN(:categoryId) AND i.brand_id IN(:storeId)";

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqQuery);
        query.setParameterList("storeId", storeId);
        query.setParameterList("categoryId", categoryId);

        BigInteger count = (BigInteger) query.uniqueResult();
        return count.intValue();
    }

    @Override
    public Integer getTotalNumberOfItems(Integer categoryId, Integer storeId) throws Exception {
        String sqQuery =    "SELECT COUNT(i.id) FROM items i WHERE i.category_id =:categoryId AND i.brand_id =:storeId";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqQuery);
        query.setParameter("categoryId", categoryId);
        query.setParameter("storeId", storeId);

        BigInteger cnt = (BigInteger) query.uniqueResult();
        return cnt.intValue();
    }

    @Override
    public MerchantEntity getCommissionVatPartnerShipStatus(Integer storeBrandId) throws Exception {
        String sqlQuery = "SELECT m.id as id, m.partnership_status as partnershipStatus, " +
                "m.service_fee as serviceFee, m.commission_percentage as commissionPercentage " +
                "FROM merchants m INNER JOIN stores_brands sb on sb.merchant_id = m.id WHERE sb.id =:storeBrandId";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery)
                .setResultTransformer(Transformers.aliasToBean(MerchantEntity.class));
        query.setParameter("storeBrandId", storeBrandId);
        return (MerchantEntity) query.uniqueResult();
    }

    @Override
    public List<Integer> getBrandIdList(Integer merchantId) throws Exception {
        String sqQuery = "SELECT sb.id FROM stores_brands sb WHERE merchant_id =:merchantId";
        Query query = getCurrentSession().createSQLQuery(sqQuery);
        query.setParameter("merchantId", merchantId);
        return (List<Integer>) query.list();
    }

    @Override
    public List<Integer> getBrandIdList() throws Exception {
        String sqQuery = "SELECT sb.id FROM stores_brands sb";
        Query query = getCurrentSession().createSQLQuery(sqQuery);
        return (List<Integer>) query.list();
    }

    @Override
    public List<Integer> getStoreIdList(List<Integer> brandId) throws Exception {
        String sqQuery = "SELECT s.id FROM stores s WHERE stores_brand_id IN(:brandId)";
        Query query = getCurrentSession().createSQLQuery(sqQuery);
        query.setParameterList("brandId", brandId);
        return (List<Integer>) query.list();
    }

    @Override
    public List<OrderEntity> getOrders(List<Integer> storeId, Page page) throws Exception {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OrderEntity.class);
        criteria.add(Restrictions.and(Restrictions.in("store.id", storeId)));
        HibernateUtil.fillPaginationCriteria(criteria, page, OrderEntity.class);
        return (List<OrderEntity>) criteria.list();
    }

    @Override
    public List<OrderEntity> getOrders(List<Integer> storeId, Page page, DeliveryStatus status) throws Exception {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OrderEntity.class);
        criteria.add(Restrictions.and(Restrictions.in("store.id", storeId), Restrictions.eq("deliveryStatus", status)));
        HibernateUtil.fillPaginationCriteria(criteria, page, OrderEntity.class);
        return (List<OrderEntity>) criteria.list();
    }

    @Override
    public Integer getTotalNumbersOfOrders(List<Integer> storeId, DeliveryStatus status, Page page) throws Exception {
        /*String sqQuery =    "SELECT COUNT(o.id) FROM orders o WHERE o.store_id IN (:storeId) && o.delivery_status =:deliveryStatus";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqQuery);
        query.setParameterList("storeId", storeId);
        query.setParameter("deliveryStatus", status.ordinal());
        BigInteger cnt = (BigInteger) query.uniqueResult();
        return cnt.intValue();*/
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OrderEntity.class);
        criteria.add(Restrictions.and(Restrictions.in("store.id", storeId), Restrictions.eq("deliveryStatus", status)));
        HibernateUtil.fillPaginationCriteria(criteria, page, OrderEntity.class);
        return (int) (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
    }

    @Override
    public List<OrderEntity> getPurchaseHistory(List<Integer> storeId, Page page) throws Exception {
        List<OrderEntity> orders = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OrderEntity.class);
        List<JobOrderStatus> purchaseOrders = new ArrayList<JobOrderStatus>();
        purchaseOrders.add(JobOrderStatus.ORDER_ACCEPTED);
        purchaseOrders.add(JobOrderStatus.IN_ROUTE_TO_PICK_UP);
        purchaseOrders.add(JobOrderStatus.AT_STORE);
        purchaseOrders.add(JobOrderStatus.IN_ROUTE_TO_DELIVERY);
        purchaseOrders.add(JobOrderStatus.DELIVERED);
        criteria.add(Restrictions.and(Restrictions.in("store.id", storeId), Restrictions.in("orderStatus", purchaseOrders)));
        HibernateUtil.fillPaginationCriteria(criteria, page, OrderEntity.class);
        orders = criteria.list();
        return orders;
    }

    @Override
    public Integer getTotalNumbersOfOrders(List<Integer> storeId, Page page) throws Exception {
        /*String sqQuery =    "SELECT COUNT(o.id) FROM orders o WHERE o.store_id IN (:storeId)";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqQuery);
        query.setParameterList("storeId", storeId);
        BigInteger cnt = (BigInteger) query.uniqueResult();
        return cnt.intValue();*/
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OrderEntity.class);
        criteria.add(Restrictions.and(Restrictions.in("store.id", storeId)));
        HibernateUtil.fillPaginationCriteria(criteria, page, OrderEntity.class);
        return (int) (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
    }

    @Override
    public Integer getTotalNumbersOfPurchases(List<Integer> storeId) throws Exception {
        List<Integer> purchaseOrders = new ArrayList<Integer>();
        purchaseOrders.add(JobOrderStatus.IN_ROUTE_TO_DELIVERY.ordinal());
        purchaseOrders.add(JobOrderStatus.DELIVERED.ordinal());
        purchaseOrders.add(JobOrderStatus.ORDER_ACCEPTED.ordinal());
        purchaseOrders.add(JobOrderStatus.IN_ROUTE_TO_PICK_UP.ordinal());
        purchaseOrders.add(JobOrderStatus.AT_STORE.ordinal());
        String sqQuery =    "SELECT COUNT(o.id) FROM orders o  WHERE o.store_id IN(:storeId) AND o.order_status IN(:purchaseOrders)";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqQuery);
        query.setParameterList("storeId", storeId);
        query.setParameterList("purchaseOrders", purchaseOrders);
        BigInteger cnt = (BigInteger) query.uniqueResult();
        return cnt.intValue();
    }

    @Override
    public List<ItemsOrderEntity> getOrdersItems(Integer orderId, Page page) throws Exception {
        List<ItemsOrderEntity> orders = new ArrayList<>();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ItemsOrderEntity.class);
        criteria.add(Restrictions.and(Restrictions.eq("order.id", orderId)));
        HibernateUtil.fillPaginationCriteria(criteria, page, OrderEntity.class);
        orders = criteria.list();
        return orders;
    }

    @Override
    public Integer getTotalNumbersItems(Integer orderId) throws Exception {
        String sqQuery =    "SELECT COUNT(io.id) FROM items_orders io  WHERE io.order_id =:orderId";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqQuery);
        query.setParameter("orderId", orderId);
        BigInteger cnt = (BigInteger) query.uniqueResult();
        return cnt.intValue();
    }

    @Override
    public Boolean checkEmailExistence(String email) throws Exception {
        String sql = "SELECT COUNT(id) FROM users WHERE username = :email AND role_id NOT IN(:role)";
        SQLQuery query = getCurrentSession().createSQLQuery(sql);
        query.setParameter("email", email);
        List<Integer> roleList = new ArrayList<>();
        roleList.add(Role.ROLE_DELIVERY_BOY.toInt());
        roleList.add(Role.ROLE_CUSTOMER.toInt());
        query.setParameterList("role", roleList);
        Integer availableQty = ((Number) query.uniqueResult()).intValue();

        return !availableQty.equals(0);
    }

    @Override
    public Integer getTotalNumberOfMerchants(Page page) throws Exception{
        page.setPageNumber(null);
        page.setPageSize(null);
        /*String sqQuery =    "SELECT COUNT(m.id) FROM merchants m";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqQuery);
        BigInteger cnt = (BigInteger) query.uniqueResult();
        return cnt.intValue();*/
        Criteria criteria  = getCurrentSession().createCriteria(MerchantEntity.class);
        HibernateUtil.fillPaginationCriteria(criteria, page, MerchantEntity.class);
        return (int) (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
    }

}
