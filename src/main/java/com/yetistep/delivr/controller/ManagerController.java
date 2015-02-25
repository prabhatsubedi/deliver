package com.yetistep.delivr.controller;

import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.PaginationDto;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.service.inf.*;
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
import java.util.Map;

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
    @Autowired
    ManagerService managerService;
    @Autowired
    AdminService adminService;

    private static final Logger log = Logger.getLogger(ManagerController.class);

    @RequestMapping(value = "/save_dboy", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> saveDeliveryBoy(@RequestHeader HttpHeaders headers, @RequestBody DeliveryBoyEntity deliveryBoy) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.USERNAME, GeneralUtil.PASSWORD);
            deliveryBoyService.saveDeliveryBoy(deliveryBoy, headerDto);

            ServiceResponse serviceResponse = new ServiceResponse("Delivery Boy has been saved successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while creating delivery boy", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_dboy", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getDeliveryBoy(@RequestHeader HttpHeaders headers) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            DeliveryBoyEntity deliveryBoy = deliveryBoyService.findDeliveryBoyById(headerDto);

            ServiceResponse serviceResponse = new ServiceResponse("Details of delivery boy with ID: "+headerDto.getId());
            serviceResponse.addParam("deliveryBoy", deliveryBoy);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while retrieving delivery boy: ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_dboys", method = RequestMethod.GET)
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
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/update_dboy_status", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<ServiceResponse> updateDeliveryBoyStatus(@RequestBody DeliveryBoyEntity deliveryBoyEntity) {
        try {
            deliveryBoyService.updateDeliveryBoyStatus(deliveryBoyEntity);

            ServiceResponse serviceResponse = new ServiceResponse("Delivery Boy status has been updated successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while updating delivery boy status", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/update_dboy", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<ServiceResponse> updateDeliveryBoy(@RequestHeader HttpHeaders headers, @RequestBody DeliveryBoyEntity deliveryBoy) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.USERNAME);

            List<String> hd = headers.get("passowrd");
            if (hd != null && hd.size() > 0)
                headerDto.setPassword(hd.get(0));
            else
                headerDto.setPassword(null);

            deliveryBoyService.updateDeliveryBoy(deliveryBoy, headerDto);

            ServiceResponse serviceResponse = new ServiceResponse("Delivery Boy has been updated successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while updating delivery boy", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
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
            GeneralUtil.logError(log, "Error Occurred while activating merchant", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }

    }

    @RequestMapping(value = "/get_merchants", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getMerchants() {
        try{
            List<MerchantEntity> merchantEntities = merchantService.getMerchants();

            ServiceResponse serviceResponse = new ServiceResponse("Merchant retrieved successfully");
            serviceResponse.addParam("merchants", merchantEntities);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while getting merchants", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }

    }

    @RequestMapping(value = "/change_user_status", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<ServiceResponse> updateUserStatus(@RequestBody UserEntity userEntity) {
        try{
            userService.changeUserStatus(userEntity);

            ServiceResponse serviceResponse = new ServiceResponse("User status updated successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while updating user status", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }

    }

    @RequestMapping(value = "/get_logs", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getActionLogs(@RequestBody(required = false) Page page) {
        try{
            PaginationDto paginationDto = managerService.getActionLog(page);

            ServiceResponse serviceResponse = new ServiceResponse("List of action log");
            serviceResponse.addParam("actionLog", paginationDto);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving action logs: ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_special_brands", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getFeaturedAndPrioritizedStoreBrands() {
        try{
            List<StoresBrandEntity> storeBrands = managerService.findFeaturedAndPrioritizedStoreBrands();
            ServiceResponse serviceResponse = new ServiceResponse("List of featured and prioritized store brands");
            serviceResponse.addParam("storeBrands", storeBrands);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving list of featured and prioritized store brands", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_other_brands", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getNonFeaturedAndPrioritizedStoreBrands(@RequestBody(required = false) Page page) {
        try{
            PaginationDto paginationDto = managerService.findNonFeaturedAndPrioritizedStoreBrands(page);

            ServiceResponse serviceResponse = new ServiceResponse("List of non featured & prioritized log");
            serviceResponse.addParam("storeBrands", paginationDto);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving list of non featured and prioritized store brands:", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_inactive_brands", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getInactiveStoreBrands(@RequestBody(required = false) Page page) {
        try{
            PaginationDto paginationDto = managerService.findInactiveStoreBrands(page);

            ServiceResponse serviceResponse = new ServiceResponse("List of inactive store brands");
            serviceResponse.addParam("storeBrands", paginationDto);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving list of inactive store brands:", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/update_special_brands", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> updateFeatureAndPriorityOfStoreBrands(@RequestBody List<StoresBrandEntity> storeBrands) {
        try{
            managerService.updateFeatureAndPriorityOfStoreBrands(storeBrands);
            ServiceResponse serviceResponse = new ServiceResponse("Feature & priority of stores brands updated successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while updating feature and priority of store brands:", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }


    @RequestMapping(value = "/save_category", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> saveCategory(@RequestHeader HttpHeaders headers, @RequestBody CategoryEntity category) {
        try {
            HeaderDto headerDto = new HeaderDto();
            List<String> hd = headers.get("id");
            if (hd != null && hd.size() > 0)
                headerDto.setId(hd.get(0));
            else
                headerDto.setId(null);

            Object newCategory = managerService.saveCategory(category, headerDto);

            ServiceResponse serviceResponse = new ServiceResponse("Category has been saved successfully");
            serviceResponse.addParam("category", newCategory);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while creating category", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }


    @RequestMapping(value = "/update_category", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<ServiceResponse> updateCategory(@RequestHeader HttpHeaders headers, @RequestBody CategoryEntity category) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);

            Object newCategory = managerService.updateCategory(category, headerDto);

            ServiceResponse serviceResponse = new ServiceResponse("Category has been updated successfully");
            serviceResponse.addParam("category", newCategory);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while updating category", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_default_categories", method = RequestMethod.GET)
    @ResponseBody
    public  ResponseEntity<ServiceResponse> getDefaultCategories() {
        try {

            List<CategoryEntity> categories = managerService.getDefaultCategories();

            ServiceResponse serviceResponse = new ServiceResponse("Category has been retrieved successfully");
            serviceResponse.addParam("categories", categories);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while retrieving category", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_category", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getCategory(@RequestHeader HttpHeaders headers) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            CategoryEntity category = managerService.getCategory(headerDto);

            ServiceResponse serviceResponse = new ServiceResponse("Category has been retrived successfully");
            serviceResponse.addParam("category", category);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while retrieving category", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }


    @RequestMapping(value="/gods_view", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> godsView(){
        try{

            List<Map<String, Map<Integer, Map<String, String>>>> godsView = adminService.getGodsView();
            ServiceResponse serviceResponse = new ServiceResponse("Gods view has been retrieved successfully successfully");
            serviceResponse.addParam("godsView", godsView);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        }catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving gods view", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);

        }
    }

    @RequestMapping(value="/get_delivery_graph", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getDeliveryGraph(@RequestHeader HttpHeaders headers){
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            List<Map<String, Map<String, Integer>>> graphData = adminService.getDeliveryGraphByDate(headerDto);
            ServiceResponse serviceResponse = new ServiceResponse("Delivery graph view has been retrieved successfully successfully");
            serviceResponse.addParam("graphData", graphData);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        }catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving delivery graph view", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);

        }
    }

    @RequestMapping(value="/get_new_user_graph", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getNewUserGraph(@RequestHeader HttpHeaders headers){
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            Map<String, Integer> graphData = adminService.getNewUserGraph(headerDto);
            ServiceResponse serviceResponse = new ServiceResponse("User graph view has been retrieved successfully successfully");
            serviceResponse.addParam("graphData", graphData);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        }catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving user graph view", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);

        }
    }

    @RequestMapping(value="/get_delivery_success_graph", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getDeliverySuccessGraph(@RequestHeader HttpHeaders headers){
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            Map<String,  Map<String, Integer>> graphData = adminService.getDeliverySuccessGraph(headerDto);
            ServiceResponse serviceResponse = new ServiceResponse("Delivery Success graph view has been retrieved successfully successfully");
            serviceResponse.addParam("graphData", graphData);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        }catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving delivery success graph view", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);

        }
    }

    @RequestMapping(value="/get_on_time_delivery_graph", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> getOnTimeDeliveryGraph(@RequestHeader HttpHeaders headers){
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            Map<String,  Map<String, Integer>> graphData = adminService.getOnTimeDeliveryGraph(headerDto);
            ServiceResponse serviceResponse = new ServiceResponse("On time delivery graph view has been retrieved successfully successfully");
            serviceResponse.addParam("graphData", graphData);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        }catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving delivery success graph view", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);

        }
    }

    @RequestMapping(value="/view_invoices", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> viewInvoices(@RequestHeader HttpHeaders headers){
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);

            ServiceResponse serviceResponse = new ServiceResponse("Invoice has been retrieved successfully successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        }catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving invoice", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);

        }
    }

    @RequestMapping(value="/change_order_settlement", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> changeOrderSettlement(@RequestHeader HttpHeaders headers){
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);

            managerService.changeOrderSettlement(headerDto);
            ServiceResponse serviceResponse = new ServiceResponse("Invoice has been retrieved successfully successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        }catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving invoice", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);

        }
    }




}
