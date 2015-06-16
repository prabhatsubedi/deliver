package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.abs.AbstractManager;
import com.yetistep.delivr.dao.inf.*;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.PaginationDto;
import com.yetistep.delivr.dto.PaymentGatewayDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.enums.*;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.model.mobile.AddressDto;
import com.yetistep.delivr.model.mobile.PageInfo;
import com.yetistep.delivr.model.mobile.StaticPagination;
import com.yetistep.delivr.model.mobile.dto.CheckOutDto;
import com.yetistep.delivr.model.mobile.dto.MyOrderDto;
import com.yetistep.delivr.model.mobile.dto.SearchDto;
import com.yetistep.delivr.model.mobile.dto.TrackOrderDto;
import com.yetistep.delivr.service.inf.CustomerService;
import com.yetistep.delivr.service.inf.SystemAlgorithmService;
import com.yetistep.delivr.service.inf.SystemPropertyService;
import com.yetistep.delivr.util.*;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomerServiceImpl extends AbstractManager implements CustomerService {
    private static final Logger log = Logger.getLogger(CustomerServiceImpl.class);
    private static final Integer ON_TIME_DELIVERY = 0;
    private static final Integer DELAYED_DELIVERY = 1;
    private static final BigDecimal minusOne = new BigDecimal(-1);
    private static final String SPLITTER = " > ";


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
    private CategoryDaoService categoryDaoService;

    @Autowired
    BrandsCategoryDaoService brandsCategoryDaoService;

    @Autowired
    ItemDaoService itemDaoService;

    @Autowired
    StoreDaoService storeDaoService;

    @Autowired
    RatingDaoService ratingDaoService;

    @Autowired
    ReasonDetailsDaoService reasonDetailsDaoService;

    @Autowired
    DeliveryBoySelectionDaoService deliveryBoySelectionDaoService;

    @Autowired
    DBoyOrderHistoryDaoService dBoyOrderHistoryDaoService;

    @Autowired
    ValidateMobileDaoService validateMobileDaoService;

    @Autowired
    WalletTransactionDaoService walletTransactionDaoService;


    @Autowired
    CartCustomItemDaoService cartCustomItemDaoService;

    @Autowired
    PaymentGatewayInfoDaoService paymentGatewayInfoDaoService;

    @Override
    public void login(CustomerEntity customerEntity) throws Exception {
        log.info("++++++++++++++ Logging Customer ++++++++++++++++");

//        UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();
//        ReadableUserAgent agent = parser.parse(httpServletRequest.getHeader("User-Agent"));
        String userAgent = httpServletRequest.getHeader("User-Agent");
        UserAgent ua = UserAgent.parseUserAgentString(userAgent);

        String family = ua.getOperatingSystem().name();

        customerEntity.getUser().getUserDevice().setFamily(family);
        customerEntity.getUser().getUserDevice().setFamilyName(family);
        customerEntity.getUser().getUserDevice().setName(family);

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

            //Update Lat and Long
            if((customerEntity.getLatitude()!=null && !customerEntity.getLatitude().isEmpty()) && (customerEntity.getLongitude()!=null && !customerEntity.getLongitude().isEmpty())) {
                registeredCustomer.setLatitude(customerEntity.getLatitude());
                registeredCustomer.setLongitude(customerEntity.getLongitude());
            }

             log.info("++++++++++++ Updating Customer Info +++++++++++++");
            //Update User Device
            if(registeredCustomer.getUser().getUserDevice() != null){
                registeredCustomer.getUser().getUserDevice().setUser(registeredCustomer.getUser());
                registeredCustomer.getUser().getUserDevice().setUuid(customerEntity.getUser().getUserDevice().getUuid());
    //            registeredCustomer.getUser().getUserDevice().setDeviceToken(customerEntity.getUser().getUserDevice().getDeviceToken());
                registeredCustomer.getUser().getUserDevice().setFamily(customerEntity.getUser().getUserDevice().getFamily());
                registeredCustomer.getUser().getUserDevice().setFamilyName(customerEntity.getUser().getUserDevice().getFamilyName());
                registeredCustomer.getUser().getUserDevice().setName(customerEntity.getUser().getUserDevice().getName());
                registeredCustomer.getUser().getUserDevice().setBrand(customerEntity.getUser().getUserDevice().getBrand());
                registeredCustomer.getUser().getUserDevice().setModel(customerEntity.getUser().getUserDevice().getModel());
                registeredCustomer.getUser().getUserDevice().setDpi(customerEntity.getUser().getUserDevice().getDpi());
                registeredCustomer.getUser().getUserDevice().setHeight(customerEntity.getUser().getUserDevice().getHeight());
                registeredCustomer.getUser().getUserDevice().setWidth(customerEntity.getUser().getUserDevice().getWidth());
            }else{
                registeredCustomer.getUser().setUserDevice(customerEntity.getUser().getUserDevice());
            }
            /*
            * if current login is the first login of the current customer
            * set reward for the customer
            * */
            if(registeredCustomer.getUser().getLastActivityDate() == null) {
                if(registeredCustomer.getReferredBy() != null){
                    registeredCustomer.setRewardsEarned(new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.REFEREE_REWARD_AMOUNT)));
                    refillCustomerWallet(registeredCustomer.getFacebookId(), new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.REFEREE_REWARD_AMOUNT)), MessageBundle.getMessage("WTM010", "push_notification.properties"), false);
                }
            }

            registeredCustomer.getUser().setLastActivityDate(MessageBundle.getCurrentTimestampSQL());

            validateUserDevice(registeredCustomer.getUser().getUserDevice());
            registeredCustomer.setDefault(false);
            customerDaoService.update(registeredCustomer);

        } else {
           if(systemPropertyService.readPrefValue(PreferenceType.ENABLE_FREE_REGISTER).equals("1")){
               /* Check User Email */
               if(customerEntity.getUser().getEmailAddress()!=null && !customerEntity.getUser().getEmailAddress().isEmpty()) {
                   if(userDaoService.checkIfEmailExists(customerEntity.getUser().getEmailAddress(), Role.ROLE_CUSTOMER.toInt()))
                       throw new YSException("VLD026");
               }

                validateUserDevice(customerEntity.getUser().getUserDevice());
                customerEntity.setRewardsEarned(BigDecimal.ZERO);
                customerEntity.setTotalOrderPlaced(0);
                customerEntity.setTotalOrderDelivered(0);

                RoleEntity userRole = userDaoService.getRoleByRole(Role.ROLE_CUSTOMER);
                customerEntity.getUser().setRole(userRole);
                customerEntity.getUser().getUserDevice().setUser(customerEntity.getUser());
                customerEntity.getUser().setCreatedDate(DateUtil.getCurrentTimestampSQL());
                customerEntity.getUser().setLastActivityDate(null);
    //            if (customerEntity.getLatitude() != null) {
    //                customerEntity.getUser().getAddresses().get(0).setLatitude(customerEntity.getLatitude());
    //                customerEntity.getUser().getAddresses().get(0).setLongitude(customerEntity.getLongitude());
    //            }
                //if(registeredCustomer.getUser().getLastActivityDate().equals(null)) {
                customerEntity.setRewardsEarned(new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.NORMAL_USER_BONUS_AMOUNT)));
               // }
               customerEntity.setDefault(false);
               customerDaoService.save(customerEntity);
               customerEntity.setWalletAmount(new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.NORMAL_USER_BONUS_AMOUNT)));
               WalletTransactionEntity walletTransactionEntity = new WalletTransactionEntity();
               walletTransactionEntity.setTransactionDate(DateUtil.getCurrentTimestampSQL());
               walletTransactionEntity.setAccountType(AccountType.CREDIT);
               String remark = MessageBundle.getMessage("WTM009", "push_notification.properties");
               walletTransactionEntity.setRemarks(remark);
               walletTransactionEntity.setTransactionAmount(new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.NORMAL_USER_BONUS_AMOUNT)));
               walletTransactionEntity.setCustomer(customerEntity);
               walletTransactionEntity.setPaymentMode(PaymentMode.WALLET);
               walletTransactionEntity.setAvailableWalletAmount(BigDecimalUtil.checkNull(customerEntity.getWalletAmount()));
               systemAlgorithmService.encodeWalletTransaction(walletTransactionEntity);
               customerEntity.setWalletTransactions(Collections.singletonList(walletTransactionEntity));
               customerEntity.setDefault(false);
               customerDaoService.update(customerEntity);
            } else {
                throw new YSException("SEC014");
           }
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

        if(user.getLastAddressMobile().length() !=10)
            throw new YSException("VLD004");

        /* Check Customer Existence */
        CustomerEntity customerEntity = customerDaoService.findUser(user.getCustomer().getFacebookId());
        if (customerEntity == null)
            throw new YSException("VLD011");

        ValidateMobileEntity validateMobileEntity = validateMobileDaoService.getMobileCode(customerEntity.getUser().getId(), user.getLastAddressMobile());
        if(validateMobileEntity == null)
              throw new YSException("SEC010");

        /*once sms is enable remove this line of code*/
        if(validateMobileEntity.getVerificationCode()!=null && !validateMobileEntity.getVerificationCode().equals(user.getVerificationCode()))
              throw new YSException("SEC011");


        /* Check Valid Mobile and Mobile Code */
