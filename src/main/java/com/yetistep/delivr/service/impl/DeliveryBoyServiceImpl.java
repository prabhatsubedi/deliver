package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.dao.inf.DeliveryBoyDaoService;
import com.yetistep.delivr.dao.inf.UserDaoService;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.enums.DBoyStatus;
import com.yetistep.delivr.enums.Role;
import com.yetistep.delivr.model.AddressEntity;
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
    public void saveDeliveryBoy(DeliveryBoyEntity deliveryBoy, HeaderDto headerDto) throws Exception {
        log.info("++++++++++++++++++ Creating Delivery Boy +++++++++++++++++");
        deliveryBoy.getUser().setUsername(headerDto.getUsername());
        deliveryBoy.getUser().setPassword(headerDto.getPassword());
        UserEntity user = deliveryBoy.getUser();
        if ((user.getUsername() == null || user.getPassword() == null) || (user.getUsername().isEmpty() || user.getPassword().isEmpty()))
            throw new YSException("VLD009");
        user.setPassword(GeneralUtil.encryptPassword(user.getPassword()));

        RoleEntity userRole = userDaoService.getRoleByRole(Role.ROLE_DELIVERY_BOY);

        /** Setting Default values **/
        user.setRole(userRole);
        user.setBlacklistStatus(false);
        user.setMobileVerificationStatus(true);
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
    public DeliveryBoyEntity findDeliveryBoyById(HeaderDto headerDto) throws Exception {
        Integer id = Integer.parseInt(headerDto.getId());
        log.info("Retrieving Deliver Boy With ID:" + id);
        DeliveryBoyEntity deliveryBoyEntity = deliveryBoyDaoService.find(id);
        if (deliveryBoyEntity == null) {
            throw new YSException("VLD011");
        }
        deliveryBoyEntity.getUser().setRole(null);
        return deliveryBoyEntity;
    }

    @Override
    public List<DeliveryBoyEntity> findAllDeliverBoy() throws Exception {
        log.info("Retrieving list of Deliver Boys");
        List<DeliveryBoyEntity> deliveryBoyEntities = deliveryBoyDaoService.findAll();
        /*For filtering role -- set to null as all delivery boy has same role*/
        for(DeliveryBoyEntity deliveryBoyEntity: deliveryBoyEntities){
            deliveryBoyEntity.getUser().setRole(null);
        }
        return deliveryBoyEntities;
    }

    @Override
    public Boolean updateDeliveryBoy(DeliveryBoyEntity deliveryBoyEntity, HeaderDto headerDto) throws Exception {
        DeliveryBoyEntity dBoyEntity = deliveryBoyDaoService.find(deliveryBoyEntity.getId());
        if (dBoyEntity == null) {
            throw new YSException("VLD011");
        }
        dBoyEntity.getUser().setUsername(headerDto.getUsername());
        dBoyEntity.getUser().setPassword(GeneralUtil.encryptPassword(headerDto.getPassword()));
        dBoyEntity.getUser().setMobileNumber(headerDto.getUsername());
        for(AddressEntity addressEntity: deliveryBoyEntity.getUser().getAddresses()){
            for(AddressEntity address: dBoyEntity.getUser().getAddresses()){
                if(address.getId().equals(addressEntity.getId())){
                   address.setStreet(addressEntity.getStreet());
                   address.setCity(addressEntity.getCity());
                   address.setState(addressEntity.getState());
                   address.setCountry(addressEntity.getCountry());
                   address.setCountryCode(addressEntity.getCountryCode());
                   break;
                }
            }
        }
        dBoyEntity.setVehicleNumber(deliveryBoyEntity.getVehicleNumber());
        dBoyEntity.setVehicleType(deliveryBoyEntity.getVehicleType());
        dBoyEntity.setLicenseNumber(deliveryBoyEntity.getLicenseNumber());

        String profileImage = deliveryBoyEntity.getUser().getProfileImage();
        if (profileImage != null && !profileImage.isEmpty()) {
            log.info("Uploading Profile Image of delivery boy to S3 Bucket ");

            String dir = MessageBundle.separateString("/", "DBoy", "DBoy" + dBoyEntity.getId());
            boolean isLocal = MessageBundle.isLocalHost();
            String imageName = "pimg" + (isLocal ? "_tmp_" : "_") + dBoyEntity.getId();
            String s3Path = GeneralUtil.saveImageToBucket(profileImage, imageName, dir, true);
            dBoyEntity.getUser().setProfileImage(s3Path);
        }

        return deliveryBoyDaoService.update(dBoyEntity);
    }

    @Override
    public Boolean updateDeliveryBoyStatus(DeliveryBoyEntity deliveryBoyEntity) throws Exception {
        log.info("Updating Status of Delivery Boy to:" + deliveryBoyEntity.getAvailabilityStatus());
        DeliveryBoyEntity dBoyEntity = deliveryBoyDaoService.find(deliveryBoyEntity.getId());
        if (dBoyEntity == null) {
            throw new YSException("VLD011");
        }
        dBoyEntity.setAvailabilityStatus(deliveryBoyEntity.getAvailabilityStatus());
        return deliveryBoyDaoService.update(dBoyEntity);
    }

    @Override
    public DeliveryBoyEntity dboyLogin(HeaderDto headerDto) throws Exception {
        log.info("+++++++++++++++ Checking DBOY Credential +++++++++++++++");

        UserEntity userEntity = userDaoService.findByUserName(headerDto.getUsername());

        if(userEntity == null)
            throw new YSException("VLD011");

        GeneralUtil.matchDBPassword(headerDto.getPassword(), userEntity.getPassword());

        userEntity.setRole(null);
        DeliveryBoyEntity deliveryBoyEntity = userEntity.getDeliveryBoy();
        //DeliveryBoyEntity deliveryBoyEntity = deliveryBoyDaoService.find(userEntity.getDeliveryBoy().getId());
        return deliveryBoyEntity;
    }
}
