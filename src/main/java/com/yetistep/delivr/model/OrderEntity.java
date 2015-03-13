package com.yetistep.delivr.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yetistep.delivr.enums.DeliveryStatus;
import com.yetistep.delivr.enums.JobOrderStatus;
import com.yetistep.delivr.util.JsonDateSerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 12/5/14
 * Time: 2:38 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name="OrderEntity")
@Table(name = "orders")
public class OrderEntity implements Serializable {


    private Integer id;
    private String orderName;
    private String orderVerificationCode;
    private Boolean orderVerificationStatus;
    private DeliveryStatus deliveryStatus;
    private JobOrderStatus orderStatus;
    //private ItemEntity item;
    private List<ItemsOrderEntity> itemsOrder;
    private AddressEntity address;
    private DeliveryBoyEntity deliveryBoy;
    private CustomerEntity customer;
    private StoreEntity store;
    private BigDecimal customerChargeableDistance; //Paid by customer
    private BigDecimal systemChargeableDistance; //Paid by system
    //private BigDecimal itemTotal;
    private BigDecimal totalCost;
    private BigDecimal transportationCharge;
    private BigDecimal systemServiceCharge;
    private BigDecimal deliveryCharge;
    private BigDecimal grandTotal;
    private BigDecimal deliveryBoyShare;
    private BigDecimal systemShare;
    private Timestamp orderDate;
    private List<DBoyOrderHistoryEntity> dBoyOrderHistories;
    private List<DeliveryBoySelectionEntity> deliveryBoySelections;
    private Integer assignedTime;
    private Integer remainingTime;
    private Integer elapsedTime;
    private RatingEntity rating;
    private List<String> attachments;
    private OrderCancelEntity orderCancel;
    private CourierTransactionEntity courierTransaction;
    private Integer surgeFactor;
    private BigDecimal itemServiceAndVatCharge;
    private InvoiceEntity invoice;
    private Integer reprocessTime;
    private BillEntity bill;
    private ReceiptEntity receipt;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "order_name")
    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    @Column(name = "order_verification_code")
    public String getOrderVerificationCode() {
        return orderVerificationCode;
    }

    public void setOrderVerificationCode(String orderVerificationCode) {
        this.orderVerificationCode = orderVerificationCode;
    }

    @Column(name = "order_verification_status", columnDefinition = "TINYINT(1)")
    public Boolean getOrderVerificationStatus() {
        return orderVerificationStatus;
    }

    public void setOrderVerificationStatus(Boolean orderVerificationStatus) {
        this.orderVerificationStatus = orderVerificationStatus;
    }

    @Column(name = "delivery_status")
    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    @Column(name = "order_status")
    public JobOrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(JobOrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

   /* @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    public ItemEntity getItem() {
        return item;
    }

    public void setItem(ItemEntity item) {
        this.item = item;
    }*/

    @OneToMany(mappedBy = "order", cascade = { CascadeType.PERSIST, CascadeType.MERGE} )
    public List<ItemsOrderEntity> getItemsOrder() {
        return itemsOrder;
    }

    public void setItemsOrder(List<ItemsOrderEntity> itemsOrder) {
        this.itemsOrder = itemsOrder;
    }

    @ManyToOne
    @JoinColumn(name = "address_id")
    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    @ManyToOne
    @JoinColumn(name = "delivery_boy_id")
    public DeliveryBoyEntity getDeliveryBoy() {
        return deliveryBoy;
    }

    public void setDeliveryBoy (DeliveryBoyEntity deliveryBoy) {
        this.deliveryBoy = deliveryBoy;
    }

    @ManyToOne
    @JoinColumn(name = "customer_id")
    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    @ManyToOne
    @JoinColumn(name = "store_id")
    public StoreEntity getStore() {
        return store;
    }

    public void setStore(StoreEntity store) {
        this.store = store;
    }

    @Column(name = "customer_chargeable_distance", precision = 19, scale = 2)
    public BigDecimal getCustomerChargeableDistance() {
        return customerChargeableDistance;
    }

    public void setCustomerChargeableDistance(BigDecimal customerChargeableDistance) {
        this.customerChargeableDistance = customerChargeableDistance;
    }

    @Column(name = "system_chargeable_distance", precision = 19, scale = 2)
    public BigDecimal getSystemChargeableDistance() {
        return systemChargeableDistance;
    }

    public void setSystemChargeableDistance(BigDecimal systemChargeableDistance) {
        this.systemChargeableDistance = systemChargeableDistance;
    }

    /*@Column(name = "item_total", precision = 4, scale = 2)
    public BigDecimal getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(BigDecimal itemTotal) {
        this.itemTotal = itemTotal;
    }*/

    @Column(name = "total_cost", precision = 19, scale = 2)
    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    @Column(name = "transportation_charge", precision = 19, scale = 2)
    public BigDecimal getTransportationCharge() {
        return transportationCharge;
    }

    public void setTransportationCharge(BigDecimal transportationCharge) {
        this.transportationCharge = transportationCharge;
    }

    @Column(name = "system_service_charge", precision = 19, scale = 2)
    public BigDecimal getSystemServiceCharge() {
        return systemServiceCharge;
    }

    public void setSystemServiceCharge(BigDecimal systemServiceCharge) {
        this.systemServiceCharge = systemServiceCharge;
    }

    @Column(name = "delivery_charge", precision = 19, scale = 2)
    public BigDecimal getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(BigDecimal deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    @Column(name = "grand_total", precision = 19, scale = 2)
    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }

    @Column(name = "delivery_boy_share", precision = 19, scale = 2)
    public BigDecimal getDeliveryBoyShare() {
        return deliveryBoyShare;
    }

    public void setDeliveryBoyShare (BigDecimal deliveryBoyShare) {
        this.deliveryBoyShare = deliveryBoyShare;
    }

    @Column(name = "system_share", precision = 19, scale = 2)
    public BigDecimal getSystemShare() {
        return systemShare ;
    }

    public void setSystemShare(BigDecimal systemShare) {
        this.systemShare = systemShare;
    }


    @JsonSerialize(using = JsonDateSerializer.class)
    @Column(name = "order_date", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    @OneToMany(mappedBy = "order", cascade = { CascadeType.PERSIST, CascadeType.MERGE} )
    public List<DBoyOrderHistoryEntity> getdBoyOrderHistories() {
        return dBoyOrderHistories;
    }

    public void setdBoyOrderHistories(List<DBoyOrderHistoryEntity> dBoyOrderHistories) {
        this.dBoyOrderHistories = dBoyOrderHistories;
    }

    @OneToMany(mappedBy = "order", cascade = { CascadeType.PERSIST, CascadeType.MERGE} )
    public List<DeliveryBoySelectionEntity> getDeliveryBoySelections() {
        return deliveryBoySelections;
    }

    public void setDeliveryBoySelections(List<DeliveryBoySelectionEntity> deliveryBoySelections) {
        this.deliveryBoySelections = deliveryBoySelections;
    }

    @Column(name = "assigned_time")
    public Integer getAssignedTime() {
        return assignedTime;
    }

    public void setAssignedTime(Integer assignedTime) {
        this.assignedTime = assignedTime;
    }

    @Column(name = "remaining_time")
    public Integer getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(Integer remainingTime) {
        this.remainingTime = remainingTime;
    }

    @Transient
    public Integer getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(Integer elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    @OneToOne(mappedBy = "order", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
    public RatingEntity getRating() {
        return rating;
    }

    public void setRating(RatingEntity rating) {
        this.rating = rating;
    }

    @ElementCollection
    @CollectionTable(name = "order_attachments", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "url")
    public List<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    @OneToOne(mappedBy = "order", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
    public OrderCancelEntity getOrderCancel() {
        return orderCancel;
    }

    @JsonProperty
    public void setOrderCancel(OrderCancelEntity orderCancel) {
        this.orderCancel = orderCancel;
    }

    @JsonIgnore
    @OneToOne(mappedBy = "order", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
    public CourierTransactionEntity getCourierTransaction() {
        return courierTransaction;
    }

    public void setCourierTransaction(CourierTransactionEntity courierTransaction) {
        this.courierTransaction = courierTransaction;
    }

    @Column(name="surge_factor")
    public Integer getSurgeFactor() {
        return surgeFactor;
    }

    public void setSurgeFactor(Integer surgeFactor) {
        this.surgeFactor = surgeFactor;
    }

    @Column(name="item_service_vat_charge", precision = 16, scale = 2)
    public BigDecimal getItemServiceAndVatCharge() {
        return itemServiceAndVatCharge;
    }

    public void setItemServiceAndVatCharge(BigDecimal itemServiceAndVatCharge) {
        this.itemServiceAndVatCharge = itemServiceAndVatCharge;
    }

    @ManyToOne
    public InvoiceEntity getInvoice() {
        return invoice;
    }

    public void setInvoice(InvoiceEntity invoice) {
        this.invoice = invoice;
    }

    @Column(name="reprocess_time")
    public Integer getReprocessTime() {
        return reprocessTime;
    }

    public void setReprocessTime(Integer reprocessTime) {
        this.reprocessTime = reprocessTime;
    }

    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
    public BillEntity getBill() {
        return bill;
    }

    public void setBill(BillEntity bill) {
        this.bill = bill;
    }

    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
    public ReceiptEntity getReceipt() {
        return receipt;
    }

    public void setReceipt(ReceiptEntity receipt) {
        this.receipt = receipt;
    }
}
