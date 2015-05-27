package com.yetistep.delivr.controller;

import com.yetistep.delivr.abs.AbstractManager;
import com.yetistep.delivr.dto.*;
import com.yetistep.delivr.enums.RatingReason;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.model.mobile.AddressDto;
import com.yetistep.delivr.model.mobile.CategoryDto;
import com.yetistep.delivr.model.mobile.DeviceInfo;
import com.yetistep.delivr.model.mobile.dto.*;
import com.yetistep.delivr.service.inf.ClientService;
import com.yetistep.delivr.service.inf.CustomerService;
import com.yetistep.delivr.util.*;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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

//    @RequestMapping(value = "/save_customer", method = RequestMethod.POST)
//    @ResponseBody
//    public ResponseEntity<ServiceResponse> processRegistration(@RequestHeader HttpHeaders headers, @RequestBody CustomerEntity customer) {
//        UserEntity user = customer.getUser();
//        try {
//            HeaderDto headerDto = new HeaderDto();
//            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.USERNAME, GeneralUtil.PASSWORD/*, GeneralUtil.ACCESS_TOKEN*/);
//            //validateMobileClient(headerDto.getAccessToken());
//            customerService.saveCustomer(customer, headerDto);
//
//            ServiceResponse serviceResponse = new ServiceResponse("Customer has been created successfully");
//            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.CREATED);
//        } catch (Exception e) {
//            GeneralUtil.logError(log, "Error Occurred while creating customer", e);
//            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
//            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
//        }
//    }

    @RequestMapping(value = "/access_token", method = RequestMethod.GET)
    public
    @ResponseBody
    ResponseEntity<ServiceResponse> getAccessToken() {
        try {
            log.info("+++++++++++ Getting Access Token ++++++++++++++++++");
//            UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();
//            ReadableUserAgent agent = parser.parse(httpServletRequest.getHeader("User-Agent"));
//            String family = agent.getOperatingSystem().getFamily().toString();
//            String token = GeneralUtil.generateAccessToken(family);
            String userAgent = httpServletRequest.getHeader("User-Agent");
            UserAgent ua = UserAgent.parseUserAgentString(userAgent);

            String family = ua.getOperatingSystem().name();
            String token = GeneralUtil.generateAccessToken(family);


            ServiceResponse serviceResponse = new ServiceResponse("Token retrieved successfully");
            /* Setting Http Headers */
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("accessToken", token);
            //FIXME:
            log.info("++++ During Access Token " + String.valueOf(System.currentTimeMillis()));
            httpHeaders.add("time", String.valueOf(System.currentTimeMillis()));
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);

            return new ResponseEntity<ServiceResponse>(serviceResponse, httpHeaders, HttpStatus.OK);

        } catch (Exception e) {
            GeneralUtil.logError(log, "Error occurred while getting access token", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }

    }

    @RequestMapping(value = "/add_address", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> addAddresses(@RequestHeader HttpHeaders headers, @RequestBody AddressDto address) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
            validateMobileClient(headerDto.getAccessToken());
            Integer addressId  = customerService.addCustomerAddress(address);

            ServiceResponse serviceResponse = new ServiceResponse("Address has been added successfully");
            serviceResponse.addParam("addressId", addressId);

            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while adding customer address", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }


    @RequestMapping(value = "/get_delivered_address/fbId/{facebookId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getDeliveredAddress(@PathVariable("facebookId") Long facebookId) {
        try {

            UserEntity user  = customerService.getDeliveredAddress(facebookId);

            ServiceResponse serviceResponse = new ServiceResponse("Delivered address retrieved successfully");
            serviceResponse.addParam("user", user);

            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while getting customer address", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/delete_delivered_address/addId/{id}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> deleteDeliveredAddress(@RequestHeader HttpHeaders headers,@PathVariable("id") Integer addressId) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
            validateMobileClient(headerDto.getAccessToken());

            customerService.deleteDeliveredAddress(addressId);

            ServiceResponse serviceResponse = new ServiceResponse("Address deleted successfully.");

            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while deleting delivered address", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    //this function is used only when the sms is enabled
    @RequestMapping(value = "/reg_mobile_code", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> verifyMobileCode(@RequestHeader HttpHeaders headers, @RequestBody UserEntity user) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
            validateMobileClient(headerDto.getAccessToken());

            AddressDto address = customerService.verifyMobile(user.getMobileNumber(), user.getCustomer().getFacebookId());

            ServiceResponse serviceResponse = new ServiceResponse("Mobile has been registered");
            serviceResponse.addParam("verifyDetail", address);
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
            /*GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
            validateMobileClient(headerDto.getAccessToken());*/
            customerService.saveOrder(requestJson, headerDto);
            ServiceResponse serviceResponse = new ServiceResponse("Order has been saved successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while fetching stores", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/store_brands", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getBrands(@RequestBody(required = false) RequestJsonDto requestJsonDto) {
        try {
            Map<String, Object> map = clientService.getBrands(requestJsonDto);
            ServiceResponse serviceResponse = new ServiceResponse("Brands fetched successfully");
            if(requestJsonDto.getPageInfo()== null) {
                DefaultInfoDto defaultInfoDto = (DefaultInfoDto) map.get("defaultInfo");
                serviceResponse.addParam("currency", defaultInfoDto.getCurrency());
                serviceResponse.addParam("featuredBrands", map.get("featured"));
                serviceResponse.addParam("pageInfo", map.get("page"));
                serviceResponse.addParam("defaultInfo", map.get("defaultInfo"));
            }
            serviceResponse.addParam("otherBrands", map.get("all"));


            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while fetching stores", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value="/login", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> customerLogin(@RequestHeader HttpHeaders headers, @RequestBody CustomerEntity customerEntity) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
            validateMobileClient(headerDto.getAccessToken());

            customerService.login(customerEntity);

            //FIXME:
//            List<String> hd = headers.get("time");
//            if (hd != null && hd.size() > 0)
//                log.info("++++ Time : " + hd.get(0));

            //log.info("++++ Time : " + headers.get("time"));

            ServiceResponse serviceResponse = new ServiceResponse("Customer Login Successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred customer login", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/parent_category/brand/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getParentCategory(@PathVariable("id") Integer brandId) {
        try{
            List<CategoryDto> categoryDtoList = clientService.getParentCategory(brandId);
            ServiceResponse serviceResponse = new ServiceResponse("Parent Category Retrieved Successfully");
            serviceResponse.addParam("categories", categoryDtoList);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        }  catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while getting parent category", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value  = "/sub_category/brand/{brandId}/subcat/{catId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getSubCategory(@PathVariable("brandId") Integer brandId, @PathVariable("catId") Integer categoryId) {
        try{
            CategoryDto categoryDto = new CategoryDto();
            categoryDto.setBrandId(brandId);
            categoryDto.setId(categoryId);

            List<CategoryDto> categoryDtoList = clientService.getSubCategory(categoryDto);

            ServiceResponse serviceResponse = new ServiceResponse("Sub Category Retrieved Successfully");
            serviceResponse.addParam("categories", categoryDtoList);

            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while getting parent category", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value="/get_order/{orderId}/fbId/{facebookId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getOrderDetails(@RequestHeader HttpHeaders headers, @PathVariable Integer orderId, @PathVariable Long facebookId) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
            //validateMobileClient(headerDto.getAccessToken());

            OrderSummaryDto order = clientService.getOrderSummaryById(orderId, facebookId);
            ServiceResponse serviceResponse = new ServiceResponse("Order Summary retrieved Successfully");
            serviceResponse.addParam("order",order);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving order", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_items/brand/{brandId}/cat/{catId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getItems(@PathVariable("brandId") Integer brandId, @PathVariable("catId") Integer categoryId) {
        try {
            List<ItemDto> items = clientService.getItems(brandId, categoryId);

            ServiceResponse serviceResponse = new ServiceResponse("Items retrieved Successfully");
            serviceResponse.addParam("items", items);

            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving items", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_item_detail/item/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getItemDetail(@PathVariable("id") Integer itemId) {
        try {
            ItemEntity item = clientService.getItemDetail(itemId);

            ServiceResponse serviceResponse = new ServiceResponse("Item Detail retrieved Successfully");
            serviceResponse.addParam("item", item);

            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while retrieving Item Detail", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/add_cart", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> saveCart(@RequestHeader HttpHeaders headers, @RequestBody CartEntity cart) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
            validateMobileClient(headerDto.getAccessToken());

            clientService.saveCart(cart);
            ServiceResponse serviceResponse = new ServiceResponse("Successfully add to cart");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while saving cart", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }

    }

    @RequestMapping(value = "/add_custom_cart/fbid/{facebookId}/storeId/{storeId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> saveCustomCart(@RequestHeader HttpHeaders headers, @RequestBody RequestJsonDto requestJsonDto, @PathVariable Long facebookId, @PathVariable Integer storeId ) {
        try{
            HeaderDto headerDto = new HeaderDto();
            /*GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
            validateMobileClient(headerDto.getAccessToken());*/
            //ToDo: move this block of code to the service layer
            List<CartEntity> carts = requestJsonDto.getCustomCartList();
            for (CartEntity cart: carts){
                CustomerEntity customer = new CustomerEntity();
                customer.setFacebookId(facebookId);
                cart.setCustomer(customer);
                StoresBrandEntity storesBrand = new StoresBrandEntity();
                storesBrand.setId(storeId);
                cart.setStoresBrand(storesBrand);
                clientService.saveCart(cart);
            }
            ServiceResponse serviceResponse = new ServiceResponse("Successfully add to cart");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while saving cart", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }

    }

    @RequestMapping(value = "/get_my_cart/fbId/{facebookId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getMyCart(@PathVariable("facebookId") Long facebookId) {
        try {
            CartDto carts = clientService.getMyCart(facebookId, null, null);
            ServiceResponse serviceResponse = new ServiceResponse("My cart successfully retrieved");
            if(carts != null)
                serviceResponse.addParam("myCart", carts);

            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while getting cart", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_my_cart/fbId/{facebookId}/lat/{lat}/long/{lon}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getMyCart(@PathVariable("facebookId") Long facebookId, @PathVariable("lat") String lat, @PathVariable("lon") String lon) {
        try {
            CartDto carts = clientService.getMyCart(facebookId, lat, lon);
            ServiceResponse serviceResponse = new ServiceResponse("My cart successfully retrieved");
            if(carts != null)
                serviceResponse.addParam("myCart", carts);

            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while getting cart", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }


    @RequestMapping(value = "/validate_cart/fbId/{facebookId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> validateCart(@RequestHeader HttpHeaders headers, @PathVariable("facebookId") Long facebookId) {

        try {
            HeaderDto headerDto = new HeaderDto();
            /*GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
            validateMobileClient(headerDto.getAccessToken());*/

            ServiceResponse serviceResponse;
            CartDto cartDto = clientService.validateCart(facebookId);
            if(!cartDto.getValid()){
                String[] msg = null;
                if(cartDto.getMessage().contains(":"))
                    msg = cartDto.getMessage().split(":");
                if(msg!=null){
                    throw new YSException(msg[0], msg[1]);
                } else {
                    throw new YSException(cartDto.getMessage());
                }

            } else {
                serviceResponse = new ServiceResponse("Cart validated successfully");
                serviceResponse.addParam("valid", true);
            }

            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while validating cart", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/delete_cart/cartId/{cartId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> deleteCart(@RequestHeader HttpHeaders headers, @PathVariable("cartId") Integer cartId) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
            validateMobileClient(headerDto.getAccessToken());

            clientService.deleteCart(cartId);
            ServiceResponse serviceResponse = new ServiceResponse("Cart deleted successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while deleting cart", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/delete_cart/fbId/{facebookId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> deleteAllCart(@RequestHeader HttpHeaders headers, @PathVariable("facebookId") Long facebookId) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
            validateMobileClient(headerDto.getAccessToken());

            clientService.deleteAllCart(facebookId);
            ServiceResponse serviceResponse = new ServiceResponse("Cart deleted successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while deleting cart", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value= "/get_cart_info/fbId/{facebookId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getCartInfo(@PathVariable("facebookId") Long facebookId) {
        try {
            CartDto cart = clientService.getCartSize(facebookId);
            ServiceResponse serviceResponse = new ServiceResponse("Cart info retrieved successfully");
            serviceResponse.addParam("myCart", cart);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while checking cart exist", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }

    }

    @RequestMapping(value = "/get_cart_detail/cartId/{cartId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getCartDetail(@PathVariable("cartId") Integer cartId) {
        try{
            CartDto cartDto = clientService.getCartDetail(cartId);
            ServiceResponse serviceResponse = new ServiceResponse("Cart detail retrieved successfully");
            serviceResponse.addParam("cartDetail", cartDto);

            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while getting cart detail", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/update_cart", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> updateCart(@RequestHeader HttpHeaders headers, @RequestBody CartEntity cart) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
            validateMobileClient(headerDto.getAccessToken());

            clientService.updateCart(cart);
            ServiceResponse serviceResponse = new ServiceResponse("Cart Updated Successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while updating cart detail", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/checkout_info/fbId/{facebookId}/addressId/{addressId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getCheckOutInfo(@RequestHeader HttpHeaders headers, @PathVariable("facebookId") Long facebookId, @PathVariable("addressId") Integer addressId) {
        try{
            HeaderDto headerDto = new HeaderDto();
            /*GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
            validateMobileClient(headerDto.getAccessToken());*/

            CheckOutDto checkOutDto = customerService.getCheckOutInfo(facebookId, addressId);
            ServiceResponse serviceResponse = new ServiceResponse("Checkout Info Retrieved Successfully");
            serviceResponse.addParam("checkOutInfo", checkOutDto);

            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while getting checkout info", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_default_categories", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getDefaultCategories() {
        try{
            List<CategoryEntity> categories = customerService.getDefaultCategories();

            ServiceResponse serviceResponse = new ServiceResponse("Parent categories retrieved successfully");
            serviceResponse.addParam("categories", categories);

            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        }catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while retrieving parent categories", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_category_brands/catId/{catId}/pageNo/{pageNo}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getCategoryBrands(@PathVariable("catId") Integer categoryId, @PathVariable("pageNo") Integer pageNo) {
        try{
            Map<String, Object> map = customerService.getCategoryBrands(categoryId, pageNo, null, null);

            ServiceResponse serviceResponse = new ServiceResponse("Brands fetched successfully");
            if(pageNo!=null && pageNo.equals(1)) {
                serviceResponse.addParam("currency", map.get("currency"));
                serviceResponse.addParam("pageInfo", map.get("page"));
                serviceResponse.addParam("featuredBrands", map.get("featured"));
            }
            serviceResponse.addParam("otherBrands", map.get("all"));
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        }catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while retrieving category's brands", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_category_brands/catId/{catId}/pageNo/{pageNo}/lat/{lat}/long/{lon}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getCategoryBrands(@PathVariable("catId") Integer categoryId, @PathVariable("pageNo") Integer pageNo,
                                                              @PathVariable("lat") String lat, @PathVariable("lon") String lon) {
        try{
            Map<String, Object> map = customerService.getCategoryBrands(categoryId, pageNo, lat, lon);

            ServiceResponse serviceResponse = new ServiceResponse("Brands fetched successfully");
            if(pageNo!=null && pageNo.equals(1)) {
                serviceResponse.addParam("currency", map.get("currency"));
                serviceResponse.addParam("pageInfo", map.get("page"));
                serviceResponse.addParam("featuredBrands", map.get("featured"));
            }
            serviceResponse.addParam("otherBrands", map.get("all"));
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        }catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while retrieving category's brands", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

//    @RequestMapping(value = "/test_cat/brandId/{brandId}", method = RequestMethod.GET)
//    @ResponseBody
//    public ResponseEntity


    @RequestMapping(value= "/invite_friend", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> inviteFriend(@RequestHeader HttpHeaders headers, @RequestBody ArrayList<String> emailList) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN, GeneralUtil.ID);
            validateMobileClient(headerDto.getAccessToken());

            clientService.inviteFriend(headerDto, emailList);
            ServiceResponse serviceResponse = new ServiceResponse("Friend(s) has been invited successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while inviting friend(s)", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value="/rating_issues", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getAllRatingReason(@RequestHeader HttpHeaders headers) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto/*, GeneralUtil.ACCESS_TOKEN*/);
            //validateMobileClient(headerDto.getAccessToken());

            List<RatingReason> ratingReasons = customerService.getRatingReasons();
            ServiceResponse serviceResponse = new ServiceResponse("Rating reason retrieved successfully");
            serviceResponse.addParam("ratingList",ratingReasons);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving rating reason list", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/update_device_info/fbId/{facebookId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> updateDeviceInfo(@RequestHeader HttpHeaders headers, @PathVariable("facebookId") Long facebookId, @RequestBody DeviceInfo deviceInfo) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
            validateMobileClient(headerDto.getAccessToken());

            clientService.updateUserDeviceToken(facebookId, deviceInfo.getDeviceToken());
            ServiceResponse serviceResponse = new ServiceResponse("Device Token updated Successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while updating device info", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/current_orders/fbId/{facebookId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getMyCurrentOrders(@RequestHeader HttpHeaders headers, @PathVariable("facebookId") Long facebookId) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
//            validateMobileClient(headerDto.getAccessToken());

            List<MyOrderDto> orders = customerService.getMyCurrentOrders(facebookId);
            ServiceResponse serviceResponse = new ServiceResponse("Current orders retrieved successfully");
            serviceResponse.addParam("orders",orders);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while retrieving current orders", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }


    @RequestMapping(value = "/past_orders/fbId/{facebookId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getMyPastOrders(@RequestHeader HttpHeaders headers, @PathVariable("facebookId") Long facebookId, @RequestBody(required = false) Page page) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
//            validateMobileClient(headerDto.getAccessToken());

           PaginationDto paginationDto= customerService.getMyPastOrders(facebookId, page);
            ServiceResponse serviceResponse = new ServiceResponse("Past orders retrieved successfully");
            serviceResponse.addParam("orders",paginationDto);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while retrieving past orders", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/update_device_info/userId/{userId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> updateDeviceInformation(@RequestHeader HttpHeaders headers, @PathVariable("userId") Integer userId, @RequestBody DeviceInfo deviceInfo) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
//            validateMobileClient(headerDto.getAccessToken());

            clientService.updateUserDeviceTokenFromUserId(userId, deviceInfo.getDeviceToken());
            ServiceResponse serviceResponse = new ServiceResponse("Device Token updated Successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while updating device info", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_currency", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getCurrency(@RequestHeader HttpHeaders headers) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
            validateMobileClient(headerDto.getAccessToken());

            String currencyType = clientService.getCurrencyType();
            ServiceResponse serviceResponse = new ServiceResponse("Currency type retrieved successfully");
            serviceResponse.addParam("currency", currencyType);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while retrieving currency type", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/search/content/{word}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getAllSearch(@PathVariable("word") String word, @RequestBody RequestJsonDto requestJsonDto) {
        try{
//            PageInfo pageInfo = new PageInfo();
//            pageInfo.setPageNumber(pageNo);
//            requestJsonDto.setPageInfo(pageInfo);

            SearchDto searchDto = customerService.getSearchContent(word, requestJsonDto);
            ServiceResponse serviceResponse = new ServiceResponse("Search content retrieved successfully");

            /* Page Information and Currency Only Displayed at First page */
//            if(pageNo!=1 && pageNo > 1){
//                searchDto.setPageInfo(null);
//                searchDto.setCurrency(null);
//            }

            serviceResponse.addParam("search", searchDto);
            return new ResponseEntity<ServiceResponse>(serviceResponse, GeneralUtil.getCacheHeader(), HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while searching items and stores", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/search_in_store/brand/{brandId}/content/{word}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getSearchInStore(@PathVariable("word") String word, @PathVariable("brandId") Integer brandId, @RequestBody RequestJsonDto requestJsonDto) {
        try{

            SearchDto searchDto = customerService.getSearchInStore(word, brandId, requestJsonDto);
            ServiceResponse serviceResponse = new ServiceResponse("Search content retrieved successfully");
            serviceResponse.addParam("search", searchDto);

            return new ResponseEntity<ServiceResponse>(serviceResponse, GeneralUtil.getCacheHeader(), HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while searching items in store " + brandId, e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }


    @RequestMapping(value = "/reorder/order/{orderId}/fbId/{facebookId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> reorderPreviousItems(@RequestHeader HttpHeaders headers, @PathVariable Integer orderId, @PathVariable Long facebookId, @RequestBody(required = false) RequestJsonDto requestJsonDto) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
            validateMobileClient(headerDto.getAccessToken());

            if(requestJsonDto == null){
                requestJsonDto = new RequestJsonDto();
                requestJsonDto.setFlushCart(false);
            }
            clientService.reOrder(orderId, facebookId, requestJsonDto.getFlushCart());
            ServiceResponse serviceResponse = new ServiceResponse("Reorder has been accomplished successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while accomplishing reorder", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/track_order/order/{orderId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> trackOrder(@RequestHeader HttpHeaders headers, @PathVariable Integer orderId) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
          //  validateMobileClient(headerDto.getAccessToken());

            TrackOrderDto trackOrder = customerService.getTrackOrderInfo(orderId);
            ServiceResponse serviceResponse = new ServiceResponse("Order has been tracked successfully");
            serviceResponse.addParam("order", trackOrder);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while tracking order", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_profile/fbId/{facebookId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getProfileInformation(@RequestHeader HttpHeaders headers, @PathVariable Long facebookId) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
            //  validateMobileClient(headerDto.getAccessToken());

            CustomerEntity customer = customerService.getCustomerProfile(facebookId);
            ServiceResponse serviceResponse = new ServiceResponse("Profile information has been retrieved successfully");
            serviceResponse.addParam("customer", customer);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while retrieving profile information", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/rate_dboy/order/{orderId}/fbId/{facebookId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> rateDeliveryBoy(@RequestHeader HttpHeaders headers,
                                                           @PathVariable Integer orderId, @PathVariable Long facebookId,
                                                           @RequestBody RatingEntity rating) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
            //  validateMobileClient(headerDto.getAccessToken());

            customerService.rateDeliveryBoy(orderId, facebookId, rating);
            ServiceResponse serviceResponse = new ServiceResponse("Shopper has been rated successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while rating shopper", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_rating/order/{orderId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getRatingDetails(@RequestHeader HttpHeaders headers, @PathVariable Integer orderId) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
            //  validateMobileClient(headerDto.getAccessToken());

            RatingEntity rating = customerService.getRatingFromCustomerSide(orderId);
            ServiceResponse serviceResponse = new ServiceResponse("Rating information has been retrieved successfully");
            serviceResponse.addParam("rating", rating);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while retrieving rating information", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_cancel_details/order/{orderId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getCancelDetails(@RequestHeader HttpHeaders headers, @PathVariable Integer orderId) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
            //  validateMobileClient(headerDto.getAccessToken());

            OrderCancelEntity orderCancel = clientService.orderCancelDetails(orderId);
            ServiceResponse serviceResponse = new ServiceResponse("Order cancel information has been retrieved successfully");
            serviceResponse.addParam("orderCancel", orderCancel);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while retrieving order cancel information", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_helpline_info", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getHelplineInformation() {
        try{
            //HeaderDto headerDto = new HeaderDto();
            //GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
            //validateMobileClient(headerDto.getAccessToken());

            PreferenceDto helplineInfo = clientService.getHelpLineDetails();
            ServiceResponse serviceResponse = new ServiceResponse("Help line details has been retrieved successfully");
            serviceResponse.addParam("helplineInfo", helplineInfo);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while retrieving helpline information", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/test_timestamp", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getSms(@RequestBody TestEntity testEntity) {
        try{
            clientService.saveTimeStamp(testEntity);
            ServiceResponse serviceResponse = new ServiceResponse("Succesully Tested");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while testing time stamp", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/refill_wallet", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> refillWallet(@RequestHeader HttpHeaders headers, @RequestBody CustomerEntity customer) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
            //  validateMobileClient(headerDto.getAccessToken());
            customerService.refillWallet(customer);
            ServiceResponse serviceResponse = new ServiceResponse("Wallet has been refilled successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while refilling customer wallet of FB ID:"+customer.getFacebookId(), e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/transactions/fbId/{facebookId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getTransactionDetails(@RequestHeader HttpHeaders headers, @RequestBody(required = false) Page page, @PathVariable Long facebookId) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
            validateMobileClient(headerDto.getAccessToken());

            PaginationDto paginationDto = customerService.getWalletTransactions(page, facebookId);
            ServiceResponse serviceResponse = new ServiceResponse("List of transactions retrieved successfully");
            serviceResponse.addParam("transactionInfo", paginationDto);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving list of transactions", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/transactions/pgresponse", method = RequestMethod.POST, headers = "content-type=application/x-www-form-urlencoded", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String getPaymentGatewayResponse(@ModelAttribute PaymentGatewayDto paymentGatewayDto) {
        try{
           return customerService.paymentGatewaySettlement(paymentGatewayDto);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while getting payment response", e);
            return "Something went wrong while getting payment response";
        }
    }

    @RequestMapping(value = "/transactions/add_fund/fbId/{facebookId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> addFundToWallet(@RequestHeader HttpHeaders headers, @RequestBody PaymentGatewayInfoEntity paymentGatewayInfo, @PathVariable Long facebookId) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
            //validateMobileClient(headerDto.getAccessToken());

            PaymentGatewayDto paymentGatewayDto = customerService.requestToAddFundToWallet(facebookId, paymentGatewayInfo.getAmount());
            ServiceResponse serviceResponse = new ServiceResponse("Payment gateway info retrieved successfully");
            serviceResponse.addParam("paymentGatewayInfo", paymentGatewayDto);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving payment gateway info", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_wallet_balance/fbId/{facebookId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getWalletBalance(@RequestHeader HttpHeaders headers, @PathVariable Long facebookId) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
            //validateMobileClient(headerDto.getAccessToken());

            CustomerEntity customer= customerService.getWalletBalance(facebookId);
            ServiceResponse serviceResponse = new ServiceResponse("Wallet balance retrieved successfully");
            serviceResponse.addParam("customer", customer);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving wallet info", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }
}
