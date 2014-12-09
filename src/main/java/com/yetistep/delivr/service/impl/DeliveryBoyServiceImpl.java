package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.dao.inf.DeliveryBoyDaoService;
import com.yetistep.delivr.dao.inf.UserDaoService;
import com.yetistep.delivr.enums.DBoyStatus;
import com.yetistep.delivr.enums.Role;
import com.yetistep.delivr.model.DeliveryBoyEntity;
import com.yetistep.delivr.model.RoleEntity;
import com.yetistep.delivr.model.UserEntity;
import com.yetistep.delivr.service.inf.DeliveryBoyService;
import com.yetistep.delivr.util.GeneralUtil;
import com.yetistep.delivr.util.MessageBundle;
import com.yetistep.delivr.util.YSException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeliveryBoyServiceImpl implements DeliveryBoyService {
    private static final Logger log = Logger.getLogger(DeliveryBoyServiceImpl.class);

    @Autowired
    DeliveryBoyDaoService deliveryBoyDaoService;

    @Autowired
    UserDaoService userDaoService;

    @Override
    public void saveDeliveryBoy(DeliveryBoyEntity deliveryBoy) throws Exception {
        log.info("++++++++++++++++++ Creating Delivery Boy +++++++++++++++++");
        UserEntity user = deliveryBoy.getUser();
        if ((user.getUsername() == null || user.getPassword() == null) || (user.getUsername().isEmpty() || user.getPassword().isEmpty()))
            throw new YSException("VLD009");
        user.setPassword(GeneralUtil.encryptPassword(user.getPassword()));

        RoleEntity userRole = userDaoService.getRoleByRole(Role.ROLE_DELIVERY_BOY);

        /** Setting Default values **/
        user.setRole(userRole);
        user.setBlacklistStatus(false);
        user.setMobileVerificationStatus(false);
        user.setVerifiedStatus(true);
        user.setSubscribeNewsletter(false);

        deliveryBoy.setAvailabilityStatus(DBoyStatus.FREE);
        deliveryBoy.setAverageRating(new BigDecimal(0));
        deliveryBoy.setTotalOrderTaken(0);
        deliveryBoy.setTotalOrderDelivered(0);
        deliveryBoy.setTotalOrderUndelivered(0);
        deliveryBoy.setTotalEarnings(new BigDecimal(0));
        deliveryBoy.setActiveOrderNo(0);

        String profileImage = deliveryBoy.getUser().getProfileImage();
        deliveryBoy.getUser().setProfileImage(null);
        deliveryBoyDaoService.save(deliveryBoy);
        if (profileImage != null && !profileImage.isEmpty()) {
            log.info("Uploading Profile Image of delivery boy to S3 Bucket ");

            String dir = MessageBundle.separateString("/", "DBoy", "DBoy" + deliveryBoy.getId());
            boolean isLocal = MessageBundle.isLocalHost();
            String imageName = "pimg" + (isLocal ? "_tmp_" : "_") + deliveryBoy.getId();
            String s3Path = GeneralUtil.saveImageToBucket(profileImage, imageName, dir, true);
            deliveryBoy.getUser().setProfileImage(s3Path);
            deliveryBoyDaoService.update(deliveryBoy);
        }

    }

    @Override
    public DeliveryBoyEntity findDeliveryBoyById(Integer id) throws Exception {
        log.info("Retrieving Deliver Boy With ID:" + id);
        DeliveryBoyEntity deliveryBoyEntity = deliveryBoyDaoService.find(id);
        if (deliveryBoyEntity == null) {
            throw new YSException("VLD011");
        }
        return deliveryBoyEntity;
    }

    @Override
    public List<DeliveryBoyEntity> findAllDeliverBoy() throws Exception {
        log.info("Retrieving list of Deliver Boys");
        List<DeliveryBoyEntity> deliveryBoyEntities = deliveryBoyDaoService.findAll();
        return deliveryBoyEntities;
    }

    @Override
    public Boolean updateDeliveryBoy(DeliveryBoyEntity deliveryBoyEntity) throws Exception {
        return deliveryBoyDaoService.update(deliveryBoyEntity);
    }

    @Override
    public Boolean updateDeliveryBoyStatus(Integer id, DBoyStatus dBoyStatus) throws Exception {
        log.info("Updating Status of Delivery Boy to:" + dBoyStatus);
        DeliveryBoyEntity deliveryBoyEntity = deliveryBoyDaoService.find(id);
        if (deliveryBoyEntity == null) {
            throw new YSException("VLD011");
        }
        deliveryBoyEntity.setAvailabilityStatus(dBoyStatus);
        return deliveryBoyDaoService.update(deliveryBoyEntity);
    }
}
