package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.abs.AbstractManager;
import com.yetistep.delivr.dao.inf.*;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.enums.InvoiceStatus;
import com.yetistep.delivr.enums.PreferenceType;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.service.inf.AccountService;
import com.yetistep.delivr.service.inf.SystemPropertyService;
import com.yetistep.delivr.util.DateUtil;
import com.yetistep.delivr.util.EmailMsg;
import com.yetistep.delivr.util.InvoiceGenerator;
import com.yetistep.delivr.util.YSException;
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

    @Override
    public String generateInvoice(Integer storeId, String fromDate, String toDate, String serverUrl) throws Exception {
        String invoicePath = new String();
        List<OrderEntity> orders =  orderDaoService.getStoresOrders(storeId, fromDate, toDate);
        if(orders.size()>0){
            StoreEntity store =  storeDaoService.find(storeId);
            store.getId();

            MerchantEntity merchant = store.getStoresBrand().getMerchant();
            InvoiceGenerator invoiceGenerator = new InvoiceGenerator();

            InvoiceEntity invoice = new InvoiceEntity();
            invoice.setInvoiceStatus(InvoiceStatus.UNPAID);
            invoice.setMerchant(merchant);
            invoice.setStore(store);
            invoice.setOrders(orders);
            invoice.setGeneratedDate(new Date(System.currentTimeMillis()));

            for (OrderEntity orderEntity: orders){
                orderEntity.setInvoice(invoice);
            }

            invoice.setGeneratedDate(new Date(System.currentTimeMillis()));
            invoice.setFromDate(new Date(new SimpleDateFormat("yyyy-MM-dd").parse(fromDate).getTime()));
            invoice.setToDate(new Date(new SimpleDateFormat("yyyy-MM-dd").parse(toDate).getTime()));
            BigDecimal totalOrderAmount = BigDecimal.ZERO;
            for (OrderEntity order: orders){
                totalOrderAmount = totalOrderAmount.add(order.getTotalCost());
            }
            BigDecimal commissionAmount = totalOrderAmount.max(merchant.getCommissionPercentage()).divide(new BigDecimal(100));
            BigDecimal vatAmount =  commissionAmount.multiply(new BigDecimal(13)).divide(new BigDecimal(100));
            BigDecimal totalPayableAmount = commissionAmount.add(vatAmount);
            invoice.setAmount(totalPayableAmount);
            invoiceDaoService.save(invoice);

            Map<String, String> preferences = new HashMap<>();
            preferences.put("HELPLINE_NUMBER", systemPropertyService.readPrefValue(PreferenceType.HELPLINE_NUMBER));
            preferences.put("SUPPORT_EMAIL", systemPropertyService.readPrefValue(PreferenceType.SUPPORT_EMAIL));
            preferences.put("COMPANY_NAME", systemPropertyService.readPrefValue(PreferenceType.COMPANY_NAME));
            preferences.put("COMPANY_ADDRESS", systemPropertyService.readPrefValue(PreferenceType.COMPANY_ADDRESS));
            preferences.put("REGISTRATION_NO", systemPropertyService.readPrefValue(PreferenceType.REGISTRATION_NO));

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
        //Integer orderId = Integer.parseInt(headerDto.getId());
        //OrderEntity order = orderDaoService.find(orderId);
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
            //extract vat from delivery charge  and system charge
            BigDecimal deliveryCharge = order.getDeliveryCharge().multiply(new BigDecimal(100)).divide(new BigDecimal(vat+100), MathContext.DECIMAL32).setScale(2, BigDecimal.ROUND_DOWN);
            bill.setDeliveryCharge(deliveryCharge);
            //bill.setDeliveryCharge(order.getDeliveryCharge());
            bill.setSystemServiceCharge(order.getSystemServiceCharge().multiply(new BigDecimal(100)).divide(new BigDecimal(vat+100), MathContext.DECIMAL32).setScale(2, BigDecimal.ROUND_DOWN));
            BigDecimal vatPcn = new BigDecimal(vat);
            BigDecimal totalCharge = order.getDeliveryCharge().add(order.getSystemServiceCharge());
            bill.setVat(totalCharge.multiply(vatPcn).divide(new BigDecimal(100)));
            BigDecimal totalAmount = totalCharge.add(totalCharge.multiply(vatPcn).divide(new BigDecimal(100)));
            bill.setBillAmount(totalAmount);
            bill.setGeneratedDate(new Date(System.currentTimeMillis()));

            ReceiptEntity receipt = new ReceiptEntity();
            receipt.setReceiptAmount(totalAmount);
            receipt.setGeneratedDate(new Date(System.currentTimeMillis()));

            receipt.setOrder(order);
            receipt.setCustomer(order.getCustomer());

            billDaoService.save(bill);
            receiptDaoService.save(receipt);

            Map<String, String> preferences = new HashMap<>();
            preferences.put("HELPLINE_NUMBER", systemPropertyService.readPrefValue(PreferenceType.HELPLINE_NUMBER));
            preferences.put("SUPPORT_EMAIL", systemPropertyService.readPrefValue(PreferenceType.SUPPORT_EMAIL));
            preferences.put("COMPANY_NAME", systemPropertyService.readPrefValue(PreferenceType.COMPANY_NAME));
            preferences.put("COMPANY_ADDRESS", systemPropertyService.readPrefValue(PreferenceType.COMPANY_ADDRESS));
            preferences.put("REGISTRATION_NO", systemPropertyService.readPrefValue(PreferenceType.REGISTRATION_NO));

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
    public List<StoreEntity> getAllStores() throws Exception {
        List<StoreEntity> storeEntities = storeDaoService.findAll();
        return  storeEntities;
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
}