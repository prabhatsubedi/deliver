package com.yetistep.delivr.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yetistep.delivr.enums.Status;
import com.yetistep.delivr.util.JsonDateSerializer;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 12/5/14
 * Time: 2:35 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "StoreEntity")
@Table(name="stores")
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class StoreEntity implements Serializable {

    private Integer id;
    private StoresBrandEntity storesBrand;
    private List<ItemsStoreEntity> itemsStore;
    private List<OrderEntity> order;
    private String name;
    private String street;
    private String city;
    private String state;
    private String country;
    private String contactNo;
    private String contactPerson;
    private String latitude;
    private String longitude;
    private Timestamp createdDate;
    private Status status;
    private BigDecimal customerToStoreDistance;//Transient Variable
    private String brandLogo;//Transient Variable


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    @JsonProperty
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "stores_brand_id")
    public StoresBrandEntity getStoresBrand() {
        return storesBrand;
    }

    public void setStoresBrand(StoresBrandEntity storesBrand) {
        this.storesBrand = storesBrand;
    }

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    @JsonProperty
    public List<ItemsStoreEntity> getItemsStore() {
        return itemsStore;
    }

    public void setItemsStore(List<ItemsStoreEntity> itemsStore) {
        this.itemsStore = itemsStore;
    }

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    public List<OrderEntity> getOrder() {
        return order;
    }

    public void setOrder(List<OrderEntity> order) {
        this.order = order;
    }


    @Column(name="name")
    @JsonProperty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name="street")
    @JsonProperty
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @Column(name="state")
    @JsonProperty
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Column(name="city")
    @JsonProperty
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Column(name="country")
    @JsonProperty
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Column(name="contact_no")
    @JsonProperty
    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    @Column(name = "contact_person")
    @JsonProperty
    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    @Column(name="latitude")
    @JsonProperty
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @Column(name="longitude")
    @JsonProperty
    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @JsonSerialize(using = JsonDateSerializer.class)
    @Column(name = "created_date", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @JsonProperty
    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    @Transient
    @JsonProperty
    public BigDecimal getCustomerToStoreDistance() {
        return customerToStoreDistance;
    }

    public void setCustomerToStoreDistance(BigDecimal customerToStoreDistance) {
        this.customerToStoreDistance = customerToStoreDistance;
    }

    @Transient
    @JsonProperty
    public String getBrandLogo() {
        return brandLogo;
    }

    public void setBrandLogo(String brandLogo) {
        this.brandLogo = brandLogo;
    }

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    @JsonProperty
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
