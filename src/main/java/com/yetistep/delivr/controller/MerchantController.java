package com.yetistep.delivr.controller;

import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.model.CategoryEntity;
import com.yetistep.delivr.model.MerchantEntity;
import com.yetistep.delivr.model.StoresBrandEntity;
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
    public ResponseEntity<ServiceResponse> saveStore(@RequestHeader HttpHeaders headers, @RequestBody RequestJsonDto requestJson) {
        try {

            merchantService.saveStore(requestJson, headers);

            ServiceResponse serviceResponse = new ServiceResponse("Store(Stores) has been saved successfully");

            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.CREATED);

        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while adding store", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/get_parent_categories", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponse> getParentCategories(){
        try {

            List<CategoryEntity> categories =  merchantService.getParentCategories();

            ServiceResponse serviceResponse = new ServiceResponse("Categories retrieved successfully");
            serviceResponse.addParam("categories", categories);
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
            GeneralUtil.logError(log, "Error Occurred while updating merchant", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_all_stores", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> findStores(@RequestHeader HttpHeaders headers) {
        try {
            List<StoresBrandEntity> stores = merchantService.findStores(headers);
            ServiceResponse serviceResponse = new ServiceResponse("Stores has been retrieved successfully");
            serviceResponse.addParam("stores", stores);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while fetching stores", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }


}
