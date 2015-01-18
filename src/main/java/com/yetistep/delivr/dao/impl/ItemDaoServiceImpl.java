package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.ItemDaoService;
import com.yetistep.delivr.enums.Status;
import com.yetistep.delivr.model.CategoryEntity;
import com.yetistep.delivr.model.ItemEntity;
import com.yetistep.delivr.model.mobile.dto.ItemDto;
import com.yetistep.delivr.hbn.AliasToBeanNestedResultTransformer;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 1/6/15
 * Time: 9:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class ItemDaoServiceImpl implements ItemDaoService{
    @Autowired
    SessionFactory sessionFactory;

    @Override
    public ItemEntity find(Integer id) throws Exception {
        return (ItemEntity) getCurrentSession().get(ItemEntity.class, id);
    }

    @Override
    public List<ItemEntity> findAll() throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean save(ItemEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean update(ItemEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean delete(ItemEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Session getCurrentSession() throws Exception {
        Session session = sessionFactory.getCurrentSession();
        return session;
    }

    @Override
    public List<CategoryEntity> findItemCategory(Integer brandId) throws Exception {
        List<CategoryEntity> categoryEntities = new ArrayList<>();

        String sql = "SELECT DISTINCT DISTINCT(cat.id), cat.name, cat.parent_id as parentId FROM items it " +
                "INNER JOIN categories cat ON(cat.id = it.category_id) " +
                "WHERE it.brand_id = :brandId AND it.status =:status";
        SQLQuery query =  getCurrentSession().createSQLQuery(sql);
        query.setParameter("brandId", brandId);
        query.setParameter("status", Status.ACTIVE.ordinal());

        query.setResultTransformer(Transformers.aliasToBean(CategoryEntity.class));
        categoryEntities = query.list();

        return categoryEntities;
    }

    @Override
    public List<ItemDto> findItems(Integer brandId, Integer categoryId) throws Exception {
        List<ItemDto> items;

        String sql = "SELECT it.id, it.name, it.description, it.unit_price AS price, it.service_charge as serviceCharge, it.vat, itim.url AS imageUrl FROM items it " +
                "LEFT JOIN items_images itim ON itim.id = (SELECT MIN(id) FROM items_images WHERE item_id = it.id) " +
                "WHERE it.brand_id = :brandId AND it.category_id = :categoryId AND status =:status";
        SQLQuery query = getCurrentSession().createSQLQuery(sql);
        query.setParameter("brandId", brandId);
        query.setParameter("categoryId", categoryId);
        query.setParameter("status", Status.ACTIVE.ordinal());

        query.setResultTransformer(Transformers.aliasToBean(ItemDto.class));
        items = query.list();

        return items;
    }

    @Override
    public List<ItemEntity> test(Integer itemId) throws Exception {
        ProjectionList projectionList = Projections.projectionList()
                .add(Projections.property("id"), "id")
                .add(Projections.property("name"), "name")
                .add(Projections.property("sb.id"), "storesBrand.id")
                .add(Projections.property("sb.brandName"), "storesBrand.brandName");
                //.add(Projections.property("ii.id"), "itemsImage.id")
                //.add(Projections.property("ii.url"), "itemsImage.url");

        Criteria criteria = getCurrentSession().createCriteria(ItemEntity.class)
        .createAlias("storesBrand", "sb")
        //.createAlias("itemsImage", "ii")
        .setProjection(projectionList)
        .setResultTransformer(new AliasToBeanNestedResultTransformer(ItemEntity.class));


        criteria.add(Restrictions.eq("id", itemId));
        List<ItemEntity> item = criteria.list();

        return item;
    }
}
