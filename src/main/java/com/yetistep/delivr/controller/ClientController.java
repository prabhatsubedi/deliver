package com.yetistep.delivr.controller;

import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.model.CustomerEntity;
import com.yetistep.delivr.model.UserEntity;
import com.yetistep.delivr.service.inf.CustomerService;
import com.yetistep.delivr.util.*;
import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/2/14
 * Time: 2:08 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/client")
public class ClientController {
    @Autowired
    HttpServletRequest httpServletRequest;

    /* Controller for Mobile API */
    private static final Logger log = Logger.getLogger(ClientController.class);

    @Autowired
    CustomerService customerService;

    @RequestMapping(value = "/save_customer", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> processRegistration(@RequestHeader HttpHeaders headers, @RequestBody CustomerEntity customer) {
        UserEntity user = customer.getUser();
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto);
            customer.getUser().setUsername(headerDto.getUsername());
            customer.getUser().setPassword(headerDto.getPassword());
            customerService.saveCustomer(customer);

            ServiceResponse serviceResponse = new ServiceResponse("Customer has been created successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while creating customer", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/access_token", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<ServiceResponse> getAccessToken() {
        try {
            log.info("+++++++++++ Getting Access Token ++++++++++++++++++");
            UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();
            ReadableUserAgent agent = parser.parse(httpServletRequest.getHeader("User-Agent"));

            String family = agent.getOperatingSystem().getFamily().toString();
            String token = GeneralUtil.generateAccessToken(family);

            ServiceResponse serviceResponse = new ServiceResponse("Token retrieved successfully");

            /* Setting Http Headers */
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("accessToken", token);
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            return new ResponseEntity<ServiceResponse>(serviceResponse, httpHeaders, HttpStatus.OK);

        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while creating customer", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }

    }


}
