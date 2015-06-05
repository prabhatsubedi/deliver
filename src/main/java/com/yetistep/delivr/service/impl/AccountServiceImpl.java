package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.abs.AbstractManager;
import com.yetistep.delivr.dao.inf.*;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.PaginationDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.enums.*;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.service.inf.AccountService;
import com.yetistep.delivr.service.inf.DeliveryBoyService;
import com.yetistep.delivr.service.inf.SystemPropertyService;
import com.yetistep.delivr.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/25/14
 * Time: 10:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class AccountServiceImpl extends AbstractManager implements AccountService{

    private static final Logger log = Logger.getLogger(MerchantServiceImpl.class);

    @Autowired
    OrderDaoService orderDaoService;

    @Autowired
    StoreDaoService storeDaoService;

    @Autowired
    InvoiceDaoService invoiceDaoService;

    @Autowired
    BillDaoService billDaoService;

    @Autowired
    ReceiptDaoService receiptDaoService;

    @Autowired
    DBoyOrderHistoryDaoService dBoyOrderHistoryDaoService;

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    SystemPropertyService systemPropertyService;

    @Autowired
    DBoyAdvanceAmountDaoService dBoyAdvanceAmountDaoService;

    @Autowired
    DBoyPaymentDaoService dBoyPaymentDaoService;

    @Autowired
    DeliveryBoyDaoService deliveryBoyDaoService;

    @Override
    public String generateInvoice(Integer storeId, String fromDate, String toDate) throws Exception {
        String invoicePath = new String();
        List<OrderEntity> orders =  orderDaoService.getStoresOrders(storeId, fromDate, toDate);
        if(orders.size()>0){
            StoreEntity store =  storeDaoService.find(storeId);
            //store.getId();

            MerchantEntity merchant = store.getStoresBrand().getMerchant();
            InvoiceGenerator invoiceGenerator = new InvoiceGenerator();

            InvoiceEntity invoice = new InvoiceEntity();
            CommissionStatementEntity commission = new CommissionStatementEntity();
            invoice.setInvoiceStatus(InvoiceStatus.UNPAID);
            invoice.setMerchant(merchant);
            invoice.setStore(store);
            invoice.setOrders(orders);
            invoice.setGeneratedDate(new Date(System.currentTimeMillis()));

            for (OrderEntity orderEntity: orders){
                orderEntity.setInvoice(invoice);
            }

            invoice.setGeneratedDate(new Date(DateUtil.getCurrentTimestampSQL().getTime()));
            invoice.setFromDate(new Date(new SimpleDateFormat("yyyy-MM-dd").parse(fromDate).getTime()));
            invoice.setToDate(new Date(new SimpleDateFormat("yyyy-MM-dd").parse(toDate).getTime()));

            BigDecimal totalOrderAmount = BigDecimal.ZERO;
            BigDecimal totalServiceCharge = BigDecimal.ZERO;
            BigDecimal itemVatAmount = BigDecimal.ZERO;
            for (OrderEntity order: orders){
                for (ItemsOrderEntity itemsOrderEntity: order.getItemsOrder()) {
                    BigDecimal itemServiceChargePcn = itemsOrderEntity.getServiceCharge();
                    BigDecimal itemServiceCharge = itemsOrderEntity.getItemTotal().multiply(itemServiceChargePcn).divide(new BigDecimal(100));
                    BigDecimal itemVatCharge = itemsOrderEntity.getItemTotal().multiply(itemsOrderEntity.getVat()).divide(new BigDecimal(100));
                    totalServiceCharge = totalServiceCharge.add(itemServiceCharge);
                    itemVatAmount = itemVatAmount.add(itemVatCharge);
                }
                //add order
                totalOrderAmount = totalOrderAmount.add(order.getTotalCost().subtract(order.getDiscountFromStore()));
            }
            BigDecimal totalTaxableAmount =  totalOrderAmount.add(totalServiceCharge);
            //BigDecimal vatAmount =  totalTaxableAmount.multiply(new BigDecimal(Integer.parseInt(systemPropertyService.readPrefValue(PreferenceType.DELIVERY_FEE_VAT)))).divide(new BigDecimal(100));
            BigDecimal grandTotalAmount = totalTaxableAmount.add(itemVatAmount);
            BigDecimal commissionAmount = totalOrderAmount.multiply(merchant.getCommissionPercentage()).divide(new BigDecimal(100));
            BigDecimal systemVatAmount =  commissionAmount.multiply(new BigDecimal(Integer.parseInt(systemPropertyService.readPrefValue(PreferenceType.DELIVERY_FEE_VAT)))).divide(new BigDecimal(100));
            commissionAmount = commissionAmount.add(systemVatAmount);

            BigDecimal netPayableAmount = grandTotalAmount.subtract(commissionAmount);
            invoice.setAmount(netPayableAmount.setScale(2, BigDecimal.ROUND_DOWN));


            BigDecimal commissionVatAmount = commissionAmount.multiply(new BigDecimal(Integer.parseInt(systemPropertyService.readPrefValue(PreferenceType.DELIVERY_FEE_VAT)))).divide(new BigDecimal(100));
            BigDecimal totalCommissionAmount = commissionAmount.add(commissionVatAmount);

            commission.setGeneratedDate(new Date(DateUtil.getCurrentTimestampSQL().getTime()));
            commission.setAmount(totalCommissionAmount);


            commission.setInvoice(invoice);
            invoice.setCommission(commission);
            invoiceDaoService.save(invoice);

            Map<String, String> preferences = new HashMap<>();
            preferences.put("CURRENCY", systemPropertyService.readPrefValue(PreferenceType.CURRENCY));
            preferences.put("HELPLINE_NUMBER", systemPropertyService.readPrefValue(PreferenceType.HELPLINE_NUMBER));
            preferences.put("SUPPORT_EMAIL", systemPropertyService.readPrefValue(PreferenceType.SUPPORT_EMAIL));
            preferences.put("COMPANY_NAME", systemPropertyService.readPrefValue(PreferenceType.COMPANY_NAME));
            preferences.put("COMPANY_ADDRESS", systemPropertyService.readPrefValue(PreferenceType.COMPANY_ADDRESS));
            preferences.put("REGISTRATION_NO", systemPropertyService.readPrefValue(PreferenceType.REGISTRATION_NO));
            preferences.put("VAT_NO", systemPropertyService.readPrefValue(PreferenceType.VAT_NO));
            preferences.put("DELIVERY_FEE_VAT", systemPropertyService.readPrefValue(PreferenceType.DELIVERY_FEE_VAT));

            String imageUrl = systemPropertyService.readPrefValue(PreferenceType.LOGO_FOR_PDF_EMAIL);

            invoicePath = invoiceGenerator.generateInvoice(orders, merchant, invoice, store, imageUrl, preferences);

            invoice.setPath(invoicePath);
            invoiceDaoService.update(invoice);

            String email = store.getEmail();
            if(email != null && !email.equals("")) {
                String serverUrl;
                if(MessageBundle.isLocalHost()){
                    serverUrl = "http://localhost:8080/";
                } else {
                    serverUrl = "http://test.idelivr.com/";
                }
                String message = EmailMsg.sendInvoiceEmail(store, fromDate, toDate, serverUrl);
                sendAttachmentEmail(email,  message, "get invoice-"+fromDate+"-"+toDate, invoicePath);
            }
            System.out.println("Email sent successfully with attachment: "+invoicePath+" for store "+store.getId());
        }else{
            System.out.println("Invoice not generated for store "+storeId);
        }
        return invoicePath;
    }

    @Override
    public String generateBillAndReceiptAndSendEmail(OrderEntity order) throws Exception{
       /* Integer orderId = Integer.parseInt(headerDto.getId());
        OrderEntity order = orderDaoService.find(orderId);*/
        String billAndReceiptPath = "";
        if(order != null) {
            Integer orderId = order.getId();

            List<BillEntity> billExists = billDaoService.getBillByOrder(orderId);
            List<ReceiptEntity> receiptsExists = receiptDaoService.getBillByOrder(orderId);
            if(billExists.size()>0 || receiptsExists.size()>0){
                throw new YSException("INV006");
            }

            InvoiceGenerator invoiceGenerator = new InvoiceGenerator();
            BillEntity bill = new BillEntity();
            bill.setOrder(order);
            bill.setCustomer(order.getCustomer());

            Integer vat = Integer.parseInt(systemPropertyService.readPrefValue(PreferenceType.DELIVERY_FEE_VAT));

            //if both delivery charge and systemService charge equals to 0, do not generate the bill and receipt
            log.info("++++++++++++ check if both delivery charge and systemService charge equals to 0  +++++++++++++++");
            log.info("++++++++++++ "+(BigDecimal.ZERO.compareTo(order.getDeliveryCharge()) != 0)+" "+(BigDecimal.ZERO.compareTo(order.getSystemServiceCharge())!=0)+"  +++++++++++++++");
            if(BigDecimal.ZERO.compareTo(order.getDeliveryCharge()) != 0 || BigDecimal.ZERO.compareTo(order.getSystemServiceCharge())!=0) {
                log.info("++++++++++++ check it  +++++++++++++++");
                BigDecimal deliveryCharge;
                BigDecimal systemServiceCharge;

                if(BigDecimal.ZERO.compareTo(order.getDeliveryCharge()) != 0)
                    deliveryCharge = order.getDeliveryCharge().multiply(new BigDecimal(100)).divide(new BigDecimal(vat+100), MathContext.DECIMAL32).setScale(2, BigDecimal.ROUND_DOWN);
                else
                    deliveryCharge = order.getDeliveryCharge();

                if(BigDecimal.ZERO.compareTo(order.getSystemServiceCharge())!=0)
                    systemServiceCharge = order.getSystemServiceCharge().multiply(new BigDecimal(100)).divide(new BigDecimal(vat+100), MathContext.DECIMAL32).setScale(2, BigDecimal.ROUND_DOWN);
                else
                    systemServiceCharge = order.getSystemServiceCharge();

                bill.setDeliveryCharge(deliveryCharge);
                bill.setSystemServiceCharge(systemServiceCharge);

                BigDecimal vatPcn = new BigDecimal(vat);
                BigDecimal totalCharge = bill.getDeliveryCharge().add(bill.getSystemServiceCharge());
                BigDecimal vatAmount = totalCharge.multiply(vatPcn).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_DOWN);
                bill.setVat(vatAmount);
                BigDecimal totalAmount = totalCharge.add(vatAmount);

                bill.setBillAmount(totalAmount.setScale(2, BigDecimal.ROUND_DOWN));
                bill.setGeneratedDate(new Date(System.currentTimeMillis()));

                ReceiptEntity receipt = new ReceiptEntity();
                receipt.setReceiptAmount(totalAmount.setScale(2, BigDecimal.ROUND_DOWN));
                receipt.setGeneratedDate(new Date(System.currentTimeMillis()));

                receipt.setOrder(order);
                receipt.setCustomer(order.getCustomer());

                billDaoService.save(bill);
                receiptDaoService.save(receipt);

                Map<String, String> preferences = new HashMap<>();
                preferences.put("CURRENCY", systemPropertyService.readPrefValue(PreferenceType.CURRENCY));
                preferences.put("HELPLINE_NUMBER", systemPropertyService.readPrefValue(PreferenceType.HELPLINE_NUMBER));
                preferences.put("SUPPORT_EMAIL", systemPropertyService.readPrefValue(PreferenceType.SUPPORT_EMAIL));
                preferences.put("COMPANY_NAME", systemPropertyService.readPrefValue(PreferenceType.COMPANY_NAME));
                preferences.put("COMPANY_ADDRESS", systemPropertyService.readPrefValue(PreferenceType.COMPANY_ADDRESS));
                preferences.put("REGISTRATION_NO", systemPropertyService.readPrefValue(PreferenceType.REGISTRATION_NO));
                preferences.put("VAT_NO", systemPropertyService.readPrefValue(PreferenceType.VAT_NO));
                preferences.put("DELIVERY_FEE_VAT", systemPropertyService.readPrefValue(PreferenceType.DELIVERY_FEE_VAT));

                String imageUrl = systemPropertyService.readPrefValue(PreferenceType.LOGO_FOR_PDF_EMAIL);
                billAndReceiptPath = invoiceGenerator.generateBillAndReceipt(order, bill, receipt, imageUrl, preferences);
                bill.setPath(billAndReceiptPath);
                receipt.setPath(billAndReceiptPath);
                receipt.setGeneratedDate(new Date(System.currentTimeMillis()));

                billDaoService.update(bill);
                receiptDaoService.update(receipt);

                String email = order.getCustomer().getUser().getEmailAddress();
                if(email != null && !email.equals("")) {
                    String message = EmailMsg.sendBillAndReceipt(order, order.getCustomer().getUser().getFullName(), email, getServerUrl());
                    sendAttachmentEmail(email,  message, "Bill and Receipt", billAndReceiptPath);
                }
            }
        }
        return billAndReceiptPath;
    }

    @Override
    public void generatedBoyPayStatement(Integer dBoyId, String fromDate, String toDate) throws Exception {
        String statementPath = new String();
        InvoiceGenerator invoiceGenerator = new InvoiceGenerator();
        List<OrderEntity> orders =  orderDaoService.getDBoyOrders(dBoyId, fromDate, toDate);

        if (orders.size()>0){
            DBoyPaymentEntity dBoyPayment = new DBoyPaymentEntity();
            DeliveryBoyEntity deliveryBoy = deliveryBoyDaoService.findDBoyById(dBoyId);
            dBoyPayment.setDeliveryBoy(deliveryBoy);
            dBoyPayment.setOrders(orders);
            dBoyPayment.setGeneratedDate(new Date(System.currentTimeMillis()));

            BigDecimal dboyTotalEarnedAmount = BigDecimal.ZERO;
            for (OrderEntity order:orders){
                dboyTotalEarnedAmount =  dboyTotalEarnedAmount.add(order.getdBoyOrderHistories().get(0).getAmountEarned());
                order.setdBoyPayment(dBoyPayment);
            }

            BigDecimal tdsAmount = dboyTotalEarnedAmount.multiply(new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.TDS_PERCENTAGE))).divide(new BigDecimal(100));
            BigDecimal totalPayableAmount = dboyTotalEarnedAmount.subtract(tdsAmount);
            dBoyPayment.setGeneratedDate(new Date(DateUtil.getCurrentTimestampSQL().getTime()));
            dBoyPayment.setFromDate(new Date(new SimpleDateFormat("yyyy-MM-dd").parse(fromDate).getTime()));
            dBoyPayment.setToDate(new Date(new SimpleDateFormat("yyyy-MM-dd").parse(toDate).getTime()));
            dBoyPayment.setdBoyPaid(false);
            dBoyPayment.setPayableAmount(totalPayableAmount.setScale(2, BigDecimal.ROUND_DOWN));

            dBoyPaymentDaoService.save(dBoyPayment);

            Map<String, String> preferences = new HashMap<>();
            preferences.put("CURRENCY", systemPropertyService.readPrefValue(PreferenceType.CURRENCY));
            preferences.put("HELPLINE_NUMBER", systemPropertyService.readPrefValue(PreferenceType.HELPLINE_NUMBER));
            preferences.put("SUPPORT_EMAIL", systemPropertyService.readPrefValue(PreferenceType.SUPPORT_EMAIL));
            preferences.put("COMPANY_NAME", systemPropertyService.readPrefValue(PreferenceType.COMPANY_NAME));
            preferences.put("COMPANY_ADDRESS", systemPropertyService.readPrefValue(PreferenceType.COMPANY_ADDRESS));
            preferences.put("REGISTRATION_NO", systemPropertyService.readPrefValue(PreferenceType.REGISTRATION_NO));
            preferences.put("VAT_NO", systemPropertyService.readPrefValue(PreferenceType.VAT_NO));
            preferences.put("DELIVERY_FEE_VAT", systemPropertyService.readPrefValue(PreferenceType.DELIVERY_FEE_VAT));
            preferences.put("TDS_PERCENTAGE", systemPropertyService.readPrefValue(PreferenceType.TDS_PERCENTAGE));

            String imageUrl = systemPropertyService.readPrefValue(PreferenceType.LOGO_FOR_PDF_EMAIL);
            statementPath =  invoiceGenerator.generateDBoyPayStatement(orders, deliveryBoy, dBoyPayment, imageUrl, preferences);

            dBoyPayment.setPath(statementPath);
            dBoyPaymentDaoService.update(dBoyPayment);

        }

    }

    @Override
    public List<StoreEntity> getAllStores() throws Exception {
        List<StoreEntity> storeEntities = storeDaoService.findAll();
        List<StoreEntity> partnerStores = new ArrayList<>();
        for (StoreEntity storeEntity:storeEntities){
            if(storeEntity.getStoresBrand().getMerchant().getPartnershipStatus().equals(Boolean.TRUE)){
                    partnerStores.add(storeEntity);
            }
        }
        return  partnerStores;
    }

    @Override
    public void payDboy(HeaderDto headerDto) throws Exception {
        String orderId = headerDto.getId();
        String[] orderIds =  orderId.split(",");
        List<OrderEntity> orders = new ArrayList<>();

        for (String id: orderIds ) {
            if(id != "")
                orders.add(orderDaoService.find(Integer.parseInt(id)));
        }

        for (OrderEntity order: orders){
            order.setdBoyPaid(true);
            order.setdBoyPaidDate(new Date(System.currentTimeMillis()));
            orderDaoService.update(order);
        }
    }

    @Override
    public void payInvoice(HeaderDto headerDto) throws Exception {
        String invoiceId = headerDto.getId();
        String[] invoiceIds =  invoiceId.split(",");
        List<InvoiceEntity> invoiceEntities = new ArrayList<>();

        for (String id: invoiceIds ) {
            if(id != "")
                invoiceEntities.add(invoiceDaoService.find(Integer.parseInt(id)));
        }

        for (InvoiceEntity invoice: invoiceEntities){
            invoice.setInvoicePaid(true);
            invoice.setPaidDate(new Date(System.currentTimeMillis()));
            invoiceDaoService.update(invoice);
        }
    }


    @Override
    public void settleMerchantsOrder(HeaderDto headerDto) throws Exception {
        String orderId = headerDto.getId();
        String[] orderIds =  orderId.split(",");
        List<OrderEntity> orders = new ArrayList<>();

        for (String id: orderIds ) {
            if(id != "")
                orders.add(orderDaoService.find(Integer.parseInt(id)));
        }

        for (OrderEntity order: orders){
            order.setSettled(true);
            order.setSettledDate(new Date(System.currentTimeMillis()));
            orderDaoService.update(order);
        }
    }


    @Override
    public void saveDBoyTransactionNote(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception {
         String className = requestJsonDto.getClassName();
         if(className.equals("order")){
             OrderEntity orderEntity = orderDaoService.find(Integer.parseInt(headerDto.getId()));
             orderEntity.setAccountantNote(requestJsonDto.getAccountantNote());
             orderDaoService.update(orderEntity);
         } else {
             DBoyAdvanceAmountEntity dBoyAdvanceAmountEntity = dBoyAdvanceAmountDaoService.find(Integer.parseInt(headerDto.getId()));
             dBoyAdvanceAmountEntity.setAccountantNote(requestJsonDto.getAccountantNote());
             dBoyAdvanceAmountDaoService.update(dBoyAdvanceAmountEntity);
         }
    }

    @Override
    public PaginationDto getDBoyPayStatement(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception{

        Page page = requestJsonDto.getPage();

        List<DBoyPaymentEntity> dBoyPaymentEntities = new ArrayList<>();
        Integer dBoyId = Integer.parseInt(headerDto.getId());

        PaginationDto paginationDto = new PaginationDto();
        Integer totalRows =  dBoyPaymentDaoService.getTotalNumberOfPayStatements(dBoyId);
        paginationDto.setNumberOfRows(totalRows);

        if(page != null){
            page.setTotalRows(totalRows);
        }

        dBoyPaymentEntities = dBoyPaymentDaoService.findAllPayStatementsOfShopper(page, dBoyId);

        List<DBoyPaymentEntity> dBoyPayments = new ArrayList<>();

        String fields = "id,generatedDate,path,payableAmount,fromDate,toDate,paidDate,dBoyPaid";

        for (DBoyPaymentEntity dBoyPayment:dBoyPaymentEntities){
            dBoyPayments.add((DBoyPaymentEntity) ReturnJsonUtil.getJsonObject(dBoyPayment, fields));
        }

        paginationDto.setData(dBoyPayments);

        return paginationDto;
    }


    @Override
    public void payDBoyPayStatement(HeaderDto headerDto) throws Exception {
        String invoiceId = headerDto.getId();
        String[] invoiceIds =  invoiceId.split(",");
        List<DBoyPaymentEntity> dBoyPaymentEntities = new ArrayList<>();

        for (String id: invoiceIds ) {
            if(id != "")
                dBoyPaymentEntities.add(dBoyPaymentDaoService.find(Integer.parseInt(id)));
        }

        for (DBoyPaymentEntity dBoyPayment: dBoyPaymentEntities){
            dBoyPayment.setdBoyPaid(true);
            dBoyPayment.setPaidDate(new Date(System.currentTimeMillis()));
            dBoyPaymentDaoService.update(dBoyPayment);
        }
    }

    @Override
    public PaginationDto getDBoyAccount(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception{
        Page page = requestJsonDto.getPage();

        PaginationDto paginationDto = new PaginationDto();

        DeliveryBoyEntity dBoy = deliveryBoyDaoService.findDBoyById(Integer.parseInt(headerDto.getId()));

        String fields = "id,dBoyAdvanceAmounts,order,user";

        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("dBoyAdvanceAmounts", "id,advanceDate,amountAdvance,type,accountantNote");
        assoc.put("order", "id,deliveryStatus,deliveryCharge,orderStatus,grandTotal,orderDate,paymentMode,paidFromWallet,paidFromCOD,store,dBoyOrderHistories,accountantNote,itemServiceAndVatCharge,totalCost,discountFromStore,itemsOrder,attachments");
        subAssoc.put("itemsOrder", "id,purchaseStatus");
        subAssoc.put("dBoyOrderHistories", "id,orderCompletedAt");
        subAssoc.put("store", "id,storesBrand");
        subAssoc.put("storesBrand", "id,merchant");
        subAssoc.put("merchant", "id,partnershipStatus");

        DeliveryBoyEntity acDBoy = (DeliveryBoyEntity) ReturnJsonUtil.getJsonObject(dBoy, fields, assoc, subAssoc);

        List<OrderEntity> advanceAsOrder = new ArrayList<>();

        for (DBoyAdvanceAmountEntity advanceAmount: acDBoy.getdBoyAdvanceAmounts()){
            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setId(advanceAmount.getOrder().getId());
            orderEntity.setOrderDate(advanceAmount.getAdvanceDate());
            orderEntity.setGrandTotal(advanceAmount.getAmountAdvance());
            orderEntity.setDescription(advanceAmount.getType());
            orderEntity.setAccountantNote(advanceAmount.getAccountantNote());
            advanceAsOrder.add(orderEntity);
        }



        List<OrderEntity> orderEntities = acDBoy.getOrder();

        List<OrderEntity> liveOrders = new ArrayList<>();

        for(OrderEntity order: orderEntities){
            //remove live orders
            List<Integer> activeStatuses = new ArrayList<>();
            activeStatuses.add(JobOrderStatus.ORDER_PLACED.ordinal());
            activeStatuses.add(JobOrderStatus.ORDER_ACCEPTED.ordinal());
            activeStatuses.add(JobOrderStatus.IN_ROUTE_TO_PICK_UP.ordinal());
            activeStatuses.add(JobOrderStatus.AT_STORE.ordinal());
            activeStatuses.add(JobOrderStatus.IN_ROUTE_TO_DELIVERY.ordinal());

            if(order.getdBoyOrderHistories().size() >0 && !activeStatuses.contains(order.getOrderStatus().ordinal()) ){
                order.setOrderDate(order.getdBoyOrderHistories().get(0).getOrderCompletedAt());
            }  else {
                liveOrders.add(order);
            }
        }

        //TODO: confirm the conditions applied
        orderEntities.removeAll(liveOrders);

        //add all order transactions as order for dboy transactions
        orderEntities.addAll(advanceAsOrder);


        //sort orders by order date
        Collections.sort(orderEntities, new Comparator<OrderEntity>() {
            public int compare(OrderEntity prev, OrderEntity next) {
                return prev.getOrderDate().compareTo(next.getOrderDate());
            }
        });

        //add rows for cr on cancel and wallet transaction
        List<OrderEntity> addedOrderRows = new ArrayList<>();

        BigDecimal balance = BigDecimal.ZERO;
        for(OrderEntity order: orderEntities){
            if(order.getDescription() != null) {
                //this is not real order but dboy transactions
                if(order.getDescription().equals("advanceAmount")){
                    balance = balance.add(order.getGrandTotal());
                    order.setBalance(balance);
                    order.setDr(order.getGrandTotal());
                    order.setDescription("Advance Amount");
                    order.setGrandTotal(null);
                } else if(order.getDescription().equals("acknowledgeAmount")){
                    balance = balance.subtract(order.getGrandTotal());
                    order.setBalance(balance);
                    order.setCr(order.getGrandTotal());
                    order.setDescription("Amount Submitted to Account");
                    order.setGrandTotal(null);
                }
            } else {
                String partnershipStatus = "";
                if(order.getStore().getStoresBrand().getMerchant().getPartnershipStatus()) {
                    partnershipStatus = "Partner";
                } else {
                    partnershipStatus = "Non Partner";
                    if(order.getDeliveryStatus().equals(DeliveryStatus.SUCCESSFUL)){
                        OrderEntity nonPartnerOrder = new OrderEntity();
                        nonPartnerOrder.setId(order.getId());
                        Long orderDateInTime = order.getOrderDate().getTime()-1000;
                        java.sql.Timestamp timestamp = new java.sql.Timestamp(orderDateInTime);
                        nonPartnerOrder.setOrderDate(timestamp);
                        nonPartnerOrder.setGrandTotal(order.getGrandTotal());
                        nonPartnerOrder.setCr(order.getTotalCost().subtract(order.getDiscountFromStore()).add(order.getItemServiceAndVatCharge()));
                        nonPartnerOrder.setPaymentMode(order.getPaymentMode());
                        nonPartnerOrder.setDescription("Paid to Merchant - "+ partnershipStatus);
                        balance = balance.subtract(order.getTotalCost().subtract(order.getDiscountFromStore()).add(order.getItemServiceAndVatCharge()));
                        nonPartnerOrder.setBalance(balance);
                        addedOrderRows.add(nonPartnerOrder);
                    }
                }

                balance = balance.add(order.getGrandTotal());
                order.setBalance(balance);
                order.setDr(order.getGrandTotal());

                //check if item purchased && processed
                Boolean itemPurchased = false;
                Boolean itemProcessed = false;
                if(order.getDeliveryStatus().equals(DeliveryStatus.CANCELLED)){
                    for (ItemsOrderEntity itemsOrder: order.getItemsOrder()){
                        if (itemsOrder.getPurchaseStatus() != null && itemsOrder.getPurchaseStatus()){
                            itemPurchased = true;
                            break;
                        }
                    }
                    for (ItemsOrderEntity itemsOrder: order.getItemsOrder()){
                        if (itemsOrder.getPurchaseStatus() != null && !itemsOrder.getPurchaseStatus()){
                            itemProcessed = true;
                            break;
                        }
                    }
                }


                if(order.getPaymentMode().equals(PaymentMode.WALLET)){
                    if(order.getDeliveryStatus().equals(DeliveryStatus.SUCCESSFUL)){
                        //cr = wallet amount
                        OrderEntity walletOrder = new OrderEntity();
                        walletOrder.setId(order.getId());
                        walletOrder.setOrderDate(order.getOrderDate());
                        walletOrder.setCr(order.getPaidFromWallet());
                        walletOrder.setPaymentMode(order.getPaymentMode());
                        //if cod amount is greater then 0 then the payment mode is wallet_cod
                        if(order.getPaidFromCOD() != null && order.getPaidFromCOD().compareTo(BigDecimal.ZERO) == 1){
                            walletOrder.setDescription("Order(WALLET+COD) - "+ partnershipStatus);
                            order.setDescription("Order(WALLET+COD) - "+ partnershipStatus);
                        } else {
                            walletOrder.setDescription("Order(WALLET) - "+ partnershipStatus);
                            order.setDescription("Order(WALLET) - "+ partnershipStatus);
                        }

                        balance = balance.subtract(order.getPaidFromWallet());
                        walletOrder.setBalance(balance);
                        addedOrderRows.add(walletOrder);
                    } else {
                        //cr = wallet amount
                        if(itemPurchased){
                            if(order.getStore().getStoresBrand().getMerchant().getPartnershipStatus()){
                                //if cod amount is greater then 0 then the payment mode is wallet_cod
                                if(order.getPaidFromCOD() != null && order.getPaidFromCOD().compareTo(BigDecimal.ZERO) == 1){
                                    //walletOrder.setDescription("Order(WALLET+COD) - "+ partnershipStatus);
                                    order.setDescription("Order(WALLET+COD) - "+ partnershipStatus);
                                } else {
                                    //walletOrder.setDescription("Order(WALLET) - "+ partnershipStatus);
                                    order.setDescription("Order(WALLET) - "+ partnershipStatus);
                                }


                                if(order.getAccountantNote()==null){
                                    String note = "receive item worth "+systemPropertyService.readPrefValue(PreferenceType.CURRENCY)+" "+order.getDr();
                                    order.setAccountantNote(note);
                                }

                            } else {
                                OrderEntity nonPartnerOrder = new OrderEntity();
                                nonPartnerOrder.setId(order.getId());
                                Long orderDateInTime = order.getOrderDate().getTime()-1000;
                                java.sql.Timestamp timestamp = new java.sql.Timestamp(orderDateInTime);
                                nonPartnerOrder.setOrderDate(timestamp);
                                nonPartnerOrder.setCr(order.getTotalCost().subtract(order.getDiscountFromStore()).add(order.getItemServiceAndVatCharge()));
                                nonPartnerOrder.setPaymentMode(order.getPaymentMode());
                                nonPartnerOrder.setDescription("Paid to Merchant - "+ partnershipStatus);

                                balance = balance.subtract(order.getTotalCost().subtract(order.getDiscountFromStore()).add(order.getItemServiceAndVatCharge())).subtract(order.getGrandTotal());
                                nonPartnerOrder.setBalance(balance);
                                balance = balance.add(order.getGrandTotal());
                                order.setBalance(balance);

                                //balance = balance.subtract(order.getGrandTotal());

                                if(order.getPaidFromCOD() != null && order.getPaidFromCOD().compareTo(BigDecimal.ZERO) == 1){
                                    order.setDescription("Order(WALLET+COD) - "+ partnershipStatus);
                                } else {
                                    order.setDescription("Order(WALLET) - "+ partnershipStatus);
                                }


                            if(order.getAccountantNote()==null){
                                    String note = "receive item worth "+systemPropertyService.readPrefValue(PreferenceType.CURRENCY)+" "+order.getDr();
                                    order.setAccountantNote(note);
                                }

                                addedOrderRows.add(nonPartnerOrder);
                           }
                        } else {

                            if(!itemProcessed || order.getDeliveryCharge() == null){
                                //both cr and dr = 0
                                order.setDr(BigDecimal.ZERO);
                                OrderEntity walletOrder = new OrderEntity();
                                walletOrder.setId(order.getId());
                                walletOrder.setOrderDate(order.getOrderDate());
                                walletOrder.setCr(BigDecimal.ZERO);
                                walletOrder.setPaymentMode(order.getPaymentMode());
                                //if cod amount is greater then 0 then the payment mode is wallet_cod
                                if(order.getPaidFromCOD() != null && order.getPaidFromCOD().compareTo(BigDecimal.ZERO) == 1){
                                    walletOrder.setDescription("Order(WALLET+COD) - item not purchased, not processed"+ partnershipStatus);
                                    order.setDescription("Order(WALLET+COD) - "+ partnershipStatus);
                                } else {
                                    walletOrder.setDescription("Order(WALLET) - "+ partnershipStatus);
                                    order.setDescription("Order(WALLET) - "+ partnershipStatus);
                                }
                                balance = balance.subtract(order.getGrandTotal());
                                order.setBalance(balance);
                                walletOrder.setBalance(balance);
                                addedOrderRows.add(walletOrder);
                            } else {
                                //both cr and dr = 0
                                BigDecimal deliveryCharge = order.getDeliveryCharge();
                                order.setDr(deliveryCharge);
                                OrderEntity walletOrder = new OrderEntity();
                                walletOrder.setId(order.getId());
                                walletOrder.setOrderDate(order.getOrderDate());
                                walletOrder.setCr(BigDecimal.ZERO);
                                walletOrder.setPaymentMode(order.getPaymentMode());
                                //if cod amount is greater then 0 then the payment mode is wallet_cod
                                if(order.getPaidFromCOD() != null && order.getPaidFromCOD().compareTo(BigDecimal.ZERO) == 1){
                                    walletOrder.setDescription("Order(WALLET+COD) - item not purchased but processed - "+ partnershipStatus);
                                    order.setDescription("Order(WALLET+COD) - item not purchased but processed - "+ partnershipStatus);
                                } else {
                                    walletOrder.setDescription("Order(WALLET) - item not purchased but processed - "+ partnershipStatus);
                                    order.setDescription("Order(WALLET) - item not purchased but processed - "+ partnershipStatus);
                                }

                                walletOrder.setBalance(balance);
                                addedOrderRows.add(walletOrder);
                            }
                        }

                    }
                } else {
                    if(order.getDeliveryStatus().equals(DeliveryStatus.CANCELLED)){

                        if(itemPurchased){
                            if(order.getStore().getStoresBrand().getMerchant().getPartnershipStatus()){
                                if(order.getAccountantNote()==null){
                                    String note = "receive item worth "+systemPropertyService.readPrefValue(PreferenceType.CURRENCY)+" "+order.getDr();
                                    order.setAccountantNote(note);
                                }
                            } else {
                                OrderEntity nonPartnerOrder = new OrderEntity();
                                nonPartnerOrder.setId(order.getId());
                                Long orderDateInTime = order.getOrderDate().getTime()-1000;
                                java.sql.Timestamp timestamp = new java.sql.Timestamp(orderDateInTime);
                                nonPartnerOrder.setOrderDate(timestamp);
                                nonPartnerOrder.setCr(order.getTotalCost().subtract(order.getDiscountFromStore()).add(order.getItemServiceAndVatCharge()));
                                nonPartnerOrder.setPaymentMode(order.getPaymentMode());
                                nonPartnerOrder.setDescription("Paid to Merchant - "+ partnershipStatus);

                                balance = balance.subtract(order.getTotalCost().subtract(order.getDiscountFromStore()).add(order.getItemServiceAndVatCharge())).subtract(order.getGrandTotal());
                                nonPartnerOrder.setBalance(balance);

                                balance = balance.add(order.getGrandTotal());
                                order.setBalance(balance);

                                addedOrderRows.add(nonPartnerOrder);

                                if(order.getAccountantNote()==null){
                                    String note = "receive item worth "+systemPropertyService.readPrefValue(PreferenceType.CURRENCY)+" "+order.getDr();
                                    order.setAccountantNote(note);
                                }
                            }

                        } else {

                            if(!itemProcessed || order.getDeliveryCharge() == null){
                                order.setDr(BigDecimal.ZERO);
                                OrderEntity cancelledOrder = new OrderEntity();
                                cancelledOrder.setId(order.getId());
                                cancelledOrder.setCr(BigDecimal.ZERO);
                                cancelledOrder.setDescription("Canceled order received-item not purchased, not processed");
                                cancelledOrder.setOrderDate(order.getOrderDate());
                                cancelledOrder.setPaymentMode(order.getPaymentMode());
                                balance = balance.subtract(order.getGrandTotal());
                                order.setBalance(balance);
                                cancelledOrder.setBalance(balance);
                                addedOrderRows.add(cancelledOrder);
                            } else {
                                BigDecimal deliveryCharge = order.getDeliveryCharge();
                                order.setDr(deliveryCharge);
                                OrderEntity cancelledOrder = new OrderEntity();
                                cancelledOrder.setId(order.getId());
                                cancelledOrder.setCr(BigDecimal.ZERO);
                                cancelledOrder.setDescription("Canceled order received-item not purchased but processed");
                                cancelledOrder.setOrderDate(order.getOrderDate());
                                cancelledOrder.setPaymentMode(order.getPaymentMode());
                                cancelledOrder.setBalance(balance);
                                addedOrderRows.add(cancelledOrder);
                            }

                        }
                    }

                    order.setDescription("Order(COD) - "+ partnershipStatus);
                }
            }
            // store information, dboy history and grand total is not required now
            order.setGrandTotal(null);
            order.setStore(null);
            order.setdBoyOrderHistories(null);
        }

        orderEntities.addAll(addedOrderRows);

        //sort orders by order date
        Collections.sort(orderEntities, new Comparator<OrderEntity>() {
            public int compare(OrderEntity one, OrderEntity other) {
                return one.getOrderDate().compareTo(other.getOrderDate());
            }
        });


        acDBoy.setOrder(orderEntities);
        acDBoy.setdBoyAdvanceAmounts(null);
        paginationDto.setNumberOfRows(orderEntities.size());
        paginationDto.setData(orderEntities);
        return paginationDto;
    }

    @Override
    public OrderEntity getOrder(HeaderDto headerDto) throws Exception{
         OrderEntity order = orderDaoService.findOrderById(Integer.parseInt(headerDto.getId()));
        String fields = "id,orderName,orderStatus,deliveryStatus,orderDate,customer,orderVerificationCode,store,deliveryBoy,deliveryBoySelections,assignedTime,attachments,itemServiceAndVatCharge,grandTotal,totalCost,discountFromStore,rating,deliveryCharge,bill,itemsOrder";

        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("deliveryBoy", "id,user,averageRating,latitude,longitude");
        assoc.put("customer", "id,user,bill");
        assoc.put("itemsOrder", "id,itemTotal,serviceAndVatCharge,availabilityStatus,purchaseStatus,vat,serviceCharge,customItem");
        assoc.put("store", "id,name,street,contactPerson,contactNo");
        assoc.put("address", "id,street,city,state,country");
        assoc.put("attachments", "url");
        assoc.put("rating", "id,customerRating,deliveryBoyRating,deliveryBoyComment,customerComment");
        assoc.put("orderCancel", "id,reasonDetails,reason");
        assoc.put("dBoyOrderHistories", "id,distanceTravelled,amountEarned,jobStartedAt,orderCompletedAt");
        assoc.put("deliveryBoySelections", "id,paidToCourier,accepted");
        assoc.put("bill", "id,path");

        subAssoc.put("user", "id,fullName,mobileNumber,profileImage,addresses,emailAddress");
        subAssoc.put("reasonDetails", "id,cancelReason");
        subAssoc.put("addresses", "id,street,city,state,country");

        return (OrderEntity) ReturnJsonUtil.getJsonObject(order, fields, assoc, subAssoc);
    }

    @Override
    public List<OrderEntity> getOrdersAmountTransferred() throws Exception{
        List<OrderEntity> ordersAmountTransferred = new ArrayList<>();
        List<JobOrderStatus> orderStatuses = new ArrayList<>();
        orderStatuses.add(JobOrderStatus.AT_STORE);
        orderStatuses.add(JobOrderStatus.IN_ROUTE_TO_DELIVERY);
        List<OrderEntity> allProcessedOrder = orderDaoService.getAllProcessedOrders(orderStatuses);
        List<OrderEntity> addedOrderRows = new ArrayList<>();
        for (OrderEntity order: allProcessedOrder){
            Boolean orderProcessed = true;
            for (ItemsOrderEntity itemsOrder: order.getItemsOrder()){
                 if(itemsOrder.getPurchaseStatus() == null){
                     orderProcessed = false;
                     break;
                 }
            }
            if(orderProcessed) {
                String fields = "id,orderName,orderStatus,deliveryStatus,orderDate,orderVerificationCode,deliveryBoy,assignedTime,itemServiceAndVatCharge,grandTotal,totalCost,discountFromStore,deliveryCharge,itemsOrder,bankAccountNumber";
                Map<String, String> assoc = new HashMap<>();
                Map<String, String> subAssoc = new HashMap<>();
                assoc.put("deliveryBoy", "id,user,averageRating,latitude,longitude,availableAmount");
                assoc.put("itemsOrder", "id,itemTotal,serviceAndVatCharge,availabilityStatus,purchaseStatus,vat,serviceCharge");
                assoc.put("advanceAmounts", "id,amountAdvance,advanceDate");
                subAssoc.put("user", "id,fullName");
                OrderEntity processOrder = (OrderEntity) ReturnJsonUtil.getJsonObject(order, fields, assoc,subAssoc);
                List<DBoyAdvanceAmountEntity> advanceAmount = processOrder.getAdvanceAmounts();
                DeliveryBoyEntity deliveryBoy = processOrder.getDeliveryBoy();
                BigDecimal paidToMerchant = processOrder.getTotalCost().subtract(order.getDiscountFromStore()).add(order.getItemServiceAndVatCharge());
                processOrder.setTotalCost(paidToMerchant);
                if(advanceAmount.size() > 0){
                    BigDecimal tillTransferred = BigDecimal.ZERO;
                    for (DBoyAdvanceAmountEntity dBoyAdvanceAmount: advanceAmount){
                        tillTransferred = tillTransferred.add(dBoyAdvanceAmount.getAmountAdvance());
                        OrderEntity orderTransferred = new OrderEntity();
                        UserEntity transferredDBoyUser = new UserEntity();
                        DeliveryBoyEntity transferredDBoy = new DeliveryBoyEntity();
                        transferredDBoyUser.setFullName(processOrder.getDeliveryBoy().getUser().getFullName());
                        transferredDBoy.setUser(transferredDBoyUser);
                        transferredDBoy.setAvailableAmount(processOrder.getDeliveryBoy().getAvailableAmount());
                        orderTransferred.setDeliveryBoy(transferredDBoy);
                        orderTransferred.setOrderDate(processOrder.getOrderDate());
                        orderTransferred.setId(processOrder.getId());
                        orderTransferred.setOrderStatus(processOrder.getOrderStatus());
                        orderTransferred.setTotalCost(paidToMerchant);
                        orderTransferred.setTransferred(dBoyAdvanceAmount.getAmountAdvance());
                        addedOrderRows.add(orderTransferred);
                    }
                    if(BigDecimalUtil.isLessThen(tillTransferred, paidToMerchant)){
                        if (BigDecimalUtil.isLessThen(deliveryBoy.getAvailableAmount(), paidToMerchant)){
                            OrderEntity orderToBeTransferred = new OrderEntity();
                            UserEntity transferredDBoyUser = new UserEntity();
                            DeliveryBoyEntity transferredDBoy = new DeliveryBoyEntity();
                            transferredDBoyUser.setFullName(processOrder.getDeliveryBoy().getUser().getFullName());
                            transferredDBoy.setUser(transferredDBoyUser);
                            transferredDBoy.setAvailableAmount(processOrder.getDeliveryBoy().getAvailableAmount());
                            orderToBeTransferred.setDeliveryBoy(transferredDBoy);
                            orderToBeTransferred.setOrderDate(processOrder.getOrderDate());
                            orderToBeTransferred.setId(processOrder.getId());
                            orderToBeTransferred.setOrderStatus(processOrder.getOrderStatus());
                            orderToBeTransferred.setTotalCost(paidToMerchant);
                            orderToBeTransferred.setToBeTransferred(paidToMerchant.subtract(processOrder.getDeliveryBoy().getAvailableAmount()));
                            addedOrderRows.add(orderToBeTransferred);
                        }
                    }
                } else  {
                    if (BigDecimalUtil.isLessThen(deliveryBoy.getAvailableAmount(), paidToMerchant)) {
                        processOrder.setToBeTransferred(paidToMerchant.subtract(processOrder.getDeliveryBoy().getAvailableAmount()));
                        ordersAmountTransferred.add(processOrder);
                    }
                }
            }
        }
        ordersAmountTransferred.addAll(addedOrderRows);
        return ordersAmountTransferred;
    }

}