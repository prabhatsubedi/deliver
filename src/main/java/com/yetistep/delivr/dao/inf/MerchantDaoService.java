package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.MerchantEntity;
import com.yetistep.delivr.model.StoreEntity;
import com.yetistep.delivr.model.StoresBrandsEntity;


/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 12:21 PM
 * To change this template use File | Settings | File Templates.
 */
public interface MerchantDaoService extends GenericDaoService<Integer, MerchantEntity>{

    public void saveStore(StoreEntity value) throws Exception;

    public StoresBrandsEntity getBrandByBrandName(String brandName) throws Exception;
}
