package com.yetistep.delivr.controller;

import com.yetistep.delivr.abs.AbstractManager;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.model.AddressEntity;
import com.yetistep.delivr.model.CustomerEntity;
import com.yetistep.delivr.model.StoresBrandEntity;
import com.yetistep.delivr.model.UserEntity;
import com.yetistep.delivr.service.inf.ClientService;
import com.yetistep.delivr.service.inf.CustomerService;
import com.yetistep.delivr.util.GeneralUtil;
import com.yetistep.delivr.util.ServiceResponse;
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
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/2/14
 * Time: 2:08 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/client")
public class ClientController extends AbstractManager{
    @Autowired
    HttpServletRequest httpServletRequest;

    /* Controller for Mobile API */
    private static final Logger log = Logger.getLogger(ClientController.class);

    @Autowired
    CustomerService customerService;

    @Autowired
    ClientService clientService;

    @RequestMapping(value = "/save_customer", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> processRegistration(@RequestHeader HttpHeaders headers, @RequestBody CustomerEntity customer) {
        UserEntity user = customer.getUser();
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.USERNAME, GeneralUtil.PASSWORD/*, GeneralUtil.ACCESS_TOKEN*/);
            //validateMobileClient(headerDto.getAccessToken());
            customerService.saveCustomer(customer, headerDto);

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

    @RequestMapping(value = "/add_address", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> addAddresses(@RequestHeader HttpHeaders headers, @RequestBody List<AddressEntity> addresses) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID, GeneralUtil.ACCESS_TOKEN);
            validateMobileClient(headerDto.getAccessToken());
            customerService.addCustomerAddress(headerDto, addresses);

            ServiceResponse serviceResponse = new ServiceResponse("Customer address has been added successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while adding customer address", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/set_mobile_code", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<ServiceResponse> setMobileCode(@RequestHeader HttpHeaders headers) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.USERNAME, GeneralUtil.ACCESS_TOKEN, GeneralUtil.VERIFICATION_CODE);
            validateMobileClient(headerDto.getAccessToken());
            customerService.setMobileCode(headerDto);

            ServiceResponse serviceResponse = new ServiceResponse("Customer has been verified");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while verifying mobile code", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/save_order", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> saveItem(@RequestHeader HttpHeaders headers, @RequestBody RequestJsonDto requestJson) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            customerService.saveOrder(requestJson, headerDto);
            ServiceResponse serviceResponse = new ServiceResponse("Order has been saved successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while fetching stores", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/store_brands", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getBrands(@RequestBody (required = false) RequestJsonDto requestJsonDto){
        try{
//            select * from stores_brands where featured is not null order by priority asc;
//            select * from stores_brands order by isnull(featured),featured asc;
            Map<String, Object> map = clientService.getBrands(requestJsonDto);
            ServiceResponse serviceResponse = new ServiceResponse("Brands fetched successfully");
                serviceResponse.addParam("featuredBrands", map.get("featured"));
                serviceResponse.addParam("otherBrands", map.get("all"));
                serviceResponse.addParam("pageInfo", map.get("page"));

            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch(Exception e) {
            GeneralUtil.logError(log, "Error Occurred while fetching stores", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

}
