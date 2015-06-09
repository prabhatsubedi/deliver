package com.yetistep.delivr.service.v2.impl;

import com.yetistep.delivr.abs.AbstractManager;
import com.yetistep.delivr.dao.inf.*;
import com.yetistep.delivr.dto.PaginationDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.enums.*;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.service.inf.*;
import com.yetistep.delivr.service.v2.inf.DeliveryBoyService;
import com.yetistep.delivr.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeliveryBoyServiceImpl extends AbstractManager implements DeliveryBoyService {
    private static final Logger log = Logger.getLogger(DeliveryBoyServiceImpl.class);

    private static final BigDecimal minusOne = new BigDecimal(-1);

    @Autowired
    DeliveryBoyDaoService deliveryBoyDaoServiceV2;

    @Autowired
    SystemPropertyService systemPropertyService;

    @Override
    public PaginationDto findAllDeliverBoy(RequestJsonDto requestJsonDto) throws Exception {
        log.info("Retrieving list of Deliver Boys");


        Page page = requestJsonDto.getPage();

        Page pageForCount = new Page();
        pageForCount.setSearchFor(page.getSearchFor());

        if(page != null && page.getSearchFor() != null && !page.getSearchFor().equals("")){
            Map<String, String> fieldsMap = new HashMap<>();
            fieldsMap.put("user", "fullName,mobileNumber,emailAddress");
            pageForCount.setSearchFields(fieldsMap);
            page.setSearchFields(fieldsMap);
        }



        PaginationDto paginationDto = new PaginationDto();
        Integer totalRows =  deliveryBoyDaoServiceV2.getTotalNumberOfDboys(pageForCount);
        paginationDto.setNumberOfRows(totalRows);
        if(page != null){
            page.setTotalRows(totalRows);
        }

        List<DeliveryBoyEntity> deliveryBoyEntities = deliveryBoyDaoServiceV2.findAll(page);
        /*For filtering role -- set to null as all delivery boy has same role*/

        List<DeliveryBoyEntity> objects = new ArrayList<>();

        String fields = "id,availabilityStatus,averageRating,bankAmount,walletAmount,availableAmount,advanceAmount,user,order,latitude,longitude,bankAccountNumber,lastLocationUpdate";

        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("user", "id,fullName,mobileNumber,emailAddress,status");
        assoc.put("order", "id,orderName,assignedTime,orderStatus,dBoyOrderHistories");

        subAssoc.put("dBoyOrderHistories", "orderAcceptedAt");

        for (DeliveryBoyEntity deliveryBoyEntity:deliveryBoyEntities){
            DeliveryBoyEntity deliveryBoy = (DeliveryBoyEntity) ReturnJsonUtil.getJsonObject(deliveryBoyEntity, fields, assoc, subAssoc);
            Integer locationUpdateTimeOut = Integer.parseInt(systemPropertyService.readPrefValue(PreferenceType.LOCATION_UPDATE_TIMEOUT_IN_MIN));
            if(deliveryBoy.getLastLocationUpdate() != null){
                if(deliveryBoy.getLastLocationUpdate().after(new Date(System.currentTimeMillis()-(locationUpdateTimeOut*60*1000)))){
                    deliveryBoy.setOutOfReach(false);
                } else {
                    deliveryBoy.setOutOfReach(true);
                }
            } else  {
                deliveryBoy.setOutOfReach(true);
            }

            List<JobOrderStatus> activeStatuses = new ArrayList<>();
            activeStatuses.add(JobOrderStatus.AT_STORE);
            activeStatuses.add(JobOrderStatus.ORDER_ACCEPTED);
            activeStatuses.add(JobOrderStatus.IN_ROUTE_TO_DELIVERY);
            activeStatuses.add(JobOrderStatus.IN_ROUTE_TO_PICK_UP);
            List<OrderEntity> currentOrders = new ArrayList<>();
            for(OrderEntity order: deliveryBoy.getOrder()){
                 if(activeStatuses.contains(order.getOrderStatus())){
                     if(order.getdBoyOrderHistories().size() > 0){
                         Double minuteDiff = DateUtil.getMinDiff(System.currentTimeMillis(), order.getdBoyOrderHistories().get(0).getOrderAcceptedAt().getTime());
                         order.setElapsedTime(minuteDiff.intValue());
                     }
                     currentOrders.add(order);
                 }
            }
            deliveryBoy.setOrder(currentOrders);
            objects.add(deliveryBoy);
        }

          paginationDto.setData(objects);
        return paginationDto;
    }


}
