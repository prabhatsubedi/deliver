package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.dao.inf.CustomerDaoService;
import com.yetistep.delivr.dao.inf.DeliveryBoyDaoService;
import com.yetistep.delivr.dao.inf.MerchantDaoService;
import com.yetistep.delivr.dao.inf.UserDaoService;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.enums.DeliveryStatus;
import com.yetistep.delivr.enums.JobOrderStatus;
import com.yetistep.delivr.enums.Role;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.service.inf.CustomerService;
import com.yetistep.delivr.service.inf.SystemPropertyService;
import com.yetistep.delivr.util.BigDecimalUtil;
import com.yetistep.delivr.util.GeneralUtil;
import com.yetistep.delivr.util.GeoCodingUtil;
import com.yetistep.delivr.util.YSException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
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

    @Override
    public void saveCustomer(CustomerEntity customer, HeaderDto headerDto) throws Exception {
        customer.getUser().setUsername(headerDto.getUsername());
        customer.getUser().setPassword(headerDto.getPassword());
        UserEntity userEntity = customer.getUser();
        if (userEntity.getUsername() == null || userEntity.getPassword() == null || userEntity.getUsername().isEmpty() || userEntity.getPassword().isEmpty())
            throw new YSException("VLD009");

        /* Setting Default value of customer while sign up. */
        RoleEntity userRole = userDaoService.getRoleByRole(Role.ROLE_CUSTOMER);
        userEntity.setRole(userRole);
        userEntity.setPassword(GeneralUtil.encryptPassword(userEntity.getPassword()));
        userEntity.setMobileNumber(userEntity.getUsername());
        userEntity.setMobileVerificationStatus(false);
        userEntity.setVerifiedStatus(false);
        userEntity.setBlacklistStatus(false);
        userEntity.setSubscribeNewsletter(false);

        customer.setTotalOrderPlaced(0);
        customer.setTotalOrderDelivered(0);
        customer.setFriendsInvitationCount(0);
        customer.setReferredFriendsCount(0);
        customerDaoService.save(customer);

        String verifyCode = GeneralUtil.generateMobileCode();
        customer.getUser().setVerificationCode(verifyCode);
        customerDaoService.update(customer);

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
    public void setMobileCode(HeaderDto headerDto) throws Exception {
        UserEntity userEntity = userDaoService.findByUserName(headerDto.getUsername());
        if (userEntity == null)
            throw new YSException("VLD011");
        if(!headerDto.getVerificationCode().equals(userEntity.getVerificationCode())){
            throw new YSException("SEC008");
        }
        userEntity.setVerifiedStatus(true);
        userEntity.setMobileVerificationStatus(true);
        userDaoService.update(userEntity);
    }

    @Override
    public void saveOrder(RequestJsonDto requestJson, HeaderDto headerDto) throws Exception {
        OrderEntity order = requestJson.getOrdersOrder();
        List<ItemsOrderEntity> itemsOrder = requestJson.getOrdersItemsOrder();
        Integer brandId = requestJson.getOrdersBrandId();
        Integer customerId = requestJson.getOrdersCustomerId();
        Integer addressId = requestJson.getOrdersAddressId();

        StoresBrandEntity brand = merchantDaoService.findBrandDetail(brandId);
        CustomerEntity customer = customerDaoService.find(customerId);
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


        order.setAddress(address);
        order.setCustomer(customer);

        order.setOrderVerificationStatus(false);
        order.setDeliveryStatus(DeliveryStatus.UNSUCCESSFUL);
        order.setOrderStatus(JobOrderStatus.ORDER_ACCEPTED);

        for (ItemsOrderEntity iOrder: itemsOrder){
            ItemEntity item = merchantDaoService.getItemDetail(iOrder.getItem().getId());

            iOrder.setItem(item);
            iOrder.setOrder(order);
        }

        order.setItemsOrder(itemsOrder);



        List<StoreEntity> stores = merchantDaoService.findStoreByBrand(brandId);
        StoreEntity store = findNearestStoreFromCustomer(order, stores);

        order.setStore(store);
        order.setOrderName(store.getName()+" to "+ order.getAddress().getStreet());
        order.setOrderVerificationCode(Integer.parseInt(GeneralUtil.generateMobileCode()));
        //TODO Send code message to customer
        List<DeliveryBoySelectionEntity> deliveryBoySelectionEntities = calculateStoreToDeliveryBoyDistance(store, deliveryBoyDaoService.findAllCapableDeliveryBoys(), order);
        order.setDeliveryBoySelections(deliveryBoySelectionEntities);
        customerDaoService.saveOrder(order);

    }


    private StoreEntity findNearestStoreFromCustomer(OrderEntity order, List<StoreEntity> stores) throws Exception {
        String orderAddress[] = {GeoCodingUtil.getLatLong(order.getAddress().getLatitude(), order.getAddress().getLongitude())};
        String storeAddress[] = new String[stores.size()];
        int i = 0;
        for(StoreEntity store: stores){
            storeAddress[i] = GeoCodingUtil.getLatLong(store.getLatitude(), store.getLongitude());
            i++;
        }
        List<BigDecimal> distanceList = GeoCodingUtil.getListOfDistances(orderAddress, storeAddress);
        int leastDistanceIndex = BigDecimalUtil.getMinimumIndex(distanceList);
        order.setCustomerChargeableDistance(BigDecimalUtil.getDistanceInKiloMeters(distanceList.get(leastDistanceIndex)));
        return stores.get(leastDistanceIndex);

    }

    private List<DeliveryBoySelectionEntity> calculateStoreToDeliveryBoyDistance(StoreEntity store, List<DeliveryBoyEntity> capableDeliveryBoys, OrderEntity order) throws Exception {
        String storeAddress[] = {GeoCodingUtil.getLatLong(store.getLatitude(), store.getLongitude())};
        String deliveryBoyAddress[] = new String[capableDeliveryBoys.size()];
        int i = 0;
        for (DeliveryBoyEntity deliveryBoy : capableDeliveryBoys) {
            deliveryBoyAddress[i] = GeoCodingUtil.getLatLong(deliveryBoy.getLatitude(), deliveryBoy.getLongitude());
            i++;
        }
        List<BigDecimal> distanceList = GeoCodingUtil.getListOfDistances(storeAddress, deliveryBoyAddress);
        List<DeliveryBoySelectionEntity> selectionDetails = new ArrayList<DeliveryBoySelectionEntity>();
        i = 0;
        for (BigDecimal distance : distanceList) {
            DeliveryBoySelectionEntity deliveryBoySelectionEntity = new DeliveryBoySelectionEntity();
            deliveryBoySelectionEntity.setDistanceToStore(BigDecimalUtil.getDistanceInKiloMeters(distance));
            deliveryBoySelectionEntity.setDeliveryBoy(capableDeliveryBoys.get(i));
            deliveryBoySelectionEntity.setStoreToCustomerDistance(order.getCustomerChargeableDistance());
            deliveryBoySelectionEntity.setOrder(order);
            int timeFactor = Integer.parseInt(systemPropertyService.readPrefValue(GeneralUtil.getTimeTakenFor(capableDeliveryBoys.get(i).getVehicleType())));

            Integer timeRequired = BigDecimalUtil.getDistanceInKiloMeters(distance).multiply(new BigDecimal(timeFactor)).setScale(0, RoundingMode.HALF_UP).intValue();
            deliveryBoySelectionEntity.setTimeRequired(timeRequired);
            deliveryBoySelectionEntity.setAccepted(false);
            selectionDetails.add(deliveryBoySelectionEntity);
            i++;
             //TODO Filter delivery boys by profit criteria - Push Notifications
            log.info(selectionDetails.toString());
        }
        return selectionDetails;
    }


}
