package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.CategoryDaoService;
import com.yetistep.delivr.model.CategoryEntity;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 1/5/15
 * Time: 9:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class CategoryDaoServiceImpl implements CategoryDaoService{
    @Autowired
    SessionFactory sessionFactory;

    @Override
    public CategoryEntity find(Integer id) throws Exception {
        return (CategoryEntity) getCurrentSession().get(CategoryEntity.class, id);
    }

    @Override
    public List<CategoryEntity> findAll() throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean save(CategoryEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean update(CategoryEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean delete(CategoryEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Session getCurrentSession() throws Exception {
        Session session = sessionFactory.getCurrentSession();
        return session;
    }

    @Override
    public CategoryEntity findCategory(Integer categoryId) throws Exception {
        CategoryEntity categoryEntity = new CategoryEntity();
        String sql = "SELECT id, name FROM categories where id = "+ categoryId;
        SQLQuery query = getCurrentSession().createSQLQuery(sql);
        query.setResultTransformer(Transformers.aliasToBean(CategoryEntity.class));
        categoryEntity = (CategoryEntity) query.list().get(0);
        return categoryEntity;
    }
}
