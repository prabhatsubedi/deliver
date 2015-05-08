package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.abs.AbstractManager;
import com.yetistep.delivr.dao.inf.UserDaoService;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.enums.PasswordActionType;
import com.yetistep.delivr.enums.Role;
import com.yetistep.delivr.enums.Status;
import com.yetistep.delivr.model.RoleEntity;
import com.yetistep.delivr.model.StoresBrandEntity;
import com.yetistep.delivr.model.UserEntity;
import com.yetistep.delivr.service.inf.UserService;
import com.yetistep.delivr.util.EmailMsg;
import com.yetistep.delivr.util.GeneralUtil;
import com.yetistep.delivr.util.MessageBundle;
import com.yetistep.delivr.util.YSException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/6/14
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserServiceImpl extends AbstractManager implements UserService{

    private static final Logger log = Logger.getLogger(UserServiceImpl.class);
    @Autowired
    UserDaoService userDaoService;

    /* Used For Only Manager and Account Registration */
    public void saveUser(UserEntity user) throws Exception{

        RoleEntity userRole = userDaoService.getRoleByRole(user.getRole().getRole());
        user.setRole(userRole);
        userDaoService.save(user);
    }

    public List<RoleEntity> findAllRoles() throws Exception{

        List<RoleEntity> roleList = new ArrayList<>();
        roleList = userDaoService.findAllRoles();

        return roleList;

    }

    @Override
    public Boolean checkUserExistence(HeaderDto headerDto) throws Exception {
        log.info("++++++++ Checking User Existence +++++++++++++++");

        UserEntity userEntity = userDaoService.findByUserName(headerDto.getUsername());
        if(userEntity != null){
            throw new YSException("VLD010");
        }
        return true;
    }

    @Override
    public String performPasswordAction(HeaderDto headerDto, PasswordActionType actionType) throws Exception {
        String successMsg = "";
        if (actionType.toString().equals(PasswordActionType.NEW.toString())) {
            log.info("+++++++++++ Creating Password +++++++++++");

            //Update Verification Status True
            updateVerificationStatus(headerDto.getVerificationCode());
            //Create Password
            resetForgotPassword(headerDto.getVerificationCode(), headerDto.getPassword());

            successMsg = "Your password has been created successfully";

        } else if (actionType.toString().equals(PasswordActionType.RESET.toString())) {
            log.info("+++++++++++ Resetting password +++++++++++");
            //Resetting Password
            resetForgotPassword(headerDto.getVerificationCode(), headerDto.getPassword());
            successMsg = "Your password has been reset successfully";

        } else if (actionType.toString().equals(PasswordActionType.FORGOT.toString())) {
            log.info("+++++++++++Performing Forgot password +++++++++++");

            String email = headerDto.getUsername();

            forgotPassword(email);
            successMsg = "Successfully sent email to " + email + " to reset the password.";
        } else if (actionType.toString().equals(PasswordActionType.RESEND.toString())) {
            log.info("++++++++++++ Resending password reset email +++++++++++");

            resendVerificationMail(headerDto.getUsername());
            successMsg = "Successfully resend email to " + headerDto.getUsername() + " to reset password.";
        }
        return successMsg;
    }

    @Override
    public void changePassword(HeaderDto headerDto) throws Exception {

        log.info("++++++ Changing User " +headerDto.getId() + " Password ++++++++");
        UserEntity user = userDaoService.find(Integer.parseInt(headerDto.getId()));
        GeneralUtil.matchDBPassword(headerDto.getPassword(), user.getPassword());
        if(headerDto.getPassword().equals(headerDto.getNewPassword())){
            throw new YSException("ERR018");
        }
        user.setPassword(GeneralUtil.encryptPassword(headerDto.getNewPassword()));
        userDaoService.update(user);

    }

    private void resetForgotPassword(String code, String password) throws Exception {

        UserEntity userEntity = userDaoService.findByVerificationCode(code);
        if (userEntity == null)
            throw new YSException("VLD011");

        userEntity.setPassword(GeneralUtil.encryptPassword(password.trim()));
        userEntity.setVerificationCode("");
        userDaoService.update(userEntity);

    }

    private void updateVerificationStatus(String code) throws Exception {

        UserEntity userEntity = userDaoService.findByVerificationCode(code);
        if (userEntity == null)
            throw new YSException("VLD011");

        //if(!userEntity.getRole().getRole().toString().equals(Role.ROLE_MERCHANT.toString())) {
            userEntity.setVerifiedStatus(true);
            if(userEntity.getStatus() != Status.ACTIVE){
                userEntity.setStatus(Status.VERIFIED);
            }
            userDaoService.update(userEntity);
        //}

    }

    private void resendVerificationMail(String userName) throws Exception {

        String verificationCode = MessageBundle.generateTokenString() + "_" + System.currentTimeMillis();

        UserEntity userEntity = userDaoService.findByUserName(userName);
        if (userEntity == null)
            throw new YSException("VLD011");

        if (userEntity.getVerifiedStatus())
            throw new YSException("RS702");

        userEntity.setVerificationCode(verificationCode);
        userDaoService.update(userEntity);

         /* Send Email */
        if (userEntity.getRole().getRole().toString().equals(Role.ROLE_MERCHANT.toString())) {
            String hostName = getServerUrl();
            String url = hostName + "/assistance/create_password/" + verificationCode;
            log.debug("Re-sending mail to " + userName + " with verify_url: " + url);
            String body = EmailMsg.createPasswordForNewUser(url, userEntity.getFullName(), userEntity.getUsername(), getServerUrl());
            String subject = "Delivr: You have been added as Merchant ";
            sendMail(userEntity.getUsername(), body, subject);
        }
    }

    private void forgotPassword(String userName) throws Exception {

        String verificationCode = MessageBundle.generateTokenString() + "_" + System.currentTimeMillis();
        //Checking User and Setting Verification Code
        UserEntity user = userDaoService.findByUserName(userName);
        if (user == null)
            throw new YSException("VLD011");

        user.setVerificationCode(verificationCode);
        userDaoService.update(user);

        String hostName = getServerUrl();
        String url = hostName + "/assistance/reset_password/" + verificationCode;
        log.info("Sending mail to " + userName + " with password reset url: " + url);

        String subject = "Dealify: Forgot your Password!";
        String body = EmailMsg.resetForgotPassword(url, user.getFullName(), getServerUrl());

        sendMail(userName, body, subject);

    }

    @Override
    public List<UserEntity> getUsers() {
        List<UserEntity> users = userDaoService.getUsers();
        return  users;

    }

    @Override
    public Boolean changeUserStatus(UserEntity userEntity) throws Exception {
        UserEntity user = userDaoService.find(userEntity.getId());
        if( user == null){
            throw new YSException("VLD011");
        }
        user.setStatus(userEntity.getStatus());
        if(user.getRole().getRole().equals(Role.ROLE_MERCHANT) && user.getStatus().equals(Status.INACTIVE)) {
             List<StoresBrandEntity> storesBrandEntities = user.getMerchant().getStoresBrand();
             for(StoresBrandEntity storesBrand: storesBrandEntities){
                 storesBrand.setStatus(Status.INACTIVE);
                 storesBrand.setFeatured(null);
                 storesBrand.setPriority(null);
             }
        }

        userDaoService.update(user);

        if(user.getRole().getRole().equals(Role.ROLE_MERCHANT) && user.getStatus().equals(Status.INACTIVE)) {
            //Sending Email For Merchant
            String url = getServerUrl();
            String body = EmailMsg.deactivateMerchant(user.getFullName(), getServerUrl());
            String subject = "Delivr: Your account has been deactivated ";
            sendMail(user.getUsername(), body, subject);
        } else if (user.getRole().getRole().equals(Role.ROLE_MERCHANT) && user.getStatus().equals(Status.ACTIVE)){
            //Sending Email For Merchant
            String url = getServerUrl();
            String body = EmailMsg.activateMerchant(url, user.getFullName(), getServerUrl());
            String subject = "Delivr: Your account has been activated ";
            sendMail(user.getUsername(), body, subject);
        }
        return true;
    }


}
