package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.abs.AbstractManager;
import com.yetistep.delivr.dao.inf.*;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.OrderSummaryDto;
import com.yetistep.delivr.dto.PaginationDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.enums.*;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.model.mobile.dto.OrderInfoDto;
import com.yetistep.delivr.model.mobile.dto.PastDeliveriesDto;
import com.yetistep.delivr.model.mobile.dto.PreferenceDto;
import com.yetistep.delivr.service.inf.*;
import com.yetistep.delivr.util.*;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Timestamp;
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
    DeliveryBoyDaoService deliveryBoyDaoService;

    @Autowired
    UserDaoService userDaoService;

    @Autowired
    OrderDaoService orderDaoService;

    @Autowired
    DeliveryBoySelectionDaoService deliveryBoySelectionDaoService;

    @Autowired
    DBoyOrderHistoryDaoService dBoyOrderHistoryDaoService;

    @Autowired
    ItemsOrderDaoService itemsOrderDaoService;

    @Autowired
    MerchantDaoService merchantDaoService;

    @Autowired
    SystemPropertyService systemPropertyService;

    @Autowired
    SystemAlgorithmService systemAlgorithmService;

    @Autowired
    ReasonDetailsDaoService reasonDetailsDaoService;

    @Autowired
    ItemDaoService itemDaoService;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    ItemsOrderAttributeDaoService itemsOrderAttributeDaoService;

    @Autowired
    UserDeviceDaoService userDeviceDaoService;

    @Autowired
    CustomerService customerService;

    @Autowired
    CustomerDaoService customerDaoService;

    @Autowired
    AccountService accountService;

    @Autowired
    DBoyAdvanceAmountDaoService dBoyAdvanceAmountDaoService;

    @Override
    public void saveDeliveryBoy(DeliveryBoyEntity deliveryBoy, HeaderDto headerDto) throws Exception {
        log.info("++++++++++++++++++ Creating Shopper +++++++++++++++++");
        deliveryBoy.getUser().setUsername(headerDto.getUsername());
        deliveryBoy.getUser().setPassword(headerDto.getPassword());
        UserEntity user = deliveryBoy.getUser();
        if ((user.getUsername() == null || user.getPassword() == null) || (user.getUsername().isEmpty() || user.getPassword().isEmpty()))
            throw new YSException("VLD009");
        if(user.getEmailAddress() != null && !user.getEmailAddress().isEmpty()){
            if(userDaoService.checkIfEmailExists(user.getEmailAddress(), Role.ROLE_DELIVERY_BOY.toInt())){
                throw new YSException("VLD026");
            }
        }
        if(userDaoService.checkIfMobileNumberExists(user.getUsername())){
            throw new YSException("VLD027");
        }
        if(deliveryBoyDaoService.checkIfLicenseNumberExists(deliveryBoy.getLicenseNumber())){
            throw new YSException("VLD034");
        }
        if(deliveryBoyDaoService.checkIfBankNumberExists(deliveryBoy.getLicenseNumber())){
            throw new YSException("VLD041");
        }


        user.setPassword(GeneralUtil.encryptPassword(user.getPassword()));

        RoleEntity userRole = userDaoService.getRoleByRole(Role.ROLE_DELIVERY_BOY);

        /** Setting Default values **/
        user.setRole(userRole);
        user.setBlacklistStatus(false);
        user.setMobileVerificationStatus(true);
        user.setVerifiedStatus(true);
        user.setSubscribeNewsletter(false);
        user.setStatus(Status.ACTIVE);
        user.setCreatedDate(DateUtil.getCurrentTimestampSQL());
        if(user.getEmailAddress().isEmpty()){
            user.setEmailAddress(null);
        }

        deliveryBoy.setAvailabilityStatus(DBoyStatus.NOT_AVAILABLE);
        deliveryBoy.setAverageRating(new BigDecimal(0));
        deliveryBoy.setTotalOrderTaken(0);
        deliveryBoy.setTotalOrderDelivered(0);
        deliveryBoy.setTotalOrderUndelivered(0);
        deliveryBoy.setTotalEarnings(new BigDecimal(0));
        deliveryBoy.setActiveOrderNo(0);

        deliveryBoy.setBankAmount(BigDecimal.ZERO);
        deliveryBoy.setWalletAmount(BigDecimal.ZERO);
        deliveryBoy.setAdvanceAmount(BigDecimal.ZERO);
        deliveryBoy.setAvailableAmount(BigDecimal.ZERO);
        deliveryBoy.setPreviousDue(BigDecimal.ZERO);

        String profileImage = deliveryBoy.getUser().getProfileImage();
        deliveryBoy.getUser().setProfileImage(null);

        for (AddressEntity address: deliveryBoy.getUser().getAddresses()){
            address.setUser(deliveryBoy.getUser());
        }

        deliveryBoyDaoService.save(deliveryBoy);
        if (profileImage != null && !profileImage.isEmpty()) {
            log.info("Uploading Profile Image of delivery boy to S3 Bucket ");
            String dir = MessageBundle.separateString("/", "DBoys", "DBoy_" + deliveryBoy.getId());
            boolean isLocal = MessageBundle.isLocalHost();
            String imageName = "pimg" + (isLocal ? "_tmp_" : "_") + deliveryBoy.getId()+System.currentTimeMillis();
            String s3Path = GeneralUtil.saveImageToBucket(profileImage, imageName, dir, true);
            deliveryBoy.getUser().setProfileImage(s3Path);
            deliveryBoyDaoService.update(deliveryBoy);
        }

    }

    @Override
    public DeliveryBoyEntity findDeliveryBoyById(HeaderDto headerDto) throws Exception {
        Integer id = Integer.parseInt(headerDto.getId());
        log.info("Retrieving Deliver Boy With ID:" + id);
        DeliveryBoyEntity deliveryBoyEntity = deliveryBoyDaoService.find(id);
        if (deliveryBoyEntity == null) {
            throw new YSException("VLD011");
        }

        String fields = "id,availabilityStatus,averageRating,bankAmount,walletAmount,advanceAmount,previousDue,vehicleType,licenseNumber,vehicleNumber,user,latitude,longitude,order,bankAccountNumber";

        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("user", "id,fullName,mobileNumber,emailAddress,profileImage,gender,status,addresses");
        assoc.put("order", "store,customer,orderStatus");
        subAssoc.put("store", "name,latitude,longitude,street,city,state,country");
        subAssoc.put("customer", "latitude,longitude,user");
        subAssoc.put("user", "id,fullName");
        subAssoc.put("addresses", "id,latitude,longitude,street,city,state,country");

        List<OrderEntity> activeOrders = new ArrayList<>();
        List<JobOrderStatus> activeStatuses = new ArrayList<>();
        activeStatuses.add(JobOrderStatus.ORDER_ACCEPTED);
        activeStatuses.add(JobOrderStatus.IN_ROUTE_TO_PICK_UP);
        activeStatuses.add(JobOrderStatus.AT_STORE);
        activeStatuses.add(JobOrderStatus.IN_ROUTE_TO_DELIVERY);
        DeliveryBoyEntity deliveryBoy = ((DeliveryBoyEntity) ReturnJsonUtil.getJsonObject(deliveryBoyEntity, fields, assoc, subAssoc));
        for (OrderEntity order: deliveryBoy.getOrder()){
            if(activeStatuses.contains(order.getOrderStatus())){
                activeOrders.add(order);
            }
        }

        deliveryBoy.setOrder(activeOrders);

        Timestamp lastAckDate = dBoyAdvanceAmountDaoService.getLatestAckTimestamp(deliveryBoy.getId());

        BigDecimal cancelledPurchaseTotal = BigDecimal.ZERO;
        //if(lastAckDate != null){
          List<OrderEntity> cancelledPurchasedOrders =  orderDaoService.getCancelledPurchasedOrder(deliveryBoy.getId(), lastAckDate);
            for (OrderEntity order: cancelledPurchasedOrders){
                Boolean itemPurchased = false;
                List<ItemsOrderEntity> itemsOrders = order.getItemsOrder();
                for(ItemsOrderEntity itemsOrder: itemsOrders){
                    if(itemsOrder.getPurchaseStatus() != null && itemsOrder.getPurchaseStatus()){
                        itemPurchased = true;
                        break;
                    }
                }
                if(itemPurchased)
                    cancelledPurchaseTotal =  cancelledPurchaseTotal.add(order.getGrandTotal());
            }
        //}

        deliveryBoy.setItemReturnedTotal(cancelledPurchaseTotal);


        return deliveryBoy;
    }

    @Override
    public List<DeliveryBoyEntity> findAllActiveDeliveryBoy() throws Exception{
        List<DeliveryBoyEntity> allActiveDBoy = new ArrayList<>();
        List<DeliveryBoyEntity> dbDeliveryBoy = deliveryBoyDaoService.findAll();

        for (DeliveryBoyEntity deliveryBoy: dbDeliveryBoy){
            if (deliveryBoy.getUser().getStatus().equals(Status.ACTIVE)){
                allActiveDBoy.add(deliveryBoy);
            }
        }

        return allActiveDBoy;
    }


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
        Integer totalRows =  deliveryBoyDaoService.getTotalNumberOfDboys(pageForCount);
        paginationDto.setNumberOfRows(totalRows);
        if(page != null){
            page.setTotalRows(totalRows);
        }

        List<DeliveryBoyEntity> deliveryBoyEntities = deliveryBoyDaoService.findAll(page);
        /*For filtering role -- set to null as all delivery boy has same role*/

        List<DeliveryBoyEntity> objects = new ArrayList<>();

        String fields = "id,availabilityStatus,averageRating,bankAmount,walletAmount,availableAmount,advanceAmount,user,order,latitude,longitude,bankAccountNumber,lastLocationUpdate";

        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("user", "id,fullName,mobileNumber,emailAddress,status");
        /*assoc.put("order", "id,orderName,assignedTime,orderStatus,dBoyOrderHistories");

        subAssoc.put("dBoyOrderHistories", "orderAcceptedAt,jobStartedAt");*/

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

            List<OrderEntity> dbCurrentOrders = orderDaoService.getShoppersLiveOrders(deliveryBoyEntity.getId());
            List<OrderEntity> currentOrders = new ArrayList<>();
            for (OrderEntity currentOrder: dbCurrentOrders){

                   String orderFields = "id,orderName,assignedTime,orderStatus,dBoyOrderHistories";
                   Map<String, String> orderAssoc = new HashMap<>();
                   orderAssoc.put("dBoyOrderHistories", "orderAcceptedAt,jobStartedAt");
                   OrderEntity order = (OrderEntity) ReturnJsonUtil.getJsonObject(currentOrder, orderFields, orderAssoc);

                    if(order.getdBoyOrderHistories().size() > 0){
                        Double minuteDiff = DateUtil.getMinDiff(System.currentTimeMillis(), order.getdBoyOrderHistories().get(0).getOrderAcceptedAt().getTime());
                        order.setElapsedTime(minuteDiff.intValue());
                    }
                    currentOrders.add(order);

            }

            /*List<JobOrderStatus> activeStatuses = new ArrayList<>();
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
            }*/
            deliveryBoy.setOrder(currentOrders);
            objects.add(deliveryBoy);
        }

        paginationDto.setData(objects);
        return paginationDto;
    }

    @Override
    public Boolean updateDeliveryBoy(DeliveryBoyEntity deliveryBoyEntity, HeaderDto headerDto) throws Exception {
        DeliveryBoyEntity dBoyEntity = deliveryBoyDaoService.find(deliveryBoyEntity.getId());
        if (dBoyEntity == null) {
            throw new YSException("VLD011");
        }

        if(userDaoService.checkIfEmailExists(deliveryBoyEntity.getUser().getEmailAddress(), Role.ROLE_DELIVERY_BOY.toInt(), dBoyEntity.getUser().getId())){
            throw new YSException("VLD026");
        }

        if(userDaoService.checkIfMobileNumberExists(deliveryBoyEntity.getUser().getMobileNumber(), dBoyEntity.getUser().getId())){
            throw new YSException("VLD027");
        }

        if(deliveryBoyDaoService.checkIfLicenseNumberExists(deliveryBoyEntity.getLicenseNumber(), dBoyEntity.getId())){
            throw new YSException("VLD034");
        }

        if(deliveryBoyDaoService.checkIfBankNumberExists(deliveryBoyEntity.getBankAccountNumber(), dBoyEntity.getId())){
            throw new YSException("VLD041");
        }



        dBoyEntity.getUser().setUsername(headerDto.getUsername());
        if(headerDto.getPassword() != null)
        dBoyEntity.getUser().setPassword(GeneralUtil.encryptPassword(headerDto.getPassword()));

        dBoyEntity.getUser().setMobileNumber(headerDto.getUsername());
        List<AddressEntity> addressEntities =  deliveryBoyEntity.getUser().getAddresses();
        for(AddressEntity addressEntity: addressEntities){
            for(AddressEntity address: dBoyEntity.getUser().getAddresses()){
                if(address.getId().equals(addressEntity.getId())){
                   address.setStreet(addressEntity.getStreet());
                   address.setCity(addressEntity.getCity());
                   address.setState(addressEntity.getState());
                   address.setCountry(addressEntity.getCountry());
                   address.setCountryCode(addressEntity.getCountryCode());
                   address.setUser(dBoyEntity.getUser());
                   break;
                }
            }
        }

        dBoyEntity.getUser().setUsername(deliveryBoyEntity.getUser().getMobileNumber());
        dBoyEntity.getUser().setFullName(deliveryBoyEntity.getUser().getFullName());
        dBoyEntity.getUser().setEmailAddress(deliveryBoyEntity.getUser().getEmailAddress());
        dBoyEntity.getUser().setMobileNumber(deliveryBoyEntity.getUser().getMobileNumber());
        dBoyEntity.getUser().setGender(deliveryBoyEntity.getUser().getGender());
        dBoyEntity.getUser().setStatus(deliveryBoyEntity.getUser().getStatus());

        dBoyEntity.setVehicleNumber(deliveryBoyEntity.getVehicleNumber());
        dBoyEntity.setVehicleType(deliveryBoyEntity.getVehicleType());
        dBoyEntity.setLicenseNumber(deliveryBoyEntity.getLicenseNumber());
        dBoyEntity.setBankAccountNumber(deliveryBoyEntity.getBankAccountNumber());

        String profileImage = deliveryBoyEntity.getUser().getProfileImage();

        deliveryBoyDaoService.update(dBoyEntity);

        if (profileImage != null && !profileImage.isEmpty()) {

            log.info("Deleting Profile Image of delivery boy to S3 Bucket ");
            AmazonUtil.deleteFileFromBucket(AmazonUtil.getAmazonS3Key(dBoyEntity.getUser().getProfileImage()));

            log.info("Uploading Profile Image of delivery boy to S3 Bucket ");

            String dir = MessageBundle.separateString("/", "DBoys", "DBoy_" + dBoyEntity.getId());
            boolean isLocal = MessageBundle.isLocalHost();
            String imageName = "pimg" + (isLocal ? "_tmp_" : "_") + dBoyEntity.getId()+System.currentTimeMillis();
            String s3Path = GeneralUtil.saveImageToBucket(profileImage, imageName, dir, true);
            dBoyEntity.getUser().setProfileImage(s3Path);
            deliveryBoyDaoService.update(dBoyEntity);
        }

        return true;
    }

    @Override
    public Boolean updateDeliveryBoyStatus(DeliveryBoyEntity deliveryBoyEntity) throws Exception {
        log.info("Updating Status of Shopper to:" + deliveryBoyEntity.getAvailabilityStatus());
        DeliveryBoyEntity dBoyEntity = deliveryBoyDaoService.find(deliveryBoyEntity.getId());
        if (dBoyEntity == null) {
            throw new YSException("VLD011");
        }
        dBoyEntity.setAvailabilityStatus(deliveryBoyEntity.getAvailabilityStatus());
        return deliveryBoyDaoService.update(dBoyEntity);
    }

    @Override
    public DeliveryBoyEntity dboyLogin(HeaderDto headerDto, UserDeviceEntity userDevice) throws Exception {
        log.info("+++++++++++++++ Checking DBOY Credential +++++++++++++++");
        UserEntity userEntity = userDaoService.findByUserName(headerDto.getUsername());
        if(userEntity == null)
            throw new YSException("VLD011");
        if(!userEntity.getStatus().equals(Status.ACTIVE)){
            throw new YSException("VLD029");
        }
        GeneralUtil.matchDBPassword(headerDto.getPassword(), userEntity.getPassword());

        /* Updating User Device Information */
        if(userDevice != null){
            String userAgent = httpServletRequest.getHeader("User-Agent");
            UserAgent ua = UserAgent.parseUserAgentString(userAgent);
            String family = ua.getOperatingSystem().name();
            UserDeviceEntity userDeviceEntity = null;
            if(userEntity.getUserDevice() != null){
                userDeviceEntity = userEntity.getUserDevice();
            }else{
                userDeviceEntity = new UserDeviceEntity();
                userDeviceEntity.setUser(userEntity);
                userEntity.setUserDevice(userDeviceEntity);
            }
            userDeviceDaoService.removeInformationForSameDevice(userDevice.getUuid(), userEntity.getId());
            userDeviceEntity.setUuid(userDevice.getUuid());
            if(userDevice.getDeviceToken() != null && !userDevice.getDeviceToken().isEmpty())
                userDeviceEntity.setDeviceToken(userDevice.getDeviceToken());
            /* TODO Family for family, familyName, name */
            userDeviceEntity.setFamily(family);
            userDeviceEntity.setFamilyName(family);
            userDeviceEntity.setName(family);
            userDeviceEntity.setBrand(userDevice.getBrand());
            userDeviceEntity.setModel(userDevice.getModel());
            userDeviceEntity.setDpi(userDevice.getDpi());
            userDeviceEntity.setHeight(userDevice.getHeight());
            userDeviceEntity.setWidth(userDevice.getWidth());
            userDeviceEntity.setLastLogin(DateUtil.getCurrentTimestampSQL());
            userDaoService.update(userEntity);
        }
        String fields = "id,availabilityStatus,averageRating,totalOrderTaken,totalOrderDelivered,totalOrderUndelivered,totalEarnings," +
                "vehicleType,activeOrderNo,licenseNumber,vehicleNumber,user";
        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();
        assoc.put("user", "id,username,fullName,gender,mobileNumber,mobileVerificationStatus,emailAddress," +
                "profileImage,blacklistStatus,verifiedStatus,subscribeNewsletter,lastActivityDate,createdDate,addresses");
        subAssoc.put("addresses", "id,street,city,state,country,countryCode");

        return (DeliveryBoyEntity) ReturnJsonUtil.getJsonObject(userEntity.getDeliveryBoy(), fields, assoc , subAssoc);
    }

    @Override
    public List<DeliveryBoyEntity> getAllCapableDeliveryBoys() throws Exception {
        int timeInMin = Integer.parseInt(systemPropertyService.readPrefValue(PreferenceType.LOCATION_UPDATE_TIMEOUT_IN_MIN));
        return deliveryBoyDaoService.findAllCapableDeliveryBoys(timeInMin);
    }

    @Override
    public Boolean updateLocationOfDeliveryBoy(DeliveryBoyEntity deliveryBoyEntity) throws Exception {
        log.info("Updating location of Shopper with ID:"+deliveryBoyEntity.getId());
        if(deliveryBoyEntity.getId() == null)
            throw new YSException("VLD011");
        DeliveryBoyEntity dBoyEntity = deliveryBoyDaoService.find(deliveryBoyEntity.getId());
        if (dBoyEntity == null) {
            throw new YSException("VLD011");
        }
        GeoCodingUtil.getLatLong(deliveryBoyEntity.getLatitude(), deliveryBoyEntity.getLongitude());
        dBoyEntity.setLatitude(deliveryBoyEntity.getLatitude());
        dBoyEntity.setLongitude(deliveryBoyEntity.getLongitude());
        dBoyEntity.setLastLocationUpdate(DateUtil.getCurrentTimestampSQL());
        return deliveryBoyDaoService.update(dBoyEntity);
    }

    @Override
    public List<OrderInfoDto> getActiveOrders(Integer deliveryBoyId) throws Exception{
        List<OrderInfoDto> orderEntities = orderDaoService.getActiveOrdersList(deliveryBoyId);
        for(OrderInfoDto orderInfoDto: orderEntities){
            updateRemainingAndElapsedTime(orderInfoDto);
        }
        List<OrderInfoDto> assignedOrders = orderDaoService.getAssignedOrders(deliveryBoyId);
        orderEntities.addAll(assignedOrders);
        this.setOrderPriorities(orderEntities);
        return orderEntities;
    }

    /* This method is used to set order priorities. */
    private void setOrderPriorities(List<OrderInfoDto> orderInfoDtos) {
        int i = 1;
       for(OrderInfoDto orderInfo: orderInfoDtos){
          orderInfo.setPriority(i);
           i++;
       }
    }

    private void updateRemainingAndElapsedTime(OrderInfoDto orderInfoDto){
        Double minuteDiff = DateUtil.getMinDiff(System.currentTimeMillis(), orderInfoDto.getOrderDate().getTime());
        int remainingTime = orderInfoDto.getRemainingTime() - minuteDiff.intValue();
        orderInfoDto.setElapsedTime(minuteDiff.intValue());
        remainingTime = (remainingTime < 0) ? 0 : remainingTime;
        orderInfoDto.setRemainingTime(remainingTime);
    }

    @Override
    public Boolean changeOrderStatus(OrderEntity orderEntityJson, Integer deliveryBoyId) throws Exception{
        OrderEntity order = orderDaoService.find(orderEntityJson.getId());
        if(order == null){
            throw new YSException("VLD017");
        }
        JobOrderStatus orderStatus = orderEntityJson.getOrderStatus();
        if(orderStatus.equals(JobOrderStatus.IN_ROUTE_TO_PICK_UP) ||
                orderStatus.equals(JobOrderStatus.AT_STORE) ||
                orderStatus.equals(JobOrderStatus.IN_ROUTE_TO_DELIVERY)){
            if(!deliveryBoyDaoService.checkForPendingOrders(deliveryBoyId, orderEntityJson.getId())){
                throw new YSException("DBY004");
            }
        }
        if(orderStatus.equals(JobOrderStatus.ORDER_ACCEPTED)){
            if(order.getOrderStatus().equals(JobOrderStatus.ORDER_ACCEPTED)){
                throw new YSException("ORD001");
            }
            return acceptDeliveryOrder(orderEntityJson.getId(), deliveryBoyId);
        }else if(orderStatus.equals(JobOrderStatus.IN_ROUTE_TO_PICK_UP)){
            return startJob(order, orderEntityJson, deliveryBoyId);
        }else if(orderStatus.equals(JobOrderStatus.AT_STORE)){
            //Such as bill upload and Bill edit
            return reachedStore(order, deliveryBoyId);
        }else if(orderStatus.equals(JobOrderStatus.IN_ROUTE_TO_DELIVERY)){
             return goRouteToDelivery(order, deliveryBoyId);
        }else if(orderStatus.equals(JobOrderStatus.DELIVERED)){
            return deliverOrder(orderEntityJson, order, deliveryBoyId);
        }else if(orderStatus.equals(JobOrderStatus.CANCELLED)){
            //reason
        }else{
            throw new YSException("ORD002");
        }
//        order.setOrderStatus(orderStatus);
//        orderDaoService.update(order);
        return true;
    }

    private Boolean acceptDeliveryOrder(Integer orderId, Integer deliveryBoyId) throws Exception {
            log.info("Accepting Order By delivery Boy with ID:"+deliveryBoyId);

            DeliveryBoySelectionEntity deliveryBoySelectionEntity = deliveryBoySelectionDaoService.getSelectionDetails(orderId, deliveryBoyId);
            if(deliveryBoySelectionEntity == null){
                throw new YSException("ORD003");
            }

            /* Returns true if order can be accepted else returns false */
            boolean orderAcceptance = deliveryBoySelectionDaoService.checkOrderAcceptedStatus(orderId);
            if(orderAcceptance){
                if(deliveryBoySelectionEntity.getOrder().getOrderStatus().equals(JobOrderStatus.CANCELLED)){
                    throw new YSException("ORD006");
                }
                deliveryBoySelectionEntity.setAccepted(true);
                DeliveryBoyEntity deliveryBoyEntity = deliveryBoySelectionEntity.getDeliveryBoy();
                if(deliveryBoyEntity.getActiveOrderNo() >= Integer.parseInt(systemPropertyService.readPrefValue(PreferenceType.LIVE_ORDER_COUNT_FOR_SHOPPER))){
                    throw new YSException("DBY003");
                }
                deliveryBoyEntity.setActiveOrderNo(deliveryBoyEntity.getActiveOrderNo()+1);
                deliveryBoyEntity.setTotalOrderTaken(deliveryBoyEntity.getTotalOrderTaken()+1);
               /* deliveryBoyEntity.setTotalOrderUndelivered(deliveryBoyEntity.getTotalOrderUndelivered()+1);*/
                deliveryBoyEntity.setAvailabilityStatus(DBoyStatus.BUSY);

                OrderEntity orderEntity = deliveryBoySelectionEntity.getOrder();
                orderEntity.setRemainingTime(deliveryBoySelectionEntity.getTotalTimeRequired());
                orderEntity.setDeliveryBoy(deliveryBoyEntity);
                orderEntity.setAssignedTime(deliveryBoySelectionEntity.getTimeRequired());
                orderEntity.setSystemChargeableDistance(deliveryBoySelectionEntity.getDistanceToStore());
                orderEntity.setOrderStatus(JobOrderStatus.ORDER_ACCEPTED);
                MerchantEntity merchant = merchantDaoService.getMerchantByOrderId(orderId);
                if(merchant == null){
                     throw new YSException("MRC003");
                }
                CourierTransactionEntity courierTransaction =  systemAlgorithmService.getCourierTransaction(orderEntity, deliveryBoySelectionEntity, orderEntity.getCommissionAmount(), orderEntity.getStore().getStoresBrand().getProcessingCharge());
                CourierTransactionEntity courierTransactionEntity = orderEntity.getCourierTransaction();
                courierTransactionEntity.setOrderTotal(courierTransaction.getOrderTotal());
                courierTransactionEntity.setAdditionalDeliveryAmt(courierTransaction.getAdditionalDeliveryAmt());
                courierTransactionEntity.setCustomerDiscount(courierTransaction.getCustomerDiscount());
                courierTransactionEntity.setDeliveryCostWithoutAdditionalDvAmt(courierTransaction.getDeliveryCostWithoutAdditionalDvAmt());
                courierTransactionEntity.setSystemProcessingChargeAmount(courierTransaction.getSystemProcessingChargeAmount());
                courierTransactionEntity.setDeliveryChargedBeforeDiscount(courierTransaction.getDeliveryChargedBeforeDiscount());
                courierTransactionEntity.setCustomerBalanceBeforeDiscount(courierTransaction.getCustomerBalanceBeforeDiscount());
                courierTransactionEntity.setDeliveryChargedAfterDiscount(courierTransaction.getDeliveryChargedAfterDiscount());
                courierTransactionEntity.setCustomerBalanceAfterDiscount(courierTransaction.getCustomerBalanceAfterDiscount());
                courierTransactionEntity.setCustomerPays(courierTransaction.getCustomerPays());
                courierTransactionEntity.setPaidToCourier(courierTransaction.getPaidToCourier());
                courierTransactionEntity.setProfit(courierTransaction.getProfit());
                courierTransactionEntity.setCourierToStoreDistance(courierTransaction.getCourierToStoreDistance());
                courierTransactionEntity.setStoreToCustomerDistance(courierTransaction.getStoreToCustomerDistance());
                orderEntity.setCourierTransaction(courierTransactionEntity);

                List<DBoyOrderHistoryEntity> dBoyOrderHistoryEntities = new ArrayList<DBoyOrderHistoryEntity>();
                DBoyOrderHistoryEntity dBoyOrderHistoryEntity = new DBoyOrderHistoryEntity();
                dBoyOrderHistoryEntity.setDeliveryBoy(deliveryBoyEntity);
                dBoyOrderHistoryEntity.setOrder(orderEntity);
                dBoyOrderHistoryEntity.setOrderAcceptedAt(DateUtil.getCurrentTimestampSQL());
                dBoyOrderHistoryEntity.setDistanceTravelled(BigDecimal.ZERO);
                dBoyOrderHistoryEntity.setDeliveryStatus(DeliveryStatus.PENDING);
                dBoyOrderHistoryEntity.setAmountEarned(BigDecimal.ZERO);
                dBoyOrderHistoryEntities.add(dBoyOrderHistoryEntity);
                orderEntity.setdBoyOrderHistories(dBoyOrderHistoryEntities);

                Boolean status = orderDaoService.update(orderEntity);
                if(status){
                    UserDeviceEntity userDevice = userDeviceDaoService.getUserDeviceInfoFromOrderId(orderId);
                    String message = MessageBundle.getPushNotificationMsg("CPN001");
                    message = String.format(message, orderEntity.getStore().getName(), deliveryBoyEntity.getUser().getFullName());
                    String extraDetail = orderId.toString()+"/status/"+JobOrderStatus.ORDER_ACCEPTED.toString();
                    PushNotificationUtil.sendPushNotification(userDevice, message, NotifyTo.CUSTOMER, PushNotificationRedirect.ORDER, extraDetail);

                    List<String> deviceTokens = userDeviceDaoService.getDeviceTokensExceptAcceptedDeliveryBoy(orderId, deliveryBoyId);
                    if(deviceTokens.size() > 0){
                        PushNotification pushNotification = new PushNotification();
                        pushNotification.setTokens(deviceTokens);
                        pushNotification.setMessage(MessageBundle.getPushNotificationMsg("PN002"));
                        pushNotification.setPushNotificationRedirect(PushNotificationRedirect.ORDER);
                        pushNotification.setExtraDetail(orderId.toString()+"/status/"+JobOrderStatus.ORDER_ACCEPTED.toString());
                        pushNotification.setNotifyTo(NotifyTo.DELIVERY_BOY);
                        PushNotificationUtil.sendNotificationToAndroidDevice(pushNotification);
                    }

                    List<DeliveryBoySelectionEntity> deliveryBoySelectionEntityList = deliveryBoySelectionDaoService.getAllSelectionDetails(orderId, deliveryBoyId);
                    for(DeliveryBoySelectionEntity dBoySelection: deliveryBoySelectionEntityList){
                        if(deliveryBoySelectionDaoService.getCountOfDeliveryBoyForOrder(dBoySelection.getOrder().getId(), deliveryBoyId) == 0){
                            customerService.reprocessOrder(dBoySelection.getOrder().getId());
                        }else{
                            dBoySelection.setRejected(true);
                            deliveryBoySelectionDaoService.update(dBoySelection);
                        }
                    }

                    /*
                    * if email subscription is set true
                    * send the email containing order detail to the contact person of the store
                    * */

                     if (orderEntity.getStore().getSendEmail() != null && orderEntity.getStore().getSendEmail()){
                        String subject = "New order has been placed : "+orderEntity.getId();
                        String body = EmailMsg.orderPlaced(orderEntity.getStore().getContactPerson(), getServerUrl(), orderEntity);
                        sendMail(orderEntity.getStore().getEmail(), body, subject);
                    }
                }
                return status;
            } else if (deliveryBoyId.equals(deliveryBoySelectionEntity.getOrder().getDeliveryBoy().getId())) {
                throw new YSException("ORD005");
            }
            throw new YSException("ORD001");

    }

    private Boolean startJob(OrderEntity order, OrderEntity orderJson, Integer deliveryBoyId) throws Exception{
        if(!order.getDeliveryBoy().getId().equals(deliveryBoyId)){
            throw new YSException("ORD003");
        }
        if(!deliveryBoyDaoService.canStartJob(order.getId(), deliveryBoyId)){
            throw new YSException("DBY005");
        }
        JobOrderStatus.traverseJobStatus(order.getOrderStatus(), JobOrderStatus.IN_ROUTE_TO_PICK_UP);
        order.setOrderStatus(JobOrderStatus.IN_ROUTE_TO_PICK_UP);
        List<DBoyOrderHistoryEntity> orderHistoryEntities = order.getdBoyOrderHistories();
        for(DBoyOrderHistoryEntity dBoyOrderHistoryEntity: orderHistoryEntities){
            if(dBoyOrderHistoryEntity.getDeliveryBoy().getId().equals(deliveryBoyId)){
                if(orderJson.getDeliveryBoy() != null){
                    dBoyOrderHistoryEntity.setStartLatitude(orderJson.getDeliveryBoy().getLatitude());
                    dBoyOrderHistoryEntity.setStartLongitude(orderJson.getDeliveryBoy().getLongitude());
                }
                dBoyOrderHistoryEntity.setJobStartedAt(DateUtil.getCurrentTimestampSQL());
                break;
            }
        }
        boolean status = orderDaoService.update(order);
        if(status){
            UserDeviceEntity userDevice = userDeviceDaoService.getUserDeviceInfoFromOrderId(order.getId());
            String message = MessageBundle.getMessage("CPN002","push_notification.properties");
            String extraDetail = order.getId().toString()+"/status/"+JobOrderStatus.IN_ROUTE_TO_PICK_UP.toString();
            PushNotificationUtil.sendPushNotification(userDevice, message, NotifyTo.CUSTOMER, PushNotificationRedirect.ORDER, extraDetail);
        }
        return status;
    }

    private Boolean reachedStore(OrderEntity order, Integer deliveryBoyId) throws Exception {
        if(!order.getDeliveryBoy().getId().equals(deliveryBoyId)){
            throw new YSException("ORD003");
        }
        JobOrderStatus.traverseJobStatus(order.getOrderStatus(), JobOrderStatus.AT_STORE);
        order.setOrderStatus(JobOrderStatus.AT_STORE);
        List<DBoyOrderHistoryEntity> orderHistoryEntities = order.getdBoyOrderHistories();
        for(DBoyOrderHistoryEntity dBoyOrderHistoryEntity: orderHistoryEntities){
            if(dBoyOrderHistoryEntity.getDeliveryBoy().getId().equals(deliveryBoyId)){
                dBoyOrderHistoryEntity.setDistanceTravelled(order.getSystemChargeableDistance());
                dBoyOrderHistoryEntity.setReachedStoreAt(DateUtil.getCurrentTimestampSQL());
                break;
            }
        }
        boolean status = orderDaoService.update(order);
        if(status){
            UserDeviceEntity userDevice = userDeviceDaoService.getUserDeviceInfoFromOrderId(order.getId());
            String message = MessageBundle.getMessage("CPN003","push_notification.properties");
            String extraDetail = order.getId().toString()+"/status/"+JobOrderStatus.AT_STORE.toString();
            PushNotificationUtil.sendPushNotification(userDevice, message, NotifyTo.CUSTOMER, PushNotificationRedirect.ORDER, extraDetail);
        }
        return status;
    }

    /*private Boolean goRouteToDelivery(OrderEntity order, Integer deliveryBoyId) throws Exception {
        if(!order.getDeliveryBoy().getId().equals(deliveryBoyId)){
            throw new YSException("ORD003");
        }
        JobOrderStatus.traverseJobStatus(order.getOrderStatus(), JobOrderStatus.IN_ROUTE_TO_DELIVERY);
        if(itemsOrderDaoService.getNumberOfUnprocessedItems(order.getId()) > 0){
           throw new YSException("ORD018");
        }
        if(BigDecimalUtil.isLessThen(order.getTotalCost(), order.getStore().getStoresBrand().getMinOrderAmount())){
            throw new YSException("CRT008", " "+order.getStore().getStoresBrand().getMinOrderAmount());
        }
        if(BigDecimalUtil.isLessThenOrEqualTo(order.getTotalCost(), BigDecimal.ZERO)){
            throw new YSException("ORD016");
        }


        Boolean PROFIT_CHECK = GeneralUtil.parseBoolean(systemPropertyService.readPrefValue(PreferenceType.PROFIT_CHECK_FLAG));
        if(PROFIT_CHECK){
            BigDecimal MINIMUM_PROFIT_PERCENTAGE = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.MINIMUM_PROFIT_PERCENTAGE));
            if(BigDecimalUtil.isLessThen(order.getCourierTransaction().getProfit(), BigDecimalUtil.percentageOf(order.getTotalCost(), MINIMUM_PROFIT_PERCENTAGE))){
                log.warn("No Profit Condition:"+order.getId());
                throw new YSException("ORD016");
            }
        }
        order.setOrderStatus(JobOrderStatus.IN_ROUTE_TO_DELIVERY);
        boolean partnerShipStatus = order.getPartnershipStatus(); //merchantDaoService.findPartnerShipStatusFromOrderId(order.getId());
        courierBoyAccountingsAfterTakingOrder(order.getDeliveryBoy(), order, partnerShipStatus);
        List<OrderEntity> orderEntities = new ArrayList<OrderEntity>();

        *//* Wallet Integration *//*
        boolean status = false;
        if(order.getPaymentMode().equals(PaymentMode.WALLET)){
            String currency = systemPropertyService.readPrefValue(PreferenceType.CURRENCY);
            BigDecimal customerWalletAmount = order.getCustomer().getWalletAmount();
            BigDecimal paidFromWallet = order.getPaidFromWallet();
            *//* Taking reference for difference in price from paidFromCOD *//*
            BigDecimal paidFromCOD = order.getPaidFromCOD();
            if(BigDecimalUtil.isEqualTo(paidFromCOD, BigDecimal.ZERO)){
                //No Change in item price
                log.info("No Change in item price for order ID:"+order.getId());
            }else if(BigDecimalUtil.isGreaterThen(paidFromCOD, BigDecimal.ZERO)){//Increase in price
                *//*Customer wallet is greater than or equal to paidFromCOD *//*
                log.info("Increase in item price for order ID:"+order.getId());
                if(BigDecimalUtil.isGreaterThenOrEqualTo(customerWalletAmount, paidFromCOD)){
                    log.info("customerWalletAmount is greater:"+customerWalletAmount+" Paid from COD:"+paidFromCOD);
                    String remarks = MessageBundle.getMessage("WTM003", "push_notification.properties");
                    remarks = String.format(remarks, currency, paidFromCOD, order.getId());
                    this.setWalletTransaction(order, paidFromCOD, AccountType.DEBIT, order.getPaymentMode(), remarks, customerWalletAmount.subtract(paidFromCOD));

                    customerWalletAmount = customerWalletAmount.subtract(paidFromCOD);
                    paidFromWallet = paidFromWallet.add(paidFromCOD);
                    paidFromCOD = BigDecimal.ZERO;
                }else{
                    *//* Customer Wallet is not enough to pay *//*
                    if(BigDecimalUtil.isGreaterThen(customerWalletAmount, BigDecimal.ZERO)){
                        log.info("customerWalletAmount is lesser:"+customerWalletAmount+" Paid from COD:"+paidFromCOD);
                        String remarks = MessageBundle.getMessage("WTM003", "push_notification.properties");
                        remarks = String.format(remarks, currency, customerWalletAmount, order.getId());
                        this.setWalletTransaction(order, customerWalletAmount, AccountType.DEBIT, order.getPaymentMode(), remarks, BigDecimal.ZERO);

                        paidFromCOD = paidFromCOD.subtract(customerWalletAmount);
                        paidFromWallet = paidFromWallet.add(customerWalletAmount);
                        customerWalletAmount = BigDecimal.ZERO;
                    }
                }
            }else if(BigDecimalUtil.isLessThen(paidFromCOD, BigDecimal.ZERO)){
                log.info("Decrease in item price for order ID:"+order.getId());
                String remarks = MessageBundle.getMessage("WTM002", "push_notification.properties");
                remarks = String.format(remarks, currency, paidFromCOD.abs(), order.getId());
                this.setWalletTransaction(order, paidFromCOD.abs(), AccountType.CREDIT, order.getPaymentMode(), remarks, customerWalletAmount.add(paidFromCOD.abs()));

                paidFromWallet = paidFromWallet.subtract(paidFromCOD.abs());
                customerWalletAmount = customerWalletAmount.add(paidFromCOD.abs());
                paidFromCOD = BigDecimal.ZERO;
                *//*Look for next orders since customer wallet amount is increased*//*
            }
            order.setPaidFromCOD(paidFromCOD);
            order.setPaidFromWallet(paidFromWallet);
            order.getCustomer().setWalletAmount(customerWalletAmount);
            status = orderDaoService.update(order);
            *//* Look for next orders only if Customer Wallet Amount is greater than zero. *//*
            if(status && BigDecimalUtil.isGreaterThen(customerWalletAmount, BigDecimal.ZERO)){
                adjustBalanceOfOtherOrders(order, customerWalletAmount);
            }

        }else{
            status = orderDaoService.update(order);
        }
        *//* Wallet Integration completed*//*
        if(status){
            UserDeviceEntity userDevice = userDeviceDaoService.getUserDeviceInfoFromOrderId(order.getId());
            String message = MessageBundle.getMessage("CPN004","push_notification.properties");
            String extraDetail = order.getId().toString()+"/status/"+JobOrderStatus.IN_ROUTE_TO_DELIVERY.toString();
            PushNotificationUtil.sendPushNotification(userDevice, message, NotifyTo.CUSTOMER, PushNotificationRedirect.ORDER, extraDetail);
        }
        return status;
    }*/

    private Boolean goRouteToDelivery(OrderEntity order, Integer deliveryBoyId) throws Exception {
        if(!order.getDeliveryBoy().getId().equals(deliveryBoyId)){
            throw new YSException("ORD003");
        }
        JobOrderStatus.traverseJobStatus(order.getOrderStatus(), JobOrderStatus.IN_ROUTE_TO_DELIVERY);
        if(itemsOrderDaoService.getNumberOfUnprocessedItems(order.getId()) > 0){
            throw new YSException("ORD018");
        }
        if(BigDecimalUtil.isLessThen(order.getTotalCost(), order.getStore().getStoresBrand().getMinOrderAmount())){
            throw new YSException("CRT008", " "+order.getStore().getStoresBrand().getMinOrderAmount());
        }
        if(BigDecimalUtil.isLessThenOrEqualTo(order.getTotalCost(), BigDecimal.ZERO)){
            throw new YSException("ORD016");
        }


        Boolean PROFIT_CHECK = GeneralUtil.parseBoolean(systemPropertyService.readPrefValue(PreferenceType.PROFIT_CHECK_FLAG));
        if(PROFIT_CHECK){
            BigDecimal MINIMUM_PROFIT_PERCENTAGE = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.MINIMUM_PROFIT_PERCENTAGE));
            if(BigDecimalUtil.isLessThen(order.getCourierTransaction().getProfit(), BigDecimalUtil.percentageOf(order.getTotalCost(), MINIMUM_PROFIT_PERCENTAGE))){
                log.warn("No Profit Condition:"+order.getId());
                throw new YSException("ORD016");
            }
        }
        order.setOrderStatus(JobOrderStatus.IN_ROUTE_TO_DELIVERY);
        boolean partnerShipStatus = order.getPartnershipStatus(); //merchantDaoService.findPartnerShipStatusFromOrderId(order.getId());
        courierBoyAccountingsAfterTakingOrder(order.getDeliveryBoy(), order, partnerShipStatus);

        //calculate paidFromCod and paid from wallet
       /* if(BigDecimalUtil.isGreaterThenOrEqualTo(BigDecimalUtil.checkNull(order.getCustomer().getWalletAmount()),order.getGrandTotal())){
            order.setPaidFromWallet(order.getGrandTotal());
            order.setPaidFromCOD(BigDecimal.ZERO);
        } else if(BigDecimalUtil.isGreaterThen(BigDecimalUtil.checkNull(order.getCustomer().getWalletAmount()), BigDecimal.ZERO)){
            BigDecimal paidFromWallet = order.getCustomer().getWalletAmount();
            BigDecimal paidFromCOD = order.getGrandTotal().subtract(paidFromWallet);
            order.setPaidFromWallet(paidFromWallet);
            order.setPaidFromCOD(paidFromCOD);
        } else {
            order.setPaidFromWallet(BigDecimal.ZERO);
            order.setPaidFromCOD(order.getGrandTotal());
        }*/
        OrderPaymentUtil.calculateOrderPayment(order);

        /* Wallet Integration */
        if(BigDecimalUtil.isGreaterThenZero(order.getPaidFromWallet()))  {
            order.getCustomer().setWalletAmount(order.getCustomer().getWalletAmount().subtract(order.getPaidFromWallet()));
            order.getCustomer().setRmBonusAmount(BigDecimalUtil.checkNull(order.getCustomer().getRmBonusAmount()).subtract(BigDecimalUtil.checkNull(order.getPaidBonusAmount())));
            order.getCustomer().setRmCashBackAmount(BigDecimalUtil.checkNull(order.getCustomer().getRmCashBackAmount()).subtract(BigDecimalUtil.checkNull(order.getPaidCashBackAmount())));
            order.getCustomer().setRmFundTransferAmount(BigDecimalUtil.checkNull(order.getCustomer().getRmFundTransferAmount()).subtract(BigDecimalUtil.checkNull(order.getPaidFundTransferAmount())));

            List<WalletTransactionEntity> walletTransactionEntities = new ArrayList<WalletTransactionEntity>();
            WalletTransactionEntity walletTransactionEntity = new WalletTransactionEntity();
            walletTransactionEntity.setTransactionDate(DateUtil.getCurrentTimestampSQL());
            walletTransactionEntity.setAccountType(AccountType.DEBIT);
            String currency = systemPropertyService.readPrefValue(PreferenceType.CURRENCY);
            String remarks = MessageBundle.getMessage("WTM001", "push_notification.properties");
            walletTransactionEntity.setRemarks(String.format(remarks, currency, order.getPaidFromWallet(), order.getStore().getName(), order.getId()));
            walletTransactionEntity.setTransactionAmount(order.getPaidFromWallet());
            walletTransactionEntity.setOrder(order);
            walletTransactionEntity.setCustomer(order.getCustomer());
            walletTransactionEntity.setPaymentMode(PaymentMode.CASH_ON_DELIVERY);
            walletTransactionEntity.setAvailableWalletAmount(order.getCustomer().getWalletAmount());
            systemAlgorithmService.encodeWalletTransaction(walletTransactionEntity);
            walletTransactionEntities.add(walletTransactionEntity);
            order.setWalletTransactions(walletTransactionEntities);
        }

        boolean status = orderDaoService.update(order);
        /*BigDecimal customerWalletAmount = order.getCustomer().getWalletAmount();
        if(status && BigDecimalUtil.isGreaterThen(customerWalletAmount, BigDecimal.ZERO)){
            adjustBalanceOfOtherOrders(order, customerWalletAmount);
        }*/

        /* Wallet Integration completed*/
        if(status){
            UserDeviceEntity userDevice = userDeviceDaoService.getUserDeviceInfoFromOrderId(order.getId());
            String message = MessageBundle.getMessage("CPN004","push_notification.properties");
            String extraDetail = order.getId().toString()+"/status/"+JobOrderStatus.IN_ROUTE_TO_DELIVERY.toString();
            PushNotificationUtil.sendPushNotification(userDevice, message, NotifyTo.CUSTOMER, PushNotificationRedirect.ORDER, extraDetail);
        }
        return status;
    }

    /*private void adjustBalanceOfOtherOrders(OrderEntity order, BigDecimal customerWalletAmount) throws Exception{
        log.info("looking for next orders whose paidToCOD > 0 and paymentMode is wallet for customer with ID:"+order.getCustomer().getId());
        String currency = systemPropertyService.readPrefValue(PreferenceType.CURRENCY);
        List<OrderEntity> orderList = orderDaoService.getWalletUnpaidOrders(order.getCustomer().getId(), order.getId());
        for(OrderEntity o: orderList){
            if(BigDecimalUtil.isGreaterThen(customerWalletAmount, BigDecimal.ZERO)){
                        *//* Customer wallet amount is less than order amount to be paid at customer during cash on delivery *//*
                if(BigDecimalUtil.isGreaterThenOrEqualTo(o.getPaidFromCOD(), customerWalletAmount)){
                    log.info("Customer wallet amount is less than order amount to be paid at customer during cash on delivery order ID:"+o.getId()+"Wallet Amount:"+customerWalletAmount+" Paid from COD:"+o.getPaidFromCOD());
                    String remarks = MessageBundle.getMessage("WTM003", "push_notification.properties");
                    remarks = String.format(remarks, currency, customerWalletAmount, o.getId());
                    this.setWalletTransaction(o, customerWalletAmount, AccountType.DEBIT, order.getPaymentMode(), remarks, BigDecimal.ZERO);

                    o.setPaidFromCOD(o.getPaidFromCOD().subtract(customerWalletAmount));
                    o.setPaidFromWallet(o.getPaidFromWallet().add(customerWalletAmount));
                    customerWalletAmount = BigDecimal.ZERO;
                    o.getCustomer().setWalletAmount(customerWalletAmount);
                }else{
                    log.info("Customer wallet amount is greater than order amount to be paid at customer during cash on delivery order ID:"+o.getId()+"Wallet Amount:"+customerWalletAmount+" Paid from COD:"+o.getPaidFromCOD());
                    String remarks = MessageBundle.getMessage("WTM003", "push_notification.properties");
                    remarks = String.format(remarks, currency, o.getPaidFromCOD(), o.getId());
                    this.setWalletTransaction(o, o.getPaidFromCOD(), AccountType.DEBIT, order.getPaymentMode(), remarks, customerWalletAmount.subtract(o.getPaidFromCOD()));

                    o.setPaidFromWallet(o.getPaidFromWallet().add(o.getPaidFromCOD()));
                    customerWalletAmount = customerWalletAmount.subtract(o.getPaidFromCOD());
                    o.setPaidFromCOD(BigDecimal.ZERO);
                    o.getCustomer().setWalletAmount(customerWalletAmount);
                }
                orderDaoService.update(o);
            }else
                break;
        }
    }*/

    private void setWalletTransaction(OrderEntity order, BigDecimal amount, AccountType accountType, PaymentMode paymentMode, String remarks, BigDecimal availableAmount) throws Exception{
        log.info("Setting wallet transaction info for order:"+order.getId()+" Amount:"+amount+" Account type:"+accountType+" Remarks:"+remarks);
        List<WalletTransactionEntity> walletTransactionEntities = order.getWalletTransactions();
        WalletTransactionEntity walletTransactionEntity = new WalletTransactionEntity();
        walletTransactionEntity.setTransactionDate(DateUtil.getCurrentTimestampSQL());
        walletTransactionEntity.setAccountType(accountType);
        walletTransactionEntity.setRemarks(remarks);
        walletTransactionEntity.setTransactionAmount(amount);
        walletTransactionEntity.setOrder(order);
        walletTransactionEntity.setCustomer(order.getCustomer());
        walletTransactionEntity.setPaymentMode(paymentMode);
        walletTransactionEntity.setAvailableWalletAmount(BigDecimalUtil.checkNull(availableAmount));
        systemAlgorithmService.encodeWalletTransaction(walletTransactionEntity);
        walletTransactionEntities.add(walletTransactionEntity);
        order.setWalletTransactions(walletTransactionEntities);

        UserDeviceEntity userDevice = userDeviceDaoService.getUserDeviceInfoFromOrderId(order.getId());
        String extraDetail = order.getId().toString();
        PushNotificationUtil.sendPushNotification(userDevice, remarks, NotifyTo.CUSTOMER, PushNotificationRedirect.TRANSACTION, extraDetail);
    }

    private Boolean deliverOrder(OrderEntity orderEntityJson, OrderEntity order, Integer deliveryBoyId) throws Exception {
        log.info("Delivering order with order ID: "+order.getId());
        if(!order.getDeliveryBoy().getId().equals(deliveryBoyId)){
            throw new YSException("ORD003");
        }

        if(order.getAttachments() != null){
            if(order.getAttachments().size() == 0)
                throw new YSException("ORD021");
        }

        if(!orderEntityJson.getOrderVerificationCode().equals(order.getOrderVerificationCode()) || !orderEntityJson.getId().equals(order.getId())){
            throw new YSException("ORD004");
        }
        JobOrderStatus.traverseJobStatus(order.getOrderStatus(), JobOrderStatus.DELIVERED);
        order.setDeliveryStatus(DeliveryStatus.SUCCESSFUL);
        order.setOrderStatus(JobOrderStatus.DELIVERED);
        order.setOrderVerificationStatus(true);

        List<DBoyOrderHistoryEntity> orderHistoryEntities = order.getdBoyOrderHistories();
        for(DBoyOrderHistoryEntity dBoyOrderHistoryEntity: orderHistoryEntities){
            if(dBoyOrderHistoryEntity.getDeliveryBoy().getId().equals(deliveryBoyId)){
                dBoyOrderHistoryEntity.setOrderCompletedAt(DateUtil.getCurrentTimestampSQL());
                dBoyOrderHistoryEntity.setDeliveryStatus(DeliveryStatus.SUCCESSFUL);
                dBoyOrderHistoryEntity.setDistanceTravelled(order.getCustomerChargeableDistance().add(order.getSystemChargeableDistance()));
                if(systemPropertyService.readPrefValue(PreferenceType.DELIVERY_FEE_CHARGING_MODEL).equals("Distance Based")) {
                    Double minuteDiff = DateUtil.getMinDiff(System.currentTimeMillis(), order.getOrderDate().getTime());
                    Integer GRACE_TIME = Integer.parseInt(systemPropertyService.readPrefValue(PreferenceType.DBOY_GRESS_TIME));
                    Integer totalTime = minuteDiff.intValue() + GRACE_TIME;
                    int remainingTime = order.getRemainingTime();
                    BigDecimal deliveryCost = order.getCourierTransaction().getPaidToCourier();
                    if(remainingTime < totalTime) {
                        BigDecimal deductionPercent = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DEDUCTION_PERCENT));
                        BigDecimal deductAmount = BigDecimalUtil.percentageOf(deliveryCost, deductionPercent);
                        deliveryCost = deliveryCost.subtract(deductAmount);
                        order.getCourierTransaction().setPaidToCourier(deliveryCost);
                        order.getCourierTransaction().setProfit(order.getCourierTransaction().getProfit().add(deductAmount));
                    }
                    dBoyOrderHistoryEntity.setAmountEarned(deliveryCost);
                } else {
                    BigDecimal shoppersEarning = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.SHOPPERS_EARNING_IN_FLAT_MODEL)).multiply(new BigDecimal(order.getSurgeFactor()));
                    order.getCourierTransaction().setPaidToCourier(shoppersEarning);
                    dBoyOrderHistoryEntity.setAmountEarned(shoppersEarning);
                }
                break;
            }
        }

        DeliveryBoyEntity deliveryBoyEntity = order.getDeliveryBoy();
        deliveryBoyEntity.setActiveOrderNo(deliveryBoyDaoService.getNumberOfActiveOrders(deliveryBoyEntity.getId()) - 1);
        if(deliveryBoyEntity.getActiveOrderNo() <= 0){
            deliveryBoyEntity.setActiveOrderNo(0);
            deliveryBoyEntity.setAvailabilityStatus(DBoyStatus.FREE);
        }
        deliveryBoyEntity.setTotalEarnings(BigDecimalUtil.checkNull(deliveryBoyEntity.getTotalEarnings()).add(order.getCourierTransaction().getPaidToCourier()));
        deliveryBoyEntity.setTotalOrderDelivered(deliveryBoyEntity.getTotalOrderDelivered() + 1);
        /*deliveryBoyEntity.setTotalOrderUndelivered(deliveryBoyEntity.getTotalOrderUndelivered() - 1);*/
        /*Accounting Implementation*/
        courierBoyAccountingsAfterOrderDelivery(deliveryBoyEntity, order);
        //reset the delivery boy for the order
        order.setDeliveryBoy(deliveryBoyEntity);
        /*Accounting Implementation Completed*/

        RatingEntity rating = order.getRating();
        if(rating == null){
            rating = new RatingEntity();
            order.setRating(rating);
            rating.setOrder(order);
        }

        rating.setCustomerRating(orderEntityJson.getRating().getCustomerRating());
        rating.setDeliveryBoyComment(orderEntityJson.getRating().getDeliveryBoyComment());
       // order.setRating(rating);
        CustomerEntity customerEntity = order.getCustomer();
        customerEntity.setTotalOrderDelivered(GeneralUtil.ifNullToZero(customerEntity.getTotalOrderDelivered())+1);
        /*List<OrderEntity> customersOrders = orderDaoService.getCustomersOrders(order.getCustomer().getId());*/

        //calculate paidFromCod and paid from wallet
        /*BigDecimal prevCodAmount = order.getPaidFromCOD();
        //BigDecimal prevWalletAmount = order.getPaidFromWallet();
        if(BigDecimalUtil.isGreaterThenOrEqualTo(BigDecimalUtil.checkNull(order.getCustomer().getWalletAmount()),order.getGrandTotal())){
            order.setPaidFromWallet(order.getGrandTotal());
            order.setPaidFromCOD(BigDecimal.ZERO);
        } else if(BigDecimalUtil.isGreaterThen(BigDecimalUtil.checkNull(order.getCustomer().getWalletAmount()), BigDecimal.ZERO)){
            BigDecimal paidFromWallet = order.getCustomer().getWalletAmount();
            BigDecimal paidFromCOD = order.getGrandTotal().subtract(paidFromWallet);
            order.setPaidFromWallet(paidFromWallet);
            order.setPaidFromCOD(paidFromCOD);
        } else {
            order.setPaidFromWallet(BigDecimal.ZERO);
            order.setPaidFromCOD(order.getGrandTotal());
        }*/

        if(BigDecimalUtil.isGreaterThenZero(order.getPaidFromCOD())){
            String currency = systemPropertyService.readPrefValue(PreferenceType.CURRENCY);
            String remarks = MessageBundle.getMessage("TRN001", "push_notification.properties");
            remarks = String.format(remarks, currency, order.getPaidFromCOD(), order.getId());
            setWalletTransaction(order, order.getPaidFromCOD(), AccountType.DEBIT, PaymentMode.CASH_ON_DELIVERY, remarks, order.getCustomer().getWalletAmount());
        }

        /* Wallet Integration */
        /*BigDecimal changeInWalletAmount = prevCodAmount.subtract(order.getPaidFromCOD());
        if(BigDecimalUtil.isGreaterThenZero(changeInWalletAmount))  {
            order.getCustomer().setWalletAmount(order.getCustomer().getWalletAmount().subtract(changeInWalletAmount));
            WalletTransactionEntity walletTransactionEntity = new WalletTransactionEntity();
            walletTransactionEntity.setTransactionDate(DateUtil.getCurrentTimestampSQL());
            walletTransactionEntity.setAccountType(AccountType.DEBIT);
            String currency = systemPropertyService.readPrefValue(PreferenceType.CURRENCY);
            String remarks = MessageBundle.getMessage("WTM001", "push_notification.properties");
            walletTransactionEntity.setRemarks(String.format(remarks, currency, changeInWalletAmount, order.getStore().getName()));
            walletTransactionEntity.setTransactionAmount(changeInWalletAmount);
            walletTransactionEntity.setOrder(order);
            walletTransactionEntity.setCustomer(order.getCustomer());
            walletTransactionEntity.setPaymentMode(order.getPaymentMode());
            walletTransactionEntity.setAvailableWalletAmount(order.getCustomer().getWalletAmount());
            systemAlgorithmService.encodeWalletTransaction(walletTransactionEntity);
            order.getWalletTransactions().add(walletTransactionEntity);
        }*/

        // refill cashback amount to customer wallet and notify customer
        if(order.getCashBackToCustomerAmount() != null && BigDecimalUtil.isGreaterThenZero(order.getCashBackToCustomerAmount())){
            order.getCustomer().setWalletAmount(order.getCustomer().getWalletAmount().add(order.getCashBackToCustomerAmount()));
            order.getCustomer().setRmCashBackAmount(BigDecimalUtil.checkNull(order.getCustomer().getRmCashBackAmount()).add(BigDecimalUtil.checkNull(order.getPaidFromWallet())));

            WalletTransactionEntity walletTransactionCashBackEntity = new WalletTransactionEntity();
            walletTransactionCashBackEntity.setTransactionDate(DateUtil.getCurrentTimestampSQL());
            walletTransactionCashBackEntity.setAccountType(AccountType.CREDIT);
            String currency = systemPropertyService.readPrefValue(PreferenceType.CURRENCY);
            String remarkCashBack = String.format(MessageBundle.getMessage("WTM013", "push_notification.properties"), currency, order.getCashBackToCustomerAmount(), order.getId());
            walletTransactionCashBackEntity.setRemarks(remarkCashBack);
            walletTransactionCashBackEntity.setTransactionAmount(order.getCashBackToCustomerAmount());
            walletTransactionCashBackEntity.setOrder(order);
            walletTransactionCashBackEntity.setCustomer(order.getCustomer());
            walletTransactionCashBackEntity.setPaymentMode(PaymentMode.CASH_ON_DELIVERY);
            walletTransactionCashBackEntity.setAvailableWalletAmount(order.getCustomer().getWalletAmount());
            systemAlgorithmService.encodeWalletTransaction(walletTransactionCashBackEntity);
            order.getWalletTransactions().add(walletTransactionCashBackEntity);

            UserDeviceEntity userDevice = userDeviceDaoService.getUserDeviceInfoFromOrderId(order.getId());
            String extraDetail = order.getId().toString();
            PushNotificationUtil.sendPushNotification(userDevice, remarkCashBack, NotifyTo.CUSTOMER, PushNotificationRedirect.TRANSACTION, extraDetail);
        }

        boolean status = orderDaoService.update(order);
        if(status){
            log.info("Order has been updated successfully");
            /*=========== Email Bill and Receipt to the customer ================ (Appended By Sagar) *//*
            accountService.generateBillAndReceiptAndSendEmail(order);*/


            /*=========== Calculate Average Rating For Customer ================ (Appended By Surendra) */
            log.info("Calculating Average Rating of Customer After Delivered ");
            //RatingEntity ratingEntity = orderDaoService.getCustomerRatingInfo(customerEntity.getId());
            List<Integer> ratings = orderDaoService.getCustomerRatings(customerEntity.getId());
            if(ratings !=null && ratings.size()> 0){
                BigDecimal totalRate = BigDecimal.ZERO;

                for(Integer rate : ratings){
                    totalRate = totalRate.add(new BigDecimal(rate));
                }

                BigDecimal averageRating = getAverageRating(ratings.size(), totalRate);
                //Now Update the average rating and deactivate the customer if
                customerDaoService.updateAverageRating(averageRating, customerEntity.getId());
                //Lets Change User Status

                if(orderDaoService.hasCustomerRunningOrders(customerEntity.getId()) <= 0){
                    if(BigDecimalUtil.isLessThen(averageRating, new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.CUSTOMER_DEFAULT_RATING)))) {
                        //Deactivate User
                        log.info("Deactivating Customer id : " + customerEntity.getId());
                        userDaoService.deactivateUser(customerEntity.getUser().getId());
                    }

                }

            /* Ended By Surendra */
            }


            /* Updating unit price of item */
            this.updateItemPrice(order);
            UserDeviceEntity userDevice = userDeviceDaoService.getUserDeviceInfoFromOrderId(order.getId());
            String message = MessageBundle.getMessage("CPN006","push_notification.properties");
            String extraDetail = order.getId().toString()+"/status/"+JobOrderStatus.DELIVERED.toString();
            PushNotificationUtil.sendPushNotification(userDevice, message, NotifyTo.CUSTOMER, PushNotificationRedirect.ORDER, extraDetail);
           /* *//*
            * if the transaction is first transaction of  the current customer
            * set reward for referrer
            * *//*
             if(customersOrders.size() == 1){
               Long referrerId = order.getCustomer().getReferredBy();
               if(referrerId != null){
                   CustomerEntity referrer = customerDaoService.find(referrerId);
                   referrer.setRewardsEarned(referrer.getRewardsEarned().add(new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.REFERRAL_REWARD_AMOUNT))));
                   customerService.refillCustomerWallet(referrer.getFacebookId(), new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.REFERRAL_REWARD_AMOUNT)), MessageBundle.getMessage("WTM005", "push_notification.properties"), false);
                   customerDaoService.update(referrer);
               }
            }*/

        }
        return status;
    }

    private BigDecimal getAverageRating(Integer totalRate, BigDecimal totalRateSum) throws Exception {
        BigDecimal averageRating;
        if(totalRate<= 10 && totalRate > 0){
            Integer rate = Integer.valueOf(totalRateSum.intValue()) + Integer.parseInt(systemPropertyService.readPrefValue(PreferenceType.CUSTOMER_DEFAULT_RATING)) * (10-totalRate);
            averageRating = new BigDecimal(rate).divide(new BigDecimal(10), MathContext.DECIMAL128);
            averageRating = averageRating.setScale(0, BigDecimal.ROUND_HALF_UP);
        } else {
            averageRating = totalRateSum.divide(new BigDecimal(totalRate), MathContext.DECIMAL128);
            averageRating = averageRating.setScale(0, BigDecimal.ROUND_HALF_UP);
        }
        return averageRating;
    }

    /* This method is used to update unit price of an item. */
    private void updateItemPrice(OrderEntity orderEntity) throws Exception {
        log.info("Updating item price of order with ID:"+orderEntity.getId());
        List<ItemsOrderEntity> itemsOrderEntities = orderEntity.getItemsOrder();
        for(ItemsOrderEntity itemsOrder: itemsOrderEntities){
            ItemEntity item = itemsOrder.getItem();
            if(item != null && itemsOrder.getAvailabilityStatus()){
                if(item.getAttributesTypes().size() == 0){
                    BigDecimal unitPrice = BigDecimalUtil.getUnitPrice(itemsOrder.getItemTotal(), itemsOrder.getQuantity());
                    if(unitPrice != null){
                        if(!item.getUnitPrice().equals(unitPrice)){
                            log.info("Updating unit price of item with ID:"+item.getId());
                            item.setUnitPrice(unitPrice);
                            itemDaoService.update(item);
                        }
                    }
                }
            }
        }
    }

    @Override
    public PaginationDto getPastDeliveries(Page page, Integer deliveryBoyId) throws Exception {
        log.info("Retrieving list of past deliveries");
        PaginationDto paginationDto = new PaginationDto();
        Integer totalRows = dBoyOrderHistoryDaoService.getTotalNumberOfPastDeliveries(deliveryBoyId);
        paginationDto.setNumberOfRows(totalRows);
        List<PastDeliveriesDto> pastDeliveriesDtos;
        if(totalRows > 0){
            if(page != null){
                page.setTotalRows(totalRows);
            }
            pastDeliveriesDtos = dBoyOrderHistoryDaoService.getPastOrders(page, deliveryBoyId);
        }else{
            pastDeliveriesDtos = new ArrayList<PastDeliveriesDto>();
        }
        paginationDto.setData(pastDeliveriesDtos);
        return paginationDto;
    }

    @Override
    public Boolean changeDeliveryBoyStatus(DeliveryBoyEntity deliveryBoyEntity) throws Exception {
        log.info("Updating Status of Shopper to:" + deliveryBoyEntity.getAvailabilityStatus());
        DeliveryBoyEntity dBoyEntity = deliveryBoyDaoService.find(deliveryBoyEntity.getId());
        if (dBoyEntity == null) {
            throw new YSException("VLD011");
        }
        if(deliveryBoyEntity.getAvailabilityStatus().equals(DBoyStatus.FREE)){
            if(dBoyEntity.getActiveOrderNo() > 0){
                dBoyEntity.setAvailabilityStatus(DBoyStatus.BUSY);
            }else{
                dBoyEntity.setAvailabilityStatus(DBoyStatus.FREE);
            }
            if (deliveryBoyEntity.getLatitude() != null && deliveryBoyEntity.getLongitude() != null) {
                dBoyEntity.setLatitude(deliveryBoyEntity.getLatitude());
                dBoyEntity.setLongitude(deliveryBoyEntity.getLongitude());
            }
        }else if(deliveryBoyEntity.getAvailabilityStatus().equals(DBoyStatus.NOT_AVAILABLE)){
            if(dBoyEntity.getAvailabilityStatus().equals(DBoyStatus.BUSY)){
               throw new YSException("DBY001");
            }
            dBoyEntity.setAvailabilityStatus(DBoyStatus.NOT_AVAILABLE);
        }else{
            throw new YSException("DBY002");
        }
        return deliveryBoyDaoService.update(dBoyEntity);
    }

    @Override
    public Boolean uploadBills(OrderEntity order, Integer deliveryBoyId) throws Exception {
        OrderEntity orderEntity = orderDaoService.find(order.getId());
        if(orderEntity == null){
            throw new YSException("VLD017");
        }
        if(!deliveryBoyId.equals(orderEntity.getDeliveryBoy().getId())){
            throw new YSException("ORD003");
        }
        log.info("Uploading Bill of an order to S3 Bucket ");
        String dir = MessageBundle.separateString("/", "Orders", "Order" + order.getId());
        boolean isLocal = MessageBundle.isLocalHost();
        List<String> attachments = orderEntity.getAttachments();
        for(String bill: attachments){
            log.info("Deleting brand log to S3 Bucket ");
            AmazonUtil.deleteFileFromBucket(AmazonUtil.getAmazonS3Key(bill));
        }
        attachments.clear();
        int i = 1;
        for (String bill: order.getAttachments()) {
            if (bill != null && !bill.isEmpty()) {
                bill = "data:image/jpeg;base64,"+bill;
                String imageName = "Bill_" + order.getId() + "_" + (isLocal ? "_tmp_" : "_") + i + "_"+System.currentTimeMillis();
                String s3Path = GeneralUtil.saveImageToBucket(bill, imageName, dir, false);
                attachments.add(s3Path);
                i++;
            }
        }
        return orderDaoService.update(orderEntity);
    }

    @Override
    public DeliveryBoyEntity getProfileOfDeliveryBoy(Integer deliveryBoyId) throws Exception {
        DeliveryBoyEntity deliveryBoy = deliveryBoyDaoService.getProfileInformation(deliveryBoyId);
        if(deliveryBoy == null){
            throw new YSException("VLD011");
        }
        Timestamp lastAckDate = dBoyAdvanceAmountDaoService.getLatestAckTimestamp(deliveryBoy.getId());

        BigDecimal cancelledPurchaseTotal = BigDecimal.ZERO;
        //if(lastAckDate != null){
        List<OrderEntity> cancelledPurchasedOrders =  orderDaoService.getCancelledPurchasedOrder(deliveryBoy.getId(), lastAckDate);
        for (OrderEntity order: cancelledPurchasedOrders){
            Boolean itemPurchased = false;
            List<ItemsOrderEntity> itemsOrders = order.getItemsOrder();
            for(ItemsOrderEntity itemsOrder: itemsOrders){
                if(itemsOrder.getPurchaseStatus() != null && itemsOrder.getPurchaseStatus()){
                    itemPurchased = true;
                    break;
                }
            }
            if(itemPurchased)
                cancelledPurchaseTotal =  cancelledPurchaseTotal.add(order.getGrandTotal());
        }
        //}

        deliveryBoy.setItemReturnedTotal(cancelledPurchaseTotal);

        return deliveryBoy;
    }

    @Override
    public Boolean addNewItem(ItemsOrderEntity itemsOrderEntity) throws Exception {
        OrderEntity order = orderDaoService.find(itemsOrderEntity.getOrder().getId());
        Boolean vatInclusive = order.getStore().getStoresBrand().getVatInclusive();
        if(order == null)
            throw new YSException("VLD017");
        /*Adding item total*/
        order.setTotalCost(order.getTotalCost().add(itemsOrderEntity.getItemTotal()));

        BigDecimal commissionAmount = BigDecimal.ZERO;
        if(itemsOrderEntity.getItem()!=null) {
            commissionAmount = BigDecimalUtil.percentageOf(BigDecimalUtil.checkNull(itemsOrderEntity.getItemTotal()), BigDecimalUtil.checkNull(itemsOrderEntity.getItem().getCommissionPercentage()));
        }  else {
            commissionAmount = BigDecimalUtil.percentageOf(itemsOrderEntity.getItemTotal(), BigDecimalUtil.checkNull(order.getStore().getStoresBrand().getDefaultCommissionPcn()));
        }

        itemsOrderEntity.setCommissionAmount(commissionAmount);

        /*Calculating service and vat charge for new item and adding it to ItemServiceAndVatCharge*/
        BigDecimal serviceCharge = BigDecimalUtil.percentageOf(itemsOrderEntity.getItemTotal(), itemsOrderEntity.getServiceCharge());
        BigDecimal vatCharge =  BigDecimal.ZERO;
        BigDecimal serviceAndVatCharge;
        if(!vatInclusive) {
            vatCharge =  BigDecimalUtil.percentageOf(itemsOrderEntity.getItemTotal().add(serviceCharge), itemsOrderEntity.getVat());
            serviceAndVatCharge = serviceCharge.add(vatCharge);
        } else
            serviceAndVatCharge = serviceCharge;

        itemsOrderEntity.setServiceAndVatCharge(serviceAndVatCharge);
        order.setItemServiceAndVatCharge(order.getItemServiceAndVatCharge().add(serviceAndVatCharge));
        order.setItemVatCharge(order.getItemVatCharge().add(vatCharge));
        order.setItemServiceCharge(order.getItemServiceCharge().add(serviceCharge));
        order.setCommissionAmount(order.getCommissionAmount().add(commissionAmount));
        itemsOrderEntity.getCustomItem().setItemsOrder(itemsOrderEntity);

        List<ItemsOrderEntity> itemsOrderEntities = order.getItemsOrder();
        itemsOrderEntities.add(itemsOrderEntity);
        order.setItemsOrder(itemsOrderEntities);

        CourierTransactionEntity courierTransactionEntity = order.getCourierTransaction();
        if(order.getDeliveryBoy() == null){
            throw new YSException("ORD011");
        }
        DeliveryBoySelectionEntity dBoySelection = deliveryBoySelectionDaoService.getSelectionDetails(order.getId(), order.getDeliveryBoy().getId());
        if(dBoySelection == null){
            throw new YSException("ORD003");
        }

        CourierTransactionEntity courierTransaction = systemAlgorithmService.getCourierTransaction(order, dBoySelection, order.getCommissionAmount(), courierTransactionEntity.getSystemProcessingChargePct());
        courierTransactionEntity.setOrderTotal(courierTransaction.getOrderTotal());
        courierTransactionEntity.setAdditionalDeliveryAmt(courierTransaction.getAdditionalDeliveryAmt());
        courierTransactionEntity.setCustomerDiscount(courierTransaction.getCustomerDiscount());
        courierTransactionEntity.setDeliveryCostWithoutAdditionalDvAmt(courierTransaction.getDeliveryCostWithoutAdditionalDvAmt());
        courierTransactionEntity.setDeliveryChargedBeforeDiscount(courierTransaction.getDeliveryChargedBeforeDiscount());
        courierTransactionEntity.setSystemProcessingChargeAmount(courierTransaction.getSystemProcessingChargeAmount());
        courierTransactionEntity.setDeliveryChargedAfterDiscount(courierTransaction.getDeliveryChargedAfterDiscount());
        courierTransactionEntity.setCustomerPays(courierTransaction.getCustomerPays());
        courierTransactionEntity.setPaidToCourier(courierTransaction.getPaidToCourier());
        courierTransactionEntity.setProfit(courierTransaction.getProfit());

        /* Wallet Integration */
        //if(order.getPaymentMode().equals(PaymentMode.WALLET)){
           // order.setPaidFromCOD(order.getGrandTotal().subtract(order.getPaidFromWallet()));
       /* }else{
            order.setPaidFromCOD(order.getGrandTotal());
        }*/

        //calculate paidFromCod and paid from wallet
       /* if(BigDecimalUtil.isGreaterThenOrEqualTo(BigDecimalUtil.checkNull(order.getCustomer().getWalletAmount()),order.getGrandTotal())){
            OrderPaymentUtil.calculateOrderWalletPayment(order);
            order.setPaidFromWallet(order.getGrandTotal());
            order.setPaidFromCOD(BigDecimal.ZERO);
        } else if(BigDecimalUtil.isGreaterThen(BigDecimalUtil.checkNull(order.getCustomer().getWalletAmount()), BigDecimal.ZERO)){
            OrderPaymentUtil.calculateOrderWalletPayment(order);
            BigDecimal paidFromWallet = order.getCustomer().getWalletAmount();
            BigDecimal paidFromCOD = order.getGrandTotal().subtract(paidFromWallet);
            order.setPaidFromWallet(paidFromWallet);
            order.setPaidFromCOD(paidFromCOD);
        } else {
            order.setPaidFromWallet(BigDecimal.ZERO);
            order.setPaidFromCOD(order.getGrandTotal());
        }*/
        OrderPaymentUtil.calculateOrderPayment(order);

        boolean status = orderDaoService.save(order);
        if(status){
            UserDeviceEntity userDevice = userDeviceDaoService.getUserDeviceInfoFromOrderId(order.getId());
            String message = MessageBundle.getMessage("CPN005","push_notification.properties");
            String extraDetail = order.getId().toString()+"/status/"+order.getOrderStatus().toString();
            PushNotificationUtil.sendPushNotification(userDevice, message, NotifyTo.CUSTOMER, PushNotificationRedirect.ORDER, extraDetail);
        }
        return true;
    }

    @Override
    public Boolean updateOrders(List<ItemsOrderEntity> itemOrders, Integer orderId) throws Exception {
        OrderEntity order = orderDaoService.find(orderId);
        if (order == null) {
            throw new YSException("VLD017");
        }

        BigDecimal itemTotalCost = BigDecimal.ZERO;
        BigDecimal itemServiceAndVatCharge = BigDecimal.ZERO;
        BigDecimal totalCommissionAmount = BigDecimal.ZERO;
        BigDecimal cashBackAmountTotal = BigDecimal.ZERO;

        List<ItemsOrderEntity> itemsOrderEntityList = order.getItemsOrder();
        for (ItemsOrderEntity itemsOrder : itemOrders) {
            /* Validating itemsOrder Entity */
            if (itemsOrder.getId() == null) {
                throw new YSException("VLD025");
            }
            ItemsOrderEntity itemsOrderEntity = getItemOrderById(itemsOrderEntityList, itemsOrder.getId());
            if (itemsOrderEntity == null)
                throw new YSException("VLD025");

            /* Service Fee and Vat calculation for available items only. */
            itemsOrderEntity.setAvailabilityStatus(itemsOrder.getAvailabilityStatus());
            if (itemsOrder.getAvailabilityStatus()) {
                BigDecimal commissionAmount = BigDecimal.ZERO;

                if(itemsOrder.getItem() != null) {
                    commissionAmount = BigDecimalUtil.percentageOf(itemsOrder.getItemTotal(), BigDecimalUtil.checkNull(itemsOrder.getItem().getCommissionPercentage()));
                    cashBackAmountTotal.add(BigDecimalUtil.checkNull(itemsOrder.getItem().getCashBackAmount()).multiply(new BigDecimal(itemsOrder.getQuantity())));
                } else {
                    commissionAmount = BigDecimalUtil.percentageOf(itemsOrder.getItemTotal(), BigDecimalUtil.checkNull(order.getStore().getStoresBrand().getDefaultCommissionPcn()));
                }

                BigDecimal serviceChargeAmount = BigDecimalUtil.percentageOf(itemsOrder.getItemTotal(), BigDecimalUtil.checkNull(itemsOrder.getServiceCharge()));

                BigDecimal serviceAndVatChargeAmount;
                if(!order.getStore().getStoresBrand().getVatInclusive())
                    serviceAndVatChargeAmount = serviceChargeAmount.add(BigDecimalUtil.percentageOf(serviceChargeAmount.add(itemsOrder.getItemTotal()), BigDecimalUtil.checkNull(itemsOrder.getVat())));
                else
                    serviceAndVatChargeAmount = serviceChargeAmount;

                itemTotalCost = itemTotalCost.add(itemsOrder.getItemTotal());
                itemServiceAndVatCharge = itemServiceAndVatCharge.add(serviceAndVatChargeAmount);
                totalCommissionAmount = totalCommissionAmount.add(commissionAmount);
                itemsOrderEntity.setCommissionAmount(commissionAmount);
                itemsOrderEntity.setServiceAndVatCharge(serviceAndVatChargeAmount);
                itemsOrderEntity.setQuantity(itemsOrder.getQuantity());
                itemsOrderEntity.setItemTotal(itemsOrder.getItemTotal());
                itemsOrderEntity.setNote(itemsOrder.getNote());
                itemsOrderEntity.setServiceCharge(itemsOrder.getServiceCharge());
                itemsOrderEntity.setVat(itemsOrder.getVat());
            }

            /* Updating name of custom item added by delivery boy */
            if(itemsOrderEntity.getCustomItem() != null && itemsOrder.getCustomItem() != null){
                itemsOrderEntity.getCustomItem().setName(itemsOrder.getCustomItem().getName());
            }

            /* Updating attributes of item added by customer */
            List<ItemsOrderAttributeEntity> itemsOrderAttributeEntityList = itemsOrderEntity.getItemOrderAttributes();
            itemsOrderAttributeEntityList.clear();
            for(ItemsOrderAttributeEntity itemsOrderAttributes : itemsOrder.getItemOrderAttributes()){
                ItemsOrderAttributeEntity itemsOrderAttribute = new ItemsOrderAttributeEntity();
                ItemsAttributeEntity itemsAttribute = itemsOrderAttributes.getItemsAttribute();
                itemsOrderAttribute.setItemOrder(itemsOrderEntity);
                itemsOrderAttribute.setItemsAttribute(itemsAttribute);
                itemsOrderAttributeEntityList.add(itemsOrderAttribute);
                itemsAttribute.setItemOrderAttributes(itemsOrderAttributeEntityList);
            }
        }

        order.setItemsOrder(itemsOrderEntityList);
        order.setTotalCost(itemTotalCost);
        order.setItemServiceAndVatCharge(itemServiceAndVatCharge);
        order.setCashBackToCustomerAmount(cashBackAmountTotal);
        order.setCommissionAmount(totalCommissionAmount);

        DeliveryBoySelectionEntity dBoySelection = new DeliveryBoySelectionEntity();
        dBoySelection.setDistanceToStore(order.getSystemChargeableDistance());
        dBoySelection.setStoreToCustomerDistance(order.getCustomerChargeableDistance());

        CourierTransactionEntity courierTransactionEntity = order.getCourierTransaction();
        /*MerchantEntity merchant = new MerchantEntity();
        merchant.setCommissionPercentage(courierTransactionEntity.getCommissionPct());
        merchant.setServiceFee(courierTransactionEntity.getServiceFeePct());*/

        CourierTransactionEntity courierTransaction = systemAlgorithmService.getCourierTransaction(order, dBoySelection, courierTransactionEntity.getCommissionAmount(), courierTransactionEntity.getSystemProcessingChargePct());
        courierTransactionEntity.setOrderTotal(courierTransaction.getOrderTotal());
        courierTransactionEntity.setAdditionalDeliveryAmt(courierTransaction.getAdditionalDeliveryAmt());
        courierTransactionEntity.setCustomerDiscount(courierTransaction.getCustomerDiscount());
        courierTransactionEntity.setDeliveryCostWithoutAdditionalDvAmt(courierTransaction.getDeliveryCostWithoutAdditionalDvAmt());
        courierTransactionEntity.setSystemProcessingChargeAmount(courierTransaction.getSystemProcessingChargeAmount());
        courierTransactionEntity.setDeliveryChargedBeforeDiscount(courierTransaction.getDeliveryChargedBeforeDiscount());
        courierTransactionEntity.setCustomerBalanceBeforeDiscount(courierTransaction.getCustomerBalanceBeforeDiscount());
        courierTransactionEntity.setDeliveryChargedAfterDiscount(courierTransaction.getDeliveryChargedAfterDiscount());
        courierTransactionEntity.setCustomerBalanceAfterDiscount(courierTransaction.getCustomerBalanceAfterDiscount());
        courierTransactionEntity.setCustomerPays(courierTransaction.getCustomerPays());
        courierTransactionEntity.setPaidToCourier(courierTransaction.getPaidToCourier());
        courierTransactionEntity.setProfit(courierTransaction.getProfit());
        boolean status = orderDaoService.update(order);
        if(status){
            UserDeviceEntity userDevice = userDeviceDaoService.getUserDeviceInfoFromOrderId(order.getId());
            String message = MessageBundle.getMessage("CPN005","push_notification.properties");
            PushNotificationUtil.sendPushNotification(userDevice, message, NotifyTo.CUSTOMER, PushNotificationRedirect.ORDER, order.getId().toString());
        }
        return status;
    }

    private ItemsOrderEntity getItemOrderById(List<ItemsOrderEntity> itemsOrderEntities, Integer itemOrderId) {
        for(ItemsOrderEntity itemOrder: itemsOrderEntities){
            if(itemOrder.getId().equals(itemOrderId)){
                return itemOrder;
            }
        }
        return null;
    }

    @Override
    public Boolean updateItemOrderByItemOrderId(ItemsOrderEntity itemOrder, Integer orderId) throws Exception {
        OrderEntity order = orderDaoService.find(orderId);
        Boolean vatInclusive = order.getStore().getStoresBrand().getVatInclusive();
        if (order == null) {
            throw new YSException("VLD017");
        } else if(order.getDeliveryBoy() == null){
            throw new YSException("ORD014");
        } else if(order.getOrderStatus().equals(JobOrderStatus.IN_ROUTE_TO_DELIVERY)
                || order.getOrderStatus().equals(JobOrderStatus.DELIVERED) || order.getOrderStatus().equals(JobOrderStatus.CANCELLED)){
            throw new YSException("ORD024");
        }

        BigDecimal itemTotalCost = BigDecimal.ZERO;
        BigDecimal itemServiceAndVatCharge = BigDecimal.ZERO;
        BigDecimal itemServiceCharge = BigDecimal.ZERO;
        BigDecimal itemVatCharge = BigDecimal.ZERO;
        BigDecimal totalCommissionAmount = BigDecimal.ZERO;
        BigDecimal cashBackAmountTotal = BigDecimal.ZERO;

        List<ItemsOrderEntity> itemsOrderEntityList = order.getItemsOrder();
        ItemsOrderEntity itemsOrderEntity = getItemOrderById(itemsOrderEntityList, itemOrder.getId());
        if (itemsOrderEntity == null)
            throw new YSException("VLD025");
        itemsOrderEntity.setQuantity(itemOrder.getQuantity());
        itemsOrderEntity.setItemTotal(itemOrder.getItemTotal());
        itemsOrderEntity.setAvailabilityStatus(itemOrder.getAvailabilityStatus());
        itemsOrderEntity.setVat(itemOrder.getVat());
        itemsOrderEntity.setServiceCharge(itemOrder.getServiceCharge());

        if(itemOrder.getItemOrderAttributes() == null){
            BigDecimal itemPrice = itemOrder.getItemTotal().divide(new BigDecimal(itemOrder.getQuantity())).setScale(2, BigDecimal.ROUND_DOWN);
            itemsOrderEntity.getItem().setUnitPrice(itemPrice);
        }

        if(itemOrder.getNote() != null && !itemOrder.getNote().equals(""))
            itemsOrderEntity.setNote(itemOrder.getNote());

        itemsOrderEntity.setPurchaseStatus(itemOrder.getPurchaseStatus());
        if (itemOrder.getPurchaseStatus() != null && itemOrder.getPurchaseStatus()) {
            itemsOrderEntity.setAvailabilityStatus(true);
        }
         /* Updating name of custom item added by delivery boy */
        if(itemsOrderEntity.getCustomItem() != null && itemOrder.getCustomItem() != null){
            itemsOrderEntity.getCustomItem().setName(itemOrder.getCustomItem().getName());
            if(itemOrder.getCustomItem().getEditedName() != null && !itemOrder.getCustomItem().getEditedName().equals(""))
                itemsOrderEntity.getCustomItem().setEditedName(itemOrder.getCustomItem().getEditedName());
        }else{
             /* Updating attributes of item added by customer */
            List<ItemsOrderAttributeEntity> itemsOrderAttributeEntityList = itemsOrderEntity.getItemOrderAttributes();
            itemsOrderAttributeEntityList.clear();
            for(ItemsOrderAttributeEntity itemsOrderAttributes : itemOrder.getItemOrderAttributes()){
                ItemsOrderAttributeEntity itemsOrderAttribute = new ItemsOrderAttributeEntity();
                ItemsAttributeEntity itemsAttribute = itemsOrderAttributes.getItemsAttribute();
                itemsOrderAttribute.setItemOrder(itemsOrderEntity);
                itemsOrderAttribute.setItemsAttribute(itemsAttribute);
                itemsOrderAttributeEntityList.add(itemsOrderAttribute);
                itemsAttribute.setItemOrderAttributes(itemsOrderAttributeEntityList);
            }
        }

        for (ItemsOrderEntity itemsOrder : itemsOrderEntityList) {
            /* Service Fee and Vat calculation for available items only. */
            if (itemsOrder.getAvailabilityStatus()) {
                BigDecimal commissionAmount = BigDecimal.ZERO;
                if(itemsOrder.getItem()!=null) {
                    commissionAmount = BigDecimalUtil.percentageOf(BigDecimalUtil.checkNull(itemsOrder.getItemTotal()), BigDecimalUtil.checkNull(itemsOrder.getItem().getCommissionPercentage()));
                    cashBackAmountTotal = cashBackAmountTotal.add(BigDecimalUtil.checkNull(itemsOrder.getItem().getCashBackAmount()).multiply(new BigDecimal(itemsOrder.getQuantity())));
                }  else {
                    commissionAmount = BigDecimalUtil.percentageOf(itemsOrder.getItemTotal(), BigDecimalUtil.checkNull(order.getStore().getStoresBrand().getDefaultCommissionPcn()));
                }
                BigDecimal serviceChargeAmount = BigDecimalUtil.percentageOf(BigDecimalUtil.checkNull(itemsOrder.getItemTotal()), BigDecimalUtil.checkNull(itemsOrder.getServiceCharge()));

                BigDecimal serviceAndVatChargeAmount;
                if(vatInclusive != null && !vatInclusive) {
                    BigDecimal itemsOrderVatAmount = BigDecimalUtil.percentageOf(serviceChargeAmount.add(BigDecimalUtil.checkNull(itemsOrder.getItemTotal())), BigDecimalUtil.checkNull(itemsOrder.getVat()));
                    itemVatCharge = itemVatCharge.add(itemsOrderVatAmount);
                    serviceAndVatChargeAmount = serviceChargeAmount.add(itemsOrderVatAmount);
                } else
                    serviceAndVatChargeAmount = serviceChargeAmount;

                itemTotalCost = itemTotalCost.add(BigDecimalUtil.checkNull(itemsOrder.getItemTotal()));
                itemServiceCharge = itemServiceCharge.add(serviceChargeAmount);
                totalCommissionAmount = totalCommissionAmount.add(commissionAmount);
                itemServiceAndVatCharge = itemServiceAndVatCharge.add(serviceAndVatChargeAmount);
                itemsOrder.setServiceAndVatCharge(serviceAndVatChargeAmount);
                itemsOrder.setCommissionAmount(commissionAmount);
            }
        }

       /* Commented since minimum amount is checked at start delivery. */
       /*if(BigDecimalUtil.isLessThen(itemTotalCost, order.getStore().getStoresBrand().getMinOrderAmount())){
            throw new YSException("CRT008", " "+order.getStore().getStoresBrand().getMinOrderAmount());
        }*/

        order.setTotalCost(BigDecimalUtil.chopToTwoDecimalPlace(itemTotalCost));
        order.setItemServiceAndVatCharge(BigDecimalUtil.chopToTwoDecimalPlace(itemServiceAndVatCharge));
        order.setItemServiceCharge(BigDecimalUtil.chopToTwoDecimalPlace(itemServiceCharge));
        order.setItemVatCharge(BigDecimalUtil.chopToTwoDecimalPlace(itemVatCharge));
        order.setCommissionAmount(totalCommissionAmount);
        order.setCashBackToCustomerAmount(cashBackAmountTotal);

        /*This section is used just for getting courier transaction information */
        DeliveryBoySelectionEntity dBoySelection = new DeliveryBoySelectionEntity();
        dBoySelection.setDistanceToStore(order.getSystemChargeableDistance());
        dBoySelection.setStoreToCustomerDistance(order.getCustomerChargeableDistance());

        CourierTransactionEntity courierTransactionEntity = order.getCourierTransaction();
        CourierTransactionEntity courierTransaction = systemAlgorithmService.getCourierTransaction(order, dBoySelection, order.getCommissionAmount(), courierTransactionEntity.getSystemProcessingChargePct());
        courierTransactionEntity.setOrderTotal(courierTransaction.getOrderTotal());
        courierTransactionEntity.setAdditionalDeliveryAmt(courierTransaction.getAdditionalDeliveryAmt());
        courierTransactionEntity.setCustomerDiscount(courierTransaction.getCustomerDiscount());
        courierTransactionEntity.setDeliveryCostWithoutAdditionalDvAmt(courierTransaction.getDeliveryCostWithoutAdditionalDvAmt());
        courierTransactionEntity.setSystemProcessingChargeAmount(courierTransaction.getSystemProcessingChargeAmount());
        courierTransactionEntity.setDeliveryChargedBeforeDiscount(courierTransaction.getDeliveryChargedBeforeDiscount());
        courierTransactionEntity.setCustomerBalanceBeforeDiscount(courierTransaction.getCustomerBalanceBeforeDiscount());
        courierTransactionEntity.setDeliveryChargedAfterDiscount(courierTransaction.getDeliveryChargedAfterDiscount());
        courierTransactionEntity.setCustomerBalanceAfterDiscount(courierTransaction.getCustomerBalanceAfterDiscount());
        courierTransactionEntity.setCustomerPays(courierTransaction.getCustomerPays());
        courierTransactionEntity.setPaidToCourier(courierTransaction.getPaidToCourier());
        courierTransactionEntity.setProfit(courierTransaction.getProfit());
        courierTransactionEntity.setCommissionAmount(courierTransaction.getCommissionAmount());

        /* Wallet Integration */
        //if(order.getPaymentMode().equals(PaymentMode.WALLET)){
            /* Difference in amount is set in Paid From COD amount
               * Case I: No change ==> PaidFromCOD = 0
               * Case II: Order Grand Total > Paid From Wallet: ==> Increase in price thus, paidFromCOD > 0
               * Case III: Order Grand Total < Paid From Wallet: ==> Decrease in price thus, paidFromCOD < 0
             */
           // order.setPaidFromCOD(order.getGrandTotal().subtract(order.getPaidFromWallet()));
        /*}else{
            order.setPaidFromCOD(order.getGrandTotal());
        }*/

        //calculate paidFromCod and paid from wallet
       /* if(BigDecimalUtil.isGreaterThenOrEqualTo(BigDecimalUtil.checkNull(order.getCustomer().getWalletAmount()),order.getGrandTotal())){
            OrderPaymentUtil.calculateOrderWalletPayment(order);
            order.setPaidFromWallet(order.getGrandTotal());
            order.setPaidFromCOD(BigDecimal.ZERO);
        } else if(BigDecimalUtil.isGreaterThen(BigDecimalUtil.checkNull(order.getCustomer().getWalletAmount()), BigDecimal.ZERO)){
            OrderPaymentUtil.calculateOrderWalletPayment(order);
            BigDecimal paidFromWallet = order.getCustomer().getWalletAmount();
            BigDecimal paidFromCOD = order.getGrandTotal().subtract(paidFromWallet);
            order.setPaidFromWallet(paidFromWallet);
            order.setPaidFromCOD(paidFromCOD);
        } else {
            order.setPaidFromWallet(BigDecimal.ZERO);
            order.setPaidFromCOD(order.getGrandTotal());
        }*/
        OrderPaymentUtil.calculateOrderPayment(order);

        boolean status = orderDaoService.update(order);
        if(status){
            UserDeviceEntity userDevice = userDeviceDaoService.getUserDeviceInfoFromOrderId(order.getId());
            String message = MessageBundle.getMessage("CPN005","push_notification.properties");
            String extraDetail = order.getId().toString()+"/status/"+order.getOrderStatus().toString();
            PushNotificationUtil.sendPushNotification(userDevice, message, NotifyTo.CUSTOMER, PushNotificationRedirect.ORDER, extraDetail);
        }
        return status;
    }

    @Override
    public Boolean cancelOrder(OrderEntity order, Integer userId) throws Exception {
        OrderEntity orderEntity = orderDaoService.find(order.getId());
        if(orderEntity == null){
            throw new YSException("VLD017");
        }

        if(orderEntity.getOrderStatus().equals(JobOrderStatus.DELIVERED)){
            throw new YSException("ORD025");
        }

        log.info("About to Cancel order with Order ID:"+order.getId());

        /*Setting Ratings */
        if (order.getRating() != null){
            RatingEntity rating = null;
            if(orderEntity.getRating() == null){
                rating = new RatingEntity();
                rating.setOrder(orderEntity);
            }else{
                rating = orderEntity.getRating();
            }
            if(order.getRating().getCustomerRating()!=null)
                rating.setCustomerRating(order.getRating().getCustomerRating());
            if(order.getRating().getDeliveryBoyComment()!=null)
                rating.setDeliveryBoyComment(order.getRating().getDeliveryBoyComment());
            if(order.getRating().getDeliveryBoyRating()!=null)
                rating.setDeliveryBoyRating(order.getRating().getDeliveryBoyRating());
            if(order.getRating().getCustomerComment()!=null)
                rating.setCustomerComment(order.getRating().getCustomerComment());
            if(order.getRating().getRatingIssues() != null)
                rating.setRatingIssues(order.getRating().getRatingIssues());
            orderEntity.setRating(rating);
        }

        /*Setting OrderCancelEntity */
        OrderCancelEntity orderCancelEntity = orderEntity.getOrderCancel();
        if(orderCancelEntity != null)
            throw new YSException("ORD006");
        else
            orderCancelEntity = new OrderCancelEntity();
        orderCancelEntity.setCancelledDate(DateUtil.getCurrentTimestampSQL());
        orderCancelEntity.setReason(order.getOrderCancel().getReason());
        orderCancelEntity.setReasonDetails(order.getOrderCancel().getReasonDetails());
        orderCancelEntity.setJobOrderStatus(orderEntity.getOrderStatus());
        UserEntity user = new UserEntity();
        user.setId(userId);
        orderCancelEntity.setUser(user);
        orderCancelEntity.setOrder(orderEntity);

        /* updating order histories */
        List<DBoyOrderHistoryEntity> dBoyOrderHistoryEntities = orderEntity.getdBoyOrderHistories();
        for (DBoyOrderHistoryEntity dBoyOrderHistoryEntity : dBoyOrderHistoryEntities) {
            if (order.getDeliveryBoy() != null) {
                if (dBoyOrderHistoryEntity.getDeliveryStatus().equals(DeliveryStatus.PENDING)
                        && dBoyOrderHistoryEntity.getDeliveryBoy().getId().equals(order.getDeliveryBoy().getId())) {
                    dBoyOrderHistoryEntity.setEndLatitude(order.getDeliveryBoy().getLatitude());
                    dBoyOrderHistoryEntity.setEndLongitude(order.getDeliveryBoy().getLongitude());
                    dBoyOrderHistoryEntity.setDeliveryStatus(DeliveryStatus.CANCELLED);
                    dBoyOrderHistoryEntity.setOrderCompletedAt(DateUtil.getCurrentTimestampSQL());
                    /* Calculates the distance travelled by a delivery boy and his earning to that specific order. */
                    if(systemPropertyService.readPrefValue(PreferenceType.DELIVERY_FEE_CHARGING_MODEL).equals("Distance Based")){
                        BigDecimal paidToCourier = this.getCourierBoyEarningAtAnyStage(dBoyOrderHistoryEntity, orderEntity.getOrderStatus());
                        orderEntity.getCourierTransaction().setPaidToCourier(paidToCourier);
                        orderEntity.getDeliveryBoy().setTotalEarnings(orderEntity.getDeliveryBoy().getTotalEarnings().add(paidToCourier));
                    }
                    if(BigDecimalUtil.isGreaterThen(orderEntity.getGrandTotal(), BigDecimal.ZERO)){
                        if (orderEntity.getOrderStatus().equals(JobOrderStatus.AT_STORE)) {
                            boolean partnerShipStatus = orderEntity.getPartnershipStatus();//merchantDaoService.findPartnerShipStatusFromOrderId(order.getId());
                            courierBoyAccountingsAfterTakingOrder(orderEntity.getDeliveryBoy(), orderEntity, partnerShipStatus);
                            courierBoyAccountingAfterOrderCancel(orderEntity.getDeliveryBoy(), orderEntity);
                        }else if (orderEntity.getOrderStatus().equals(JobOrderStatus.IN_ROUTE_TO_DELIVERY)) {
                            courierBoyAccountingAfterOrderCancel(orderEntity.getDeliveryBoy(), orderEntity);
                        }
                    }
                    break;
                }
            }
        }

        /* updating delivery boy */
        DeliveryBoyEntity deliveryBoyEntity = orderEntity.getDeliveryBoy();
        if(deliveryBoyEntity != null){
            deliveryBoyEntity.setActiveOrderNo(deliveryBoyDaoService.getNumberOfActiveOrders(deliveryBoyEntity.getId()) - 1);
            deliveryBoyEntity.setTotalOrderUndelivered(deliveryBoyEntity.getTotalOrderUndelivered()+1);
            if(deliveryBoyEntity.getActiveOrderNo() <= 0){
                deliveryBoyEntity.setActiveOrderNo(0);
                deliveryBoyEntity.setAvailabilityStatus(DBoyStatus.FREE);
            }
        }

        this.walletAdjustmentAfterOrderCancel(orderEntity);
        orderEntity.setOrderCancel(orderCancelEntity);
        orderEntity.setOrderStatus(JobOrderStatus.CANCELLED);
        orderEntity.setDeliveryStatus(DeliveryStatus.CANCELLED);
        boolean status = orderDaoService.update(orderEntity);
        /*if(status && BigDecimalUtil.isGreaterThen(orderEntity.getCustomer().getWalletAmount(), BigDecimal.ZERO)){
            this.adjustBalanceOfOtherOrders(orderEntity, orderEntity.getCustomer().getWalletAmount());
        }*/
        /* Now Calculate Average Rating for Customer (Appended By Surendra) */
        List<Integer> ratings = orderDaoService.getCustomerRatings(orderEntity.getCustomer().getId());
        if(ratings !=null && ratings.size()> 0){
            BigDecimal totalRate = BigDecimal.ZERO;

            for(Integer rate : ratings){
                totalRate = totalRate.add(new BigDecimal(rate));
            }

            BigDecimal averageRating = getAverageRating(ratings.size(), totalRate);

            //Now Update the average rating and deactivate the customer if
            customerDaoService.updateAverageRating(averageRating, orderEntity.getCustomer().getId());

            //Lets Change User Status
        /* Less then or equal 1 means Current Delivery Also In Session (That has not completed) */
            if(orderDaoService.hasCustomerRunningOrders(orderEntity.getCustomer().getId()) <= 1){
                if(BigDecimalUtil.isLessThen(averageRating, new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.CUSTOMER_DEFAULT_RATING)))){
                    //Deactivate User
                    log.info("Deactivating Customer id : " + orderEntity.getCustomer().getId());
                    userDaoService.deactivateUser(orderEntity.getCustomer().getUser().getId());
                }
            }

        /* Ended By Surendra */
        }


        if(status){
            UserDeviceEntity userDevice = userDeviceDaoService.getUserDeviceInfoFromOrderId(order.getId());
            String message = MessageBundle.getMessage("CPN007","push_notification.properties");
            String extraDetail = order.getId().toString()+"/status/"+JobOrderStatus.CANCELLED.toString();
            PushNotificationUtil.sendPushNotification(userDevice, message, NotifyTo.CUSTOMER, PushNotificationRedirect.ORDER, extraDetail);
        }
        return status;
    }

    private void walletAdjustmentAfterOrderCancel(OrderEntity orderEntity) throws Exception {
        JobOrderStatus orderStatus = orderEntity.getOrderStatus();
        BigDecimal customerWalletAmount = BigDecimalUtil.checkNull(orderEntity.getCustomer().getWalletAmount());
        BigDecimal paidFromWallet = orderEntity.getPaidFromWallet();
        BigDecimal paidFromCOD = orderEntity.getPaidFromCOD();
        String currency = systemPropertyService.readPrefValue(PreferenceType.CURRENCY);

        if(orderStatus.equals(JobOrderStatus.IN_ROUTE_TO_DELIVERY)){
            if(BigDecimalUtil.isGreaterThen(orderEntity.getPaidFromCOD(), BigDecimal.ZERO)){
                paidFromWallet = orderEntity.getGrandTotal();
                customerWalletAmount = customerWalletAmount.subtract(orderEntity.getPaidFromCOD());
                String remarks = MessageBundle.getMessage("WTM001", "push_notification.properties");
                remarks = String.format(remarks, currency, orderEntity.getGrandTotal(), orderEntity.getStore().getName(), orderEntity.getId());
                this.setWalletTransaction(orderEntity, orderEntity.getPaidFromCOD(), AccountType.CREDIT, PaymentMode.WALLET, remarks, customerWalletAmount);

                paidFromCOD = BigDecimal.ZERO;
            }
        }

        if(orderStatus.equals(JobOrderStatus.AT_STORE)){
            Boolean itemPurchased = false;
            for (ItemsOrderEntity itemsOrder: orderEntity.getItemsOrder()){
                if (itemsOrder.getPurchaseStatus() != null && itemsOrder.getPurchaseStatus()){
                    itemPurchased = true;
                    break;
                }
            }
            if(itemPurchased){
                paidFromWallet = orderEntity.getGrandTotal();
                customerWalletAmount = customerWalletAmount.subtract(paidFromWallet);
                String remarks = MessageBundle.getMessage("WTM001", "push_notification.properties");
                remarks = String.format(remarks, currency, orderEntity.getGrandTotal(), orderEntity.getStore().getName(), orderEntity.getId());
                this.setWalletTransaction(orderEntity, paidFromWallet, AccountType.CREDIT, PaymentMode.WALLET, remarks, customerWalletAmount);
                paidFromCOD = BigDecimal.ZERO;
            }
        }

        OrderPaymentUtil.calculateOrderWalletPaymentOnOrderCancel(orderEntity);
        orderEntity.setPaidFromCOD(paidFromCOD);
        orderEntity.setPaidFromWallet(paidFromWallet);

        orderEntity.getCustomer().setRmBonusAmount(BigDecimalUtil.checkNull(orderEntity.getCustomer().getRmBonusAmount()).subtract(BigDecimalUtil.checkNull(orderEntity.getPaidBonusAmount())));
        orderEntity.getCustomer().setRmCashBackAmount(BigDecimalUtil.checkNull(orderEntity.getCustomer().getRmCashBackAmount()).subtract(BigDecimalUtil.checkNull(orderEntity.getPaidCashBackAmount())));
        orderEntity.getCustomer().setRmFundTransferAmount(BigDecimalUtil.checkNull(orderEntity.getCustomer().getRmFundTransferAmount()).subtract(BigDecimalUtil.checkNull(orderEntity.getPaidFundTransferAmount())));
        orderEntity.getCustomer().setWalletAmount(customerWalletAmount);


        /*if (orderStatus.equals(JobOrderStatus.ORDER_ACCEPTED) && BigDecimalUtil.isGreaterThen(orderEntity.getPaidFromWallet(), BigDecimal.ZERO)) {
            log.info("ORDER ACCEPTED STAGE: No Change in item price before cancelling for order ID:" + orderEntity.getId());
            customerWalletAmount = customerWalletAmount.add(paidFromWallet);
            String remarks = MessageBundle.getMessage("WTM012", "push_notification.properties");
            remarks = String.format(remarks, currency, paidFromWallet, orderEntity.getId());
            this.setWalletTransaction(orderEntity, paidFromWallet, AccountType.CREDIT, orderEntity.getPaymentMode(), remarks, customerWalletAmount);
            paidFromWallet = BigDecimal.ZERO;
        }else if (orderStatus.equals(JobOrderStatus.IN_ROUTE_TO_PICK_UP)) {
            log.info("IN_ROUTE_TO_PICK_UP: No Change in item price before cancelling for order ID:" + orderEntity.getId());
            BigDecimal deliveryCharge = orderEntity.getDeliveryCharge();
            if(orderEntity.getPaymentMode().equals(PaymentMode.CASH_ON_DELIVERY)){
                if(BigDecimalUtil.isGreaterThen(deliveryCharge, BigDecimal.ZERO)){
                    if(BigDecimalUtil.isGreaterThenOrEqualTo(customerWalletAmount, deliveryCharge)){
                        customerWalletAmount = customerWalletAmount.subtract(deliveryCharge);
                        String remarks = MessageBundle.getMessage("WTM008", "push_notification.properties");
                        remarks = String.format(remarks, currency, deliveryCharge, orderEntity.getId());
                        this.setWalletTransaction(orderEntity, deliveryCharge, AccountType.DEBIT, PaymentMode.WALLET, remarks, customerWalletAmount);
                    }else{
                        orderEntity.setShortFallAmount(deliveryCharge.subtract(customerWalletAmount));
                        if(BigDecimalUtil.isGreaterThen(customerWalletAmount, BigDecimal.ZERO)){
                            String remarks = MessageBundle.getMessage("WTM008", "push_notification.properties");
                            remarks = String.format(remarks, currency, customerWalletAmount, orderEntity.getId());
                            this.setWalletTransaction(orderEntity, customerWalletAmount, AccountType.DEBIT, PaymentMode.WALLET, remarks, BigDecimal.ZERO);
                            customerWalletAmount = BigDecimal.ZERO;
                        }
                        orderEntity.getCustomer().setShortFallAmount(BigDecimalUtil.checkNull(orderEntity.getCustomer().getShortFallAmount()).add(orderEntity.getShortFallAmount()));
                    }
                }
            }else{
                BigDecimal adjustAmount = paidFromWallet.subtract(deliveryCharge);
                if(BigDecimalUtil.isGreaterThen(adjustAmount, BigDecimal.ZERO)){
                    customerWalletAmount = customerWalletAmount.add(adjustAmount);
                    paidFromWallet = deliveryCharge;
                    String remarks = MessageBundle.getMessage("WTM006", "push_notification.properties");
                    remarks = String.format(remarks, currency, adjustAmount, orderEntity.getId(), currency, deliveryCharge);
                    this.setWalletTransaction(orderEntity, adjustAmount, AccountType.CREDIT, PaymentMode.WALLET, remarks, customerWalletAmount);
                }

            }
            paidFromCOD = BigDecimal.ZERO;
        } else if (orderStatus.equals(JobOrderStatus.AT_STORE)) {
            if (BigDecimalUtil.isEqualTo(paidFromCOD, BigDecimal.ZERO)) {
                log.info("No Change in item price before cancelling for order ID:" + orderEntity.getId());
            } else {
                log.info("Change in item price before cancelling for order ID:" + orderEntity.getId());
                if (BigDecimalUtil.isGreaterThen(paidFromCOD, BigDecimal.ZERO)) {
                    paidFromWallet = orderEntity.getGrandTotal();
                    *//* Adjusting balance for shortfall amount for this order *//*
                    if(BigDecimalUtil.isGreaterThenOrEqualTo(customerWalletAmount, paidFromCOD)){
                       customerWalletAmount = customerWalletAmount.subtract(paidFromCOD);
                       String remarks = MessageBundle.getMessage("WTM008", "push_notification.properties");
                       remarks = String.format(remarks, currency, paidFromCOD, orderEntity.getId());
                       this.setWalletTransaction(orderEntity, paidFromCOD, AccountType.DEBIT, PaymentMode.WALLET, remarks, customerWalletAmount);
                    }else{
                        orderEntity.setShortFallAmount(paidFromCOD.subtract(customerWalletAmount));
                        if(BigDecimalUtil.isGreaterThen(customerWalletAmount, BigDecimal.ZERO)){
                            String remarks = MessageBundle.getMessage("WTM008", "push_notification.properties");
                            remarks = String.format(remarks, currency, customerWalletAmount, orderEntity.getId());
                            this.setWalletTransaction(orderEntity, customerWalletAmount, AccountType.DEBIT, PaymentMode.WALLET, remarks, BigDecimal.ZERO);
                            customerWalletAmount = BigDecimal.ZERO;
                        }
                        orderEntity.getCustomer().setShortFallAmount(BigDecimalUtil.checkNull(orderEntity.getCustomer().getShortFallAmount()).add(orderEntity.getShortFallAmount()));
                    }
                    paidFromCOD = BigDecimal.ZERO;
                } else if (BigDecimalUtil.isLessThen(paidFromCOD, BigDecimal.ZERO)) {
                    String remarks = MessageBundle.getMessage("WTM007", "push_notification.properties");
                    remarks = String.format(remarks, currency, paidFromCOD.abs(), orderEntity.getId());
                    this.setWalletTransaction(orderEntity, paidFromCOD.abs(), AccountType.CREDIT, PaymentMode.WALLET, remarks, customerWalletAmount.add(paidFromCOD.abs()));

                    paidFromWallet = orderEntity.getGrandTotal();
                    customerWalletAmount = customerWalletAmount.add(paidFromCOD.abs());
                    paidFromCOD = BigDecimal.ZERO;
                }
            }

        } else if (orderStatus.equals(JobOrderStatus.IN_ROUTE_TO_DELIVERY)) {
            log.info("Cancelled order while in route to delivery");
            if (BigDecimalUtil.isEqualTo(paidFromCOD, BigDecimal.ZERO)) {
                log.info("No Change in item price before cancelling for order ID:" + orderEntity.getId());
            } else {
                log.info("Change in item price before cancelling for order ID:" + orderEntity.getId());
                if (BigDecimalUtil.isGreaterThen(paidFromCOD, BigDecimal.ZERO)) {
                    paidFromWallet = orderEntity.getGrandTotal();
                    *//* Adjusting balance for shortfall amount for this order *//*
                    if(BigDecimalUtil.isGreaterThenOrEqualTo(customerWalletAmount, paidFromCOD)){
                        customerWalletAmount = customerWalletAmount.subtract(paidFromCOD);
                        String remarks = MessageBundle.getMessage("WTM008", "push_notification.properties");
                        remarks = String.format(remarks, currency, paidFromCOD, orderEntity.getId());
                        this.setWalletTransaction(orderEntity, paidFromCOD, AccountType.DEBIT, PaymentMode.WALLET, remarks, customerWalletAmount);
                    }else{
                        orderEntity.setShortFallAmount(paidFromCOD.subtract(customerWalletAmount));
                        if(BigDecimalUtil.isGreaterThen(customerWalletAmount, BigDecimal.ZERO)){
                            String remarks = MessageBundle.getMessage("WTM008", "push_notification.properties");
                            remarks = String.format(remarks, currency, customerWalletAmount, orderEntity.getId());
                            this.setWalletTransaction(orderEntity, customerWalletAmount, AccountType.DEBIT, PaymentMode.WALLET, remarks, BigDecimal.ZERO);
                            customerWalletAmount = BigDecimal.ZERO;
                        }
                        orderEntity.getCustomer().setShortFallAmount(BigDecimalUtil.checkNull(orderEntity.getCustomer().getShortFallAmount()).add(orderEntity.getShortFallAmount()));
                    }
                    paidFromCOD = BigDecimal.ZERO;
                } else if (BigDecimalUtil.isLessThen(paidFromCOD, BigDecimal.ZERO)) {
                    String remarks = MessageBundle.getMessage("WTM007", "push_notification.properties");
                    remarks = String.format(remarks, currency, paidFromCOD.abs(), orderEntity.getId());
                    this.setWalletTransaction(orderEntity, paidFromCOD.abs(), AccountType.CREDIT, PaymentMode.WALLET, remarks, customerWalletAmount.add(paidFromCOD.abs()));

                    paidFromWallet = orderEntity.getGrandTotal();
                    customerWalletAmount = customerWalletAmount.add(paidFromCOD.abs());
                    paidFromCOD = BigDecimal.ZERO;
                }
            }
        }
        orderEntity.setPaidFromCOD(paidFromCOD);
        orderEntity.setPaidFromWallet(paidFromWallet);
        orderEntity.getCustomer().setWalletAmount(customerWalletAmount);*/
    }

    private BigDecimal getCourierBoyEarningAtAnyStage(DBoyOrderHistoryEntity dBoyOrderHistory, JobOrderStatus orderStatus) throws Exception {
        BigDecimal DBOY_ADDITIONAL_PER_KM_CHARGE = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DBOY_ADDITIONAL_PER_KM_CHARGE));
        BigDecimal ADDITIONAL_KM_FREE_LIMIT = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.ADDITIONAL_KM_FREE_LIMIT));
        DistanceType distanceType = DistanceType.fromInt(Integer.parseInt(systemPropertyService.readPrefValue(PreferenceType.AIR_OR_ACTUAL_DISTANCE_SWITCH)));

        BigDecimal deliveryCost = BigDecimal.ZERO;
        BigDecimal chargeableDistance = BigDecimal.ZERO;

        if (orderStatus.equals(JobOrderStatus.IN_ROUTE_TO_PICK_UP)) {
            String startAddress[] = {GeoCodingUtil.getLatLong(dBoyOrderHistory.getStartLatitude(), dBoyOrderHistory.getStartLongitude())};
            String endAddress[] = {GeoCodingUtil.getLatLong(dBoyOrderHistory.getEndLatitude(), dBoyOrderHistory.getEndLongitude())};
            if (distanceType.equals(DistanceType.AIR_DISTANCE))
                chargeableDistance = BigDecimalUtil.getDistanceInKiloMeters(GeoCodingUtil.getListOfAssumedDistance(startAddress[0], endAddress).get(0));
            else
                chargeableDistance = BigDecimalUtil.getDistanceInKiloMeters(GeoCodingUtil.getListOfDistances(startAddress, endAddress).get(0));
            if (BigDecimalUtil.isGreaterThen(chargeableDistance, ADDITIONAL_KM_FREE_LIMIT))
                deliveryCost = chargeableDistance.subtract(ADDITIONAL_KM_FREE_LIMIT).multiply(DBOY_ADDITIONAL_PER_KM_CHARGE);
            dBoyOrderHistory.setDistanceTravelled(chargeableDistance);
        } else if (orderStatus.equals(JobOrderStatus.AT_STORE)) {
            if(itemsOrderDaoService.getNumberOfUnprocessedItems(dBoyOrderHistory.getOrder().getId()) > 0){
                throw new YSException("ORD019");
            }
            chargeableDistance = dBoyOrderHistory.getOrder().getSystemChargeableDistance();
            if (BigDecimalUtil.isGreaterThen(chargeableDistance, ADDITIONAL_KM_FREE_LIMIT))
                deliveryCost = chargeableDistance.subtract(ADDITIONAL_KM_FREE_LIMIT).multiply(DBOY_ADDITIONAL_PER_KM_CHARGE);
            dBoyOrderHistory.setDistanceTravelled(chargeableDistance);
            //courierBoyAccountingsAfterTakingOrder(dBoyOrderHistory.getDeliveryBoy(), dBoyOrderHistory.getOrder(), dBoyOrderHistory.getOrder().getStore().getStoresBrand().getMerchant().getPartnershipStatus());
        } else if (orderStatus.equals(JobOrderStatus.IN_ROUTE_TO_DELIVERY)) {
            BigDecimal DBOY_PER_KM_CHARGE_UPTO_NKM = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DBOY_PER_KM_CHARGE_UPTO_NKM));
            BigDecimal DBOY_PER_KM_CHARGE_ABOVE_NKM = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DBOY_PER_KM_CHARGE_ABOVE_NKM));
            BigDecimal DEFAULT_NKM_DISTANCE = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DEFAULT_NKM_DISTANCE));
            BigDecimal DBOY_COMMISSION = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DBOY_COMMISSION));
            BigDecimal customerSideDistance = BigDecimal.ZERO;
            BigDecimal customerSideDeliveryCost = BigDecimal.ZERO;

            chargeableDistance = dBoyOrderHistory.getOrder().getSystemChargeableDistance();
            if (BigDecimalUtil.isGreaterThen(chargeableDistance, ADDITIONAL_KM_FREE_LIMIT))
                deliveryCost = chargeableDistance.subtract(ADDITIONAL_KM_FREE_LIMIT).multiply(DBOY_ADDITIONAL_PER_KM_CHARGE);
            String startAddress[] = {GeoCodingUtil.getLatLong(dBoyOrderHistory.getOrder().getStore().getLatitude(), dBoyOrderHistory.getOrder().getStore().getLongitude())};
            String endAddress[] = {GeoCodingUtil.getLatLong(dBoyOrderHistory.getEndLatitude(), dBoyOrderHistory.getEndLongitude())};
            if (distanceType.equals(DistanceType.AIR_DISTANCE))
                customerSideDistance = BigDecimalUtil.getDistanceInKiloMeters(GeoCodingUtil.getListOfAssumedDistance(startAddress[0], endAddress).get(0));
            else
                customerSideDistance = BigDecimalUtil.getDistanceInKiloMeters(GeoCodingUtil.getListOfDistances(startAddress, endAddress).get(0));
            if(BigDecimalUtil.isLessThen(customerSideDistance, DEFAULT_NKM_DISTANCE))
                customerSideDeliveryCost = DBOY_PER_KM_CHARGE_UPTO_NKM.multiply(new BigDecimal(dBoyOrderHistory.getOrder().getSurgeFactor()));
            else
                customerSideDeliveryCost = customerSideDistance.multiply(DBOY_PER_KM_CHARGE_ABOVE_NKM).multiply(new BigDecimal(dBoyOrderHistory.getOrder().getSurgeFactor()));
            customerSideDeliveryCost = BigDecimalUtil.percentageOf(customerSideDeliveryCost, DBOY_COMMISSION);
            deliveryCost = deliveryCost.add(customerSideDeliveryCost);
            dBoyOrderHistory.setDistanceTravelled(chargeableDistance.add(customerSideDistance));
        }
        Double minuteDiff = DateUtil.getMinDiff(System.currentTimeMillis(), dBoyOrderHistory.getOrder().getOrderDate().getTime());
        Integer GRACE_TIME = Integer.parseInt(systemPropertyService.readPrefValue(PreferenceType.DBOY_GRESS_TIME));
        Integer totalTime = minuteDiff.intValue() + GRACE_TIME;
        int remainingTime = dBoyOrderHistory.getOrder().getRemainingTime();
        if(remainingTime < totalTime) {
            BigDecimal deductionPercent = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DEDUCTION_PERCENT));
            BigDecimal deductAmount = BigDecimalUtil.percentageOf(deliveryCost, deductionPercent);
            deliveryCost = deliveryCost.subtract(deductAmount);
            dBoyOrderHistory.getOrder().getCourierTransaction().setProfit(dBoyOrderHistory.getOrder().getCourierTransaction().getProfit().add(deductAmount));
        }
        dBoyOrderHistory.setAmountEarned(deliveryCost);
        return deliveryCost;
    }

    private void courierBoyAccountingsAfterTakingOrder(DeliveryBoyEntity deliveryBoy, OrderEntity order, Boolean partnerShipStatus) throws Exception{
        BigDecimal orderAmount = order.getTotalCost().add(order.getItemServiceAndVatCharge());
        orderAmount = orderAmount.subtract(BigDecimalUtil.checkNull(order.getDiscountFromStore()));
       // BigDecimal orderAmtReceived = order.getGrandTotal();
        BigDecimal walletAmount = deliveryBoy.getWalletAmount();
        BigDecimal bankAmount = deliveryBoy.getBankAmount();
        BigDecimal availableAmount = deliveryBoy.getAvailableAmount();

        log.info("== BEFORE TAKING ORDER == \n " +
                "Order Amount: " + orderAmount
                + "\t Wallet Amount: " + walletAmount
                + "\t Bank Amount: " + bankAmount
                + "\t Available Amount: " + availableAmount);

        if (!partnerShipStatus) {
            if (availableAmount.compareTo(orderAmount) != -1) {
                if (walletAmount.compareTo(orderAmount) != -1) {
                    walletAmount = walletAmount.subtract(orderAmount);
                } else {
                    bankAmount = bankAmount.subtract(orderAmount.subtract(walletAmount));
                    walletAmount = BigDecimal.ZERO;
                }
                availableAmount = availableAmount.subtract(orderAmount);
            } else {
                throw new YSException("ERR017");
            }
            log.info("set courier transaction account");
            // set courier transaction account
            List<CourierTransactionAccountEntity> courierTransactionAccounts = new ArrayList<>();
            CourierTransactionAccountEntity courierTransactionAccount = new CourierTransactionAccountEntity();
            courierTransactionAccount.setBalance(availableAmount);
            courierTransactionAccount.setDeliveryBoy(deliveryBoy);
            courierTransactionAccount.setOrder(order);
            courierTransactionAccount.setDescription(MessageBundle.getShoppersTransactionDescription("STD001"));
            courierTransactionAccount.setCr(orderAmount);
            courierTransactionAccount.setDateTime(new Timestamp(System.currentTimeMillis()));
            courierTransactionAccounts.add(courierTransactionAccount);
            order.setCourierTransactionAccount(courierTransactionAccounts);
        }
        deliveryBoy.setWalletAmount(walletAmount);
        deliveryBoy.setBankAmount(bankAmount);
        deliveryBoy.setAvailableAmount(availableAmount);

        log.info("== AFTER TAKING ORDER == "
                + "\n Order Amount: " + orderAmount
                + "\t Wallet Amount: " + walletAmount
                + "\t Bank Amount: " + bankAmount
                + "\t Available Amount: " + availableAmount);
    }


    private void setCourierTransactionAccountDescriptionDelivered(CourierTransactionAccountEntity courierTransactionAccount, OrderEntity order){

        if(order.getPartnershipStatus() && order.getPaymentMode().equals(PaymentMode.CASH_ON_DELIVERY)){
            if(BigDecimalUtil.isEqualTo(order.getPaidFromWallet(), BigDecimal.ZERO)){
                courierTransactionAccount.setDescription(MessageBundle.getShoppersTransactionDescription("STD004"));
            }else {
                courierTransactionAccount.setDescription(MessageBundle.getShoppersTransactionDescription("STD0014"));
            }
        } else if (!order.getPartnershipStatus() && order.getPaymentMode().equals(PaymentMode.CASH_ON_DELIVERY)){
            if(BigDecimalUtil.isEqualTo(order.getPaidFromWallet(), BigDecimal.ZERO)){
                courierTransactionAccount.setDescription(MessageBundle.getShoppersTransactionDescription("STD005"));
            }else {
                courierTransactionAccount.setDescription(MessageBundle.getShoppersTransactionDescription("STD0015"));
            }
        }  else if(order.getPartnershipStatus() && order.getPaymentMode().equals(PaymentMode.WALLET)){
            if(BigDecimalUtil.isEqualTo(order.getPaidFromCOD(), BigDecimal.ZERO)){
                courierTransactionAccount.setDescription(MessageBundle.getShoppersTransactionDescription("STD003"));
            }else {
                courierTransactionAccount.setDescription(MessageBundle.getShoppersTransactionDescription("STD006"));
            }
        } else if(!order.getPartnershipStatus() && order.getPaymentMode().equals(PaymentMode.WALLET)) {
            if(BigDecimalUtil.isEqualTo(order.getPaidFromCOD(), BigDecimal.ZERO)){
                courierTransactionAccount.setDescription(MessageBundle.getShoppersTransactionDescription("STD002"));
            }else {
                courierTransactionAccount.setDescription(MessageBundle.getShoppersTransactionDescription("STD007"));
            }
        }

    }

    private void setCourierTransactionAccountDescriptionCanceled(CourierTransactionAccountEntity courierTransactionAccount, OrderEntity order) throws Exception{

            if(order.getPartnershipStatus() && BigDecimalUtil.isGreaterThenZero(order.getPaidFromWallet()) && BigDecimalUtil.isGreaterThenZero(order.getPaidFromCOD())){
                if(order.getPaymentMode().equals(PaymentMode.WALLET))
                    courierTransactionAccount.setDescription(MessageBundle.getShoppersTransactionDescription("STD008")); //wallet+COD partner
                else
                    courierTransactionAccount.setDescription(MessageBundle.getShoppersTransactionDescription("STD0016")); //cod+wallet partner
            } else if(!order.getPartnershipStatus() && BigDecimalUtil.isGreaterThenZero(order.getPaidFromWallet()) && BigDecimalUtil.isGreaterThenZero(order.getPaidFromCOD())) {
                if(order.getPaymentMode().equals(PaymentMode.WALLET))
                    courierTransactionAccount.setDescription(MessageBundle.getShoppersTransactionDescription("STD009")); //wallet+COD non partner
                else
                    courierTransactionAccount.setDescription(MessageBundle.getShoppersTransactionDescription("STD0017")); //cod+wallet non partner

            } else if(order.getPartnershipStatus() && !BigDecimalUtil.isGreaterThenZero(order.getPaidFromWallet()) && BigDecimalUtil.isGreaterThenZero(order.getPaidFromCOD())) {
                courierTransactionAccount.setDescription(MessageBundle.getShoppersTransactionDescription("STD0012")); //cod partner
            } else if(!order.getPartnershipStatus() && !BigDecimalUtil.isGreaterThenZero(order.getPaidFromWallet()) && BigDecimalUtil.isGreaterThenZero(order.getPaidFromCOD())) {
                courierTransactionAccount.setDescription(MessageBundle.getShoppersTransactionDescription("STD0013")); //cod non partner
            } else if(order.getPartnershipStatus() && BigDecimalUtil.isGreaterThenZero(order.getPaidFromWallet()) && !BigDecimalUtil.isGreaterThenZero(order.getPaidFromCOD())) {
                courierTransactionAccount.setDescription(MessageBundle.getShoppersTransactionDescription("STD0010")); //wallet partner
            } else if(!order.getPartnershipStatus() && BigDecimalUtil.isGreaterThenZero(order.getPaidFromWallet()) && !BigDecimalUtil.isGreaterThenZero(order.getPaidFromCOD())) {
                courierTransactionAccount.setDescription(MessageBundle.getShoppersTransactionDescription("STD0011")); //wallet non partner
            }

            courierTransactionAccount.setNote(MessageBundle.getShoppersTransactionDescription("STD0021")+" "+systemPropertyService.readPrefValue(PreferenceType.CURRENCY)+ order.getGrandTotal());
    }

    private void courierBoyAccountingsAfterOrderDelivery(DeliveryBoyEntity deliveryBoy, OrderEntity order) {
        BigDecimal orderAmtReceived = order.getPaidFromCOD();
        BigDecimal walletAmount = deliveryBoy.getWalletAmount();
        BigDecimal availableAmount = deliveryBoy.getAvailableAmount();

        availableAmount = availableAmount.add(orderAmtReceived);
        walletAmount = walletAmount.add(orderAmtReceived);
        deliveryBoy.setWalletAmount(walletAmount);
        deliveryBoy.setAvailableAmount(availableAmount);

        log.info("== AFTER ORDER DELIVERY =="
                + "\n Wallet Amount: " + walletAmount
                + "\t Available Amount: " + availableAmount
                + "\t Amount to be Submitted: " + walletAmount);

        log.info("set courier transaction account");
        List<CourierTransactionAccountEntity> courierTransactionAccounts = new ArrayList<>();

        CourierTransactionAccountEntity courierTransactionAccountDr = new CourierTransactionAccountEntity();
        courierTransactionAccountDr.setBalance(availableAmount.add(order.getPaidFromWallet()));
        courierTransactionAccountDr.setDeliveryBoy(deliveryBoy);
        courierTransactionAccountDr.setOrder(order);
        courierTransactionAccountDr.setDr(order.getGrandTotal());
        courierTransactionAccountDr.setDateTime(new Timestamp(System.currentTimeMillis()));
        courierTransactionAccounts.add(courierTransactionAccountDr);
        setCourierTransactionAccountDescriptionDelivered(courierTransactionAccountDr, order);
        courierTransactionAccounts.add(courierTransactionAccountDr);

        if(BigDecimalUtil.isGreaterThenZero(order.getPaidFromWallet())){
            CourierTransactionAccountEntity courierTransactionAccountCr = new CourierTransactionAccountEntity();
            courierTransactionAccountCr.setBalance(availableAmount);
            courierTransactionAccountCr.setDeliveryBoy(deliveryBoy);
            courierTransactionAccountCr.setOrder(order);
            courierTransactionAccountCr.setCr(order.getPaidFromWallet());
            courierTransactionAccountCr.setDateTime(new Timestamp(System.currentTimeMillis()));
            setCourierTransactionAccountDescriptionDelivered(courierTransactionAccountCr, order);
            courierTransactionAccounts.add(courierTransactionAccountCr);
        }
        order.setCourierTransactionAccount(courierTransactionAccounts);
    }

    private void courierBoyAccountingAfterOrderCancel(DeliveryBoyEntity deliveryBoy, OrderEntity order) throws Exception{
        Boolean itemPurchased = false;
        for (ItemsOrderEntity itemsOrder: order.getItemsOrder()){
            if (itemsOrder.getPurchaseStatus() != null && itemsOrder.getPurchaseStatus()){
                itemPurchased = true;
                break;
            }
        }

        if(itemPurchased) {
            if(order.getAttachments() == null || order.getAttachments().size()==0)
                throw new YSException("V1ORD025");

            BigDecimal orderAmtReceived = order.getGrandTotal();
            BigDecimal walletAmount = deliveryBoy.getWalletAmount();
            BigDecimal availableAmount = deliveryBoy.getAvailableAmount();

            availableAmount = availableAmount.add(orderAmtReceived);
            walletAmount = walletAmount.add(orderAmtReceived);
            deliveryBoy.setWalletAmount(walletAmount);
            deliveryBoy.setAvailableAmount(availableAmount);

            log.info("== AFTER ORDER DELIVERY =="
                    + "\n Wallet Amount: " + walletAmount
                    + "\t Available Amount: " + availableAmount
                    + "\t Amount to be Submitted: " + walletAmount);


            log.info("set courier transaction account");
            List<CourierTransactionAccountEntity> courierTransactionAccounts = new ArrayList<>();

            CourierTransactionAccountEntity courierTransactionAccountDr = new CourierTransactionAccountEntity();
            courierTransactionAccountDr.setBalance(availableAmount);
            courierTransactionAccountDr.setDeliveryBoy(deliveryBoy);
            courierTransactionAccountDr.setOrder(order);
            courierTransactionAccountDr.setDr(order.getGrandTotal());
            courierTransactionAccountDr.setDateTime(new Timestamp(System.currentTimeMillis()));
            setCourierTransactionAccountDescriptionCanceled(courierTransactionAccountDr, order);
            courierTransactionAccounts.add(courierTransactionAccountDr);
            order.setCourierTransactionAccount(courierTransactionAccounts);
        }
    }


    @Override
    public OrderSummaryDto getShoppingList(Integer orderId) throws Exception {
        OrderEntity order = orderDaoService.find(orderId);
        if (order == null) {
            throw new YSException("VLD017");
        }
        MerchantEntity merchant = merchantDaoService.getMerchantByOrderId(orderId);
        OrderSummaryDto orderSummary = new OrderSummaryDto();
        orderSummary.setMinimumOrderAmount(order.getStore().getStoresBrand().getMinOrderAmount());

        List<ItemsOrderEntity> itemsOrder = order.getItemsOrder();
        List<ItemsOrderEntity> itemsOrderEntities = new ArrayList<ItemsOrderEntity>();
        ItemsOrderEntity itemsOrderEntity = null;
        Integer countCustomItem = 0;
        for (ItemsOrderEntity itemOrder : itemsOrder) {
            if(itemOrder.getNote() !=null &&  itemOrder.getNote().equals(""))
                itemOrder.setNote(null);

            itemsOrderEntity = itemOrder;
            if (itemOrder.getItem() != null) {
                ItemEntity item = new ItemEntity();
                item.setId(itemOrder.getItem().getId());
                item.setName(itemOrder.getItem().getName());
                item.setAdditionalOffer(itemOrder.getItem().getAdditionalOffer());
                item.setUnitPrice(itemOrder.getItem().getUnitPrice());
                itemsOrderEntity.setItem(item);
            }

            if(itemOrder.getCustomItem() != null && itemOrder.getItemTotal() == null && itemOrder.getAvailabilityStatus()){
                itemOrder.setItemTotal(minusOne);
                countCustomItem++;
            }

            itemsOrderEntity.setCustomItem(itemOrder.getCustomItem());
            List<ItemsOrderAttributeEntity> itemsOrderAttributeEntities = new ArrayList<ItemsOrderAttributeEntity>();
            for(ItemsOrderAttributeEntity itemOrderAttribute: itemOrder.getItemOrderAttributes()){
                itemsOrderAttributeEntities.add(itemOrderAttribute);
            }
            itemsOrderEntity.setItemOrderAttributes(itemsOrderAttributeEntities);
            itemsOrderEntity.setCategoryName(itemOrder.getCategoryName());
            itemsOrderEntities.add(itemsOrderEntity);
        }

        orderSummary.setId(order.getId());
        orderSummary.setItemOrders(itemsOrderEntities);
        OrderSummaryDto.AccountSummary accountSummary = orderSummary.new AccountSummary();
        BigDecimal totalDiscount = BigDecimal.ZERO;

        if(countCustomItem.equals(itemsOrder.size())){
            accountSummary.setSubTotal(minusOne);
        } else {
            accountSummary.setSubTotal(order.getTotalCost().setScale(2, BigDecimal.ROUND_DOWN));
        }

        if(countCustomItem.equals(0)) {
            accountSummary.setServiceFee(order.getSystemServiceCharge().setScale(2, BigDecimal.ROUND_DOWN));
            accountSummary.setVatAndServiceCharge(order.getItemServiceAndVatCharge().setScale(2, BigDecimal.ROUND_DOWN));
            accountSummary.setItemServiceCharge(order.getItemServiceCharge().setScale(2, BigDecimal.ROUND_DOWN));
            accountSummary.setItemVatCharge(order.getItemVatCharge().setScale(2, BigDecimal.ROUND_DOWN));
            accountSummary.setDeliveryFee(order.getDeliveryCharge().setScale(2, BigDecimal.ROUND_DOWN));
            accountSummary.setPaidFromCOD(order.getPaidFromCOD().setScale(2, BigDecimal.ROUND_DOWN));
            accountSummary.setPaidFromWallet(order.getPaidFromWallet().setScale(2, BigDecimal.ROUND_DOWN));
            accountSummary.setEstimatedTotal(order.getGrandTotal().setScale(2, BigDecimal.ROUND_DOWN));
        } else  {
            accountSummary.setServiceFee(minusOne);
            accountSummary.setVatAndServiceCharge(minusOne);
            accountSummary.setItemServiceCharge(minusOne);
            accountSummary.setItemVatCharge(minusOne);
            accountSummary.setDeliveryFee(minusOne);
            accountSummary.setPaidFromCOD(minusOne);
            accountSummary.setPaidFromWallet(minusOne);
            accountSummary.setEstimatedTotal(minusOne);
        }

        accountSummary.setTotalDiscount(totalDiscount);
        accountSummary.setPartnerShipStatus(order.getPartnershipStatus());
        accountSummary.setPaymentMode(order.getPaymentMode());

        accountSummary.setDiscountFromStore(order.getDiscountFromStore());
        orderSummary.setAccountSummary(accountSummary);
        return orderSummary;
    }

    @Override
    public OrderEntity getOrderById(Integer orderId, Integer deliveryBoyId) throws Exception {
        OrderEntity order = orderDaoService.find(orderId);
        if (order == null) {
            throw new YSException("VLD017");
        }
        DeliveryBoySelectionEntity deliveryBoySelection = deliveryBoySelectionDaoService.getSelectionDetails(orderId, deliveryBoyId);

        if(deliveryBoySelection == null){
            throw new YSException("ORD003");
        }



        OrderEntity responseOrder = new OrderEntity();

        Integer countCustomItem = 0;
        for (ItemsOrderEntity itemOrder : order.getItemsOrder()) {
            if(itemOrder.getCustomItem() != null && itemOrder.getItemTotal() == null && itemOrder.getAvailabilityStatus()){
                countCustomItem++;
            }
        }

        if(countCustomItem > 0)
            deliveryBoySelection.setPaidToCourier(minusOne);

        responseOrder.setId(order.getId());
        responseOrder.setOrderName(order.getOrderName());
        responseOrder.setOrderVerificationStatus(order.getOrderVerificationStatus());
        responseOrder.setDeliveryStatus(order.getDeliveryStatus());
        responseOrder.setOrderStatus(order.getOrderStatus());
        responseOrder.setCustomerChargeableDistance(order.getCustomerChargeableDistance());
        responseOrder.setDiscountFromStore(order.getDiscountFromStore());
        if(countCustomItem == 0){
            responseOrder.setTotalCost(order.getTotalCost());
            responseOrder.setSystemServiceCharge(order.getSystemServiceCharge());
            responseOrder.setDeliveryCharge(order.getDeliveryCharge());
            responseOrder.setGrandTotal(order.getGrandTotal());
            responseOrder.setItemServiceAndVatCharge(order.getItemServiceAndVatCharge());
        } else {
            responseOrder.setTotalCost(minusOne);
            responseOrder.setSystemServiceCharge(minusOne);
            responseOrder.setDeliveryCharge(minusOne);
            responseOrder.setGrandTotal(minusOne);
            responseOrder.setItemServiceAndVatCharge(minusOne);
        }

        responseOrder.setAssignedTime(order.getAssignedTime());
        responseOrder.setRemainingTime(order.getRemainingTime());
        responseOrder.setSurgeFactor(order.getSurgeFactor());

        responseOrder.setOrderDate(order.getOrderDate());
        responseOrder.setAssignedTime(deliveryBoySelection.getTotalTimeRequired());
        responseOrder.setDeliveryBoyShare(deliveryBoySelection.getPaidToCourier());
        responseOrder.setSystemChargeableDistance(deliveryBoySelection.getDistanceToStore());

        AddressEntity address = new AddressEntity();
        address.setId(order.getAddress().getId());
        address.setStreet(order.getAddress().getStreet());
        address.setCity(order.getAddress().getCity());
        address.setState(order.getAddress().getState());
        address.setCountry(order.getAddress().getCountry());
        address.setCountryCode(order.getAddress().getCountryCode());
        address.setLatitude(order.getAddress().getLatitude());
        address.setLongitude(order.getAddress().getLongitude());
        address.setMobileNumber(order.getAddress().getMobileNumber());
        address.setFullName(order.getAddress().getFullName());
        address.setNotes(order.getAddress().getNotes());
        address.setGivenLocation(order.getAddress().getGivenLocation());
        responseOrder.setAddress(address);

        StoreEntity store = new StoreEntity();
        store.setName(order.getStore().getName());
        store.setStreet(order.getStore().getStreet());
        store.setCity(order.getStore().getCity());
        store.setState(order.getStore().getState());
        store.setCountry(order.getStore().getCountry());
        store.setContactNo(order.getStore().getContactNo());
        store.setLatitude(order.getStore().getLatitude());
        store.setLongitude(order.getStore().getLongitude());
        store.setBrandLogo(order.getStore().getStoresBrand().getBrandLogo());
        responseOrder.setStore(store);

        CustomerEntity customer = new CustomerEntity();
        customer.setId(order.getCustomer().getId());
        UserEntity user = new UserEntity();
        user.setId(order.getCustomer().getUser().getId());
        user.setFullName(order.getCustomer().getUser().getFullName());
        user.setMobileNumber(order.getCustomer().getUser().getMobileNumber());
        customer.setUser(user);
        responseOrder.setCustomer(customer);

        List<ItemsOrderEntity> itemsOrder = order.getItemsOrder();
        for (ItemsOrderEntity itemOrder : itemsOrder) {
            if (itemOrder.getItem() != null) {
                ItemEntity item = new ItemEntity();
                item.setId(itemOrder.getItem().getId());
                item.setName(itemOrder.getItem().getName());
                item.setAdditionalOffer(itemOrder.getItem().getAdditionalOffer());
                item.setUnitPrice(itemOrder.getItem().getUnitPrice());
                itemOrder.setItem(item);
            }else if(itemOrder.getCustomItem() != null){
                CustomItemEntity customItem = new CustomItemEntity();
                customItem.setId(itemOrder.getCustomItem().getId());
                customItem.setName(itemOrder.getCustomItem().getName());
                itemOrder.setCustomItem(customItem);
            }
            itemOrder.setItemOrderAttributes(null);
        }

        responseOrder.setItemsOrder(itemsOrder);
        return responseOrder;
    }

    @Override
    public JobOrderStatus getJobOrderStatusFromOrderId(Integer orderId) throws Exception {
        return orderDaoService.getJobOrderStatus(orderId);
    }

    @Override
    public List<ReasonDetailsEntity> getCancelReasonList() throws Exception {
        return reasonDetailsDaoService.findAll();
    }

    @Override
    public ItemsOrderEntity getItemOrderById(Integer itemOrderId) throws Exception {
        log.info("++++++++++++ Getting Item Order Detail of id " + itemOrderId + " +++++++++++++");
        ItemsOrderEntity itemOrder = itemsOrderDaoService.find(itemOrderId);
        if(itemOrder == null){
            throw new YSException("ITM004");
        }
        if (itemOrder.getItem() != null) {
            String fields = "id,quantity,itemTotal,serviceAndVatCharge,availabilityStatus,vat,serviceCharge,note,purchaseStatus,customerNote,item";

            Map<String, String> assoc = new HashMap<>();
            Map<String, String> subAssoc = new HashMap<>();
            assoc.put("item", "id,name,availableStartTime,availableEndTime,maxOrderQuantity,minOrderQuantity,unitPrice,itemsImage,attributesTypes,additionalOffer");
            subAssoc.put("itemsImage", "url");
            subAssoc.put("attributesTypes", "id,itemsAttribute,multiSelect,type");
            subAssoc.put("itemsAttribute", "id,attribute,unitPrice,selected");

            itemOrder = (ItemsOrderEntity) ReturnJsonUtil.getJsonObject(itemOrder, fields, assoc, subAssoc);

            if(itemOrder.getNote() == "")
                itemOrder.setNote(null);
            ItemEntity itemEntity = itemOrder.getItem();
            itemEntity.setBrandName("");
            itemEntity.setCurrency(systemPropertyService.readPrefValue(PreferenceType.CURRENCY));

            if (itemEntity.getItemsImage() == null || itemEntity.getItemsImage().size() == 0) {
                ItemsImageEntity itemsImage = new ItemsImageEntity();
                itemsImage.setUrl(systemPropertyService.readPrefValue(PreferenceType.DEFAULT_IMG_ITEM));
                itemEntity.setItemsImage(Collections.singletonList(itemsImage));
            }

            List<Integer> selectedAttributes = itemsOrderAttributeDaoService.getSelectedAttributesOfItemOrder(itemOrderId);
            if(selectedAttributes.size() > 0){
                for (ItemsAttributesTypeEntity itemsAttributesTypeEntity : itemEntity.getAttributesTypes()) {
                    for (ItemsAttributeEntity itemsAttributeEntity : itemsAttributesTypeEntity.getItemsAttribute()) {
                        if (selectedAttributes.contains(itemsAttributeEntity.getId()))
                            itemsAttributeEntity.setSelected(true);
                    }
                }
            }

        }else{
            String fields = "id,quantity,itemTotal,serviceAndVatCharge,availabilityStatus,vat,serviceCharge,note,customItem";
            Map<String, String> assoc = new HashMap<>();
            assoc.put("customItem", "id,name,editedName,customerCustom");
            itemOrder = (ItemsOrderEntity) ReturnJsonUtil.getJsonObject(itemOrder, fields, assoc);

            if(itemOrder.getNote() == "")
                itemOrder.setNote(null);

            if (itemOrder.getServiceAndVatCharge() == null)
                itemOrder.setServiceAndVatCharge(minusOne);

            if(itemOrder.getVat() == null){
                itemOrder.setVat(minusOne);
            }

            if(itemOrder.getServiceCharge() == null)
                itemOrder.setServiceCharge(minusOne);

            if(itemOrder.getItemTotal() == null)
                itemOrder.setItemTotal(minusOne);

        }
        return itemOrder;
    }

    @Override
    public PaginationDto get_order_history(Integer dBoyId, RequestJsonDto requestJsonDto) throws Exception {
        PaginationDto paginationDto = new PaginationDto();
        Map<String, Date> dateRange = requestJsonDto.getDateRange();
        Date fromDate = null;
        Date toDate = null;
        if(dateRange != null){
            if(dateRange.get("fromDate") != null){
                fromDate = dateRange.get("fromDate");
            }
            if(dateRange.get("toDate") != null){
                toDate = dateRange.get("toDate");
            }
        }

        Page page = requestJsonDto.getPage();

        Integer  totalRows =  orderDaoService.getTotalNumberOrderHostory(dBoyId, fromDate, toDate);

        paginationDto.setNumberOfRows(totalRows);

        if(page != null){
            page.setTotalRows(totalRows);
        }

        List<Object> orders = new ArrayList<>();

        if(fromDate != null && toDate != null){
            orders = orderDaoService.get_dBoy_order_history(dBoyId, fromDate, toDate, page);
        } else {
            orders = orderDaoService.get_dBoy_order_history(dBoyId, page);
        }

        List<Object> objects = new ArrayList<>();

        String fields = "id,orderName,deliveryStatus,assignedTime,customer,deliveryBoy,grandTotal,orderDate,dBoyOrderHistories,rating,dBoyPaid,dBoyPaidDate";

        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("customer", "id,user");
        assoc.put("deliveryBoy", "id,averageRating,user");
        assoc.put("dBoyOrderHistories", "id,amountEarned,jobStartedAt,orderCompletedAt,distanceTravelled");
        assoc.put("rating", "id,customerRating,deliveryBoyRating,deliveryBoyComment,customerComment");

        subAssoc.put("user", "id,fullName");

        for (Object order:orders){
            objects.add(ReturnJsonUtil.getJsonObject(order, fields, assoc, subAssoc));
        }
        paginationDto.setData(objects);
        return paginationDto;
    }

    @Override
    public Boolean rejectOrder(Integer deliveryBoyId, Integer orderId) throws Exception {
        DeliveryBoySelectionEntity deliveryBoySelectionEntity = deliveryBoySelectionDaoService.getSelectionDetails(orderId, deliveryBoyId);
        if(deliveryBoySelectionEntity == null){
            throw new YSException("ORD003");
        }
        if(deliveryBoySelectionEntity.getAccepted()){
            throw new YSException("ORD005");
        }
        if(deliveryBoySelectionEntity.getRejected()){
           throw new YSException("ORD015");
        }else{
            deliveryBoySelectionEntity.setRejected(true);
            deliveryBoySelectionDaoService.update(deliveryBoySelectionEntity);
            if(deliveryBoySelectionDaoService.getRemainingOrderSelections(orderId).equals(0)){
                customerService.reprocessOrder(orderId);
            }
            return true;
        }
    }

    @Override
    public PreferenceDto getAcceptanceDetails() throws Exception {
        PreferenceDto preferenceDto = new PreferenceDto();
        preferenceDto.setAcceptanceRadius(systemPropertyService.readPrefValue(PreferenceType.ACCEPTANCE_RADIUS));
        return preferenceDto;
    }

    /*@Override
    public PaginationDto getAcknowledgements(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception{
        Page page = requestJsonDto.getPage();

        PaginationDto paginationDto = new PaginationDto();
        Integer totalRows =  dBoySubmittedAmountDaoService.getTotalNumbersAcknowledgements(Integer.parseInt(headerDto.getId()));
        paginationDto.setNumberOfRows(totalRows);

        if(page != null){
            page.setTotalRows(totalRows);
        }

        List<DBoySubmittedAmountEntity> acknowledgements = dBoySubmittedAmountDaoService.getcknowledgements(Integer.parseInt(headerDto.getId()), page);

        List<Object> objects = new ArrayList<>();
        String fields = "id,ackDate,amountReceived,deliveryBoy";

        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("deliveryBoy", "id,user");
        subAssoc.put("user", "id,fullName,mobileNumber,profileImage");

        for (DBoySubmittedAmountEntity acknowledgement:acknowledgements){
            objects.add(ReturnJsonUtil.getJsonObject(acknowledgement, fields, assoc, subAssoc));
        }

        paginationDto.setData(objects);
        return paginationDto;
    }*/

    @Override
    public PaginationDto getAdvanceAmounts(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception{
        Page page = requestJsonDto.getPage();

        PaginationDto paginationDto = new PaginationDto();
        Integer totalRows =  dBoyAdvanceAmountDaoService.getTotalNumbersOfAdvanceAmounts(Integer.parseInt(headerDto.getId()));
        paginationDto.setNumberOfRows(totalRows);

        if(page != null){
            page.setTotalRows(totalRows);
        }

        List<DBoyAdvanceAmountEntity> advanceAmounts = dBoyAdvanceAmountDaoService.getAdvanceAmounts(Integer.parseInt(headerDto.getId()), page);

        List<Object> objects = new ArrayList<>();

        String fields = "id,advanceDate,amountAdvance,type,deliveryBoy";

        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("deliveryBoy", "id,user");
        subAssoc.put("user", "id,fullName,mobileNumber,profileImage");

        for (DBoyAdvanceAmountEntity advanceAmount:advanceAmounts){
            objects.add(ReturnJsonUtil.getJsonObject(advanceAmount, fields, assoc, subAssoc));
        }

        paginationDto.setData(objects);
        return paginationDto;
    }

    @Override
    public OrderEntity getPaidFromCODAmount(Integer orderId) throws Exception {
        OrderEntity order = orderDaoService.getPaidFromCashOnDeliveryAmount(orderId);
        if(order == null)
            throw new YSException("VLD017");
        order.setCurrency(systemPropertyService.readPrefValue(PreferenceType.CURRENCY));
        return order;
    }

    @Override
    public PreferenceDto getHelpLineDetails() throws Exception {
        PreferenceDto preferenceDto = new PreferenceDto();
        preferenceDto.setHelplineNumber(systemPropertyService.readPrefValue(PreferenceType.HELPLINE_NUMBER));
        preferenceDto.setCurrency(systemPropertyService.readPrefValue(PreferenceType.CURRENCY));
        return preferenceDto;
    }

    @Override
    public Boolean updateDiscountFromStore(Integer orderId, BigDecimal discountAmount) throws Exception {
        OrderEntity order = orderDaoService.find(orderId);
        Boolean vatInclusive = order.getStore().getStoresBrand().getVatInclusive();
        if (order == null) {
            throw new YSException("VLD017");
        } else if(!BigDecimalUtil.isGreaterThen(order.getTotalCost(), order.getDiscountFromStore())){
            throw new YSException("ORD023");
        } else if(order.getOrderStatus().equals(JobOrderStatus.IN_ROUTE_TO_DELIVERY)
                || order.getOrderStatus().equals(JobOrderStatus.DELIVERED) || order.getOrderStatus().equals(JobOrderStatus.CANCELLED)){
            throw new YSException("ORD024");
        }

        BigDecimal itemTotalCost = BigDecimal.ZERO;
        BigDecimal itemServiceAndVatCharge = BigDecimal.ZERO;
        BigDecimal itemServiceCharge = BigDecimal.ZERO;
        BigDecimal itemVatCharge = BigDecimal.ZERO;
        BigDecimal totalCommissionAmount = BigDecimal.ZERO;
        BigDecimal cashBackAmountTotal = BigDecimal.ZERO;

        List<ItemsOrderEntity> itemsOrderEntityList = order.getItemsOrder();
        for (ItemsOrderEntity itemsOrder : itemsOrderEntityList) {
            /* Service Fee and Vat calculation for available items only. */
            if (itemsOrder.getAvailabilityStatus()) {
                BigDecimal commissionAmount = BigDecimal.ZERO;
                if(itemsOrder.getItem()!=null) {
                    commissionAmount = BigDecimalUtil.percentageOf(BigDecimalUtil.checkNull(itemsOrder.getItemTotal()), BigDecimalUtil.checkNull(itemsOrder.getItem().getCommissionPercentage()));
                    cashBackAmountTotal = cashBackAmountTotal.add(BigDecimalUtil.checkNull(itemsOrder.getItem().getCashBackAmount()).multiply(new BigDecimal(itemsOrder.getQuantity())));
                }  else {
                    commissionAmount = BigDecimalUtil.percentageOf(itemsOrder.getItemTotal(), BigDecimalUtil.checkNull(order.getStore().getStoresBrand().getDefaultCommissionPcn()));
                }
                BigDecimal serviceChargeAmount = BigDecimalUtil.percentageOf(BigDecimalUtil.checkNull(itemsOrder.getItemTotal()), BigDecimalUtil.checkNull(itemsOrder.getServiceCharge()));

                BigDecimal serviceAndVatChargeAmount;
                if(vatInclusive != null && !vatInclusive) {
                    BigDecimal itemsOrderVatAmount = BigDecimalUtil.percentageOf(serviceChargeAmount.add(BigDecimalUtil.checkNull(itemsOrder.getItemTotal())), BigDecimalUtil.checkNull(itemsOrder.getVat()));
                    itemVatCharge = itemVatCharge.add(itemsOrderVatAmount);
                    serviceAndVatChargeAmount = serviceChargeAmount.add(itemsOrderVatAmount);
                } else
                    serviceAndVatChargeAmount = serviceChargeAmount;

                itemTotalCost = itemTotalCost.add(BigDecimalUtil.checkNull(itemsOrder.getItemTotal()));
                itemServiceCharge = itemServiceCharge.add(serviceChargeAmount);
                totalCommissionAmount = totalCommissionAmount.add(commissionAmount);
                itemServiceAndVatCharge = itemServiceAndVatCharge.add(serviceAndVatChargeAmount);
                itemsOrder.setServiceAndVatCharge(serviceAndVatChargeAmount);
                itemsOrder.setCommissionAmount(commissionAmount);
            }
        }

       /* Commented since minimum amount is checked at start delivery. */
       /*if(BigDecimalUtil.isLessThen(itemTotalCost, order.getStore().getStoresBrand().getMinOrderAmount())){
            throw new YSException("CRT008", " "+order.getStore().getStoresBrand().getMinOrderAmount());
        }*/

        order.setDiscountFromStore(BigDecimalUtil.chopToTwoDecimalPlace(discountAmount));
        order.setTotalCost(BigDecimalUtil.chopToTwoDecimalPlace(itemTotalCost));
        order.setItemServiceAndVatCharge(BigDecimalUtil.chopToTwoDecimalPlace(itemServiceAndVatCharge));
        order.setItemServiceCharge(BigDecimalUtil.chopToTwoDecimalPlace(itemServiceCharge));
        order.setItemVatCharge(BigDecimalUtil.chopToTwoDecimalPlace(itemVatCharge));
        order.setCashBackToCustomerAmount(cashBackAmountTotal);
        order.setCommissionAmount(totalCommissionAmount);

        /*This section is used just for getting courier transaction information */
        DeliveryBoySelectionEntity dBoySelection = new DeliveryBoySelectionEntity();
        dBoySelection.setDistanceToStore(order.getSystemChargeableDistance());
        dBoySelection.setStoreToCustomerDistance(order.getCustomerChargeableDistance());

        CourierTransactionEntity courierTransactionEntity = order.getCourierTransaction();
        systemAlgorithmService.getCourierTransaction(order, dBoySelection, totalCommissionAmount, courierTransactionEntity.getSystemProcessingChargePct());

        //calculate paidFromCod and paid from wallet
        /*if(BigDecimalUtil.isGreaterThenOrEqualTo(BigDecimalUtil.checkNull(order.getCustomer().getWalletAmount()),order.getGrandTotal())){
            order.setPaidFromWallet(order.getGrandTotal());
            order.setPaidFromCOD(BigDecimal.ZERO);
        } else if(BigDecimalUtil.isGreaterThen(BigDecimalUtil.checkNull(order.getCustomer().getWalletAmount()), BigDecimal.ZERO)){
            BigDecimal paidFromWallet = order.getCustomer().getWalletAmount();
            BigDecimal paidFromCOD = order.getGrandTotal().subtract(paidFromWallet);
            order.setPaidFromWallet(paidFromWallet);
            order.setPaidFromCOD(paidFromCOD);
        } else {
            order.setPaidFromWallet(BigDecimal.ZERO);
            order.setPaidFromCOD(order.getGrandTotal());
        }*/
        OrderPaymentUtil.calculateOrderPayment(order);

        boolean status = orderDaoService.update(order);
        if(status){
            UserDeviceEntity userDevice = userDeviceDaoService.getUserDeviceInfoFromOrderId(order.getId());
            String message = MessageBundle.getMessage("CPN005","push_notification.properties");
            String extraDetail = order.getId().toString()+"/status/"+order.getOrderStatus().toString();
            PushNotificationUtil.sendPushNotification(userDevice, message, NotifyTo.CUSTOMER, PushNotificationRedirect.ORDER, extraDetail);
        }
        return status;
    }

}
