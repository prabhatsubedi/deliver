package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.ItemsAttributeDaoService;
import com.yetistep.delivr.model.ItemsAttributeEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 1/22/15
 * Time: 10:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class ItemsAttributeDaoServiceImpl implements ItemsAttributeDaoService {
    @Autowired
    SessionFactory sessionFactory;

    @Override
    public ItemsAttributeEntity find(Integer id) throws Exception {
        return (ItemsAttributeEntity) getCurrentSession().get(ItemsAttributeEntity.class, id);
    }

    @Override
    public List<ItemsAttributeEntity> findAll() throws Exception {
        return (List<ItemsAttributeEntity>) getCurrentSession().createCriteria(ItemsAttributeEntity.class).list();
    }

    @Override
    public Boolean save(ItemsAttributeEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean update(ItemsAttributeEntity value) throws Exception {
        getCurrentSession().persist(value);
        return true;
    }

    @Override
    public Boolean delete(ItemsAttributeEntity value) throws Exception {
        return null;
    }

    @Override
    public Session getCurrentSession() throws Exception {
        Session session = sessionFactory.getCurrentSession();
        return session;
    }
}
