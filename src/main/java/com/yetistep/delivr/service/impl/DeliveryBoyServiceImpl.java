package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.abs.AbstractManager;
import com.yetistep.delivr.dao.inf.*;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.OrderSummaryDto;
import com.yetistep.delivr.dto.PaginationDto;
import com.yetistep.delivr.enums.*;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.model.mobile.dto.OrderInfoDto;
import com.yetistep.delivr.model.mobile.dto.PastDeliveriesDto;
import com.yetistep.delivr.model.mobile.dto.PreferenceDto;
import com.yetistep.delivr.service.inf.CustomerService;
import com.yetistep.delivr.service.inf.DeliveryBoyService;
import com.yetistep.delivr.service.inf.SystemAlgorithmService;
import com.yetistep.delivr.service.inf.SystemPropertyService;
import com.yetistep.delivr.util.*;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
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

    @Override
    public void saveDeliveryBoy(DeliveryBoyEntity deliveryBoy, HeaderDto headerDto) throws Exception {
        log.info("++++++++++++++++++ Creating Delivery Boy +++++++++++++++++");
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
        user.setPassword(GeneralUtil.encryptPassword(user.getPassword()));

        RoleEntity userRole = userDaoService.getRoleByRole(Role.ROLE_DELIVERY_BOY);

        /** Setting Default values **/
        user.setRole(userRole);
        user.setBlacklistStatus(false);
        user.setMobileVerificationStatus(true);
        user.setVerifiedStatus(true);
        user.setSubscribeNewsletter(false);
        user.setStatus(Status.ACTIVE);
        if(user.getEmailAddress().isEmpty()){
            user.setEmailAddress(null);
        }

        deliveryBoy.setAvailabilityStatus(DBoyStatus.FREE);
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


        String fields = "id,availabilityStatus,averageRating,bankAmount,walletAmount,advanceAmount,vehicleType,licenseNumber,vehicleNumber,user,latitude,longitude,order";

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
        return deliveryBoy;
    }

    @Override
    public List<DeliveryBoyEntity> findAllDeliverBoy() throws Exception {
        log.info("Retrieving list of Deliver Boys");
        List<DeliveryBoyEntity> deliveryBoyEntities = deliveryBoyDaoService.findAll();
        /*For filtering role -- set to null as all delivery boy has same role*/

        List<DeliveryBoyEntity> objects = new ArrayList<>();

        String fields = "id,availabilityStatus,averageRating,bankAmount,walletAmount,advanceAmount,user,order,latitude,longitude";

        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("user", "id,fullName,mobileNumber,emailAddress,status");
        assoc.put("order", "id,orderName,assignedTime,orderStatus,dBoyOrderHistories");

        subAssoc.put("dBoyOrderHistories", "orderAcceptedAt");

        for (DeliveryBoyEntity deliveryBoyEntity:deliveryBoyEntities){
            DeliveryBoyEntity deliveryBoy = (DeliveryBoyEntity) ReturnJsonUtil.getJsonObject(deliveryBoyEntity, fields, assoc, subAssoc);
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


        return objects;
    }

    @Override
    public Boolean updateDeliveryBoy(DeliveryBoyEntity deliveryBoyEntity, HeaderDto headerDto) throws Exception {
        DeliveryBoyEntity dBoyEntity = deliveryBoyDaoService.find(deliveryBoyEntity.getId());
        if (dBoyEntity == null) {
            throw new YSException("VLD011");
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
                   address.setUser(deliveryBoyEntity.getUser());
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

        String profileImage = deliveryBoyEntity.getUser().getProfileImage();

        deliveryBoyDaoService.update(dBoyEntity);

        if (profileImage != null && !profileImage.isEmpty()) {

            log.info("Deleting Profile Image of delivery boy to S3 Bucket ");
            AmazonUtil.deleteFileFromBucket(AmazonUtil.getAmazonS3Key(dBoyEntity.getUser().getProfileImage()));

            log.info("Uploading Profile Image of delivery boy to S3 Bucket ");

            String dir = MessageBundle.separateString("/", "DBoy", "DBoy" + dBoyEntity.getId());
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
        log.info("Updating Status of Delivery Boy to:" + deliveryBoyEntity.getAvailabilityStatus());
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
            userDaoService.update(userEntity);
        }
        String fields = "id,availabilityStatus,averageRating,totalOrderTaken,totalOrderDelivered,totalOrderUndelivered,totalEarnings," +
                "vehicleType,activeOrderNo,licenseNumber,vehicleNumber,user";
        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();
        assoc.put("user", "id,username,fullName,gender,mobileNumber,mobileVerificationStatus,emailAddress," +
                "profileImage,blacklistStatus,verifiedStatus,subscribeNewsletter,lastActivityDate,createdDate,addresses");
        subAssoc.put("addresses", "id,street,city,state,country,countryCode");

        DeliveryBoyEntity deliveryBoyEntity = (DeliveryBoyEntity) ReturnJsonUtil.getJsonObject(userEntity.getDeliveryBoy(), fields, assoc , subAssoc);
        return deliveryBoyEntity;
    }

    @Override
    public List<DeliveryBoyEntity> getAllCapableDeliveryBoys() throws Exception {
        return deliveryBoyDaoService.findAllCapableDeliveryBoys();
    }

    @Override
    public Boolean updateLocationOfDeliveryBoy(DeliveryBoyEntity deliveryBoyEntity) throws Exception {
        log.info("Updating location of Delivery Boy with ID:"+deliveryBoyEntity.getId());
        DeliveryBoyEntity dBoyEntity = deliveryBoyDaoService.find(deliveryBoyEntity.getId());
        if (dBoyEntity == null) {
            throw new YSException("VLD011");
        }
        GeoCodingUtil.getLatLong(deliveryBoyEntity.getLatitude(), deliveryBoyEntity.getLongitude());
        dBoyEntity.setLatitude(deliveryBoyEntity.getLatitude());
        dBoyEntity.setLongitude(deliveryBoyEntity.getLongitude());
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
        return orderEntities;
    }

    private void updateRemainingAndElapsedTime(OrderInfoDto orderInfoDto){
        Double minuteDiff = DateUtil.getMinDiff(System.currentTimeMillis(), orderInfoDto.getOrderAcceptedAt().getTime());
        int remainingTime = orderInfoDto.getRemainingTime() - minuteDiff.intValue();
        orderInfoDto.setElapsedTime(minuteDiff.intValue());
        remainingTime = (remainingTime < 0) ? 0 : remainingTime;
        orderInfoDto.setRemainingTime(remainingTime);
    }

    @Override
    public Boolean changeOrderStatus(OrderEntity orderEntity, Integer deliveryBoyId) throws Exception{
        OrderEntity order = orderDaoService.find(orderEntity.getId());
        if(order == null){
            throw new YSException("VLD017");
        }
        JobOrderStatus orderStatus = orderEntity.getOrderStatus();
        if(orderStatus.equals(JobOrderStatus.IN_ROUTE_TO_PICK_UP) ||
                orderStatus.equals(JobOrderStatus.AT_STORE) ||
                orderStatus.equals(JobOrderStatus.IN_ROUTE_TO_DELIVERY)){
            if(!deliveryBoyDaoService.checkForPendingOrders(deliveryBoyId, orderEntity.getId())){
                throw new YSException("DBY004");
            }
        }
        if(orderStatus.equals(JobOrderStatus.ORDER_ACCEPTED)){
            return acceptDeliveryOrder(orderEntity.getId(), deliveryBoyId);
        }else if(orderStatus.equals(JobOrderStatus.IN_ROUTE_TO_PICK_UP)){
            return startJob(order, deliveryBoyId);
        }else if(orderStatus.equals(JobOrderStatus.AT_STORE)){
            //Such as bill upload and Bill edit
            return reachedStore(order, deliveryBoyId);
        }else if(orderStatus.equals(JobOrderStatus.IN_ROUTE_TO_DELIVERY)){
             return goRouteToDelivery(order, deliveryBoyId);
        }else if(orderStatus.equals(JobOrderStatus.DELIVERED)){
            return deliverOrder(orderEntity, order, deliveryBoyId);
        }else if(orderStatus.equals(JobOrderStatus.CANCELLED)){
            //reason
        }else{
            throw new YSException("ORD002");
        }
        order.setOrderStatus(orderStatus);
        orderDaoService.update(order);
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
            if(deliveryBoyEntity.getActiveOrderNo() >= 3){
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
            CourierTransactionEntity courierTransaction =  systemAlgorithmService.getCourierTransaction(orderEntity, deliveryBoySelectionEntity, merchant.getCommissionPercentage(), merchant.getServiceFee());
            CourierTransactionEntity courierTransactionEntity = orderEntity.getCourierTransaction();
            courierTransactionEntity.setOrderTotal(courierTransaction.getOrderTotal());
            courierTransactionEntity.setAdditionalDeliveryAmt(courierTransaction.getAdditionalDeliveryAmt());
            courierTransactionEntity.setCustomerDiscount(courierTransaction.getCustomerDiscount());
            courierTransactionEntity.setDeliveryCostWithoutAdditionalDvAmt(courierTransaction.getDeliveryCostWithoutAdditionalDvAmt());
            courierTransactionEntity.setServiceFeeAmt(courierTransaction.getServiceFeeAmt());
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
                String message = MessageBundle.getMessage("CPN001","push_notification.properties");
                message = String.format(message, orderEntity.getStore().getName(), deliveryBoyEntity.getUser().getFullName());
                String extraDetail = orderId.toString()+"/status/"+JobOrderStatus.ORDER_ACCEPTED.toString();
                PushNotificationUtil.sendPushNotification(userDevice, message, NotifyTo.CUSTOMER, PushNotificationRedirect.ORDER, extraDetail);

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
        } else if (deliveryBoyId.equals(deliveryBoySelectionEntity.getDeliveryBoy().getId())) {
            throw new YSException("ORD005");
        }
        throw new YSException("ORD001");
    }

    private Boolean startJob(OrderEntity order, Integer deliveryBoyId) throws Exception{
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

    private Boolean goRouteToDelivery(OrderEntity order, Integer deliveryBoyId) throws Exception {
        if(!order.getDeliveryBoy().getId().equals(deliveryBoyId)){
            throw new YSException("ORD003");
        }
        JobOrderStatus.traverseJobStatus(order.getOrderStatus(), JobOrderStatus.IN_ROUTE_TO_DELIVERY);

        if(BigDecimalUtil.isLessThen(order.getTotalCost(), order.getStore().getStoresBrand().getMinOrderAmount())){
            throw new YSException("CRT008", " "+order.getStore().getStoresBrand().getMinOrderAmount());
        }
        if(BigDecimalUtil.isLessThenOrEqualTo(order.getTotalCost(), BigDecimal.ZERO)){
            throw new YSException("ORD016");
        }
        BigDecimal MINIMUM_PROFIT_PERCENTAGE = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.MINIMUM_PROFIT_PERCENTAGE));
        if(BigDecimalUtil.isLessThen(order.getCourierTransaction().getProfit(), BigDecimalUtil.percentageOf(order.getTotalCost(), MINIMUM_PROFIT_PERCENTAGE))){
            log.warn("No Profit Condition:"+order.getId());
            throw new YSException("ORD016");
        }
        order.setOrderStatus(JobOrderStatus.IN_ROUTE_TO_DELIVERY);
        boolean partnerShipStatus = merchantDaoService.findPartnerShipStatusFromOrderId(order.getId());
        courierBoyAccountingsAfterTakingOrder(order.getDeliveryBoy(), order, partnerShipStatus);
        boolean status = orderDaoService.update(order);
        if(status){
            UserDeviceEntity userDevice = userDeviceDaoService.getUserDeviceInfoFromOrderId(order.getId());
            String message = MessageBundle.getMessage("CPN004","push_notification.properties");
            String extraDetail = order.getId().toString()+"/status/"+JobOrderStatus.IN_ROUTE_TO_DELIVERY.toString();
            PushNotificationUtil.sendPushNotification(userDevice, message, NotifyTo.CUSTOMER, PushNotificationRedirect.ORDER, extraDetail);
        }
        return status;
    }

    private Boolean deliverOrder(OrderEntity orderEntity, OrderEntity order, Integer deliveryBoyId) throws Exception {
        if(!order.getDeliveryBoy().getId().equals(deliveryBoyId)){
            throw new YSException("ORD003");
        }

        if(!orderEntity.getOrderVerificationCode().equals(order.getOrderVerificationCode()) || !orderEntity.getId().equals(order.getId())){
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
                dBoyOrderHistoryEntity.setAmountEarned(order.getCourierTransaction().getPaidToCourier());
                break;
            }
        }

        DeliveryBoyEntity deliveryBoyEntity = order.getDeliveryBoy();
        deliveryBoyEntity.setActiveOrderNo(deliveryBoyEntity.getActiveOrderNo()-1);
        if(deliveryBoyEntity.getActiveOrderNo() == 0){
            deliveryBoyEntity.setAvailabilityStatus(DBoyStatus.FREE);
        }
        deliveryBoyEntity.setTotalEarnings(BigDecimalUtil.checkNull(deliveryBoyEntity.getTotalEarnings()).add(order.getCourierTransaction().getPaidToCourier()));
        deliveryBoyEntity.setTotalOrderDelivered(deliveryBoyEntity.getTotalOrderDelivered() + 1);
        /*deliveryBoyEntity.setTotalOrderUndelivered(deliveryBoyEntity.getTotalOrderUndelivered() - 1);*/
        /*Accounting Implementation*/
        courierBoyAccountingsAfterOrderDelivery(deliveryBoyEntity, order);
        /*Accounting Implementation Completed*/

        RatingEntity rating = order.getRating();
        if(rating == null){
            rating = new RatingEntity();
            rating.setOrder(order);
        }
        rating.setCustomerRating(orderEntity.getRating().getCustomerRating());
        rating.setDeliveryBoyComment(orderEntity.getRating().getDeliveryBoyComment());
        order.setRating(rating);
        List<OrderEntity> customersOrders = orderDaoService.getCustomersOrders(order.getCustomer().getId());
        boolean status = orderDaoService.update(order);
        if(status){
            /* Updating unit price of item */
            this.updateItemPrice(order);
            UserDeviceEntity userDevice = userDeviceDaoService.getUserDeviceInfoFromOrderId(order.getId());
            String message = MessageBundle.getMessage("CPN006","push_notification.properties");
            String extraDetail = order.getId().toString()+"/status/"+JobOrderStatus.DELIVERED.toString();
            PushNotificationUtil.sendPushNotification(userDevice, message, NotifyTo.CUSTOMER, PushNotificationRedirect.ORDER, extraDetail);
            /*
            * if the transaction is first transaction of  the current customer
            * set reward for referrer
            * */
            if(customersOrders.size() == 0){
               Long referrerId = order.getCustomer().getReferredBy();
               if(referrerId != null){
                   CustomerEntity referrer = customerDaoService.find(referrerId);
                   referrer.setRewardsEarned(referrer.getRewardsEarned().add(new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.REFERRAL_REWARD_AMOUNT))));
                   customerDaoService.update(referrer);
               }
            }

        }
        return status;
    }

    /* This method is used to update unit price of an item. */
    private void updateItemPrice(OrderEntity orderEntity) throws Exception {
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
        log.info("Updating Status of Delivery Boy to:" + deliveryBoyEntity.getAvailabilityStatus());
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
        for (String bill: order.getAttachments()) {
            if (bill != null && !bill.isEmpty()) {
                String imageName = "bill" + (isLocal ? "_tmp_" : "_") + System.currentTimeMillis();
                String s3Path = GeneralUtil.saveImageToBucket(bill, imageName, dir, true);
                attachments.add(s3Path);
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
        return deliveryBoy;
    }

    @Override
    public Boolean addNewItem(ItemsOrderEntity itemsOrderEntity) throws Exception {
        OrderEntity order = orderDaoService.find(itemsOrderEntity.getOrder().getId());
        if(order == null)
            throw new YSException("VLD017");
        /*Adding item total*/
        order.setTotalCost(order.getTotalCost().add(itemsOrderEntity.getItemTotal()));

        /*Calculating service and vat charge for new item and adding it to ItemServiceAndVatCharge*/
        BigDecimal serviceCharge = BigDecimalUtil.percentageOf(itemsOrderEntity.getItemTotal(), itemsOrderEntity.getServiceCharge());
        BigDecimal serviceAndVatCharge = serviceCharge.add(BigDecimalUtil.percentageOf(itemsOrderEntity.getItemTotal().add(serviceCharge), itemsOrderEntity.getVat()));
        itemsOrderEntity.setServiceAndVatCharge(serviceAndVatCharge);
        order.setItemServiceAndVatCharge(order.getItemServiceAndVatCharge().add(serviceAndVatCharge));
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

        CourierTransactionEntity courierTransaction = systemAlgorithmService.getCourierTransaction(order, dBoySelection, courierTransactionEntity.getCommissionPct(), courierTransactionEntity.getServiceFeePct());
        courierTransactionEntity.setOrderTotal(courierTransaction.getOrderTotal());
        courierTransactionEntity.setAdditionalDeliveryAmt(courierTransaction.getAdditionalDeliveryAmt());
        courierTransactionEntity.setCustomerDiscount(courierTransaction.getCustomerDiscount());
        courierTransactionEntity.setDeliveryCostWithoutAdditionalDvAmt(courierTransaction.getDeliveryCostWithoutAdditionalDvAmt());
        courierTransactionEntity.setDeliveryChargedBeforeDiscount(courierTransaction.getDeliveryChargedBeforeDiscount());
        courierTransactionEntity.setServiceFeeAmt(courierTransaction.getServiceFeeAmt());
        courierTransactionEntity.setDeliveryChargedAfterDiscount(courierTransaction.getDeliveryChargedAfterDiscount());
        courierTransactionEntity.setCustomerPays(courierTransaction.getCustomerPays());
        courierTransactionEntity.setPaidToCourier(courierTransaction.getPaidToCourier());
        courierTransactionEntity.setProfit(courierTransaction.getProfit());

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
                BigDecimal serviceChargeAmount = BigDecimalUtil.percentageOf(itemsOrder.getItemTotal(), BigDecimalUtil.checkNull(itemsOrder.getServiceCharge()));
                BigDecimal serviceAndVatChargeAmount = serviceChargeAmount.add(BigDecimalUtil.percentageOf(serviceChargeAmount.add(itemsOrder.getItemTotal()), BigDecimalUtil.checkNull(itemsOrder.getVat())));

                itemTotalCost = itemTotalCost.add(itemsOrder.getItemTotal());
                itemServiceAndVatCharge = itemServiceAndVatCharge.add(serviceAndVatChargeAmount);
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

        DeliveryBoySelectionEntity dBoySelection = new DeliveryBoySelectionEntity();
        dBoySelection.setDistanceToStore(order.getSystemChargeableDistance());
        dBoySelection.setStoreToCustomerDistance(order.getCustomerChargeableDistance());

        CourierTransactionEntity courierTransactionEntity = order.getCourierTransaction();
        MerchantEntity merchant = new MerchantEntity();
        merchant.setCommissionPercentage(courierTransactionEntity.getCommissionPct());
        merchant.setServiceFee(courierTransactionEntity.getServiceFeePct());

        CourierTransactionEntity courierTransaction = systemAlgorithmService.getCourierTransaction(order, dBoySelection, merchant.getCommissionPercentage(), merchant.getServiceFee());
        courierTransactionEntity.setOrderTotal(courierTransaction.getOrderTotal());
        courierTransactionEntity.setAdditionalDeliveryAmt(courierTransaction.getAdditionalDeliveryAmt());
        courierTransactionEntity.setCustomerDiscount(courierTransaction.getCustomerDiscount());
        courierTransactionEntity.setDeliveryCostWithoutAdditionalDvAmt(courierTransaction.getDeliveryCostWithoutAdditionalDvAmt());
        courierTransactionEntity.setServiceFeeAmt(courierTransaction.getServiceFeeAmt());
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
        if (order == null) {
            throw new YSException("VLD017");
        } else if(order.getDeliveryBoy() == null){
            throw new YSException("ORD014");
        }

        BigDecimal itemTotalCost = BigDecimal.ZERO;
        BigDecimal itemServiceAndVatCharge = BigDecimal.ZERO;

        List<ItemsOrderEntity> itemsOrderEntityList = order.getItemsOrder();
        ItemsOrderEntity itemsOrderEntity = getItemOrderById(itemsOrderEntityList, itemOrder.getId());
        if (itemsOrderEntity == null)
            throw new YSException("VLD025");
        itemsOrderEntity.setQuantity(itemOrder.getQuantity());
        itemsOrderEntity.setItemTotal(itemOrder.getItemTotal());
        itemsOrderEntity.setAvailabilityStatus(itemOrder.getAvailabilityStatus());
        itemsOrderEntity.setVat(itemOrder.getVat());
        itemsOrderEntity.setServiceCharge(itemOrder.getServiceCharge());
        itemsOrderEntity.setNote(itemOrder.getNote());

         /* Updating name of custom item added by delivery boy */
        if(itemsOrderEntity.getCustomItem() != null && itemOrder.getCustomItem() != null){
            itemsOrderEntity.getCustomItem().setName(itemOrder.getCustomItem().getName());
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
                BigDecimal serviceChargeAmount = BigDecimalUtil.percentageOf(itemsOrder.getItemTotal(), BigDecimalUtil.checkNull(itemsOrder.getServiceCharge()));
                BigDecimal serviceAndVatChargeAmount = serviceChargeAmount.add(BigDecimalUtil.percentageOf(serviceChargeAmount.add(itemsOrder.getItemTotal()), BigDecimalUtil.checkNull(itemsOrder.getVat())));
                itemTotalCost = itemTotalCost.add(itemsOrder.getItemTotal());
                itemServiceAndVatCharge = itemServiceAndVatCharge.add(serviceAndVatChargeAmount);
                itemsOrder.setServiceAndVatCharge(serviceAndVatChargeAmount);
            }
        }

       /* Commented since minimum amount is checked at start delivery. */
       /*if(BigDecimalUtil.isLessThen(itemTotalCost, order.getStore().getStoresBrand().getMinOrderAmount())){
            throw new YSException("CRT008", " "+order.getStore().getStoresBrand().getMinOrderAmount());
        }*/

        order.setTotalCost(itemTotalCost);
        order.setItemServiceAndVatCharge(itemServiceAndVatCharge);

        DeliveryBoySelectionEntity dBoySelection = new DeliveryBoySelectionEntity();
        dBoySelection.setDistanceToStore(order.getSystemChargeableDistance());
        dBoySelection.setStoreToCustomerDistance(order.getCustomerChargeableDistance());

        CourierTransactionEntity courierTransactionEntity = order.getCourierTransaction();
        CourierTransactionEntity courierTransaction = systemAlgorithmService.getCourierTransaction(order, dBoySelection, courierTransactionEntity.getCommissionPct(), courierTransactionEntity.getServiceFeePct());
        courierTransactionEntity.setOrderTotal(courierTransaction.getOrderTotal());
        courierTransactionEntity.setAdditionalDeliveryAmt(courierTransaction.getAdditionalDeliveryAmt());
        courierTransactionEntity.setCustomerDiscount(courierTransaction.getCustomerDiscount());
        courierTransactionEntity.setDeliveryCostWithoutAdditionalDvAmt(courierTransaction.getDeliveryCostWithoutAdditionalDvAmt());
        courierTransactionEntity.setServiceFeeAmt(courierTransaction.getServiceFeeAmt());
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

        orderEntity.setOrderCancel(orderCancelEntity);
        orderEntity.setOrderStatus(JobOrderStatus.CANCELLED);
        orderEntity.setDeliveryStatus(DeliveryStatus.CANCELLED);


        /* updating order histories */
        List<DBoyOrderHistoryEntity> dBoyOrderHistoryEntities =  orderEntity.getdBoyOrderHistories();
        for(DBoyOrderHistoryEntity dBoyOrderHistoryEntity: dBoyOrderHistoryEntities){
            if(dBoyOrderHistoryEntity.getDeliveryStatus().equals(DeliveryStatus.PENDING)){
                dBoyOrderHistoryEntity.setDeliveryStatus(DeliveryStatus.CANCELLED);
            }
        }

        /* updating delivery boy */
        DeliveryBoyEntity deliveryBoyEntity = orderEntity.getDeliveryBoy();
        if(deliveryBoyEntity != null){
            deliveryBoyEntity.setActiveOrderNo(deliveryBoyEntity.getActiveOrderNo()-1);
            deliveryBoyEntity.setTotalOrderUndelivered(deliveryBoyEntity.getTotalOrderUndelivered()+1);
            if(deliveryBoyEntity.getActiveOrderNo() <= 0){
                deliveryBoyEntity.setActiveOrderNo(0);
                deliveryBoyEntity.setAvailabilityStatus(DBoyStatus.FREE);
            }
        }

        //TODO dboy transaction implementation
        boolean status = orderDaoService.update(orderEntity);
        if(status){
            UserDeviceEntity userDevice = userDeviceDaoService.getUserDeviceInfoFromOrderId(order.getId());
            String message = MessageBundle.getMessage("CPN007","push_notification.properties");
            String extraDetail = order.getId().toString()+"/status/"+JobOrderStatus.CANCELLED.toString();
            PushNotificationUtil.sendPushNotification(userDevice, message, NotifyTo.CUSTOMER, PushNotificationRedirect.ORDER, extraDetail);
        }
        return status;
    }

    private void courierBoyAccountingsAfterTakingOrder(DeliveryBoyEntity deliveryBoy, OrderEntity order, Boolean partnerShipStatus) throws Exception{
        BigDecimal orderAmount = order.getTotalCost();
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

    private void courierBoyAccountingsAfterOrderDelivery(DeliveryBoyEntity deliveryBoy, OrderEntity order) {
        BigDecimal orderAmtReceived = order.getGrandTotal();
        BigDecimal walletAmount = deliveryBoy.getWalletAmount();
        BigDecimal bankAmount = deliveryBoy.getBankAmount();
        BigDecimal availableAmount = deliveryBoy.getAvailableAmount();

        availableAmount = availableAmount.add(orderAmtReceived);
        walletAmount = walletAmount.add(orderAmtReceived);
        deliveryBoy.setBankAmount(bankAmount);
        deliveryBoy.setWalletAmount(walletAmount);
        deliveryBoy.setAvailableAmount(availableAmount);

        log.info("== AFTER ORDER DELIVERY =="
                + "\n Wallet Amount: " + walletAmount
                + "\t Bank Amount: " + bankAmount
                + "\t Available Amount: " + availableAmount
                + "\t Amount to be Submitted: " + walletAmount);
    }

    @Override
    public OrderSummaryDto getShoppingList(Integer orderId) throws Exception {
        OrderEntity order = orderDaoService.find(orderId);
        if (order == null) {
            throw new YSException("VLD017");
        }
        MerchantEntity merchant = merchantDaoService.getMerchantByOrderId(orderId);
        OrderSummaryDto orderSummary = new OrderSummaryDto();

        List<ItemsOrderEntity> itemsOrder = order.getItemsOrder();
        List<ItemsOrderEntity> itemsOrderEntities = new ArrayList<ItemsOrderEntity>();
        ItemsOrderEntity itemsOrderEntity = null;
        for (ItemsOrderEntity itemOrder : itemsOrder) {
            itemsOrderEntity = itemOrder;
            if (itemOrder.getItem() != null) {
                ItemEntity item = new ItemEntity();
                item.setId(itemOrder.getItem().getId());
                item.setName(itemOrder.getItem().getName());
                item.setUnitPrice(itemOrder.getItem().getUnitPrice());
                itemsOrderEntity.setItem(item);
            }
            itemsOrderEntity.setCustomItem(itemOrder.getCustomItem());
            List<ItemsOrderAttributeEntity> itemsOrderAttributeEntities = new ArrayList<ItemsOrderAttributeEntity>();
            for(ItemsOrderAttributeEntity itemOrderAttribute: itemOrder.getItemOrderAttributes()){
                itemsOrderAttributeEntities.add(itemOrderAttribute);
            }
            itemsOrderEntity.setItemOrderAttributes(itemsOrderAttributeEntities);
            itemsOrderEntities.add(itemsOrderEntity);
        }

        orderSummary.setId(order.getId());
        orderSummary.setItemOrders(itemsOrderEntities);
        OrderSummaryDto.AccountSummary accountSummary = orderSummary.new AccountSummary();
        BigDecimal totalDiscount = BigDecimal.ZERO;
        if(order.getCustomer() != null){
            if(BigDecimalUtil.isGreaterThenOrEqualTo(order.getDeliveryCharge(), order.getCustomer().getRewardsEarned())){
                totalDiscount = order.getCustomer().getRewardsEarned();
            }
        }

        accountSummary.setSubTotal(order.getTotalCost());
        accountSummary.setServiceFee(order.getSystemServiceCharge());
        accountSummary.setVatAndServiceCharge(order.getItemServiceAndVatCharge());
        accountSummary.setDeliveryFee(order.getDeliveryCharge().add(totalDiscount));
        accountSummary.setTotalDiscount(totalDiscount);
        accountSummary.setPartnerShipStatus(merchant.getPartnershipStatus());
        accountSummary.setEstimatedTotal(order.getGrandTotal());
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
        responseOrder.setId(order.getId());
        responseOrder.setOrderName(order.getOrderName());
        responseOrder.setOrderVerificationStatus(order.getOrderVerificationStatus());
        responseOrder.setDeliveryStatus(order.getDeliveryStatus());
        responseOrder.setOrderStatus(order.getOrderStatus());
        responseOrder.setCustomerChargeableDistance(order.getCustomerChargeableDistance());
        responseOrder.setTotalCost(order.getTotalCost());
        responseOrder.setSystemServiceCharge(order.getSystemServiceCharge());
        responseOrder.setDeliveryCharge(order.getDeliveryCharge());
        responseOrder.setGrandTotal(order.getGrandTotal());
        responseOrder.setAssignedTime(order.getAssignedTime());
        responseOrder.setRemainingTime(order.getRemainingTime());
        responseOrder.setSurgeFactor(order.getSurgeFactor());
        responseOrder.setItemServiceAndVatCharge(order.getItemServiceAndVatCharge());
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
        address.setNotes(order.getAddress().getNotes());
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
            String fields = "id,quantity,itemTotal,serviceAndVatCharge,availabilityStatus,vat,serviceCharge,note,customerNote,item";

            Map<String, String> assoc = new HashMap<>();
            Map<String, String> subAssoc = new HashMap<>();
            assoc.put("item", "id,name,availableStartTime,availableEndTime,maxOrderQuantity,minOrderQuantity,unitPrice,itemsImage,attributesTypes");
            subAssoc.put("itemsImage", "url");
            subAssoc.put("attributesTypes", "id,itemsAttribute,multiSelect,type");
            subAssoc.put("itemsAttribute", "id,attribute,unitPrice,selected");

            itemOrder = (ItemsOrderEntity) ReturnJsonUtil.getJsonObject(itemOrder, fields, assoc, subAssoc);
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
            String fields = "id,quantity,itemTotal,serviceAndVatCharge,availabilityStatus,vat,serviceCharge,customItem";
            Map<String, String> assoc = new HashMap<>();
            assoc.put("customItem", "id,name");
            itemOrder = (ItemsOrderEntity) ReturnJsonUtil.getJsonObject(itemOrder, fields, assoc);
        }
        return itemOrder;
    }

    @Override
    public List<Object> get_order_history(Integer dBoyId) throws Exception {
        List<Object> orders = orderDaoService.get_dBoy_order_history(dBoyId);

        List<Object> objects = new ArrayList<>();

        String fields = "id,orderName,deliveryStatus,assignedTime,customer,deliveryBoy,grandTotal,orderDate,dBoyOrderHistories,rating";

        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("customer", "id,user");
        assoc.put("deliveryBoy", "id,user");
        assoc.put("dBoyOrderHistories", "id,amountEarned,orderAcceptedAt,orderCompletedAt,distanceTravelled");
        assoc.put("rating", "id,customerRating,deliveryBoyRating,deliveryBoyComment,customerComment");

        subAssoc.put("user", "id,fullName");

        for (Object order:orders){
            objects.add(ReturnJsonUtil.getJsonObject(order, fields, assoc, subAssoc));
        }

        return objects;
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
}
