package com.yetistep.delivr.util;


import com.itextpdf.text.*;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.yetistep.delivr.enums.PreferenceType;
import com.yetistep.delivr.model.*;
import org.apache.commons.mail.EmailException;
import com.yetistep.delivr.service.inf.SystemPropertyService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 2/20/15
 * Time: 11:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class InvoiceGenerator {

    @Autowired
    SystemPropertyService systemPropertyService;

    private static final Logger log = Logger.getLogger(InvoiceGenerator.class);

    private static final String HOME_DIR = System.getProperty("catalina.home");
    private static final String INVOICE_URL = "http://www.iDeliver.com";

    public String generateInvoice(List<OrderEntity> orders, MerchantEntity merchant, InvoiceEntity invoice, StoreEntity store, String serverUrl) throws Exception {
            File invoiceFile = generateInvoicePDF(orders, merchant, invoice, store, serverUrl);

            if (invoiceFile == null)
                return null;

            String invoicePath = invoiceFile.getPath();

            //upload invoice to S3 Bucket
            if (!MessageBundle.isLocalHost()) {
                int noOfRetry = 3;
                for (int i = 0; i < noOfRetry; i++) {//retry three time, if exception occurs
                    try {
                        String dir = getInvoiceDir(merchant, "/");

                        String bucketUrl = AmazonUtil.uploadFileToS3(invoiceFile, dir, invoiceFile.getName(), true);
                        invoicePath = AmazonUtil.cacheImage(bucketUrl);
                        break;
                    } catch (Exception e) {
                        if (i == (noOfRetry - 1)) throw e;
                            GeneralUtil.wait(1500);
                    }
                }
            }
        return invoicePath;
    }


    public String generateBillAndReceipt(OrderEntity order, BillEntity bill, ReceiptEntity receipt, String serverUrl) throws Exception{
        File invoiceFile = generateBillAndReceiptPDF(order, bill, receipt, serverUrl);

        if (invoiceFile == null)
            return null;

        String invoicePath = invoiceFile.getPath();

        //upload invoice to S3 Bucket
        if (!MessageBundle.isLocalHost()) {
            int noOfRetry = 3;
            for (int i = 0; i < noOfRetry; i++) {//retry three time, if exception occurs
                try {
                    String dir = getBillAndReiceptDir(order.getStore().getStoresBrand().getMerchant(), order, "/");

                    String bucketUrl = AmazonUtil.uploadFileToS3(invoiceFile, dir, invoiceFile.getName(), true);
                    invoicePath = AmazonUtil.cacheImage(bucketUrl);
                    break;
                } catch (Exception e) {
                    if (i == (noOfRetry - 1)) throw e;
                    GeneralUtil.wait(1500);
                }
            }
        }
        return invoicePath;
    }

    private File generateBillAndReceiptPDF(OrderEntity order, BillEntity bill, ReceiptEntity receipt, String serverUrl) throws Exception{
        FileOutputStream stream = null;
        File billAndReceiptFile = null;


        try{
            Document document = new Document();
            billAndReceiptFile = getBillAndReceiptFile(order.getStore().getStoresBrand().getMerchant(), order, "receipt");
            stream = new FileOutputStream(billAndReceiptFile);
            PdfWriter writer = PdfWriter.getInstance(document, stream);
            document.open();
            document.setMargins(20, 20, 20, 20);
            //add bill header
            addPdfHeader(document, serverUrl);

            addBillBody(document, order, bill);

            document.newPage();

            //add receipt header
            addPdfHeader(document, serverUrl);

            addReceiptBody(document, order, receipt, bill);

            document.close();
        }catch (Exception e) {
            log.error("Error occurred while generating coupon bill", e);

            if (stream != null) {
                stream.flush();
                stream.close();
            }

            if (billAndReceiptFile != null) {
                billAndReceiptFile.delete();
            }
            billAndReceiptFile = null;
            throw e;
        }

        return billAndReceiptFile;
    }

    private File generateInvoicePDF(List<OrderEntity> orders, MerchantEntity merchant, InvoiceEntity invoice, StoreEntity store, String serverUrl) throws Exception {

        FileOutputStream stream = null;
        File invoiceFile = null;


        try{
            Document document = new Document();
            /*Integer cntOrder = 1;*/

            invoiceFile = getFile(merchant, "invoice");
            stream = new FileOutputStream(invoiceFile);
            PdfWriter writer = PdfWriter.getInstance(document, stream);
            document.open();
            document.setMargins(20, 20, 20, 20);
            //add doc header
            addPdfHeader(document, serverUrl);

            addInvoiceDetail(document, merchant, invoice, store);
            //document.newPage();

            addInvoiceBody(document, orders);

            addFooter(document);
            document.close();
        }catch (Exception e) {
            log.error("Error occurred while generating coupon bill", e);

            if (stream != null) {
                stream.flush();
                stream.close();
            }

            if (invoiceFile != null) {
                invoiceFile.delete();
            }
            invoiceFile = null;
            throw e;
        }

        return invoiceFile;
    }

//    //create a header for the company
//    private void addInvoiceHeader(Document document, InvoiceEntity invoice) throws Exception {
//        int bottomPadding = 35;
//
//        Paragraph title = PdfUtil.getParagraph(PdfUtil.catFont, "INVOICE");
//        title.setAlignment(Element.ALIGN_CENTER);
//        document.add(title);
//
//        //add empty line
//        PdfUtil.addEmptyLine(document, 2);
//
//        //address cell
//        PdfPCell addressCell = new PdfPCell();
//        PdfUtil.setPadding(addressCell, 0, 0, bottomPadding, 0);
//        addressCell.addElement(PdfUtil.getParagraph(PdfUtil.catFont, ""));
//
//        String address = "";
//        String contactNo = "";
//        String vatNo = "";
//
//        Paragraph info = PdfUtil.getParagraph(PdfUtil.smallFont, true, address, "Ph: " + contactNo, "VAT No.: " + vatNo);
//        addressCell.addElement(info);
//
//        //billing cell
//        PdfPCell billingCell = new PdfPCell();
//
//        Paragraph paragraph1 = new Paragraph();
//        paragraph1.add(PdfUtil.getPhrase("Billing Date: ", PdfUtil.smallBold));
//        paragraph1.add(PdfUtil.getPhrase(invoice.getGeneratedDate(), PdfUtil.smallFont));
//        paragraph1.setAlignment(Element.ALIGN_RIGHT);
//
//        Paragraph paragraph2 = new Paragraph();
//        paragraph2.add(PdfUtil.getPhrase("Invoice No: ", PdfUtil.smallBold));
//        paragraph2.add(PdfUtil.getPhrase(1, PdfUtil.smallFont));
//        paragraph2.setAlignment(Element.ALIGN_RIGHT);
//
//        PdfUtil.setPadding(billingCell, 0, 0, bottomPadding, 0);
//
//        billingCell.addElement(paragraph1);
//        billingCell.addElement(paragraph2);
//
//        //no border on the cells
//        PdfUtil.setBorder(0, addressCell, billingCell);
//
//        //add cells to table
//        PdfPTable headerTable = new PdfPTable(2);
//        headerTable.setWidthPercentage(100);
//        headerTable.addCell(addressCell);
//        headerTable.addCell(billingCell);
//
//        document.add(headerTable);
//    }

    //create a header for the company
    private void addPdfHeader(Document document, String serverUrl) throws Exception {
        int bottomPadding = 35;

        //address cell
        PdfPCell addressCell = new PdfPCell();
        PdfUtil.setPadding(addressCell, 0, 0, bottomPadding, 0);

        String name = "Deliver Private Limited";
        String street = "Charkhal Road, Dillibazar";
        String city = "Kathmandu, Nepal";
        String reg = "258956555";

        String imageUrl = serverUrl+"/resources/images/delivr-logo.png";

        Image logo = Image.getInstance(new URL(imageUrl));
        logo.setAlignment(Element.ALIGN_LEFT);
        logo.setWidthPercentage(40);
        addressCell.addElement(logo);

        Paragraph info = PdfUtil.getParagraph(PdfUtil.largeBold, true, name, street, city, "Reg: " + reg);
        addressCell.addElement(info);

        //billing cell
        PdfPCell billingCell = new PdfPCell();

        Paragraph paragraph1 = new Paragraph();
        paragraph1.add(PdfUtil.getPhrase("iDelivr Customer Care : 1800 208 9898", PdfUtil.largeBold));
        paragraph1.setAlignment(Element.ALIGN_RIGHT);

        Paragraph paragraph2 = new Paragraph();
        paragraph2.add(PdfUtil.getPhrase("cs@idelivr.com", PdfUtil.largeBold));
        paragraph2.setAlignment(Element.ALIGN_RIGHT);

        PdfUtil.setPadding(billingCell, 0, 0, bottomPadding, 0);

        billingCell.addElement(paragraph1);
        billingCell.addElement(paragraph2);

        //no border on the cells
        PdfUtil.setBorder(0, addressCell, billingCell);

        //add cells to table
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.addCell(addressCell);
        headerTable.addCell(billingCell);

        document.add(headerTable);
    }

    private void addInvoiceDetail(Document document, MerchantEntity merchant, InvoiceEntity invoice, StoreEntity store) throws Exception{
        //add invoice detail
        Paragraph title1 = PdfUtil.getParagraph(PdfUtil.largeBold, "Tax Invoice: "+invoice.getId());
        title1.setAlignment(Element.ALIGN_CENTER);
        document.add(title1);
        PdfUtil.addEmptyLine(document, 1);//add empty line

        PdfPCell transactionCell = new PdfPCell();
        PdfUtil.setPadding(transactionCell, 0, 0, 10, 10);
        Paragraph transactionInfo = PdfUtil.getParagraph(PdfUtil.smallFont, true, "Transaction Detail", "Transaction Between: ", "Invoice Date: "+invoice.getGeneratedDate());
        transactionInfo.setAlignment(Element.ALIGN_LEFT);
        transactionCell.addElement(transactionInfo);

        PdfPCell billingCell = new PdfPCell();
        PdfUtil.setPadding(transactionCell, 0, 0, 10, 10);
        Paragraph billingInfo = PdfUtil.getParagraph(PdfUtil.smallFont, true, "Billing Address", store.getStoresBrand().getBrandName(), store.getStreet()+", "+store.getCity(), store.getState()+", "+store.getCountry(), "Phone: "+store.getContactNo() );
        billingInfo.setAlignment(Element.ALIGN_RIGHT);
        billingCell.addElement(billingInfo);

        PdfUtil.setBorder(0, transactionCell, billingCell);

        //add cells to table
        PdfPTable invoiceDetailTable = new PdfPTable(2);
        invoiceDetailTable.setWidthPercentage(100);
        invoiceDetailTable.addCell(transactionCell);
        invoiceDetailTable.addCell(billingCell);

        document.add(invoiceDetailTable);

        PdfUtil.addEmptyLine(document, 2);
    }

    private void addInvoiceBody(Document document, List<OrderEntity> orders) throws Exception {

        Integer cntOrder = 1;

        PdfPTable billingTable = new PdfPTable(4);
        billingTable.setWidthPercentage(100);
        billingTable.setWidths(new float[]{10, 30, 30, 20});

        String currency = "NRS";
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase("SN", PdfUtil.smallBold), PdfUtil.getPhrase("Date and Time of Transaction", PdfUtil.smallBold), PdfUtil.getPhrase("Order No.", PdfUtil.smallBold), PdfUtil.getPhrase("Amount ("+currency+")", PdfUtil.smallBold));
        BigDecimal totalOrderAmount = BigDecimal.ZERO;
        for (OrderEntity order: orders){

            //add order
            PdfUtil.addRow(billingTable, PdfUtil.getPhrase(cntOrder), PdfUtil.getPhrase(order.getOrderDate()), PdfUtil.getPhrase(order.getId()), PdfUtil.getPhrase(order.getTotalCost()));
            totalOrderAmount = totalOrderAmount.add(order.getTotalCost());

            if(cntOrder%20==0){
                document.newPage();
            }

            cntOrder++;
        }
        BigDecimal vatAmount =  totalOrderAmount.multiply(new BigDecimal(13)).divide(new BigDecimal(100));
        BigDecimal commissionAmount = BigDecimal.ZERO;
        Phrase totalPayable = PdfUtil.getPhrase("Total Payable", PdfUtil.largeBold);
        Phrase vat = PdfUtil.getPhrase("Vat(13%)", PdfUtil.largeBold);
        Phrase commission = PdfUtil.getPhrase("Commission", PdfUtil.largeBold);
        Phrase subTotal = PdfUtil.getPhrase("Sub Total", PdfUtil.largeBold);

        PdfUtil.addRow(billingTable, PdfUtil.getPhrase(""), PdfUtil.getPhrase(""), subTotal, PdfUtil.getPhrase(totalOrderAmount));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase(""), PdfUtil.getPhrase(""), commission, PdfUtil.getPhrase(commissionAmount));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase(""), PdfUtil.getPhrase(""), vat, PdfUtil.getPhrase(vatAmount));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase(""), PdfUtil.getPhrase(""), totalPayable, PdfUtil.getPhrase(totalOrderAmount.add(vatAmount).add(commissionAmount)));
        document.add(billingTable);
    }


    private void addBillBody(Document document, OrderEntity order, BillEntity bill) throws Exception {
        //add invoice detail
        Paragraph title = PdfUtil.getParagraph(PdfUtil.smallBold, "Tax Invoice : "+bill.getId());
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        PdfUtil.addEmptyLine(document, 1);//add empty line


        PdfPCell orderCell = new PdfPCell();
        PdfUtil.setPadding(orderCell, 0, 0, 10, 10);
        Paragraph orderInfo = PdfUtil.getParagraph(PdfUtil.smallFont, true, "Order Detail", "Order Id: "+order.getId(), "Order Date: "+order.getOrderDate(), "Invoice Date: "+bill.getGeneratedDate());
        orderCell.addElement(orderInfo);

        PdfPCell addressCell = new PdfPCell();
        PdfUtil.setPadding(addressCell, 0, 0, 10, 10);
        String street = "";
        String city = "";
        String state = "";
        String country = "";
        String mobile = "";
        for (AddressEntity address: order.getCustomer().getUser().getAddresses()){
            if (address.getdFlag() != null && address.getdFlag().equals("D")){
                street = address.getStreet();
                city = address.getCity();
                state = address.getState();
                country = address.getCountry();
                mobile = address.getMobileNumber();
            }
        }

        Paragraph addressInfo = PdfUtil.getParagraph(PdfUtil.smallFont, true, "Billing Address", street+", "+city, state+", "+country, "Phone: "+mobile);
        addressInfo.setAlignment(Element.ALIGN_RIGHT);
        addressCell.addElement(addressInfo);

        PdfUtil.setBorder(0, orderCell, addressCell);
        //add cells to table
        PdfPTable invoiceTable = new PdfPTable(2);
        invoiceTable.setWidthPercentage(100);
        invoiceTable.addCell(orderCell);
        invoiceTable.addCell(addressCell);


        invoiceTable.getDefaultCell().setBorder(0);

        document.add(invoiceTable);

        PdfUtil.addEmptyLine(document, 2);//add empty line

        //add billing table
        PdfPTable billingTable = new PdfPTable(2);
        billingTable.setWidthPercentage(100);
        billingTable.setWidths(new float[]{45, 45});


        String currency = "NRS";
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase("Title", PdfUtil.smallBold), PdfUtil.getPhrase("Amount ("+currency+")", PdfUtil.smallBold));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase("iDelivr Fee"),
                PdfUtil.getPhrase(bill.getSystemServiceCharge()));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase("Delivery Fee"),
                PdfUtil.getPhrase(bill.getDeliveryCharge(), PdfUtil.smallBold));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase("Subtotal"),
                PdfUtil.getPhrase(bill.getDeliveryCharge().add(bill.getSystemServiceCharge()), PdfUtil.smallBold));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase("Vat"),
                PdfUtil.getPhrase(bill.getVat(), PdfUtil.smallBold));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase("Grand Total"),
                PdfUtil.getPhrase(bill.getDeliveryCharge().add(bill.getSystemServiceCharge()).add(bill.getVat()), PdfUtil.smallBold));

        document.add(billingTable);

    }


    private void addReceiptBody(Document document, OrderEntity order, ReceiptEntity receipt, BillEntity bill) throws Exception {
        //add invoice detail
        Paragraph title = PdfUtil.getParagraph(PdfUtil.largeBold, "Receipt for Payment of Delivery and iDelivery Fee");
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        PdfUtil.addEmptyLine(document, 2);

        PdfPCell infoCell = new PdfPCell();
        PdfUtil.setPadding(infoCell, 0, 0, 10, 10);
        Paragraph info = PdfUtil.getParagraph(PdfUtil.largeBold, true, "Bill No: "+bill.getId(), "Bill Issued On: "+bill.getGeneratedDate(), "Receipt No: "+receipt.getId(), "Order Id: "+order.getId());
        infoCell.addElement(info);

        PdfPCell receiptDetailCell = new PdfPCell();
        PdfUtil.setPadding(receiptDetailCell, 0, 0, 10, 10);
        Paragraph detail = PdfUtil.getParagraph(PdfUtil.largeBold, true, "From: "+bill.getId(), "Delivered at: "+bill.getGeneratedDate(), "Mode of Payment : "+receipt.getId(), "Time of Payment: "+bill.getGeneratedDate(), "Card Number: ");
        receiptDetailCell.addElement(detail);

        PdfPCell billAmountCell = new PdfPCell();
        PdfUtil.setPadding(billAmountCell, 0, 0, 10, 10);
        Paragraph amount = PdfUtil.getParagraph(PdfUtil.largeBold, true, "Total Amount Billed: "+bill.getBillAmount());
        billAmountCell.addElement(amount);

        PdfPCell receiptCell = new PdfPCell();
        PdfUtil.setPadding(receiptCell, 0, 0, 10, 10);
        Paragraph receivedBy = PdfUtil.getParagraph(PdfUtil.largeBold, true, "Received by: "+bill.getCustomer().getUser().getFullName());
        receiptCell.addElement(receivedBy);

        PdfUtil.setBorder(0, infoCell, receiptDetailCell, billAmountCell, receiptCell);

        PdfPTable receiptTable = new PdfPTable(1);
        receiptTable.spacingBefore();
        receiptTable.setWidthPercentage(100);
        receiptTable.addCell(infoCell);
        receiptTable.addCell(receiptDetailCell);
        receiptTable.addCell(billAmountCell);
        receiptTable.addCell(receiptCell);

        receiptTable.getDefaultCell().setBorder(0);
        document.add(receiptTable);
    }

   /* private void sendInvoicePaymentNReceiptMail() {

    }*/

    private void addFooter(Document document) throws Exception {
        PdfUtil.addEmptyLine(document, 2);//add empty line

        document.add(PdfUtil.getParagraph(PdfUtil.smallFont, "Thank you for your business."));
        document.add(PdfUtil.getParagraph(PdfUtil.smallFont, "If you have any questions, contact us at support@iDelivr.com"));
        document.add(PdfUtil.getParagraph(PdfUtil.smallFont, "View all invoices: " + INVOICE_URL));
    }





    private File getFile(MerchantEntity merchant, String name) {
        String dir = getInvoiceDir(merchant, File.separator);

        File invoiceDir = new File(HOME_DIR + File.separator + dir + File.separator);
        if (!invoiceDir.exists()) {
            invoiceDir.mkdirs();
        }

        String fileName = DateUtil.getCurrentDate() + "_" +name+ ".pdf";
        return new File(invoiceDir, fileName);
    }

    private File getBillAndReceiptFile(MerchantEntity merchant, OrderEntity order, String name) {
        String dir = getBillAndReiceptDir(merchant, order, File.separator);

        File invoiceDir = new File(HOME_DIR + File.separator + dir + File.separator);
        if (!invoiceDir.exists()) {
            invoiceDir.mkdirs();
        }

        String fileName = DateUtil.getCurrentDate() + "_" +name+ ".pdf";
        return new File(invoiceDir, fileName);
    }

    private String getInvoiceDir(MerchantEntity merchant, String separator) {
        String dir = MessageBundle.separateString(separator, "Invoices", "Merchant_" + merchant.getId());

        return dir;
    }

    private String getBillAndReiceptDir(MerchantEntity merchant, OrderEntity order, String separator) {
        String dir = MessageBundle.separateString(separator, "receipts", "Merchant_" + merchant.getId(), "Order_" + merchant.getId());
        return dir;
    }

}
