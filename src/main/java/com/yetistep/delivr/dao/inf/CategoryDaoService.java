package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.CategoryEntity;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 1/5/15
 * Time: 9:26 AM
 * To change this template use File | Settings | File Templates.
 */
public interface CategoryDaoService extends GenericDaoService<Integer, CategoryEntity>{
    public CategoryEntity findCategory(Integer categoryId) throws Exception;

    List<CategoryEntity> findDefaultCategories() throws Exception;

    public CategoryEntity getCategory(String categoryName, Integer parentId) throws Exception;

    public void updatePriority(List<CategoryEntity> categoryEntities) throws Exception;

    public CategoryEntity getCategory(String categoryName, Integer parentId, Integer storeId) throws Exception;

    public List<CategoryEntity> getBrandsChildCategories(Integer parentId, Integer storeId) throws Exception;

}
