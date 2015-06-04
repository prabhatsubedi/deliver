package com.yetistep.delivr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yetistep.delivr.enums.DeliveryStatus;
import com.yetistep.delivr.enums.JobOrderStatus;
import com.yetistep.delivr.enums.PaymentMode;
import com.yetistep.delivr.util.JsonDateSerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
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
    private BigDecimal customerChargeableDistance; //Paid by customer
    private BigDecimal systemChargeableDistance; //Paid by system
    //private BigDecimal itemTotal;
    private BigDecimal totalCost;
    //private BigDecimal transportationCharge;
    private BigDecimal systemServiceCharge;
    private BigDecimal deliveryCharge;
    private BigDecimal grandTotal;
    private BigDecimal deliveryBoyShare;
    /*private BigDecimal systemShare;*/
    private Timestamp orderDate;
    private Integer assignedTime;
    private Integer remainingTime;
    private Integer elapsedTime;
    private RatingEntity rating;
    private List<String> attachments;
    private Integer surgeFactor;
    private BigDecimal itemServiceAndVatCharge;
    private Integer reprocessTime;
    private Boolean dBoyPaid;
    private Date dBoyPaidDate;
    private PaymentMode paymentMode;
    private BigDecimal paidFromWallet;
    private BigDecimal paidFromCOD;
    private BigDecimal shortFallAmount;
    private BigDecimal itemServiceCharge;
    private BigDecimal itemVatCharge;
    private BigDecimal discountFromStore;
    private String description; //used as transient for dBoy account
    private BigDecimal balance; //used as transient for dBoy account
    private BigDecimal dr;  //used as transient for dBoy account
    private BigDecimal cr;  //used as transient for dBoy account
    private BigDecimal toBeTransferred;  //used as transient for dBoy account
    private BigDecimal transferred;  //used as transient for dBoy account
    private String accountantNote;

    private OrderCancelEntity orderCancel;
    private CourierTransactionEntity courierTransaction;
    private InvoiceEntity invoice;
    private DBoyPaymentEntity dBoyPayment;
    private List<ItemsOrderEntity> itemsOrder;
    private AddressEntity address;
    private DeliveryBoyEntity deliveryBoy;
    private CustomerEntity customer;
    private StoreEntity store;
    private List<DBoyOrderHistoryEntity> dBoyOrderHistories;
    private List<DeliveryBoySelectionEntity> deliveryBoySelections;
    private List<DBoyAdvanceAmountEntity> advanceAmounts;
    private BillEntity bill;
    private ReceiptEntity receipt;
    private List<WalletTransactionEntity> walletTransactions;
    //for merchant settlement
    private Boolean settled;
    private Date settledDate;
    private String currency; //Transient Field

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

    /*@Column(name = "transportation_charge", precision = 19, scale = 2)
    public BigDecimal getTransportationCharge() {
        return transportationCharge;
    }

    public void setTransportationCharge(BigDecimal transportationCharge) {
        this.transportationCharge = transportationCharge;
    }*/

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

    @Transient
    public BigDecimal getDeliveryBoyShare() {
        return deliveryBoyShare;
    }

    public void setDeliveryBoyShare (BigDecimal deliveryBoyShare) {
        this.deliveryBoyShare = deliveryBoyShare;
    }

