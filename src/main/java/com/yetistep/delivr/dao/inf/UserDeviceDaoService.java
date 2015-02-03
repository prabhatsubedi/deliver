package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.UserDeviceEntity;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/30/14
 * Time: 2:38 PM
 * To change this template use File | Settings | File Templates.
 */
public interface UserDeviceDaoService extends GenericDaoService<Integer, UserDeviceEntity>{
    public UserDeviceEntity find(BigInteger customerId, String uuid) throws Exception;

    public Boolean updateUserDeviceToken(Long facebookId, String deviceToken) throws Exception;
}
