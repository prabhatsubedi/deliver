package com.yetistep.delivr.model;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yetistep.delivr.enums.DBoyStatus;
import com.yetistep.delivr.enums.VehicleType;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/19/14
 * Time: 11:19 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name="DeliveryBoyEntity")
@Table(name="delivery_boys")
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = DeliveryBoyEntity.class)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class DeliveryBoyEntity implements Serializable {

    private Integer id;
    private UserEntity user;
    private List<OrderEntity> order;
    private DBoyStatus availabilityStatus;
    private BigDecimal averageRating;
    private Integer totalOrderTaken;
    private Integer totalOrderDelivered;
    private Integer totalOrderUndelivered;
    private BigDecimal totalEarnings;
    private VehicleType vehicleType;
    private Integer activeOrderNo;
    private BigDecimal availableAmount;
    private BigDecimal bankAmount;
    private BigDecimal walletAmount;
    private BigDecimal advanceAmount;
    private BigDecimal previousDue;
    private String licenseNumber;
    private String vehicleNumber;
    private String bankAccountNumber;
    private List<DBoyOrderHistoryEntity> dBoyOrderHistories;
    private List<DeliveryBoySelectionEntity> deliveryBoySelections;
    private List<DBoyAdvanceAmountEntity> dBoyAdvanceAmounts;
    private List<DBoyPaymentEntity> dBoyPayments;
    //private List<DBoySubmittedAmountEntity> dBoySubmittedAmount;
    private String latitude;
    private String longitude;
    private Timestamp lastLocationUpdate;
    private BigDecimal itemReturnedTotal; //transient variable

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @JsonProperty
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    @JsonProperty
    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @JsonProperty
    @OneToMany(mappedBy = "deliveryBoy")
    public List<OrderEntity> getOrder() {
        return order;
    }

    public void setOrder(List<OrderEntity> order) {
        this.order = order;
    }

    @Column(name = "availability_status")
    @Enumerated(EnumType.ORDINAL)
    @JsonProperty
    public DBoyStatus getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(DBoyStatus availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    @Column(name = "average_rating", precision = 4, scale = 2)
    @JsonProperty
    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    @Column(name = "total_order_taken")
    @JsonProperty
    public Integer getTotalOrderTaken() {
        return totalOrderTaken;
    }

    public void setTotalOrderTaken(Integer totalOrderTaken) {
        this.totalOrderTaken = totalOrderTaken;
    }

    @Column(name = "total_order_delivered")
    @JsonProperty
    public Integer getTotalOrderDelivered() {
        return totalOrderDelivered;
    }

    public void setTotalOrderDelivered(Integer totalOrderDelivered) {
        this.totalOrderDelivered = totalOrderDelivered;
    }

    @Column(name = "total_order_undelivered")
    @JsonProperty
    public Integer getTotalOrderUndelivered() {
        return totalOrderUndelivered;
    }

    public void setTotalOrderUndelivered(Integer totalOrderUndelivered) {
        this.totalOrderUndelivered = totalOrderUndelivered;
    }

    @Column(name = "total_earnings", precision = 16, scale = 2)
    @JsonProperty
    public BigDecimal getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(BigDecimal totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    @Column(name = "vehicle_type")
    @Type(type="com.yetistep.delivr.enums.VehicleTypeCustom")
    @JsonProperty
    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    @Column(name = "active_order_no")
    @JsonProperty
    public Integer getActiveOrderNo() {
        return activeOrderNo;
    }

    public void setActiveOrderNo(Integer activeOrderNo) {
        this.activeOrderNo = activeOrderNo;
    }

    @Column(name = "available_amount", precision = 16, scale = 2)
    @JsonProperty
    public BigDecimal getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(BigDecimal availableAmount) {
        this.availableAmount = availableAmount;
    }

    @Column(name = "advance_amount", precision = 16, scale = 2)
    @JsonProperty
    public BigDecimal getAdvanceAmount() {
        return advanceAmount;
    }

    public void setAdvanceAmount(BigDecimal advanceAmount) {
        this.advanceAmount = advanceAmount;
    }

    @Column(name = "bank_amount", precision = 16, scale = 2)
    @JsonProperty
    public BigDecimal getBankAmount() {
        return bankAmount;
    }

    public void setBankAmount(BigDecimal bankAmount) {
        this.bankAmount = bankAmount;
    }

    @Column(name = "wallet_amount", precision = 16, scale = 2)
    @JsonProperty
    public BigDecimal getWalletAmount() {
        return walletAmount;
    }

    public void setWalletAmount(BigDecimal walletAmount) {
        this.walletAmount = walletAmount;
    }

    @Column(name = "previous_due", precision = 16, scale = 2)
    @JsonProperty
    public BigDecimal getPreviousDue() {
        return previousDue;
    }

    public void setPreviousDue(BigDecimal previousDue) {
        this.previousDue = previousDue;
    }

    @Column(name="license_number", unique = true)
    @JsonProperty
    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    @Column(name="vehicle_number")
    @JsonProperty
    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "deliveryBoy")
    public List<DBoyOrderHistoryEntity> getdBoyOrderHistories() {
        return dBoyOrderHistories;
    }

    public void setdBoyOrderHistories(List<DBoyOrderHistoryEntity> dBoyOrderHistories) {
        this.dBoyOrderHistories = dBoyOrderHistories;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "deliveryBoy")
    public List<DeliveryBoySelectionEntity> getDeliveryBoySelections() {
        return deliveryBoySelections;
    }

    public void setDeliveryBoySelections(List<DeliveryBoySelectionEntity> deliveryBoySelections) {
        this.deliveryBoySelections = deliveryBoySelections;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "deliveryBoy", cascade = { CascadeType.PERSIST, CascadeType.MERGE} )
    public List<DBoyAdvanceAmountEntity> getdBoyAdvanceAmounts() {
        return dBoyAdvanceAmounts;
    }

    public void setdBoyAdvanceAmounts(List<DBoyAdvanceAmountEntity> dBoyAdvanceAmounts) {
        this.dBoyAdvanceAmounts = dBoyAdvanceAmounts;
    }

    @OneToMany(mappedBy = "deliveryBoy", cascade = { CascadeType.PERSIST, CascadeType.MERGE})
    public List<DBoyPaymentEntity> getdBoyPayments() {
        return dBoyPayments;
    }

    public void setdBoyPayments(List<DBoyPaymentEntity> dBoyPayments) {
        this.dBoyPayments = dBoyPayments;
    }

/*@JsonIgnore
    @OneToMany(mappedBy = "deliveryBoy", cascade = { CascadeType.PERSIST, CascadeType.MERGE} )
    public List<DBoySubmittedAmountEntity> getdBoySubmittedAmount() {
        return dBoySubmittedAmount;
    }

    public void setdBoySubmittedAmount(List<DBoySubmittedAmountEntity> dBoySubmittedAmount) {
        this.dBoySubmittedAmount = dBoySubmittedAmount;
    }*/

    @Column(name = "latitude")
    @JsonProperty
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @Column(name = "longitude")
    @JsonProperty
    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Column(name = "bank_account_number")
    @JsonProperty
    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    @JsonIgnore
    @Column(name="last_location_update", columnDefinition="TIMESTAMP NULL DEFAULT NULL")
    public Timestamp getLastLocationUpdate() {
        return lastLocationUpdate;
    }

    public void setLastLocationUpdate(Timestamp lastLocationUpdate) {
        this.lastLocationUpdate = lastLocationUpdate;
    }


    @Transient
    @JsonProperty
    public BigDecimal getItemReturnedTotal() {
        return itemReturnedTotal;
    }

    public void setItemReturnedTotal(BigDecimal itemReturnedTotal) {
        this.itemReturnedTotal = itemReturnedTotal;
    }
}
