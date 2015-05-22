package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.ItemsImageDaoService;
import com.yetistep.delivr.model.ItemsImageEntity;
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
 * Date: 1/9/15
 * Time: 12:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class ItemsImageDaoServiceImpl implements ItemsImageDaoService{
    @Autowired
    SessionFactory sessionFactory;

    @Override
    public ItemsImageEntity find(Integer id) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ItemsImageEntity> findAll() throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean save(ItemsImageEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean update(ItemsImageEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean delete(ItemsImageEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Session getCurrentSession() throws Exception {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public ItemsImageEntity findImage(Integer itemId) throws Exception {
        String sql = "SELECT id, url FROM items_images WHERE item_id = :itemId";
        SQLQuery query = getCurrentSession().createSQLQuery(sql);
        query.setParameter("itemId", itemId);
        query.setResultTransformer(Transformers.aliasToBean(ItemsImageEntity.class));
        ItemsImageEntity itemsImageEntity;
        itemsImageEntity = query.list().size() > 0 ? (ItemsImageEntity) query.list().get(0) : null;
        return itemsImageEntity;
    }
}
