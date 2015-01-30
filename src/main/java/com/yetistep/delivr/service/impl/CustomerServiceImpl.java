package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.dao.inf.*;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.enums.*;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.model.mobile.AddressDto;
import com.yetistep.delivr.model.mobile.dto.CheckOutDto;
import com.yetistep.delivr.schedular.ScheduleChanger;
import com.yetistep.delivr.service.inf.CustomerService;
import com.yetistep.delivr.service.inf.SystemAlgorithmService;
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

    @Autowired
    AddressDaoService addressDaoService;

    @Autowired
    ItemsAttributeDaoService itemsAttributeDaoService;

    @Autowired
    CartDaoService cartDaoService;

    @Autowired
    CartAttributesDaoService cartAttributesDaoService;

    @Autowired
    SystemAlgorithmService systemAlgorithmService;

    @Autowired
    StoresBrandDaoService storesBrandDaoService;

    @Autowired
    private ScheduleChanger scheduleChanger;

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
                    (registeredCustomer.getUser().getEmailAddress() == null || !registeredCustomer.getUser().getEmailAddress().equals(customerEntity.getUser().getEmailAddress())))
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
            customerEntity.setRewardsEarned(BigDecimal.ZERO);
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
    public Integer addCustomerAddress(AddressDto addressDto) throws Exception {
        log.info("+++++++++++++++ Adding Customer Address +++++++++++++++");
        UserEntity user = addressDto.getUser();
        AddressEntity address = addressDto.getAddress();

        /* Check Customer JSON */
        if (user.getCustomer() == null || user.getCustomer().getFacebookId() == null)
            throw new YSException("JSN001");

        /* Check Mobile and Address */
        if (user.getLastAddressMobile() == null)
            throw new YSException("JSN001");

        /* Check Customer Existence */
        CustomerEntity customerEntity = customerDaoService.findUser(user.getCustomer().getFacebookId());
        if (customerEntity == null)
            throw new YSException("VLD011");

        /* Check Valid Mobile and Mobile Code */
        if (user.getLastAddressMobile().equals(customerEntity.getUser().getLastAddressMobile())) {
            if (user.getVerificationCode() != null) {
                if (!user.getVerificationCode().equals(customerEntity.getUser().getVerificationCode()))
                    throw new YSException("SEC011");
            } else {
                String mobCode = addressDaoService.getMobileCode(customerEntity.getUser().getId(), user.getLastAddressMobile());
                if (mobCode == null)
                    throw new YSException("SEC010");
                else
                    user.setVerificationCode(mobCode);
            }

        } else {
            String mobCode = addressDaoService.getMobileCode(customerEntity.getUser().getId(), user.getLastAddressMobile());
            if (mobCode == null)
                throw new YSException("SEC010");
            else
                user.setVerificationCode(mobCode);
        }


        UserEntity usr  = new UserEntity();
        usr.setId(customerEntity.getUser().getId());

        address.setUser(usr);
        address.setMobileNumber(user.getLastAddressMobile());
        address.setVerificationCode(user.getVerificationCode());
        address.setVerified(true);

       // List<AddressEntity> addressEntities = new ArrayList<>();
        //addressEntities.add(address);

        //customerEntity.getUser().setAddresses(addressEntities);
        //customerDaoService.save(customerEntity);
        addressDaoService.save(address);
        return address.getId();
    }

    @Override
    public AddressDto verifyMobile(String mobile, Long facebookId) throws Exception {
        log.info("++++++++ Verifying mobile " + mobile + " +++++++++++");
        CustomerEntity customerEntity  = customerDaoService.findUser(facebookId);
        if(customerEntity == null)
            throw new Exception("VLD011");

        String mobCode = addressDaoService.getMobileCode(customerEntity.getUser().getId(), mobile);
        String verificationCode = null;
        Boolean isValid = false;
        if(mobCode == null) {
            log.debug("++++++ Updating Mobile No and Validation Code");
            verificationCode = GeneralUtil.generateMobileCode();
            userDaoService.updateDeliveryContact(customerEntity.getUser().getId(), mobile, verificationCode);
        } else {
            verificationCode = String.valueOf(mobCode);
            isValid = true;
        }

        AddressDto address = new AddressDto();
        address.setVerificationCode(verificationCode);
        address.setMobileValidate(isValid);

        return address;

    }

    @Override
    public UserEntity getDeliveredAddress(Long facebookId) throws Exception {
        log.info("++++++++++++++ Getting my delivered address ++++++++++++++");

        CustomerEntity customerEntity  = customerDaoService.findUser(facebookId);
        if(customerEntity == null)
            throw new Exception("VLD011");

        UserEntity user = customerEntity.getUser();

        List<AddressEntity> address = addressDaoService.getDeliveredAddress(user.getId());
        if(address.size() == 0)
            address = null;

        user.setAddresses(address);
        return user;
    }

    @Override
    public void deleteDeliveredAddress(Integer addressId) throws Exception {
        log.info("+++++++++++ Deleting address of id " + addressId + " +++++++++++++");

        AddressEntity address = new AddressEntity();
        address.setId(addressId);

        addressDaoService.delete(address);

    }

    @Override
    public CheckOutDto getCheckOutInfo(Long facebookId, Integer addressId) throws Exception {
        log.info("+++++++++++++++ Getting Check Out Info of client fb id : " + facebookId + " +++++++++++++++++");

        CheckOutDto checkOutDto = new CheckOutDto();
        List<ItemEntity> items = new ArrayList<>();

        List<CartEntity> carts = cartDaoService.getMyCarts(facebookId);
        if(carts==null || carts.size() == 0)
            throw new YSException("CRT001");

        AddressEntity address = addressDaoService.getMyAddress(addressId);

        Integer brandId = carts.get(0).getStoresBrand().getId();
        BigDecimal subTotal = BigDecimal.ZERO;
        BigDecimal merchantTax = BigDecimal.ZERO;
        for(CartEntity cart : carts){
            ItemEntity item = merchantDaoService.getItemDetail(cart.getItem().getId());

            //Calculating Sub Total
            BigDecimal attributePrice = cartAttributesDaoService.findAttributesPrice(cart.getId());

            BigDecimal total = BigDecimalUtil.calculateCost(cart.getOrderQuantity(), item.getUnitPrice(), attributePrice);

            BigDecimal itemTax = BigDecimal.ZERO;

            if(item.getServiceCharge()!=null && BigDecimalUtil.isGreaterThenZero(item.getServiceCharge()))
                itemTax =  BigDecimalUtil.percentageOf(total, item.getServiceCharge());

            if(item.getVat()!=null && BigDecimalUtil.isGreaterThenZero(item.getVat()))
                itemTax = itemTax.add(BigDecimalUtil.percentageOf(total.add(itemTax), item.getVat()));

            merchantTax = merchantTax.add(itemTax);
            subTotal = subTotal.add(total);
            //Set Item
            ItemEntity tempItem = new ItemEntity();
            tempItem.setId(item.getId());
            tempItem.setUnitPrice(total);
            tempItem.setOrderQuantity(cart.getOrderQuantity());
            tempItem.setName(item.getName());
            if(item.getItemsImage()!=null && item.getItemsImage().size() > 0)
                tempItem.setImageUrl(item.getItemsImage().get(0).getUrl());

            items.add(tempItem);
        }
        OrderEntity order = new OrderEntity();
        order.setAddress(address);

        /* Get All Active Active Stores */
        List<StoreEntity> stores = merchantDaoService.findActiveStoresByBrand(brandId);

        /* Get Nearest Store From Customer */
        findNearestStoreFromCustomer(order, stores);
        BigDecimal storeToCustomerDistance = order.getCustomerChargeableDistance();


        /* Get Merchant */
        Integer merchantId = storesBrandDaoService.getMerchantId(brandId);

        /* Now Get Merchant's Commission and Percentage */
        MerchantEntity merchant = merchantDaoService.getCommissionAndVat(merchantId);
        BigDecimal commissionPct = merchant.getCommissionPercentage() != null ? merchant.getCommissionPercentage() : BigDecimal.ZERO;
        BigDecimal serviceFeePct = merchant.getServiceFee() != null ? merchant.getServiceFee() : BigDecimal.ZERO;

        /* Customer Reward Money */

        BigDecimal customerBalanceBeforeDiscount = customerDaoService.getRewardsPoint(facebookId);

        /* Now Algorithm */
        // ==== Discount on delivery to customer =======
        BigDecimal customerDiscount = BigDecimal.ZERO;
        BigDecimal systemReservedCommissionAmt = BigDecimal.ZERO;
        if(BigDecimalUtil.isGreaterThenZero(commissionPct)){
            BigDecimal totalCommission = BigDecimalUtil.percentageOf(subTotal,commissionPct);
            systemReservedCommissionAmt = BigDecimalUtil.percentageOf(totalCommission, new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.RESERVED_COMM_PER_BY_SYSTEM)));
            customerDiscount = totalCommission.subtract(systemReservedCommissionAmt);
        }

         /* 8. ==== Surge Factor ======= */
        Integer surgeFactor = getSurgeFactor();


         /* 9. ====== Delivery cost (Does not include additional delivery amt) ============== */
        BigDecimal deliveryCostWithoutAdditionalDvAmt = BigDecimal.ZERO;
        if(BigDecimalUtil.isLessThen(storeToCustomerDistance, new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DEFAULT_NKM_DISTANCE))))
            deliveryCostWithoutAdditionalDvAmt = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DBOY_PER_KM_CHARGE_UPTO_NKM)).multiply(new BigDecimal(surgeFactor));
        else
            deliveryCostWithoutAdditionalDvAmt = storeToCustomerDistance.multiply(new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DBOY_PER_KM_CHARGE_ABOVE_NKM))).multiply(new BigDecimal(surgeFactor));

        /* 10. ======= Service fee Amount with VAT =========== */
        BigDecimal serviceFeeAmt = BigDecimalUtil.percentageOf(subTotal, serviceFeePct);
        serviceFeeAmt = serviceFeeAmt.add(BigDecimalUtil.percentageOf(serviceFeeAmt, new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DELIVERY_FEE_VAT))));

        /* 11. ====== Delivery cost  with VAT and with Discount from system ======== */
        BigDecimal deliveryChargedBeforeDiscount = BigDecimal.ZERO;
        if(BigDecimalUtil.isGreaterThenOrEqualTo(deliveryCostWithoutAdditionalDvAmt, customerDiscount)){
            deliveryChargedBeforeDiscount = deliveryCostWithoutAdditionalDvAmt.subtract(customerDiscount);
            deliveryChargedBeforeDiscount = deliveryChargedBeforeDiscount.add(BigDecimalUtil.percentageOf(deliveryChargedBeforeDiscount, new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DELIVERY_FEE_VAT))));
        }


        /* Item Detail and Others */
        StoresBrandEntity storeBrand = new StoresBrandEntity();
        storeBrand.setId(brandId);
        storeBrand.setBrandName(carts.get(0).getStoresBrand().getBrandName());
        storeBrand.setBrandLogo(carts.get(0).getStoresBrand().getBrandLogo());


        checkOutDto.setStoresBrand(storeBrand);
        checkOutDto.setItems(items);
        checkOutDto.setSubTotal(subTotal.setScale(0, BigDecimal.ROUND_DOWN));
        checkOutDto.setTax(merchantTax.setScale(0, BigDecimal.ROUND_DOWN));
        checkOutDto.setServiceFee(serviceFeeAmt.setScale(0, BigDecimal.ROUND_DOWN));
        checkOutDto.setDeliveryFee(deliveryChargedBeforeDiscount.setScale(0, BigDecimal.ROUND_DOWN));

        if(BigDecimalUtil.isZero(checkOutDto.getDeliveryFee())) {
            customerBalanceBeforeDiscount = BigDecimal.ZERO;
        }

        if(BigDecimalUtil.isLessThenOrEqualTo(checkOutDto.getDeliveryFee(), customerBalanceBeforeDiscount)){
            customerBalanceBeforeDiscount =  checkOutDto.getDeliveryFee();
        }

        checkOutDto.setDiscount(customerBalanceBeforeDiscount.setScale(0, BigDecimal.ROUND_DOWN));
        BigDecimal estimatedAmt = subTotal.add(merchantTax).add(serviceFeeAmt).add(deliveryChargedBeforeDiscount).subtract(customerBalanceBeforeDiscount);
        checkOutDto.setEstimatedAmount(estimatedAmt.setScale(0, BigDecimal.ROUND_DOWN));
        return checkOutDto;
    }

    @Override
    public void saveOrder(RequestJsonDto requestJson, HeaderDto headerDto) throws Exception {
        Long customerId = requestJson.getOrdersCustomerId();
        Integer addressId = requestJson.getOrdersAddressId();
        CustomerEntity customer = customerDaoService.getCustomerIdAndRewardFromFacebookId(customerId);
        AddressEntity address = addressDaoService.getMyAddress(addressId);
        if(address == null)
            throw new YSException("VLD014");
        if (customer == null)
            throw new YSException("VLD011");
        OrderEntity order = new OrderEntity();
        order.setAddress(address);
        order.setCustomer(customer);

        order.setOrderVerificationStatus(false);
        order.setDeliveryStatus(DeliveryStatus.PENDING);
        order.setOrderStatus(JobOrderStatus.ORDER_PLACED);

        /* Transferring data of cart to order and calculating price */
        BigDecimal itemTotalCost = BigDecimal.ZERO;
        BigDecimal itemServiceAndVatCharge = BigDecimal.ZERO;
        List<CartEntity> cartEntities = cartDaoService.getMyCarts(customerId);
        List<ItemsOrderEntity> itemsOrder = new ArrayList<ItemsOrderEntity>();
        Integer brandId = null;
        for (CartEntity cart : cartEntities) {
            ItemsOrderEntity itemsOrderEntity = new ItemsOrderEntity();
            itemsOrderEntity.setOrder(order);
            itemsOrderEntity.setAvailabilityStatus(true);
            itemsOrderEntity.setQuantity(cart.getOrderQuantity());
            itemsOrderEntity.setCustomerNote(cart.getNote());
            itemsOrderEntity.setItem(cart.getItem());
            itemsOrderEntity.setServiceCharge(cart.getItem().getServiceCharge());
            itemsOrderEntity.setVat(cart.getItem().getVat());
            List<Integer> cartAttributes = cartAttributesDaoService.findCartAttributes(cart.getId());
            List<ItemsOrderAttributeEntity> itemsOrderAttributeEntities = new ArrayList<ItemsOrderAttributeEntity>();
            for(Integer attribute: cartAttributes){
                ItemsOrderAttributeEntity itemsOrderAttribute = new ItemsOrderAttributeEntity();
                itemsOrderAttribute.setItemOrder(itemsOrderEntity);
                ItemsAttributeEntity itemsAttribute = new ItemsAttributeEntity();
                itemsAttribute.setId(attribute);
                itemsOrderAttribute.setItemsAttribute(itemsAttribute);
                itemsOrderAttributeEntities.add(itemsOrderAttribute);
            }
            itemsOrderEntity.setItemOrderAttributes(itemsOrderAttributeEntities);
            BigDecimal attributePrice = cartAttributesDaoService.findAttributesPrice(cart.getId());
            BigDecimal itemPrice = BigDecimalUtil.calculateCost(cart.getOrderQuantity(), cart.getItem().getUnitPrice(), attributePrice);

            BigDecimal serviceCharge = BigDecimalUtil.percentageOf(itemPrice, cart.getItem().getServiceCharge());
            BigDecimal serviceAndVatCharge = serviceCharge.add(BigDecimalUtil.percentageOf(itemPrice.add(serviceCharge), cart.getItem().getVat()));
            /*Set for order total and total serviceVat*/
            itemServiceAndVatCharge = itemServiceAndVatCharge.add(serviceAndVatCharge);
            itemTotalCost = itemTotalCost.add(itemPrice);

            itemsOrderEntity.setItemTotal(itemPrice);
            itemsOrderEntity.setServiceAndVatCharge(serviceAndVatCharge);
            itemsOrder.add(itemsOrderEntity);
            brandId = cart.getStoresBrand().getId();
        }


        MerchantEntity merchant = merchantDaoService.getCommissionVatPartnerShipStatus(brandId);
        if(merchant == null){
            throw new YSException("MRC003");
        }

        order.setItemsOrder(itemsOrder);
        order.setTotalCost(itemTotalCost);
        order.setSurgeFactor(getSurgeFactor());
        order.setItemServiceAndVatCharge(itemServiceAndVatCharge);

        /* Listing Active stores of a store brand and finding shortest store */
        List<StoreEntity> stores = merchantDaoService.findActiveStoresByBrand(brandId);
        StoreEntity store = findNearestStoreFromCustomer(order, stores);
        order.setStore(store);
        order.setOrderName(store.getName()+" to "+ order.getAddress().getStreet());
        order.setOrderVerificationCode(GeneralUtil.generateMobileCode());
        //TODO Send code message to customer

        /* Finding delivery boys based on active status. */
        List<DeliveryBoyEntity> availableAndActiveDBoys = deliveryBoyDaoService.findAllCapableDeliveryBoys();
        /* Selects nearest delivery boys based on timing. */
        List<DeliveryBoySelectionEntity> deliveryBoySelectionEntities = calculateStoreToDeliveryBoyDistance(store, availableAndActiveDBoys, order);
        /* Selects delivery boys based on profit criteria. */
        List<DeliveryBoySelectionEntity> deliveryBoySelectionEntitiesWithProfit =  filterDBoyWithProfitCriteria(order, deliveryBoySelectionEntities, merchant);
        order.setDeliveryBoySelections(deliveryBoySelectionEntitiesWithProfit);
        customerDaoService.saveOrder(order);

        //TODO Filter delivery boys by profit criteria - Push Notifications
        Float timeInSeconds = Float.parseFloat(systemPropertyService.readPrefValue(PreferenceType.ORDER_REQUEST_TIMEOUT_IN_MIN)) * 60;
        Integer timeOut = timeInSeconds.intValue();
        scheduleChanger.scheduleTask(DateUtil.findDelayDifference(DateUtil.getCurrentTimestampSQL(), timeOut));
    }

    /*
    * This method returns nearest store from customer, i.e Order Address Among list of stores.
    * */
    private StoreEntity findNearestStoreFromCustomer(OrderEntity order, List<StoreEntity> stores) throws Exception {
        DistanceType distanceType = DistanceType.fromInt(Integer.parseInt(systemPropertyService.readPrefValue(PreferenceType.AIR_OR_ACTUAL_DISTANCE_SWITCH)));
        String orderAddress[] = {GeoCodingUtil.getLatLong(order.getAddress().getLatitude(), order.getAddress().getLongitude())};
        String storeAddress[] = new String[stores.size()];
        for (int i = 0; i < stores.size(); i++) {
            storeAddress[i] = GeoCodingUtil.getLatLong(stores.get(i).getLatitude(), stores.get(i).getLongitude());
        }
        StoreEntity nearestStore = null;
        BigDecimal actualDistance = BigDecimal.ZERO;
        List<BigDecimal> distanceList = new ArrayList<BigDecimal>();
        if(distanceType.equals(DistanceType.AIR_DISTANCE)){
            distanceList =  GeoCodingUtil.getListOfAssumedDistance(orderAddress[0], storeAddress);
            int minimumIndex = distanceList.indexOf(Collections.min(distanceList));
            nearestStore = stores.get(minimumIndex);
            actualDistance = Collections.min(distanceList);
        }else{
            distanceList = GeoCodingUtil.getListOfAirDistances(orderAddress, storeAddress);
            int minimumIndex = distanceList.indexOf(Collections.min(distanceList));
            nearestStore = stores.get(minimumIndex);
            String finalStore[] = {GeoCodingUtil.getLatLong(nearestStore.getLatitude(), nearestStore.getLongitude())};
            actualDistance =  GeoCodingUtil.getListOfDistances(orderAddress, finalStore).get(0);
        }
        order.setCustomerChargeableDistance(BigDecimalUtil.getDistanceInKiloMeters(actualDistance));
        return nearestStore;
    }

    /*Calculate distance of delivery boy to store and select only delivery boys satisfying timing condition*/
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

        DistanceType distanceType = DistanceType.fromInt(Integer.parseInt(systemPropertyService.readPrefValue(PreferenceType.AIR_OR_ACTUAL_DISTANCE_SWITCH)));
        List<BigDecimal> distanceList = new ArrayList<BigDecimal>();
        if(distanceType.equals(DistanceType.AIR_DISTANCE)){
            distanceList = GeoCodingUtil.getListOfAssumedDistance(storeAddress[0], deliveryBoyAddress);
        }else{
            distanceList = GeoCodingUtil.getListOfDistances(storeAddress, deliveryBoyAddress);
        }

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
            log.info("Delivery boys selected from distance calculation:"+selectionDetails.toString());
        }
        return filterDeliveryBoySelection(selectionDetails, timeDetails);
    }

    /* Select only delivery boys satisfying timing condition from list of selected delivery boys from distance criteria*/
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
            CourierTransactionEntity courierTransaction = systemAlgorithmService.getCourierTransaction(order, deliveryBoySelectionEntity, merchant.getCommissionPercentage(), merchant.getServiceFee());
            if (BigDecimalUtil.isGreaterThen(courierTransaction.getProfit(),BigDecimal.ZERO))
                filteredDeliveryBoys.add(deliveryBoySelectionEntity);
        }
        log.info("Filtered Dboys:"+filteredDeliveryBoys.toString());
        return filteredDeliveryBoys;
    }

    private Integer getSurgeFactor() throws Exception{
        Integer surgeFactor = 0;
        String currentTime = DateUtil.getCurrentTime().toString();
        if(DateUtil.isTimeBetweenTwoTime("07:00:00", "21:00:00", currentTime))
            surgeFactor = Integer.parseInt(systemPropertyService.readPrefValue(PreferenceType.SURGE_FACTOR_7AM_9PM)); // if Time is 7 AM-9 PM
        else if(DateUtil.isTimeBetweenTwoTime("21:00:00", "22:00:00", currentTime))
            surgeFactor = Integer.parseInt(systemPropertyService.readPrefValue(PreferenceType.SURGE_FACTOR_9_10PM)); // if Time is 9-10 PM
        else if(DateUtil.isTimeBetweenTwoTime("22:00:00", "23:00:00", currentTime))
            surgeFactor = Integer.parseInt(systemPropertyService.readPrefValue(PreferenceType.SURGE_FACTOR_10_11PM)); // if Time is 10 PM-11 PM
        else
            surgeFactor = Integer.parseInt(systemPropertyService.readPrefValue(PreferenceType.SURGE_FACTOR_11PM_7AM)); // if Time is 11 PM-7 AM

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
