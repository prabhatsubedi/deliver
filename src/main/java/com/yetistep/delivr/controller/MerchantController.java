package com.yetistep.delivr.controller;

import com.yetistep.delivr.model.MerchantEntity;
import com.yetistep.delivr.model.StoreEntity;
import com.yetistep.delivr.service.inf.MerchantService;
import com.yetistep.delivr.util.GeneralUtil;
import com.yetistep.delivr.util.ServiceResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 3:23 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/merchant")
public class MerchantController {
    @Autowired
    MerchantService merchantService;

    private static final Logger log = Logger.getLogger(ManagerController.class);

    @RequestMapping(value = "/save_store", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> saveStore(@RequestHeader HttpHeaders headers, @RequestBody List<StoreEntity> stores) {
        try {
            merchantService.saveStore(stores, headers);

            ServiceResponse serviceResponse = new ServiceResponse("Store(Stores) has been saved successfully");

            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.CREATED);

        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while creating delivery boy", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/update_merchant", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<ServiceResponse> updateMerchant(@RequestBody MerchantEntity merchantEntity) {
        try {
            merchantService.updateMerchant(merchantEntity);
            ServiceResponse serviceResponse = new ServiceResponse("Merchant has been saved successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while creating delivery boy", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

}
