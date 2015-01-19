package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.dao.inf.*;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.enums.DBoyStatus;
import com.yetistep.delivr.enums.DeliveryStatus;
import com.yetistep.delivr.enums.JobOrderStatus;
import com.yetistep.delivr.enums.PreferenceType;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.service.inf.CustomerService;
import com.yetistep.delivr.service.inf.SystemPropertyService;
import com.yetistep.delivr.util.*;
import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class CustomerServiceImpl implements CustomerService {
    private static final Logger log = Logger.getLogger(CustomerServiceImpl.class);

    @Autowired
    CustomerDaoService customerDaoService;

    @Autowired
    UserDaoService userDaoService;

    @Autowired
    MerchantDaoService merchantDaoService;

    @Autowired
    DeliveryBoyDaoService deliveryBoyDaoService;

    @Autowired
    SystemPropertyService systemPropertyService;

    @Autowired
    OrderDaoService orderDaoService;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    UserDeviceDaoService userDeviceDaoService;
    @Override
    public void login(CustomerEntity customerEntity) throws Exception {
        log.info("++++++++++++++ Logging Customer ++++++++++++++++");

        UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();
        ReadableUserAgent agent = parser.parse(httpServletRequest.getHeader("User-Agent"));

        customerEntity.getUser().getUserDevice().setFamily(agent.getOperatingSystem().getFamily().toString());
        customerEntity.getUser().getUserDevice().setFamilyName(agent.getOperatingSystem().getFamilyName());
        customerEntity.getUser().getUserDevice().setName(agent.getOperatingSystem().getName());

        //Now Signup Process
        CustomerEntity registeredCustomer = customerDaoService.find(customerEntity.getFacebookId());

        //If Account already exist
        if (registeredCustomer != null) {
            //If Client Permission granted after denied first time then email & DOB should updated
            if ((customerEntity.getUser().getEmailAddress() != null && !customerEntity.getUser().getEmailAddress().isEmpty()) &&
                    (!registeredCustomer.getUser().getEmailAddress().equals(customerEntity.getUser().getEmailAddress())))
                registeredCustomer.getUser().setEmailAddress(customerEntity.getUser().getEmailAddress());

            if(customerEntity.getFbToken()!=null && !customerEntity.getFbToken().isEmpty())
                registeredCustomer.setFbToken(customerEntity.getFbToken());

             log.info("++++++++++++ Updating Customer Info +++++++++++++");
            //Update User Device
            registeredCustomer.getUser().getUserDevice().setUuid(customerEntity.getUser().getUserDevice().getUuid());
            registeredCustomer.getUser().getUserDevice().setDeviceToken(customerEntity.getUser().getUserDevice().getDeviceToken());
            registeredCustomer.getUser().getUserDevice().setFamily(customerEntity.getUser().getUserDevice().getFamily());
            registeredCustomer.getUser().getUserDevice().setFamilyName(customerEntity.getUser().getUserDevice().getFamilyName());
            registeredCustomer.getUser().getUserDevice().setName(customerEntity.getUser().getUserDevice().getName());
            registeredCustomer.getUser().getUserDevice().setBrand(customerEntity.getUser().getUserDevice().getBrand());
            registeredCustomer.getUser().getUserDevice().setModel(customerEntity.getUser().getUserDevice().getModel());
            registeredCustomer.getUser().getUserDevice().setDpi(customerEntity.getUser().getUserDevice().getDpi());
            registeredCustomer.getUser().getUserDevice().setHeight(customerEntity.getUser().getUserDevice().getHeight());
            registeredCustomer.getUser().getUserDevice().setWidth(customerEntity.getUser().getUserDevice().getWidth());


            validateUserDevice(registeredCustomer.getUser().getUserDevice());
            customerDaoService.update(registeredCustomer);

        } else {
            validateUserDevice(customerEntity.getUser().getUserDevice());
//            if (customerEntity.getLatitude() != null) {
//                customerEntity.getUser().getAddresses().get(0).setLatitude(customerEntity.getLatitude());
//                customerEntity.getUser().getAddresses().get(0).setLongitude(customerEntity.getLongitude());
//            }

            customerDaoService.save(customerEntity);

        }
    }


    private void validateUserDevice(UserDeviceEntity userDevice) throws Exception {
        Validator.validateString(userDevice.getName(), "Invalid Name");
        Validator.validateString(userDevice.getFamily(), "Invalid Family");
        Validator.validateString(userDevice.getFamilyName(), "Invalid Family Name");
        Validator.validateString(userDevice.getBrand(), "Invalid Brand");
        Validator.validateString(userDevice.getBrand(), "Invalid UUID");
    }


    @Override
    public void saveCustomer(CustomerEntity customer, HeaderDto headerDto) throws Exception {
//        customer.getUser().setUsername(headerDto.getUsername());
//        customer.getUser().setPassword(headerDto.getPassword());
//        UserEntity userEntity = customer.getUser();
//        if (userEntity.getUsername() == null || userEntity.getPassword() == null || userEntity.getUsername().isEmpty() || userEntity.getPassword().isEmpty())
//            throw new YSException("VLD009");
//
//        /* Setting Default value of customer while sign up. */
//        RoleEntity userRole = userDaoService.getRoleByRole(Role.ROLE_CUSTOMER);
//        userEntity.setRole(userRole);
//        userEntity.setPassword(GeneralUtil.encryptPassword(userEntity.getPassword()));
//        userEntity.setMobileNumber(userEntity.getUsername());
//        userEntity.setMobileVerificationStatus(false);
//        userEntity.setVerifiedStatus(false);
//        userEntity.setBlacklistStatus(false);
//        userEntity.setSubscribeNewsletter(false);
//
//        customer.setTotalOrderPlaced(0);
//        customer.setTotalOrderDelivered(0);
//        customer.setFriendsInvitationCount(0);
//        customer.setReferredFriendsCount(0);
//        customerDaoService.save(customer);
//
//        String verifyCode = GeneralUtil.generateMobileCode();
//        customer.getUser().setVerificationCode(verifyCode);
//        customerDaoService.update(customer);

//        if (profileImage != null && !profileImage.isEmpty()) {
//            log.info("Uploading Profile Image of delivery boy to S3 Bucket ");
//
//            String dir = MessageBundle.separateString("/", "Customer", "Customer" + customer.getId());
//            boolean isLocal = MessageBundle.isLocalHost();
//            String imageName = "pimg" + (isLocal ? "_tmp_" : "_") + customer.getId();
//            String s3Path = GeneralUtil.saveImageToBucket(profileImage, imageName, dir, true);
//            customer.getUser().setProfileImage(s3Path);
//            customerDaoService.update(customer);
//        }
    }

    @Override
    public void addCustomerAddress(HeaderDto headerDto, List<AddressEntity> addresses) throws Exception {
        int customerId = Integer.parseInt(headerDto.getId());
        CustomerEntity customerEntity = customerDaoService.find(customerId);
        if(customerEntity == null){
            throw new YSException("VLD011");
        }
        for(AddressEntity address: addresses){
            address.setUser(customerEntity.getUser());
        }
        customerEntity.getUser().setAddresses(addresses);
        customerDaoService.save(customerEntity);
    }

    @Override
    public void verifyMobile(String mobile, Long facebookId) throws Exception {
        log.info("++++++++ Verifying mobile " + mobile + " +++++++++++");
        CustomerEntity customerEntity  = customerDaoService.findUser(facebookId);
        if(customerEntity == null)
            throw new Exception("VLD011");


//        UserEntity userEntity = userDaoService.findByUserName(headerDto.getUsername());
//        if (userEntity == null)
//            throw new YSException("VLD011");
//        if(!headerDto.getVerificationCode().equals(userEntity.getVerificationCode())){
//            throw new YSException("SEC008");
//        }
//        userEntity.setVerifiedStatus(true);
//        userEntity.setMobileVerificationStatus(true);
        //userDaoService.update(userEntity);
    }

    @Override
    public void saveOrder(RequestJsonDto requestJson, HeaderDto headerDto) throws Exception {
       // OrderEntity order = requestJson.getOrdersOrder();
        List<ItemsOrderEntity> itemsOrder = requestJson.getOrdersItemsOrder();
        Integer brandId = requestJson.getOrdersBrandId();
        Integer customerId = requestJson.getOrdersCustomerId();
        Integer addressId = requestJson.getOrdersAddressId();

        StoresBrandEntity brand = merchantDaoService.findBrandDetail(brandId);
        //FIXME Dummy Value =
        //Long id = 5435435435435435L;
        CustomerEntity customer = customerDaoService.find(customerId);
//        CustomerEntity customer = customerDaoService.find(customerId);
        AddressEntity address = customerDaoService.findAddressById(addressId);

        if(address == null)
            throw new YSException("VLD014");

        if (customer == null)
            throw new YSException("VLD011");

        if (brand == null)
            throw new YSException("VLD012");

//        Integer storeId = requestJson.getOrdersStoreId();
//        StoreEntity storeEntity = merchantDaoService.getStoreById(storeId);
//        if(storeEntity == null)
//            throw new YSException("VLD016");

        OrderEntity order = new OrderEntity();
        order.setAddress(address);
        order.setCustomer(customer);

        order.setOrderVerificationStatus(false);
        order.setDeliveryStatus(DeliveryStatus.PENDING);
        order.setOrderStatus(JobOrderStatus.ORDER_PLACED);

        BigDecimal itemTotalCost = BigDecimal.ZERO;
        BigDecimal itemServiceAndVatCharge = BigDecimal.ZERO;
        for (ItemsOrderEntity iOrder: itemsOrder){
            ItemEntity item = merchantDaoService.getItemDetail(iOrder.getItem().getId());

            iOrder.setItem(item);
            iOrder.setOrder(order);
            iOrder.setAvailabilityStatus(true);
            BigDecimal itemTotal = BigDecimalUtil.calculateCost(iOrder.getQuantity(), item.getUnitPrice());
            iOrder.setItemTotal(itemTotal);
            itemTotalCost = itemTotalCost.add(itemTotal);

            BigDecimal serviceCharge = BigDecimalUtil.percentageOf(itemTotal, item.getServiceCharge());
            BigDecimal serviceAndVatCharge = serviceCharge.add(BigDecimalUtil.percentageOf(itemTotal.add(serviceCharge), item.getVat()));
            itemServiceAndVatCharge = itemServiceAndVatCharge.add(serviceAndVatCharge);
        }
        order.setItemsOrder(itemsOrder);
        order.setTotalCost(itemTotalCost);
        order.setSurgeFactor(getSurgeFactor());
        order.setItemServiceAndVatCharge(itemServiceAndVatCharge);
        List<StoreEntity> stores = merchantDaoService.findStoreByBrand(brandId);
        StoreEntity store = findNearestStoreFromCustomer(order, stores);

        order.setStore(store);
        order.setOrderName(store.getName()+" to "+ order.getAddress().getStreet());
        order.setOrderVerificationCode(Integer.parseInt(GeneralUtil.generateMobileCode()));
        //TODO Send code message to customer
        List<DeliveryBoySelectionEntity> deliveryBoySelectionEntities = calculateStoreToDeliveryBoyDistance(store, deliveryBoyDaoService.findAllCapableDeliveryBoys(), order);
        List<DeliveryBoySelectionEntity> deliveryBoySelectionEntitiesWithProfit =  filterDBoyWithProfitCriteria(order, deliveryBoySelectionEntities, brand.getMerchant());
        order.setDeliveryBoySelections(deliveryBoySelectionEntitiesWithProfit);
        customerDaoService.saveOrder(order);

    }


    private StoreEntity findNearestStoreFromCustomer(OrderEntity order, List<StoreEntity> stores) throws Exception {
        String orderAddress[] = {GeoCodingUtil.getLatLong(order.getAddress().getLatitude(), order.getAddress().getLongitude())};
        String storeAddress[] = new String[stores.size()];
        for (int i = 0; i < stores.size(); i++) {
            storeAddress[i] = GeoCodingUtil.getLatLong(stores.get(i).getLatitude(), stores.get(i).getLongitude());
        }
        List<BigDecimal> distanceList =  GeoCodingUtil.getListOfAirDistances(orderAddress, storeAddress);
        int minimumIndex = distanceList.indexOf(Collections.min(distanceList));

        StoreEntity nearestStore = stores.get(minimumIndex);
        String finalStore[] = new String[1];
        finalStore[0] = GeoCodingUtil.getLatLong(nearestStore.getLatitude(), nearestStore.getLongitude());
        order.setCustomerChargeableDistance(BigDecimalUtil.getDistanceInKiloMeters( GeoCodingUtil.getListOfDistances(orderAddress, finalStore).get(0)));
        return nearestStore;
    }

    private List<DeliveryBoySelectionEntity> calculateStoreToDeliveryBoyDistance(StoreEntity store, List<DeliveryBoyEntity> capableDeliveryBoys, OrderEntity order) throws Exception {
        String storeAddress[] = {GeoCodingUtil.getLatLong(store.getLatitude(), store.getLongitude())};
        String deliveryBoyAddress[] = new String[capableDeliveryBoys.size()];
        for (int i = 0; i < capableDeliveryBoys.size(); i++) {
            if(capableDeliveryBoys.get(i).getAvailabilityStatus().equals(DBoyStatus.BUSY)){
                OrderEntity previousActiveOrder = orderDaoService.getLastActiveOrder(capableDeliveryBoys.get(i).getId());
                if (previousActiveOrder != null) {
                    deliveryBoyAddress[i] = GeoCodingUtil.getLatLong(previousActiveOrder.getAddress().getLatitude(), previousActiveOrder.getAddress().getLongitude());
                }else{
                    deliveryBoyAddress[i] = GeoCodingUtil.getLatLong(capableDeliveryBoys.get(i).getLatitude(), capableDeliveryBoys.get(i).getLongitude());
                }
            }else{
                deliveryBoyAddress[i] = GeoCodingUtil.getLatLong(capableDeliveryBoys.get(i).getLatitude(), capableDeliveryBoys.get(i).getLongitude());
            }
        }


        List<BigDecimal> distanceList = GeoCodingUtil.getListOfDistances(storeAddress, deliveryBoyAddress);
        List<DeliveryBoySelectionEntity> selectionDetails = new ArrayList<DeliveryBoySelectionEntity>();
        List<Integer> timeDetails = new ArrayList<Integer>();
        int timeAtStore = Integer.parseInt(systemPropertyService.readPrefValue(PreferenceType.TIME_AT_STORE));

        for (int i = 0; i < distanceList.size(); i++) {
            DeliveryBoySelectionEntity deliveryBoySelectionEntity = new DeliveryBoySelectionEntity();
            deliveryBoySelectionEntity.setDistanceToStore(BigDecimalUtil.getDistanceInKiloMeters(distanceList.get(i)));
            deliveryBoySelectionEntity.setDeliveryBoy(capableDeliveryBoys.get(i));
            deliveryBoySelectionEntity.setStoreToCustomerDistance(order.getCustomerChargeableDistance());
            deliveryBoySelectionEntity.setOrder(order);
            int timeFactor = Integer.parseInt(systemPropertyService.readPrefValue(GeneralUtil.getTimeTakenFor(capableDeliveryBoys.get(i).getVehicleType())));

            BigDecimal totalDistance = BigDecimalUtil.getDistanceInKiloMeters(distanceList.get(i)).add(order.getCustomerChargeableDistance());
            Integer timeRequired = totalDistance.multiply(new BigDecimal(timeFactor)).setScale(0, RoundingMode.HALF_UP).intValue();

            deliveryBoySelectionEntity.setTimeRequired(timeRequired + timeAtStore);
            int timeRemaining = findRemainingTimeForPreviousOrder(capableDeliveryBoys.get(i).getId()) + timeRequired + timeAtStore;
            deliveryBoySelectionEntity.setTotalTimeRequired(timeRemaining);
            timeDetails.add(timeRemaining);

            deliveryBoySelectionEntity.setAccepted(false);
            selectionDetails.add(deliveryBoySelectionEntity);
            log.info(selectionDetails.toString());
        }
        return filterDeliveryBoySelection(selectionDetails, timeDetails);
        //TODO Filter delivery boys by profit criteria - Push Notifications
    }

    private List<DeliveryBoySelectionEntity> filterDeliveryBoySelection(List<DeliveryBoySelectionEntity> selectionEntities, List<Integer> timeDetails) throws Exception {
        List<DeliveryBoySelectionEntity> filteredDBoys = new ArrayList<DeliveryBoySelectionEntity>();
        int minimumTime = Collections.min(timeDetails);
        int deviationInTime = Integer.parseInt(systemPropertyService.readPrefValue(PreferenceType.DEVIATION_IN_TIME));
        for (DeliveryBoySelectionEntity deliveryBoySelectionEntity : selectionEntities) {
            if ((deliveryBoySelectionEntity.getTotalTimeRequired() - minimumTime) <= deviationInTime) {
                filteredDBoys.add(deliveryBoySelectionEntity);
            }
        }
        return filteredDBoys;
    }

    private int findRemainingTimeForPreviousOrder(Integer deliveryBoyId) throws Exception {
        OrderEntity previousActiveOrder = orderDaoService.getLastActiveOrder(deliveryBoyId);
        if (previousActiveOrder != null) {
            Double minuteDiff = DateUtil.getMinDiff(System.currentTimeMillis(), previousActiveOrder.getOrderDate().getTime());
            int remainingTime = previousActiveOrder.getRemainingTime() - minuteDiff.intValue();
            if (remainingTime <= 0)
                return 0;
            else
                return remainingTime;
        } else {
            return 0;
        }
    }

    private List<DeliveryBoySelectionEntity> filterDBoyWithProfitCriteria(OrderEntity order, List<DeliveryBoySelectionEntity> deliveryBoySelectionEntities, MerchantEntity merchant) throws Exception {
        log.info("Unfiltered Dboys:"+deliveryBoySelectionEntities.toString());
        List<DeliveryBoySelectionEntity> filteredDeliveryBoys = new ArrayList<DeliveryBoySelectionEntity>();
        for (DeliveryBoySelectionEntity deliveryBoySelectionEntity : deliveryBoySelectionEntities) {
            if (checkProfitCriteria(order, deliveryBoySelectionEntity, merchant))
                filteredDeliveryBoys.add(deliveryBoySelectionEntity);
        }
        log.info("Filtered Dboys:"+filteredDeliveryBoys.toString());
        return filteredDeliveryBoys;
    }

    private Boolean checkProfitCriteria(OrderEntity order, DeliveryBoySelectionEntity dBoySelection, MerchantEntity merchant)throws Exception {
        BigDecimal DBOY_ADDITIONAL_PER_KM_CHARGE = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DBOY_ADDITIONAL_PER_KM_CHARGE));
        BigDecimal RESERVED_COMM_PER_BY_SYSTEM = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.RESERVED_COMM_PER_BY_SYSTEM));
        BigDecimal DBOY_PER_KM_CHARGE_UPTO_2KM = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DBOY_PER_KM_CHARGE_UPTO_2KM));
        BigDecimal DBOY_PER_KM_CHARGE_ABOVE_2KM = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DBOY_PER_KM_CHARGE_ABOVE_2KM));
        BigDecimal DBOY_COMMISSION = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DBOY_COMMISSION));
        BigDecimal DBOY_MIN_AMOUNT = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DBOY_MIN_AMOUNT));
        BigDecimal DELIVERY_FEE_VAT = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DELIVERY_FEE_VAT));
        BigDecimal MINIMUM_PROFIT_PERCENTAGE = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.MINIMUM_PROFIT_PERCENTAGE));
        BigDecimal ADDITIONAL_KM_FREE_LIMIT = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.ADDITIONAL_KM_FREE_LIMIT));
        BigDecimal ZERO = BigDecimal.ZERO;
        BigDecimal TWO = new BigDecimal(2);
         /* 1. ===== Order Total ======= */
        BigDecimal totalOrder = order.getTotalCost();
        /* 2. ======= Commission Percent ====== */
        BigDecimal commissionPct = merchant.getCommissionPercentage();
        /* 3. ===== Distance Store to Customer(KM) ======== */
        BigDecimal storeToCustomerDistance =  dBoySelection.getStoreToCustomerDistance();
        /* 4. ====== Distance Courier to Store (KM) ======== */
        BigDecimal courierToStoreDistance = dBoySelection.getDistanceToStore();
        /* 5. ==== Service Fee % ======= */
        BigDecimal serviceFeePct = merchant.getServiceFee();
        /* 6. ====== Additional delivery amt ======= */
        BigDecimal additionalDeliveryAmt = ZERO;
        if(BigDecimalUtil.isGreaterThen(courierToStoreDistance, ADDITIONAL_KM_FREE_LIMIT))
            additionalDeliveryAmt = courierToStoreDistance.subtract(ADDITIONAL_KM_FREE_LIMIT).multiply(DBOY_ADDITIONAL_PER_KM_CHARGE);
       /* 7. ==== Discount on delivery to customer ======= */
        BigDecimal customerDiscount = ZERO;
        BigDecimal systemReservedCommissionAmt = ZERO;
        if(BigDecimalUtil.isGreaterThenZero(commissionPct)){
            BigDecimal totalCommission = BigDecimalUtil.percentageOf(totalOrder, commissionPct);
            systemReservedCommissionAmt = BigDecimalUtil.percentageOf(totalCommission, RESERVED_COMM_PER_BY_SYSTEM);
            customerDiscount = totalCommission.subtract(systemReservedCommissionAmt);
        }
        /* 8. ==== Surge Factor ======= */
        Integer surgeFactor = order.getSurgeFactor();
        /* 9. ====== Delivery cost (Does not include additional delivery amt) ============== */
        BigDecimal deliveryCostWithoutAdditionalDvAmt = ZERO;
        if(BigDecimalUtil.isLessThen(storeToCustomerDistance, TWO))
            deliveryCostWithoutAdditionalDvAmt = DBOY_PER_KM_CHARGE_UPTO_2KM.multiply(new BigDecimal(surgeFactor));
        else
            deliveryCostWithoutAdditionalDvAmt = storeToCustomerDistance.multiply(DBOY_PER_KM_CHARGE_ABOVE_2KM).multiply(new BigDecimal(surgeFactor));
        /* 10. ======= Service Fee Amount =========== */
        BigDecimal serviceFeeAmt = BigDecimalUtil.percentageOf(totalOrder, serviceFeePct);
        /* 11. ====== Delivery charged to customer before Discount ======== */
        BigDecimal deliveryChargedBeforeDiscount = ZERO;
        if(BigDecimalUtil.isGreaterThenOrEqualTo(deliveryCostWithoutAdditionalDvAmt, customerDiscount)){
            deliveryChargedBeforeDiscount = deliveryCostWithoutAdditionalDvAmt.subtract(customerDiscount);
            deliveryChargedBeforeDiscount = deliveryChargedBeforeDiscount.add(BigDecimalUtil.percentageOf(deliveryChargedBeforeDiscount, DELIVERY_FEE_VAT));
        }
        /* 12. ======= Customer Available balance before discount ====== */
        BigDecimal customerBalanceBeforeDiscount = order.getCustomer().getRewardsEarned();
        /* 13. ======== Delivery charged to customer After Discount ====== */
        BigDecimal deliveryChargedAfterDiscount = ZERO;
        if(BigDecimalUtil.isGreaterThen(deliveryChargedBeforeDiscount, customerBalanceBeforeDiscount))
            deliveryChargedAfterDiscount = deliveryChargedBeforeDiscount.subtract(customerBalanceBeforeDiscount);
        /* 14. ======= Customer available balance after discount ======== */
        BigDecimal customerBalanceAfterDiscount = ZERO;
        if(BigDecimalUtil.isGreaterThen(deliveryChargedBeforeDiscount, customerBalanceBeforeDiscount))
            customerBalanceAfterDiscount = customerBalanceBeforeDiscount.subtract(deliveryChargedBeforeDiscount);

        /* 15. ====== Customer Pays ========*/
        BigDecimal customerPays = order.getTotalCost().add(deliveryChargedAfterDiscount).add(serviceFeeAmt).add(order.getItemServiceAndVatCharge());
        order.setGrandTotal(customerPays);
        order.setDeliveryCharge(deliveryChargedAfterDiscount);
        order.setSystemServiceCharge(serviceFeeAmt);

        /* 16. ======= Paid to Courier ====== */
        BigDecimal paidToCourier = ZERO;
        if(BigDecimalUtil.isGreaterThen(BigDecimalUtil.percentageOf(deliveryCostWithoutAdditionalDvAmt, DBOY_COMMISSION), DBOY_MIN_AMOUNT))
            paidToCourier = BigDecimalUtil.percentageOf(deliveryCostWithoutAdditionalDvAmt, DBOY_COMMISSION).add(additionalDeliveryAmt);
        else
            paidToCourier = DBOY_MIN_AMOUNT.add(additionalDeliveryAmt);
        dBoySelection.setPaidToCourier(paidToCourier);

        /* 16 ===== Profit ====== */
        // total order * profit% = >  actual profit
        BigDecimal profit = ZERO;
        profit = BigDecimalUtil.percentageOf(totalOrder, commissionPct).add(deliveryChargedBeforeDiscount).add(serviceFeeAmt).subtract(paidToCourier);

        if(BigDecimalUtil.isLessThen(profit, BigDecimalUtil.percentageOf(totalOrder, MINIMUM_PROFIT_PERCENTAGE))){
            System.err.println("No Profit");
            return false;
        }
        return true;
    }

    private Integer getSurgeFactor() throws Exception{
        Integer surgeFactor = 0;
        String currentTime = DateUtil.getCurrentTime().toString();
        if(DateUtil.isTimeBetweenTwoTime("07:00:00", "21:00:00", currentTime))
            surgeFactor = 1; // if Time is 7 AM-9 PM
        else if(DateUtil.isTimeBetweenTwoTime("21:00:00", "22:00:00", currentTime))
            surgeFactor = 2; // if Time is 9-10 PM
        else if(DateUtil.isTimeBetweenTwoTime("22:00:00", "23:00:00", currentTime))
            surgeFactor = 3; // if Time is 10 PM-11 PM
        else
            surgeFactor = 4; // if Time is 11 PM-7 AM
        return surgeFactor;
    }

    public CustomerEntity getCustomerByFbId(Long facebook_id) throws Exception{
        CustomerEntity customer = customerDaoService.find(facebook_id);
        return customer;
    }


    /* Used For Only Manager and Account Registration */
    public void registerCustomer(UserEntity user, HeaderDto headerDto) throws Exception{

        RoleEntity userRole = userDaoService.getRoleByRole(user.getRole().getRole());
        user.setRole(userRole);

        CustomerEntity referrer = getCustomerByFbId(Long.parseLong(headerDto.getId()));

        Integer referred_friends_count = referrer.getReferredFriendsCount();
        if(referred_friends_count == null){
            referred_friends_count = 1;
        }else{
            referred_friends_count++;
        }
        referrer.setReferredFriendsCount(referred_friends_count);
        referrer.getUser().setLastActivityDate(DateUtil.getCurrentTimestampSQL());
        customerDaoService.update(referrer);

        CustomerEntity cUser = getCustomerByFbId(user.getCustomer().getFacebookId());

        if(cUser != null)
            throw new YSException("VLD010");

        if(referrer == null)
            throw new YSException("VLD011");

        if(referrer.getReferredFriendsCount() != null && referrer.getReferredFriendsCount() >= Integer.parseInt(systemPropertyService.readPrefValue(PreferenceType.MAX_REFERRED_FRIENDS_COUNT)))
            throw new YSException("VLD021");

        user.getCustomer().setReferredBy(Long.parseLong(headerDto.getId()));
        user.getCustomer().setFbToken(headerDto.getAccessToken());
        user.getCustomer().setUser(user);
        user.getCustomer().setRewardsEarned(new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.CUSTOMER_REWARD_AMOUNT)));
        userDaoService.save(user);
    }
}