//        if (user.getLastAddressMobile().equals(customerEntity.getUser().getLastAddressMobile())) {
//            if (user.getVerificationCode() != null) {
//                if (!user.getVerificationCode().equals(customerEntity.getUser().getVerificationCode()))
//                    throw new YSException("SEC011");
//            } else {
//                String mobCode = addressDaoService.getMobileCode(customerEntity.getUser().getId(), user.getLastAddressMobile());
//                if (mobCode == null)
//                    throw new YSException("SEC010");
//                else
//                    user.setVerificationCode(mobCode);
//            }
//
//        } else {
//            String mobCode = addressDaoService.getMobileCode(customerEntity.getUser().getId(), user.getLastAddressMobile());
//            if (mobCode == null)
//                throw new YSException("SEC010");
//            else
//                user.setVerificationCode(mobCode);
//        }
        //Update Customer
        if(address.getLatitude()!=null && address.getLongitude()!=null){
            customerDaoService.updateLatLong(address.getLatitude(), address.getLongitude(), user.getCustomer().getFacebookId());
        }

        //Now Update Delivery Address
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

        // Now Update Validate Mobile after successfully saved the database
        validateMobileDaoService.updateVerifiedByUser(validateMobileEntity.getId());

        return address.getId();
    }

    @Override
    public AddressDto verifyMobile(String mobile, Long facebookId) throws Exception {
        log.info("++++++++ Verifying mobile " + mobile + " +++++++++++");

        CustomerEntity customerEntity  = customerDaoService.findUser(facebookId);
        if(customerEntity == null)
            throw new Exception("VLD011");

        if(mobile.length()!=10)
            throw new YSException("VLD004");

        ValidateMobileEntity validMobile = validateMobileDaoService.getMobileCode(customerEntity.getUser().getId(), mobile);

        String verificationCode = null;
        //once sms is enabled set validatedByUser false by default and uncomment the related lines of code
        Boolean validatedByUser = false;

        String countryCode = systemPropertyService.readPrefValue(PreferenceType.SMS_COUNTRY_CODE);

        if(validMobile == null) {
            log.debug("++++++ Updating Mobile No and Validation Code");


            verificationCode = GeneralUtil.generateMobileCode();

            SMSUtil.sendSMS(CommonConstants.SMS_PRE_TEXT + verificationCode + ".", mobile, countryCode, systemPropertyService.readPrefValue(PreferenceType.SMS_PROVIDER));

            ValidateMobileEntity validateMobileEntity = new ValidateMobileEntity();
            validateMobileEntity.setMobileNo(mobile);
            validateMobileEntity.setVerificationCode(verificationCode);
            UserEntity userEntity = new UserEntity();
            userEntity.setId(customerEntity.getUser().getId());
            validateMobileEntity.setUser(userEntity);
            validateMobileEntity.setTotalSmsSend(1);
            //Now save mobile and other detail
            validateMobileDaoService.save(validateMobileEntity);
            //userDaoService.updateDeliveryContact(customerEntity.getUser().getId(), mobile, verificationCode);

        } else {

            if(validMobile.getVerifiedByUser() == null) {

                if(validMobile.getTotalSmsSend()!=null && validMobile.getTotalSmsSend() > 2)
                    throw new YSException("SEC012", "#" + systemPropertyService.readPrefValue(PreferenceType.HELPLINE_NUMBER));

                //Send SMS Only
                verificationCode = String.valueOf(validMobile.getVerificationCode());

                SMSUtil.sendSMS(CommonConstants.SMS_PRE_TEXT + verificationCode + ".", mobile, countryCode, systemPropertyService.readPrefValue(PreferenceType.SMS_PROVIDER));

                validateMobileDaoService.updateNoOfSMSSend(validMobile.getId());

                //
                validatedByUser = false;

            } else {
                if(validMobile.getVerificationCode() != null)  {
                    verificationCode = String.valueOf(validMobile.getVerificationCode());
                }
                validatedByUser = validMobile.getVerifiedByUser()!=null ? validMobile.getVerifiedByUser() : false;
            }

        }

        AddressDto address = new AddressDto();
        address.setVerificationCode(verificationCode);
        address.setMobileValidate(true);
        address.setValidatedByUser(validatedByUser);

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
        List<CartCustomItemEntity> cartCustomItems = new ArrayList<>();

        List<CartEntity> carts = cartDaoService.getMyCarts(facebookId);
        //get custom item carts
        List<CartEntity> customItemCarts = cartDaoService.getMyCustomItemCarts(facebookId);
        //merge both carts
        carts.addAll(customItemCarts);

        if(carts==null || carts.size() == 0)
            throw new YSException("CRT001");

        AddressEntity address = addressDaoService.getMyAddress(addressId);

        Integer brandId = carts.get(0).getStoresBrand().getId();
        BigDecimal subTotal = BigDecimal.ZERO;
        BigDecimal merchantTax = BigDecimal.ZERO;
        BigDecimal itemServiceCharge = BigDecimal.ZERO;
        BigDecimal itemVatCharge = BigDecimal.ZERO;
        for(CartEntity cart : carts){
            if(cart.getItem() != null){
                ItemEntity item = merchantDaoService.getItemDetail(cart.getItem().getId());

                //Calculating Sub Total
                BigDecimal attributePrice = cartAttributesDaoService.findAttributesPrice(cart.getId());

                BigDecimal total = BigDecimalUtil.calculateCost(cart.getOrderQuantity(), item.getUnitPrice(), attributePrice);

                BigDecimal itemTax = BigDecimal.ZERO;

                if(item.getServiceCharge()!=null && BigDecimalUtil.isGreaterThenZero(item.getServiceCharge())){
                    itemTax =  BigDecimalUtil.percentageOf(total, item.getServiceCharge());
                    itemServiceCharge = itemServiceCharge.add(itemTax);
                }

                if(item.getVat()!=null && BigDecimalUtil.isGreaterThenZero(item.getVat())){
                    itemVatCharge = itemVatCharge.add(BigDecimalUtil.percentageOf(total.add(itemTax), item.getVat()));
                    itemTax = itemTax.add(BigDecimalUtil.percentageOf(total.add(itemTax), item.getVat()));
                }

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
                else
                    tempItem.setImageUrl(systemPropertyService.readPrefValue(PreferenceType.DEFAULT_IMG_ITEM));

                items.add(tempItem);
            } else if(cart.getCartCustomItem() != null){
                 cart.getCartCustomItem().setCart(cart);
                 cartCustomItems.add(cart.getCartCustomItem());
            }
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

        //BigDecimal customerBalanceBeforeDiscount = customerDaoService.getRewardsPoint(facebookId);
        /* Set this to zero since no discount in this phase */
        BigDecimal customerBalanceBeforeDiscount = BigDecimal.ZERO;

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
        deliveryChargedBeforeDiscount = deliveryChargedBeforeDiscount.subtract(BigDecimalUtil.percentageOf(deliveryChargedBeforeDiscount, BigDecimalUtil.checkNull(new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DISCOUNT_ON_DELIVERY_FEE)))));


        /* Item Detail and Others */
        StoresBrandEntity storeBrand = new StoresBrandEntity();
        storeBrand.setId(brandId);
        storeBrand.setBrandName(carts.get(0).getStoresBrand().getBrandName());
        storeBrand.setBrandLogo(carts.get(0).getStoresBrand().getBrandLogo());

        //add custom items to the item
        List<ItemEntity> customItems = new ArrayList<>();
        if(cartCustomItems.size() > 0){
            for (CartCustomItemEntity cartCustomItem: cartCustomItems){
                ItemEntity item = new ItemEntity();
                item.setId(cartCustomItem.getId());
                item.setName(cartCustomItem.getName());
                item.setImageUrl(systemPropertyService.readPrefValue(PreferenceType.DEFAULT_IMG_ITEM));
                item.setUnitPrice(minusOne);
                item.setOrderQuantity(cartCustomItem.getCart().getOrderQuantity());
                item.setIsCustomItem(Boolean.TRUE);
                customItems.add(item);
            }
        }

        //set none custom item to use later
        //List<ItemEntity> defaultItems = items;

        items.addAll(customItems);

        checkOutDto.setStoresBrand(storeBrand);
        checkOutDto.setItems(items);
        //checkOutDto.setCartCustomItems(cartCustomItems);


        if(cartCustomItems.size()==items.size()){
            checkOutDto.setSubTotal(minusOne);
        }else{
            checkOutDto.setSubTotal(subTotal.setScale(2, BigDecimal.ROUND_DOWN));
        }

        if(cartCustomItems.size()>0){
            checkOutDto.setTax(minusOne);
            checkOutDto.setServiceFee(minusOne);
            checkOutDto.setDeliveryFee(minusOne);
            checkOutDto.setItemServiceCharge(minusOne);
            checkOutDto.setItemVatCharge(minusOne);
        }else{
            merchantTax = merchantTax.setScale(2, BigDecimal.ROUND_DOWN);
            serviceFeeAmt =  serviceFeeAmt.setScale(2, BigDecimal.ROUND_DOWN);
            deliveryChargedBeforeDiscount = deliveryChargedBeforeDiscount.setScale(2, BigDecimal.ROUND_DOWN);
            itemServiceCharge = itemServiceCharge.setScale(2, BigDecimal.ROUND_DOWN);
            itemVatCharge = itemVatCharge.setScale(2, BigDecimal.ROUND_DOWN);
            checkOutDto.setTax(merchantTax);
            checkOutDto.setServiceFee(serviceFeeAmt);
            checkOutDto.setDeliveryFee(deliveryChargedBeforeDiscount);
            checkOutDto.setItemServiceCharge(itemServiceCharge);
            checkOutDto.setItemVatCharge(itemVatCharge);
        }

        if(BigDecimalUtil.isZero(checkOutDto.getDeliveryFee())) {
            customerBalanceBeforeDiscount = BigDecimal.ZERO;
        }

        if(BigDecimalUtil.isLessThenOrEqualTo(checkOutDto.getDeliveryFee(), customerBalanceBeforeDiscount)){
            customerBalanceBeforeDiscount =  checkOutDto.getDeliveryFee();
        }

        checkOutDto.setDiscount(customerBalanceBeforeDiscount.setScale(2, BigDecimal.ROUND_DOWN));
        if(cartCustomItems.size()>0){
            checkOutDto.setEstimatedAmount(minusOne);
        }   else {
            BigDecimal estimatedAmt = subTotal.add(merchantTax).add(serviceFeeAmt).add(deliveryChargedBeforeDiscount).subtract(customerBalanceBeforeDiscount);
            checkOutDto.setEstimatedAmount(estimatedAmt.setScale(2, BigDecimal.ROUND_DOWN));
        }
        checkOutDto.setCurrency(systemPropertyService.readPrefValue(PreferenceType.CURRENCY));
        checkOutDto.setMaxOrderLimit(new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.ORDER_MAX_AMOUNT)));
        checkOutDto.setWalletAmount(BigDecimalUtil.checkNull(this.getCustomerWalletBalance(facebookId)));
        return checkOutDto;
    }

    @Override
    public List<CategoryEntity> getDefaultCategories() throws Exception {
        log.info("+++++++++++++++ Getting Default Categories ++++++++++++");

        List<CategoryEntity> categories = new ArrayList<>();
        categories = categoryDaoService.findDefaultCategories();
        for(CategoryEntity categoryEntity : categories){
            if(categoryEntity.getImageUrl()==null)
                categoryEntity.setImageUrl(systemPropertyService.readPrefValue(PreferenceType.DEFAULT_IMG_CATEGORY));
        }
        return categories;
    }

    @Override
    public Map<String, Object> getCategoryBrands(Integer categoryId, Integer pageNo, String lat, String lon) throws Exception {
        log.info("++++++++++++++++++ Getting Category's Brands of Id: " + categoryId + " ++++++++++++++");

        List<StoresBrandEntity> resultBrands = new ArrayList<>();
        List<StoresBrandEntity> featuredBrands = new ArrayList<>();

        // Unique Brands from brand category
        List<StoresBrandEntity> brands = brandsCategoryDaoService.getCategoryBrands(categoryId);

        List<Integer> featuredBrandsId = new ArrayList<>();
        List<Integer> priorityBrandsId  = new ArrayList<>();
        List<Integer> otherBrandsId = new ArrayList<>();

        //Now Get Detail of Brand
        if(brands!=null && brands.size()>0){
            for(StoresBrandEntity brand : brands){
                if(brand.getFeatured()!=null && brand.getFeatured())
                    featuredBrandsId.add(brand.getId());
                else if(brand.getPriority()!=null && brand.getPriority() > 0)
                    priorityBrandsId.add(brand.getId());
                else
                    otherBrandsId.add(brand.getId());
            }
        }

        //Now Get Information of that
        if(featuredBrandsId.size() > 0) {
            Integer[] ids = new Integer[featuredBrandsId.size()];
            ids = featuredBrandsId.toArray(ids);
            featuredBrands = storesBrandDaoService.findStoresBrand(false, ids);
        }

        if(priorityBrandsId.size() > 0){
            Integer[] ids = new Integer[priorityBrandsId.size()];
            ids = priorityBrandsId.toArray(ids);
            resultBrands = storesBrandDaoService.findStoresBrand(true, ids);
        }

        if(otherBrandsId.size() > 0){
            Integer[] ids = new Integer[otherBrandsId.size()];
            ids = otherBrandsId.toArray(ids);
            List<StoresBrandEntity> tempBrands = storesBrandDaoService.findStoresBrand(false, ids);
            resultBrands.addAll(tempBrands);
        }

        //Now Implement Nearest Location To Of store

        for(StoresBrandEntity featureBrand : featuredBrands) {

                List<StoreEntity> storeEntities = storeDaoService.findStores(featureBrand.getId());
            if((lat!=null && !lat.isEmpty()) && (lon!=null && !lon.isEmpty()))
                sortStoreByLocation(lat, lon, storeEntities);

            if(storeEntities.get(0).getStreet()!=null && !storeEntities.get(0).getStreet().isEmpty())
                featureBrand.setNearestStoreLocation(storeEntities.get(0).getStreet());
            else
                featureBrand.setNearestStoreLocation(CommonConstants.UNKNOWN_LOCATION);


        }

        //For All Brands
        for(StoresBrandEntity resultBrand : resultBrands) {

                List<StoreEntity> storeEntities = storeDaoService.findStores(resultBrand.getId());
            if((lat!=null && !lat.isEmpty()) && (lon!=null && !lon.isEmpty()))
                sortStoreByLocation(lat, lon, storeEntities);

            if(storeEntities.get(0).getStreet()!=null && !storeEntities.get(0).getStreet().isEmpty())
                resultBrand.setNearestStoreLocation(storeEntities.get(0).getStreet());
            else
                resultBrand.setNearestStoreLocation(CommonConstants.UNKNOWN_LOCATION);


        }

        // Perform sorted store pagination
        PageInfo pageInfo = null;
        List<StoresBrandEntity> sortedList = new ArrayList<>();
        if (resultBrands.size() > 0) {
            StaticPagination staticPagination = new StaticPagination();
            staticPagination.paginate(resultBrands, pageNo);
            sortedList = (List<StoresBrandEntity>) staticPagination.getList();
            staticPagination.setList(null);
            pageInfo = staticPagination;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("featured", featuredBrands);
        map.put("page", pageInfo);
        map.put("all", sortedList);
        map.put("currency", systemPropertyService.readPrefValue(PreferenceType.CURRENCY));
        return map;
    }

    private void sortStoreByLocation(String lat, String lon, List<StoreEntity> storeEntities) throws Exception {
        String[] storeDistance = new String[storeEntities.size()];
        String[] customerDistance = {GeoCodingUtil.getLatLong(lat, lon)};

        for (int i = 0; i < storeEntities.size(); i++) {
            storeDistance[i] = GeoCodingUtil.getLatLong(storeEntities.get(i).getLatitude(), storeEntities.get(i).getLongitude());
        }

        List<BigDecimal> distanceList = GeoCodingUtil.getListOfAirDistances(customerDistance, storeDistance);

        //Set Distance to Store Entity
        for (int i = 0; i < storeEntities.size(); i++) {
            storeEntities.get(i).setCustomerToStoreDistance(distanceList.get(i));
        }

        //Store Entity List Sorted by Distance
        Collections.sort(storeEntities, new StoreDistanceComparator());
    }

    @Override
    public void saveOrder(RequestJsonDto requestJson, HeaderDto headerDto) throws Exception {
        Long customerId = requestJson.getOrdersCustomerId();
        Integer addressId = requestJson.getOrdersAddressId();
        PaymentMode paymentMode = PaymentMode.CASH_ON_DELIVERY;
        if(requestJson.getPaymentMode() != null){
            paymentMode = requestJson.getPaymentMode();
        }

        CustomerEntity customer = customerDaoService.find(customerId);
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
        order.setReprocessTime(0);
        order.setOrderDate(DateUtil.getCurrentTimestampSQL());

        /* Transferring data of cart to order and calculating price */
        BigDecimal itemTotalCost = BigDecimal.ZERO;
        BigDecimal itemServiceAndVatCharge = BigDecimal.ZERO;
        BigDecimal itemServiceCharge = BigDecimal.ZERO;
        BigDecimal itemVatCharge = BigDecimal.ZERO;


        List<CartEntity> cartEntities = cartDaoService.getMyCartsWithCategories(customerId);
        //get custom item carts
        List<CartEntity> customItemCarts = cartDaoService.getMyCustomItemCarts(customerId);
        //merge both carts
        cartEntities.addAll(customItemCarts);

        List<Integer> cartIds = new ArrayList<Integer>();
        List<ItemsOrderEntity> itemsOrder = new ArrayList<ItemsOrderEntity>();
        Integer brandId = null;
        StoresBrandEntity storesBrand = null;
        for (CartEntity cart : cartEntities) {
            ItemsOrderEntity itemsOrderEntity = new ItemsOrderEntity();
            itemsOrderEntity.setOrder(order);
            itemsOrderEntity.setAvailabilityStatus(true);
            itemsOrderEntity.setQuantity(cart.getOrderQuantity());
            if(cart.getNote() != null && cart.getNote() != "")
                itemsOrderEntity.setCustomerNote(cart.getNote());

            itemsOrderEntity.setItem(cart.getItem());
            if(cart.getItem() == null){
                CustomItemEntity customItem = new CustomItemEntity();
                customItem.setName(cart.getCartCustomItem().getName());
                if(cart.getCartCustomItem().getEditedName() != null && cart.getCartCustomItem().getEditedName() != "")
                    customItem.setEditedName(cart.getCartCustomItem().getEditedName());

                customItem.setItemsOrder(itemsOrderEntity);
                customItem.setCustomerCustom(Boolean.TRUE);
                itemsOrderEntity.setCustomItem(customItem);
            }

            //if cart doesn't have custom item then work for attributes
            BigDecimal itemPrice;
            if(cart.getItem() != null){
                itemsOrderEntity.setServiceCharge(BigDecimalUtil.checkNull(cart.getItem().getServiceCharge()));
                itemsOrderEntity.setVat(BigDecimalUtil.checkNull(cart.getItem().getVat()));
                CategoryEntity categoryEntity = cart.getItem().getCategory();
                String categoryName = categoryEntity.getName();
                categoryEntity = categoryEntity.getParent();
                while(categoryEntity != null){
                    categoryName =  categoryEntity.getName() + SPLITTER + categoryName;
                    categoryEntity = categoryEntity.getParent();
                }
                itemsOrderEntity.setCategoryName(categoryName);

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
                itemPrice = BigDecimalUtil.calculateCost(cart.getOrderQuantity(), cart.getItem().getUnitPrice(), attributePrice);

                BigDecimal serviceCharge = BigDecimalUtil.percentageOf(itemPrice, BigDecimalUtil.checkNull(cart.getItem().getServiceCharge()));
                BigDecimal serviceAndVatCharge = serviceCharge.add(BigDecimalUtil.percentageOf(itemPrice.add(serviceCharge), BigDecimalUtil.checkNull(cart.getItem().getVat())));
                /*Set for order total and total serviceVat*/

                itemServiceCharge = itemServiceCharge.add(serviceCharge);
                itemVatCharge = itemVatCharge.add(BigDecimalUtil.percentageOf(itemPrice.add(serviceCharge), BigDecimalUtil.checkNull(cart.getItem().getVat())));
                
                itemServiceAndVatCharge = itemServiceAndVatCharge.add(serviceAndVatCharge);
                itemsOrderEntity.setServiceAndVatCharge(serviceAndVatCharge);

                itemTotalCost = itemTotalCost.add(itemPrice);

                itemsOrderEntity.setItemTotal(itemPrice);
            }

            /*BigDecimal serviceCharge = BigDecimalUtil.percentageOf(itemPrice, BigDecimalUtil.checkNull(cart.getItem().getServiceCharge()));
            BigDecimal serviceAndVatCharge = serviceCharge.add(BigDecimalUtil.percentageOf(itemPrice.add(serviceCharge), BigDecimalUtil.checkNull(cart.getItem().getVat())));
            /*Set for order total and total serviceVat*/
            //itemServiceAndVatCharge = itemServiceAndVatCharge.add(serviceAndVatCharge);
            //itemsOrderEntity.setServiceAndVatCharge(serviceAndVatCharge);*/
            /*itemTotalCost = itemTotalCost.add(itemPrice);

            itemsOrderEntity.setItemTotal(itemPrice);*/
            itemsOrder.add(itemsOrderEntity);
            brandId = cart.getStoresBrand().getId();
            storesBrand = cart.getStoresBrand();
            cartIds.add(cart.getId());
        }


        MerchantEntity merchant = merchantDaoService.getCommissionVatPartnerShipStatus(brandId);
        if(merchant == null){
            throw new YSException("MRC003");
        }

        Boolean isOpen = DateUtil.isTimeBetweenTwoTime(storesBrand.getOpeningTime().toString(), storesBrand.getClosingTime().toString(),DateUtil.getCurrentTime().toString());
        if(!isOpen){
            throw new YSException("CRT003");
        }

        order.setItemsOrder(itemsOrder);
        order.setTotalCost(itemTotalCost.setScale(2, BigDecimal.ROUND_DOWN));
        order.setDiscountFromStore(BigDecimal.ZERO);
        order.setSurgeFactor(getSurgeFactor());
        order.setItemServiceAndVatCharge(itemServiceAndVatCharge.setScale(2, BigDecimal.ROUND_DOWN));
        order.setItemServiceCharge(itemServiceCharge.setScale(2, BigDecimal.ROUND_DOWN));
        order.setItemVatCharge(itemVatCharge.setScale(2, BigDecimal.ROUND_DOWN));

        /* Listing Active stores of a store brand and finding shortest store */
        List<StoreEntity> stores = merchantDaoService.findActiveStoresByBrand(brandId);
        if(stores.size() == 0){
            throw new YSException("VLD024");
        }
        StoreEntity store = findNearestStoreFromCustomer(order, stores);
        BigDecimal maxDistance = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.MAX_ORDER_SERVING_DISTANCE));
        if(BigDecimalUtil.isGreaterThen(order.getCustomerChargeableDistance(), maxDistance)){
            throw new YSException("VLD036", maxDistance + "km");
        }
        order.setStore(store);
        order.setOrderName(store.getName() + " to " + order.getAddress().getStreet());
        order.setOrderVerificationCode(GeneralUtil.generateMobileCode());

        /* Finding delivery boys based on active status. */
        int timeInMin = Integer.parseInt(systemPropertyService.readPrefValue(PreferenceType.LOCATION_UPDATE_TIMEOUT_IN_MIN));
        List<DeliveryBoyEntity> availableAndActiveDBoys = deliveryBoyDaoService.findAllCapableDeliveryBoys(timeInMin);
        if(availableAndActiveDBoys.size() == 0){
            log.info("No shoppers available");
           throw new YSException("ORD012");
        }
        /* Selects nearest delivery boys based on timing. */
        List<DeliveryBoySelectionEntity> deliveryBoySelectionEntities = calculateStoreToDeliveryBoyDistance(store, availableAndActiveDBoys, order, false);
        /* Selects delivery boys based on profit criteria. */
        List<DeliveryBoySelectionEntity> deliveryBoySelectionEntitiesWithProfit =  filterDBoyWithProfitCriteria(order, deliveryBoySelectionEntities, merchant.getCommissionPercentage(), merchant.getServiceFee());
        if(deliveryBoySelectionEntitiesWithProfit.size() == 0){
            throw new YSException("ORD012");
        }
        order.setDeliveryBoySelections(deliveryBoySelectionEntitiesWithProfit);
        CourierTransactionEntity courierTransaction = systemAlgorithmService.getCourierTransaction(order, deliveryBoySelectionEntities.get(0), merchant.getCommissionPercentage(), merchant.getServiceFee());
        courierTransaction.setCourierToStoreDistance(null);
        courierTransaction.setAdditionalDeliveryAmt(null);
        courierTransaction.setPaidToCourier(null);
        courierTransaction.setProfit(null);
        order.setCourierTransaction(courierTransaction);

        order.setPaymentMode(paymentMode);
        if(paymentMode.equals(PaymentMode.WALLET)){
            if(BigDecimalUtil.isLessThen(BigDecimalUtil.checkNull(order.getCustomer().getWalletAmount()),order.getGrandTotal())){
                throw new YSException("ORD020");
            }
            order.setPaidFromWallet(order.getGrandTotal());
            order.setPaidFromCOD(BigDecimal.ZERO);
            order.getCustomer().setWalletAmount(order.getCustomer().getWalletAmount().subtract(order.getGrandTotal()));

            /*Setting data for wallet transaction entity*/
            List<WalletTransactionEntity> walletTransactionEntities = new ArrayList<WalletTransactionEntity>();
            WalletTransactionEntity walletTransactionEntity = new WalletTransactionEntity();
            walletTransactionEntity.setTransactionDate(DateUtil.getCurrentTimestampSQL());
            walletTransactionEntity.setAccountType(AccountType.DEBIT);
            String currency = systemPropertyService.readPrefValue(PreferenceType.CURRENCY);
            String remarks = MessageBundle.getMessage("WTM001", "push_notification.properties");
            walletTransactionEntity.setRemarks(String.format(remarks, currency, order.getGrandTotal(), order.getStore().getName()));
            walletTransactionEntity.setTransactionAmount(order.getGrandTotal());
            walletTransactionEntity.setOrder(order);
            walletTransactionEntity.setCustomer(order.getCustomer());
            walletTransactionEntity.setPaymentMode(PaymentMode.WALLET);
            walletTransactionEntity.setAvailableWalletAmount(order.getCustomer().getWalletAmount());
            systemAlgorithmService.encodeWalletTransaction(walletTransactionEntity);
            walletTransactionEntities.add(walletTransactionEntity);
            order.setWalletTransactions(walletTransactionEntities);
        }else{
            if(BigDecimalUtil.isLessThen(new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.ORDER_MAX_AMOUNT)), order.getGrandTotal())) {
                //Max order amount reached
                throw new YSException("CRT007: Value of "+ systemPropertyService.readPrefValue(PreferenceType.CURRENCY) + systemPropertyService.readPrefValue(PreferenceType.ORDER_MAX_AMOUNT) + " can be order");
            }
            order.setPaidFromWallet(BigDecimal.ZERO);
            order.setPaidFromCOD(order.getGrandTotal());
        }

        order.setShortFallAmount(BigDecimal.ZERO);
        if(order.getCustomer().getTotalOrderPlaced() == null){
            order.getCustomer().setTotalOrderPlaced(1);
        }else{
            order.getCustomer().setTotalOrderPlaced(order.getCustomer().getTotalOrderPlaced()+1);
        }
        orderDaoService.save(order);

        deleteCarts(cartIds);
        /* Push Notifications */
        List<Integer> idList = getIdOfDeliveryBoys(deliveryBoySelectionEntitiesWithProfit);
        List<String> deviceTokens = userDeviceDaoService.getDeviceTokenFromDeliveryBoyId(idList);
        PushNotification pushNotification = new PushNotification();
        pushNotification.setTokens(deviceTokens);
        pushNotification.setMessage(MessageBundle.getPushNotificationMsg("PN001"));
        pushNotification.setPushNotificationRedirect(PushNotificationRedirect.ORDER);
        pushNotification.setExtraDetail(order.getId().toString());
        pushNotification.setNotifyTo(NotifyTo.DELIVERY_BOY);
        PushNotificationUtil.sendNotificationToAndroidDevice(pushNotification);

