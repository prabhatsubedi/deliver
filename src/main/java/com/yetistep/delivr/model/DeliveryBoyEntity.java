package com.yetistep.delivr.model;


import com.yetistep.delivr.enums.DBoyStatus;
import com.yetistep.delivr.enums.VehicleType;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/19/14
 * Time: 11:19 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name="DeliveryBoyEntity")
@Table(name="delivery_boys")
public class DeliveryBoyEntity {

    private int id;
    private UserEntity user;
    private DBoyStatus availabilityStatus;
    private BigDecimal averageRating;
    private int totalOrderTaken;
    private int totalOrderDelivered;
    private int totalOrderUndelivered;
    private BigDecimal totalEarnings;
    private VehicleType vehicleType;
    private int activeOrderNo;
    private BigDecimal availableAmount;
    private String latitude;
    private String longitude;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
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
    public int getTotalOrderTaken() {
        return totalOrderTaken;
    }

    public void setTotalOrderTaken(int totalOrderTaken) {
        this.totalOrderTaken = totalOrderTaken;
    }

    @Column(name = "total_order_delivered", nullable = false)
    public int getTotalOrderDelivered() {
        return totalOrderDelivered;
    }

    public void setTotalOrderDelivered(int totalOrderDelivered) {
        this.totalOrderDelivered = totalOrderDelivered;
    }

    @Column(name = "total_order_undelivered", nullable = false)
    public int getTotalOrderUndelivered() {
        return totalOrderUndelivered;
    }

    public void setTotalOrderUndelivered(int totalOrderUndelivered) {
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
    public int getActiveOrderNo() {
        return activeOrderNo;
    }

    public void setActiveOrderNo(int activeOrderNo) {
        this.activeOrderNo = activeOrderNo;
    }

    @Column(name = "available_amount", precision = 16, scale = 4)
    public BigDecimal getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(BigDecimal availableAmount) {
        this.availableAmount = availableAmount;
    }

    @Column(name = "latitude")
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @Column(name = "longitude")
    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
