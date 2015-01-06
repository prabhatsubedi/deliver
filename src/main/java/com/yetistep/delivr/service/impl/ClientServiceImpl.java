package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.dao.inf.*;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.model.CategoryEntity;
import com.yetistep.delivr.model.CustomerEntity;
import com.yetistep.delivr.model.StoreEntity;
import com.yetistep.delivr.model.StoresBrandEntity;
import com.yetistep.delivr.model.mobile.CategoryDto;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.model.mobile.PageInfo;
import com.yetistep.delivr.model.mobile.StaticPagination;
import com.yetistep.delivr.model.mobile.dto.ItemDto;
import com.yetistep.delivr.service.inf.ClientService;
import com.yetistep.delivr.util.BigDecimalUtil;
import com.yetistep.delivr.util.DateUtil;
import com.yetistep.delivr.util.GeoCodingUtil;
import com.yetistep.delivr.util.YSException;
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
public class ClientServiceImpl implements ClientService {
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
            CustomerEntity customerEntity =  customerDaoService.find(requestJsonDto.getCustomerInfo().getClientId());
            if(customerEntity == null)
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
            for(StoresBrandEntity storesBrandEntity : featuredBrands){
                ignoreList.add(storesBrandEntity.getId());
            }

            for(StoresBrandEntity storesBrandEntity : priorityBrands) {
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

        // Perform sorted store pagination
        PageInfo pageInfo = null;
        List<StoresBrandEntity> sortedList = new ArrayList<>();
        if(storeBrandResult.size() > 0){
            Integer pageId = 1;
            if(requestJsonDto.getPageInfo()!=null)
                pageId = requestJsonDto.getPageInfo().getPageNumber();

            StaticPagination staticPagination= new StaticPagination();
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
        List<CategoryDto> categoryDtoList = new ArrayList<>();
        CategoryDto categoryDto = null;
        List<CategoryEntity> categoryEntities = itemDaoService.findItemCategory(brandId);

        for(CategoryEntity categoryEntity : categoryEntities){
            categoryDto = new CategoryDto();
            if(categoryEntity.getParentId() == null){
                categoryDto.setValues(categoryEntity);
                categoryDto.setHasNext(false);
            } else {
                //Search  Main Parent Category
                CategoryEntity resultCat = categoryDaoService.find(categoryEntity.getId());
                Integer hasPrevParent = 0;
                CategoryEntity cat = null;
                //LOOP for parent (Search Parent and Category)
                while(hasPrevParent != null) {
                    // 2nd Level Search
                    if(resultCat.getParent().getParent()==null) {
                        categoryDto.setValues(resultCat.getParent());
                        break;
                    }

                    //Multi Level Search
                    if(cat == null)
                        cat = resultCat.getParent();
                    else
                        cat = cat.getParent();

                    if(cat == null || cat.getParent() == null)
                        break;

                    hasPrevParent = cat.getParent().getId();
                    categoryDto.setValues(cat.getParent());

                }

                categoryDto.setHasNext(true);
            }

           Boolean isAlreadyContain = false;
           for(CategoryDto temp : categoryDtoList){
               if(temp.getId() == categoryDto.getId())
                   isAlreadyContain  = true;
           }

           if(!isAlreadyContain)
                categoryDtoList.add(categoryDto);

        }
        return categoryDtoList;
    }

    @Override
    public List<CategoryDto> getSubCategory(CategoryDto categoryDto) throws Exception {
        log.info("+++++++++++++ Getting Sub Category Of " + categoryDto.getId() + " +++++++++");
        List<CategoryDto> list = new ArrayList<>();
        Integer brandId = categoryDto.getBrandId();
        Integer nextId = categoryDto.getId();
        List<CategoryEntity> categoryEntities = itemDaoService.findItemCategory(brandId);

        if(categoryEntities == null || categoryEntities.size() == 0)
            throw new YSException("VLD012");

        for(CategoryEntity categoryEntity : categoryEntities){
            CategoryEntity cat = null;
            CategoryEntity resultCat = categoryDaoService.find(categoryEntity.getId());
            CategoryDto resultDto = new CategoryDto();
            Integer hasNext = 0;
            while(hasNext !=null){

                if(resultCat.getParent().getId()==nextId) {
                    resultDto.setValues(resultCat);
                    resultDto.setHasNext(false);
                    hasNext = null;
                    break;
                }

                if(cat == null)
                    cat = resultCat.getParent();
                else
                    cat = cat.getParent();

                if(cat == null || cat.getParent() == null)
                    break;

                if(cat.getParent().getId() == nextId){
                    resultDto.setValues(cat);
                    resultDto.setHasNext(true);
                    hasNext = null;
                    break;
                }

                resultDto.setValues(cat.getParent());
            }

            if(hasNext == null){
                Boolean isAlreadyContain = false;
                for(CategoryDto temp : list){
                    if(temp.getId() == resultDto.getId())
                        isAlreadyContain  = true;
                }

                if(!isAlreadyContain)
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

    private void setStoreOpenSet(List<StoresBrandEntity> storesBrandEntities) throws Exception{
        for(StoresBrandEntity storesBrandEntity : storesBrandEntities) {
            storesBrandEntity.setOpenStatus(DateUtil.isTimeBetweenTwoTime(storesBrandEntity.getOpeningTime().toString(),
                    storesBrandEntity.getClosingTime().toString(), DateUtil.getCurrentTime().toString()));
        }
    }

    @Override
    public OrderEntity getOrderById(Integer orderId) throws Exception {
        OrderEntity order = orderDaoService.find(orderId);
        if(order == null){
           throw new YSException("VLD017");
        }

        AddressEntity address = new AddressEntity();
        address.setId(order.getAddress().getId());
        address.setStreet(order.getAddress().getStreet());
        address.setCity(order.getAddress().getCity());
        address.setState(order.getAddress().getState());
        address.setCountry(order.getAddress().getCountry());
        address.setCountryCode(order.getAddress().getCountryCode());
        address.setLatitude(order.getAddress().getLatitude());
        address.setLongitude(order.getAddress().getLongitude());
        order.setAddress(address);

        StoreEntity store = new StoreEntity();
        store.setName(order.getStore().getName());
        store.setCity(order.getStore().getCity());
        store.setState(order.getStore().getState());
        store.setCountry(order.getStore().getCountry());
        store.setContactNo(order.getStore().getContactNo());
        store.setLatitude(order.getStore().getLatitude());
        store.setLongitude(order.getStore().getLongitude());
        order.setStore(store);

        CustomerEntity customer = new CustomerEntity();
        customer.setId(order.getCustomer().getId());
        UserEntity user = new UserEntity();
        user.setId(order.getCustomer().getUser().getId());
        user.setFullName(order.getCustomer().getUser().getFullName());
        user.setMobileNumber(order.getCustomer().getUser().getMobileNumber());
        customer.setUser(user);
        order.setCustomer(customer);

        List<ItemsOrderEntity> itemsOrder = order.getItemsOrder();
        for(ItemsOrderEntity itemOrder: itemsOrder){
            ItemEntity item = new ItemEntity();
            item.setId(itemOrder.getItem().getId());
            item.setName(itemOrder.getItem().getName());
            itemOrder.setItem(item);
        }

        order.setDeliveryBoy(null);
        order.setAttachments(null);
        return order;
    }

    @Override
    public List<ItemDto> getItems(Integer brandId, Integer categoryId) throws Exception {
        log.info("++++++++++ Getting Items of Brand " + brandId + " +++++++++++");

        List<ItemDto> items = itemDaoService.findItems(brandId, categoryId);
        if(items.size() == 0)
            throw new YSException("ITM001");

        for(ItemDto itemDto : items){
            BigDecimal price = itemDto.getPrice();
            //Service Charge
            if(itemDto.getServiceCharge()!=null && BigDecimalUtil.isNotZero(itemDto.getServiceCharge())) {
                price = price.add(BigDecimalUtil.percentageOf(price, itemDto.getServiceCharge()));
            }

            //Vat
            if(itemDto.getVat()!=null && BigDecimalUtil.isNotZero(itemDto.getVat())) {
                price = price.add(BigDecimalUtil.percentageOf(price, itemDto.getVat()));
            }

            itemDto.setPrice(price);
            itemDto.setVat(null);
            itemDto.setServiceCharge(null);
            itemDto.setBrandId(brandId);
        }

        return items;

    }
}
