package com.yetistep.delivr.controller;

import com.yetistep.delivr.abs.AbstractManager;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.OrderSummaryDto;
import com.yetistep.delivr.dto.PaginationDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.enums.JobOrderStatus;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.model.mobile.dto.OrderInfoDto;
import com.yetistep.delivr.model.mobile.dto.PreferenceDto;
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
    public ResponseEntity<ServiceResponse> login(@RequestHeader HttpHeaders headers, @RequestBody UserDeviceEntity userDevice) {
        try{

            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.USERNAME, GeneralUtil.PASSWORD, GeneralUtil.ACCESS_TOKEN);

            validateMobileClient(headerDto.getAccessToken());

            DeliveryBoyEntity deliveryBoyEntity =  deliveryBoyService.dboyLogin(headerDto, userDevice);
            ServiceResponse serviceResponse = new ServiceResponse("Shopper logged in successfully");
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
            ServiceResponse serviceResponse = new ServiceResponse("Location of shopper updated successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred during updating location of shopper", e);
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
//            validateMobileClient(headerDto.getAccessToken());

            List<OrderInfoDto> activeOrders = deliveryBoyService.getActiveOrders (Integer.parseInt(headerDto.getId()));
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
    public ResponseEntity<ServiceResponse> getPastDeliveries(@RequestHeader HttpHeaders headers, @RequestBody(required = false) Page page) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID, GeneralUtil.ACCESS_TOKEN);
