package com.yetistep.delivr.service.inf;

import com.yetistep.delivr.model.RoleEntity;
import com.yetistep.delivr.model.UserEntity;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/6/14
 * Time: 2:44 PM
 * To change this template use File | Settings | File Templates.
 */
public interface UserService {

    public void saveUser(UserEntity user) throws Exception;
    public void saveRole(RoleEntity role) throws Exception;
    public List<RoleEntity> findAllRoles() throws Exception;

}
