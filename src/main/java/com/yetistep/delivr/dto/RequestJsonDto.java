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
    private List<ItemsAttributeEntity> itemsAttributes;
    private List<ItemsImageEntity> itemsImages;

    /*Get child categories properties*/
    private Integer parentCategoryId;
    private Integer categoryStoreId;






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

    public List<ItemsAttributeEntity> getItemsAttributes() {
        return itemsAttributes;
    }

    public void setItemsAttributes(List<ItemsAttributeEntity> itemsAttributes) {
        this.itemsAttributes = itemsAttributes;
    }

    public List<ItemsImageEntity> getItemsImages() {
        return itemsImages;
    }

    public void setItemsImages(List<ItemsImageEntity> itemsImages) {
        this.itemsImages = itemsImages;
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
}
