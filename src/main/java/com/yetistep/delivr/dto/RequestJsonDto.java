package com.yetistep.delivr.dto;

import com.yetistep.delivr.enums.PasswordActionType;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.model.mobile.CustomerInfo;
import com.yetistep.delivr.model.mobile.DeviceInfo;
import com.yetistep.delivr.model.mobile.GpsInfo;
import com.yetistep.delivr.model.mobile.PageInfo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 12/11/14
 * Time: 10:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class RequestJsonDto {

    /* Password Action Type */
   /* ====== Used at AnonController.changePassword ===== */
    private PasswordActionType actionType;

    /*Add store properties*/
    private List<StoreEntity> stores;
    private StoresBrandEntity storesBrand;
    private List<StoresBrandEntity> storesBrandEntities;
    private List<Integer> categories;
    private List<PreferencesEntity> preferences;

    /*Add item properties*/
    private ItemEntity item;
    private List<CategoryEntity> itemCategories;
    private List<Integer> itemStores;
    private List<ItemsAttributesTypeEntity> itemAttributesTypes;
    private List<String> itemImages;
    private List<ItemsImageEntity> editItemImages;

    /*Get child categories properties*/
    private Integer parentCategoryId;
    private Integer categoryStoreId;


    /*save order properties*/
    private OrderEntity ordersOrder;
    private List<ItemsOrderEntity> ordersItemsOrder;
    private Integer ordersBrandId;
    private Integer ordersStoreId;
    private Integer ordersCustomerId;
    private Integer ordersAddressId;

    /*update account properties*/
    private BigDecimal submittedAmount;
    private BigDecimal advanceAmount;
    /* For Mobile */
    private GpsInfo gpsInfo;
    private DeviceInfo deviceInfo;
    private CustomerInfo customerInfo;
    private PageInfo pageInfo;
    private UserDeviceEntity customerDevice;
    private CustomerEntity customer;
    /*For accepting delivery order*/
    private Integer orderId;
    private String className;
    private Integer statusId;


    /*web search properties
    * */
    private String searchString;




    /*=================================================================== */
    public PasswordActionType getActionType() {
        return actionType;
    }

    public void setActionType(PasswordActionType actionType) {
        this.actionType = actionType;
    }

    public List<StoreEntity> getStores() {
        return stores;
    }

    public void setStores(List<StoreEntity> stores) {
        this.stores = stores;
    }

    public StoresBrandEntity getStoresBrand() {
        return storesBrand;
    }

    public void setStoresBrand(StoresBrandEntity storesBrand) {
        this.storesBrand = storesBrand;
    }

    public List<StoresBrandEntity> getStoresBrandEntities() {
        return storesBrandEntities;
    }

    public void setStoresBrandEntities(List<StoresBrandEntity> storesBrandEntities) {
        this.storesBrandEntities = storesBrandEntities;
    }

    public List<Integer> getCategories() {
        return categories;
    }

    public void setCategories(List<Integer> categories) {
        this.categories = categories;
    }

    public List<PreferencesEntity> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<PreferencesEntity> preferences) {
        this.preferences = preferences;
    }

    public ItemEntity getItem() {
        return item;
    }

    public void setItem(ItemEntity item) {
        this.item = item;
    }

    public List<CategoryEntity> getItemCategories() {
        return itemCategories;
    }

    public void setItemCategories(List<CategoryEntity> itemCategories) {
        this.itemCategories = itemCategories;
    }

    public List<Integer> getItemStores() {
        return itemStores;
    }

    public void setItemStores(List<Integer> itemStores) {
        this.itemStores = itemStores;
    }

    public List<ItemsAttributesTypeEntity> getItemAttributesTypes() {
        return itemAttributesTypes;
    }

    public void setItemAttributesTypes(List<ItemsAttributesTypeEntity> itemAttributesTypes) {
        this.itemAttributesTypes = itemAttributesTypes;
    }

    public List<String> getItemImages() {
        return itemImages;
    }

    public void setItemImages(List<String> itemImages) {
        this.itemImages = itemImages;
    }

    public List<ItemsImageEntity> getEditItemImages() {
        return editItemImages;
    }

    public void setEditItemImages(List<ItemsImageEntity> editItemImages) {
        this.editItemImages = editItemImages;
    }

    public Integer getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Integer parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public Integer getCategoryStoreId() {
        return categoryStoreId;
    }

    public void setCategoryStoreId(Integer categoryStoreId) {
        this.categoryStoreId = categoryStoreId;
    }

    public OrderEntity getOrdersOrder() {
        return ordersOrder;
    }

    public void setOrdersOrder(OrderEntity ordersOrder) {
        this.ordersOrder = ordersOrder;
    }

    public List<ItemsOrderEntity> getOrdersItemsOrder() {
        return ordersItemsOrder;
    }

    public void setOrdersItemsOrder(List<ItemsOrderEntity> ordersItemsOrder) {
        this.ordersItemsOrder = ordersItemsOrder;
    }

    public Integer getOrdersBrandId() {
        return ordersBrandId;
    }

    public void setOrdersBrandId(Integer ordersBrandId) {
        this.ordersBrandId = ordersBrandId;
    }

    public Integer getOrdersStoreId() {
        return ordersStoreId;
    }

    public void setOrdersStoreId(Integer ordersStoreId) {
        this.ordersStoreId = ordersStoreId;
    }

    public Integer getOrdersCustomerId() {
        return ordersCustomerId;
    }

    public void setOrdersCustomerId(Integer ordersCustomerId) {
        this.ordersCustomerId = ordersCustomerId;
    }

    public Integer getOrdersAddressId() {
        return ordersAddressId;
    }

    public void setOrdersAddressId(Integer ordersAddressId) {
        this.ordersAddressId = ordersAddressId;
    }

    public BigDecimal getSubmittedAmount() {
        return submittedAmount;
    }

    public void setSubmittedAmount(BigDecimal submittedAmount) {
        this.submittedAmount = submittedAmount;
    }

    public BigDecimal getAdvanceAmount() {
        return advanceAmount;
    }

    public void setAdvanceAmount(BigDecimal advanceAmount) {
        this.advanceAmount = advanceAmount;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public GpsInfo getGpsInfo() {
        return gpsInfo;
    }

    public void setGpsInfo(GpsInfo gpsInfo) {
        this.gpsInfo = gpsInfo;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public CustomerInfo getCustomerInfo() {
        return customerInfo;
    }

    public UserDeviceEntity getCustomerDevice() {
        return customerDevice;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public void setCustomerDevice(UserDeviceEntity customerDevice) {
        this.customerDevice = customerDevice;
    }

    public void setCustomerInfo(CustomerInfo customerInfo) {
        this.customerInfo = customerInfo;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }
}
