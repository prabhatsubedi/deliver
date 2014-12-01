package com.yetistep.delivr.controller;

import com.yetistep.delivr.enums.Role;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.service.inf.*;
import com.yetistep.delivr.util.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/19/14
 * Time: 11:54 AM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/admin")
public class AdminController {
    @Autowired
    UserService userService;
    @Autowired
    DeliveryBoyService deliveryBoyService;
    @Autowired
    MerchantService merchantService;
    @Autowired
    CustomerService customerService;

    @RequestMapping(value = "/save_role", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse processRegistration() {

        try {
            if (userService.findAllRoles().size() == 0) {
                for (int i = 1; i < (Role.values().length + 1); i++) {
                    Role role = Role.fromInt(i);
                    RoleEntity userRole = new RoleEntity();
                    userRole.setRole(role.toStr());
                    userService.saveRole(userRole);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ServiceResponse serviceResponse = new ServiceResponse("Role has been saved successfully");
        return serviceResponse;
    }


}
