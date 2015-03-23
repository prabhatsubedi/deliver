package com.yetistep.delivr.controller;

import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.PaginationDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.enums.Role;
import com.yetistep.delivr.model.PreferencesEntity;
import com.yetistep.delivr.model.RoleEntity;
import com.yetistep.delivr.model.UserEntity;
import com.yetistep.delivr.service.inf.*;
import com.yetistep.delivr.util.GeneralUtil;
import com.yetistep.delivr.util.ServiceResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/19/14
 * Time: 11:54 AM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/admin")
public class AdminController {
    @Autowired
    UserService userService;
    @Autowired
    MerchantService merchantService;
    @Autowired
    CustomerService customerService;
    @Autowired
    SystemPropertyService systemPropertyService;
    @Autowired
    ManagerService managerService;
    @Autowired
    AdminService adminService;

    private static final Logger log = Logger.getLogger(AdminController.class);

    /*
    * save manager or accountant
    * */
    @RequestMapping(value = "/save_user", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> saveUser(@RequestHeader HttpHeaders headers, @RequestBody UserEntity user) {

        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.USERNAME);
            managerService.saveManagerOrAccountant(user, headerDto);

            ServiceResponse serviceResponse = new ServiceResponse("User has been saved successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while creating user", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/update_user", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> updateDeliveryBoy(@RequestHeader HttpHeaders headers, @RequestBody UserEntity user) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);

            managerService.updateManagerOrAccountant(user, headerDto);

            ServiceResponse serviceResponse = new ServiceResponse("User has been updated successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while updating user", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_user", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getUser(@RequestHeader HttpHeaders headers) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            UserEntity user = managerService.findUserById(headerDto);

            ServiceResponse serviceResponse = new ServiceResponse("Detail of user with ID: "+headerDto.getId());
            serviceResponse.addParam("user", user);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while retrieving shopper: ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_managers", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getManagers(@RequestBody RequestJsonDto requestJsonDto) {
        try {
            PaginationDto managers = managerService.findAllManagers(requestJsonDto);

            ServiceResponse serviceResponse = new ServiceResponse("List of managers");
            serviceResponse.addParam("managers", managers);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while retrieving managers", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_accountants", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getAccountants(@RequestBody RequestJsonDto requestJsonDto) {
        try {
            PaginationDto allAccountants = managerService.findAllAccountants(requestJsonDto);

            ServiceResponse serviceResponse = new ServiceResponse("List of accountants");
            serviceResponse.addParam("accountants", allAccountants);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while retrieving accountants", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_users", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse get_users() {
        List<UserEntity> users = new ArrayList<>();

        try {
          users =  userService.getUsers();
        } catch (Exception e){
            e.printStackTrace();
        }
        ServiceResponse serviceResponse = new ServiceResponse("user list");
        serviceResponse.addParam("userList", users);
        return serviceResponse;
    }



    //@unused
    @RequestMapping(value = "/save_role", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse processRegistration() {

        try {
            if (userService.findAllRoles().size() == 0) {
                for (int i = 1; i < (Role.values().length + 1); i++) {
                    Role role = Role.fromInt(i);
                    RoleEntity userRole = new RoleEntity();
                    userRole.setRole(role);
                    //userService.saveRole(userRole);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ServiceResponse serviceResponse = new ServiceResponse("Role has been saved successfully");
        return serviceResponse;
    }

    @RequestMapping(value="/get_preferences", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getPreferences(){
        try{
            List<PreferencesEntity> preferencesEntities = systemPropertyService.getAllPreferences();

            ServiceResponse serviceResponse = new ServiceResponse("Preferences retrieved successfully");
            serviceResponse.addParam("preferences", preferencesEntities);

            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        }catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while getting system preferences", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);

        }
    }

    @RequestMapping(value="/update_preferences", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> updatePreferences(@RequestBody RequestJsonDto requestJsonDto){
        try{
            systemPropertyService.updateSystemPreferences(requestJsonDto.getPreferences());

            ServiceResponse serviceResponse = new ServiceResponse("Preferences updated successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        }catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while updating system preferences", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);

        }
    }


}




