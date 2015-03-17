package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.AddressEntity;
import com.yetistep.delivr.model.ValidateMobileEntity;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 3/16/15
 * Time: 4:08 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ValidateMobileDaoService  extends GenericDaoService<Integer, ValidateMobileEntity> {
    public ValidateMobileEntity getMobileCode(Integer userId, String mobileNo) throws Exception;

    public Boolean updateVerifiedByUser(Integer id) throws Exception;

    public Boolean updateNoOfSMSSend(Integer id) throws Exception;
}
