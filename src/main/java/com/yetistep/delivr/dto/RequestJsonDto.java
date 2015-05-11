package com.yetistep.delivr.dto;

import com.yetistep.delivr.enums.DeliveryStatus;
import com.yetistep.delivr.enums.NotifyTo;
import com.yetistep.delivr.enums.PasswordActionType;
import com.yetistep.delivr.enums.PaymentMode;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.model.mobile.CustomerInfo;
import com.yetistep.delivr.model.mobile.DeviceInfo;
import com.yetistep.delivr.model.mobile.GpsInfo;
import com.yetistep.delivr.model.mobile.PageInfo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    private PreferenceTypeEntity preferenceType;

    /*update categories*/
    private List<CategoryEntity> categoryList;


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
    private Integer parentCategoriesItemsCount;


    /*save order properties*/
    private OrderEntity ordersOrder;
    private List<ItemsOrderEntity> ordersItemsOrder;
    private Integer ordersBrandId;
    //private Integer ordersStoreId;
    private Long ordersCustomerId;
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
    private List<String> searchColumns;

    //add to custom cart
    private List<CartEntity> customCartList;

    //update string image
    private String imageString;
    private String prefKey;


    /*web search properties
    * */

    private String searchString;
    private List<Integer> brands;
    private Map<String, Date> dateRange;
    private Page page;

    /* Variable to decide whether to flush cart or not. */
    private Boolean flushCart;

    /*Get orders properties*/
    private DeliveryStatus deliveryStatus;

    /* Push notification service */
    private String pushMessage;
    private List<NotifyTo> notifyToList;

    /* Payment mode */
    private PaymentMode paymentMode;

    /*boy transaction account*/
    private String accountantNote;

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

    public PreferenceTypeEntity getPreferenceType() {
        return preferenceType;
    }

    public void setPreferenceType(PreferenceTypeEntity preferenceType) {
        this.preferenceType = preferenceType;
    }

    public List<CategoryEntity> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<CategoryEntity> categoryList) {
        this.categoryList = categoryList;
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

    public Integer getParentCategoriesItemsCount() {
        return parentCategoriesItemsCount;
    }

    public void setParentCategoriesItemsCount(Integer parentCategoriesItemsCount) {
        this.parentCategoriesItemsCount = parentCategoriesItemsCount;
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

//    public Integer getOrdersStoreId() {
//        return ordersStoreId;
//    }
//
//    public void setOrdersStoreId(Integer ordersStoreId) {
//        this.ordersStoreId = ordersStoreId;
//    }

    public Long getOrdersCustomerId() {
        return ordersCustomerId;
    }

    public void setOrdersCustomerId(Long ordersCustomerId) {
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

    public List<Integer> getBrands() {
        return brands;
    }

    public void setBrands(List<Integer> brands) {
        this.brands = brands;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public Boolean getFlushCart() {
        return flushCart;
    }

    public void setFlushCart(Boolean flushCart) {
        this.flushCart = flushCart;
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public Map<String, Date> getDateRange() {
        return dateRange;
    }

    public void setDateRange(Map<String, Date> dateRange) {
        this.dateRange = dateRange;
    }

    public String getPushMessage() {
        return pushMessage;
    }

    public void setPushMessage(String pushMessage) {
        this.pushMessage = pushMessage;
    }

    public List<NotifyTo> getNotifyToList() {
        return notifyToList;
    }

    public void setNotifyToList(List<NotifyTo> notifyToList) {
        this.notifyToList = notifyToList;
    }

    public PaymentMode getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(PaymentMode paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getImageString() {
        return imageString;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }

    public String getPrefKey() {
        return prefKey;
    }

    public void setPrefKey(String prefKey) {
        this.prefKey = prefKey;
    }

    public List<CartEntity> getCustomCartList() {
        return customCartList;
    }

    public void setCustomCartList(List<CartEntity> customCartList) {
        this.customCartList = customCartList;
    }


    public String getAccountantNote() {
        return accountantNote;
    }

    public void setAccountantNote(String accountantNote) {
        this.accountantNote = accountantNote;
    }
}
