package com.yetistep.delivr.service.impl;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONObject;
import com.yetistep.delivr.dao.inf.AdminDaoService;
import com.yetistep.delivr.dao.inf.CountryDaoService;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.enums.DBoyStatus;
import com.yetistep.delivr.enums.DeliveryStatus;
import com.yetistep.delivr.enums.JobOrderStatus;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.service.inf.AdminService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import java.util.*;

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
        generalData.put("totalUserRegistered", totalUserCount);

        Map<Integer, Map<String, String>> newUserCount =  new HashMap<>();
        newUserCount.put(adminDaoService.getNewUserCount(1), null);
        generalData.put("newUserRegistered", newUserCount);

        Map<Integer, Map<String, String>> userLeftAppCount =  new HashMap<>();
        userLeftAppCount.put(adminDaoService.getActiveUserCount(), null);
        generalData.put("totalActiveUser", userLeftAppCount);

        Map<Integer, Map<String, String>> partnerStoresCount =  new HashMap<>();
        partnerStoresCount.put(adminDaoService.getPartnerStoreCount(), null);
        generalData.put("partnerStores", partnerStoresCount);

        Integer succeedOrderCount = adminDaoService.getOrderCount(JobOrderStatus.DELIVERED.ordinal());
        Integer failedOrderCount = adminDaoService.getOrderCount(JobOrderStatus.CANCELLED.ordinal());
        Integer totalOrderCount = succeedOrderCount+failedOrderCount;

        Map<Integer, Map<String, String>> successfulDeliveryPcn =  new HashMap<>();
        if(succeedOrderCount != 0){
            successfulDeliveryPcn.put(succeedOrderCount*100/totalOrderCount, null);
        }else{
            successfulDeliveryPcn.put(0, null);
        }
        generalData.put("successfulDelivery", successfulDeliveryPcn);

        Map<Integer, Map<String, String>> deliveryFailedPcn =  new HashMap<>();
        if(failedOrderCount != 0){
            deliveryFailedPcn.put(failedOrderCount*100/totalOrderCount, null);
        }else{
            deliveryFailedPcn.put(0, null);
        }
        generalData.put("deliveryFailed", deliveryFailedPcn);

        Integer onTimeDeliveryCount = adminDaoService.getOnTimeCount("<=");
        Integer exceedTimeDeliveryCount = adminDaoService.getOnTimeCount(">");
        Integer totalDeliveryCount = onTimeDeliveryCount+exceedTimeDeliveryCount;

        Map<Integer, Map<String, String>> onTimeDeliveryPcn =  new HashMap<>();
        if(onTimeDeliveryCount != 0){
            onTimeDeliveryPcn.put(onTimeDeliveryCount*100/totalDeliveryCount, null);
        } else {
            onTimeDeliveryPcn.put(0, null);
        }
        generalData.put("onTimeDelivery", onTimeDeliveryPcn);

        Map<Integer, Map<String, String>> exceedTimePcn =  new HashMap<>();
        if(exceedTimeDeliveryCount != 0){
            exceedTimePcn.put(exceedTimeDeliveryCount*100/totalDeliveryCount, null);
        } else {
            exceedTimePcn.put(0, null);
        }
        generalData.put("timeExceededDelivery", exceedTimePcn);

        Map<Integer, Map<String, String>> onDutyDBCount =  new HashMap<>();
        List<Integer> statuses = new ArrayList<>();
        statuses.add(DBoyStatus.FREE.ordinal());
        statuses.add(DBoyStatus.BUSY.ordinal());
        onDutyDBCount.put(adminDaoService.getDBoyCount(statuses), null);
        generalData.put("onDutyDeliveryBoy", onDutyDBCount);

        Map<Integer, Map<String, String>> offDutyDBCount =  new HashMap<>();
        List<Integer> offDutyStatus = new ArrayList<>();
        offDutyStatus.add(DBoyStatus.NOT_AVAILABLE.ordinal());

        offDutyDBCount.put(adminDaoService.getDBoyCount(offDutyStatus), null);
        generalData.put("offDutyDeliveryBoy", offDutyDBCount);

        Map<Integer, Map<String, String>> servedTillDateCount =  new HashMap<>();
        Map<String, String> avgDVTime = new HashMap<>();
        if(succeedOrderCount > 0)
            avgDVTime.put("averageDeliveryTime", String.valueOf(adminDaoService.getOrderTotalTime()/succeedOrderCount));
        servedTillDateCount.put(succeedOrderCount, avgDVTime);
        generalData.put("servedTillDate", servedTillDateCount);

        Integer todayOrderCount = adminDaoService.getTodayOrderCount();
        Integer todayOrderTotalTime = adminDaoService.getTodayOrderTotalTime();

        Map<Integer, Map<String, String>> completedTodayCount =  new HashMap<>();
        Map<String, String> avgDVTimeToday = new HashMap<>();
        if(todayOrderCount > 0)
            avgDVTimeToday.put("averageDeliveryTime", String.valueOf(adminDaoService.getTodayOrderTotalTime()/todayOrderCount));

        completedTodayCount.put(todayOrderCount, avgDVTimeToday);
        generalData.put("completedToday", completedTodayCount);

        List<Integer> orderStatuses = new ArrayList<>();
        orderStatuses.add(JobOrderStatus.AT_STORE.ordinal());
        orderStatuses.add(JobOrderStatus.ORDER_ACCEPTED.ordinal());
        orderStatuses.add(JobOrderStatus.IN_ROUTE_TO_PICK_UP.ordinal());
        orderStatuses.add(JobOrderStatus.IN_ROUTE_TO_DELIVERY.ordinal());

        Integer orderInProcess = adminDaoService.getOrderCount(orderStatuses);

        Map<Integer, Map<String, String>> currentDeliveryCount =  new HashMap<>();
        Map<String, String> currentDeliveryTypesCount = new HashMap<>();
        currentDeliveryTypesCount.put("orderAccepted", String.valueOf(adminDaoService.getOrderCount(JobOrderStatus.ORDER_ACCEPTED.ordinal())));
        currentDeliveryTypesCount.put("inRouteToPickUp", String.valueOf(adminDaoService.getOrderCount(JobOrderStatus.IN_ROUTE_TO_PICK_UP.ordinal())));
        currentDeliveryTypesCount.put("atStore", String.valueOf(adminDaoService.getOrderCount(JobOrderStatus.AT_STORE.ordinal())));
        currentDeliveryTypesCount.put("inRouteToDelivery", String.valueOf(adminDaoService.getOrderCount(JobOrderStatus.IN_ROUTE_TO_DELIVERY.ordinal())));
        currentDeliveryCount.put(orderInProcess, currentDeliveryTypesCount);
        generalData.put("currentDelivery", currentDeliveryCount);


        List<JobOrderStatus> routeStatuses = new ArrayList<>();
        routeStatuses.add(JobOrderStatus.AT_STORE);
        routeStatuses.add(JobOrderStatus.IN_ROUTE_TO_DELIVERY);
        routeStatuses.add(JobOrderStatus.IN_ROUTE_TO_PICK_UP);
        routeStatuses.add(JobOrderStatus.ORDER_ACCEPTED);

        List<OrderEntity> orders = adminDaoService.getOrderRoute(routeStatuses);

        Map<Integer, Map<String, String>> newOrdersCount =  new HashMap<>();
        Map<String, String> routes = new HashMap<>();

        Map<Integer, Map<String, String>> storeLocations =  new HashMap<>();
        Map<String, String> storeLatLang = new HashMap<>();

        Map<Integer, Map<String, String>> customerLocations =  new HashMap<>();
        Map<String, String> customerLatLang = new HashMap<>();

        Map<Integer, Map<String, String>> dbLocations =  new HashMap<>();
        Map<String, String> dbLatLang = new HashMap<>();

        Integer i=0;
        for (OrderEntity order: orders){
            StoreEntity store = order.getStore();
            AddressEntity address = order.getAddress();
            DeliveryBoyEntity db = order.getDeliveryBoy();
            JSONObject jo = new JSONObject();
            JSONObject storeObj = new JSONObject();
            JSONObject customerObj = new JSONObject();
            JSONObject dBoyObj = new JSONObject();

            storeObj.put("name", store.getName());
            storeObj.put("address", store.getStreet());
            storeObj.put("lat", store.getLatitude());
            storeObj.put("lang", store.getLongitude());

            customerObj.put("name", address.getUser().getFullName());
            customerObj.put("address", address.getStreet());
            customerObj.put("lat", address.getLatitude());
            customerObj.put("lang", address.getLongitude());

            if(db!=null) {
                dBoyObj.put("name", db.getUser().getFullName());
                dBoyObj.put("lat", db.getLatitude());
                dBoyObj.put("lang", db.getLongitude());
            }

            jo.put("store", storeObj);
            jo.put("customer", customerObj);
            jo.put("courierBoy", dBoyObj);
            routes.put(i.toString(), jo.toString());

            storeLatLang.put(i.toString(), storeObj.toString());
            customerLatLang.put(i.toString(), customerObj.toString());
            dbLatLang.put(i.toString(), dBoyObj.toString());
           i++;
        }
        newOrdersCount.put(orderInProcess, routes);
        generalData.put("newOrders", newOrdersCount);

        storeLocations.put(storeLatLang.size(), storeLatLang);
        generalData.put("storeLocations", storeLocations);

        customerLocations.put(customerLatLang.size(), customerLatLang);
        generalData.put("customerLocations", customerLocations);

        dbLocations.put(dbLatLang.size(), dbLatLang);
        generalData.put("deliveryBoyLocations", dbLocations);

        godsView.add(generalData);
        return  godsView;

    }

    @Override
    public List<Map<String, Map<String, Integer>>> getDeliveryGraphByDate(HeaderDto headerDto) throws Exception{
        List<Map<String, Map<String, Integer>>> graphData = new ArrayList<>();
        Map<String, Integer> deliveryByDate = new HashMap<>();
        Map<String, Integer> totalTime = new HashMap<>();
        Integer count;
        if(headerDto.getId().equals("1Month")) {
            deliveryByDate = adminDaoService.getOrderByDayCount(30);
            totalTime  = adminDaoService.getOrderTotalTimeByDay(30);
        }

        if(headerDto.getId().equals("3Month")) {
            deliveryByDate = adminDaoService.getOrderByDayCount(90);
            totalTime = adminDaoService.getOrderTotalTimeByDay(90);
        }

        if(headerDto.getId().equals("Year")) {
            deliveryByDate = adminDaoService.getOrderByDayCount(365);
            totalTime = adminDaoService.getOrderTotalTimeByDay(365);
        }

        if(headerDto.getId().equals("All")) {
            deliveryByDate = adminDaoService.getOrderByDayCount(3*365);
            totalTime = adminDaoService.getOrderTotalTimeByDay(3*365);
        }
        Map<String, Map<String, Integer>> deliveryByDateMap = new HashMap<>();
        Map<String, Map<String, Integer>> averageTimeMap = new HashMap<>();
        deliveryByDateMap.put("orderCount", deliveryByDate);
        averageTimeMap.put("totalTime", totalTime);
        graphData.add(deliveryByDateMap);
        graphData.add(averageTimeMap);

        return graphData;
    }

    @Override
    public Map<String, Integer> getNewUserGraph(HeaderDto headerDto) throws Exception{
        Map<String, Integer> graphData = new TreeMap<>();
        Integer count;
        if(headerDto.getId().equals("1Month")) {
            graphData = adminDaoService.getNewUserByDayCount(30);
        }

        if(headerDto.getId().equals("3Month")) {
            graphData = adminDaoService.getNewUserByDayCount(90);
        }

        if(headerDto.getId().equals("Year")) {
            graphData = adminDaoService.getNewUserByDayCount(365);
        }

        if(headerDto.getId().equals("All")) {
            graphData = adminDaoService.getNewUserByDayCount(3*365);
        }

        return graphData;
    }

    @Override
    public Map<String, Map<String, Integer>> getDeliverySuccessGraph(HeaderDto headerDto) throws Exception{
        Map<String, Map<String, Integer>> graphData = new HashMap<>();
        List<Integer> successOrderStatuses = new ArrayList<>();
        successOrderStatuses.add(JobOrderStatus.DELIVERED.ordinal());

        List<Integer> failedOrderStatuses = new ArrayList<>();
        failedOrderStatuses.add(JobOrderStatus.CANCELLED.ordinal());
        Map<String, Integer> successDvCount = new TreeMap<>();
        Map<String, Integer> failedDvCount = new TreeMap<>();

        if(headerDto.getId().equals("1Month")) {
            successDvCount = adminDaoService.getOrderByDayCount(successOrderStatuses, 30);
            failedDvCount = adminDaoService.getOrderByDayCount(failedOrderStatuses, 30);
        }

        if(headerDto.getId().equals("3Month")) {
            successDvCount = adminDaoService.getOrderByDayCount(successOrderStatuses, 90);
            failedDvCount = adminDaoService.getOrderByDayCount(failedOrderStatuses, 90);
        }

        if(headerDto.getId().equals("Year")) {
            successDvCount = adminDaoService.getOrderByDayCount(successOrderStatuses, 365);
            failedDvCount = adminDaoService.getOrderByDayCount(failedOrderStatuses, 365);
        }

        if(headerDto.getId().equals("All")) {
            successDvCount = adminDaoService.getOrderByDayCount(successOrderStatuses, 3*365);
            failedDvCount = adminDaoService.getOrderByDayCount(failedOrderStatuses, 3*365);
        }
        graphData.put("Successful", successDvCount);
        graphData.put("Failed", failedDvCount);
        return graphData;
    }

    @Override
    public Map<String, Map<String, Integer>> getOnTimeDeliveryGraph(HeaderDto headerDto) throws Exception {
        Map<String, Map<String, Integer>> graphData = new HashMap<>();

        Map<String, Integer> onTimeDelivery = new TreeMap<>();
        Map<String, Integer> exceededTimeDelivery = new TreeMap<>();

        if(headerDto.getId().equals("1Month")) {
            onTimeDelivery = adminDaoService.onTimeDeliveryCount(30, "<=");
            exceededTimeDelivery = adminDaoService.onTimeDeliveryCount(30, ">");
        }

        if(headerDto.getId().equals("3Month")) {
            onTimeDelivery = adminDaoService.onTimeDeliveryCount(90, "<=");
            exceededTimeDelivery = adminDaoService.onTimeDeliveryCount(90, ">");
        }

        if(headerDto.getId().equals("Year")) {
            onTimeDelivery = adminDaoService.onTimeDeliveryCount(365, "<=");
            exceededTimeDelivery = adminDaoService.onTimeDeliveryCount(365, ">");
        }

        if(headerDto.getId().equals("All")) {
            onTimeDelivery = adminDaoService.onTimeDeliveryCount(3*365, "<=");
            exceededTimeDelivery = adminDaoService.onTimeDeliveryCount(3*365, ">");
        }
        graphData.put("onTimeDelivery", onTimeDelivery);
        graphData.put("exceededTimeDelivery", exceededTimeDelivery);
        return graphData;
    }
}
