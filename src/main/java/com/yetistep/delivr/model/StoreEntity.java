package com.yetistep.delivr.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.security.Timestamp;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 12/5/14
 * Time: 2:35 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "StoreEntity")
@Table(name="stores")
public class StoreEntity implements Serializable {

    private Integer id;
    private StoresBrandEntity storesBrand;
    private Set<CategoryEntity> category;
    private Set<ItemsStoreEntity> itemsStore;
    private Set<OrderEntity> order;
    private String name;
    private String street;
    private String city;
    private String state;
    private String country;
    private String contactNo;
    private String contactPerson;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Timestamp createdDate;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "stores_brand_id")
    public StoresBrandEntity getStoresBrand() {
        return storesBrand;
    }

    public void setStoresBrand(StoresBrandEntity storesBrand) {
        this.storesBrand = storesBrand;
    }

    @OneToMany(mappedBy = "store")
    public Set<CategoryEntity> getCategory() {
        return category;
    }

    public void setCategory(Set<CategoryEntity> category) {
        this.category = category;
    }

    @OneToMany(mappedBy = "store")
    public Set<ItemsStoreEntity> getItemsStore() {
        return itemsStore;
    }

    public void setItemsStore(Set<ItemsStoreEntity> itemsStore) {
        this.itemsStore = itemsStore;
    }

    @OneToMany(mappedBy = "store")
    public Set<OrderEntity> getOrder() {
        return order;
    }

    public void setOrder(Set<OrderEntity> order) {
        this.order = order;
    }

    @Column(name="name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name="street")
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @Column(name="state")
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Column(name="city", nullable = false)
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Column(name="country", nullable = false)
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Column(name="contact_no", nullable = false)
    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    @Column(name = "contact_person", nullable = false)
    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    @Column(name="latitude", nullable = false)
    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    @Column(name="longitude", nullable = false)
    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    @Column(name = "created_date", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }
}
