package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.enums.Role;
import com.yetistep.delivr.model.Page;
import com.yetistep.delivr.model.RoleEntity;
import com.yetistep.delivr.model.UserEntity;

import java.util.List;

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

    public List<UserEntity> findManagers(Page page) throws Exception;

    public List<UserEntity> findAccountants(Page page) throws Exception;

    //Spring Authentication Only
    public UserEntity findByUserName(String userName);

    public UserEntity findByVerificationCode(String code) throws Exception;

    public List<UserEntity> getUsers();

    public UserEntity find(String userName, String password) throws Exception;

    public Boolean updateDeliveryContact(Integer userId, String mobileNo, String verificationCode) throws Exception;

    public Boolean checkIfMobileNumberExists(String mobileNumber) throws Exception;

    public Boolean checkIfEmailExists(String emailAddress, Integer roleId) throws Exception;

    public Boolean checkIfEmailExists(String emailAddress) throws Exception;

    public Boolean deactivateUser(Integer userId) throws Exception;

    public Boolean activateUser(Integer userId) throws Exception;

    public List<UserEntity> getInactivatedCustomers(Page page) throws Exception;

    public Integer getTotalNumberInactiveCustomers() throws Exception;

    public Integer getTotalNumberManagers()throws Exception;

    public Integer getTotalNumberAccountants()throws Exception;

    public Boolean checkIfMobileNumberExists(String mobileNumber, Integer userId) throws Exception;

    public Boolean checkIfEmailExists(String emailAddress, Integer roleId, Integer userId) throws Exception;

    public UserEntity getUserByMobileNumber(String mobileNumber, Integer roleId) throws Exception;

}