//        Float timeInSeconds = Float.parseFloat(systemPropertyService.readPrefValue(PreferenceType.ORDER_REQUEST_TIMEOUT_IN_MIN)) * 60;
//        Integer timeOut = timeInSeconds.intValue();
//        scheduleChanger.scheduleTask(DateUtil.findDelayDifference(DateUtil.getCurrentTimestampSQL(), timeOut));
    }

    private  List<Integer> getIdOfDeliveryBoys(List<DeliveryBoySelectionEntity> deliveryBoySelectionEntities){
        List<Integer> idList = new ArrayList<Integer>();
        for(DeliveryBoySelectionEntity deliveryBoySelectionEntity: deliveryBoySelectionEntities){
            idList.add(deliveryBoySelectionEntity.getDeliveryBoy().getId());
        }
        return idList;
    }

    private Boolean deleteCarts(List<Integer> cartList) throws Exception{
        if (cartList.size() > 0) {
            log.info("++++++++ Deleting previous cart and and its attributes ++++++++");
            List<Integer> cartAttributes = cartAttributesDaoService.findCartAttributes(cartList);
            // Delete Cart Attributes
            if (cartAttributes.size() > 0)
                cartAttributesDaoService.deleteCartAttributes(cartAttributes);


            //get custom items of carts
            List<Integer> cartCustomItems = cartCustomItemDaoService.findCartCustomItems(cartList);

            //delete cart custom items
            if (cartCustomItems.size() > 0)
                cartCustomItemDaoService.deleteCartCustomItems(cartCustomItems);

            //Delete Carts
            cartDaoService.deleteCarts(cartList);
        }
        return true;
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
        order.setCustomerChargeableDistance(BigDecimalUtil.getDistanceInKiloMeters(actualDistance).setScale(2, RoundingMode.HALF_UP));
        return nearestStore;
    }

    /*Calculate distance of delivery boy to store and select only delivery boys satisfying timing condition*/
    private List<DeliveryBoySelectionEntity> calculateStoreToDeliveryBoyDistance(StoreEntity store, List<DeliveryBoyEntity> capableDeliveryBoys, OrderEntity order, Boolean reprocess) throws Exception {
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
            deliveryBoySelectionEntity.setDistanceToStore(BigDecimalUtil.getDistanceInKiloMeters(distanceList.get(i)).setScale(2, RoundingMode.HALF_UP));
            deliveryBoySelectionEntity.setDeliveryBoy(capableDeliveryBoys.get(i));
            deliveryBoySelectionEntity.setStoreToCustomerDistance(order.getCustomerChargeableDistance());
            deliveryBoySelectionEntity.setOrder(order);
            int timeFactor = Integer.parseInt(systemPropertyService.readPrefValue(GeneralUtil.getTimeTakenFor(capableDeliveryBoys.get(i).getVehicleType())));

            BigDecimal totalDistance = BigDecimalUtil.getDistanceInKiloMeters(distanceList.get(i)).add(order.getCustomerChargeableDistance().setScale(2, RoundingMode.HALF_UP));
            Integer timeRequired = totalDistance.multiply(new BigDecimal(timeFactor)).setScale(0, RoundingMode.HALF_UP).intValue();

            deliveryBoySelectionEntity.setTimeRequired(timeRequired + timeAtStore);
            int timeRemaining = findRemainingTimeForPreviousOrder(capableDeliveryBoys.get(i).getId()) + timeRequired + timeAtStore;
            deliveryBoySelectionEntity.setTotalTimeRequired(timeRemaining);
            timeDetails.add(timeRemaining);

            deliveryBoySelectionEntity.setAccepted(false);
            deliveryBoySelectionEntity.setRejected(false);
            selectionDetails.add(deliveryBoySelectionEntity);
            log.info("Shoppers selected from distance calculation:"+selectionDetails.toString());
        }
        return filterDeliveryBoySelection(selectionDetails, timeDetails, reprocess);
    }

    /* Select only delivery boys satisfying timing condition from list of selected delivery boys from distance criteria*/
    private List<DeliveryBoySelectionEntity> filterDeliveryBoySelection(List<DeliveryBoySelectionEntity> selectionEntities, List<Integer> timeDetails, Boolean reprocess) throws Exception {
        List<DeliveryBoySelectionEntity> filteredDBoys = new ArrayList<DeliveryBoySelectionEntity>();
        int minimumTime = Collections.min(timeDetails);
        int deviationInTime = Integer.parseInt(systemPropertyService.readPrefValue(PreferenceType.DEVIATION_IN_TIME));
        if(reprocess)
            deviationInTime += Integer.parseInt(systemPropertyService.readPrefValue(PreferenceType.REPROCESS_EXTRA_TIME));
        for (DeliveryBoySelectionEntity deliveryBoySelectionEntity : selectionEntities) {
            if ((deliveryBoySelectionEntity.getTotalTimeRequired() - minimumTime) <= deviationInTime) {
                filteredDBoys.add(deliveryBoySelectionEntity);
            }
        }
        return filteredDBoys;
    }

    /* This method return the time required for completing previous orders. */
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

    private Boolean hasCustomerCustomItem(OrderEntity order) throws Exception{
        Boolean hasCustomerCustomItem = false;
        List<ItemsOrderEntity> itemsOrders = order.getItemsOrder();
        if(itemsOrders != null){
            for (ItemsOrderEntity itemsOrder: itemsOrders){
                CustomItemEntity customItem = itemsOrder.getCustomItem();
                if(customItem != null && customItem.getCustomerCustom()){
                    hasCustomerCustomItem = true;
                    break;
                }
            }
        }
        return hasCustomerCustomItem;
    }

    private List<DeliveryBoySelectionEntity> filterDBoyWithProfitCriteria(OrderEntity order, List<DeliveryBoySelectionEntity> deliveryBoySelectionEntities, BigDecimal merchantCommission, BigDecimal merchantServiceFee) throws Exception {
        log.info("Unfiltered Dboys:"+deliveryBoySelectionEntities.toString());
        List<DeliveryBoySelectionEntity> filteredDeliveryBoys = new ArrayList<DeliveryBoySelectionEntity>();
        BigDecimal MINIMUM_PROFIT_PERCENTAGE = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.MINIMUM_PROFIT_PERCENTAGE));
        Boolean PROFIT_CHECK = GeneralUtil.parseBoolean(systemPropertyService.readPrefValue(PreferenceType.PROFIT_CHECK_FLAG));

        //check if is custom order: sagar
        Boolean isCustomerCustomOrder = hasCustomerCustomItem(order);

        for (DeliveryBoySelectionEntity deliveryBoySelectionEntity : deliveryBoySelectionEntities) {
            CourierTransactionEntity courierTransaction = systemAlgorithmService.getCourierTransaction(order, deliveryBoySelectionEntity, merchantCommission, merchantServiceFee);
            if(PROFIT_CHECK && !isCustomerCustomOrder){
                if(BigDecimalUtil.isGreaterThen(courierTransaction.getProfit(), BigDecimalUtil.percentageOf(order.getTotalCost(), MINIMUM_PROFIT_PERCENTAGE)))
                    filteredDeliveryBoys.add(deliveryBoySelectionEntity);
            }else{
                filteredDeliveryBoys.add(deliveryBoySelectionEntity);
            }
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

    /* Used For Only Manager and Account Registration */
    public void registerCustomer(UserEntity user, HeaderDto headerDto) throws Exception{

        RoleEntity userRole = userDaoService.getRoleByRole(user.getRole().getRole());
        user.setRole(userRole);

        CustomerEntity referrer = customerDaoService.find(Long.parseLong(headerDto.getId()));

        if(referrer == null)
            throw new YSException("VLD011");

        Integer referred_friends_count = referrer.getReferredFriendsCount();
        if(referred_friends_count == null){
            referred_friends_count = 1;
        }else{
            referred_friends_count++;
        }
        referrer.setReferredFriendsCount(referred_friends_count);
        referrer.getUser().setLastActivityDate(DateUtil.getCurrentTimestampSQL());
        customerDaoService.update(referrer);
        CustomerEntity cUser = customerDaoService.find(user.getCustomer().getFacebookId());
        if(cUser != null)
            throw new YSException("VLD010");

        if(!referrer.getDefault() && referrer.getReferredFriendsCount() != null && referrer.getReferredFriendsCount() >= Integer.parseInt(systemPropertyService.readPrefValue(PreferenceType.MAX_REFERRED_FRIENDS_COUNT)))
            throw new YSException("VLD021");

        user.getCustomer().setReferredBy(Long.parseLong(headerDto.getId()));
        user.getCustomer().setDefault(false);
        user.getCustomer().setFbToken(headerDto.getAccessToken());
        user.getCustomer().setUser(user);
        user.setCreatedDate(DateUtil.getCurrentTimestampSQL());
        userDaoService.save(user);
    }

    @Override
    public List<RatingReason> getRatingReasons() throws Exception {
        return new ArrayList<RatingReason>(EnumSet.allOf(RatingReason.class));
    }

    @Override
    public List<MyOrderDto> getMyCurrentOrders(Long facebookId) throws Exception {
        return customerDaoService.getCurrentOrdersByFacebookId(facebookId);
    }

    @Override
    public PaginationDto getMyPastOrders(Long facebookId, Page page) throws Exception {
        PaginationDto paginationDto = new PaginationDto();
        Integer totalRows =  customerDaoService.getNumberOfPastOrdersByFacebookId(facebookId);
        paginationDto.setNumberOfRows(totalRows);

        if(page != null){
            page.setTotalRows(totalRows);
        }
        List<MyOrderDto> pastOrders = customerDaoService.getPastOrdersByFacebookId(facebookId, page);
        paginationDto.setData(pastOrders);
        return paginationDto;
    }

    @Override
    public SearchDto getSearchContent(String word, RequestJsonDto requestJsonDto) throws Exception {
        log.info("+++++++++++++ Searching content of " + word + " +++++++++++++");
        List<SearchDto> searchResult = null;

//        if(word.length() < 3)
//            throw new YSException("VLD028");

        String lat = null;
        String lon = null;
        if (requestJsonDto.getGpsInfo() == null) {
            CustomerEntity customerEntity = customerDaoService.getLatLong(requestJsonDto.getCustomerInfo().getClientId());
            if (customerEntity != null) {
                lat = customerEntity.getLatitude();
                lon = customerEntity.getLongitude();
            }
//                throw new YSException("VLD011");

        } else {
            lat = requestJsonDto.getGpsInfo().getLatitude();
            lon = requestJsonDto.getGpsInfo().getLongitude();
        }

        /* Item Search Algorithm Starts */
        List<ItemEntity> items = itemDaoService.searchItems(word);
        if(items !=null && items.size() > 0){
            searchResult = new ArrayList<>();
            //Search Nearest Stores for Address
            for(Iterator<ItemEntity> iterator = items.iterator(); iterator.hasNext();){
                ItemEntity item = iterator.next();

                SearchDto tempItem = new SearchDto();
                List<StoreEntity> storeEntities = storeDaoService.findActiveStores(item.getBrandId());
                if(storeEntities.size() == 0) { //If all Stores are inactivated
                    iterator.remove();
                    continue;
                }

                //Add Default Image If Image not at item
                if(item.getImageUrl() == null){
                    item.setImageUrl(systemPropertyService.readPrefValue(PreferenceType.DEFAULT_IMG_ITEM));
                    tempItem.setDefaultImage(true);
                }

                if(lat !=null && lon !=null){
                    /* Extract Latitude and Longitude */
                    String[] storeDistance = new String[storeEntities.size()];
                    String[] customerDistance = {GeoCodingUtil.getLatLong(lat, lon)};

                    for (int i = 0; i < storeEntities.size(); i++) {
                        storeDistance[i] = GeoCodingUtil.getLatLong(storeEntities.get(i).getLatitude(), storeEntities.get(i).getLongitude());
                    }

                    List<BigDecimal> distanceList = GeoCodingUtil.getListOfAirDistances(customerDistance, storeDistance);

                    //Set Distance to Store Entity
                    for (int i = 0; i < storeEntities.size(); i++) {
                        storeEntities.get(i).setCustomerToStoreDistance(distanceList.get(i));
                    }

                    //Store Entity List Sorted by Distance
                    Collections.sort(storeEntities, new StoreDistanceComparator());
                }


                //Set to All
                tempItem.setItemId(item.getId());
                tempItem.setItemName(item.getName());
                tempItem.setUnitPrice(item.getUnitPrice());
                tempItem.setItemImageUrl(item.getImageUrl());
                tempItem.setBrandId(item.getBrandId());
                tempItem.setBrandName(item.getBrandName());
                tempItem.setStoreStreet(storeEntities.get(0).getStreet());
                tempItem.setAdditionalOffer(item.getAdditionalOffer());
                tempItem.setItem(true);
                searchResult.add(tempItem);
            }
        }
        Integer totalItemSize = searchResult==null ? 0 : searchResult.size();

        /* Store Search Algorithm Starts */
        Integer limit = 0;
        if(totalItemSize > CommonConstants.MAX_SEARCH_ITEM)
            limit = CommonConstants.MAX_SEARCH_STORE;
        else if(totalItemSize > 0)
            limit = CommonConstants.MAX_SEARCH_DATA - totalItemSize;
        else if(totalItemSize==0)
            limit = CommonConstants.MAX_SEARCH_DATA;

        List<Integer> brandIds = storesBrandDaoService.getActiveSearchBrands(word, limit);

        List<SearchDto> storeList = new ArrayList<>();

        if(brandIds!=null && brandIds.size() > 0){
            if(searchResult == null)
                searchResult = new ArrayList<>();

            //Check How Many Items
            if(totalItemSize > CommonConstants.MAX_SEARCH_ITEM) {

                Integer exceedItem = 0;
                if(totalItemSize > CommonConstants.MAX_SEARCH_ITEM && brandIds.size()>= CommonConstants.MAX_SEARCH_STORE)
                    exceedItem = CommonConstants.MAX_SEARCH_ITEM;
                else
                    exceedItem = CommonConstants.MAX_SEARCH_ITEM + CommonConstants.MAX_SEARCH_STORE - brandIds.size();

                for(int i= searchResult.size()-1; i>= exceedItem; i--){
                    searchResult.remove(i);
                    totalItemSize--;
                }
            }

            List<StoreEntity> storeEntities = storeDaoService.findSearchStores(brandIds);

            if(lat !=null && lon !=null){
                  /* Extract Latitude and Longitude */
                String[] storeDistance = new String[storeEntities.size()];
                String[] customerDistance = {GeoCodingUtil.getLatLong(lat, lon)};

                for (int i = 0; i < storeEntities.size(); i++) {
                    storeDistance[i] = GeoCodingUtil.getLatLong(storeEntities.get(i).getLatitude(), storeEntities.get(i).getLongitude());
                }

                List<BigDecimal> distanceList = GeoCodingUtil.getListOfAirDistances(customerDistance, storeDistance);

                //Set Distance to Store Entity
                for (int i = 0; i < storeEntities.size(); i++) {
                    storeEntities.get(i).setCustomerToStoreDistance(distanceList.get(i));
                }

                //Store Entity List Sorted by Distance
                Collections.sort(storeEntities, new StoreDistanceComparator());
            }


            //Now Combine all brand in one list
            for (StoreEntity storeEntity : storeEntities) {
                if (!containsBrandId(storeList, storeEntity.getStoresBrand().getId())) {
                    SearchDto tempBrand = new SearchDto();
                    tempBrand.setBrandId(storeEntity.getStoresBrand().getId());
                    tempBrand.setOpeningTime(storeEntity.getStoresBrand().getOpeningTime());
                    tempBrand.setClosingTime(storeEntity.getStoresBrand().getClosingTime());
                    tempBrand.setBrandName(storeEntity.getStoresBrand().getBrandName());
                    tempBrand.setBrandLogo(storeEntity.getStoresBrand().getBrandLogo());
                    tempBrand.setBrandImage(storeEntity.getStoresBrand().getBrandImage());
                    tempBrand.setBrandUrl(storeEntity.getStoresBrand().getBrandUrl());
                    tempBrand.setItem(false);
                    tempBrand.setStoreStreet(storeEntity.getStreet());
                    tempBrand.setMinOrderAmount(storeEntity.getStoresBrand().getMinOrderAmount());
                    searchResult.add(tempBrand);
                    storeList.add(tempBrand);
                }

            }
        }

        //Perform Sorted Search Content Pagination
//        PageInfo pageInfo = null;
//        List<SearchDto> sortedList = new ArrayList<>();
//        if (searchResult !=null && searchResult.size() > 0) {
//            Integer pageId = 1;
//            if (requestJsonDto.getPageInfo() != null)
//                pageId = requestJsonDto.getPageInfo().getPageNumber();
//
//            StaticPagination staticPagination = new StaticPagination();
//            staticPagination.paginate(searchResult, pageId);
//            sortedList = (List<SearchDto>) staticPagination.getList();
//            staticPagination.setList(null);
//            pageInfo = staticPagination;
//        }

        SearchDto searchDto = new SearchDto();
        if(searchResult != null){
//            searchDto = new SearchDto();

//            searchDto.setPageInfo(pageInfo);
            searchDto.setCurrency(systemPropertyService.readPrefValue(PreferenceType.CURRENCY));
            searchDto.setTotalItemSize(totalItemSize);
            searchDto.setTotalStoreSize(brandIds.size());
            searchDto.setSearchList(searchResult);
        }
        return searchDto;
    }

    @Override
    public SearchDto getSearchInStore(String word, Integer brandId,RequestJsonDto requestJsonDto) throws Exception {
        log.info("++++++++ Searching Items in Store " + brandId + " for content " + word + " +++++++++++++++++++++");

        List<SearchDto> searchResult = null;
//        if(word.length() < 3)
//            throw new YSException("VLD028");

        String lat = null;
        String lon = null;
        if (requestJsonDto.getGpsInfo() == null) {
            CustomerEntity customerEntity = customerDaoService.getLatLong(requestJsonDto.getCustomerInfo().getClientId());
            if (customerEntity != null) {
                lat = customerEntity.getLatitude();
                lon = customerEntity.getLongitude();
            }
//                throw new YSException("VLD011");

        } else {
            lat = requestJsonDto.getGpsInfo().getLatitude();
            lon = requestJsonDto.getGpsInfo().getLongitude();
        }


        List<StoreEntity> storeEntities = storeDaoService.findStores(brandId);

        if(storeEntities.size() == 0)//If all Stores are inactivated
            return new SearchDto();

        List<ItemEntity> items = itemDaoService.searchItemsInStore(word, brandId);

        if(items !=null && items.size() > 0){
            searchResult = new ArrayList<>();
            for(ItemEntity item : items){
                SearchDto tempItem = new SearchDto();

                //Add Default Image If Image not at item
                if(item.getImageUrl() == null){
                    item.setImageUrl(systemPropertyService.readPrefValue(PreferenceType.DEFAULT_IMG_ITEM));
                    tempItem.setDefaultImage(true);
                }

                if(lat !=null && lon !=null) {
                    /* Extract Latitude and Longitude */
                    String[] storeDistance = new String[storeEntities.size()];
                    String[] customerDistance = {GeoCodingUtil.getLatLong(lat, lon)};

                    for (int i = 0; i < storeEntities.size(); i++) {
                        storeDistance[i] = GeoCodingUtil.getLatLong(storeEntities.get(i).getLatitude(), storeEntities.get(i).getLongitude());
                    }

                    List<BigDecimal> distanceList = GeoCodingUtil.getListOfAirDistances(customerDistance, storeDistance);

                    //Set Distance to Store Entity
                    for (int i = 0; i < storeEntities.size(); i++) {
                        storeEntities.get(i).setCustomerToStoreDistance(distanceList.get(i));
                    }

                    //Store Entity List Sorted by Distance
                    Collections.sort(storeEntities, new StoreDistanceComparator());
                }

                //Set to All
                tempItem.setItemId(item.getId());
                tempItem.setItemName(item.getName());
                tempItem.setUnitPrice(item.getUnitPrice());
                tempItem.setItemImageUrl(item.getImageUrl());
                tempItem.setBrandId(item.getBrandId());
                tempItem.setBrandName(item.getBrandName());

                if(storeEntities.get(0).getStreet()!=null && !storeEntities.get(0).getStreet().isEmpty())
                    tempItem.setStoreStreet(storeEntities.get(0).getStreet());
                else
                    tempItem.setStoreStreet("Unknown Location");

                //tempItem.setItem(true);
                searchResult.add(tempItem);
            }
        }

        SearchDto searchDto = new SearchDto();
        if(searchResult!=null && searchResult.size() > 0){
            searchDto.setSearchList(searchResult);
            searchDto.setCurrency(systemPropertyService.readPrefValue(PreferenceType.CURRENCY));
            searchDto.setTotalItemSize(searchResult.size());
        }

        return searchDto;
    }

    private boolean containsBrandId(List<SearchDto> list, Integer id) {
        for (SearchDto object : list) {
            if (object.getBrandId() == id) {
                return true;
            }
        }
        return false;
    }


    class StoreDistanceComparator implements Comparator<StoreEntity> {
        @Override
        public int compare(StoreEntity o1, StoreEntity o2) {
            BigDecimal distanceSub = o1.getCustomerToStoreDistance().subtract(o2.getCustomerToStoreDistance());
            return distanceSub.intValue();
        }
    }

    @Override
    public TrackOrderDto getTrackOrderInfo(Integer orderId) throws Exception {
        TrackOrderDto trackOrder = orderDaoService.getTrackOrderInfo(orderId);
        if(trackOrder == null){
            throw new YSException("ORD014");
        }
        if(trackOrder.getOrderStatus().equals(JobOrderStatus.ORDER_PLACED)){
            String message = MessageBundle.getMessage("TDM001", "push_notification.properties");
            trackOrder.setTimeDisplayMessage(message);
        }else if(trackOrder.getOrderStatus().equals(JobOrderStatus.ORDER_ACCEPTED)){
            String message = MessageBundle.getMessage("TDM002", "push_notification.properties");
            trackOrder.setTimeDisplayMessage(message);
        }else if(trackOrder.getOrderStatus().equals(JobOrderStatus.IN_ROUTE_TO_PICK_UP)){
            String origin = GeoCodingUtil.getLatLong(trackOrder.getCourierBoyLatitude(), trackOrder.getCourierBoyLongitude());
            String destination = GeoCodingUtil.getLatLong(trackOrder.getStoreLatitude(), trackOrder.getStoreLongitude());
            VehicleType vehicleType = VehicleType.fromInt(trackOrder.getVehicleType());
            Integer requiredTime = GeoCodingUtil.getRequiredTime(origin, destination, vehicleType);
            requiredTime = GeneralUtil.getMinimumTimeDisplay(requiredTime);
            String message = MessageBundle.getMessage("TDM003", "push_notification.properties");
            trackOrder.setTimeDisplayMessage(String.format(message, requiredTime));
        }else if(trackOrder.getOrderStatus().equals(JobOrderStatus.AT_STORE)){
            String message = MessageBundle.getMessage("TDM004", "push_notification.properties");
            trackOrder.setTimeDisplayMessage(message);
        }else if(trackOrder.getOrderStatus().equals(JobOrderStatus.IN_ROUTE_TO_DELIVERY)){
            String origin = GeoCodingUtil.getLatLong(trackOrder.getCourierBoyLatitude(), trackOrder.getCourierBoyLongitude());
            String destination = GeoCodingUtil.getLatLong(trackOrder.getDeliveryLatitude(), trackOrder.getDeliveryLongitude());
            VehicleType vehicleType = VehicleType.fromInt(trackOrder.getVehicleType());
            Integer requiredTime = GeoCodingUtil.getRequiredTime(origin, destination, vehicleType);
            requiredTime = GeneralUtil.getMinimumTimeDisplay(requiredTime);
            String message = MessageBundle.getMessage("TDM005", "push_notification.properties");
            trackOrder.setTimeDisplayMessage(String.format(message, requiredTime));
        }else if(trackOrder.getOrderStatus().equals(JobOrderStatus.DELIVERED)){
            String message = MessageBundle.getMessage("TDM006", "push_notification.properties");
            trackOrder.setTimeDisplayMessage(message);
        }
        return trackOrder;
    }

    @Override
    public CustomerEntity getCustomerProfile(Long facebookId) throws Exception {
        CustomerEntity customer = customerDaoService.getCustomerProfile(facebookId);
        if(customer.getId() == null)
            throw new YSException("VLD011");
        if (customer.getReferredFriendsCount() == null)
            customer.setReferredFriendsCount(0);
        if (customer.getRewardsEarned() == null)
            customer.setRewardsEarned(BigDecimal.ZERO);
        customer.setWalletAmount(BigDecimalUtil.checkNull(customer.getWalletAmount()).subtract(BigDecimalUtil.checkNull(customer.getShortFallAmount())));
        customer.setShortFallAmount(null);
        customer.setCurrency(systemPropertyService.readPrefValue(PreferenceType.CURRENCY));
        return customer;
    }

    @Override
    public Boolean rateDeliveryBoy(Integer orderId, Long facebookId, RatingEntity rating) throws Exception {
        OrderEntity order = orderDaoService.find(orderId);
        if(order == null){
            throw new YSException("VLD017");
        }
        if(!order.getCustomer().getFacebookId().equals(facebookId)){
            throw new YSException("ORD013");
        }
        RatingEntity ratingEntity = order.getRating();
        if(ratingEntity == null){
            ratingEntity = new RatingEntity();
            ratingEntity.setOrder(order);
            order.setRating(ratingEntity);
        }

        // IF Delayed In Deliver Then Rating Will be Decreased (Appended By Surendra)
//        DBoyOrderHistoryEntity dBoyOrderHistoryEntity = dBoyOrderHistoryDaoService.getOrderHistory(orderId, order.getDeliveryBoy().getId());
//        Integer assignedTime = order.getAssignedTime();
//        Double deliveredTime = DateUtil.getMinDiff(dBoyOrderHistoryEntity.getOrderAcceptedAt().getTime(), dBoyOrderHistoryEntity.getOrderCompletedAt().getTime());
//        Integer remainingTime = (assignedTime + Integer.valueOf(systemPropertyService.readPrefValue(PreferenceType.DBOY_GRESS_TIME))) - deliveredTime.intValue();
//        log.info("+++++++ Assigned Time " + assignedTime + " & Delivered Time " + deliveredTime + " +++++++++");
//        Integer dboyRating = rating.getDeliveryBoyRating();
//        if(remainingTime < 0)
//            dboyRating = dboyRating - DELAYED_DELIVERY;

        // Ended By Surendra

        ratingEntity.setCustomerComment(rating.getCustomerComment());
        ratingEntity.setDeliveryBoyRating(rating.getDeliveryBoyRating());
        ratingEntity.setRatingIssues(rating.getRatingIssues());
        orderDaoService.update(order);

        /* Now Calculate Average Rating (Appended By Surendra) */
//        DeliveryBoyEntity deliveryBoy = order.getDeliveryBoy();
//        List<Integer> ratings = orderDaoService.getDboyRatings(order.getDeliveryBoy().getId());
//        if(ratings !=null && ratings.size()> 0){
//            BigDecimal totalRate = BigDecimal.ZERO;
//
//            for(Integer rate : ratings){
//                totalRate = totalRate.add(new BigDecimal(rate));
//            }
//
//            BigDecimal averageRating = getAverageRating(ratings.size(), totalRate);
//            deliveryBoyDaoService.updateAverageRating(averageRating, order.getDeliveryBoy().getId());
//
//        /* Less then or equal 1 means Current Delivery Also In Session (That has not completed) */
//            if(orderDaoService.hasDboyRunningOrders(order.getDeliveryBoy().getId()) <= 1){
//                if(BigDecimalUtil.isLessThen(averageRating, new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DBOY_DEFAULT_RATING)))) {
//                    //Deactivate User
//                    log.info("Deactivating Shopper id : " + order.getDeliveryBoy().getId());
//                    userDaoService.deactivateUser(order.getDeliveryBoy().getUser().getId());
//                }
//
//            }
//
//        }

        Integer deliveryBoyId = order.getDeliveryBoy().getId();
        List<RatingEntity> ratings = orderDaoService.getDboyRatingDetails(deliveryBoyId);
        Integer gressTime = Integer.valueOf(systemPropertyService.readPrefValue(PreferenceType.DBOY_GRESS_TIME));
        if(ratings !=null && ratings.size()> 0){
            BigDecimal totalRate = BigDecimal.ZERO;
            for(RatingEntity rate : ratings){

                /* IF Delayed In Deliver Then Rating Will be Decreased during Calculate Average Rating */
                DBoyOrderHistoryEntity dBoyOrderHistoryEntity = dBoyOrderHistoryDaoService.getOrderHistory(rate.getOrderId(), deliveryBoyId);
                Integer assignedTime = order.getAssignedTime();
                Double deliveredTime = DateUtil.getMinDiff(dBoyOrderHistoryEntity.getOrderAcceptedAt().getTime(), dBoyOrderHistoryEntity.getOrderCompletedAt().getTime());
                Integer remainingTime = (assignedTime + gressTime) - deliveredTime.intValue();
                log.info("+++++++ Assigned Time " + assignedTime + " & Delivered Time " + deliveredTime + " +++++++++");
                Integer dboyRating = rate.getDeliveryBoyRating();

                if(remainingTime < 0)
                    dboyRating = dboyRating - DELAYED_DELIVERY;

                totalRate = totalRate.add(new BigDecimal(dboyRating));
            }

            BigDecimal averageRating = getAverageRating(ratings.size(), totalRate);
            deliveryBoyDaoService.updateAverageRating(averageRating, deliveryBoyId);

        /* Less then or equal 1 means Current Delivery Also In Session (That has not completed) */
            if(orderDaoService.hasDboyRunningOrders(deliveryBoyId) <= 1){
                if(BigDecimalUtil.isLessThen(averageRating, new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DBOY_DEFAULT_RATING)))) {
                    //Deactivate User
                    log.info("Deactivating Shopper id : " + deliveryBoyId);
                    userDaoService.deactivateUser(order.getDeliveryBoy().getUser().getId());
                }

            }

        }

        return true;
    }

    private BigDecimal getAverageRating(Integer totalSize, BigDecimal totalRateSum) throws Exception {
        BigDecimal averageRating;
        /* This Code Perform for all order  All But.....
            Being Requirement Changed Only 10 data retrieved, Code is as it is */

        if(totalSize<= 10 && totalSize > 0){
            Integer rate = Integer.valueOf(totalRateSum.intValue()) + Integer.parseInt(systemPropertyService.readPrefValue(PreferenceType.DBOY_DEFAULT_RATING)) * (10-totalSize);
            averageRating = new BigDecimal(rate).divide(new BigDecimal(10), MathContext.DECIMAL128);
            averageRating = averageRating.setScale(0, BigDecimal.ROUND_HALF_UP);
        } else {
            averageRating = totalRateSum.divide(new BigDecimal(totalSize), MathContext.DECIMAL128);
            averageRating = averageRating.setScale(0, BigDecimal.ROUND_HALF_UP);
        }
        return averageRating;
    }

    @Override
    public RatingEntity getRatingFromCustomerSide(Integer orderId) throws Exception {
        RatingEntity ratingEntity = ratingDaoService.getCustomerSideRatingFromOrderId(orderId);
        RatingEntity rating = new RatingEntity();
        if(ratingEntity != null){
            rating.setDeliveryBoyRating(ratingEntity.getDeliveryBoyRating());
            rating.setCustomerComment(ratingEntity.getCustomerComment());
            List<RatingReason> ratingReasons = new ArrayList<RatingReason >();
            for(RatingReason ratingReason: ratingEntity.getRatingIssues()){
                ratingReasons.add(ratingReason);
            }
            if(ratingReasons.size() > 0)
                rating.setRatingIssues(ratingReasons);
        }
        rating.setAllRatingIssues(this.getRatingReasons());
        return rating;
    }

    @Override
    public Boolean reprocessOrder(Integer orderId) throws Exception {
        log.info("Reprocessing order with ID:"+orderId);
        OrderEntity order = orderDaoService.find(orderId);
        if (order != null) {
            if (order.getOrderStatus().equals(JobOrderStatus.ORDER_PLACED)) {
                /* FORCE ASSIGN FEATURE */
               /* if(order.getReprocessTime() > 0){
                    log.info("Order is cancelling since it has been reprocessed already:"+orderId);
                    return cancelOrder(order);
                }*/
                order.setReprocessTime(GeneralUtil.ifNullToZero(order.getReprocessTime()) + 1);
                order.setOrderDate(DateUtil.getCurrentTimestampSQL());
                deliveryBoySelectionDaoService.updateAllSelectionToRejectMode(orderId);
                /* Finding delivery boys based on active status. */
                int timeInMin = Integer.parseInt(systemPropertyService.readPrefValue(PreferenceType.LOCATION_UPDATE_TIMEOUT_IN_MIN));
                List<DeliveryBoyEntity> availableAndActiveDBoys = deliveryBoyDaoService.findAllCapableDeliveryBoys(timeInMin);
                if (availableAndActiveDBoys.size() == 0) {
                    log.info("Order is cancelling since number of available shoppers is zero:" + orderId);
                    return cancelOrder(order);
                }
                CourierTransactionEntity courierTransactionEntity = order.getCourierTransaction();
                 /* Selects nearest delivery boys based on timing. */
                List<DeliveryBoySelectionEntity> deliveryBoySelectionEntities = calculateStoreToDeliveryBoyDistance(order.getStore(), availableAndActiveDBoys, order, true);
                /* Selects delivery boys based on profit criteria. */
                List<DeliveryBoySelectionEntity> deliveryBoySelectionEntitiesWithProfit = filterDBoyWithProfitCriteria(order, deliveryBoySelectionEntities, courierTransactionEntity.getCommissionPct(), courierTransactionEntity.getServiceFeePct());

                if (deliveryBoySelectionEntitiesWithProfit.size() == 0) {
                    log.info("Order is cancelling since not enough profit:" + orderId);
                    return cancelOrder(order);
                }

                if (order.getReprocessTime() > 0) {
                    Collections.sort(deliveryBoySelectionEntitiesWithProfit, new TotalTimeTakenComparator());
                    DeliveryBoySelectionEntity deliveryBoySelectionEntity = deliveryBoySelectionEntitiesWithProfit.get(0);
                    deliveryBoySelectionEntity.setOrder(order);
                    deliveryBoySelectionEntity.setAccepted(true);
                    order.setDeliveryBoySelections(Collections.singletonList(deliveryBoySelectionEntity));
                    boolean isAssigned = forceAssign(deliveryBoySelectionEntity);
                    if (isAssigned) {
                        List<String> deviceTokens = userDeviceDaoService.getDeviceTokenFromDeliveryBoyId(Collections.singletonList(deliveryBoySelectionEntity.getDeliveryBoy().getId()));
                        PushNotification pushNotification = new PushNotification();
                        pushNotification.setTokens(deviceTokens);
                        pushNotification.setMessage(String.format(MessageBundle.getPushNotificationMsg("PN004"), orderId));
                        pushNotification.setPushNotificationRedirect(PushNotificationRedirect.ORDER);
                        pushNotification.setExtraDetail(order.getId().toString()+"/status/FORCE_ASSIGNED");
                        pushNotification.setNotifyTo(NotifyTo.DELIVERY_BOY);
                        PushNotificationUtil.sendNotificationToAndroidDevice(pushNotification);

                        SMSUtil.sendSMS(String.format(CommonConstants.FORCE_ORDER_ASSIGN_TEXT, orderId), deliveryBoySelectionEntity.getDeliveryBoy().getUser().getUsername(), systemPropertyService.readPrefValue(PreferenceType.SMS_COUNTRY_CODE),systemPropertyService.readPrefValue(PreferenceType.SMS_PROVIDER));
                    }
                } else {
                    order.setDeliveryBoySelections(deliveryBoySelectionEntitiesWithProfit);
                    orderDaoService.update(order);
                    /* Push Notifications */
                    List<Integer> idList = getIdOfDeliveryBoys(deliveryBoySelectionEntitiesWithProfit);
                    List<String> deviceTokens = userDeviceDaoService.getDeviceTokenFromDeliveryBoyId(idList);
                    PushNotification pushNotification = new PushNotification();
                    pushNotification.setTokens(deviceTokens);
                    pushNotification.setMessage(MessageBundle.getPushNotificationMsg("PN001"));
                    pushNotification.setPushNotificationRedirect(PushNotificationRedirect.ORDER);
                    pushNotification.setExtraDetail(order.getId().toString());
                    pushNotification.setNotifyTo(NotifyTo.DELIVERY_BOY);
                    PushNotificationUtil.sendNotificationToAndroidDevice(pushNotification);
                }
            } else {
                log.warn("Only order with Order Placed status can be reprocessed." + orderId);
            }
        } else {
            log.warn("Order not found during reprocessing it." + orderId);
        }
        return true;
    }

    class TotalTimeTakenComparator implements Comparator<DeliveryBoySelectionEntity> {
        @Override
        public int compare(DeliveryBoySelectionEntity ds1, DeliveryBoySelectionEntity ds2) {
            return (ds1.getTotalTimeRequired() - ds2.getTotalTimeRequired());
        }
    }

    private Boolean forceAssign(DeliveryBoySelectionEntity deliveryBoySelectionEntity) throws Exception{
        log.info("Assigning order forcefully to shoppers");
        deliveryBoySelectionEntity.setAccepted(true);
        DeliveryBoyEntity deliveryBoyEntity = deliveryBoySelectionEntity.getDeliveryBoy();
        deliveryBoyEntity.setActiveOrderNo(deliveryBoyEntity.getActiveOrderNo()+1);
        deliveryBoyEntity.setTotalOrderTaken(deliveryBoyEntity.getTotalOrderTaken()+1);
        deliveryBoyEntity.setAvailabilityStatus(DBoyStatus.BUSY);

        OrderEntity orderEntity = deliveryBoySelectionEntity.getOrder();
        orderEntity.setForceAssigned(true);
        orderEntity.setRemainingTime(deliveryBoySelectionEntity.getTotalTimeRequired());
        orderEntity.setDeliveryBoy(deliveryBoyEntity);
        orderEntity.setAssignedTime(deliveryBoySelectionEntity.getTimeRequired());
        orderEntity.setSystemChargeableDistance(deliveryBoySelectionEntity.getDistanceToStore());
        orderEntity.setOrderStatus(JobOrderStatus.ORDER_ACCEPTED);
        MerchantEntity merchant = merchantDaoService.getMerchantByOrderId(orderEntity.getId());
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

        List<DBoyOrderHistoryEntity> dBoyOrderHistoryEntities = orderEntity.getdBoyOrderHistories();
        dBoyOrderHistoryEntities.removeAll(dBoyOrderHistoryEntities);
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
            UserDeviceEntity userDevice = userDeviceDaoService.getUserDeviceInfoFromOrderId(orderEntity.getId());
            String message = MessageBundle.getPushNotificationMsg("CPN001");
            message = String.format(message, orderEntity.getStore().getName(), deliveryBoyEntity.getUser().getFullName());
            String extraDetail = orderEntity.getId().toString()+"/status/"+JobOrderStatus.ORDER_ACCEPTED.toString();
            PushNotificationUtil.sendPushNotification(userDevice, message, NotifyTo.CUSTOMER, PushNotificationRedirect.ORDER, extraDetail);
            /*
            * if email subscription is set true
            * send the email containing order detail to the contact person of the store
            * */

            if (orderEntity.getStore().getSendEmail() != null && orderEntity.getStore().getSendEmail()){
                String subject = "New order has been placed : "+orderEntity.getId();
                String body = EmailMsg.orderPlaced(orderEntity.getStore().getContactPerson(), "http://ktm.koolkat.in/", orderEntity);
                sendMail(orderEntity.getStore().getEmail(), body, subject);
            }
        }
        return status;
    }

    @Override
    public Boolean cancelOrder(OrderEntity order) throws Exception {
        if(order.getOrderCancel() == null){
            String CANCEL_REASON = "Unable to process your order";
            OrderCancelEntity orderCancel = new OrderCancelEntity();
            orderCancel.setJobOrderStatus(order.getOrderStatus());
            orderCancel.setReason(CANCEL_REASON);
            orderCancel.setOrder(order);
            orderCancel.setCancelledDate(DateUtil.getCurrentTimestampSQL());
            order.setOrderCancel(orderCancel);
        }
        order.setOrderStatus(JobOrderStatus.CANCELLED);
        order.setDeliveryStatus(DeliveryStatus.CANCELLED);
        BigDecimal customerWalletAmount = order.getCustomer().getWalletAmount();
        if(order.getPaymentMode().equals(PaymentMode.WALLET)){
            String currency = systemPropertyService.readPrefValue(PreferenceType.CURRENCY);
            BigDecimal paidFromWallet = order.getPaidFromWallet();
            customerWalletAmount = customerWalletAmount.add(paidFromWallet);

            String remarks = MessageBundle.getMessage("WTM002", "push_notification.properties");
            remarks = String.format(remarks, currency, paidFromWallet, order.getId());
            this.setWalletTransaction(order, paidFromWallet, AccountType.CREDIT, PaymentMode.WALLET, remarks, customerWalletAmount);
            order.getCustomer().setWalletAmount(customerWalletAmount);
            order.setPaidFromWallet(BigDecimal.ZERO);
        }
        boolean status = orderDaoService.update(order);
        if(status){
            if(order.getPaymentMode().equals(PaymentMode.WALLET))
                adjustWalletBalanceForPendingOrders(customerWalletAmount, order.getCustomer().getId());
            UserDeviceEntity userDevice = userDeviceDaoService.getUserDeviceInfoFromOrderId(order.getId());
            String message = MessageBundle.getMessage("CPN007", "push_notification.properties");
            String extraDetail = order.getId().toString()+"/status/"+order.getOrderStatus().toString();
            PushNotificationUtil.sendPushNotification(userDevice, message, NotifyTo.CUSTOMER, PushNotificationRedirect.ORDER, extraDetail);

            List<String> deviceTokens = userDeviceDaoService.getDeviceTokensOfAssignedDeliveryBoy(order.getId());
            if(deviceTokens.size() > 0){
                PushNotification pushNotification = new PushNotification();
                pushNotification.setTokens(deviceTokens);
                pushNotification.setMessage(MessageBundle.getPushNotificationMsg("PN003"));
                pushNotification.setPushNotificationRedirect(PushNotificationRedirect.ORDER);
                pushNotification.setExtraDetail(order.getId().toString()+"/status/"+JobOrderStatus.CANCELLED.toString());
                pushNotification.setNotifyTo(NotifyTo.DELIVERY_BOY);
                PushNotificationUtil.sendNotificationToAndroidDevice(pushNotification);
            }
        }
        return status;
    }

    private void adjustWalletBalanceForPendingOrders(BigDecimal customerWalletAmount, Integer customerId) throws Exception{
        log.info("looking for next orders whose paidToCOD > 0 and paymentMode is wallet for customer with ID:"+customerId);
        List<OrderEntity> orderList = orderDaoService.getAllWalletUnpaidOrdersOfCustomer(customerId);
        String currency = systemPropertyService.readPrefValue(PreferenceType.CURRENCY);
        for(OrderEntity o: orderList){
            if(BigDecimalUtil.isGreaterThen(customerWalletAmount, BigDecimal.ZERO)){
                /* Customer wallet amount is less than order amount to be paid at customer during cash on delivery */
                if(BigDecimalUtil.isGreaterThenOrEqualTo(o.getPaidFromCOD(), customerWalletAmount)){
                    log.info("Customer wallet amount is less than order amount to be paid at customer during cash on delivery order ID:"+o.getId()+"Wallet Amount:"+customerWalletAmount+" Paid from COD:"+o.getPaidFromCOD());
                    String remarks = MessageBundle.getMessage("WTM003", "push_notification.properties");
                    remarks = String.format(remarks, currency, customerWalletAmount, o.getId());
                    this.setWalletTransaction(o, customerWalletAmount, AccountType.DEBIT, PaymentMode.WALLET, remarks, BigDecimal.ZERO);

                    o.setPaidFromCOD(o.getPaidFromCOD().subtract(customerWalletAmount));
                    o.setPaidFromWallet(o.getPaidFromWallet().add(customerWalletAmount));
                    customerWalletAmount = BigDecimal.ZERO;
                    o.getCustomer().setWalletAmount(customerWalletAmount);
                }else{
                    log.info("Customer wallet amount is greater than order amount to be paid at customer during cash on delivery order ID:"+o.getId()+"Wallet Amount:"+customerWalletAmount+" Paid from COD:"+o.getPaidFromCOD());
                    String remarks = MessageBundle.getMessage("WTM003", "push_notification.properties");
                    remarks = String.format(remarks, currency, o.getPaidFromCOD(), o.getId());
                    this.setWalletTransaction(o, o.getPaidFromCOD(), AccountType.DEBIT, PaymentMode.WALLET, remarks, customerWalletAmount.subtract(o.getPaidFromCOD()));

                    o.setPaidFromWallet(o.getPaidFromWallet().add(o.getPaidFromCOD()));
                    customerWalletAmount = customerWalletAmount.subtract(o.getPaidFromCOD());
                    o.setPaidFromCOD(BigDecimal.ZERO);
                    o.getCustomer().setWalletAmount(customerWalletAmount);
                }
                orderDaoService.update(o);
            }else
                break;
        }
    }

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

    private void setCustomerWalletTransaction(CustomerEntity customer, BigDecimal amount, AccountType accountType, PaymentMode paymentMode, String remarks, BigDecimal availableAmount) throws Exception{
        log.info("Setting customer wallet transaction info for customer:"+customer.getId()+" Amount:"+amount+" Account type:"+accountType+" Remarks:"+remarks);
        List<WalletTransactionEntity> walletTransactionEntities = customer.getWalletTransactions();
        WalletTransactionEntity walletTransactionEntity = new WalletTransactionEntity();
        walletTransactionEntity.setTransactionDate(DateUtil.getCurrentTimestampSQL());
        walletTransactionEntity.setAccountType(accountType);
        walletTransactionEntity.setRemarks(remarks);
        walletTransactionEntity.setTransactionAmount(amount);
        walletTransactionEntity.setCustomer(customer);
        walletTransactionEntity.setPaymentMode(paymentMode);
        walletTransactionEntity.setAvailableWalletAmount(BigDecimalUtil.checkNull(availableAmount));
        systemAlgorithmService.encodeWalletTransaction(walletTransactionEntity);
        walletTransactionEntities.add(walletTransactionEntity);
        customer.setWalletTransactions(walletTransactionEntities);

        UserDeviceEntity userDevice = userDeviceDaoService.getUserDeviceInfoFromCustomerId(customer.getId());
        String extraDetail = "";
        PushNotificationUtil.sendPushNotification(userDevice, remarks, NotifyTo.CUSTOMER, PushNotificationRedirect.TRANSACTION, extraDetail);
    }

    /* This method should be removed later */
    @Override
    public Boolean refillWallet(CustomerEntity customer) throws Exception {
        String remarks = MessageBundle.getMessage("WTM004", "push_notification.properties");
        String currency = systemPropertyService.readPrefValue(PreferenceType.CURRENCY);
        remarks = String.format(remarks, currency, customer.getWalletAmount());
        return refillCustomerWallet(customer.getFacebookId(), customer.getWalletAmount(), remarks, true);
    }

    @Override
    public Boolean refillCustomerWallet(Long facebookId, BigDecimal refillAmount, String remark, Boolean isTransfer) throws Exception {
        CustomerEntity customerEntity = customerDaoService.find(facebookId);
        if(customerEntity == null){
            throw new YSException("VLD011");
        }
        customerEntity.setWalletAmount(BigDecimalUtil.checkNull(customerEntity.getWalletAmount()).add(refillAmount));
        this.setCustomerWalletTransaction(customerEntity, refillAmount, AccountType.CREDIT, PaymentMode.WALLET, remark, customerEntity.getWalletAmount());
        /* Since bonus is given in transferred amount */
        if(isTransfer){
           BigDecimal bonusAmount = BigDecimalUtil.percentageOf(refillAmount, new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.TRANSFER_BONUS_PERCENT)));
           if(BigDecimalUtil.isGreaterThen(bonusAmount, BigDecimal.ZERO)){
               String bonusRemark = MessageBundle.getPushNotificationMsg("WTM011");
               String currency = systemPropertyService.readPrefValue(PreferenceType.CURRENCY);
               bonusRemark = String.format(bonusRemark, currency, bonusAmount);
               customerEntity.setWalletAmount(BigDecimalUtil.checkNull(customerEntity.getWalletAmount()).add(bonusAmount));
               this.setCustomerWalletTransaction(customerEntity, bonusAmount, AccountType.CREDIT, PaymentMode.WALLET, bonusRemark, customerEntity.getWalletAmount());
           }
        }
        boolean status = customerDaoService.update(customerEntity);

        BigDecimal customerWalletAmount = customerEntity.getWalletAmount();
        BigDecimal customerShortFallAmount = BigDecimalUtil.checkNull(customerEntity.getShortFallAmount());

        /* Short Fall amount adjustments */
        if(BigDecimalUtil.isGreaterThen(customerShortFallAmount, BigDecimal.ZERO)){
            log.info("looking for shortfall orders whose shortFallAmount > 0 for customer with ID:"+customerEntity.getId());
            String currency = systemPropertyService.readPrefValue(PreferenceType.CURRENCY);
            List<OrderEntity> orderList = orderDaoService.getAllShortFallOrdersOfCustomer(customerEntity.getId());
            for(OrderEntity o: orderList){
                if(BigDecimalUtil.isGreaterThen(customerWalletAmount, BigDecimal.ZERO)){
                    /* Customer wallet amount is less than order amount to be paid at customer during cash on delivery */
                    if(BigDecimalUtil.isGreaterThen(o.getShortFallAmount(), customerWalletAmount)){
                        log.info("Customer wallet amount is less than short fall amount for order ID:"+o.getId());
                        String remarks = MessageBundle.getMessage("WTM003", "push_notification.properties");
                        remarks = String.format(remarks, currency, customerWalletAmount, o.getId());
                        this.setWalletTransaction(o, customerWalletAmount, AccountType.DEBIT, PaymentMode.WALLET, remarks, BigDecimal.ZERO);

                        customerShortFallAmount = customerShortFallAmount.subtract(customerWalletAmount);
                        o.getCustomer().setShortFallAmount(customerShortFallAmount);
                        o.setShortFallAmount(o.getShortFallAmount().subtract(customerWalletAmount));
                        customerWalletAmount = BigDecimal.ZERO;
                        o.getCustomer().setWalletAmount(customerWalletAmount);
                    }else{
                        log.info("Customer wallet amount is greater than short fall amount for order ID:"+o.getId());
                        String remarks = MessageBundle.getMessage("WTM003", "push_notification.properties");
                        remarks = String.format(remarks, currency, o.getShortFallAmount(), o.getId());
                        this.setWalletTransaction(o, o.getShortFallAmount(), AccountType.DEBIT, PaymentMode.WALLET, remarks, customerWalletAmount.subtract(o.getShortFallAmount()));

                        customerWalletAmount = customerWalletAmount.subtract(o.getShortFallAmount());
                        customerShortFallAmount = customerShortFallAmount.subtract(o.getShortFallAmount());
                        o.setShortFallAmount(BigDecimal.ZERO);
                        o.getCustomer().setShortFallAmount(customerShortFallAmount);
                        o.getCustomer().setWalletAmount(customerWalletAmount);
                    }
                    orderDaoService.update(o);
                }else
                    break;
            }
        }
        /*Running wallet order adjustments*/
        this.adjustWalletBalanceForPendingOrders(customerWalletAmount, customerEntity.getId());
        return status;
    }

    private BigDecimal getCustomerWalletBalance(Long facebookId) throws Exception{
        CustomerEntity customerEntity = customerDaoService.getWalletInfo(facebookId);
        validateAvailableWalletAmount(BigDecimalUtil.checkNull(customerEntity.getWalletAmount()), customerEntity.getId());
        return BigDecimalUtil.checkNull(customerEntity.getWalletAmount()).subtract(BigDecimalUtil.checkNull(customerEntity.getShortFallAmount()));
    }

    private void validateAvailableWalletAmount(BigDecimal availableAmount, Integer customerId) throws Exception{
        WalletTransactionEntity walletTransactionEntity = walletTransactionDaoService.getLatestWalletTransaction(customerId);
        if(walletTransactionEntity == null){
            if(BigDecimalUtil.isNotEqualTo(availableAmount, BigDecimal.ZERO)){
                log.info("Customer available amount has no transactions and should be zero:"+availableAmount);
                throw new YSException("SEC012", "#" + systemPropertyService.readPrefValue(PreferenceType.HELPLINE_NUMBER));
            }
            log.info("Customer available amount has been validated:"+availableAmount);
        }else{
            systemAlgorithmService.decodeWalletTransaction(walletTransactionEntity);
            if(!walletTransactionEntity.getFlag()){
                log.info("Signature validation failed");
               // throw new YSException("SEC012", "#" + systemPropertyService.readPrefValue(PreferenceType.HELPLINE_NUMBER));
            }
            if(BigDecimalUtil.isNotEqualTo(availableAmount, walletTransactionEntity.getAvailableWalletAmount())){
                log.info("Customer available amount is different than that in signature");
                //throw new YSException("SEC012", "#" + systemPropertyService.readPrefValue(PreferenceType.HELPLINE_NUMBER));
            }
        }
    }

    @Override
    public PaginationDto getWalletTransactions(Page page, Long facebookId) throws Exception {
        log.info("Retrieving list of wallet transactions");
        List<PaymentMode> paymentModes = new ArrayList<PaymentMode>();

        if (page != null && page.getPaymentModes() != null) {
            paymentModes = page.getPaymentModes();
        }else{
            paymentModes.add(PaymentMode.CASH_ON_DELIVERY);
            paymentModes.add(PaymentMode.WALLET);
        }
        PaginationDto paginationDto = new PaginationDto();
        Integer totalRows = walletTransactionDaoService.getTotalNumberOfWalletTransactions(facebookId, paymentModes);
        paginationDto.setNumberOfRows(totalRows);
        List<WalletTransactionEntity> walletTransactionEntities;
        if(totalRows > 0){
            if(page != null){
                page.setTotalRows(totalRows);
            }
            walletTransactionEntities = walletTransactionDaoService.getWalletTransactions(page, facebookId, paymentModes);
        }else{
            walletTransactionEntities = new ArrayList<WalletTransactionEntity>();
        }
        paginationDto.setData(walletTransactionEntities);
        paginationDto.setCurrency(systemPropertyService.readPrefValue(PreferenceType.CURRENCY));
        return paginationDto;
    }

    @Override
    public PaymentGatewayDto requestToAddFundToWallet(Long facebookId, BigDecimal amount) throws Exception{
        log.info("Request to add fund of "+amount + " from customer "+facebookId);
        CustomerEntity customerEntity = customerDaoService.find(facebookId);
        if(customerEntity == null){
            throw new YSException("VLD011");
        }
        PaymentGatewayInfoEntity paymentGatewayInfoEntity = new PaymentGatewayInfoEntity();
        BigDecimal minimumTransferableAmount = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.MINIMUM_TRANSFERABLE_AMOUNT));
        if(BigDecimalUtil.isLessThen(BigDecimalUtil.checkNull(amount), minimumTransferableAmount)){
            String currency = systemPropertyService.readPrefValue(PreferenceType.CURRENCY);
            throw new YSException("VLD038", currency +". "+minimumTransferableAmount);
        }
        BigDecimal conversionRate = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.INR_CONVERSION_RATE));
        paymentGatewayInfoEntity.setAmount(amount);
        paymentGatewayInfoEntity.setInrAmount(BigDecimalUtil.convertToINRPaisa(amount, conversionRate));
        paymentGatewayInfoEntity.setCurrencyCode(MessageBundle.getPaymentGatewayMsg("currencyCode"));
        paymentGatewayInfoEntity.setTransactionReference(System.currentTimeMillis()+"");
        paymentGatewayInfoEntity.setFlag(false);
        paymentGatewayInfoEntity.setCustomer(customerEntity);
        paymentGatewayInfoDaoService.save(paymentGatewayInfoEntity);
        return SHAEncoder.getPaymentRequestData(paymentGatewayInfoEntity);
    }

    @Override
    public String paymentGatewaySettlement(PaymentGatewayDto paymentGatewayDto) throws Exception {
        Properties properties = GeneralUtil.parsePropertiesString(paymentGatewayDto.getData(), "|");
        Integer transactionId = Integer.parseInt(properties.getProperty(SHAEncoder.ORDER_ID_NAME));
        PaymentGatewayInfoEntity paymentGatewayInfoEntity = paymentGatewayInfoDaoService.find(transactionId);
        if(paymentGatewayInfoEntity == null){
            log.warn("Invalid Data:"+paymentGatewayDto.getData());
            throw new YSException("SEC015");
        }
        if(!paymentGatewayDto.getSeal().equals(SHAEncoder.computeSeal(paymentGatewayDto.getData()))){
            log.warn("Invalid Data Seal:"+paymentGatewayDto.getData());
            throw new YSException("SEC016");
        }
        if(paymentGatewayInfoEntity.getFlag()){
            log.warn("Transactions already processed:"+paymentGatewayDto.getData());
            return this.getHTMLMessage(paymentGatewayInfoEntity.getResponseCode(), paymentGatewayInfoEntity.getCustomer().getId());
            //throw new YSException("SEC017");
        }
        String transactionReference = properties.getProperty(SHAEncoder.TRANSACTION_REFERENCE_NAME);
        BigDecimal inrAmount = new BigDecimal(properties.getProperty(SHAEncoder.AMOUNT_NAME));
        String currencyCode = properties.getProperty(SHAEncoder.CURRENCY_CODE_NAME);

        if(!paymentGatewayInfoEntity.getTransactionReference().equals(transactionReference) ||
                BigDecimalUtil.isNotEqualTo(paymentGatewayInfoEntity.getInrAmount(), inrAmount) ||
                !paymentGatewayInfoEntity.getCurrencyCode().equals(currencyCode)){
            log.warn("Corrupted Data:"+paymentGatewayDto.getData());
            throw new YSException("SEC016");
        }

        String responseCode = properties.getProperty(SHAEncoder.RESPONSE_CODE);
        if(responseCode.equals(SHAEncoder.SUCCESSFUL_RESPONSE_CODE)){
            String remarks = MessageBundle.getMessage("WTM004", "push_notification.properties");
            String currency = systemPropertyService.readPrefValue(PreferenceType.CURRENCY);
            remarks = String.format(remarks, currency, paymentGatewayInfoEntity.getAmount());
            this.refillCustomerWallet(paymentGatewayInfoEntity.getCustomer().getFacebookId(), paymentGatewayInfoEntity.getAmount(), remarks, true);
            paymentGatewayInfoEntity.setFlag(true);
        }else{
            String remarks = MessageBundle.getPaymentGatewayMsg(responseCode);
            UserDeviceEntity userDevice = userDeviceDaoService.getUserDeviceInfoFromCustomerId(paymentGatewayInfoEntity.getCustomer().getId());
            String extraDetail = "-1";
            PushNotificationUtil.sendPushNotification(userDevice, remarks, NotifyTo.CUSTOMER, PushNotificationRedirect.TRANSACTION, extraDetail);
        }
        paymentGatewayInfoEntity.setResponseCode(responseCode);
        paymentGatewayInfoDaoService.update(paymentGatewayInfoEntity);
        return this.getHTMLMessage(paymentGatewayInfoEntity.getResponseCode(), paymentGatewayInfoEntity.getCustomer().getId());
    }

    @Override
    public void setReferralReward(OrderEntity orderJson) throws Exception{
         /*
            * if the transaction is first transaction of  the current customer
            * set reward for referrer
            * */
        OrderEntity order = orderDaoService.find(orderJson.getId());

        List<OrderEntity> customersOrders = orderDaoService.getCustomersOrders(order.getCustomer().getId());
         if(customersOrders.size() == 1){
            Long referrerId = order.getCustomer().getReferredBy();
            if(referrerId != null){
                CustomerEntity referrer = customerDaoService.find(referrerId);
                referrer.setRewardsEarned(referrer.getRewardsEarned().add(new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.REFERRAL_REWARD_AMOUNT))));
                refillCustomerWallet(referrer.getFacebookId(), new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.REFERRAL_REWARD_AMOUNT)), MessageBundle.getMessage("WTM005", "push_notification.properties"), false);
                customerDaoService.update(referrer);
            }
        }
    }


    private String getHTMLMessage(String responseCode, Integer customerId) throws Exception{
        String remarks = MessageBundle.getPaymentGatewayMsg(responseCode);
        Boolean flag = false;
        if(responseCode.equals(SHAEncoder.SUCCESSFUL_RESPONSE_CODE))
            flag = true;
        UserDeviceEntity userDevice = userDeviceDaoService.getUserDeviceInfoFromCustomerId(customerId);
        return SHAEncoder.getResponseHTML(remarks, flag, userDevice.getFamily());
    }

    @Override
    public CustomerEntity getWalletBalance(Long facebookId) throws Exception {
        CustomerEntity customer = customerDaoService.getWalletInfo(facebookId);
        if(customer == null)
            throw new YSException("VLD011");
        customer.setWalletAmount(BigDecimalUtil.checkNull(customer.getWalletAmount()).subtract(BigDecimalUtil.checkNull(customer.getShortFallAmount())));
        customer.setShortFallAmount(null);
        customer.setCurrency(systemPropertyService.readPrefValue(PreferenceType.CURRENCY));
        return  customer;
    }
}
