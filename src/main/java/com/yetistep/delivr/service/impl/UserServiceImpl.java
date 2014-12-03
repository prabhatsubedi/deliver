package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.dao.inf.UserDaoService;
import com.yetistep.delivr.model.RoleEntity;
import com.yetistep.delivr.model.UserEntity;
import com.yetistep.delivr.service.inf.UserService;
import com.yetistep.delivr.util.YSException;
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

    @Autowired
    UserDaoService userDaoService;

    /* Used For Only Manager and Account Registration */
    public void saveUser(UserEntity user) throws Exception{

        RoleEntity userRole = userDaoService.getRoleByRole(user.getRole().getRole());
        user.setRole(userRole);
        userDaoService.save(user);
    }


    public void saveRole(RoleEntity role) throws Exception{
        userDaoService.saveRole(role);
    }

    public List<RoleEntity> findAllRoles() throws Exception{

        List<RoleEntity> roleList = new ArrayList<>();
        roleList = userDaoService.findAllRoles();

        return roleList;

    }

    @Override
    public Boolean checkUserExistence(String username) throws Exception {
        UserEntity userEntity = userDaoService.findByUserName(username);
        if(userEntity != null){
            throw new YSException("VLD010");
        }
        return true;
    }


}
