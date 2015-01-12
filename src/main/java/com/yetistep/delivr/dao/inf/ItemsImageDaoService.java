package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.ItemsImageEntity;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 1/9/15
 * Time: 12:57 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ItemsImageDaoService extends GenericDaoService<Integer, ItemsImageEntity>{
    public ItemsImageEntity findImage(Integer itemId) throws Exception;
}
