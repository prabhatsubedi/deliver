package com.yetistep.delivr.controller;

import com.yetistep.delivr.abs.AbstractManager;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.model.DeliveryBoyEntity;
import com.yetistep.delivr.model.OrderEntity;
import com.yetistep.delivr.model.Page;
import com.yetistep.delivr.model.mobile.dto.PastDeliveriesDto;
import com.yetistep.delivr.service.inf.DeliveryBoyService;
import com.yetistep.delivr.service.inf.UserService;
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
 * Time: 3:28 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/dboy")
public class DeliveryBoyController extends AbstractManager{
    @Autowired
    DeliveryBoyService deliveryBoyService;

    @Autowired
    UserService userService;

    Logger log = Logger.getLogger(DeliveryBoyController.class);

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> login(@RequestHeader HttpHeaders headers) {
        try{

            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.USERNAME, GeneralUtil.PASSWORD, GeneralUtil.ACCESS_TOKEN);

            validateMobileClient(headerDto.getAccessToken());

            DeliveryBoyEntity deliveryBoyEntity =  deliveryBoyService.dboyLogin(headerDto);

            ServiceResponse serviceResponse = new ServiceResponse("Delivery boy logged in successfully");
            serviceResponse.addParam("userDetail", deliveryBoyEntity);

            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred during login ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/update_location", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<ServiceResponse> updateDeliveryBoyLocation(@RequestHeader HttpHeaders headers, @RequestBody DeliveryBoyEntity deliveryBoy) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
            validateMobileClient(headerDto.getAccessToken());

            deliveryBoyService.updateLocationOfDeliveryBoy(deliveryBoy);
            ServiceResponse serviceResponse = new ServiceResponse("Location of delivery boy updated successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred during updating location of delivery boy", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/current_deliveries", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getCurrentDeliveries(@RequestHeader HttpHeaders headers) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID, GeneralUtil.ACCESS_TOKEN);
            validateMobileClient(headerDto.getAccessToken());

            List<OrderEntity> activeOrders = deliveryBoyService.getActiveOrders (Integer.parseInt(headerDto.getId()));
            ServiceResponse serviceResponse = new ServiceResponse("List of current deliveries retrieved successfully");
            serviceResponse.addParam("orders", activeOrders);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving list of current deliveries", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/past_deliveries", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getCurrentDeliveries(@RequestHeader HttpHeaders headers, @RequestBody(required = false) Page page) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID, GeneralUtil.ACCESS_TOKEN);
            validateMobileClient(headerDto.getAccessToken());

            List<PastDeliveriesDto> pastDeliveries = deliveryBoyService.getPastDeliveries(page, Integer.parseInt(headerDto.getId()));
            ServiceResponse serviceResponse = new ServiceResponse("List of past deliveries retrieved successfully");
            serviceResponse.addParam("pastDeliveries", pastDeliveries);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving list of past deliveries", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/update_job_status", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> updateJobOrderStatus(@RequestHeader HttpHeaders headers, @RequestBody OrderEntity order) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID/*, GeneralUtil.ACCESS_TOKEN*/);
            //validateMobileClient(headerDto.getAccessToken());

            deliveryBoyService.changeOrderStatus(order, Integer.parseInt(headerDto.getId()));
            ServiceResponse serviceResponse = new ServiceResponse("Job order status changed successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while updating job status", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/change_password")
    public ResponseEntity<ServiceResponse> changePassword(@RequestHeader HttpHeaders headers) throws Exception {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID, GeneralUtil.PASSWORD, GeneralUtil.NEW_PASSWORD, GeneralUtil.ACCESS_TOKEN);
            validateMobileClient(headerDto.getAccessToken());
            userService.changePassword(headerDto);

            ServiceResponse serviceResponse = new ServiceResponse("Password changed successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        } catch (Exception e) {
            GeneralUtil.logError(log, "Error occurred while changing password", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/update_dboy_status", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> updateDeliveryBoyStatus(@RequestHeader HttpHeaders headers, @RequestBody DeliveryBoyEntity deliveryBoyEntity) {
        try {
           /* HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
            validateMobileClient(headerDto.getAccessToken());*/
            deliveryBoyService.changeDeliveryBoyStatus(deliveryBoyEntity);

            ServiceResponse serviceResponse = new ServiceResponse("Delivery Boy status has been updated successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while updating delivery boy status", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }
}
