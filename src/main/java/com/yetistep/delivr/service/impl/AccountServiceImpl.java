package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.abs.AbstractManager;
import com.yetistep.delivr.dao.inf.*;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.PaginationDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.enums.InvoiceStatus;
import com.yetistep.delivr.enums.PreferenceType;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.service.inf.AccountService;
import com.yetistep.delivr.service.inf.DeliveryBoyService;
import com.yetistep.delivr.service.inf.SystemPropertyService;
import com.yetistep.delivr.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/25/14
 * Time: 10:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class AccountServiceImpl extends AbstractManager implements AccountService{

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
    public String generateInvoice(Integer storeId, String fromDate, String toDate, String serverUrl) throws Exception {
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
            for (OrderEntity order: orders){
                for (ItemsOrderEntity itemsOrderEntity: order.getItemsOrder()) {
                    BigDecimal itemServiceChargePcn = itemsOrderEntity.getItem().getServiceCharge();
                    BigDecimal itemServiceCharge = itemsOrderEntity.getItemTotal().multiply(itemServiceChargePcn).divide(new BigDecimal(100));
                    totalServiceCharge.add(itemServiceCharge);
                }
                //add order
                totalOrderAmount = totalOrderAmount.add(order.getTotalCost());
            }
            BigDecimal totalTaxableAmount =  totalOrderAmount.add(totalServiceCharge);
            BigDecimal vatAmount =  totalTaxableAmount.multiply(new BigDecimal(Integer.parseInt(systemPropertyService.readPrefValue(PreferenceType.DELIVERY_FEE_VAT)))).divide(new BigDecimal(100));
            BigDecimal grandTotalAmount = totalTaxableAmount.add(vatAmount);
            BigDecimal commissionAmount = totalOrderAmount.multiply(merchant.getCommissionPercentage()).divide(new BigDecimal(100));
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

            invoicePath = invoiceGenerator.generateInvoice(orders, merchant, invoice, store, serverUrl, preferences);

            invoice.setPath(invoicePath);
            invoiceDaoService.update(invoice);

            String email = store.getEmail();
            if(email != null && !email.equals("")) {
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

            if(order.getDeliveryCharge().equals(BigDecimal.ZERO) && order.getSystemServiceCharge().equals(BigDecimal.ZERO))
                throw new  YSException("INV007");

            BigDecimal deliveryCharge = order.getDeliveryCharge().multiply(new BigDecimal(100)).divide(new BigDecimal(vat+100), MathContext.DECIMAL32).setScale(2, BigDecimal.ROUND_DOWN);
            BigDecimal systemServiceCharge = order.getSystemServiceCharge().multiply(new BigDecimal(100)).divide(new BigDecimal(vat+100), MathContext.DECIMAL32).setScale(2, BigDecimal.ROUND_DOWN);

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

            billAndReceiptPath = invoiceGenerator.generateBillAndReceipt(order, bill, receipt, getServerUrl(), preferences);
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
        return billAndReceiptPath;
    }

    @Override
    public void generatedBoyPayStatement(Integer dBoyId, String fromDate, String toDate, String serverUrl) throws Exception {
        String statementPath = new String();
        InvoiceGenerator invoiceGenerator = new InvoiceGenerator();
        List<OrderEntity> orders =  orderDaoService.getDBoyOrders(dBoyId, fromDate, toDate);

        if (orders.size()>0){
            DBoyPaymentEntity dBoyPayment = new DBoyPaymentEntity();
            DeliveryBoyEntity deliveryBoy = deliveryBoyDaoService.findDBoyById(dBoyId);
            dBoyPayment.setDeliveryBoy(deliveryBoy);
            dBoyPayment.setOrders(orders);
            dBoyPayment.setGeneratedDate(new Date(System.currentTimeMillis()));

            BigDecimal totalAmountEarned = BigDecimal.ZERO;
            for (OrderEntity orderEntity: orders){
                orderEntity.setdBoyPayment(dBoyPayment);
                totalAmountEarned.add(orderEntity.getdBoyOrderHistories().get(0).getAmountEarned());
            }

            BigDecimal tdsAmount = totalAmountEarned.multiply(new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.TDS_PERCENTAGE))).divide(new BigDecimal(100));

            BigDecimal totalPayableAmount = totalAmountEarned.subtract(tdsAmount);

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

            statementPath =  invoiceGenerator.generateDBoyPayStatement(orders, deliveryBoy, dBoyPayment, serverUrl, preferences);

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

        dBoyPaymentEntities = dBoyPaymentDaoService.findAllOfShopper(page, dBoyId);

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



}