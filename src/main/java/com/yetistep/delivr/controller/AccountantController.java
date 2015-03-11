package com.yetistep.delivr.controller;

import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.model.DeliveryBoyEntity;
import com.yetistep.delivr.service.inf.AccountService;
import com.yetistep.delivr.service.inf.ManagerService;
import com.yetistep.delivr.util.GeneralUtil;
import com.yetistep.delivr.util.ServiceResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 3:26 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "accountant")
public class AccountantController {

    private static final Logger log = Logger.getLogger(AccountantController.class);

    @Autowired
    ManagerService managerService;

    @Autowired
    AccountService accountService;

    @RequestMapping(value = "/update_dboy_account", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> updateDboyAccount(@RequestHeader HttpHeaders headers, @RequestBody RequestJsonDto requestJsonDto) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            DeliveryBoyEntity deliveryBoy = managerService.updateDboyAccount(headerDto, requestJsonDto);

            ServiceResponse serviceResponse = new ServiceResponse("Shopper account updated successfully with ID: "+headerDto.getId());
            serviceResponse.addParam("deliveryBoy", deliveryBoy);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while updating shopper account: ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/dboy_ack_payment", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> dBoyAckPayment(@RequestHeader HttpHeaders headers, @RequestBody RequestJsonDto requestJsonDto) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            DeliveryBoyEntity deliveryBoy = managerService.ackSubmittedAmount(headerDto, requestJsonDto);

            ServiceResponse serviceResponse = new ServiceResponse("Shopper payment acknowledged successfully with ID: "+headerDto.getId());
            serviceResponse.addParam("deliveryBoy", deliveryBoy);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred on the process of acknowledgement: ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }


    @RequestMapping(value = "/dboy_wallet_payment", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> dBoyWalletPayment(@RequestHeader HttpHeaders headers, @RequestBody RequestJsonDto requestJsonDto) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            DeliveryBoyEntity deliveryBoy = managerService.walletSubmittedAmount(headerDto, requestJsonDto);

            ServiceResponse serviceResponse = new ServiceResponse("Shopper payment submitted successfully with ID: "+headerDto.getId());
            serviceResponse.addParam("deliveryBoy", deliveryBoy);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred on the process of payment submission: ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/generate_invoice", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getGenerateInvoice(@RequestHeader HttpHeaders headers) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            String invoicePath = accountService.generateInvoice(Integer.parseInt(headerDto.getId()), "2015-02-18", "2015-03-06", "http://test.idelivr.com/");

            ServiceResponse serviceResponse = new ServiceResponse("Invoice has been generated successfully for store: "+headerDto.getId());
            serviceResponse.addParam("path", invoicePath);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while generating invoice: ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }


    @RequestMapping(value = "/send_bill_and_receipt", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> sendBillAndReceipt(@RequestHeader HttpHeaders headers) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            //String invoicePath = accountService.generateBillAndReceiptAndSendEmail(Integer.parseInt(headerDto.getId()));

            ServiceResponse serviceResponse = new ServiceResponse("Invoice has been generated successfully: "+headerDto.getId());
            //serviceResponse.addParam("path", invoicePath);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while generating invoice: ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/pay_dboy", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> payDboy(@RequestHeader HttpHeaders headers) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            accountService.payDboy(headerDto);

            ServiceResponse serviceResponse = new ServiceResponse("Invoice has been generated successfully: "+headerDto.getId());
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while generating invoice: ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }






}
