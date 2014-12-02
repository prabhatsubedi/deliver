package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.enums.Role;
import com.yetistep.delivr.model.RoleEntity;
import com.yetistep.delivr.model.UserEntity;

import java.sql.SQLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/6/14
 * Time: 2:42 PM
 * To change this template use File | Settings | File Templates.
 */
public interface UserDaoService {
    public void saveRole(RoleEntity role) throws SQLException;

    public RoleEntity getRoleByRole(Role role) throws SQLException;

    public List<RoleEntity> findAllRoles() throws SQLException;

    //Spring Authentication Only
    public UserEntity findByUserName(String userName);

    public void save(UserEntity user) throws SQLException;

}
