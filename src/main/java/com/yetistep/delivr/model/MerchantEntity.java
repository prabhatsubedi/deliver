package com.yetistep.delivr.model;

import com.yetistep.delivr.enums.MerchantType;
import com.yetistep.delivr.enums.Status;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

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
    private Boolean partnershipStatus;
    private BigDecimal commissionPercentage;
    private BigDecimal serviceFee;
    private String website;
    private String agreementDetail;
    private String businessTitle;
    private String businessLogo;
    private String companyRegistrationNo;
    private String vatNo;
    private String panNo;
    private Status status;
    private List<StoresBrandEntity> storesBrand;
    private List<InvoiceEntity> invoices;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
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
    public List<StoresBrandEntity> getStoresBrand() {
        return storesBrand;
    }

    public void setStoresBrand(List<StoresBrandEntity> storesBrand) {
        this.storesBrand = storesBrand;
    }

    @Column(name = "partnership_status", columnDefinition = "TINYINT(1)")
    public Boolean getPartnershipStatus() {
        return partnershipStatus;
    }

    public void setPartnershipStatus(Boolean partnershipStatus) {
        this.partnershipStatus = partnershipStatus;
    }

    @Column(name = "commission_percentage", columnDefinition="Decimal(6,2)")
    public BigDecimal getCommissionPercentage() {
        return commissionPercentage;
    }

    public void setCommissionPercentage(BigDecimal commissionPercentage) {
        this.commissionPercentage = commissionPercentage;
    }

    @Column(name = "service_fee", columnDefinition="Decimal(16,2)")
    public BigDecimal getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(BigDecimal serviceFee) {
        this.serviceFee = serviceFee;
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

    @Column(name = "business_title")
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

    @Column(name = "pan_no")
    public String getPanNo() {
        return panNo;
    }

    public void setPanNo(String panNo) {
        this.panNo = panNo;
    }

    @Transient
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @OneToMany(mappedBy = "merchant")
    public List<InvoiceEntity> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<InvoiceEntity> invoices) {
        this.invoices = invoices;
    }
}
