package com.yetistep.delivr.controller;

import com.yetistep.delivr.abs.AbstractManager;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.model.DeliveryBoyEntity;
import com.yetistep.delivr.service.inf.DeliveryBoyService;
import com.yetistep.delivr.util.GeneralUtil;
import com.yetistep.delivr.util.ServiceResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = "/accept_order", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> acceptOrderFromDeliveryBoy(@RequestHeader HttpHeaders headers, @RequestBody RequestJsonDto requestJsonDto) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID/*, GeneralUtil.ACCESS_TOKEN*/);
            //validateMobileClient(headerDto.getAccessToken());

            deliveryBoyService.acceptOrder(Integer.parseInt(headerDto.getId()), requestJsonDto.getOrderId());
            ServiceResponse serviceResponse = new ServiceResponse("Order has been accepted successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while accepting order", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }
}
