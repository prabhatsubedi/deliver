package com.yetistep.delivr.controller;

import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.PaginationDto;
import com.yetistep.delivr.model.*;
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

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Surendra
 * Date: 11/21/14
 * Time: 3:21 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/organizer")
public class ManagerController {

    @Autowired
    UserService userService;
    @Autowired
    DeliveryBoyService deliveryBoyService;
    @Autowired
    MerchantService merchantService;
    @Autowired
    CustomerService customerService;
    @Autowired
    ManagerService managerService;

    private static final Logger log = Logger.getLogger(ManagerController.class);

    @RequestMapping(value = "/save_dboy", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> saveDeliveryBoy(@RequestHeader HttpHeaders headers, @RequestBody DeliveryBoyEntity deliveryBoy) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.USERNAME, GeneralUtil.PASSWORD);
            deliveryBoyService.saveDeliveryBoy(deliveryBoy, headerDto);

            ServiceResponse serviceResponse = new ServiceResponse("Delivery Boy has been saved successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while creating delivery boy", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_dboy", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getDeliveryBoy(@RequestHeader HttpHeaders headers) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            DeliveryBoyEntity deliveryBoy = deliveryBoyService.findDeliveryBoyById(headerDto);

            ServiceResponse serviceResponse = new ServiceResponse("Details of delivery boy with ID: "+headerDto.getId());
            serviceResponse.addParam("deliveryBoy", deliveryBoy);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while retrieving delivery boy: ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_dboys", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getAllDeliveryBoy() {
        try {
            List<DeliveryBoyEntity> deliveryBoyEntities = deliveryBoyService.findAllDeliverBoy();

            ServiceResponse serviceResponse = new ServiceResponse("List of delivery boys");
            serviceResponse.addParam("deliveryBoys", deliveryBoyEntities);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while retrieving delivery boys", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/update_dboy_status", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<ServiceResponse> updateDeliveryBoyStatus(@RequestBody DeliveryBoyEntity deliveryBoyEntity) {
        try {
            deliveryBoyService.updateDeliveryBoyStatus(deliveryBoyEntity);

            ServiceResponse serviceResponse = new ServiceResponse("Delivery Boy status has been updated successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while updating delivery boy status", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/update_dboy", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<ServiceResponse> updateDeliveryBoy(@RequestHeader HttpHeaders headers, @RequestBody DeliveryBoyEntity deliveryBoy) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.USERNAME);

            List<String> hd = headers.get("passowrd");
            if (hd != null && hd.size() > 0)
                headerDto.setPassword(hd.get(0));
            else
                headerDto.setPassword(null);

            deliveryBoyService.updateDeliveryBoy(deliveryBoy, headerDto);

            ServiceResponse serviceResponse = new ServiceResponse("Delivery Boy has been updated successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while updating delivery boy", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/activate_merchant", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> activateMerchant(@RequestBody MerchantEntity merchantEntity) {
        try{
            merchantService.activateMerchant(merchantEntity);

            ServiceResponse serviceResponse = new ServiceResponse("Merchant activated successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while activating merchant", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }

    }

    @RequestMapping(value = "/get_merchants", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getMerchants() {
        try{
            List<MerchantEntity> merchantEntities = merchantService.getMerchants();

            ServiceResponse serviceResponse = new ServiceResponse("Merchant retrieved successfully");
            serviceResponse.addParam("merchants", merchantEntities);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while getting merchants", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }

    }

    @RequestMapping(value = "/change_user_status", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<ServiceResponse> updateUserStatus(@RequestBody UserEntity userEntity) {
        try{
            userService.changeUserStatus(userEntity);

            ServiceResponse serviceResponse = new ServiceResponse("User status updated successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while updating user status", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }

    }

    @RequestMapping(value = "/get_merchant", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getMerchantById(@RequestHeader HttpHeaders headers) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.MERCHANT_ID);
            MerchantEntity merchantEntity = merchantService.getMerchantById(headerDto);

            ServiceResponse serviceResponse = new ServiceResponse("Merchant retrieved successfully with ID: "+headerDto.getMerchantId());
            serviceResponse.addParam("merchant", merchantEntity);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving merchant: ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }

    }

    @RequestMapping(value = "/get_logs", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getActionLogs(@RequestBody(required = false) Page page) {
        try{
            PaginationDto paginationDto = managerService.getActionLog(page);

            ServiceResponse serviceResponse = new ServiceResponse("List of action log");
            serviceResponse.addParam("actionLog", paginationDto);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving action logs: ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_special_brands", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getFeaturedAndPrioritizedStoreBrands() {
        try{
            List<StoresBrandEntity> storeBrands = managerService.findFeaturedAndPrioritizedStoreBrands();
            ServiceResponse serviceResponse = new ServiceResponse("List of featured and prioritized store brands");
            serviceResponse.addParam("storeBrands", storeBrands);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving list of featured and prioritized store brands", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_other_brands", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getNonFeaturedAndPrioritizedStoreBrands(@RequestBody(required = false) Page page) {
        try{
            PaginationDto paginationDto = managerService.findNonFeaturedAndPrioritizedStoreBrands(page);

            ServiceResponse serviceResponse = new ServiceResponse("List of non featured & prioritized log");
            serviceResponse.addParam("storeBrands", paginationDto);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving list of non featured and prioritized store brands:", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/update_special_brands", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> updateFeatureAndPriorityOfStoreBrands(@RequestBody List<StoresBrandEntity> storeBrands) {
        try{
            managerService.updateFeatureAndPriorityOfStoreBrands(storeBrands);
            ServiceResponse serviceResponse = new ServiceResponse("Feature & priority of stores brands updated successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while updating feature and priority of store brands:", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

}
