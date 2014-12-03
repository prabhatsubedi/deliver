package com.yetistep.delivr.controller;

import com.yetistep.delivr.model.CountryEntity;
import com.yetistep.delivr.model.MerchantEntity;
import com.yetistep.delivr.service.inf.AdminService;
import com.yetistep.delivr.service.inf.MerchantService;
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

    /* Controller For All User */
    @RequestMapping(value = "/save_merchant", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ServiceResponse> processRegistration(@RequestHeader HttpHeaders headers, @RequestBody MerchantEntity merchant){

        try{

            merchantService.saveMerchant(merchant, headers);

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
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.BAD_REQUEST);
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
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.BAD_REQUEST);
        }
    }
}
