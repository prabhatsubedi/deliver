package com.yetistep.delivr.controller;

import com.yetistep.delivr.model.CustomerEntity;
import com.yetistep.delivr.model.DeliveryBoyEntity;
import com.yetistep.delivr.model.MerchantEntity;
import com.yetistep.delivr.model.UserEntity;
import com.yetistep.delivr.service.inf.CustomerService;
import com.yetistep.delivr.service.inf.DeliveryBoyService;
import com.yetistep.delivr.service.inf.MerchantService;
import com.yetistep.delivr.service.inf.UserService;
import com.yetistep.delivr.util.GeneralUtil;
import com.yetistep.delivr.util.MessageBundle;
import com.yetistep.delivr.util.ServiceResponse;
import com.yetistep.delivr.util.YSException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

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
    public ServiceResponse saveDeliveryBoy(@RequestBody final DeliveryBoyEntity deliveryBoy) {

        UserEntity user = deliveryBoy.getUser();
        try{

            String password = user.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(password);

            user.setPassword(hashedPassword);
            user.setMobileVerificationStatus(false);
            //user.setLastActivityDate(new Date());
            user.setBlacklistStatus(false);
            user.setVerifiedStatus(false);
            deliveryBoy.setUser(user);
            deliveryBoy.setAvailabilityStatus(true);
            deliveryBoy.setAverageRating(5);
            deliveryBoy.setTotalOrderTaken(0);
            deliveryBoy.setTotalOrderDelivered(0);
            deliveryBoy.setTotalOrderUndelivered(0);
            deliveryBoy.setTotalEarnings(0);
            deliveryBoy.setActiveOrderNo(0);
            deliveryBoy.setAvailableAmount(0);

            deliveryBoyService.saveDeliveryBoy(deliveryBoy);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ServiceResponse serviceResponse = new ServiceResponse("User(Delivery Boy) has been saved successfully");
        return serviceResponse;
    }

    @RequestMapping(value = "/save_accountant", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse processRegistration(@RequestBody final UserEntity user) {

        try{
            String password = user.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(password);

            user.setPassword(hashedPassword);

            user.setMobileVerificationStatus(false);
            //user.setLastActivityDate(new Date());
            user.setBlacklistStatus(false);
            user.setVerifiedStatus(false);

            userService.saveUser(user);
        }catch (Exception e) {
            e.printStackTrace();
        }

        ServiceResponse serviceResponse = new ServiceResponse("User has been saved successfully");
        return serviceResponse;
    }

    @RequestMapping(value = "/save_merchant", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ServiceResponse> processRegistration(@RequestHeader(value="username", required = false) String username, @RequestHeader(value="password", required = false) String password, @RequestBody MerchantEntity merchant){

        try{

            merchantService.saveMerchant(merchant, username, password);

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


    @RequestMapping(value = "/save_customer", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse processRegistration(@RequestBody final CustomerEntity customer) {
        UserEntity user = customer.getUser();
        try{

            String password = user.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(password);

            user.setPassword(hashedPassword);
            //user.setLastActivityDate(new Date());
            user.setBlacklistStatus(false);
            user.setVerifiedStatus(false);

            customer.setTotalOrderPlaced(0);
            customer.setTotalOrderDelivered(0);
            customer.setAverageRating(5);
            customer.setFriendsInvitationCount(0);
            customer.setReferredFriendsCount(0);
            customer.setRewardsEarned(BigDecimal.ZERO);




            customerService.saveCustomer(customer);


        } catch (Exception e) {
            e.printStackTrace();
        }

        ServiceResponse serviceResponse = new ServiceResponse("User has been saved successfully");
        return serviceResponse;
    }



    @RequestMapping(value = "/save_test_delivery_boy", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse saveTestDeliveryBoy() {
        DeliveryBoyEntity deliveryBoy = new DeliveryBoyEntity();
        UserEntity user = new UserEntity();
        try{

            String password = "dboypassword";
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(password);
            user.setUsername("sagar1 dboy");
            user.setPassword(hashedPassword);
            user.setFullName("Sagar Sapkota1");
            user.setStreet("balaju-16, Kathmandu1");
            user.setCity("city1");
            user.setState("Bagmati1");
            user.setCountry("Nepal1");
            user.setCountryCode("346656");
            user.setMobileNumber("9849540028456");
            user.setMobileVerificationStatus(true);
            user.setEmailAddress("sapktoasagardboy5@yahoo.com");
            user.setProfileImage("/user/profile-image1.jpg");
            user.setBlacklistStatus(false);
            user.setVerifiedStatus(true);
            user.setSubscribeNewsletter(true);

            deliveryBoy.setUser(user);
            deliveryBoy.setGender(1);
            deliveryBoy.setAvailabilityStatus(true);
            deliveryBoy.setAverageRating(5);
            deliveryBoy.setTotalOrderTaken(0);
            deliveryBoy.setTotalOrderDelivered(0);
            deliveryBoy.setTotalOrderUndelivered(0);
            deliveryBoy.setTotalEarnings(0);
            deliveryBoy.setVehicleType(1);
            deliveryBoy.setActiveOrderNo(0);
            deliveryBoy.setAvailableAmount(0);
            deliveryBoy.setLatitude("477879845");
            deliveryBoy.setLongitude("34578645345");


            deliveryBoyService.saveDeliveryBoy(deliveryBoy);
        }catch (Exception e) {
            //
        }

        ServiceResponse serviceResponse = new ServiceResponse("User(Delivery Boy) has been saved successfully");
        return serviceResponse;
    }

}
