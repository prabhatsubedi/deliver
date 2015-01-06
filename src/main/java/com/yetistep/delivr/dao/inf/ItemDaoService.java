package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.CategoryEntity;
import com.yetistep.delivr.model.ItemEntity;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 1/6/15
 * Time: 9:50 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ItemDaoService extends GenericDaoService<Integer, ItemEntity>{
    public List<CategoryEntity> findItemCategory(Integer brandId) throws Exception;
}
