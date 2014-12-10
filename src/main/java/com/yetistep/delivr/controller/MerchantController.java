package com.yetistep.delivr.controller;

import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.model.DeliveryBoyEntity;
import com.yetistep.delivr.model.StoreEntity;
import com.yetistep.delivr.service.inf.MerchantService;
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
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 3:23 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/merchant")
public class MerchantController {
    @Autowired
    MerchantService merchantService;

    private static final Logger log = Logger.getLogger(ManagerController.class);

    @RequestMapping(value = "/save_store", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ServiceResponse> saveStore(@RequestHeader HttpHeaders headers, @RequestBody List<StoreEntity> stores) {
        try {
            /*HeaderDto headerDto = new HeaderDto();
            GeneralUtil.fillHeaderCredential(headers, headerDto);*/
            /*
           private Integer id;
            private StoresBrandsEntity storesBrand;
            private Set<CategoryEntity> category = new HashSet<CategoryEntity>();
            private Set<ItemsStoreEntity> itemsStore = new HashSet<ItemsStoreEntity>();
            private Set<OrderEntity> order = new HashSet<OrderEntity>();
            private String name;
            private String street;
            private String locality;
            private String city;
            private String country;
            private String contact_no;
            private BigDecimal latitude;
            private BigDecimal longitude;
            private Timestamp created_date;
            private String return_policy;
            private Integer delivery_fee;
            private String promo_code;
            private BigDecimal vat;
            private BigDecimal service_charge;
        * */

            merchantService.saveStore(stores, headers);

            ServiceResponse serviceResponse = new ServiceResponse("Store has been saved successfully");
//            HttpHeaders httpHeaders = new HttpHeaders();
//            httpHeaders.add("deliveryBoyId", deliveryBoy.getUser().getId() + "");
//            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.CREATED);

        } catch (Exception e) {
            GeneralUtil.logError(log, "Error Occurred while creating delivery boy", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.BAD_REQUEST);
        }
    }

}
