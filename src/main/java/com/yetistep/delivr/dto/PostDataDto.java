package com.yetistep.delivr.dto;

import com.yetistep.delivr.model.StoreEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 12/11/14
 * Time: 10:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class PostDataDto {

    /*Add store properties start*/
    private List<StoreEntity> stores = new ArrayList<StoreEntity>();
    private String brandName;
    private List<Integer> categories = new ArrayList<Integer>();

    public List<StoreEntity> getStores() {
        return stores;
    }

    public void setStores(List<StoreEntity> stores) {
        this.stores = stores;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public List<Integer> getCategories() {
        return categories;
    }

    public void setCategories(List<Integer> categories) {
        this.categories = categories;
    }
    /*Add store properties end*/
}
