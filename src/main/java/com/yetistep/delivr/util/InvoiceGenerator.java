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

    public String generateInvoice(List<OrderEntity> orders, MerchantEntity merchant, InvoiceEntity invoice) throws Exception {
            File invoiceFile = generateInvoicePDF(orders, merchant, invoice);

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


    public String generateBillAndReceipt(OrderEntity order, BillEntity bill, ReceiptEntity receipt) throws Exception{
        File invoiceFile = generateBillAndReceiptPDF(order, bill, receipt);

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

    private File generateBillAndReceiptPDF(OrderEntity order, BillEntity bill, ReceiptEntity receipt) throws Exception{
        FileOutputStream stream = null;
        File billAndReceiptFile = null;


        try{
            Document document = new Document();
            Integer cntPage = 1;

            billAndReceiptFile = getBillAndReceiptFile(order.getStore().getStoresBrand().getMerchant(), order, "receipt");
            stream = new FileOutputStream(billAndReceiptFile);
            PdfWriter writer = PdfWriter.getInstance(document, stream);
            document.open();
            document.setMargins(20, 20, 20, 20);
            //add doc header
            addBillAndReceiptHeader(document);

            addBillBody(document, order, bill);

            document.newPage();

            addBillAndReceiptHeader(document);

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

    private File generateInvoicePDF(List<OrderEntity> orders, MerchantEntity merchant, InvoiceEntity invoice) throws Exception {

        FileOutputStream stream = null;
        File invoiceFile = null;


        try{
            Document document = new Document();
            Integer cntPage = 1;

            invoiceFile = getFile(merchant, "invoice");
            stream = new FileOutputStream(invoiceFile);
            PdfWriter writer = PdfWriter.getInstance(document, stream);
            document.open();
            document.setMargins(20, 20, 20, 20);
            //add doc header
            addInvoiceHeader(document, invoice);

            for (OrderEntity order: orders){
                //add invoice body
                addInvoiceBody(document, order, merchant, invoice);

                //add invoice footer
                document.newPage();
                cntPage++;
            }
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

    //create a header for the company
    private void addInvoiceHeader(Document document, InvoiceEntity invoice) throws Exception {
        int bottomPadding = 35;

        Paragraph title = PdfUtil.getParagraph(PdfUtil.catFont, "INVOICE");
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        //add empty line
        PdfUtil.addEmptyLine(document, 2);

        //address cell
        PdfPCell addressCell = new PdfPCell();
        PdfUtil.setPadding(addressCell, 0, 0, bottomPadding, 0);
        addressCell.addElement(PdfUtil.getParagraph(PdfUtil.catFont, ""));

        String address = "";
        String contactNo = "";
        String vatNo = "";

        Paragraph info = PdfUtil.getParagraph(PdfUtil.smallFont, true, address, "Ph: " + contactNo, "VAT No.: " + vatNo);
        addressCell.addElement(info);

        //billing cell
        PdfPCell billingCell = new PdfPCell();

        Paragraph paragraph1 = new Paragraph();
        paragraph1.add(PdfUtil.getPhrase("Billing Date: ", PdfUtil.smallBold));
        paragraph1.add(PdfUtil.getPhrase(invoice.getGeneratedDate(), PdfUtil.smallFont));
        paragraph1.setAlignment(Element.ALIGN_RIGHT);

        Paragraph paragraph2 = new Paragraph();
        paragraph2.add(PdfUtil.getPhrase("Invoice No: ", PdfUtil.smallBold));
        paragraph2.add(PdfUtil.getPhrase(invoice.getId(), PdfUtil.smallFont));
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

    //create a header for the company
    private void addBillAndReceiptHeader(Document document) throws Exception {
        int bottomPadding = 35;

        //address cell
        PdfPCell addressCell = new PdfPCell();
        PdfUtil.setPadding(addressCell, 0, 0, bottomPadding, 0);
        addressCell.addElement(PdfUtil.getParagraph(PdfUtil.catFont, ""));

        String name = "Deliver Private Limited";
        String street = "Charkhal Road, Dillibazar";
        String city = "Kathmandu, Nepal";
        String reg = "258956555";

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

    private void addInvoiceBody(Document document, OrderEntity order, MerchantEntity merchant, InvoiceEntity invoice) throws Exception {
        //add invoice detail
        Paragraph title1 = PdfUtil.getParagraph(PdfUtil.smallBold, "Invoice Details");
        document.add(title1);
        PdfUtil.addEmptyLine(document, 1);//add empty line

        PdfPCell advertiserCell = new PdfPCell();
        PdfUtil.setPadding(advertiserCell, 0, 0, 10, 10);
        Paragraph advertiserInfo = PdfUtil.getParagraph(PdfUtil.smallFont, true, "To:", merchant.getBusinessTitle());
        advertiserCell.addElement(advertiserInfo);

        PdfPCell couponCell = new PdfPCell();
        PdfUtil.setPadding(couponCell, 0, 0, 10, 10);
        Paragraph couponInfo = PdfUtil.getParagraph(PdfUtil.smallFont, true, "Deal ID: " + order.getId(), "Order: " + order.getOrderName());
        couponCell.addElement(couponInfo);

        //add cells to table
        PdfPTable invoiceTable = new PdfPTable(2);
        invoiceTable.setWidthPercentage(100);
        invoiceTable.addCell(advertiserCell);
        invoiceTable.addCell(couponCell);

        document.add(invoiceTable);

        PdfUtil.addEmptyLine(document, 2);//add empty line

        //add billing activity
        Paragraph title2 = PdfUtil.getParagraph(PdfUtil.smallBold, "Billing Activity");
        document.add(title2);

        PdfUtil.addEmptyLine(document, 1);//add empty line

        //add billing table
        PdfPTable billingTable = new PdfPTable(3);
        billingTable.setWidthPercentage(100);
        billingTable.setWidths(new float[]{10, 45, 45});

        //Apparently, setting total sales to zero
        invoice.setAmount(BigDecimal.ZERO);

        String currency = "NRS";
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase("SN", PdfUtil.smallBold), PdfUtil.getPhrase("Item", PdfUtil.smallBold), PdfUtil.getPhrase("Amount", PdfUtil.smallBold));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase("1"), PdfUtil.getPhrase("Deal Fee"),
                PdfUtil.getPhrase(currency + " " + invoice.getAmount()));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase(""), PdfUtil.getPhrase("Total", PdfUtil.smallBold),
                PdfUtil.getPhrase(currency + " " + invoice.getAmount(), PdfUtil.smallBold));

        document.add(billingTable);


        PdfUtil.addEmptyLine(document, 2);//add empty line

        //add notes
        document.add(PdfUtil.getParagraph(PdfUtil.smallFont, "Notes:"));
        com.itextpdf.text.List noteList = new com.itextpdf.text.List(true);
        noteList.add(new ListItem("Total cost is calculated as " + currency + " " + invoice.getAmount() + " per Deal per month.", PdfUtil.smallFont));
        noteList.add(new ListItem("Deals with less than month period also, same rate is applicable.", PdfUtil.smallFont));
        document.add(noteList);
    }


    private void addBillBody(Document document, OrderEntity order, BillEntity bill) throws Exception {
        //add invoice detail
        Paragraph title = PdfUtil.getParagraph(PdfUtil.smallBold, "Tax Invoice : "+bill.getId());
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        PdfUtil.addEmptyLine(document, 1);//add empty line


        PdfPCell orderCell = new PdfPCell();
        PdfUtil.setPadding(orderCell, 0, 0, 10, 10);
        Paragraph advertiserInfo = PdfUtil.getParagraph(PdfUtil.smallFont, true, "Order Detail Billing Address", "Order Id: "+order.getId(), "Order Date: "+order.getOrderDate(), "Invoice Date: "+bill.getGeneratedDate());
        orderCell.addElement(advertiserInfo);

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

        Paragraph couponInfo = PdfUtil.getParagraph(PdfUtil.smallFont, true, "Billing Address", street+", "+city, state+", "+country, "Phone: "+mobile);
        addressCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        addressCell.addElement(couponInfo);

        //add cells to table
        PdfPTable invoiceTable = new PdfPTable(2);
        invoiceTable.setWidthPercentage(100);
        invoiceTable.addCell(orderCell);
        invoiceTable.addCell(addressCell);

        PdfUtil.setBorder(0, orderCell, addressCell);

        document.add(invoiceTable);

        PdfUtil.addEmptyLine(document, 2);//add empty line

        //add billing table
        PdfPTable billingTable = new PdfPTable(2);
        billingTable.setWidthPercentage(100);
        billingTable.setWidths(new float[]{45, 45});


        String currency = "NRS";
       PdfUtil.addRow(billingTable, PdfUtil.getPhrase("Title", PdfUtil.smallBold), PdfUtil.getPhrase("Amount"+currency, PdfUtil.smallBold));
       PdfUtil.addRow(billingTable, PdfUtil.getPhrase("iDelivr Fee"),
                PdfUtil.getPhrase(bill.getSystemServiceCharge()));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase("Delivery Fee"),
                PdfUtil.getPhrase(currency + " " + bill.getDeliveryCharge(), PdfUtil.smallBold));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase("Subtotal"),
                PdfUtil.getPhrase(currency + " " + bill.getDeliveryCharge().add(bill.getSystemServiceCharge()), PdfUtil.smallBold));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase("Vat"),
                PdfUtil.getPhrase(currency + " " + bill.getVat(), PdfUtil.smallBold));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase("Grand Total"),
                PdfUtil.getPhrase(currency + " " + bill.getDeliveryCharge().add(bill.getSystemServiceCharge()).add(bill.getVat()), PdfUtil.smallBold));

        document.add(billingTable);



    }


    private void addReceiptBody(Document document, OrderEntity order, ReceiptEntity receipt, BillEntity bill) throws Exception {
        //add invoice detail
        Paragraph title = PdfUtil.getParagraph(PdfUtil.smallBold, "Receipt for Payment of Delivery and iDelivery Fee");
        document.add(title);
        title.setAlignment(Element.ALIGN_CENTER);
        PdfUtil.addEmptyLine(document, 1);//add empty line

        PdfPCell infoCell = new PdfPCell();
        PdfUtil.setPadding(infoCell, 0, 0, 10, 10);
        Paragraph info = PdfUtil.getParagraph(PdfUtil.smallFont, true, "Bill No: "+bill.getId(), "Bill Issued On: "+bill.getGeneratedDate(), "Receipt No: "+receipt.getId(), "Order Id: "+order.getId());
        infoCell.addElement(info);

        PdfUtil.addEmptyLine(document, 2);//add empty line

        PdfPCell receiptDetailCell = new PdfPCell();
        PdfUtil.setPadding(infoCell, 0, 0, 10, 10);
        Paragraph detail = PdfUtil.getParagraph(PdfUtil.smallFont, true, "From: "+bill.getId(), "Delivered at: "+bill.getGeneratedDate(), "Mode of Payment : "+receipt.getId(), "Time of Payment: "+order.getId(), "Card Number: "+order.getId());
        receiptDetailCell.addElement(detail);

        PdfUtil.addEmptyLine(document, 2);//add empty line

        PdfPCell billAmountCell = new PdfPCell();
        PdfUtil.setPadding(infoCell, 0, 0, 10, 10);
        Paragraph amount = PdfUtil.getParagraph(PdfUtil.smallFont, true, "Total Amount Billed: "+bill.getBillAmount());
        billAmountCell.addElement(amount);

        PdfUtil.addEmptyLine(document, 2);//add empty line

        PdfPCell receivedCell = new PdfPCell();
        PdfUtil.setPadding(infoCell, 0, 0, 10, 10);
        Paragraph receivedBy = PdfUtil.getParagraph(PdfUtil.smallFont, true, "Received by: "+bill.getCustomer().getUser().getFullName());
        receivedCell.addElement(receivedBy);

        PdfUtil.addEmptyLine(document, 2);//add empty line

        PdfPTable receiptTable = new PdfPTable(1);
        receiptTable.setWidthPercentage(100);
        receiptTable.addCell(infoCell);
        receiptTable.addCell(receiptDetailCell);
        receiptTable.addCell(billAmountCell);
        receiptTable.addCell(receivedCell);

        PdfUtil.setBorder(0, infoCell, receiptDetailCell, billAmountCell, receivedCell);

        document.add(receiptTable);
    }

   /* private void sendInvoicePaymentNReceiptMail() {

    }*/

    private void addFooter(Document document) throws Exception {
        PdfUtil.addEmptyLine(document, 2);//add empty line

        document.add(PdfUtil.getParagraph(PdfUtil.smallFont, "Thank you for your business."));
        document.add(PdfUtil.getParagraph(PdfUtil.smallFont, "If you have any questions, contact us at support@dealify.com"));
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
