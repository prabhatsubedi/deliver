package com.yetistep.delivr.util;


import com.itextpdf.text.*;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.yetistep.delivr.enums.PreferenceType;
import com.yetistep.delivr.model.*;
import org.apache.commons.mail.EmailException;
import com.yetistep.delivr.service.inf.SystemPropertyService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.List;
import java.util.Map;

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

    public String generateInvoice(List<OrderEntity> orders, MerchantEntity merchant, InvoiceEntity invoice, StoreEntity store, String serverUrl, Map<String, String> preferences) throws Exception {
            File invoiceFile = generateInvoicePDF(orders, merchant, invoice, store, serverUrl, preferences);

            if (invoiceFile == null)
                return null;

            String invoicePath = invoiceFile.getPath();

            //upload invoice to S3 Bucket
            if (!MessageBundle.isLocalHost()) {
                int noOfRetry = 3;
                for (int i = 0; i < noOfRetry; i++) {//retry three time, if exception occurs
                    try {
                        String dir = getInvoiceDir(merchant, store, File.separator);
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


    public String generateBillAndReceipt(OrderEntity order, BillEntity bill, ReceiptEntity receipt, String serverUrl, Map<String, String> preferences) throws Exception{
        File billFile = generateBillAndReceiptPDF(order, bill, receipt, serverUrl, preferences);

        if (billFile == null)
            return null;

        String billPath = billFile.getPath();

        //upload invoice to S3 Bucket
        if (!MessageBundle.isLocalHost()) {
            int noOfRetry = 3;
            for (int i = 0; i < noOfRetry; i++) {//retry three time, if exception occurs
                try {
                    String dir = getBillAndReiceptDir(order.getStore().getStoresBrand().getMerchant(), order, File.separator);

                    String bucketUrl = AmazonUtil.uploadFileToS3(billFile, dir, billFile.getName(), true);
                    billPath = AmazonUtil.cacheImage(bucketUrl);
                    break;
                } catch (Exception e) {
                    if (i == (noOfRetry - 1)) throw e;
                    GeneralUtil.wait(1500);
                }
            }
        }
        return billPath;
    }

    private File generateBillAndReceiptPDF(OrderEntity order, BillEntity bill, ReceiptEntity receipt, String serverUrl, Map<String, String> preferences) throws Exception{
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
            addPdfHeader(document, serverUrl, preferences);

            addBillBody(document, order, bill, preferences);

            document.newPage();

            //add receipt header
            addPdfHeader(document, serverUrl, preferences);

            addReceiptBody(document, order, receipt, bill, preferences);

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

    private File generateInvoicePDF(List<OrderEntity> orders, MerchantEntity merchant, InvoiceEntity invoice, StoreEntity store, String serverUrl, Map<String, String> preferences) throws Exception {

        FileOutputStream stream = null;
        File invoiceFile = null;


        try{
            Document document = new Document();
            /*Integer cntOrder = 1;*/

            invoiceFile = getFile(merchant, store, "invoice");
            stream = new FileOutputStream(invoiceFile);
            PdfWriter writer = PdfWriter.getInstance(document, stream);
            document.open();
            document.setMargins(20, 20, 20, 20);
            //add doc header
            addPdfHeader(document, serverUrl, preferences);

            addInvoiceDetail(document, merchant, invoice, store);
            //document.newPage();

            addInvoiceBody(document, merchant, orders, preferences);

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
    private void addPdfHeader(Document document, String serverUrl, Map<String, String> preferences) throws Exception {
        //address cell
        String name = preferences.get("COMPANY_NAME");
        String address = preferences.get("COMPANY_ADDRESS");
        String[] addressInArray = address.split(",");
        String street = addressInArray[0]+","+addressInArray[1];
        String city = addressInArray[2]+","+addressInArray[3];
        String reg = preferences.get("REGISTRATION_NO");
        String vat = preferences.get("VAT_NO");
        String imageUrl = serverUrl+"/resources/images/idelivr-logo-name.png";

        //logo cell
        PdfPCell logoCell = new PdfPCell();
        Image logo = Image.getInstance(new URL(imageUrl));
        logo.setAlignment(Element.ALIGN_LEFT);
        logo.setWidthPercentage(60);
        logoCell.addElement(logo);
        logoCell.setRight(40);

        //contact cell
        PdfPCell contactCell = new PdfPCell();
        com.itextpdf.text.Font supportFont = new com.itextpdf.text.Font(FontFactory.getFont(FontFactory.TIMES_ROMAN, 9f));
        supportFont.setColor(new BaseColor(0xaaaaaa));

        Paragraph support = PdfUtil.getParagraph(supportFont, true, "iDelivr Customer Care : "+preferences.get("HELPLINE_NUMBER")+" | "+preferences.get("SUPPORT_EMAIL"));
        support.setAlignment(Element.ALIGN_LEFT);
        support.getFont().setColor(new BaseColor(0xaaaaaa));
        PdfUtil.setPadding(contactCell, 0, 0, 30, 0);
        contactCell.addElement(support);

        //address cell
        PdfPCell addressCell = new PdfPCell();
        com.itextpdf.text.Font infoFont = new com.itextpdf.text.Font(FontFactory.getFont(FontFactory.TIMES_ROMAN, 16f));
        infoFont.setColor(new BaseColor(0xaaaaaa));
        Paragraph info = PdfUtil.getParagraph(infoFont, true, name, street, city, "Reg: " + reg, "Vat: "+vat);
        info.getFont().setColor(new BaseColor(0x9c9c9c));
        info.setAlignment(Element.ALIGN_LEFT);
        info.setLeading(19);
        PdfUtil.setPadding(addressCell, 20, 0, 20, 50);
        addressCell.addElement(info);
        addressCell.setColspan(2);

        //no border on the cells
        PdfUtil.setBorder(0, addressCell, contactCell, logoCell);

        //set bottom border on contact cell
        contactCell.setBorderColorBottom(new BaseColor(0xff9235));
        contactCell.setBorderWidthBottom(new Float(0.3));

        //set bottom border on address cell
        addressCell.setBorderColorBottom(new BaseColor(0xFF9235));
        addressCell.setBorderWidthBottom(new Float(0.3));

        //add cells to table
        PdfPTable headerTable = new PdfPTable(2);
        float[] columnWidths = new float[] {50f, 50f};
        headerTable.setWidths(columnWidths);
        headerTable.setWidthPercentage(100);
        headerTable.addCell(logoCell);
        headerTable.addCell(contactCell);
        headerTable.addCell(addressCell);

        //add table to document
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
        Paragraph transactionHeader = PdfUtil.getParagraph(PdfUtil.largeBold, true, "Transaction Detail");
        Paragraph transactionInfo = PdfUtil.getParagraph(PdfUtil.smallFont, true, "Transaction Between: "+invoice.getFromDate()+" and "+invoice.getToDate(), "Invoice Date: "+invoice.getGeneratedDate());
        transactionHeader.setAlignment(Element.ALIGN_LEFT);
        transactionInfo.setAlignment(Element.ALIGN_LEFT);
        transactionCell.addElement(transactionHeader);
        transactionCell.addElement(transactionInfo);

        PdfPCell billingCell = new PdfPCell();
        PdfUtil.setPadding(transactionCell, 0, 0, 10, 10);
        Paragraph billingHeader = PdfUtil.getParagraph(PdfUtil.largeBold, true, "Billing Address");
        Paragraph billingInfo = PdfUtil.getParagraph(PdfUtil.smallFont, true, store.getStoresBrand().getBrandName(), store.getStreet()+", "+store.getCity(), store.getState()+", "+store.getCountry(), "Phone: "+store.getContactNo() );
        billingInfo.setAlignment(Element.ALIGN_RIGHT);
        billingHeader.setAlignment(Element.ALIGN_RIGHT);
        billingCell.addElement(billingHeader);
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

    private void addInvoiceBody(Document document, MerchantEntity merchant, List<OrderEntity> orders, Map<String, String> preferences) throws Exception {

        Integer cntOrder = 1;

        PdfPTable billingTable = new PdfPTable(4);
        billingTable.setWidthPercentage(100);
        billingTable.setWidths(new float[]{10, 30, 30, 20});

        String currency = preferences.get("CURRENCY");
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase("SN", PdfUtil.smallBold), PdfUtil.getPhrase("Date and Time of Transaction", PdfUtil.smallBold), PdfUtil.getPhrase("Order No.", PdfUtil.smallBold), PdfUtil.getPhrase("Amount ("+currency+")", PdfUtil.smallBold));
        BigDecimal totalOrderAmount = BigDecimal.ZERO;
        BigDecimal totalServiceCharge = BigDecimal.ZERO;
        for (OrderEntity order: orders){
            for (ItemsOrderEntity itemsOrderEntity: order.getItemsOrder()) {
                BigDecimal itemServiceChargePcn = itemsOrderEntity.getItem().getServiceCharge();
                BigDecimal itemServiceCharge = itemsOrderEntity.getItemTotal().multiply(itemServiceChargePcn).divide(new BigDecimal(100));
                totalServiceCharge.add(itemServiceCharge);
            }
            //add order
            PdfUtil.addRow(billingTable, PdfUtil.getPhrase(cntOrder), PdfUtil.getPhrase(order.getOrderDate()), PdfUtil.getPhrase(order.getId()), PdfUtil.getPhrase(order.getTotalCost()));
            totalOrderAmount = totalOrderAmount.add(order.getTotalCost());

            if(cntOrder%20==0){
                document.newPage();
            }
            cntOrder++;
        }
        BigDecimal totalTaxableAmount =  totalOrderAmount.add(totalOrderAmount);
        BigDecimal vatAmount =  (totalOrderAmount.add(totalOrderAmount)).multiply(new BigDecimal(Integer.parseInt(preferences.get("DELIVERY_FEE_VAT")))).divide(new BigDecimal(100));
        BigDecimal grandTotalAmount = totalTaxableAmount.add(vatAmount);
        BigDecimal commissionAmount = grandTotalAmount.multiply(merchant.getCommissionPercentage()).divide(new BigDecimal(100));
        BigDecimal netPayableAmount = grandTotalAmount.subtract(commissionAmount);


        Phrase subTotal = PdfUtil.getPhrase("Sub Total", PdfUtil.largeBold);
        Phrase serviceCharge = PdfUtil.getPhrase("Service Charge", PdfUtil.largeBold);
        Phrase taxableAmount = PdfUtil.getPhrase("Taxable Amount", PdfUtil.largeBold);
        Phrase vat = PdfUtil.getPhrase("Vat("+preferences.get("DELIVERY_FEE_VAT")+"%)", PdfUtil.largeBold);
        Phrase grandTotal = PdfUtil.getPhrase("Grand Total", PdfUtil.largeBold);
        Phrase commission = PdfUtil.getPhrase("Commission("+merchant.getCommissionPercentage()+")", PdfUtil.largeBold);
        Phrase totalPayable = PdfUtil.getPhrase("Net Payable", PdfUtil.largeBold);


        PdfUtil.addRow(billingTable, PdfUtil.getPhrase(""), PdfUtil.getPhrase(""), subTotal, PdfUtil.getPhrase(totalOrderAmount));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase(""), PdfUtil.getPhrase(""), serviceCharge, PdfUtil.getPhrase(totalServiceCharge));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase(""), PdfUtil.getPhrase(""), taxableAmount, PdfUtil.getPhrase(totalTaxableAmount));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase(""), PdfUtil.getPhrase(""), vat, PdfUtil.getPhrase(vatAmount));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase(""), PdfUtil.getPhrase(""), grandTotal, PdfUtil.getPhrase(grandTotalAmount));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase(""), PdfUtil.getPhrase(""), commission, PdfUtil.getPhrase(commissionAmount));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase(""), PdfUtil.getPhrase(""), totalPayable, PdfUtil.getPhrase(netPayableAmount));

        document.add(billingTable);
    }


    private void addBillBody(Document document, OrderEntity order, BillEntity bill, Map<String, String> preferences) throws Exception {
        //add invoice detail
        Paragraph title = PdfUtil.getParagraph(PdfUtil.smallBold, "Invoice : "+bill.getId());
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        PdfUtil.addEmptyLine(document, 1);//add empty line


        PdfPCell orderCell = new PdfPCell();
        PdfUtil.setPadding(orderCell, 0, 0, 10, 10);
        Paragraph orderInfo = PdfUtil.getParagraph(PdfUtil.smallFont, true, "Order Detail", "Order Id: "+order.getId(), "Order Date: "+order.getOrderDate(), "Invoice Date: "+bill.getGeneratedDate());
        orderCell.addElement(orderInfo);

        orderCell.setBorderColorBottom(new BaseColor(0xFF9235));
        orderCell.setBorderWidthBottom(1);

        PdfPCell addressCell = new PdfPCell();
        PdfUtil.setPadding(addressCell, 0, 0, 10, 10);
        String street = order.getAddress().getStreet();
        String city = order.getAddress().getCity();
        String state = order.getAddress().getState();
        String country = order.getAddress().getCountry();
        String mobile = order.getAddress().getMobileNumber();

        Paragraph addressInfo = PdfUtil.getParagraph(PdfUtil.smallFont, true, "Customer Address", order.getCustomer().getUser().getFullName(), street+", "+city, state+", "+country, "Phone: "+mobile);
        addressInfo.setAlignment(Element.ALIGN_RIGHT);
        addressCell.addElement(addressInfo);

        addressCell.setBorderColorBottom(new BaseColor(0xFF9235));
        addressCell.setBorderWidthBottom(1);

        PdfUtil.setBorder(0, orderCell, addressCell);
        //add cells to table
        PdfPTable billTable = new PdfPTable(2);
        billTable.setWidthPercentage(100);

        orderCell.setBorderColorBottom(new BaseColor(0xFF9235));
        orderCell.setBorderWidthBottom(1);

        addressCell.setBorderColorBottom(new BaseColor(0xFF9235));
        addressCell.setBorderWidthBottom(1);

        billTable.addCell(orderCell);
        billTable.addCell(addressCell);


        billTable.getDefaultCell().setBorder(0);

        document.add(billTable);

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


    private void addReceiptBody(Document document, OrderEntity order, ReceiptEntity receipt, BillEntity bill, Map<String, String> preferences) throws Exception {
        //add invoice detail
        com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(FontFactory.getFont(FontFactory.TIMES_ROMAN, 19f));
        titleFont.setColor(new BaseColor(0xFF9235));
        Paragraph title = PdfUtil.getParagraph(titleFont, "Receipt for Payment of Processing Charge and Delivery Fee");
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingBefore(50);
        document.add(title);

        PdfPCell infoCell = new PdfPCell();
        com.itextpdf.text.Font infoFont = new com.itextpdf.text.Font(FontFactory.getFont(FontFactory.TIMES_ROMAN, 13f));
        infoFont.setColor(new BaseColor(0x969696));
        Paragraph info = PdfUtil.getParagraph(infoFont, true, "Invoice No: "+bill.getId(), "Invoice Issued On: "+bill.getGeneratedDate(), "Receipt No: "+receipt.getId(), "Order Id: "+order.getId());
        infoCell.addElement(info);
        PdfUtil.setPadding(infoCell, 20, 0, 20, 50);


        PdfPCell receiptDetailCell = new PdfPCell();
        String paymentMode = order.getPaymentMode().toString();
        String paymentModeArr[] = paymentMode.split("_");
        String formattedPaymentMode = "";
        for (String pMode: paymentModeArr){
            formattedPaymentMode+=pMode.toLowerCase()+" ";
        }
        Paragraph detail = PdfUtil.getParagraph(infoFont, true, "Date of Payment: "+bill.getGeneratedDate(), "Mode of Payment : "+formattedPaymentMode);
        receiptDetailCell.addElement(detail);
        PdfUtil.setPadding(receiptDetailCell, 20, 0, 20, 50);


        PdfPCell billAmountCell = new PdfPCell();
        Paragraph amount = PdfUtil.getParagraph(infoFont, true, "Total Amount Billed: "+receipt.getReceiptAmount());
        billAmountCell.addElement(amount);
        PdfUtil.setPadding(billAmountCell, 20, 0, 20, 50);


        PdfPCell receiptCell = new PdfPCell();
        Paragraph receivedBy = PdfUtil.getParagraph(infoFont, true, "Received by: "+order.getDeliveryBoy().getUser().getFullName());
        receiptCell.addElement(receivedBy);
        PdfUtil.setPadding(receiptCell, 20, 0, 20, 50);

        PdfUtil.setBorder(0, infoCell, receiptDetailCell, billAmountCell, receiptCell);


        //set bottom border on address cell
        infoCell.setBorderColorBottom(new BaseColor(0xFF9235));
        infoCell.setBorderWidthBottom(new Float(0.3));

        receiptDetailCell.setBorderColorBottom(new BaseColor(0xFF9235));
        receiptDetailCell.setBorderWidthBottom(new Float(0.3));

        billAmountCell.setBorderColorBottom(new BaseColor(0xFF9235));
        billAmountCell.setBorderWidthBottom(new Float(0.3));

        receiptCell.setBorderColorBottom(new BaseColor(0xFF9235));
        receiptCell.setBorderWidthBottom(new Float(0.3));





        PdfPTable receiptTable = new PdfPTable(1);
        receiptTable.spacingBefore();
        receiptTable.setWidthPercentage(100);
        receiptTable.addCell(infoCell);
        receiptTable.addCell(receiptDetailCell);
        receiptTable.addCell(billAmountCell);
        receiptTable.addCell(receiptCell);

        receiptTable.getDefaultCell().setBorder(0);

        document.add(receiptTable);
        LineSeparator ls = new LineSeparator();
        ls.setLineColor(new BaseColor(0xFF9235));
        ls.setPercentage(100);
        document.add(ls);
    }

   /* private void sendInvoicePaymentNReceiptMail() {

    }*/

    private void addFooter(Document document) throws Exception {
        PdfUtil.addEmptyLine(document, 2);//add empty line

        document.add(PdfUtil.getParagraph(PdfUtil.smallFont, "Thank you for your business."));
        document.add(PdfUtil.getParagraph(PdfUtil.smallFont, "If you have any questions, contact us at support@iDelivr.com"));
        document.add(PdfUtil.getParagraph(PdfUtil.smallFont, "View all invoices: " + INVOICE_URL));
    }





    private File getFile(MerchantEntity merchant, StoreEntity store, String name) {
        String dir = getInvoiceDir(merchant, store, File.separator);

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

    private String getInvoiceDir(MerchantEntity merchant, StoreEntity store, String separator) {
        String dir = MessageBundle.separateString(separator, "Invoices", "Merchant_" + merchant.getId(), "StoreBrand_"+store.getStoresBrand().getId(), "Store"+store.getId());

        return dir;
    }

    private String getBillAndReiceptDir(MerchantEntity merchant, OrderEntity order, String separator) {
        String dir = MessageBundle.separateString(separator, "receipts", "Merchant_" + merchant.getId(), "Order_" + merchant.getId());
        return dir;
    }

}
