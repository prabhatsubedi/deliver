package com.yetistep.delivr.controller;

import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.model.DeliveryBoyEntity;
import com.yetistep.delivr.model.MerchantEntity;
import com.yetistep.delivr.service.inf.CustomerService;
import com.yetistep.delivr.service.inf.DeliveryBoyService;
import com.yetistep.delivr.service.inf.MerchantService;
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

    private static final Logger log = Logger.getLogger(ManagerController.class);

    @RequestMapping(value = "/save_delivery_boy", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> saveDeliveryBoy(@RequestHeader HttpHeaders headers, @RequestBody DeliveryBoyEntity deliveryBoy) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto);
            deliveryBoy.getUser().setUsername(headerDto.getUsername());
            deliveryBoy.getUser().setPassword(headerDto.getPassword());
            deliveryBoyService.saveDeliveryBoy(deliveryBoy);

            ServiceResponse serviceResponse = new ServiceResponse("User(Delivery Boy) has been saved successfully");
//            HttpHeaders httpHeaders = new HttpHeaders();
//            httpHeaders.add("deliveryBoyId", deliveryBoy.getUser().getId() + "");
//            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.CREATED);

        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while creating delivery boy", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/get_delivery_boy/{dbId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getDeliveryBoy(@PathVariable("dbId") Integer deliveryBoyId) {
        try {
            DeliveryBoyEntity deliveryBoy = deliveryBoyService.findDeliveryBoyById(deliveryBoyId);
            deliveryBoy.getUser().setActionLogEntities(null);
            ServiceResponse serviceResponse = new ServiceResponse("Details of delivery boy with ID: "+deliveryBoyId);
            serviceResponse.addParam("deliveryBoy", deliveryBoy);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while retrieving delivery boy with ID: "+deliveryBoyId, e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/get_all_delivery_boy", method = RequestMethod.GET)
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
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.BAD_REQUEST);
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
            GeneralUtil.logError(log, "Error Occurred while creating delivery boy", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }

    }

}
