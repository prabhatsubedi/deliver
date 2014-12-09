package com.yetistep.delivr.model;

import com.yetistep.delivr.enums.MerchantType;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.metamodel.EmbeddableType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/19/14
 * Time: 11:18 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name="MerchantEntity")
@Table(name="merchants")

@DynamicUpdate
public class MerchantEntity implements Serializable {

    private Integer id;
    private UserEntity user;
    private MerchantType type;
    private Set<StoreEntity> store = new HashSet<StoreEntity>();
    private Boolean partnershipStatus;
    private BigDecimal commissionPercentage;
    private String website;
    private String agreementDetail;
    private String businessTitle;
    private String businessLogo;
    private String companyRegistrationNo;
    private String vatNo;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @OneToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "user_id")
    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @Column(name = "type")
    @Type(type="com.yetistep.delivr.enums.MerchantTypeCustom")
    public MerchantType getType() {
        return type;
    }

    public void setType(MerchantType type) {
        this.type = type;
    }

    @OneToMany(mappedBy = "merchant")
    public Set<StoreEntity> getStore() {
        return store;
    }

    public void setStore(Set<StoreEntity> store) {
        this.store = store;
    }

    @Column(name = "partnership_status", nullable = false)
    public Boolean getPartnershipStatus() {
        return partnershipStatus;
    }

    public void setPartnershipStatus(Boolean partnershipStatus) {
        this.partnershipStatus = partnershipStatus;
    }

    @Column(name = "commission_percentage", nullable = false, columnDefinition="Decimal(6,4)")
    public BigDecimal getCommissionPercentage() {
        return commissionPercentage;
    }

    public void setCommissionPercentage(BigDecimal commissionPercentage) {
        this.commissionPercentage = commissionPercentage;
    }


    @Column(name = "website")
    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Column(name = "agreement_detail", columnDefinition = "longtext")
    public String getAgreementDetail() {
        return agreementDetail;
    }

    public void setAgreementDetail(String agreementDetail) {
        this.agreementDetail = agreementDetail;
    }

    @Column(name = "business_title", nullable = false)
    public String getBusinessTitle() {
        return businessTitle;
    }

    public void setBusinessTitle(String businessTitle) {
        this.businessTitle = businessTitle;
    }

    @Column(name="business_logo")
    public String getBusinessLogo() {
        return businessLogo;
    }

    public void setBusinessLogo(String businessLogo) {
        this.businessLogo = businessLogo;
    }

    @Column(name = "registration_no")
    public String getCompanyRegistrationNo() {
        return companyRegistrationNo;
    }

    public void setCompanyRegistrationNo(String companyRegistrationNo) {
        this.companyRegistrationNo = companyRegistrationNo;
    }

    @Column(name = "vat_no")
    public String getVatNo() {
        return vatNo;
    }

    public void setVatNo(String vatNo) {
        this.vatNo = vatNo;
    }
}
