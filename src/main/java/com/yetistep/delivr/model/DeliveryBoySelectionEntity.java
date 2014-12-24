package com.yetistep.delivr.model;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 12/19/14
 * Time: 2:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeliveryBoySelectionEntity {
    private Integer id;
    private BigDecimal storeToCustomerDistance;
    private BigDecimal distanceToStore;
    private Integer timeRequired;
    private DeliveryBoyEntity deliveryBoy;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getStoreToCustomerDistance() {
        return storeToCustomerDistance;
    }

    public void setStoreToCustomerDistance(BigDecimal storeToCustomerDistance) {
        this.storeToCustomerDistance = storeToCustomerDistance;
    }

    public BigDecimal getDistanceToStore() {
        return distanceToStore;
    }

    public void setDistanceToStore(BigDecimal distanceToStore) {
        this.distanceToStore = distanceToStore;
    }

    public Integer getTimeRequired() {
        return timeRequired;
    }

    public void setTimeRequired(Integer timeRequired) {
        this.timeRequired = timeRequired;
    }

    public DeliveryBoyEntity getDeliveryBoy() {
        return deliveryBoy;
    }

    public void setDeliveryBoy(DeliveryBoyEntity deliveryBoy) {
        this.deliveryBoy = deliveryBoy;
    }

    @Override
    public String toString() {
        return "DeliveryBoySelectionEntity{" +
                "id=" + id +
                ", storeToCustomerDistance=" + storeToCustomerDistance +
                ", distanceToStore=" + distanceToStore +
                ", timeRequired=" + timeRequired +
                ", deliveryBoy=" + deliveryBoy.getId() +
                '}';
    }
}
