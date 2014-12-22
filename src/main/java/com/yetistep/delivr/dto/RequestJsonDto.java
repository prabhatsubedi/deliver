package com.yetistep.delivr.dto;

import com.yetistep.delivr.enums.PasswordActionType;
import com.yetistep.delivr.model.*;

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
    private List<Integer> categories;
    private List<PreferencesEntity> preferences;

    /*Add item properties*/
    private ItemEntity item;
    private List<CategoryEntity> itemCategories;
    private List<Integer> itemStores;
    private List<ItemsAttributesTypeEntity> itemAttributesTypes;
    private List<String> itemImages;

    /*Get child categories properties*/
    private Integer parentCategoryId;
    private Integer categoryStoreId;


    /*save order properties*/

    private OrderEntity ordersOrder;
    private List<ItemsOrderEntity> ordersItemsOrder;
    private Integer ordersBrandId;
    private Integer ordersCustomerId;
    private Integer ordersAddressId;







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
}
