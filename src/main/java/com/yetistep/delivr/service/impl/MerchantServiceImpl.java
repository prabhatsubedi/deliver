package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.dao.inf.MerchantDaoService;
import com.yetistep.delivr.dao.inf.UserDaoService;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.service.inf.MerchantService;
import com.yetistep.delivr.util.YSException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Surendra
 * Date: 11/21/14
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchantServiceImpl implements MerchantService {
    private static final Logger log = Logger.getLogger(MerchantServiceImpl.class);
    @Autowired
    MerchantDaoService merchantDaoService;

    @Autowired
    UserDaoService userDaoService;

    @Override
    public void saveMerchant(MerchantEntity merchant, String username, String password) throws Exception {
        log.info("++++++++++++++++++ Creating Merchant +++++++++++++++++");
        UserEntity user = merchant.getUser();
        if((username==null || password==null) || (username.isEmpty() || password.isEmpty()))
            throw new YSException("VLD009");

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(password);

        user.setUsername(username);
        user.setPassword(hashedPassword);
        merchant.setUser(user);

        log.info("++++++++++ Checking Role from Database +++++++++++++++++++++++++");
        RoleEntity userRole = userDaoService.getRoleByRole(merchant.getUser().getRole().getRole());
        merchant.getUser().setRole(userRole);
        merchantDaoService.save(merchant);

    }
}
