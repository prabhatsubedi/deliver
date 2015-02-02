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
        if(value.getName().contains("#")) {
            String[] arrCat = value.getName().split("#");
            Integer i=0;
            for (String cat: arrCat){
                CategoryEntity catE = new CategoryEntity();
                catE.setName(cat);
                if(value.getParent() != null)
                    catE.setParent(value.getParent());

                getCurrentSession().save(catE);
                if ( i % 20 == 0 ) { //20, same as the JDBC batch size
                    //flush a batch of inserts and release memory:
                    getCurrentSession().flush();
                    getCurrentSession().clear();
                }
                i++;
            }

        } else {
            getCurrentSession().save(value);
        }
        return true;
    }

    @Override
    public Boolean update(CategoryEntity value) throws Exception {
        getCurrentSession().update(value);
        return true;
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

    @Override
    public List<CategoryEntity> findDefaultCategories() throws Exception {
        String sql = "SELECT DISTINCT(c.id), c.name, c.image_url AS imageUrl FROM categories c " +
                "INNER JOIN brands_categories bc ON(bc.category_id = c.id) " +
                "WHERE c.parent_id IS NULL ";

        SQLQuery sqlQuery = getCurrentSession().createSQLQuery(sql);
        sqlQuery.setResultTransformer(Transformers.aliasToBean(CategoryEntity.class));
        List<CategoryEntity> categories = sqlQuery.list();
        return categories;
    }


}
