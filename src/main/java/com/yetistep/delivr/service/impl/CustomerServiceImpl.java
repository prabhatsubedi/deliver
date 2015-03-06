package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.dao.inf.*;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.PaginationDto;
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
import com.yetistep.delivr.schedular.ScheduleChanger;
import com.yetistep.delivr.service.inf.CustomerService;
import com.yetistep.delivr.service.inf.SystemAlgorithmService;
import com.yetistep.delivr.service.inf.SystemPropertyService;
import com.yetistep.delivr.util.*;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

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
@Controller
public class CustomerServiceImpl implements CustomerService {
    private static final Logger log = Logger.getLogger(CustomerServiceImpl.class);
    private static final Integer ON_TIME_DELIVERY = 0;
    private static final Integer DELAYED_DELIVERY = -1;


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

            /*
            * if current login is the first login of the current customer
            * set reward for the customer
            * */
            if(registeredCustomer.getUser().getLastActivityDate() == null) {
                registeredCustomer.setRewardsEarned(new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.REFEREE_REWARD_AMOUNT)));
            }

            registeredCustomer.getUser().setLastActivityDate(MessageBundle.getCurrentTimestampSQL());

            validateUserDevice(registeredCustomer.getUser().getUserDevice());
            customerDaoService.update(registeredCustomer);

        } else {
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

//            if (customerEntity.getLatitude() != null) {
//                customerEntity.getUser().getAddresses().get(0).setLatitude(customerEntity.getLatitude());
//                customerEntity.getUser().getAddresses().get(0).setLongitude(customerEntity.getLongitude());
//            }
            //if(registeredCustomer.getUser().getLastActivityDate().equals(null)) {
            customerEntity.setRewardsEarned(new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.NORMAL_USER_BONUS_AMOUNT)));
           // }
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
            else
                tempItem.setImageUrl(systemPropertyService.readPrefValue(PreferenceType.DEFAULT_IMG_ITEM));

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
        checkOutDto.setCurrency(systemPropertyService.readPrefValue(PreferenceType.CURRENCY));
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
    public Map<String, Object> getCategoryBrands(Integer categoryId, Integer pageNo) throws Exception {
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
        return map;
    }

    @Override
    public void saveOrder(RequestJsonDto requestJson, HeaderDto headerDto) throws Exception {
        Long customerId = requestJson.getOrdersCustomerId();
        Integer addressId = requestJson.getOrdersAddressId();
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

        /* Transferring data of cart to order and calculating price */
        BigDecimal itemTotalCost = BigDecimal.ZERO;
        BigDecimal itemServiceAndVatCharge = BigDecimal.ZERO;
        List<CartEntity> cartEntities = cartDaoService.getMyCarts(customerId);
        List<Integer> cartIds = new ArrayList<Integer>();
        List<ItemsOrderEntity> itemsOrder = new ArrayList<ItemsOrderEntity>();
        Integer brandId = null;
        StoresBrandEntity storesBrand = null;
        for (CartEntity cart : cartEntities) {
            ItemsOrderEntity itemsOrderEntity = new ItemsOrderEntity();
            itemsOrderEntity.setOrder(order);
            itemsOrderEntity.setAvailabilityStatus(true);
            itemsOrderEntity.setQuantity(cart.getOrderQuantity());
            itemsOrderEntity.setCustomerNote(cart.getNote());
            itemsOrderEntity.setItem(cart.getItem());
            itemsOrderEntity.setServiceCharge(BigDecimalUtil.checkNull(cart.getItem().getServiceCharge()));
            itemsOrderEntity.setVat(BigDecimalUtil.checkNull(cart.getItem().getVat()));
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

            BigDecimal serviceCharge = BigDecimalUtil.percentageOf(itemPrice, BigDecimalUtil.checkNull(cart.getItem().getServiceCharge()));
            BigDecimal serviceAndVatCharge = serviceCharge.add(BigDecimalUtil.percentageOf(itemPrice.add(serviceCharge), BigDecimalUtil.checkNull(cart.getItem().getVat())));
            /*Set for order total and total serviceVat*/
            itemServiceAndVatCharge = itemServiceAndVatCharge.add(serviceAndVatCharge);
            itemTotalCost = itemTotalCost.add(itemPrice);

            itemsOrderEntity.setItemTotal(itemPrice);
            itemsOrderEntity.setServiceAndVatCharge(serviceAndVatCharge);
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
        order.setTotalCost(itemTotalCost);
        order.setSurgeFactor(getSurgeFactor());
        order.setItemServiceAndVatCharge(itemServiceAndVatCharge);

        /* Listing Active stores of a store brand and finding shortest store */
        List<StoreEntity> stores = merchantDaoService.findActiveStoresByBrand(brandId);
        if(stores.size() == 0){
            throw new YSException("VLD024");
        }
        StoreEntity store = findNearestStoreFromCustomer(order, stores);
        order.setStore(store);
        order.setOrderName(store.getName() + " to " + order.getAddress().getStreet());
        order.setOrderVerificationCode(GeneralUtil.generateMobileCode());

        /* Finding delivery boys based on active status. */
        List<DeliveryBoyEntity> availableAndActiveDBoys = deliveryBoyDaoService.findAllCapableDeliveryBoys();
        if(availableAndActiveDBoys.size() == 0){
            log.info("No delivery boys available");
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

        Float timeInSeconds = Float.parseFloat(systemPropertyService.readPrefValue(PreferenceType.ORDER_REQUEST_TIMEOUT_IN_MIN)) * 60;
        Integer timeOut = timeInSeconds.intValue();
        scheduleChanger.scheduleTask(DateUtil.findDelayDifference(DateUtil.getCurrentTimestampSQL(), timeOut));
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
        order.setCustomerChargeableDistance(BigDecimalUtil.getDistanceInKiloMeters(actualDistance));
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
            deliveryBoySelectionEntity.setRejected(false);
            selectionDetails.add(deliveryBoySelectionEntity);
            log.info("Delivery boys selected from distance calculation:"+selectionDetails.toString());
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

    private List<DeliveryBoySelectionEntity> filterDBoyWithProfitCriteria(OrderEntity order, List<DeliveryBoySelectionEntity> deliveryBoySelectionEntities, BigDecimal merchantCommission, BigDecimal merchantServiceFee) throws Exception {
        log.info("Unfiltered Dboys:"+deliveryBoySelectionEntities.toString());
        List<DeliveryBoySelectionEntity> filteredDeliveryBoys = new ArrayList<DeliveryBoySelectionEntity>();
        BigDecimal MINIMUM_PROFIT_PERCENTAGE = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.MINIMUM_PROFIT_PERCENTAGE));
        for (DeliveryBoySelectionEntity deliveryBoySelectionEntity : deliveryBoySelectionEntities) {
            CourierTransactionEntity courierTransaction = systemAlgorithmService.getCourierTransaction(order, deliveryBoySelectionEntity, merchantCommission, merchantServiceFee);
            if(BigDecimalUtil.isGreaterThen(courierTransaction.getProfit(), BigDecimalUtil.percentageOf(order.getTotalCost(), MINIMUM_PROFIT_PERCENTAGE)))
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
        //referrer.setRewardsEarned(referrer.getRewardsEarned().add(new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.REFEREE_REWARD_AMOUNT))));
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
        //user.getCustomer().setRewardsEarned(new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.REFEREE_REWARD_AMOUNT)));
        userDaoService.save(user);
    }

    @Override
    public List<RatingReason> getRatingReasons() throws Exception {
        List<RatingReason> reasonList =
                new ArrayList<RatingReason>(EnumSet.allOf(RatingReason.class));
        return reasonList;
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


        List<ItemEntity> items = itemDaoService.searchItems(word);
        if(items !=null && items.size() > 0){
            searchResult = new ArrayList<>();
            //Search Nearest Stores for Address
            for(Iterator<ItemEntity> iterator = items.iterator(); iterator.hasNext();){
                ItemEntity item = iterator.next();

                SearchDto tempItem = new SearchDto();
                List<StoreEntity> storeEntities = storeDaoService.findStores(item.getBrandId());
                if(storeEntities.size() == 0) { //If all Stores are inactivated
                    iterator.remove();
                    continue;
                }

                //Add Default Image If Image not at item
                if(item.getImageUrl() == null)
                    item.setImageUrl(systemPropertyService.readPrefValue(PreferenceType.DEFAULT_IMG_ITEM));

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

        //Now Goes To Search Brand
        Integer limit = 0;
        if(totalItemSize > CommonConstants.MAX_SEARCH_ITEM)
            limit = CommonConstants.MAX_SEARCH_STORE;
        else if(totalItemSize > 0)
            limit = CommonConstants.MAX_SEARCH_DATA - totalItemSize;
        else if(totalItemSize==0)
            limit = CommonConstants.MAX_SEARCH_DATA;

        List<Integer> brandIds = storesBrandDaoService.getSearchBrands(word, limit);

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
                if(item.getImageUrl() == null)
                    item.setImageUrl(systemPropertyService.readPrefValue(PreferenceType.DEFAULT_IMG_ITEM));

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
                tempItem.setStoreStreet(storeEntities.get(0).getStreet());
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
        DBoyOrderHistoryEntity dBoyOrderHistoryEntity = dBoyOrderHistoryDaoService.getOrderHistory(orderId, order.getDeliveryBoy().getId());
        Integer assignedTime = order.getAssignedTime();
        Double deliveredTime = DateUtil.getMinDiff(dBoyOrderHistoryEntity.getOrderAcceptedAt().getTime(), dBoyOrderHistoryEntity.getOrderCompletedAt().getTime());
        Integer remainingTime = (assignedTime + Integer.valueOf(systemPropertyService.readPrefValue(PreferenceType.DBOY_GRESS_TIME))) - deliveredTime.intValue();
        log.info("+++++++ Assigned Time " + assignedTime + " & Delivered Time " + deliveredTime + " +++++++++");
        Integer dboyRating = rating.getDeliveryBoyRating();
        if(remainingTime < 0)
            dboyRating = dboyRating - DELAYED_DELIVERY;

        // Ended By Surendra

        ratingEntity.setCustomerComment(rating.getCustomerComment());
        ratingEntity.setDeliveryBoyRating(dboyRating);
        ratingEntity.setRatingIssues(rating.getRatingIssues());
        orderDaoService.update(order);

        /* Now Calculate Average Rating (Appended By Surendra) */
//        DeliveryBoyEntity deliveryBoy = order.getDeliveryBoy();
        List<Integer> ratings = orderDaoService.getDboyRatings(order.getDeliveryBoy().getId());
        if(ratings !=null && ratings.size()> 0){
            BigDecimal totalRate = BigDecimal.ZERO;

            for(Integer rate : ratings){
                totalRate = totalRate.add(new BigDecimal(rate));
            }

            BigDecimal averageRating = getAverageRating(ratings.size(), totalRate);
            deliveryBoyDaoService.updateAverageRating(averageRating, order.getDeliveryBoy().getId());

        /* Less then or equal 1 means Current Delivery Also In Session (That has not completed) */
            if(orderDaoService.hasDboyRunningOrders(order.getDeliveryBoy().getId()) <= 1){
                if(BigDecimalUtil.isLessThen(averageRating, new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DBOY_DEFAULT_RATING)))) {
                    //Deactivate User
                    log.info("Deactivating Delivery Boy id : " + order.getDeliveryBoy().getId());
                    userDaoService.deactivateUser(order.getDeliveryBoy().getUser().getId());
                }

            }

        }

        /* Ended By Surendra */

        return true;
    }

    private BigDecimal getAverageRating(Integer totalRate, BigDecimal totalRateSum) throws Exception {
        BigDecimal averageRating;
        if(totalRate<= 10 && totalRate > 0){
            Integer rate = Integer.valueOf(totalRateSum.intValue()) + Integer.parseInt(systemPropertyService.readPrefValue(PreferenceType.DBOY_DEFAULT_RATING)) * (10-totalRate);
            averageRating = new BigDecimal(rate).divide(new BigDecimal(10), MathContext.DECIMAL128);
            averageRating = averageRating.setScale(0, BigDecimal.ROUND_HALF_UP);
        } else {
            averageRating = totalRateSum.divide(new BigDecimal(totalRate), MathContext.DECIMAL128);
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
        OrderEntity order = orderDaoService.find(orderId);
        if(order != null){
            if(order.getOrderStatus().equals(JobOrderStatus.ORDER_PLACED)){
                if(order.getReprocessTime() > 0){
                    log.info("Order is cancelling since it has been reprocessed already:"+orderId);
                    ReasonDetailsEntity reasonDetailsEntity = reasonDetailsDaoService.find(5);
                    return cancelOrder(order, reasonDetailsEntity);
                }
                order.setReprocessTime(GeneralUtil.ifNullToZero(order.getReprocessTime())+1);
                order.setOrderDate(DateUtil.getCurrentTimestampSQL());
                deliveryBoySelectionDaoService.updateAllSelectionToRejectMode(orderId);
                /* Finding delivery boys based on active status. */
                List<DeliveryBoyEntity> availableAndActiveDBoys = deliveryBoyDaoService.findAllCapableDeliveryBoys();
                if(availableAndActiveDBoys.size() == 0){
                    log.info("Order is cancelling since number of available delivery boys is zero:"+orderId);
                    ReasonDetailsEntity reasonDetailsEntity = reasonDetailsDaoService.find(5);
                    return cancelOrder(order, reasonDetailsEntity);
                }
                CourierTransactionEntity courierTransactionEntity = order.getCourierTransaction();
                 /* Selects nearest delivery boys based on timing. */
                List<DeliveryBoySelectionEntity> deliveryBoySelectionEntities = calculateStoreToDeliveryBoyDistance(order.getStore(), availableAndActiveDBoys, order, true);
                /* Selects delivery boys based on profit criteria. */
                List<DeliveryBoySelectionEntity> deliveryBoySelectionEntitiesWithProfit =  filterDBoyWithProfitCriteria(order, deliveryBoySelectionEntities, courierTransactionEntity.getCommissionPct(), courierTransactionEntity.getServiceFeePct());
                if(deliveryBoySelectionEntitiesWithProfit.size() == 0){
                    log.info("Order is cancelling since not enough profit:"+orderId);
                    ReasonDetailsEntity reasonDetailsEntity = reasonDetailsDaoService.find(5);
                    return cancelOrder(order, reasonDetailsEntity);
                }
                order.setDeliveryBoySelections(deliveryBoySelectionEntitiesWithProfit);

                orderDaoService.update(order);

                /* Push Notifications */
                List<Integer> idList = getIdOfDeliveryBoys(deliveryBoySelectionEntitiesWithProfit);
                log.info("List of delivery boy with push notification:");
                for(Integer id: idList){
                    log.info(id);
                }
                List<String> deviceTokens = userDeviceDaoService.getDeviceTokenFromDeliveryBoyId(idList);
                PushNotification pushNotification = new PushNotification();
                pushNotification.setTokens(deviceTokens);
                pushNotification.setMessage(MessageBundle.getPushNotificationMsg("PN001"));
                pushNotification.setPushNotificationRedirect(PushNotificationRedirect.ORDER);
                pushNotification.setExtraDetail(order.getId().toString());
                pushNotification.setNotifyTo(NotifyTo.DELIVERY_BOY);
                PushNotificationUtil.sendNotificationToAndroidDevice(pushNotification);

                Float timeInSeconds = Float.parseFloat(systemPropertyService.readPrefValue(PreferenceType.ORDER_REQUEST_TIMEOUT_IN_MIN)) * 60;
                Integer timeOut = timeInSeconds.intValue();
                scheduleChanger.scheduleTask(DateUtil.findDelayDifference(DateUtil.getCurrentTimestampSQL(), timeOut));
            }else{
                log.warn("Only order with Order Placed status can be reprocessed."+orderId);
            }
        }else{
            log.warn("Order not found during reprocessing it."+orderId);
        }
        return true;
    }

    @Override
    public Boolean cancelOrder(OrderEntity order, ReasonDetailsEntity reasonDetailsEntity) throws Exception {
        OrderCancelEntity orderCancel = new OrderCancelEntity();
        orderCancel.setReason(reasonDetailsEntity.getCancelReason());
        orderCancel.setReasonDetails(reasonDetailsEntity);
        orderCancel.setJobOrderStatus(order.getOrderStatus());
        orderCancel.setOrder(order);
        order.setOrderCancel(orderCancel);
        order.setOrderStatus(JobOrderStatus.CANCELLED);
        boolean status = orderDaoService.update(order);
        if(status){
            UserDeviceEntity userDevice = userDeviceDaoService.getUserDeviceInfoFromOrderId(order.getId());
            String message = MessageBundle.getMessage("CPN007", "push_notification.properties");
            String extraDetail = order.getId().toString()+"/status/"+order.getOrderStatus().toString();
            PushNotificationUtil.sendPushNotification(userDevice, message, NotifyTo.CUSTOMER, PushNotificationRedirect.ORDER, extraDetail);
        }
        return status;
    }
}
