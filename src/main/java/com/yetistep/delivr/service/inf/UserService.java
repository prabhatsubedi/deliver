package com.yetistep.delivr.service.inf;

import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.enums.PasswordActionType;
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

    public List<RoleEntity> findAllRoles() throws Exception;

    public Boolean checkUserExistence(HeaderDto headerDto) throws Exception;

    public String performPasswordAction(HeaderDto headerDto, PasswordActionType passwordActionType) throws Exception;

    public List<UserEntity> getUsers();

    public void changePassword(HeaderDto headerDto) throws Exception;

    public Boolean changeUserStatus(UserEntity userEntity) throws Exception;

}
