package com.yetistep.delivr.controller;

import com.yetistep.delivr.abs.AbstractManager;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.model.mobile.CategoryDto;
import com.yetistep.delivr.model.mobile.dto.CartDto;
import com.yetistep.delivr.model.mobile.dto.ItemDto;
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
    public ResponseEntity<ServiceResponse> getBrands(@RequestBody(required = false) RequestJsonDto requestJsonDto) {
        try {
            Map<String, Object> map = clientService.getBrands(requestJsonDto);
            ServiceResponse serviceResponse = new ServiceResponse("Brands fetched successfully");
            if(requestJsonDto.getPageInfo()== null) {
                serviceResponse.addParam("featuredBrands", map.get("featured"));
                serviceResponse.addParam("pageInfo", map.get("page"));
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

    @RequestMapping(value="/get_order", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getOrderDetails(@RequestHeader HttpHeaders headers) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID/*, GeneralUtil.ACCESS_TOKEN*/);
            //validateMobileClient(headerDto.getAccessToken());

            OrderEntity order = clientService.getOrderById(Integer.parseInt(headerDto.getId()));
            ServiceResponse serviceResponse = new ServiceResponse("Order retrieved Successfully");
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

    @RequestMapping(value = "/get_my_cart/fbId/{facebookId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getMyCart(@PathVariable("facebookId") Long facebookId) {
        try {
            CartDto carts = clientService.getMyCart(facebookId);
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

    @RequestMapping(value= "/get_cart_info/fbId/{facebookId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> checkCart(@PathVariable("facebookId") Long facebookId) {
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
//            HeaderDto headerDto = new HeaderDto();
//            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
//            validateMobileClient(headerDto.getAccessToken());
            clientService.updateCart(cart);
            ServiceResponse serviceResponse = new ServiceResponse("Cart Updated Successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while updating cart detail", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }


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


}
