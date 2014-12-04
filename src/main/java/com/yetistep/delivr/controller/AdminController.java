package com.yetistep.delivr.controller;

import com.yetistep.delivr.enums.Role;
import com.yetistep.delivr.model.RoleEntity;
import com.yetistep.delivr.model.UserEntity;
import com.yetistep.delivr.service.inf.CustomerService;
import com.yetistep.delivr.service.inf.DeliveryBoyService;
import com.yetistep.delivr.service.inf.MerchantService;
import com.yetistep.delivr.service.inf.UserService;
import com.yetistep.delivr.util.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @RequestMapping(value = "/save_manager", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse processRegistration(@RequestBody final UserEntity user) {

        try {
            String password = user.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(password);

            user.setPassword(hashedPassword);

            user.setMobileVerificationStatus(false);
            //user.setLastActivityDate(new Date());
            user.setBlacklistStatus(false);
            user.setVerifiedStatus(false);

            userService.saveUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ServiceResponse serviceResponse = new ServiceResponse("User has been saved successfully");
        return serviceResponse;
    }


    @RequestMapping(value = "/save_role", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse processRegistration() {

        try {
            if (userService.findAllRoles().size() == 0) {
                for (int i = 1; i < (Role.values().length + 1); i++) {
                    Role role = Role.fromInt(i);
                    RoleEntity userRole = new RoleEntity();
                    userRole.setRole(role);
                    //userService.saveRole(userRole);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ServiceResponse serviceResponse = new ServiceResponse("Role has been saved successfully");
        return serviceResponse;
    }


}
