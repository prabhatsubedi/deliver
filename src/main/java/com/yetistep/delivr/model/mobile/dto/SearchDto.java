package com.yetistep.delivr.model.mobile.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 2/12/15
 * Time: 3:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchDto {
    private List<Items> items;
    private List<Stores> stores;

    public static class Items {
        private Integer itemId;
        private String itemName;
        private BigDecimal unitPrice;
        private String itemImageUrl;
        private String brandName;
        private String storeStreet;

        public Integer getItemId() {
            return itemId;
        }

        public void setItemId(Integer itemId) {
            this.itemId = itemId;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }

        public String getItemImageUrl() {
            return itemImageUrl;
        }

        public void setItemImageUrl(String itemImageUrl) {
            this.itemImageUrl = itemImageUrl;
        }

        public String getBrandName() {
            return brandName;
        }

        public void setBrandName(String brandName) {
            this.brandName = brandName;
        }

        public String getStoreStreet() {
            return storeStreet;
        }

        public void setStoreStreet(String storeStreet) {
            this.storeStreet = storeStreet;
        }
    }

    public static class Stores {

    }

}
