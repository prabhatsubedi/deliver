package com.yetistep.delivr.controller;

import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.enums.PasswordActionType;
import com.yetistep.delivr.model.CountryEntity;
import com.yetistep.delivr.model.MerchantEntity;
import com.yetistep.delivr.service.inf.AdminService;
import com.yetistep.delivr.service.inf.MerchantService;
import com.yetistep.delivr.service.inf.UserService;
import com.yetistep.delivr.util.GeneralUtil;
import com.yetistep.delivr.util.ServiceResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/2/14
 * Time: 2:09 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/anon")
public class AnonController {
    private static final Logger log = Logger.getLogger(AnonController.class);

    @Autowired
    MerchantService merchantService;

    @Autowired
    AdminService adminService;

    @Autowired
    UserService userService;

    /* Controller For All User */
    @RequestMapping(value = "/save_merchant", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> processRegistration(@RequestHeader HttpHeaders headers, @RequestBody MerchantEntity merchant){
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.USERNAME);
            merchantService.saveMerchant(merchant, headerDto);

             /* Response Success */
            // JSON Information
            ServiceResponse serviceResponse = new ServiceResponse("Merchant has been saved successfully");
            // Header Parameter Response
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("merchantId", merchant.getId().toString());
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<ServiceResponse>(serviceResponse, httpHeaders, HttpStatus.OK);

        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while creating merchant", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }

    }

    @RequestMapping(value = "/country_list", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getCountryList() {
        try {
            List<CountryEntity> countryEntityList =  adminService.findAllCountries();
            ServiceResponse serviceResponse = new ServiceResponse("List of countries");
            serviceResponse.addParam("countryList", countryEntityList);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while retrieving list of countries", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/check_user", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> validateUser(@RequestHeader HttpHeaders headers) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.USERNAME);
            userService.checkUserExistence(headerDto);

            ServiceResponse serviceResponse = new ServiceResponse("User name available");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "User name already exists", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/password_assist", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> changePassword(@RequestHeader HttpHeaders headers, @RequestBody RequestJsonDto actionType) {
        try{
            HeaderDto headerDto = new HeaderDto();
            if (actionType.getActionType().toString().equals(PasswordActionType.NEW.toString()) ||
                    actionType.getActionType().toString().equals(PasswordActionType.RESET.toString())) {
                GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.PASSWORD, GeneralUtil.VERIFICATION_CODE);
            }else {
                GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.USERNAME);
            }
            String msg = userService.performPasswordAction(headerDto, actionType.getActionType());

            ServiceResponse serviceResponse = new ServiceResponse(msg);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error occurred while assisting password", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }
    @RequestMapping(value = "/change_password")
    public ResponseEntity<ServiceResponse> changePassword(@RequestHeader HttpHeaders httpHeaders) throws Exception {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(httpHeaders, headerDto, GeneralUtil.ID, GeneralUtil.PASSWORD, GeneralUtil.NEW_PASSWORD);
            userService.changePassword(headerDto);

            ServiceResponse serviceResponse = new ServiceResponse("User password changed successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        } catch (Exception e) {
            GeneralUtil.logError(log, "Error occurred while changing password", e);
            HttpHeaders headers = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(headers, HttpStatus.EXPECTATION_FAILED);
        }
    }

}
