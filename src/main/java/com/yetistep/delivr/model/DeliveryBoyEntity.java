package com.yetistep.delivr.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.yetistep.delivr.enums.DBoyStatus;
import com.yetistep.delivr.enums.VehicleType;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
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
    private List<DBoyOrderHistoryEntity> dBoyOrderHistories;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonManagedReference("dboy-user")
    @OneToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "user_id")
    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "deliveryBoy")
    public List<OrderEntity> getOrder() {
        return order;
    }

    public void setOrder(List<OrderEntity> order) {
        this.order = order;
    }

    @Column(name = "availability_status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    public DBoyStatus getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(DBoyStatus availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    @Column(name = "average_rating", nullable = false, precision = 4, scale = 2)
    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    @Column(name = "total_order_taken", nullable = false)
    public Integer getTotalOrderTaken() {
        return totalOrderTaken;
    }

    public void setTotalOrderTaken(Integer totalOrderTaken) {
        this.totalOrderTaken = totalOrderTaken;
    }

    @Column(name = "total_order_delivered", nullable = false)
    public Integer getTotalOrderDelivered() {
        return totalOrderDelivered;
    }

    public void setTotalOrderDelivered(Integer totalOrderDelivered) {
        this.totalOrderDelivered = totalOrderDelivered;
    }

    @Column(name = "total_order_undelivered", nullable = false)
    public Integer getTotalOrderUndelivered() {
        return totalOrderUndelivered;
    }

    public void setTotalOrderUndelivered(Integer totalOrderUndelivered) {
        this.totalOrderUndelivered = totalOrderUndelivered;
    }

    @Column(name = "total_earnings", nullable = false, precision = 16, scale = 4)
    public BigDecimal getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(BigDecimal totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    @Column(name = "vehicle_type", nullable = false)
    @Type(type="com.yetistep.delivr.enums.VehicleTypeCustom")
    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    @Column(name = "active_order_no", nullable = false)
    public Integer getActiveOrderNo() {
        return activeOrderNo;
    }

    public void setActiveOrderNo(Integer activeOrderNo) {
        this.activeOrderNo = activeOrderNo;
    }

    @Column(name = "available_amount", precision = 16, scale = 4)
    public BigDecimal getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(BigDecimal availableAmount) {
        this.availableAmount = availableAmount;
    }

    @Column(name = "advance_amount", precision = 16, scale = 4)
    public BigDecimal getAdvanceAmount() {
        return advanceAmount;
    }

    public void setAdvanceAmount(BigDecimal advanceAmount) {
        this.advanceAmount = advanceAmount;
    }

    @Column(name = "bank_amount", precision = 16, scale = 4)
    public BigDecimal getBankAmount() {
        return bankAmount;
    }

    public void setBankAmount(BigDecimal bankAmount) {
        this.bankAmount = bankAmount;
    }

    @Column(name = "wallet_amount", precision = 16, scale = 4)
    public BigDecimal getWalletAmount() {
        return walletAmount;
    }

    public void setWalletAmount(BigDecimal walletAmount) {
        this.walletAmount = walletAmount;
    }

    @Column(name = "previous_due", precision = 16, scale = 4)
    public BigDecimal getPreviousDue() {
        return previousDue;
    }

    public void setPreviousDue(BigDecimal previousDue) {
        this.previousDue = previousDue;
    }

    @Column(name="license_number", unique = true)
    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    @Column(name="vehicle_number")
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
}
