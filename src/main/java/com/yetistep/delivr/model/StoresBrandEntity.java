package com.yetistep.delivr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 12/9/14
 * Time: 12:56 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "StoresBrandEntity")
@Table(name="stores_brands")
public class StoresBrandEntity implements Serializable {

   private Integer id;
   private MerchantEntity merchant;
   private Set<StoreEntity> store = new HashSet<StoreEntity>();
   private List<BrandsCategoryEntity> brandsCategory = new ArrayList<BrandsCategoryEntity>();
   private String brandName;
   private Boolean featured;
   private Integer priority;
   private Timestamp createdDate;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "merchant_id")
    public MerchantEntity getMerchant() {
        return merchant;
    }

    public void setMerchant(MerchantEntity merchant) {
        this.merchant = merchant;
    }

    @OneToMany(mappedBy = "storesBrand")
    public Set<StoreEntity> getStore() {
        return store;
    }

    public void setStore(Set<StoreEntity> store) {
        this.store = store;
    }

    @OneToMany(mappedBy = "storesBrand", cascade = CascadeType.ALL)
    public List<BrandsCategoryEntity> getBrandsCategory() {
        return brandsCategory;
    }

    public void setBrandsCategory(List<BrandsCategoryEntity> brandsCategory) {
        this.brandsCategory = brandsCategory;
    }

    @Column(name = "brand_name")
    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    @Column(name = "featured", columnDefinition = "int default '0'")
    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    @Column(name = "priority")
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Column(name = "created_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @JsonSerialize
    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }
}
