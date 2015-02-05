package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.dao.inf.AdminDaoService;
import com.yetistep.delivr.dao.inf.CountryDaoService;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.enums.JobOrderStatus;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.service.inf.AdminService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/25/14
 * Time: 10:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class AdminServiceImpl implements AdminService {
    private static final Logger log = Logger.getLogger(AdminServiceImpl.class);

    @Autowired
    CountryDaoService countryDaoService;

    @Autowired
    AdminDaoService adminDaoService;

    @Override
    public List<CountryEntity> findAllCountries() throws Exception {
        return countryDaoService.findAll();
    }


    public List<Map<String, Map<Integer, Map<String, String>>>> getGodsView() throws Exception{
        List<Map<String, Map<Integer, Map<String, String>>>> godsView = new ArrayList<>();
        Map<String, Map<Integer, Map<String, String>>> generalData = new HashMap<>();


        Map<Integer, Map<String, String>> totalUserCount =  new HashMap<>();
        totalUserCount.put(adminDaoService.getRegisteredUserCount(), null);
        generalData.put("Total User Registered", totalUserCount);

        Map<Integer, Map<String, String>> newUserCount =  new HashMap<>();
        newUserCount.put(adminDaoService.getNewUserCount(1), null);
        generalData.put("new User Registered", newUserCount);

        Map<Integer, Map<String, String>> userLeftAppCount =  new HashMap<>();
        userLeftAppCount.put(adminDaoService.getActiveUserCount(), null);
        generalData.put("Total Active User", userLeftAppCount);

        Map<Integer, Map<String, String>> partnerStoresCount =  new HashMap<>();
        partnerStoresCount.put(adminDaoService.getPartnerStoreCount(), null);
        generalData.put("Partner Stores", partnerStoresCount);

        Integer succeedOrderCount = adminDaoService.getOrderCount("(5)");
        Integer failedOrderCount = adminDaoService.getOrderCount("(6)");
        Integer totalOrderCount = succeedOrderCount+failedOrderCount;

        Map<Integer, Map<String, String>> successfulDeliveryPcn =  new HashMap<>();
        if(succeedOrderCount != 0){
            successfulDeliveryPcn.put(succeedOrderCount*100/totalOrderCount, null);
        }else{
            successfulDeliveryPcn.put(0, null);
        }
        generalData.put("Successful Delivery", successfulDeliveryPcn);

        Map<Integer, Map<String, String>> deliveryFailedPcn =  new HashMap<>();
        if(failedOrderCount != 0){
            deliveryFailedPcn.put(failedOrderCount*100/totalOrderCount, null);
        }else{
            deliveryFailedPcn.put(0, null);
        }
        generalData.put("Delivery Failed", deliveryFailedPcn);

        Integer onTimeDeliveryCount = adminDaoService.getOnTimeCount("<=");
        Integer exceedTimeDeliveryCount = adminDaoService.getOnTimeCount(">");
        Integer totalDeliveryCount = onTimeDeliveryCount+exceedTimeDeliveryCount;

        Map<Integer, Map<String, String>> onTimeDeliveryPcn =  new HashMap<>();
        if(onTimeDeliveryCount != 0){
            onTimeDeliveryPcn.put(onTimeDeliveryCount*100/totalDeliveryCount, null);
        } else {
            onTimeDeliveryPcn.put(0, null);
        }
        generalData.put("On time Delivery", onTimeDeliveryPcn);

        Map<Integer, Map<String, String>> exceedTimePcn =  new HashMap<>();
        if(exceedTimeDeliveryCount != 0){
            exceedTimePcn.put(exceedTimeDeliveryCount*100/totalDeliveryCount, null);
        } else {
            exceedTimePcn.put(0, null);
        }
        generalData.put("Time Exceeded Delivery", exceedTimePcn);

        Map<Integer, Map<String, String>> onDutyDBCount =  new HashMap<>();
        onDutyDBCount.put(adminDaoService.getDBoyCount("(1,2)"), null);
        generalData.put("On Duty Delivery Boy", onDutyDBCount);

        Map<Integer, Map<String, String>> offDutyDBCount =  new HashMap<>();
        offDutyDBCount.put(adminDaoService.getDBoyCount("(0)"), null);
        generalData.put("Off Duty Delivery Boy", offDutyDBCount);

        Map<Integer, Map<String, String>> servedTillDateCount =  new HashMap<>();
        Map<String, String> avgDVTime = new HashMap<>();
        if(succeedOrderCount > 0)
            avgDVTime.put("Average Delivery Time", String.valueOf(adminDaoService.getOrderTotalTime()/succeedOrderCount));
        servedTillDateCount.put(succeedOrderCount, avgDVTime);
        generalData.put("Served Till Date", servedTillDateCount);

        Integer todayOrderCount = adminDaoService.getTodayOrderCount();
        Integer todayOrderTotalTime = adminDaoService.getTodayOrderTotalTime();

        Map<Integer, Map<String, String>> completedTodayCount =  new HashMap<>();
        Map<String, String> avgDVTimeToday = new HashMap<>();
        if(todayOrderCount > 0)
            avgDVTimeToday.put("Average Delivery Time", String.valueOf(adminDaoService.getTodayOrderTotalTime()/todayOrderCount));
        completedTodayCount.put(todayOrderCount, avgDVTimeToday);
        generalData.put("Completed Today", completedTodayCount);


        Integer orderInProcess = adminDaoService.getOrderCount("(1,2,3,4)");

        Map<Integer, Map<String, String>> currentDeliveryCount =  new HashMap<>();
        Map<String, String> currentDeliveryTypesCount = new HashMap<>();
        currentDeliveryTypesCount.put("Order Accepted", String.valueOf(adminDaoService.getOrderCount("(1)")));
        currentDeliveryTypesCount.put("In Route To Pick Up", String.valueOf(adminDaoService.getOrderCount("(2)")));
        currentDeliveryTypesCount.put("At Store", String.valueOf(adminDaoService.getOrderCount("(3)")));
        currentDeliveryTypesCount.put("In Route To Delivery", String.valueOf(adminDaoService.getOrderCount("(4)")));
        currentDeliveryCount.put(orderInProcess, currentDeliveryTypesCount);
        generalData.put("Current Delivery", currentDeliveryCount);


        List<JobOrderStatus> status = new ArrayList<>();
        status.add(JobOrderStatus.AT_STORE);
        status.add(JobOrderStatus.IN_ROUTE_TO_DELIVERY);
        status.add(JobOrderStatus.IN_ROUTE_TO_PICK_UP);
        status.add(JobOrderStatus.ORDER_ACCEPTED);

        List<OrderEntity> orders = adminDaoService.getOrderRoute(status);

        Map<Integer, Map<String, String>> newOrdersCount =  new HashMap<>();
        Map<String, String> routes = new HashMap<>();

        Map<Integer, Map<String, String>> storeLocations =  new HashMap<>();
        Map<String, String> storeLatLang = new HashMap<>();

        Map<Integer, Map<String, String>> customerLocations =  new HashMap<>();
        Map<String, String> customerLatLang = new HashMap<>();

        Map<Integer, Map<String, String>> dbLocations =  new HashMap<>();
        Map<String, String> dbLatLang = new HashMap<>();

        for (OrderEntity order: orders){
            StoreEntity store = order.getStore();
            AddressEntity address = order.getAddress();
            DeliveryBoyEntity db = order.getDeliveryBoy();
            routes.put(store.getName()+"("+store.getStreet()+")=> lat:"+store.getLatitude()+", long:"+store.getLongitude(), address.getStreet()+"=> lat:"+address.getLatitude()+", long:"+address.getLongitude());
            storeLatLang.put(store.getLatitude(), store.getLongitude());
            customerLatLang.put(address.getLatitude(), address.getLongitude());
            dbLatLang.put(db.getLatitude(), db.getLongitude());

        }
        newOrdersCount.put(orderInProcess, routes);
        generalData.put("New Orders", newOrdersCount);

        storeLocations.put(storeLatLang.size(), storeLatLang);
        generalData.put("Store Locations", storeLocations);

        customerLocations.put(customerLatLang.size(), customerLatLang);
        generalData.put("Customer Locations", storeLocations);

        dbLocations.put(dbLatLang.size(), dbLatLang);
        generalData.put("DB Locations", dbLocations);

        godsView.add(generalData);
        return  godsView;

    }

    @Override
    public List<Map<String, Map<Integer, Integer>>> getDeliveryGraphByDate(HeaderDto headerDto) throws Exception{
        List<Map<String, Map<Integer, Integer>>> graphData = new ArrayList<>();
        Map<Integer, Integer> deliveryByDate = new HashMap<>();
        Map<Integer, Integer> averageTime = new HashMap<>();
        Integer count;
        if(headerDto.getId().equals("Day")) {
            count = 8;
            Integer i;
            for ( i=1; i< count; i++){
                Integer orderCount = adminDaoService.getOrderByDayCount(i, i-1);
                Integer totalTime = adminDaoService.getOrderTotalTimeByDay(i, i-1);
                deliveryByDate.put(i, orderCount);
                if(orderCount > 0)
                    averageTime.put(i, totalTime/orderCount);
            }
        }

        if(headerDto.getId().equals("Week")) {
            count = 5;
            Integer i;
            for ( i=1; i< count; i++){
                Integer orderCount = adminDaoService.getOrderByDayCount(i*7, 7*(i-1));
                Integer totalTime = adminDaoService.getOrderTotalTimeByDay(i*7, 7*(i-1));
                deliveryByDate.put(i, orderCount);
                if(orderCount > 0)
                    averageTime.put(i, totalTime/orderCount);
            }
        }

        if(headerDto.getId().equals("Month")) {
            count = 13;
            Integer i;
            for ( i=1; i< count; i++){
                Integer orderCount = adminDaoService.getOrderByDayCount(i*30, 30*(i-1));
                Integer totalTime = adminDaoService.getOrderTotalTimeByDay(i*30, 30*(i-1));
                deliveryByDate.put(i, orderCount);
                if(orderCount > 0)
                    averageTime.put(i, totalTime/orderCount);
            }
        }

        if(headerDto.getId().equals("Year")) {
            count = 10;
            Integer i;
            for ( i=1; i< count; i++){
                Integer orderCount = adminDaoService.getOrderByDayCount(i*365, 365*(i-1));
                Integer totalTime = adminDaoService.getOrderTotalTimeByDay(i*365, 365*(i-1));
                deliveryByDate.put(i, orderCount);
                if(orderCount > 0)
                    averageTime.put(i, totalTime/orderCount);
            }
        }
        Map<String, Map<Integer, Integer>> deliveryByDateMap = new HashMap<>();
        Map<String, Map<Integer, Integer>> averageTimeMap = new HashMap<>();
        deliveryByDateMap.put("orderCount", deliveryByDate);
        averageTimeMap.put("averageTime", averageTime);
        graphData.add(deliveryByDateMap);
        graphData.add(averageTimeMap);

        return graphData;
    }

    @Override
    public Map<Integer, Integer> getNewUserGraph(HeaderDto headerDto) throws Exception{
        Map<Integer, Integer> graphData = new HashMap<>();
        Integer count;
        if(headerDto.getId().equals("Day")) {
            count = 8;
            Integer i;
            for ( i=1; i< count; i++){
                Integer orderCount = adminDaoService.getNewUserByDayCount(i, i - 1);
                graphData.put(i, orderCount);
            }
        }

        if(headerDto.getId().equals("Week")) {
            count = 5;
            Integer i;
            for ( i=1; i< count; i++){
                Integer orderCount = adminDaoService.getNewUserByDayCount(i * 7, 7 * (i - 1));
                graphData.put(i, orderCount);
            }
        }

        if(headerDto.getId().equals("Month")) {
            count = 13;
            Integer i;
            for ( i=1; i< count; i++){
                Integer orderCount = adminDaoService.getNewUserByDayCount(i * 30, 30 * (i - 1));
                graphData.put(i, orderCount);
            }
        }

        if(headerDto.getId().equals("Year")) {
            count = 10;
            Integer i;
            for ( i=1; i< count; i++){
                Integer orderCount = adminDaoService.getNewUserByDayCount(i * 365, 365 * (i - 1));
                graphData.put(i, orderCount);

            }
        }

        return graphData;
    }

    @Override
    public List<Map<String, Map<Integer, Integer>>> getDeliverySuccessGraph(HeaderDto headerDto) throws Exception{
        List<Map<String, Map<Integer, Integer>>> graphData = new ArrayList<>();
        Map<Integer, Integer> successfulPcn = new HashMap<>();
        Map<Integer, Integer> failedPcn = new HashMap<>();
        Integer count;
        if(headerDto.getId().equals("Day")) {
            count = 8;
            Integer i;
            for ( i=1; i< count; i++){
                Integer successDvCount = adminDaoService.getOrderByDayCount("(5)", i, i - 1);
                Integer failedDvCount = adminDaoService.getOrderByDayCount("(5)", i, i-1);
                Integer totalDvCount = successDvCount+failedDvCount;
                if(successDvCount > 0)
                    successfulPcn.put(i, successDvCount*100/totalDvCount);
                else
                    successfulPcn.put(i, 0);
                if(failedDvCount > 0)
                    failedPcn.put(i, failedDvCount*100/totalDvCount);
                else
                    failedPcn.put(i, 0);
            }
        }

        if(headerDto.getId().equals("Week")) {
            count = 5;
            Integer i;
            for ( i=1; i< count; i++){
                Integer successDvCount = adminDaoService.getOrderByDayCount("(5)", i * 7, 7 * (i - 1));
                Integer failedDvCount = adminDaoService.getOrderByDayCount("(5)", i*7, 7*(i-1));
                Integer totalDvCount = successDvCount+failedDvCount;
                if(successDvCount > 0)
                    successfulPcn.put(i, successDvCount*100/totalDvCount);
                else
                    successfulPcn.put(i, 0);
                if(failedDvCount > 0)
                    failedPcn.put(i, failedDvCount*100/totalDvCount);
                else
                    failedPcn.put(i, 0);
            }
        }

        if(headerDto.getId().equals("Month")) {
            count = 13;
            Integer i;
            for ( i=1; i< count; i++){
                Integer successDvCount = adminDaoService.getOrderByDayCount("(5)", i * 30, 30 * (i - 1));
                Integer failedDvCount = adminDaoService.getOrderByDayCount("(5)", i*30, 30*(i-1));
                Integer totalDvCount = successDvCount+failedDvCount;
                if(successDvCount > 0)
                    successfulPcn.put(i, successDvCount*100/totalDvCount);
                else
                    successfulPcn.put(i, 0);
                if(failedDvCount > 0)
                    failedPcn.put(i, failedDvCount*100/totalDvCount);
                else
                    failedPcn.put(i, 0);
            }
        }

        if(headerDto.getId().equals("Year")) {
            count = 10;
            Integer i;
            for ( i=1; i< count; i++){
                Integer successDvCount = adminDaoService.getOrderByDayCount("(5)", i * 365, 365 * (i - 1));
                Integer failedDvCount = adminDaoService.getOrderByDayCount("(5)", i*365, 365*(i-1));
                Integer totalDvCount = successDvCount+failedDvCount;
                if(successDvCount > 0)
                    successfulPcn.put(i, successDvCount*100/totalDvCount);
                else
                    successfulPcn.put(i, 0);
                if(failedDvCount > 0)
                    failedPcn.put(i, failedDvCount*100/totalDvCount);
                else
                    failedPcn.put(i, 0);

            }
        }
        Map<String, Map<Integer, Integer>> successfulPcnMap = new HashMap<>();
        Map<String, Map<Integer, Integer>> failedPcnMap = new HashMap<>();
        successfulPcnMap.put("Successful", successfulPcn);
        failedPcnMap.put("Failed", failedPcn);
        graphData.add(successfulPcnMap);
        graphData.add(failedPcnMap);
        return graphData;
    }



}
