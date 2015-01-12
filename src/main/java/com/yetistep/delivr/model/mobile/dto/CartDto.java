package com.yetistep.delivr.model.mobile.dto;

import com.yetistep.delivr.model.CartEntity;
import com.yetistep.delivr.model.StoresBrandEntity;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 1/9/15
 * Time: 2:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class CartDto {
    StoresBrandEntity storesBrand;
    List<CartEntity> carts;
    Integer totalCart;

    public StoresBrandEntity getStoresBrand() {
        return storesBrand;
    }

    public void setStoresBrand(StoresBrandEntity storesBrand) {
        this.storesBrand = storesBrand;
    }

    public List<CartEntity> getCarts() {
        return carts;
    }

    public void setCarts(List<CartEntity> carts) {
        this.carts = carts;
    }

    public Integer getTotalCart() {
        return totalCart;
    }

    public void setTotalCart(Integer totalCart) {
        this.totalCart = totalCart;
    }
}
