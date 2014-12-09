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
    private MerchantEntity merchant;
    private Set<ItemsStoreEntity> itemsStore = new HashSet<ItemsStoreEntity>();
    private Set<OrderEntity> order = new HashSet<OrderEntity>();
    private String name;
    private String street;
    private String locality;
    private String city;
    private String country;
    private String contact_no;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Timestamp created_date;
    private String return_policy;
    private Integer delivery_fee;
    private String promo_code;
    private BigDecimal vat;
    private BigDecimal service_charge;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
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

    @Column(name="locality")
    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
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
    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
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

    @Column(name="return_policy")
    public String getReturn_policy() {
        return return_policy;
    }

    public void setReturn_policy(String return_policy) {
        this.return_policy = return_policy;
    }

    @Column(name="delivery_fee")
    public Integer getDelivery_fee() {
        return delivery_fee;
    }

    public void setDelivery_fee(Integer delivery_fee) {
        this.delivery_fee = delivery_fee;
    }

    @Column(name="promo_code")
    public String getPromo_code() {
        return promo_code;
    }

    public void setPromo_code(String promo_code) {
        this.promo_code = promo_code;
    }

    @Column(name="vat")
    public BigDecimal getVat() {
        return vat;
    }

    public void setVat(BigDecimal vat) {
        this.vat = vat;
    }

    @Column(name="service_charge")
    public BigDecimal getService_charge() {
        return service_charge;
    }

    public void setService_charge(BigDecimal service_charge) {
        this.service_charge = service_charge;
    }


    @Column(name = "created_date", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    public Timestamp getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Timestamp created_date) {
        this.created_date = created_date;
    }
}
