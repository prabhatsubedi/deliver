package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.CategoryEntity;
import com.yetistep.delivr.model.ItemEntity;
import com.yetistep.delivr.model.mobile.dto.ItemDto;

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

    public List<ItemDto> findItems(Integer brandId, Integer categoryId) throws Exception;

    public List<ItemEntity> test(Integer itemId) throws Exception;

    public List<ItemEntity> searchItems(String word) throws Exception;

    public List<ItemEntity> searchItemsInStore(String word, Integer brandId) throws Exception;

    public List<ItemEntity> findItems(Integer storeId, Integer categoryId, String name) throws Exception;

}
