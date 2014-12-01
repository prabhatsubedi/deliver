package com.yetistep.delivr.model;



import javax.persistence.*;

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
    private int gender;
    private Boolean availabilityStatus;
    private int averageRating;
    private int totalOrderTaken;
    private int totalOrderDelivered;
    private int totalOrderUndelivered;
    private int totalEarnings;
    private int vehicleType;
    private int activeOrderNo;
    private int availableAmount;
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

    @Column(name = "gender", nullable = false)
    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    @Column(name = "availability_status", nullable = false)
    public Boolean getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(Boolean availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    @Column(name = "average_rating", nullable = false)
    public int getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(int averageRating) {
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

    @Column(name = "total_earnings", nullable = false)
    public int getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(int totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    @Column(name = "vehicle_type", nullable = false)
    public int getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(int vehicleType) {
        this.vehicleType = vehicleType;
    }

    @Column(name = "active_order_no", nullable = false)
    public int getActiveOrderNo() {
        return activeOrderNo;
    }

    public void setActiveOrderNo(int activeOrderNo) {
        this.activeOrderNo = activeOrderNo;
    }

    @Column(name = "available_amount")
    public int getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(int availableAmount) {
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
