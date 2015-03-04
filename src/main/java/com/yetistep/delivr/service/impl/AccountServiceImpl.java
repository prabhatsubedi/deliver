package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.abs.AbstractManager;
import com.yetistep.delivr.dao.inf.*;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.enums.InvoiceStatus;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.service.inf.AccountService;
import com.yetistep.delivr.util.EmailMsg;
import com.yetistep.delivr.util.InvoiceGenerator;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

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
    HttpServletRequest httpServletRequest;

    @Override
    public String getGenerateInvoice(HeaderDto headerDto) throws Exception {
        //TODO: generate invoice is not completed
        Integer storeId = Integer.parseInt(headerDto.getId());
        List<OrderEntity> orders =  orderDaoService.getStoresOrders(storeId);
        StoreEntity store =  storeDaoService.find(storeId);
        store.getId();

        MerchantEntity merchant = store.getStoresBrand().getMerchant();
        MerchantEntity testMerchant = new MerchantEntity();
        testMerchant.setId(merchant.getId());
        InvoiceGenerator invoiceGenerator = new InvoiceGenerator();

        InvoiceEntity invoice = new InvoiceEntity();
        invoice.setInvoiceStatus(InvoiceStatus.UNPAID);
        invoice.setMerchant(testMerchant);
        invoice.setOrders(orders);
        invoice.setGeneratedDate(new Date(System.currentTimeMillis()));
        invoice.setId(1);
        invoiceDaoService.save(invoice);
        String invoicePath = new String();
        if(orders.size()>0){
            invoicePath = invoiceGenerator.generateInvoice(orders, merchant, invoice);
        }
        return  invoicePath;
    }

    @Override
    public String generateBillAndReceiptAndSendEmail(OrderEntity order) throws Exception{
        //Integer orderId = Integer.parseInt(headerDto.getId());
        //OrderEntity order = orderDaoService.find(orderId);
        InvoiceGenerator invoiceGenerator = new InvoiceGenerator();
        BillEntity bill = new BillEntity();
        bill.setOrder(order);
        bill.setCustomer(order.getCustomer());
        BigDecimal vat = order.getItemsOrder().get(0).getVat();
        bill.setVat(vat);
        bill.setDeliveryCharge(order.getDeliveryCharge());
        bill.setSystemServiceCharge(order.getSystemServiceCharge());

        bill.setGeneratedDate(new Date(System.currentTimeMillis()));
        bill.setPath("path");
        BigDecimal vatPcn = order.getItemsOrder().get(0).getVat();
        BigDecimal totalCharge = order.getDeliveryCharge().add(order.getSystemServiceCharge());
        bill.setVat(totalCharge.multiply(vatPcn).divide(new BigDecimal(100)));
        BigDecimal totalAmount = totalCharge.add(totalCharge.multiply(vatPcn).divide(new BigDecimal(100)));
        bill.setBillAmount(totalAmount);

        ReceiptEntity receipt = new ReceiptEntity();
        receipt.setReceiptAmount(totalAmount);
        receipt.setGeneratedDate(new Date(System.currentTimeMillis()));

        receipt.setOrder(order);
        receipt.setCustomer(order.getCustomer());

        billDaoService.save(bill);
        receiptDaoService.save(receipt);

        String billAndReceiptPath = invoiceGenerator.generateBillAndReceipt(order, bill, receipt, getServerUrl());
        bill.setPath(billAndReceiptPath);
        receipt.setPath(billAndReceiptPath);


        billDaoService.update(bill);
        receiptDaoService.update(receipt);

        String message = EmailMsg.sendBillAndReceipt(order, order.getCustomer().getUser().getFullName(), "sagar123@yopmail.com"/*order.getCustomer().getUser().getEmailAddress()*/, getServerUrl());

        sendAttachmentEmail("sagar123@yopmail.com"/*order.getCustomer().getUser().getEmailAddress()*/,  message, "Bill and Receipt", billAndReceiptPath);

        return billAndReceiptPath;
    }



}