/*
    @Column(name = "system_share", precision = 19, scale = 2)
    public BigDecimal getSystemShare() {
        return systemShare ;
    }

    public void setSystemShare(BigDecimal systemShare) {
        this.systemShare = systemShare;
    }
*/

    @JsonSerialize(using = JsonDateSerializer.class)
    @Column(name = "order_date", columnDefinition="TIMESTAMP NULL DEFAULT NULL")
    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    @Column(name = "payment_mode")
    @Enumerated(EnumType.ORDINAL)
    public PaymentMode getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(PaymentMode paymentMode) {
        this.paymentMode = paymentMode;
    }

    @Column(name = "paid_from_wallet", precision = 19, scale = 2)
    public BigDecimal getPaidFromWallet() {
        return paidFromWallet;
    }

    public void setPaidFromWallet(BigDecimal paidFromWallet) {
        this.paidFromWallet = paidFromWallet;
    }

    @Column(name = "paid_from_cod", precision = 19, scale = 2)
    public BigDecimal getPaidFromCOD() {
        return paidFromCOD;
    }

    public void setPaidFromCOD(BigDecimal paidFromCOD) {
        this.paidFromCOD = paidFromCOD;
    }

    @Column(name = "shortfall_amount", precision = 19, scale = 2)
    public BigDecimal getShortFallAmount() {
        return shortFallAmount;
    }

    public void setShortFallAmount(BigDecimal shortFallAmount) {
        this.shortFallAmount = shortFallAmount;
    }

    @Column(name = "item_service_charge", precision = 19, scale = 2)
    public BigDecimal getItemServiceCharge() {
        return itemServiceCharge;
    }

    public void setItemServiceCharge(BigDecimal itemServiceCharge) {
        this.itemServiceCharge = itemServiceCharge;
    }

    @Column(name = "item_vat_charge", precision = 19, scale = 2)
    public BigDecimal getItemVatCharge() {
        return itemVatCharge;
    }

    public void setItemVatCharge(BigDecimal itemVatCharge) {
        this.itemVatCharge = itemVatCharge;
    }

    @Column(name = "discount_from_store", precision = 19, scale = 2)
    public BigDecimal getDiscountFromStore() {
        return discountFromStore;
    }

    public void setDiscountFromStore(BigDecimal discountFromStore) {
        this.discountFromStore = discountFromStore;
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

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    public InvoiceEntity getInvoice() {
        return invoice;
    }

    public void setInvoice(InvoiceEntity invoice) {
        this.invoice = invoice;
    }

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "dboy_payment_id")
    public DBoyPaymentEntity getdBoyPayment() {
        return dBoyPayment;
    }

    public void setdBoyPayment(DBoyPaymentEntity dBoyPayment) {
        this.dBoyPayment = dBoyPayment;
    }

    @Column(name="reprocess_time")
    public Integer getReprocessTime() {
        return reprocessTime;
    }

    public void setReprocessTime(Integer reprocessTime) {
        this.reprocessTime = reprocessTime;
    }

    @Column(name = "dboy_paid")
    public Boolean getdBoyPaid() {
        return dBoyPaid;
    }

    public void setdBoyPaid(Boolean dBoyPaid) {
        this.dBoyPaid = dBoyPaid;
    }

    @Column(name = "dboy_paid_date", columnDefinition="TIMESTAMP NULL DEFAULT NULL")
    public Date getdBoyPaidDate() {
        return dBoyPaidDate;
    }

    public void setdBoyPaidDate(Date dBoyPaidDate) {
        this.dBoyPaidDate = dBoyPaidDate;
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

    @OneToMany(mappedBy = "order", cascade = { CascadeType.PERSIST, CascadeType.MERGE} )
    public List<WalletTransactionEntity> getWalletTransactions() {
        return walletTransactions;
    }

    public void setWalletTransactions(List<WalletTransactionEntity> walletTransactions) {
        this.walletTransactions = walletTransactions;
    }

    @Column(name = "settled", columnDefinition = "TINYINT(1) DEFAULT 0")
    public Boolean getSettled() {
        return settled;
    }

    public void setSettled(Boolean settled) {
        this.settled = settled;
    }


    @Column(name = "settled_date", columnDefinition="TIMESTAMP NULL DEFAULT NULL")
    public Date getSettledDate() {
        return settledDate;
    }

    public void setSettledDate(Date settledDate) {
        this.settledDate = settledDate;
    }

    @Column(name = "accountant_note")
    public String getAccountantNote() {
        return accountantNote;
    }

    public void setAccountantNote(String accountantNote) {
        this.accountantNote = accountantNote;
    }

    @OneToMany(mappedBy = "order", cascade = { CascadeType.PERSIST, CascadeType.MERGE} )
    public List<DBoyAdvanceAmountEntity> getAdvanceAmounts() {
        return advanceAmounts;
    }

    public void setAdvanceAmounts(List<DBoyAdvanceAmountEntity> advanceAmounts) {
        this.advanceAmounts = advanceAmounts;
    }

    @Transient
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Transient
    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Transient
    public BigDecimal getDr() {
        return dr;
    }

    public void setDr(BigDecimal dr) {
        this.dr = dr;
    }

    @Transient
    public BigDecimal getCr() {
        return cr;
    }

    public void setCr(BigDecimal cr) {
        this.cr = cr;
    }

    @Transient
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Transient
    public BigDecimal getToBeTransferred() {
        return toBeTransferred;
    }

    public void setToBeTransferred(BigDecimal toBeTransferred) {
        this.toBeTransferred = toBeTransferred;
    }

    @Transient
    public BigDecimal getTransferred() {
        return transferred;
    }

    public void setTransferred(BigDecimal transferred) {
        this.transferred = transferred;
    }
}
