package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.dao.inf.UserDaoService;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.enums.PasswordActionType;
import com.yetistep.delivr.model.RoleEntity;
import com.yetistep.delivr.model.UserEntity;
import com.yetistep.delivr.service.inf.UserService;
import com.yetistep.delivr.util.GeneralUtil;
import com.yetistep.delivr.util.MessageBundle;
import com.yetistep.delivr.util.ServiceResponse;
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
public class UserServiceImpl implements UserService{

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
    public Boolean checkUserExistence(String username) throws Exception {
        log.info("++++++++ Checking User Existence +++++++++++++++");

        UserEntity userEntity = userDaoService.findByUserName(username);
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

        } else if(actionType.toString().equals(PasswordActionType.CHANGE.toString())){
            log.info("+++++++++++ Resetting password +++++++++++");
            //Resetting Password
            resetForgotPassword(headerDto.getVerificationCode(), headerDto.getPassword());
            successMsg = "Your password has been reset successfully";

        } else if (actionType.toString().equals(PasswordActionType.FORGOT.toString())) {
            log.info("+++++++++++Performing Forgot password +++++++++++");
            //TODO
        } else if (actionType.toString().equals(PasswordActionType.RESEND.toString())) {
            log.info("++++++++++++ Resending password reset email +++++++++++");
            //TODO

        }
        return successMsg;
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

        userEntity.setVerifiedStatus(true);
        userDaoService.update(userEntity);
    }


}
