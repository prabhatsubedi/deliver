package com.yetistep.delivr.controller;

import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.PaginationDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.enums.Role;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.service.inf.MerchantService;
import com.yetistep.delivr.util.GeneralUtil;
import com.yetistep.delivr.util.ServiceResponse;
import com.yetistep.delivr.util.SessionManager;
import com.yetistep.delivr.util.YSException;
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
 * Time: 3:23 PM
 * To change this template use File | Settings | File Templates.
 */
@RestController
@RequestMapping(value = "/merchant")
public class MerchantController {
    @Autowired
    MerchantService merchantService;

    private static final Logger log = Logger.getLogger(ManagerController.class);

    @RequestMapping(value = "/save_store", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> saveStore(@RequestHeader HttpHeaders headers, @RequestBody RequestJsonDto requestJson) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.MERCHANT_ID);
            merchantService.saveStore(requestJson, headerDto);

            ServiceResponse serviceResponse = new ServiceResponse("Store(Stores) has been saved successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while adding store", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/update_store", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> updateStore(@RequestBody RequestJsonDto requestJson) {
        try {
            merchantService.updateStore(requestJson);

            ServiceResponse serviceResponse = new ServiceResponse("Store(Stores) has been updated successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while adding store", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_parent_categories", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponse> getParentCategories(){
        try {

            List<CategoryEntity> categories =  merchantService.getParentCategories();

            ServiceResponse serviceResponse = new ServiceResponse("Categories retrieved successfully");
            serviceResponse.addParam("categories", categories);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);

        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while retrieving categories", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/update_merchant", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> updateMerchant(@RequestHeader HttpHeaders headers, @RequestBody MerchantEntity merchantEntity) {
        try {
            HeaderDto headerDto = new HeaderDto();
            List<String> hd = headers.get("merchantId");
            if (hd != null && hd.size() > 0)
                headerDto.setMerchantId(Integer.parseInt( hd.get(0)));
            else {
                if(SessionManager.getRole().toString().equals(Role.ROLE_MERCHANT.toString())){
                    headerDto.setMerchantId(SessionManager.getMerchantId());
                } else {
                    headerDto.setMerchantId(null);
                }
            }
            merchantService.updateMerchant(merchantEntity, headerDto);
            ServiceResponse serviceResponse = new ServiceResponse("Merchant has been updated successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while updating merchant", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_stores", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponse> findStoreList(@RequestHeader HttpHeaders headers) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.MERCHANT_ID);
            List<Object> storesBrand = merchantService.findBrandList(headerDto);
            ServiceResponse serviceResponse = new ServiceResponse("Stores has been retrieved successfully");
            serviceResponse.addParam("storesBrand", storesBrand);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while fetching stores", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_brands", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> findAllBrands(@RequestHeader HttpHeaders headers, @RequestBody RequestJsonDto requestJsonDto) {
        try {
            HeaderDto headerDto = new HeaderDto();
            List<String> hd = headers.get("merchantId");
            if (hd != null && hd.size() > 0)
                headerDto.setMerchantId(Integer.parseInt( hd.get(0)));
            else {
                if(SessionManager.getRole().toString().equals(Role.ROLE_MERCHANT.toString())){
                    headerDto.setMerchantId(SessionManager.getMerchantId());
                } else {
                    headerDto.setMerchantId(null);
                }
            }
            PaginationDto brands = merchantService.findBrands(headerDto, requestJsonDto);
            ServiceResponse serviceResponse = new ServiceResponse("brands has been retrieved successfully");
            serviceResponse.addParam("brands", brands);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while fetching stores", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_search_brands", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponse> findSearchBrands(@RequestHeader HttpHeaders headers) {
        try {
            HeaderDto headerDto = new HeaderDto();
            List<String> hd = headers.get("merchantId");
            if (hd != null && hd.size() > 0)
                headerDto.setMerchantId(Integer.parseInt( hd.get(0)));
            else {
                if(SessionManager.getRole().toString().equals(Role.ROLE_MERCHANT.toString())){
                    headerDto.setMerchantId(SessionManager.getMerchantId());
                } else {
                    headerDto.setMerchantId(null);
                }
            }
            List<StoresBrandEntity> brands = merchantService.findSearchBrands(headerDto);
            ServiceResponse serviceResponse = new ServiceResponse("brands has been retrieved successfully");
            serviceResponse.addParam("brands", brands);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while fetching stores", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_store_detail", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponse> findStoreDetail(@RequestHeader HttpHeaders headers) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            StoresBrandEntity storesBrand = merchantService.findBrandDetail(headerDto);
            ServiceResponse serviceResponse = new ServiceResponse("Stores detail has been retrieved successfully");
            serviceResponse.addParam("storesBrand", storesBrand);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while fetching stores", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/save_item", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> saveItem(@RequestHeader HttpHeaders headers, @RequestBody RequestJsonDto requestJson) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            merchantService.saveItem(requestJson, headerDto);
            ServiceResponse serviceResponse = new ServiceResponse("Item has been saved successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while saving item", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/update_item", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> updateItem(@RequestBody RequestJsonDto requestJson) {
        try {
            merchantService.updateItem(requestJson);
            ServiceResponse serviceResponse = new ServiceResponse("Item has been updated successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while updating item", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    /*store list is required on item create*/
    @RequestMapping(value = "/get_brands_stores", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponse> findBrandsStores(@RequestHeader HttpHeaders headers) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            List<StoreEntity> stores = merchantService.findStoresByBrand(headerDto);
            ServiceResponse serviceResponse = new ServiceResponse("Stores has been retrieved successfully");
            serviceResponse.addParam("stores", stores);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while fetching stores", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    /*store list is required on item create*/
    @RequestMapping(value = "/get_brands_categories", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponse> findBrandsCategories(@RequestHeader HttpHeaders headers) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            List<CategoryEntity> categories = merchantService.findCategoriesByBrand(headerDto);
            ServiceResponse serviceResponse = new ServiceResponse("Categories has been retrieved successfully");
            serviceResponse.addParam("categories", categories);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while fetching stores", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_search_categories", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponse> findSearchCategories(@RequestHeader HttpHeaders headers) {
        try {
            HeaderDto headerDto = new HeaderDto();
            //GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            List<String> hd = headers.get("id");
            if (hd != null && hd.size() > 0)
                headerDto.setId( hd.get(0));
            else
                headerDto.setId(null);

            List<CategoryEntity> categories = merchantService.findBrandCategoryList(headerDto);
            ServiceResponse serviceResponse = new ServiceResponse("Categories has been retrieved successfully");
            serviceResponse.addParam("categories", categories);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while fetching stores", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    /*store list is required on item create*/
    @RequestMapping(value = "/get_child_categories", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> findChildCategories(@RequestBody RequestJsonDto requestJson) {
        try {
            List<CategoryEntity> categories = merchantService.findChildCategories(requestJson);
            ServiceResponse serviceResponse = new ServiceResponse("Categories has been retrieved successfully");
            serviceResponse.addParam("categories", categories);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while fetching stores", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_stores_items", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponse> findStoresItems(@RequestHeader HttpHeaders headers) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            List<ItemEntity> items = merchantService.findStoresItems(headerDto);
            ServiceResponse serviceResponse = new ServiceResponse("Items has been retrieved successfully");
            serviceResponse.addParam("items", items);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while fetching stores", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }


    @RequestMapping(value = "/get_categories_items", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> findCategoriesItems(@RequestBody RequestJsonDto requestJson) {
        try {
            PaginationDto items = merchantService.findCategoriesItems(requestJson);
            ServiceResponse serviceResponse = new ServiceResponse("Items has been retrieved successfully");
            serviceResponse.addParam("items", items);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while fetching stores", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }


    @RequestMapping(value = "/get_items_detail", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponse> getItemDetail(@RequestHeader HttpHeaders headers) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            ItemEntity item = merchantService.getItemDetail(headerDto);
            ServiceResponse serviceResponse = new ServiceResponse("Items has been retrieved successfully");
            serviceResponse.addParam("item", item);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while fetching stores", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_merchants_categories", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponse> findMerchantsCategories(@RequestHeader HttpHeaders headers) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.MERCHANT_ID);
            List<CategoryEntity> categories = merchantService.findMerchantsDefaultCategory(headerDto);
            ServiceResponse serviceResponse = new ServiceResponse("Categories has been retrieved successfully");
            serviceResponse.addParam("categories", categories);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while fetching stores", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }


    @RequestMapping(value = "/get_parent_categories_items", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> findParentCategoriesItems(@RequestBody RequestJsonDto requestJson) {
        try {
            List<CategoryEntity> categories = merchantService.findParentCategoriesItems(requestJson);
            ServiceResponse serviceResponse = new ServiceResponse("Items has been retrieved successfully");
            serviceResponse.addParam("categories", categories);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while fetching stores", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_merchant", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponse> getMerchantById(@RequestHeader HttpHeaders headers) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.MERCHANT_ID);
            MerchantEntity merchantEntity = merchantService.getMerchantById(headerDto);

            ServiceResponse serviceResponse = new ServiceResponse("Merchant retrieved successfully with ID: "+headerDto.getMerchantId());
            serviceResponse.addParam("merchant", merchantEntity);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving merchant: ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_brands_parent_categories", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponse> findBrandsParentCategories(@RequestHeader HttpHeaders headers) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            List<CategoryEntity> categories = merchantService.findParentCategoriesByBrand(headerDto);
            ServiceResponse serviceResponse = new ServiceResponse("Categories has been retrieved successfully");
            serviceResponse.addParam("categories", categories);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while fetching stores", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/change_status", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> changeStatus(@RequestHeader HttpHeaders headers, @RequestBody RequestJsonDto requestJsonDto) {
        try {
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            merchantService.changeStatus(requestJsonDto ,headerDto);
            ServiceResponse serviceResponse = new ServiceResponse("Status has been changed successfully");
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while changing status", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/search_item", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> getWebSearchItem(@RequestBody RequestJsonDto requestJsonDto) {
        try {
            PaginationDto paginationDto = merchantService.getWebItemSearch(requestJsonDto);
            ServiceResponse serviceResponse = new ServiceResponse("Items has been retrieved successfully");
            serviceResponse.addParam("items", paginationDto);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while fetching items", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_orders", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> getOrders(@RequestHeader HttpHeaders headers, @RequestBody RequestJsonDto requestJson) {
        try{
            HeaderDto headerDto = new HeaderDto();
            List<String> hd = headers.get("merchantId");
            if (hd != null && hd.size() > 0)
                headerDto.setMerchantId(Integer.parseInt( hd.get(0)));
            else {
                if(SessionManager.getRole().toString().equals(Role.ROLE_MERCHANT.toString())){
                    headerDto.setMerchantId(SessionManager.getMerchantId());
                } else {
                    headerDto.setMerchantId(null);
                }
            }
            PaginationDto orders = merchantService.getOrders(headerDto, requestJson);
            ServiceResponse serviceResponse = new ServiceResponse("Orders retrieved successfully with Merchant ID: "+headerDto.getMerchantId());
            serviceResponse.addParam("orders", orders);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            //check if exception is YSException then send header as json
            if(e instanceof YSException){
                ServiceResponse serviceResponse = new ServiceResponse("YSException");
                serviceResponse.addParam("error", httpHeaders);
                return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
            }

            GeneralUtil.logError(log, "Error Occurred while retrieving orders: ", e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_purchase_history", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> getPurchaseHistory(@RequestHeader HttpHeaders headers, @RequestBody RequestJsonDto requestJson) {
        try{
            HeaderDto headerDto = new HeaderDto();
            List<String> hd = headers.get("merchantId");
            if (hd != null && hd.size() > 0)
                headerDto.setMerchantId(Integer.parseInt( hd.get(0)));
            else {
                if(SessionManager.getRole().toString().equals(Role.ROLE_MERCHANT.toString())){
                    headerDto.setMerchantId(SessionManager.getMerchantId());
                } else {
                    headerDto.setMerchantId(null);
                }
            }
            PaginationDto orders = merchantService.getPurchaseHistory(headerDto, requestJson);

            ServiceResponse serviceResponse = new ServiceResponse("Purchase History retrieved successfully with Merchant ID: "+headerDto.getMerchantId());
            serviceResponse.addParam("orders", orders);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
             //check if exception is YSException then send header as json
            if(e instanceof YSException){
                ServiceResponse serviceResponse = new ServiceResponse("YSException");
                serviceResponse.addParam("error", httpHeaders);
                return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
            }

            GeneralUtil.logError(log, "Error Occurred while retrieving purchase history: ", e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/add_items_images", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> addItemsImages(@RequestHeader HttpHeaders headers, @RequestBody RequestJsonDto requestJson) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            merchantService.addItemsImages(headerDto, requestJson);

            ServiceResponse serviceResponse = new ServiceResponse("Images has been saved successfully saved for item: "+headerDto.getId());
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving purchase history: ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/add_items_tags", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> addItemsTags(@RequestHeader HttpHeaders headers, @RequestBody RequestJsonDto requestJson) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);
            merchantService.addItemsTags(headerDto, requestJson);

            ServiceResponse serviceResponse = new ServiceResponse("Tags has been saved successfully saved for item: "+headerDto.getId());
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving purchase history: ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/get_orders_items", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> getOrdersItems(@RequestHeader HttpHeaders headers, @RequestBody RequestJsonDto requestJsonDto) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.ID);

             PaginationDto items = merchantService.getOrderItems(headerDto, requestJsonDto);

            ServiceResponse serviceResponse = new ServiceResponse("Purchase History retrieved successfully with Merchant ID: "+headerDto.getMerchantId());
            serviceResponse.addParam("items", items);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while retrieving purchase history: ", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }


    @RequestMapping(value = "/get_invoices", method = RequestMethod.POST)
    public ResponseEntity<ServiceResponse> getInvoices(@RequestHeader HttpHeaders headers, @RequestBody RequestJsonDto requestJsonDto) {
        try{
            HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto, GeneralUtil.MERCHANT_ID);

            PaginationDto invoices = merchantService.getInvoices(headerDto, requestJsonDto);

            ServiceResponse serviceResponse = new ServiceResponse("Invoices retrieved successfully with Merchant ID: "+headerDto.getMerchantId());
            serviceResponse.addParam("invoices", invoices);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
        } catch (Exception e){
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            GeneralUtil.logError(log, "Error Occurred while retrieving Invoices: ", e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }



}
