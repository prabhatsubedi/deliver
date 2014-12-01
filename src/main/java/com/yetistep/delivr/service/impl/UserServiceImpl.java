package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.dao.inf.UserDaoService;
import com.yetistep.delivr.model.RoleEntity;
import com.yetistep.delivr.model.UserEntity;
import com.yetistep.delivr.service.inf.UserService;
import com.yetistep.delivr.util.YSException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
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


    public void saveRole(RoleEntity role) throws Exception{
            userDaoService.saveRole(role);
    }

    public List<RoleEntity> findAllRoles() throws Exception{

        List<RoleEntity> roleList = new ArrayList<>();
        roleList = userDaoService.findAllRoles();

        return roleList;

    }


}