//            validateMobileClient(headerDto.getAccessToken());

            PaginationDto paginationDto = deliveryBoyService.getPastDeliveries(page, Integer.parseInt(headerDto.getId()));
            ServiceResponse serviceResponse = new ServiceResponse("List of past deliveries retrieved successfully");
            serviceResponse.addParam("pastDeliveries", paginationDto);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving list of past deliveries", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }


    @RequestMapping(value = "/get_dBoy_order_history", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> dBoyOrderHistory(@RequestHeader HttpHeaders headers, @RequestBody RequestJsonDto requestJsonDto) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);

            PaginationDto orders = deliveryBoyService.get_order_history(Integer.parseInt(headerDto.getId()), requestJsonDto);
            ServiceResponse serviceResponse = new ServiceResponse("List of past deliveries retrieved successfully");
            serviceResponse.addParam("orders", orders);
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

            ServiceResponse serviceResponse = new ServiceResponse("Shopper status has been updated successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while updating shopper status", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/upload_bill", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> uploadBill(@RequestHeader HttpHeaders headers, @RequestBody OrderEntity order) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID/*, GeneralUtil.ACCESS_TOKEN*/);
//            validateMobileClient(headerDto.getAccessToken());
            deliveryBoyService.uploadBills(order, Integer.parseInt(headerDto.getId()));
            ServiceResponse serviceResponse = new ServiceResponse("Attachments has been uploaded successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while loading attachments", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_profile", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getProfileInformation(@RequestHeader HttpHeaders headers) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID/*, GeneralUtil.ACCESS_TOKEN*/);
            //validateMobileClient(headerDto.getAccessToken());
            DeliveryBoyEntity deliveryBoy = deliveryBoyService.getProfileOfDeliveryBoy(Integer.parseInt(headerDto.getId()));
            ServiceResponse serviceResponse = new ServiceResponse("Information of shopper retrieved successfully");
            serviceResponse.addParam("deliveryBoy", deliveryBoy);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while retrieving information of shopper", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/add_item", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> addItemToOrder(@RequestHeader HttpHeaders headers, @RequestBody ItemsOrderEntity itemsOrderEntity) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID/*, GeneralUtil.ACCESS_TOKEN*/);
            //validateMobileClient(headerDto.getAccessToken());
            deliveryBoyService.addNewItem(itemsOrderEntity);
            ServiceResponse serviceResponse = new ServiceResponse("New item has been added successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while adding new item", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/update_item_order/{orderId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> updateItemsInOrder(@RequestHeader HttpHeaders headers, @RequestBody List<ItemsOrderEntity> itemsOrders, @PathVariable Integer orderId) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID/*, GeneralUtil.ACCESS_TOKEN*/);
            //validateMobileClient(headerDto.getAccessToken());
            deliveryBoyService.updateOrders(itemsOrders, orderId);
            ServiceResponse serviceResponse = new ServiceResponse("Order item has been updated successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while updating order item", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/update_order_item/{orderId}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> updateItemsOrderByItemOrderId(@RequestHeader HttpHeaders headers, @RequestBody ItemsOrderEntity itemsOrder, @PathVariable Integer orderId) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID/*, GeneralUtil.ACCESS_TOKEN*/);
            //validateMobileClient(headerDto.getAccessToken());
            deliveryBoyService.updateItemOrderByItemOrderId(itemsOrder, orderId);
            ServiceResponse serviceResponse = new ServiceResponse("Order item has been updated successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while updating order item", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/cancel_order", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> cancelOrder(@RequestHeader HttpHeaders headers, @RequestBody OrderEntity order) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID/*, GeneralUtil.ACCESS_TOKEN*/);
            //validateMobileClient(headerDto.getAccessToken());
            deliveryBoyService.cancelOrder(order, Integer.parseInt(headerDto.getId()));
            ServiceResponse serviceResponse = new ServiceResponse("Order has been cancelled successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while cancelling order", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value="/shopping_list/order/{orderId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getShoppingList(@RequestHeader HttpHeaders headers, @PathVariable Integer orderId) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto/*, GeneralUtil.ACCESS_TOKEN*/);
            //validateMobileClient(headerDto.getAccessToken());

            OrderSummaryDto orderSummary = deliveryBoyService.getShoppingList(orderId);
            ServiceResponse serviceResponse = new ServiceResponse("Shopping List retrieved successfully");
            serviceResponse.addParam("order",orderSummary);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving shopping list", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value="/get_order/{orderId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getOrderDetails(@RequestHeader HttpHeaders headers, @PathVariable Integer orderId) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID/*, GeneralUtil.ACCESS_TOKEN*/);
            //validateMobileClient(headerDto.getAccessToken());

            OrderEntity order = deliveryBoyService.getOrderById(orderId, Integer.parseInt(headerDto.getId()));
            ServiceResponse serviceResponse = new ServiceResponse("Order retrieved Successfully");
            serviceResponse.addParam("order",order);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving order", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value="/job_status", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getJobOrderStatus(@RequestHeader HttpHeaders headers) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID/*, GeneralUtil.ACCESS_TOKEN*/);
            //validateMobileClient(headerDto.getAccessToken());

            JobOrderStatus jobOrderStatus = deliveryBoyService.getJobOrderStatusFromOrderId(Integer.parseInt(headerDto.getId()));
            ServiceResponse serviceResponse = new ServiceResponse("Job order status retrieved successfully");
            serviceResponse.addParam("orderStatus",jobOrderStatus);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving job order list", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value="/cancel_reason", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getAllCancelReason(@RequestHeader HttpHeaders headers) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto/*, GeneralUtil.ACCESS_TOKEN*/);
            //validateMobileClient(headerDto.getAccessToken());

            List<ReasonDetailsEntity> cancelReasons = deliveryBoyService.getCancelReasonList();
            ServiceResponse serviceResponse = new ServiceResponse("List of cancel reason retrieved successfully");
            serviceResponse.addParam("reasonList",cancelReasons);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving cancel reason list", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value="/item_order_details/{itemOrderId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getItemOrderDetails(@RequestHeader HttpHeaders headers, @PathVariable Integer itemOrderId) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto/*, GeneralUtil.ACCESS_TOKEN*/);
            //validateMobileClient(headerDto.getAccessToken());

            ItemsOrderEntity itemOrder = deliveryBoyService.getItemOrderById(itemOrderId);
            ServiceResponse serviceResponse = new ServiceResponse("Details of item retrieved successfully");
            serviceResponse.addParam("itemOrder",itemOrder);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving details of order item", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value="/reject_order/{orderId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> rejectOrder(@RequestHeader HttpHeaders headers, @PathVariable Integer orderId) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID/*, GeneralUtil.ACCESS_TOKEN*/);
            //validateMobileClient(headerDto.getAccessToken());

            deliveryBoyService.rejectOrder(Integer.parseInt(headerDto.getId()), orderId);
            ServiceResponse serviceResponse = new ServiceResponse("Order has been rejected successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while rejecting order", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_acceptance_radius", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getAcceptanceRadius(@RequestHeader HttpHeaders headers) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
            //  validateMobileClient(headerDto.getAccessToken());

            PreferenceDto acceptanceDetails = deliveryBoyService.getAcceptanceDetails();
            ServiceResponse serviceResponse = new ServiceResponse("Acceptance radius has been retrieved successfully");
            serviceResponse.addParam("acceptanceDetails", acceptanceDetails);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while retrieving acceptance radius", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/order/paid_from_cod/{orderId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getPaidFromCODAmount(@RequestHeader HttpHeaders headers, @PathVariable Integer orderId) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ACCESS_TOKEN);
            validateMobileClient(headerDto.getAccessToken());

            OrderEntity order = deliveryBoyService.getPaidFromCODAmount(orderId);
            ServiceResponse serviceResponse = new ServiceResponse("Paid from cash on delivery amount has been retrieved successfully");
            serviceResponse.addParam("order", order);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while retrieving paid from cash on delivery amount", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }
}
