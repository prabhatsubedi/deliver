package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.AddressEntity;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 1/19/15
 * Time: 12:55 PM
 * To change this template use File | Settings | File Templates.
 */
public interface AddressDaoService extends GenericDaoService<Integer, AddressEntity> {
    public String getMobileCode(Integer userId, String mobileNo) throws Exception;
}
