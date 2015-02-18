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
import com.yetistep.delivr.model.mobile.dto.ItemDto;
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

    @Override
    public Map<String, Object> getBrands(RequestJsonDto requestJsonDto) throws Exception {
        log.info("+++++++++ Getting all brands +++++++++++++++");

        /* Featured Brand List Only */
        List<StoresBrandEntity> featuredBrands = storesBrandDaoService.findFeaturedBrands();

        /* Priority Brands Not Featured */
        List<StoresBrandEntity> priorityBrands = null;
        boolean isBrandWitDistanceSort = false;
        String lat = null;
        String lon = null;
        if (requestJsonDto.getGpsInfo() == null) {
            CustomerEntity customerEntity = customerDaoService.getLatLong(requestJsonDto.getCustomerInfo().getClientId());
            if (customerEntity == null)
                throw new YSException("VLD011");

            lat = customerEntity.getLatitude();
            lon = customerEntity.getLongitude();

            priorityBrands = storesBrandDaoService.findPriorityBrands(null);
        } else {
            priorityBrands = storesBrandDaoService.findPriorityBrands("Not Null");
            isBrandWitDistanceSort = true;
            lat = requestJsonDto.getGpsInfo().getLatitude();
            lon = requestJsonDto.getGpsInfo().getLongitude();
        }

        /* Add Both Brand in One List */
        List<StoresBrandEntity> storeBrandResult = new ArrayList<>();

        if (priorityBrands.size() > 0)
            storeBrandResult.addAll(priorityBrands);

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

            List<StoreEntity> storeEntities = storeDaoService.findStores(ignoreList);

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
                    storeBrandResult.add(tempBrand);

                }

            }
        }

        /* If all stores are not activate then  brand should escaped to display */
        for(StoresBrandEntity featureBrand : featuredBrands){
             if(storeDaoService.getActiveStores(featureBrand.getId()) == 0)
                 featuredBrands.remove(featureBrand);
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

        return map;
    }

    @Override
    public List<CategoryDto> getParentCategory(Integer brandId) throws Exception {
        log.info("++++++++++ Getting Parent Category and list cat id +++++++++++++");
        /* Code to display category by skipping main parent code */

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
                continue;
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

        //TODO: Below Logic Removed temporary (It will revoked later) [Show with main parent category]

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
            if (itemOrder.getItem() != null) {
                ItemEntity item = new ItemEntity();
                item.setId(itemOrder.getItem().getId());
                item.setName(itemOrder.getItem().getName());
                ItemsImageEntity itemsImageEntity = itemsImageDaoService.findImage(itemOrder.getItem().getId());
                if(itemsImageEntity !=null)
                    item.setImageUrl(itemsImageEntity.getUrl());
                else
                    item.setImageUrl(systemPropertyService.readPrefValue(PreferenceType.DEFAULT_IMG_ITEM));
                itemOrder.setItem(item);
            }else if(itemOrder.getCustomItem() != null){
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
        orderSummary.setOrderVerificationCode(order.getOrderVerificationCode());
        orderSummary.setItemOrders(itemsOrder);

        OrderSummaryDto.AccountSummary accountSummary = orderSummary.new AccountSummary();
        accountSummary.setServiceFee(order.getSystemServiceCharge());
        accountSummary.setVatAndServiceCharge(order.getItemServiceAndVatCharge());
        accountSummary.setSubTotal(order.getTotalCost());
        accountSummary.setEstimatedTotal(order.getGrandTotal());
        accountSummary.setDeliveryFee(order.getCourierTransaction().getDeliveryChargedBeforeDiscount());
        accountSummary.setTotalDiscount(order.getCourierTransaction().getDeliveryChargedBeforeDiscount().subtract(order.getCourierTransaction().getDeliveryChargedAfterDiscount()));
        accountSummary.setCurrency(systemPropertyService.readPrefValue(PreferenceType.CURRENCY));
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

            if(itemDto.getImageUrl()==null || itemDto.getImageUrl().isEmpty())
                 itemDto.setImageUrl(systemPropertyService.readPrefValue(PreferenceType.DEFAULT_IMG_ITEM));

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

        ItemEntity item = (ItemEntity) ReturnJsonUtil.getJsonObject(itemEntity, fields, assoc , subAssoc);


        return item;
//        return itemEntity;
    }

    @Override
    public void saveCart(CartEntity cart) throws Exception {
        log.info("+++++++++++++ Performing Add to Cart of " + cart.getCustomer().getFacebookId() + " +++++++++++");
        /*Check The Cart If Customer Have Previous Cart on different store
        If cart on different brand then delete it */
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

        //Now Check Same Information
       List<Integer> prevCartIds = cartDaoService.findCarts(cart.getCustomer().getFacebookId(), cart.getItem().getId(),
               cart.getStoresBrand().getId(), cart.getNote());
      Integer updatedCartId = 0;
      Boolean isUpdated = false;
      if(prevCartIds.size() > 0){
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

          if(isUpdated){
              Integer availableQty = cartDaoService.getAvailableOrderItem(updatedCartId);
              if(availableQty == 0)
                  throw new YSException("CRT002");

              if(cart.getOrderQuantity() > availableQty)
                  throw new YSException("CRT002", "Only " + availableQty + " qty can be added");

              cartDaoService.updateOrderQuantity(updatedCartId, cart.getOrderQuantity());
          } else {
              cartDaoService.save(cart);
          }

      } else {
          //Such Cart is not available
          cartDaoService.save(cart);
      }

    }

    @Override
    public CartDto getMyCart(Long facebookId) throws Exception {
        log.info("++++++++++++ Getting my carts of client " + facebookId + " ++++++++++++");
       CartDto cartDto = null;
       List<CartEntity> cartEntities = cartDaoService.getMyCarts(facebookId);

       StoresBrandEntity storesBrandEntity;
       if(cartEntities !=null && cartEntities.size() > 0){
           storesBrandEntity = cartEntities.get(0).getStoresBrand();

           for(CartEntity cartEntity : cartEntities){
               if(cartEntity.getItem() != null){
                   ItemsImageEntity itemsImageEntity = itemsImageDaoService.findImage(cartEntity.getItem().getId());
                   if(itemsImageEntity !=null)
                        cartEntity.getItem().setImageUrl(itemsImageEntity.getUrl());
                   else
                        cartEntity.getItem().setImageUrl(systemPropertyService.readPrefValue(PreferenceType.DEFAULT_IMG_ITEM));

                   //Add Attribute Price and Unit Price
                   BigDecimal attributesPrice = cartAttributesDaoService.findAttributesPrice(cartEntity.getId());
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
           store.setOpenStatus(isOpen);

           cartDto.setStoresBrand(store);
           cartDto.setCarts(cartEntities);
           cartDto.setCurrency(systemPropertyService.readPrefValue(PreferenceType.CURRENCY));

       }


       return cartDto;

    }

    @Override
    public CartDto validateCart(Long facebookId) throws Exception {
        log.info("+++++++++++++ Validating Cart of Client Facebook Id : " + facebookId  +" +++++++++++++");
        CartDto cartDto = new CartDto();

        List<CartEntity> cartEntities = cartDaoService.getMyCarts(facebookId);
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
            if(cartEntities.get(0).getStoresBrand().getStatus().ordinal()!=Status.ACTIVE.ordinal()) {
                isBrandNotActive = true;
                break;
            }

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

        //If Brand is Inactive
        if(isBrandNotActive) {
            List<Integer> carts = new ArrayList<>();
            for(CartEntity cartEntity : cartEntities){
                carts.add(cartEntity.getId());
            }


            List<Integer> cartAttributes = cartAttributesDaoService.findCartAttributes(carts);
            if (cartAttributes.size() > 0)
                cartAttributesDaoService.deleteCartAttributes(cartAttributes);

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

       if(BigDecimalUtil.isLessThen(new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.ORDER_MAX_AMOUNT)), totalPrice)) {
           //Max order amount reached
           cartDto.setMessage("CRT007: Value of "+ systemPropertyService.readPrefValue(PreferenceType.CURRENCY) + systemPropertyService.readPrefValue(PreferenceType.ORDER_MAX_AMOUNT) + " can be order");
           cartDto.setValid(false);
           return cartDto;
       }


       if(BigDecimalUtil.isLessThen(totalPrice, cartEntities.get(0).getStoresBrand().getMinOrderAmount())) {
           //Minimum order value is
           cartDto.setMessage("CRT008:"+" "+systemPropertyService.readPrefValue(PreferenceType.CURRENCY)+ cartEntities.get(0).getStoresBrand().getMinOrderAmount());
           cartDto.setValid(false);
           return cartDto;
       }

        //Now Check if Min and Max Order Quanitity Has been changed from system
        boolean orderLimitationChanged = false;
        for(CartEntity cartEntity : cartEntities){
            if(cartEntity.getOrderQuantity()< cartEntity.getItem().getMinOrderQuantity() || cartEntity.getOrderQuantity() > cartEntity.getItem().getMaxOrderQuantity()) {
                orderLimitationChanged = true;
                Integer changeableOrderQn = cartEntity.getItem().getMinOrderQuantity();
                cartDaoService.updateMinOrderQuantity(cartEntity.getId(), changeableOrderQn);
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

        List<Integer> cartAttributes = cartAttributesDaoService.findCartAttributes(cartList);
        if (cartAttributes.size() > 0)
            cartAttributesDaoService.deleteCartAttributes(cartAttributes);

        cartDaoService.deleteCarts(cartList);
    }

    @Override
    public CartDto getCartSize(Long facebookId) throws Exception {
        log.info("++++++ Getting cart info of customer : " + facebookId + " +++++++++");
        List<CartEntity> carts = cartDaoService.getMyCarts(facebookId);

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
            throw new YSException("CRT001");

        //Getting Cart Attributes
        List<Integer> selectedAttributes = cartAttributesDaoService.findCartAttributes(cartEntity.getId());

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
        cartEntity.setItem(null);
        cartDto.setCart(cartEntity);
        cartDto.setItem(itemEntity);
        cartDto.setCurrency(systemPropertyService.readPrefValue(PreferenceType.CURRENCY));

        return cartDto;
    }

    @Override
    public void updateCart(CartEntity cart) throws Exception {
        log.info("+++++++++++ Updating Cart of cart id " + cart.getId() + " ++++++++++++++++++++++");

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

        // Now Update Cart
        cart.setModifiedDate(DateUtil.getCurrentTimestampSQL());
        cartDaoService.update(cart);

    }

    public String inviteFriend(HeaderDto headerDto, ArrayList<String> emailList) throws Exception{

        CustomerEntity client = customerDaoService.find(Long.parseLong(headerDto.getId()));

        String clientName = client.getUser().getFullName();
        Integer referredFriendsCount = client.getReferredFriendsCount();

        if(referredFriendsCount >= 3)
            throw new YSException("Your friend invitation already reached the maximum number.");


        boolean isLocal =  MessageBundle.isLocalHost();
        String clientUrl;
        if(isLocal) {
            clientUrl = "http://localhost:8080/anon/referral/"+headerDto.getId();
        }else{
            clientUrl = "http://delivr.com/anon/referral/"+headerDto.getId();
        }

        Integer cnt = 0;
        if(emailList.size()>0){
            for (String email: emailList){
                if(referredFriendsCount >= 3)
                    break;
                String serverUrl = getServerUrl();
                String body = "<p>Hi</p>";
                body += clientName+"<p> has invited you to use delivr application.</p>";
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
}
