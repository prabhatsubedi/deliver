package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.abs.AbstractManager;
import com.yetistep.delivr.dao.inf.*;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.OrderSummaryDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.enums.PreferenceType;
import com.yetistep.delivr.enums.Status;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.model.mobile.CategoryDto;
import com.yetistep.delivr.model.mobile.PageInfo;
import com.yetistep.delivr.model.mobile.StaticPagination;
import com.yetistep.delivr.model.mobile.dto.CartDto;
import com.yetistep.delivr.model.mobile.dto.DefaultInfoDto;
import com.yetistep.delivr.model.mobile.dto.ItemDto;
import com.yetistep.delivr.model.mobile.dto.PreferenceDto;
import com.yetistep.delivr.service.inf.ClientService;
import com.yetistep.delivr.service.inf.SystemPropertyService;
import com.yetistep.delivr.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/9/14
 * Time: 12:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClientServiceImpl extends AbstractManager implements ClientService {
    private static final Logger log = Logger.getLogger(ClientServiceImpl.class);
    private static final BigDecimal minusOne = new BigDecimal(-1);

    @Autowired
    StoresBrandDaoService storesBrandDaoService;

    @Autowired
    StoreDaoService storeDaoService;

    @Autowired
    OrderDaoService orderDaoService;

    @Autowired
    CustomerDaoService customerDaoService;

    @Autowired
    CategoryDaoService categoryDaoService;

    @Autowired
    ItemDaoService itemDaoService;

    @Autowired
    CartDaoService cartDaoService;

    @Autowired
    CartAttributesDaoService cartAttributesDaoService;

    @Autowired
    ItemsImageDaoService itemsImageDaoService;

    @Autowired
    DeliveryBoySelectionDaoService deliveryBoySelectionDaoService;

    @Autowired
    SystemPropertyService systemPropertyService;

    @Autowired
    UserDeviceDaoService userDeviceDaoService;

    @Autowired
    OrderCancelDaoService orderCancelDaoService;

    @Autowired
    ActionLogDaoService actionLogDaoService;

    @Autowired
    CartCustomItemDaoService cartCustomItemDaoService;

    @Override
    public Map<String, Object> getBrands(RequestJsonDto requestJsonDto) throws Exception {
        log.info("+++++++++ Getting all brands +++++++++++++++");

        /* Check Client Info */
        CustomerEntity customer = customerDaoService.find(requestJsonDto.getCustomerInfo().getClientId());
        if(customer == null)
            throw new YSException("VLD033");

        /* Featured Brand List Only */
        List<StoresBrandEntity> featuredBrands = storesBrandDaoService.findActiveFeaturedBrands();

        /* Priority Brands Not Featured */
        List<StoresBrandEntity> priorityBrands = null;
        boolean isBrandWitDistanceSort = false;
        String lat = null;
        String lon = null;
        if (requestJsonDto.getGpsInfo() == null) {
            CustomerEntity customerEntity = customerDaoService.getLatLong(requestJsonDto.getCustomerInfo().getClientId());
            if (customerEntity != null) {
                lat = customerEntity.getLatitude();
                lon = customerEntity.getLongitude();
            }
//                throw new YSException("VLD011");

            priorityBrands = storesBrandDaoService.findActivePriorityBrands(null);
        } else {
            priorityBrands = storesBrandDaoService.findActivePriorityBrands("Not Null");
            isBrandWitDistanceSort = true;
            lat = requestJsonDto.getGpsInfo().getLatitude();
            lon = requestJsonDto.getGpsInfo().getLongitude();
        }


        log.info(" Customer lat, lon ==== " + lat + " ::::: " + lon);
        /* Add Both Brand in One List */
        List<StoresBrandEntity> storeBrandResult = new ArrayList<>();

        if (priorityBrands.size() > 0) {

            for(StoresBrandEntity priorityBrandEntity : priorityBrands){
                List<StoreEntity> storeEntities = storeDaoService.findStores(priorityBrandEntity.getId());

                if((lat!=null && !lat.isEmpty()) && (lon!=null && !lon.isEmpty())) //If and only if lat, lon exist of customer then sort
                    sortStoreByLocation(lat, lon, storeEntities);

                if(storeEntities.get(0).getStreet()!=null && !storeEntities.get(0).getStreet().isEmpty())
                    priorityBrandEntity.setNearestStoreLocation(storeEntities.get(0).getStreet());
                else
                    priorityBrandEntity.setNearestStoreLocation(CommonConstants.UNKNOWN_LOCATION);

                if(DateUtil.isTimeBetweenTwoTime(priorityBrandEntity.getOpeningTime().toString(), priorityBrandEntity.getClosingTime().toString(), DateUtil.getCurrentTime().toString())){
                    priorityBrandEntity.setOpenStatus(true);
                }else{
                    priorityBrandEntity.setOpenStatus(false);
                }
            }

            storeBrandResult.addAll(priorityBrands);
        }


        /* Other then Featured and Priority List, Should be Sort by Customer Distance */
        if (isBrandWitDistanceSort) {
            /* Now Ignore Brand */
            List<Integer> ignoreList = new ArrayList<>();
            for (StoresBrandEntity storesBrandEntity : featuredBrands) {
                ignoreList.add(storesBrandEntity.getId());
            }

            for (StoresBrandEntity storesBrandEntity : priorityBrands) {
                ignoreList.add(storesBrandEntity.getId());
            }

            List<StoreEntity> storeEntities = storeDaoService.findActiveStores(ignoreList);

                 /* Extract Latitude and Longitude */
            if((lat!=null && !lat.isEmpty()) && (lon!=null && !lon.isEmpty())) //If and only if lat, lon exist of customer then sort
                sortStoreByLocation(lat, lon, storeEntities);


            //Now Combine all brand in one list
            for (StoreEntity storeEntity : storeEntities) {
                if (!containsBrandId(storeBrandResult, storeEntity.getStoresBrand().getId()) && !containsBrandId(featuredBrands, storeEntity.getStoresBrand().getId())) {
                    if(storeEntity.getStoresBrand().getStatus().ordinal() != Status.ACTIVE.ordinal())
                        continue;

                    StoresBrandEntity tempBrand = new StoresBrandEntity();
                    tempBrand.setId(storeEntity.getStoresBrand().getId());
                    tempBrand.setOpeningTime(storeEntity.getStoresBrand().getOpeningTime());
                    tempBrand.setClosingTime(storeEntity.getStoresBrand().getClosingTime());
                    tempBrand.setBrandName(storeEntity.getStoresBrand().getBrandName());
                    tempBrand.setBrandLogo(storeEntity.getStoresBrand().getBrandLogo());
                    tempBrand.setBrandImage(storeEntity.getStoresBrand().getBrandImage());
                    tempBrand.setBrandUrl(storeEntity.getStoresBrand().getBrandUrl());
                    tempBrand.setFeatured(storeEntity.getStoresBrand().getFeatured());
                    tempBrand.setPriority(storeEntity.getStoresBrand().getPriority());
                    tempBrand.setMinOrderAmount(storeEntity.getStoresBrand().getMinOrderAmount());

                    //Nearest Location
                    if(storeEntity.getStreet() !=null && !storeEntity.getStreet().isEmpty())
                        tempBrand.setNearestStoreLocation(storeEntity.getStreet());
                    else
                        tempBrand.setNearestStoreLocation(CommonConstants.UNKNOWN_LOCATION);

                    if(DateUtil.isTimeBetweenTwoTime(storeEntity.getStoresBrand().getOpeningTime().toString(), storeEntity.getStoresBrand().getClosingTime().toString(), DateUtil.getCurrentTime().toString())){
                        tempBrand.setOpenStatus(true);
                    }else{
                        tempBrand.setOpenStatus(false);
                    }

                    storeBrandResult.add(tempBrand);

                }

            }
        }

        /* If all stores are not activate then  brand should escaped to display */
        for(StoresBrandEntity featureBrand : featuredBrands){
             if(storeDaoService.getActiveStores(featureBrand.getId()) == 0) {
                 featuredBrands.remove(featureBrand);
             } else {
                 List<StoreEntity> storeEntities = storeDaoService.findStores(featureBrand.getId());

                 if((lat!=null && !lat.isEmpty()) && (lon!=null && !lon.isEmpty())) //If and only if lat, lon exist of customer then sort
                    sortStoreByLocation(lat, lon, storeEntities);

                 if(storeEntities.get(0).getStreet()!=null && !storeEntities.get(0).getStreet().isEmpty())
                     featureBrand.setNearestStoreLocation(storeEntities.get(0).getStreet());
                 else
                     featureBrand.setNearestStoreLocation(CommonConstants.UNKNOWN_LOCATION);

                 if(DateUtil.isTimeBetweenTwoTime(featureBrand.getOpeningTime().toString(), featureBrand.getClosingTime().toString(), DateUtil.getCurrentTime().toString())){
                     featureBrand.setOpenStatus(true);
                 }else{
                     featureBrand.setOpenStatus(false);
                 }
             }


        }

        for(StoresBrandEntity otherBrand : storeBrandResult) {
            if(storeDaoService.getActiveStores(otherBrand.getId()) == 0)
                storeBrandResult.remove(otherBrand);
        }

        // Perform sorted store pagination
        PageInfo pageInfo = null;
        List<StoresBrandEntity> sortedList = new ArrayList<>();
        if (storeBrandResult.size() > 0) {
            Integer pageId = 1;
            if (requestJsonDto.getPageInfo() != null)
                pageId = requestJsonDto.getPageInfo().getPageNumber();

            StaticPagination staticPagination = new StaticPagination();
            staticPagination.paginate(storeBrandResult, pageId);
            sortedList = (List<StoresBrandEntity>) staticPagination.getList();
            staticPagination.setList(null);
            pageInfo = staticPagination;
        }

        //Response Data

        Map<String, Object> map = new HashMap<>();
        map.put("featured", featuredBrands);
        map.put("all", sortedList);
        map.put("page", pageInfo);
        //map.put("currency", getCurrencyType());
        map.put("defaultInfo", getDefaultInfo());
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
    public List<CategoryDto> getParentCategory(Integer brandId) throws Exception {
        log.info("++++++++++ Getting Parent Category and list cat id +++++++++++++");
        /* [Category Listing Without Main Root Category] */

        List<CategoryDto> categoryDtoList = new ArrayList<>();
        CategoryDto categoryDto = null;
        List<CategoryEntity> categoryEntities = itemDaoService.findItemCategory(brandId);

        for (CategoryEntity categoryEntity : categoryEntities) {
            categoryDto = new CategoryDto();

            CategoryEntity resultCat = categoryDaoService.find(categoryEntity.getId());
            if (resultCat.getParent().getParent() == null) {
                /* Item has only one category to display (After Skipping Parent Category) */
                categoryDto.setValues(resultCat);
                categoryDto.setHasNext(false);
                addToList(categoryDtoList, categoryDto);
            } else {
                /* Item has many categories */
                Integer hasPrevParent = 0;
                CategoryEntity cat = null;
                while (hasPrevParent != null) { //Just for maintaining loop

                    //Multi Level Search
                    if (cat == null)
                        cat = resultCat.getParent();
                    else
                        cat = cat.getParent();

                    if (cat == null || cat.getParent().getParent() == null) {
                        categoryDto.setValues(cat);
                        break;
                    }

                }

                categoryDto.setHasNext(true);
                addToList(categoryDtoList, categoryDto);
            }

        }
        return categoryDtoList;

        //TODO: Below Logic Removed temporary (It might be revoked later) [Show with main parent category]
        /* [Category Listing With Root Category ] */
        /* Below Commented Code Used to showing with main parent category */

//        log.info("++++++++++ Getting Parent Category and list cat id +++++++++++++");
//        List<CategoryDto> categoryDtoList = new ArrayList<>();
//        CategoryDto categoryDto = null;
//        List<CategoryEntity> categoryEntities = itemDaoService.findItemCategory(brandId);
//
//        for (CategoryEntity categoryEntity : categoryEntities) {
//            categoryDto = new CategoryDto();
//            if (categoryEntity.getParentId() == null) {
//                categoryDto.setValues(categoryEntity);
//                categoryDto.setHasNext(false);
//            } else {
//                //Search  Main Parent Category
//                CategoryEntity resultCat = categoryDaoService.find(categoryEntity.getId());
//                Integer hasPrevParent = 0;
//                CategoryEntity cat = null;
//                //LOOP for parent (Search Parent and Category)
//                while (hasPrevParent != null) {
//                    // 2nd Level Search
//                    if (resultCat.getParent().getParent() == null) {
//                        categoryDto.setValues(resultCat.getParent());
//                        break;
//                    }
//
//                    //Multi Level Search
//                    if (cat == null)
//                        cat = resultCat.getParent();
//                    else
//                        cat = cat.getParent();
//
//                    if (cat == null || cat.getParent() == null)
//                        break;
//
//                    hasPrevParent = cat.getParent().getId();
//                    categoryDto.setValues(cat.getParent());
//
//                }
//
//                categoryDto.setHasNext(true);
//            }
//
//            Boolean isAlreadyContain = false;
//            for (CategoryDto temp : categoryDtoList) {
//                if (temp.getId().equals(categoryDto.getId()))
//                    isAlreadyContain = true;
//            }
//
//            if (!isAlreadyContain)
//                categoryDtoList.add(categoryDto);
//
//        }
//        return categoryDtoList;

}

    private void addToList(List<CategoryDto> categoryDtoList, CategoryDto categoryDto){
        Boolean isAlreadyContain = false;
        for (CategoryDto temp : categoryDtoList) {
            if (temp.getId().equals(categoryDto.getId()))
                isAlreadyContain = true;
        }

        if (!isAlreadyContain)
            categoryDtoList.add(categoryDto);

    }

    @Override
    public List<CategoryDto> getSubCategory(CategoryDto categoryDto) throws Exception {
        log.info("+++++++++++++ Getting Sub Category Of " + categoryDto.getId() + " +++++++++");
        List<CategoryDto> list = new ArrayList<>();
        Integer brandId = categoryDto.getBrandId();
        Integer nextId = categoryDto.getId();

        List<CategoryEntity> categoryEntities = itemDaoService.findItemCategory(brandId);

        if (categoryEntities == null || categoryEntities.size() == 0)
            throw new YSException("VLD012");

        for (CategoryEntity categoryEntity : categoryEntities) {
            CategoryEntity cat = null;
            CategoryEntity resultCat = categoryDaoService.find(categoryEntity.getId());
            CategoryDto resultDto = new CategoryDto();
            Integer hasNext = 0;
            while (hasNext != null) {

                if (resultCat.getParent().getId().equals(nextId)) {
                    resultDto.setValues(resultCat);
                    resultDto.setHasNext(false);
                    hasNext = null;
                    break;
                }

                if (cat == null)
                    cat = resultCat.getParent();
                else
                    cat = cat.getParent();

                if (cat == null || cat.getParent() == null)
                    break;

                if (cat.getParent().getId().equals(nextId)) {
                    resultDto.setValues(cat);
                    resultDto.setHasNext(true);
                    hasNext = null;
                    break;
                }

                resultDto.setValues(cat.getParent());
            }

            if (hasNext == null) {
                Boolean isAlreadyContain = false;
                for (CategoryDto temp : list) {
                    if (temp.getId().equals(resultDto.getId()))
                        isAlreadyContain = true;
                }

                if (!isAlreadyContain)
                    list.add(resultDto);
            }
        }

        return list;
    }

    class StoreDistanceComparator implements Comparator<StoreEntity> {

        @Override
        public int compare(StoreEntity o1, StoreEntity o2) {
            BigDecimal distanceSub = o1.getCustomerToStoreDistance().subtract(o2.getCustomerToStoreDistance());
            return distanceSub.intValue();
        }
    }

    private boolean containsBrandId(List<StoresBrandEntity> list, Integer id) {
        for (StoresBrandEntity object : list) {
            if (object.getId() == id) {
                return true;
            }
        }
        return false;
    }

    private void setStoreOpenSet(List<StoresBrandEntity> storesBrandEntities) throws Exception {
        for (StoresBrandEntity storesBrandEntity : storesBrandEntities) {
            storesBrandEntity.setOpenStatus(DateUtil.isTimeBetweenTwoTime(storesBrandEntity.getOpeningTime().toString(),
                    storesBrandEntity.getClosingTime().toString(), DateUtil.getCurrentTime().toString()));
        }
    }

    @Override
    public OrderSummaryDto getOrderSummaryById(Integer orderId, Long facebookId) throws Exception {
        log.info("Getting order summary of order ID:"+orderId);
        OrderEntity order = orderDaoService.find(orderId);
        if (order == null) {
            throw new YSException("VLD017");
        }
        CustomerEntity customerEntity = order.getCustomer();
        if(!customerEntity.getFacebookId().equals(facebookId)){
            throw new YSException("ORD013");
        }

        OrderSummaryDto orderSummary = new OrderSummaryDto();
        List<ItemsOrderEntity> itemsOrder = order.getItemsOrder();
        for (ItemsOrderEntity itemOrder : itemsOrder) {
            if ( itemOrder.getNote()!=null && itemOrder.getNote().equals(""))
                itemOrder.setNote(null);

            if (itemOrder.getItem() != null) {
                ItemEntity item = new ItemEntity();
                item.setId(itemOrder.getItem().getId());
                item.setName(itemOrder.getItem().getName());
                item.setAdditionalOffer(itemOrder.getItem().getAdditionalOffer());
                ItemsImageEntity itemsImageEntity = itemsImageDaoService.findImage(itemOrder.getItem().getId());
                if(itemsImageEntity !=null)
                    item.setImageUrl(itemsImageEntity.getUrl());
                else
                    item.setImageUrl(systemPropertyService.readPrefValue(PreferenceType.DEFAULT_IMG_ITEM));
                itemOrder.setItem(item);
            }else if(itemOrder.getCustomItem() != null){
                if(itemOrder.getCustomItem().getCustomerCustom().equals(Boolean.TRUE))   {
                    if(itemOrder.getPurchaseStatus() == null || !itemOrder.getPurchaseStatus().equals(Boolean.TRUE)) {
                        itemOrder.setVat(minusOne);
                        itemOrder.setServiceCharge(minusOne);
                        itemOrder.setItemTotal(minusOne);
                        itemOrder.setServiceAndVatCharge(minusOne);
                    }
                }

                itemOrder.getCustomItem().setImageUrl(systemPropertyService.readPrefValue(PreferenceType.DEFAULT_IMG_ITEM));
            }
            itemOrder.setItemOrderAttributes(null);
        }

        StoreEntity store = new StoreEntity();
        store.setName(order.getStore().getName());
        store.setBrandLogo(order.getStore().getStoresBrand().getBrandLogo());

        List<String> orderAttachments = new ArrayList<String>();
        for(String attachments: order.getAttachments()){
            orderAttachments.add(attachments);
        }

        orderSummary.setId(order.getId());
        orderSummary.setStore(store);
        orderSummary.setOrderStatus(order.getOrderStatus());
        orderSummary.setOrderDate(order.getOrderDate());
        orderSummary.setOrderVerificationCode(order.getOrderVerificationCode());
        orderSummary.setItemOrders(itemsOrder);

        OrderSummaryDto.AccountSummary accountSummary = orderSummary.new AccountSummary();
        

        Boolean tbd = false;
        //to calculate subtotal
        Boolean allTbd = true;
        for(ItemsOrderEntity itemsOrderEntity: order.getItemsOrder()){
            if(itemsOrderEntity.getItemTotal().equals(minusOne) && itemsOrderEntity.getAvailabilityStatus()){
                tbd = true;
            }  else {
                allTbd = false;
            }
        }



        if(tbd){
            accountSummary.setServiceFee(minusOne);
            accountSummary.setVatAndServiceCharge(minusOne);
            accountSummary.setEstimatedTotal(minusOne);
            accountSummary.setDeliveryFee(minusOne);
            accountSummary.setItemServiceCharge(minusOne);
            accountSummary.setItemVatCharge(minusOne);
        }   else {
            accountSummary.setServiceFee(order.getSystemServiceCharge());
            accountSummary.setVatAndServiceCharge(order.getItemServiceAndVatCharge());
            accountSummary.setDeliveryFee(order.getCourierTransaction().getDeliveryChargedAfterDiscount());
            accountSummary.setEstimatedTotal(order.getGrandTotal());
            accountSummary.setItemServiceCharge(order.getItemServiceCharge());
            accountSummary.setItemVatCharge(order.getItemVatCharge());
        }

        if(allTbd)
            accountSummary.setSubTotal(minusOne);
        else
            accountSummary.setSubTotal(order.getTotalCost());

        //TODO discussion
        //accountSummary.setTotalDiscount(order.getCourierTransaction().getDeliveryChargedBeforeDiscount().subtract(order.getCourierTransaction().getDeliveryChargedAfterDiscount()));
        accountSummary.setTotalDiscount(BigDecimal.ZERO);
        accountSummary.setCurrency(systemPropertyService.readPrefValue(PreferenceType.CURRENCY));
        accountSummary.setPaidFromCOD(order.getPaidFromCOD());
        accountSummary.setPaidFromWallet(order.getPaidFromWallet());
        accountSummary.setPaymentMode(order.getPaymentMode());
        accountSummary.setDiscountFromStore(order.getDiscountFromStore());
        orderSummary.setAccountSummary(accountSummary);
        orderSummary.setAttachments(orderAttachments);

        if(order.getDeliveryBoy() != null){
            DeliveryBoyEntity deliveryBoyEntity = new DeliveryBoyEntity();
            UserEntity user = new UserEntity();
            user.setProfileImage(order.getDeliveryBoy().getUser().getProfileImage());
            user.setFullName(order.getDeliveryBoy().getUser().getFullName());
            user.setMobileNumber(order.getDeliveryBoy().getUser().getMobileNumber());
            deliveryBoyEntity.setUser(user);
            orderSummary.setDeliveryBoy(deliveryBoyEntity);
        }

        if(order.getDeliveryBoy() != null){
            DeliveryBoySelectionEntity deliveryBoySelectionEntity = deliveryBoySelectionDaoService.getSelectionDetails(order.getId(), order.getDeliveryBoy().getId());
            Integer totalTimeRequired = deliveryBoySelectionEntity.getTotalTimeRequired();
            orderSummary.setEstimatedDeliveryTime(DateUtil.addMinute(order.getOrderDate(), totalTimeRequired));
        }
        return orderSummary;
    }

    @Override
    public List<ItemDto> getItems(Integer brandId, Integer categoryId) throws Exception {
        log.info("++++++++++ Getting Items of Brand " + brandId + " +++++++++++");

        List<ItemDto> items = itemDaoService.findItems(brandId, categoryId);
        if (items.size() == 0)
            throw new YSException("ITM001");

        for (ItemDto itemDto : items) {
            BigDecimal price = itemDto.getPrice();
            //Service Charge
//            if (itemDto.getServiceCharge() != null && BigDecimalUtil.isNotZero(itemDto.getServiceCharge())) {
//                price = price.add(BigDecimalUtil.percentageOf(price, itemDto.getServiceCharge()));
//            }

            //Vat
//            if (itemDto.getVat() != null && BigDecimalUtil.isNotZero(itemDto.getVat())) {
//                price = price.add(BigDecimalUtil.percentageOf(price, itemDto.getVat()));
//            }

            if(itemDto.getImageUrl()==null || itemDto.getImageUrl().isEmpty()){
                 itemDto.setImageUrl(systemPropertyService.readPrefValue(PreferenceType.DEFAULT_IMG_ITEM));
                itemDto.setDefaultImage(true);
            }

            itemDto.setPrice(price.setScale(2, BigDecimal.ROUND_UP));
            itemDto.setVat(null);
            itemDto.setServiceCharge(null);
            itemDto.setBrandId(brandId);
            itemDto.setCurrency(systemPropertyService.readPrefValue(PreferenceType.CURRENCY));
        }

        return items;

    }

    @Override
    public ItemEntity getItemDetail(Integer itemId) throws Exception {
        log.info("++++++++++++ Getting Item Detail of id " + itemId + " +++++++++++++");

        ItemEntity itemEntity = itemDaoService.find(itemId);
        if (itemEntity == null)
            throw new YSException("ITM001");

        if (!itemEntity.getStatus().toString().equals(Status.ACTIVE.toString()))
            throw new YSException("ITM002");


//        if (itemEntity.getAttributesTypes().size() == 0)
//            itemEntity.setAttributesTypes(null);
//
//        if (itemEntity.getAttributesTypes() != null && itemEntity.getAttributesTypes().size() != 0) {
//            for (ItemsAttributesTypeEntity itemsAttributesTypeEntity : itemEntity.getAttributesTypes()) {
//                //itemsAttributesTypeEntity.setId(null);
//                itemsAttributesTypeEntity.setItem(null);
//                for (ItemsAttributeEntity itemsAttributeEntity : itemsAttributesTypeEntity.getItemsAttribute()) {
//                    //itemsAttributeEntity.setId(null);
//                    itemsAttributeEntity.setType(null);
//                    itemsAttributeEntity.setCartAttributes(null);
//                }
//            }
//        }

        if(itemEntity.getItemsImage()==null || itemEntity.getItemsImage().size() == 0) {
            //If Item has not any image then set default image
            List<ItemsImageEntity> itemImages = new ArrayList<>();
            ItemsImageEntity itemsImage = new ItemsImageEntity();
            itemsImage.setUrl(systemPropertyService.readPrefValue(PreferenceType.DEFAULT_IMG_ITEM));
            itemsImage.setDefaultImage(true);
            itemImages.add(itemsImage);
            itemEntity.setItemsImage(itemImages);
        }
// else {
//            for (ItemsImageEntity itemsImageEntity : itemEntity.getItemsImage()) {
//                itemsImageEntity.setId(null);
//            }
//        }


        itemEntity.setBrandName(itemEntity.getStoresBrand().getBrandName());
       /* itemEntity.setItemsStores(null);
        itemEntity.setItemsOrder(null);
        itemEntity.setCategory(null);
        itemEntity.setStoresBrand(null);
        itemEntity.setStatus(null);
        itemEntity.setCreatedDate(null);
        itemEntity.setModifiedDate(null);
        itemEntity.setVat(null);
        itemEntity.setServiceCharge(null);*/
        itemEntity.setCurrency(systemPropertyService.readPrefValue(PreferenceType.CURRENCY));

        String fields = "id,name,description,availableStartTime,availableEndTime,maxOrderQuantity,minOrderQuantity,unitPrice,additionalOffer,brandName,currency,itemsImage,attributesTypes";

        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("itemsImage", "url");
        assoc.put("attributesTypes", "id,type,multiSelect,itemsAttribute");
        subAssoc.put("itemsAttribute", "id,attribute,unitPrice");

        return  (ItemEntity) ReturnJsonUtil.getJsonObject(itemEntity, fields, assoc , subAssoc);
    }

    @Override
    public void saveCart(CartEntity cart) throws Exception {
        log.info("+++++++++++++ Performing Add to Cart of " + cart.getCustomer().getFacebookId() + " +++++++++++");
        /*Check The Cart If Customer Have Previous Cart on different store
        If cart on different brand then delete it */

        CustomerEntity customerEntity = customerDaoService.getCustomerStatus(cart.getCustomer().getFacebookId());
        if(customerEntity == null)
             throw new YSException("VLD033");

        if(customerEntity.getUser().getStatus() !=null && (customerEntity.getUser().getStatus().toInt() == Status.INACTIVE.toInt()))
            throw new YSException("SEC012", "#" + systemPropertyService.readPrefValue(PreferenceType.HELPLINE_NUMBER)); //Customer Inactivated

        //This Will Delete If Cart from Another Brand
        List<Integer> cartList = cartDaoService.findCarts(cart.getCustomer().getFacebookId(), cart.getStoresBrand().getId());
        if (cartList.size() > 0) {
            log.info("++++++++ Deleting previous cart and and its attributes ++++++++");
            List<Integer> cartAttributes = cartAttributesDaoService.findCartAttributes(cartList);
            // Delete Cart Attributes
            if (cartAttributes.size() > 0)
                cartAttributesDaoService.deleteCartAttributes(cartAttributes);

            //Delete Carts
            cartDaoService.deleteCarts(cartList);
        }

        List<Integer> inputAttributes = new ArrayList<>();
        if (cart.getCartAttributes() != null && cart.getCartAttributes().size() > 0) {
            for (CartAttributesEntity cartAttributesEntity : cart.getCartAttributes()) {
                inputAttributes.add(cartAttributesEntity.getItemsAttribute().getId());
                cartAttributesEntity.setCart(cart);
            }
        }

        String customItemName = new String();
        if (cart.getCartCustomItem() != null) {
            if(cart.getCartCustomItem().getName() == null)
                throw new YSException("VLD037");

            customItemName = cart.getCartCustomItem().getName();
            cart.getCartCustomItem().setCart(cart);
        }

        //Now Check Same Information
        List<Integer> prevCartIds = null;

        if(cart.getItem() != null){
            prevCartIds = cartDaoService.findCarts(cart.getCustomer().getFacebookId(), cart.getItem().getId(),
               cart.getStoresBrand().getId(), cart.getNote());
        }else{
            prevCartIds = cartDaoService.findCarts(cart.getCustomer().getFacebookId(),
                    cart.getStoresBrand().getId(), cart.getNote());
        }

      Integer updatedCartId = 0;
      Boolean isUpdated = false;
      if(prevCartIds.size() > 0){
          if(cart.getItem() != null){
               for(Integer cartId: prevCartIds){
                   List<Integer> dbAttributes = cartAttributesDaoService.findCartAttributes(cartId);
                   if(inputAttributes.size() == 0){
                       if(dbAttributes.size() == 0) {
                           updatedCartId = cartId;
                           isUpdated = true;
                           break;
                       }
                   } else {
                       if(dbAttributes.size() > 0){
                             if(inputAttributes.size() == dbAttributes.size()){
                                 Boolean isSame = true;
                                 for(int i=0; i<inputAttributes.size(); i++){
                                     if(inputAttributes.get(i) != dbAttributes.get(i)) {
                                         isSame = false;
                                         break;
                                     }
                                 }
                                 if(isSame){
                                     updatedCartId = cartId;
                                     isUpdated = true;
                                     break;
                                 }
                             }
                       }
                   }
               }
          }else{
              //for custom items
              for(Integer cartId: prevCartIds){
                  CartCustomItemEntity dbCustomItem = cartCustomItemDaoService.findCustomItem(cartId);
                  if(dbCustomItem != null){
                      String dbCustomItemName = dbCustomItem.getName();
                      Boolean isSame = true;
                      if(!customItemName.equals(dbCustomItemName)){
                          isSame = false;
                          break;
                      }

                      if(isSame){
                          updatedCartId = cartId;
                          isUpdated = true;
                          break;
                      }
                  }
              }
          }

          if(isUpdated){
              //if not custom item check available quantity
              if(cart.getItem() != null){
                  Integer availableQty = cartDaoService.getAvailableOrderItem(updatedCartId);
                  if(availableQty == 0)
                      throw new YSException("CRT002");

                  if(cart.getOrderQuantity() > availableQty)
                      throw new YSException("CRT002", "Only " + availableQty + " qty can be added");
              }
              cartDaoService.updateOrderQuantity(updatedCartId, cart.getOrderQuantity());
          } else {
              cart.setCreatedDate(DateUtil.getCurrentTimestampSQL());
              cart.setModifiedDate(null);
              cartDaoService.save(cart);
          }

      } else {
          //Such Cart is not available
          cart.setCreatedDate(DateUtil.getCurrentTimestampSQL());
          cart.setModifiedDate(null);
          cartDaoService.save(cart);
      }

    }

    @Override
    public CartDto getMyCart(Long facebookId, String lat, String lon) throws Exception {
        log.info("++++++++++++ Getting my carts of client " + facebookId + " ++++++++++++");

        CustomerEntity customerEntity = customerDaoService.getCustomerStatus(facebookId);
        if(customerEntity == null)
            throw new YSException("VLD033");


        if (lat==null && lon == null) {
            CustomerEntity customer = customerDaoService.getLatLong(facebookId);
            if(customer != null) {
                lat = customer.getLatitude();
                lon = customer.getLongitude();
            }
        }

       CartDto cartDto = null;
       List<CartEntity> cartEntities = cartDaoService.getMyCarts(facebookId);
        //get custom item carts
        List<CartEntity> customItemCarts = cartDaoService.getMyCustomItemCarts(facebookId);
        for (CartEntity cart: customItemCarts){
            ItemEntity item = new ItemEntity();
            /*List<ItemsImageEntity> itemsImages = new ArrayList<>();
            ItemsImageEntity itemsImage = new ItemsImageEntity();
            itemsImage.setUrl("https://idelivrlive.s3.amazonaws.com/default/item/noimg.jpg");
            itemsImages.add(itemsImage);*/
            item.setId(cart.getCartCustomItem().getId());
            item.setName(cart.getCartCustomItem().getName());
            //item.setItemsImage(itemsImages);
            item.setImageUrl(systemPropertyService.readPrefValue(PreferenceType.DEFAULT_IMG_ITEM));
            item.setDefaultImage(true);
            item.setUnitPrice(minusOne);
            item.setIsCustomItem(Boolean.TRUE);
            cart.setItem(item);
            cart.setCartCustomItem(null);
        }
        //merge both carts
        cartEntities.addAll(customItemCarts);

       StoresBrandEntity storesBrandEntity;
       if(cartEntities !=null && cartEntities.size() > 0){
           storesBrandEntity = cartEntities.get(0).getStoresBrand();

           for(CartEntity cartEntity : cartEntities){
               //if not custom item set image and attributes
               if(!cartEntity.getItem().getIsCustomItem()){
                   ItemsImageEntity itemsImageEntity = itemsImageDaoService.findImage(cartEntity.getItem().getId());
                   if(itemsImageEntity !=null)
                        cartEntity.getItem().setImageUrl(itemsImageEntity.getUrl());
                   else {
                        cartEntity.getItem().setImageUrl(systemPropertyService.readPrefValue(PreferenceType.DEFAULT_IMG_ITEM));
                       cartEntity.getItem().setDefaultImage(true);
                   }

                   //Add Attribute Price and Unit Price
                   BigDecimal attributesPrice = cartAttributesDaoService.findAttributesPrice(cartEntity.getId());

                   if(!cartEntity.getItem().getUnitPrice().equals(minusOne))
                        cartEntity.getItem().setUnitPrice(cartEntity.getItem().getUnitPrice().add(attributesPrice).multiply(new BigDecimal(cartEntity.getOrderQuantity())));

                   cartEntity.getItem().setVat(null);
                   cartEntity.getItem().setServiceCharge(null);
                   cartEntity.getItem().setStatus(null);
               }

               cartEntity.setStoresBrand(null);

           }

           cartDto = new CartDto();
           Boolean isOpen = DateUtil.isTimeBetweenTwoTime(storesBrandEntity.getOpeningTime().toString(), storesBrandEntity.getClosingTime().toString(),DateUtil.getCurrentTime().toString());
           //Set Store Brand
           StoresBrandEntity store = new StoresBrandEntity();
           store.setId(storesBrandEntity.getId());
           store.setBrandName(storesBrandEntity.getBrandName());
           store.setOpeningTime(storesBrandEntity.getOpeningTime());
           store.setClosingTime(storesBrandEntity.getClosingTime());
           store.setBrandLogo(storesBrandEntity.getBrandLogo());
           store.setBrandImage(storesBrandEntity.getBrandImage());
           store.setMinOrderAmount(storesBrandEntity.getMinOrderAmount());
           store.setOpenStatus(isOpen);

           if(lat!=null && lon != null){
               List<StoreEntity> storeEntities = storeDaoService.findStores(storesBrandEntity.getId());

               sortStoreByLocation(lat, lon, storeEntities);

               if(storeEntities.get(0).getStreet()!=null && !storeEntities.get(0).getStreet().isEmpty())
                   store.setNearestStoreLocation(storeEntities.get(0).getStreet());
               else
                   store.setNearestStoreLocation(CommonConstants.UNKNOWN_LOCATION);
           } else {
               store.setNearestStoreLocation(CommonConstants.UNKNOWN_LOCATION);
           }

           cartDto.setStoresBrand(store);
           cartDto.setCarts(cartEntities);
           cartDto.setCurrency(systemPropertyService.readPrefValue(PreferenceType.CURRENCY));

       }


       return cartDto;

    }

    @Override
    public CartDto validateCart(Long facebookId) throws Exception {
        log.info("+++++++++++++ Validating Cart of Client Facebook Id : " + facebookId  +" +++++++++++++");

        CustomerEntity customerEntity = customerDaoService.getCustomerStatus(facebookId);
        if(customerEntity == null)
            throw new YSException("VLD033");

        if(customerEntity.getUser().getStatus() !=null && (customerEntity.getUser().getStatus().toInt() == Status.INACTIVE.toInt()))
            throw new YSException("SEC012", "#" + systemPropertyService.readPrefValue(PreferenceType.HELPLINE_NUMBER)); //Customer Inactivated

        CartDto cartDto = new CartDto();

        List<CartEntity> cartEntities = cartDaoService.getMyCarts(facebookId);
        //get custom item carts
        List<CartEntity> customItemCarts = cartDaoService.getMyCustomItemCarts(facebookId);
        //merge both carts
        cartEntities.addAll(customItemCarts);

        if(cartEntities==null || cartEntities.size() == 0) {
            cartDto.setMessage("CRT001"); //Cart does not exist
            cartDto.setValid(false);
            return cartDto;
        }


        Boolean isOpen = DateUtil.isTimeBetweenTwoTime(cartEntities.get(0).getStoresBrand().getOpeningTime().toString(), cartEntities.get(0).getStoresBrand().getClosingTime().toString(),DateUtil.getCurrentTime().toString());
        if(!isOpen){
            cartDto.setMessage("CRT003"); //Store is closed at this time
            cartDto.setValid(false);
            return cartDto;
        }

        Boolean isBrandNotActive = false;
        List<Integer> inactiveItems = new ArrayList<>();
        List<CartEntity> inActiveCarts = new ArrayList<>();

        BigDecimal totalPrice = BigDecimal.ZERO;
        for(CartEntity cartEntity : cartEntities){
            //Check Whether Brand Active Or Not
            //TODO: take this block out of the for loop
            if(cartEntities.get(0).getStoresBrand().getStatus().ordinal()!=Status.ACTIVE.ordinal()) {
                isBrandNotActive = true;
                break;
            }

            //if not custom item
            if(cartEntity.getItem() != null){
                if(cartEntity.getItem().getStatus().ordinal() != Status.ACTIVE.ordinal()){
                    if(!inactiveItems.contains(cartEntity.getItem().getId())){
                        inactiveItems.add(cartEntity.getItem().getId());
                        inActiveCarts.add(cartEntity);
                    }

                } else {
                    BigDecimal attributesPrice = cartAttributesDaoService.findAttributesPrice(cartEntity.getId());
                    totalPrice = totalPrice.add(cartEntity.getItem().getUnitPrice().add(attributesPrice).multiply(new BigDecimal(cartEntity.getOrderQuantity())));
                }
            }

        }

        //If Brand is Inactive
        if(isBrandNotActive) {
            //TODO: move this block of code to the previous loop of cartEntities
            List<Integer> carts = new ArrayList<>();
            for(CartEntity cartEntity : cartEntities){
                carts.add(cartEntity.getId());
            }

            List<Integer> cartAttributes = cartAttributesDaoService.findCartAttributes(carts);
            if (cartAttributes.size() > 0)
                cartAttributesDaoService.deleteCartAttributes(cartAttributes);

            //get custom items of carts
            List<Integer> cartCustomItems = cartCustomItemDaoService.findCartCustomItems(carts);

            //delete cart custom items
            if (cartCustomItems.size() > 0)
                cartCustomItemDaoService.deleteCartCustomItems(cartCustomItems);

            cartDaoService.deleteCarts(carts);

            cartDto.setMessage("CRT004"); //Cart has been deleted due to store is inactive
            cartDto.setValid(false);
            return cartDto;

        }

        //If Items are inactive
        if(inActiveCarts.size() > 0) {

            List<Integer> carts = new ArrayList<>();
            for(CartEntity cartEntity : inActiveCarts){
                carts.add(cartEntity.getId());
            }

            List<Integer> cartAttributes = cartAttributesDaoService.findCartAttributes(carts);
            if (cartAttributes.size() > 0)
                cartAttributesDaoService.deleteCartAttributes(cartAttributes);

            //get custom items of carts
            List<Integer> cartCustomItems = cartCustomItemDaoService.findCartCustomItems(carts);

            //delete cart custom items
            if (cartCustomItems.size() > 0)
                cartCustomItemDaoService.deleteCartCustomItems(cartCustomItems);


            cartDaoService.deleteCarts(carts);

            if(cartEntities.size() == inActiveCarts.size())    {
                cartDto.setMessage("CRT005"); //Cart has been deleted due to all items are inactive
                cartDto.setValid(false);
                return cartDto;
            } else {
                cartDto.setMessage("CRT006"); //Some items has been deleted due to inactive
                cartDto.setValid(false);
                return cartDto;
            }


        }

//       if(BigDecimalUtil.isLessThen(new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.ORDER_MAX_AMOUNT)), totalPrice)) {
//           //Max order amount reached
//           cartDto.setMessage("CRT007: Value of "+ systemPropertyService.readPrefValue(PreferenceType.CURRENCY) + systemPropertyService.readPrefValue(PreferenceType.ORDER_MAX_AMOUNT) + " can be order");
//           cartDto.setValid(false);
//           return cartDto;
//       }


       Boolean customItemExists = false;
       for (CartEntity cartEntity: cartEntities){
           if(cartEntity.getCartCustomItem() != null){
               customItemExists = true;
               break;
           }
       }

       if(!customItemExists){
           if(BigDecimalUtil.isLessThen(totalPrice, cartEntities.get(0).getStoresBrand().getMinOrderAmount())) {
               //Minimum order value is
               cartDto.setMessage("CRT008:"+" "+systemPropertyService.readPrefValue(PreferenceType.CURRENCY)+ cartEntities.get(0).getStoresBrand().getMinOrderAmount());
               cartDto.setValid(false);
               return cartDto;
           }
       }

        //Now Check if Min and Max Order Quanitity Has been changed from system
        boolean orderLimitationChanged = false;
        for(CartEntity cartEntity : cartEntities){
            if(cartEntity.getItem() != null){
                if(cartEntity.getOrderQuantity()< cartEntity.getItem().getMinOrderQuantity() || cartEntity.getOrderQuantity() > cartEntity.getItem().getMaxOrderQuantity()) {
                    orderLimitationChanged = true;
                    Integer changeableOrderQn = cartEntity.getItem().getMinOrderQuantity();
                    cartDaoService.updateMinOrderQuantity(cartEntity.getId(), changeableOrderQn);
                }
            }
        }

        if(orderLimitationChanged) {
            cartDto.setMessage("CRT009"); //Some item's order quantity limitation has been changed. Please review your order
            cartDto.setValid(false);
            return cartDto;
        }


        cartDto.setMessage("");
        cartDto.setValid(true);
        return cartDto;
    }

    @Override
    public void deleteCart(Integer cartId) throws Exception {
        log.info("++++++ Deleting cart id : " + cartId + " +++++++++");
        List<Integer> cartList = new ArrayList<>();
        cartList.add(cartId);

        //get custom items of carts
        List<Integer> cartCustomItems = cartCustomItemDaoService.findCartCustomItems(cartList);

        //delete cart custom items
        if (cartCustomItems.size() > 0)
            cartCustomItemDaoService.deleteCartCustomItems(cartCustomItems);

        List<Integer> cartAttributes = cartAttributesDaoService.findCartAttributes(cartList);
        if (cartAttributes.size() > 0)
            cartAttributesDaoService.deleteCartAttributes(cartAttributes);

        cartDaoService.deleteCarts(cartList);
    }

    @Override
    public void deleteAllCart(Long facebookId) throws Exception {
        log.info("++++++ Deleting cart of facebook id : " + facebookId + " +++++++++");
        List<Integer> cartList = cartDaoService.findCarts(facebookId, null);
        if(cartList==null || cartList.size() == 0)
            throw new YSException("CRT001");

        //get custom items of carts
        List<Integer> cartCustomItems = cartCustomItemDaoService.findCartCustomItems(cartList);

        //delete cart custom items
        if (cartCustomItems.size() > 0)
            cartCustomItemDaoService.deleteCartCustomItems(cartCustomItems);


        List<Integer> cartAttributes = cartAttributesDaoService.findCartAttributes(cartList);
        if (cartAttributes.size() > 0)
            cartAttributesDaoService.deleteCartAttributes(cartAttributes);

        cartDaoService.deleteCarts(cartList);
    }

    @Override
    public CartDto getCartSize(Long facebookId) throws Exception {
        log.info("++++++ Getting cart info of customer : " + facebookId + " +++++++++");
        List<CartEntity> carts = cartDaoService.getMyCarts(facebookId);
        //get custom item carts
        List<CartEntity> customItemCarts = cartDaoService.getMyCustomItemCarts(facebookId);
        //merge both carts
        carts.addAll(customItemCarts);

        CartDto cartDto = new CartDto();
        cartDto.setTotalCart(carts.size());
        if(carts.size() > 0){
            cartDto.setStoresBrand(carts.get(0).getStoresBrand());
            cartDto.getStoresBrand().setBrandLogo(null);
        }


        return cartDto;
    }

    @Override
    public CartDto getCartDetail(Integer cartId) throws Exception {
        log.info("+++++++++++++ Getting Cart Detail of id " + cartId + " +++++++++++");
        CartDto cartDto = new CartDto();
        //Cart Detail
        CartEntity cartEntity = cartDaoService.findCart(cartId);

        if(cartEntity == null)
                    cartEntity = cartDaoService.findCustomCart(cartId);

        if(cartEntity == null)
            throw new YSException("CRT001");

        //Getting Cart Attributes
        List<Integer> selectedAttributes = cartAttributesDaoService.findCartAttributes(cartEntity.getId());

        if(cartEntity.getItem() != null){
            //Getting Item Detail
            ItemEntity itemEntity = getItemDetail(cartEntity.getItem().getId());

            // Now Check Selected Attributes
            if(itemEntity.getAttributesTypes() !=null && itemEntity.getAttributesTypes().size() > 0){
                for (ItemsAttributesTypeEntity itemsAttributesTypeEntity : itemEntity.getAttributesTypes()) {
                    //itemsAttributesTypeEntity.setId(null);
                    itemsAttributesTypeEntity.setItem(null);
                    for (ItemsAttributeEntity itemsAttributeEntity : itemsAttributesTypeEntity.getItemsAttribute()) {
                        //itemsAttributeEntity.setId(null);
                        if(selectedAttributes.contains(itemsAttributeEntity.getId()))
                            itemsAttributeEntity.setSelected(true);
                        else
                            itemsAttributeEntity.setSelected(false);
                    }
                }
            }
            itemEntity.setCurrency(null);
            cartDto.setItem(itemEntity);
        }

        //get cutom item of the cart
        CartCustomItemEntity cartCustomItem = cartCustomItemDaoService.findCustomItem(cartId);
        if(cartCustomItem != null) {
            ItemEntity customItem = new ItemEntity();
            List<ItemsImageEntity> itemsImages = new ArrayList<>();
            ItemsImageEntity itemsImage = new ItemsImageEntity();
            itemsImage.setUrl(systemPropertyService.readPrefValue(PreferenceType.DEFAULT_IMG_ITEM));
            itemsImage.setDefaultImage(true);
            itemsImages.add(itemsImage);
            customItem.setId(cartCustomItem.getId());
            customItem.setName(cartCustomItem.getName());
            customItem.setItemsImage(itemsImages);
            customItem.setUnitPrice(minusOne);
            customItem.setMinOrderQuantity(1);
            customItem.setMaxOrderQuantity(100);
            customItem.setIsCustomItem(Boolean.TRUE);
            cartDto.setItem(customItem);
        }

        cartEntity.setItem(null);
        cartDto.setCart(cartEntity);
        //cartDto.setCustomItem(cartCustomItem);
        cartDto.setCurrency(systemPropertyService.readPrefValue(PreferenceType.CURRENCY));

        return cartDto;
    }

    @Override
    public void updateCart(CartEntity cart) throws Exception {
        log.info("+++++++++++ Updating Cart of cart id " + cart.getId() + " ++++++++++++++++++++++");

        //if cart has custom item unset the item else save the updated attributes
        if(cart.getItem().getIsCustomItem().equals(Boolean.TRUE)){
            cart.setItem(null);
        } else {
            // Deleting Cart Attributes
            cartAttributesDaoService.deleteCartAttributes(cart.getId());

            //Inserting Cart Attributes
            if(cart.getCartAttributes()!=null && cart.getCartAttributes().size() > 0){
                for(CartAttributesEntity cartAttributesEntity : cart.getCartAttributes()){
                    CartEntity cartEntity = new CartEntity();
                    cartEntity.setId(cart.getId());
                    cartAttributesEntity.setCart(cartEntity);
                    cartAttributesDaoService.save(cartAttributesEntity);
                }
            }
        }

        // Now Update Cart
        cart.setModifiedDate(DateUtil.getCurrentTimestampSQL());
        cartDaoService.update(cart);

    }

    public String inviteFriend(HeaderDto headerDto, ArrayList<String> emailList) throws Exception{

        CustomerEntity client = customerDaoService.find(Long.parseLong(headerDto.getId()));

        String clientName = client.getUser().getFullName();
        Integer referredFriendsCount = client.getReferredFriendsCount();

        if(referredFriendsCount >= Integer.parseInt(systemPropertyService.readPrefValue(PreferenceType.MAX_REFERRED_FRIENDS_COUNT)))
            throw new YSException("Your friend invitation already reached the maximum number.");


        boolean isLocal =  MessageBundle.isLocalHost();
        String clientUrl = getServerUrl()+"/anon/referral/"+headerDto.getId();
        /*if(isLocal) {
            clientUrl = getServerUrl()+"/anon/referral/"+headerDto.getId();
        }else{
            clientUrl = getServerUrl()+"/anon/referral/"+headerDto.getId();
        }*/

        Integer cnt = 0;
        if(emailList.size()>0){
            for (String email: emailList){
                if(referredFriendsCount >= Integer.parseInt(systemPropertyService.readPrefValue(PreferenceType.MAX_REFERRED_FRIENDS_COUNT)))
                    break;
                String serverUrl = getServerUrl();
                String body = "<p>Hi</p>";
                body += clientName+"<p> has invited you to use KollKat application.</p>";
                body += "<p>to join the delivr please find the following link: </p>";
                body += "<p>"+clientUrl+"</p>";
                String subject = " You are invited to use delivr application ";
                sendMail(email, body, subject);
                cnt++;
                referredFriendsCount++;
            }

            client.setReferredFriendsCount(referredFriendsCount+cnt);

            customerDaoService.update(client);
        }
        return "Your "+cnt+" friends has been invited";
    }

    @Override
    public Boolean updateUserDeviceToken(Long facebookId, String deviceToken) throws Exception {
        return userDeviceDaoService.updateUserDeviceToken(facebookId, deviceToken);
    }

    @Override
    public Boolean updateUserDeviceTokenFromUserId(Integer userId, String deviceToken) throws Exception {
        return userDeviceDaoService.updateUserDeviceTokenFromUserID(userId, deviceToken);
    }

    @Override
    public String getCurrencyType() throws Exception {
        return systemPropertyService.readPrefValue(PreferenceType.CURRENCY);
    }

    @Override
    public Boolean reOrder(Integer orderId, Long facebookId, Boolean flushCart) throws Exception{
        Boolean cartExists = cartDaoService.checkCartExist(facebookId);
        if (!flushCart) {
            if (cartExists) {
                throw new YSException("CRT010");
            }
        }
        OrderEntity order = orderDaoService.find(orderId);
        if(!order.getCustomer().getFacebookId().equals(facebookId)){
            throw new YSException("ORD013");
        }
        StoresBrandEntity storesBrandEntity = order.getStore().getStoresBrand();
        if(!storesBrandEntity.getStatus().equals(Status.ACTIVE)){
            throw new YSException("STB001");
        }
        if(cartExists){
            List<Integer> cartList = cartDaoService.findCarts(facebookId, null);
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
        }
        List<ItemsOrderEntity> itemsOrderEntities = order.getItemsOrder();
        for(ItemsOrderEntity itemsOrder: itemsOrderEntities){
            if(itemsOrder.getItem() != null){
                CartEntity cartEntity = new CartEntity();
                cartEntity.setItem(itemsOrder.getItem());
                cartEntity.setCustomer(order.getCustomer());
                cartEntity.setNote(itemsOrder.getCustomerNote());
                cartEntity.setOrderQuantity(itemsOrder.getQuantity());
                cartEntity.setStoresBrand(storesBrandEntity);
                List<CartAttributesEntity> cartAttributesEntities = new ArrayList<CartAttributesEntity>();
                for(ItemsOrderAttributeEntity itemsOrderAttribute: itemsOrder.getItemOrderAttributes()){
                    CartAttributesEntity cartAttribute = new CartAttributesEntity();
                    cartAttribute.setCart(cartEntity);
                    cartAttribute.setItemsAttribute(itemsOrderAttribute.getItemsAttribute());
                    cartAttributesEntities.add(cartAttribute);
                }
                cartEntity.setCartAttributes(cartAttributesEntities);
                cartDaoService.save(cartEntity);
            }
        }
        return true;
    }

    @Override
    public OrderCancelEntity orderCancelDetails(Integer orderId) throws Exception {
        OrderCancelEntity orderCancel = orderCancelDaoService.getOrderCancelInfoFromOrderId(orderId);
        if(orderCancel == null){
            throw new YSException("ORD017");
        }
        String fields = "id,reason,jobOrderStatus,cancelledDate,reasonDetails";
        Map<String, String> assoc = new HashMap<>();
        assoc.put("reasonDetails", "id,cancelReason");
        orderCancel =  (OrderCancelEntity) ReturnJsonUtil.getJsonObject(orderCancel, fields, assoc);
        return orderCancel;
    }

    @Override
    public PreferenceDto getHelpLineDetails() throws Exception {
        PreferenceDto preferenceDto = new PreferenceDto();
        preferenceDto.setHelplineNumber(systemPropertyService.readPrefValue(PreferenceType.HELPLINE_NUMBER));
        preferenceDto.setCustomerCareEmail(systemPropertyService.readPrefValue(PreferenceType.CUSTOMER_CARE_EMAIL));
        preferenceDto.setRefereeRewardAmount(new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.REFEREE_REWARD_AMOUNT)));
        preferenceDto.setCurrency(systemPropertyService.readPrefValue(PreferenceType.CURRENCY));
        return preferenceDto;
    }

    @Override
    public Boolean saveTimeStamp(TestEntity testEntity) throws Exception {
        List<RatingEntity> rates = new ArrayList<>();
        rates = orderDaoService.getDboyRatingDetails(6);

        return true;
    }

    @Override
    public DefaultInfoDto getDefaultInfo() throws Exception {
        DefaultInfoDto defaultInfoDto = new DefaultInfoDto();
        defaultInfoDto.setCurrency(systemPropertyService.readPrefValue(PreferenceType.CURRENCY));
        defaultInfoDto.setSearchImage(systemPropertyService.readPrefValue(PreferenceType.DEFAULT_IMG_SEARCH));
        return defaultInfoDto;
    }


}
