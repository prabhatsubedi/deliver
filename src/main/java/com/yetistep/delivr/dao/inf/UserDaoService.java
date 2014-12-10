package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.enums.Role;
import com.yetistep.delivr.model.RoleEntity;
import com.yetistep.delivr.model.UserEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/6/14
 * Time: 2:42 PM
 * To change this template use File | Settings | File Templates.
 */
public interface UserDaoService extends GenericDaoService<Integer, UserEntity>{

    public RoleEntity getRoleByRole(Role role) throws Exception;

    public List<RoleEntity> findAllRoles() throws Exception;

    //Spring Authentication Only
    public UserEntity findByUserName(String userName);

    public UserEntity findByVerificationCode(String code) throws Exception;

    public List<UserEntity> getUsers();

    public UserEntity find(String userName, String password) throws Exception;
}
