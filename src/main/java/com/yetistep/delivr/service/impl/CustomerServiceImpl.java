package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.dao.inf.CustomerDaoService;
import com.yetistep.delivr.dao.inf.UserDaoService;
import com.yetistep.delivr.enums.Role;
import com.yetistep.delivr.model.CustomerEntity;
import com.yetistep.delivr.model.RoleEntity;
import com.yetistep.delivr.model.UserEntity;
import com.yetistep.delivr.service.inf.CustomerService;
import com.yetistep.delivr.util.GeneralUtil;
import com.yetistep.delivr.util.MessageBundle;
import com.yetistep.delivr.util.YSException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomerServiceImpl implements CustomerService {
    private static final Logger log = Logger.getLogger(CustomerServiceImpl.class);

    @Autowired
    CustomerDaoService customerDaoService;

    @Autowired
    UserDaoService userDaoService;

    @Override
    public void saveCustomer(CustomerEntity customer) throws Exception {
        UserEntity userEntity = customer.getUser();
        if (userEntity.getUsername() == null || userEntity.getPassword() == null || userEntity.getUsername().isEmpty() || userEntity.getPassword().isEmpty())
            throw new YSException("VLD009");

        /* Setting Default value of customer while sign up. */
        RoleEntity userRole = userDaoService.getRoleByRole(Role.ROLE_CUSTOMER);
        userEntity.setRole(userRole);
        userEntity.setPassword(GeneralUtil.encryptPassword(userEntity.getPassword()));
        customer.setTotalOrderPlaced(0);
        customer.setTotalOrderDelivered(0);
        customer.setAverageRating(new BigDecimal(5));
        customer.setFriendsInvitationCount(0);
        customer.setReferenceUrl("");
        customer.setReferredFriendsCount(0);
        customer.setRewardsEarned(new BigDecimal(0));

        String profileImage = customer.getUser().getProfileImage();
        customer.getUser().setProfileImage(null);
        customerDaoService.save(customer);
        if (profileImage != null && !profileImage.isEmpty()) {
            log.info("Uploading Profile Image of delivery boy to S3 Bucket ");

            String dir = MessageBundle.separateString("/", "Customer", "Customer" + customer.getId());
            boolean isLocal = MessageBundle.isLocalHost();
            String imageName = "pimg" + (isLocal ? "_tmp_" : "_") + customer.getId();
            String s3Path = GeneralUtil.saveImageToBucket(profileImage, imageName, dir, true);
            customer.getUser().setProfileImage(s3Path);
            customerDaoService.update(customer);
        }
    }
}
