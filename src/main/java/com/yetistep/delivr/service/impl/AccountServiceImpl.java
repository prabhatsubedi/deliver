package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.dao.inf.*;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.enums.InvoiceStatus;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.service.inf.AccountService;
import com.yetistep.delivr.util.InvoiceGenerator;
import org.springframework.beans.factory.annotation.Autowired;

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
public class AccountServiceImpl implements AccountService{

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
        //invoiceDaoService.save(invoice);
        String invoicePath = new String();
        if(orders.size()>0){
            invoicePath = invoiceGenerator.generateInvoice(orders, merchant, invoice);
        }
        return  invoicePath;
    }

    @Override
    public String getGenerateBillAndReceipt(HeaderDto headerDto) throws Exception{
        Integer orderId = Integer.parseInt(headerDto.getId());
        OrderEntity order = orderDaoService.find(orderId);
        InvoiceGenerator invoiceGenerator = new InvoiceGenerator();
        BillEntity bill = new BillEntity();
        bill.setOrder(order);
        bill.setCustomer(order.getCustomer());
        BigDecimal vat = order.getItemsOrder().get(0).getVat();
        bill.setVat(vat);
        bill.setDeliveryCharge(order.getDeliveryCharge());
        bill.setSystemServiceCharge(order.getSystemServiceCharge());
        bill.setGeneratedDate(new Date(System.currentTimeMillis()));
        bill.setBillAmount(order.getDeliveryCharge().add(order.getSystemServiceCharge()).add(order.getItemsOrder().get(0).getVat()));

        ReceiptEntity receipt = new ReceiptEntity();
        receipt.setReceiptAmount(order.getDeliveryCharge().add(order.getSystemServiceCharge()).add(order.getItemsOrder().get(0).getVat()));
        receipt.setGeneratedDate(new Date(System.currentTimeMillis()));

        String billAndReceiptPath = invoiceGenerator.generateBillAndReceipt(order, bill, receipt);
        bill.setPath(billAndReceiptPath);
        receipt.setPath(billAndReceiptPath);

        //billDaoService.save(bill);
        //receiptDaoService.save(receipt);

        return billAndReceiptPath;
    }



}
