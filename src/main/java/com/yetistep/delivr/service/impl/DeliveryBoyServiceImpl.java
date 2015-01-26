package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.dao.inf.*;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.OrderSummaryDto;
import com.yetistep.delivr.dto.PaginationDto;
import com.yetistep.delivr.enums.*;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.model.mobile.dto.PastDeliveriesDto;
import com.yetistep.delivr.service.inf.DeliveryBoyService;
import com.yetistep.delivr.service.inf.SystemAlgorithmService;
import com.yetistep.delivr.service.inf.SystemPropertyService;
import com.yetistep.delivr.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeliveryBoyServiceImpl implements DeliveryBoyService {
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

    @Override
    public void saveDeliveryBoy(DeliveryBoyEntity deliveryBoy, HeaderDto headerDto) throws Exception {
        log.info("++++++++++++++++++ Creating Delivery Boy +++++++++++++++++");
        deliveryBoy.getUser().setUsername(headerDto.getUsername());
        deliveryBoy.getUser().setPassword(headerDto.getPassword());
        UserEntity user = deliveryBoy.getUser();
        if ((user.getUsername() == null || user.getPassword() == null) || (user.getUsername().isEmpty() || user.getPassword().isEmpty()))
            throw new YSException("VLD009");
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
        deliveryBoyEntity.getUser().setRole(null);
        return deliveryBoyEntity;
    }

    @Override
    public List<DeliveryBoyEntity> findAllDeliverBoy() throws Exception {
        log.info("Retrieving list of Deliver Boys");
        List<DeliveryBoyEntity> deliveryBoyEntities = deliveryBoyDaoService.findAll();
        /*For filtering role -- set to null as all delivery boy has same role*/
        for(DeliveryBoyEntity deliveryBoyEntity: deliveryBoyEntities){
            deliveryBoyEntity.getUser().setRole(null);
        }
        return deliveryBoyEntities;
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
        for(AddressEntity addressEntity: deliveryBoyEntity.getUser().getAddresses()){
            for(AddressEntity address: dBoyEntity.getUser().getAddresses()){
                if(address.getId().equals(addressEntity.getId())){
                   address.setStreet(addressEntity.getStreet());
                   address.setCity(addressEntity.getCity());
                   address.setState(addressEntity.getState());
                   address.setCountry(addressEntity.getCountry());
                   address.setCountryCode(addressEntity.getCountryCode());
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

    @Transactional(readOnly = true)
    @Override
    public DeliveryBoyEntity dboyLogin(HeaderDto headerDto) throws Exception {
        log.info("+++++++++++++++ Checking DBOY Credential +++++++++++++++");
        UserEntity userEntity = userDaoService.findByUserName(headerDto.getUsername());

        if(userEntity == null)
            throw new YSException("VLD011");

        GeneralUtil.matchDBPassword(headerDto.getPassword(), userEntity.getPassword());

        userEntity.setRole(null);
        DeliveryBoyEntity deliveryBoyEntity = userEntity.getDeliveryBoy();
        //DeliveryBoyEntity deliveryBoyEntity = deliveryBoyDaoService.find(userEntity.getDeliveryBoy().getId());
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
    public List<OrderEntity> getActiveOrders(Integer deliveryBoyId) throws Exception{
        List<OrderEntity> orderEntities = orderDaoService.getActiveOrdersList(deliveryBoyId);
        for(OrderEntity orderEntity: orderEntities){
            updateRemainingAndElapsedTime(orderEntity);
        }
        return orderEntities;
    }

    private void updateRemainingAndElapsedTime(OrderEntity orderEntity){
        Double minuteDiff = DateUtil.getMinDiff(System.currentTimeMillis(), orderEntity.getOrderDate().getTime());
        int remainingTime = orderEntity.getRemainingTime() - minuteDiff.intValue();
        orderEntity.setElapsedTime(minuteDiff.intValue());
        remainingTime = (remainingTime < 0) ? 0 : remainingTime;
        orderEntity.setRemainingTime(remainingTime);
    }

    @Override
    public Boolean changeOrderStatus(OrderEntity orderEntity, Integer deliveryBoyId) throws Exception{
        OrderEntity order = orderDaoService.find(orderEntity.getId());
        if(order == null){
            throw new YSException("VLD017");
        }
        JobOrderStatus orderStatus = orderEntity.getOrderStatus();
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
            deliveryBoySelectionEntity.setAccepted(true);
            DeliveryBoyEntity deliveryBoyEntity = deliveryBoySelectionEntity.getDeliveryBoy();
            deliveryBoyEntity.setActiveOrderNo(deliveryBoyEntity.getActiveOrderNo()+1);
            deliveryBoyEntity.setTotalOrderTaken(deliveryBoyEntity.getTotalOrderTaken()+1);
            deliveryBoyEntity.setTotalOrderUndelivered(deliveryBoyEntity.getTotalOrderUndelivered()+1);
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
            orderEntity.setCourierTransaction(courierTransaction);

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

            return orderDaoService.update(orderEntity);
        } else if (deliveryBoyId.equals(deliveryBoySelectionEntity.getDeliveryBoy().getId())) {
            throw new YSException("ORD005");
        }
        throw new YSException("ORD001");
    }

    private Boolean startJob(OrderEntity order, Integer deliveryBoyId) throws Exception{
        if(!order.getDeliveryBoy().getId().equals(deliveryBoyId)){
            throw new YSException("ORD003");
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
        return orderDaoService.update(order);
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
        return orderDaoService.update(order);
    }

    private Boolean goRouteToDelivery(OrderEntity order, Integer deliveryBoyId) throws Exception {
        if(!order.getDeliveryBoy().getId().equals(deliveryBoyId)){
            throw new YSException("ORD003");
        }
        JobOrderStatus.traverseJobStatus(order.getOrderStatus(), JobOrderStatus.IN_ROUTE_TO_DELIVERY);
        order.setOrderStatus(JobOrderStatus.IN_ROUTE_TO_DELIVERY);
        boolean partnerShipStatus = merchantDaoService.findPartnerShipStatusFromOrderId(order.getId());
        courierBoyAccountingsAfterTakingOrder(order.getDeliveryBoy(), order, partnerShipStatus);
        return orderDaoService.update(order);
    }

    private Boolean deliverOrder(OrderEntity orderEntity, OrderEntity order, Integer deliveryBoyId) throws Exception {
        if(!order.getDeliveryBoy().getId().equals(deliveryBoyId)){
            throw new YSException("ORD003");
        }
        //TODO check order id as well
        if(!orderEntity.getOrderVerificationCode().equals(order.getOrderVerificationCode())){
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
                break;
            }
        }

        DeliveryBoyEntity deliveryBoyEntity = order.getDeliveryBoy();
        deliveryBoyEntity.setActiveOrderNo(deliveryBoyEntity.getActiveOrderNo()-1);
        if(deliveryBoyEntity.getActiveOrderNo() == 0){
            deliveryBoyEntity.setAvailabilityStatus(DBoyStatus.FREE);
        }
        deliveryBoyEntity.setTotalOrderDelivered(deliveryBoyEntity.getTotalOrderDelivered() + 1);
        deliveryBoyEntity.setTotalOrderUndelivered(deliveryBoyEntity.getTotalOrderUndelivered() - 1);
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
        return orderDaoService.update(order);
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
            if(!dBoyEntity.getAvailabilityStatus().equals(DBoyStatus.FREE)){
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
        order.setTotalCost(order.getTotalCost().add(itemsOrderEntity.getItemTotal()));
        List<ItemsOrderEntity> itemsOrderEntities = order.getItemsOrder();
        itemsOrderEntities.add(itemsOrderEntity);
        order.setItemsOrder(itemsOrderEntities);

        CourierTransactionEntity courierTransactionEntity = order.getCourierTransaction();
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

        return orderDaoService.save(order);
    }

    @Override
    public Boolean updateOrders(List<ItemsOrderEntity> itemOrders) throws Exception {
        for(ItemsOrderEntity itemsOrder : itemOrders){
            ItemsOrderEntity itemsOrderEntity = itemsOrderDaoService.find(itemsOrder.getId());
            if(itemsOrderEntity == null)
                throw new YSException("VLD020");
            itemsOrderEntity.setQuantity(itemsOrder.getQuantity());
            itemsOrderEntity.setItemTotal(itemsOrder.getItemTotal());
            itemsOrderEntity.setAvailabilityStatus(itemsOrder.getAvailabilityStatus());
            itemsOrderEntity.setNote(itemsOrder.getNote());
            itemsOrderEntity.setCustomItem(itemsOrder.getCustomItem());
            itemsOrderDaoService.update(itemsOrderEntity);
        }
        //TODO add price in totalCost of item, grandTotal and service fee
        return true;
    }

    @Override
    public Boolean cancelOrder(OrderEntity order) throws Exception {
        OrderEntity orderEntity = orderDaoService.find(order.getId());
        if(orderEntity == null){
            throw new YSException("VLD017");
        }
        log.info("About to Cancel order with Order ID:"+order.getId());

        /*Setting Ratings */
        if (order.getRating() != null){
            RatingEntity rating = (orderEntity.getRating() == null) ? new RatingEntity() : orderEntity.getRating();
            if(order.getRating().getCustomerRating()!=null)
                rating.setCustomerRating(order.getRating().getCustomerRating());
            if(order.getRating().getDeliveryBoyComment()!=null)
                rating.setDeliveryBoyComment(order.getRating().getDeliveryBoyComment());
            if(order.getRating().getDeliveryBoyRating()!=null)
                rating.setDeliveryBoyRating(order.getRating().getDeliveryBoyRating());
            if(order.getRating().getCustomerComment()!=null)
                rating.setCustomerComment(order.getRating().getCustomerComment());
        }

        /*Setting OrderCancelEntity */
        OrderCancelEntity orderCancelEntity = orderEntity.getOrderCancel();
        if(orderCancelEntity != null)
            throw new YSException("ORD006");
        else
            orderCancelEntity = new OrderCancelEntity();
        orderCancelEntity.setCancelledDate(DateUtil.getCurrentTimestampSQL());
        if(!order.getOrderCancel().getCancelReason().equals(CancelReason.OTHERS)){
            orderCancelEntity.setReason(order.getOrderCancel().getCancelReason().toString());
        }else{
            orderCancelEntity.setReason(order.getOrderCancel().getReason());
        }
        orderCancelEntity.setJobOrderStatus(orderEntity.getOrderStatus());
        orderCancelEntity.setUser(order.getOrderCancel().getUser());
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
            if(deliveryBoyEntity.getActiveOrderNo() == 0){
                deliveryBoyEntity.setAvailabilityStatus(DBoyStatus.FREE);
            }
        }

        //TODO dboy rating implementation and transaction implementation
        return orderDaoService.update(orderEntity);
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
                item.setVat(itemOrder.getItem().getVat());
                item.setServiceCharge(itemOrder.getItem().getServiceCharge());
                itemsOrderEntity.setItem(item);
            }
            itemsOrderEntities.add(itemsOrderEntity);
        }

        orderSummary.setId(order.getId());
        orderSummary.setItemOrders(itemsOrderEntities);
        if(order.getCourierTransaction() != null){
            CourierTransactionEntity courierTransaction = order.getCourierTransaction();
            orderSummary.setSubTotal(courierTransaction.getOrderTotal());
            orderSummary.setServiceFee(courierTransaction.getServiceFeeAmt());
            orderSummary.setDeliveryFee(courierTransaction.getDeliveryCostWithoutAdditionalDvAmt());
            orderSummary.setTotalDiscount(courierTransaction.getCustomerBalanceBeforeDiscount().subtract(courierTransaction.getCustomerBalanceAfterDiscount()));
            orderSummary.setEstimatedTotal(courierTransaction.getCustomerPays());
        }
        orderSummary.setVatAndServiceCharge(order.getItemServiceAndVatCharge());
        orderSummary.setPartnerShipStatus(merchant.getPartnershipStatus());
        return orderSummary;
    }

    @Override
    public JobOrderStatus getJobOrderStatusFromOrderId(Integer orderId) throws Exception {
        return orderDaoService.getJobOrderStatus(orderId);
    }
}
