package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.enums.Role;
import com.yetistep.delivr.model.UserDeviceEntity;

import java.math.BigInteger;
import java.util.List;

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

    public Boolean updateUserDeviceTokenFromUserID(Integer userId, String deviceToken) throws Exception;

    public List<String> getDeviceTokenFromDeliveryBoyId(List<Integer> deliveryBoysId) throws Exception;

    public UserDeviceEntity getUserDeviceInfoFromOrderId(Integer orderId) throws Exception;

    public Boolean removeInformationForSameDevice(String uuid, Integer userId) throws Exception;

    public List<String> getDeviceTokensExceptAcceptedDeliveryBoy(Integer orderId, Integer exceptDeliveryBoyId) throws Exception;

    public List<String> getDeviceTokensOfAssignedDeliveryBoy(Integer orderId) throws Exception;

    public List<String> getAllDeviceTokensForFamilyAndRole(Role role, String family) throws Exception;
}
