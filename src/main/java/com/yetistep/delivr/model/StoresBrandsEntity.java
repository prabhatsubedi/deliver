package com.yetistep.delivr.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 12/9/14
 * Time: 12:56 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "StoresBrandsEntity")
@Table(name="stores_brands")
public class StoresBrandsEntity {

   private Integer id;
   private MerchantEntity merchant;
   private Set<StoreEntity> store = new HashSet<StoreEntity>();
   private String brand_name;
   private Boolean featured;
   private Integer priority;
   private Timestamp created_date;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
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

    @Column(name = "brand_name")
    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
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
    public Timestamp getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Timestamp created_date) {
        this.created_date = created_date;
    }
}
