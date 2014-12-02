package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.dao.inf.DeliveryBoyDaoService;
import com.yetistep.delivr.dao.inf.UserDaoService;
import com.yetistep.delivr.enums.Role;
import com.yetistep.delivr.model.DeliveryBoyEntity;
import com.yetistep.delivr.model.RoleEntity;
import com.yetistep.delivr.service.inf.DeliveryBoyService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeliveryBoyServiceImpl implements DeliveryBoyService {

    @Autowired
    DeliveryBoyDaoService deliveryBoyDaoService;

    @Autowired
    UserDaoService userDaoService;

    @Override
    public void saveDeliveryBoy(DeliveryBoyEntity deliveryBoy) throws Exception{

            RoleEntity userRole = userDaoService.getRoleByRole(Role.ROLE_DELIVERY_BOY);
            deliveryBoy.getUser().setRole(userRole);
            deliveryBoyDaoService.saveDeliveryBoy(deliveryBoy);


    }
}
