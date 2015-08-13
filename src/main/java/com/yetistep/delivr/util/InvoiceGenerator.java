package com.yetistep.delivr.util;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPRow;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.service.inf.SystemPropertyService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
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

    public String generateInvoice(List<OrderEntity> orders, MerchantEntity merchant, InvoiceEntity invoice, StoreEntity store, String imageUrl, Map<String, String> preferences) throws Exception {
            File invoiceFile = generateInvoicePDF(orders, merchant, invoice, store, imageUrl, preferences);

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


    public String generateDBoyPayStatement(List<OrderEntity> orders, DeliveryBoyEntity deliveryBoy, DBoyPaymentEntity dBoyPayment, String imageUrl, Map<String, String> preferences) throws Exception {
        File invoiceFile = generatedDBoyPaymentPDF(orders, deliveryBoy, dBoyPayment, imageUrl, preferences);

        if (invoiceFile == null)
            return null;

        String invoicePath = invoiceFile.getPath();

        //upload invoice to S3 Bucket
        if (!MessageBundle.isLocalHost()) {
            int noOfRetry = 3;
            for (int i = 0; i < noOfRetry; i++) {//retry three time, if exception occurs
                try {
                    String dir = getDBoyPaymentDir(deliveryBoy, File.separator);
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

    private File generateInvoicePDF(List<OrderEntity> orders, MerchantEntity merchant, InvoiceEntity invoice, StoreEntity store, String imageUrl, Map<String, String> preferences) throws Exception {

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
            addPdfHeader(document, imageUrl, preferences);

            addInvoiceDetail(document, merchant, invoice, store);
            //document.newPage();

            addInvoiceBody(document, merchant, orders, preferences);

            addFooter(document);

            document.newPage();

            addPdfHeader(document, imageUrl, preferences);

            addCommissionDetail(document, merchant, invoice, store);

            addCommissionBody(document, orders, preferences);

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
    private void addPdfHeader(Document document, String imageUrl, Map<String, String> preferences) throws Exception {
        //address cell
        String name = preferences.get("COMPANY_NAME");
        String address = preferences.get("COMPANY_ADDRESS");
        String[] addressInArray = address.split(",");
        String street = addressInArray[0]+","+addressInArray[1];
        String city = addressInArray[2]+","+addressInArray[3];
        String reg = preferences.get("REGISTRATION_NO");
        String vat = preferences.get("VAT_NO");
        //String imageUrl = serverUrl+"/resources/images/login-logo.png";

        //logo cell
        PdfPCell logoCell = new PdfPCell();
        System.out.println(imageUrl);
        if(imageUrl != null && !imageUrl.equals("")) {
            Image logo = Image.getInstance(new URL(imageUrl));
            logo.setAlignment(Element.ALIGN_LEFT);
            logo.setWidthPercentage(60);
            logoCell.addElement(logo);
        }
        logoCell.setRight(40);

        //contact cell
        PdfPCell contactCell = new PdfPCell();
        com.itextpdf.text.Font supportFont = new com.itextpdf.text.Font(FontFactory.getFont(FontFactory.TIMES_ROMAN, 9f));
        supportFont.setColor(new BaseColor(0xaaaaaa));

        Paragraph support = PdfUtil.getParagraph(supportFont, true, "Koolkat Customer Care : "+preferences.get("HELPLINE_NUMBER")+" | "+preferences.get("SUPPORT_EMAIL"));
        support.setAlignment(Element.ALIGN_LEFT);
        support.getFont().setColor(new BaseColor(0xaaaaaa));
        PdfUtil.setPadding(contactCell, 0, 0, 30, 0);
        contactCell.addElement(support);

        //address cell
        PdfPCell addressCell = new PdfPCell();
        com.itextpdf.text.Font infoFont = new com.itextpdf.text.Font(FontFactory.getFont(FontFactory.TIMES_ROMAN, 13f));
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
        com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(FontFactory.getFont(FontFactory.TIMES_ROMAN, 19f));
        titleFont.setColor(new BaseColor(0xFF9235));

        Paragraph title1 = PdfUtil.getParagraph(titleFont, "Statement of Transaction");
        title1.setAlignment(Element.ALIGN_CENTER);
        title1.setSpacingBefore(10);
        document.add(title1);
        PdfUtil.addEmptyLine(document, 1);//add empty line

        PdfPCell transactionCell = new PdfPCell();
        PdfUtil.setPadding(transactionCell, 20, 0, 20, 50);
        com.itextpdf.text.Font infoFont = new com.itextpdf.text.Font(FontFactory.getFont(FontFactory.TIMES_ROMAN, 13f));
        infoFont.setColor(new BaseColor(0x969696));
        Paragraph transactionHeader = PdfUtil.getParagraph(titleFont, true, "Transaction Detail");
        Paragraph transactionInfo = PdfUtil.getParagraph(infoFont, true, "Transaction Between: "+invoice.getFromDate()+" and "+invoice.getToDate(), "Statement No.: "+invoice.getId(), "Statement Date: "+invoice.getGeneratedDate());
        transactionHeader.setAlignment(Element.ALIGN_LEFT);
        transactionInfo.setAlignment(Element.ALIGN_LEFT);
        transactionCell.addElement(transactionHeader);
        transactionCell.addElement(transactionInfo);

        PdfPCell billingCell = new PdfPCell();
        PdfUtil.setPadding(transactionCell, 20, 0, 20, 50);
        Paragraph billingHeader = PdfUtil.getParagraph(titleFont, true, "Merchant Address");
        Paragraph billingInfo = PdfUtil.getParagraph(infoFont, true, store.getStoresBrand().getBrandName(), store.getStreet()+", "+store.getCity(), store.getState()+", "+store.getCountry(), "Phone: "+store.getContactNo() );
        billingInfo.setAlignment(Element.ALIGN_RIGHT);
        billingHeader.setAlignment(Element.ALIGN_RIGHT);
        billingCell.addElement(billingHeader);
        billingCell.addElement(billingInfo);

        PdfUtil.setBorder(0, transactionCell, billingCell);

        //set bottom border on contact cell
        billingCell.setBorderColorBottom(new BaseColor(0xff9235));
        billingCell.setBorderWidthBottom(new Float(0.3));

        //set bottom border on contact cell
        transactionCell.setBorderColorBottom(new BaseColor(0xff9235));
        transactionCell.setBorderWidthBottom(new Float(0.3));

        //add cells to table
        PdfPTable invoiceDetailTable = new PdfPTable(2);
        invoiceDetailTable.setWidthPercentage(100);
        invoiceDetailTable.addCell(transactionCell);
        invoiceDetailTable.addCell(billingCell);

        document.add(invoiceDetailTable);

        PdfUtil.addEmptyLine(document, 2);
    }


    private void addCommissionDetail(Document document, MerchantEntity merchant, InvoiceEntity invoice, StoreEntity store) throws Exception{
        //add invoice detail
        com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(FontFactory.getFont(FontFactory.TIMES_ROMAN, 19f));
        titleFont.setColor(new BaseColor(0xFF9235));

        Paragraph title1 = PdfUtil.getParagraph(titleFont, "Invoice No.: "+invoice.getId());
        Paragraph title2 = PdfUtil.getParagraph(titleFont, "For Commission");
        title1.setAlignment(Element.ALIGN_CENTER);
        title2.setAlignment(Element.ALIGN_CENTER);
        title1.setSpacingBefore(10);
        title2.setSpacingBefore(8);
        document.add(title1);
        document.add(title2);
        PdfUtil.addEmptyLine(document, 1);//add empty line

        PdfPCell transactionCell = new PdfPCell();
        PdfUtil.setPadding(transactionCell, 20, 0, 20, 50);
        com.itextpdf.text.Font infoFont = new com.itextpdf.text.Font(FontFactory.getFont(FontFactory.TIMES_ROMAN, 13f));
        infoFont.setColor(new BaseColor(0x969696));
        Paragraph transactionHeader = PdfUtil.getParagraph(titleFont, true, "Transaction Detail");
        Paragraph transactionInfo = PdfUtil.getParagraph(infoFont, true, "Transaction Between: "+invoice.getFromDate()+" and "+invoice.getToDate(), "Statement Date: "+invoice.getGeneratedDate(), "For Statement No.: "+invoice.getId());
        transactionHeader.setAlignment(Element.ALIGN_LEFT);
        transactionInfo.setAlignment(Element.ALIGN_LEFT);
        transactionCell.addElement(transactionHeader);
        transactionCell.addElement(transactionInfo);

        PdfPCell billingCell = new PdfPCell();
        PdfUtil.setPadding(transactionCell, 20, 0, 20, 50);
        Paragraph billingHeader = PdfUtil.getParagraph(titleFont, true, "Merchant Address");
        Paragraph billingInfo = PdfUtil.getParagraph(infoFont, true, store.getStoresBrand().getBrandName(), store.getStreet()+", "+store.getCity(), store.getState()+", "+store.getCountry(), "VAT: "+merchant.getVatNo(), "PAN: "+merchant.getPanNo(), "Phone: "+store.getContactNo() );
        billingInfo.setAlignment(Element.ALIGN_RIGHT);
        billingHeader.setAlignment(Element.ALIGN_RIGHT);
        billingCell.addElement(billingHeader);
        billingCell.addElement(billingInfo);

        PdfUtil.setBorder(0, transactionCell, billingCell);

        //set bottom border on contact cell
        billingCell.setBorderColorBottom(new BaseColor(0xff9235));
        billingCell.setBorderWidthBottom(new Float(0.3));

        //set bottom border on contact cell
        transactionCell.setBorderColorBottom(new BaseColor(0xff9235));
        transactionCell.setBorderWidthBottom(new Float(0.3));

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
        billingTable.setWidths(new float[]{15, 30, 35, 20});

        com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(FontFactory.getFont(FontFactory.TIMES_ROMAN, 19f));
        titleFont.setColor(new BaseColor(0xFF9235));

        com.itextpdf.text.Font tableHeadFont = new com.itextpdf.text.Font(FontFactory.getFont(FontFactory.TIMES_ROMAN, 13f));
        tableHeadFont.setColor(new BaseColor(0xFF9235));
        String currency = preferences.get("CURRENCY");
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase("SN", tableHeadFont), PdfUtil.getPhrase("Date and Time of Transaction", tableHeadFont), PdfUtil.getPhrase("Order No.", tableHeadFont), PdfUtil.getPhrase("Amount ("+currency+")", tableHeadFont));
        BigDecimal totalOrderAmount = BigDecimal.ZERO;
        BigDecimal totalServiceCharge = BigDecimal.ZERO;

        BigDecimal totalVatAmount = BigDecimal.ZERO;
        BigDecimal commissionAmount = BigDecimal.ZERO;
        BigDecimal commissionVatAmount = BigDecimal.ZERO;
        for (OrderEntity order: orders){
            commissionAmount = commissionAmount.add(order.getCommissionAmount());
            for (ItemsOrderEntity itemsOrderEntity: order.getItemsOrder()) {
                BigDecimal itemServiceCharge = BigDecimalUtil.percentageOf(itemsOrderEntity.getItemTotal(), BigDecimalUtil.checkNull(itemsOrderEntity.getServiceCharge()));
                totalServiceCharge = totalServiceCharge.add(itemServiceCharge);

                if(!order.getStore().getStoresBrand().getVatInclusive()) {
                    totalVatAmount = totalVatAmount.add(BigDecimalUtil.percentageOf(itemsOrderEntity.getItemTotal().add(itemServiceCharge), BigDecimalUtil.checkNull(itemsOrderEntity.getVat())));
                }
                commissionVatAmount = commissionVatAmount.add(BigDecimalUtil.percentageOf(itemsOrderEntity.getCommissionAmount(), BigDecimalUtil.checkNull(itemsOrderEntity.getVat())));
            }
            //add order
            PdfUtil.addRow(billingTable, PdfUtil.getPhrase(cntOrder), PdfUtil.getPhrase(order.getOrderDate()), PdfUtil.getPhrase(order.getId()), PdfUtil.getPhrase(order.getTotalCost().subtract(order.getDiscountFromStore())));
            totalOrderAmount = totalOrderAmount.add(order.getTotalCost().subtract(order.getDiscountFromStore()));

            if(cntOrder%20==0){
                document.newPage();
            }
            cntOrder++;
        }
        BigDecimal totalTaxableAmount =  totalOrderAmount.add(totalServiceCharge);
        BigDecimal grandTotalAmount = totalTaxableAmount.add(totalVatAmount);
        commissionAmount = commissionAmount.add(commissionVatAmount);
        BigDecimal netPayableAmount = grandTotalAmount.subtract(commissionAmount);


        Phrase subTotal = PdfUtil.getPhrase("Sub Total", tableHeadFont);
        Phrase serviceCharge = PdfUtil.getPhrase("Service Charge", tableHeadFont);
        Phrase taxableAmount = PdfUtil.getPhrase("Taxable Amount", tableHeadFont);
        Phrase vat;
        Phrase commission;
        if(BigDecimalUtil.isIntegerValue(new BigDecimal(preferences.get("DELIVERY_FEE_VAT")))){
             vat = PdfUtil.getPhrase("VAT Amount", tableHeadFont);
        } else  {
             vat = PdfUtil.getPhrase("VAT Amount", tableHeadFont);
        }

        Phrase grandTotal = PdfUtil.getPhrase("Grand Total", tableHeadFont);
        commission = PdfUtil.getPhrase("Total Commission", tableHeadFont);
        Phrase totalPayable = PdfUtil.getPhrase("Net Payable", tableHeadFont);


        PdfUtil.addRow(billingTable, PdfUtil.getPhrase(""), PdfUtil.getPhrase(""), subTotal, PdfUtil.getPhrase(totalOrderAmount.setScale(2, BigDecimal.ROUND_DOWN)));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase(""), PdfUtil.getPhrase(""), serviceCharge, PdfUtil.getPhrase(totalServiceCharge.setScale(2, BigDecimal.ROUND_DOWN)));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase(""), PdfUtil.getPhrase(""), taxableAmount, PdfUtil.getPhrase(totalTaxableAmount.setScale(2, BigDecimal.ROUND_DOWN)));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase(""), PdfUtil.getPhrase(""), vat, PdfUtil.getPhrase(totalVatAmount.setScale(2, BigDecimal.ROUND_DOWN)));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase(""), PdfUtil.getPhrase(""), grandTotal, PdfUtil.getPhrase(grandTotalAmount.setScale(2, BigDecimal.ROUND_DOWN)));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase(""), PdfUtil.getPhrase(""), commission, PdfUtil.getPhrase(commissionAmount.setScale(2, BigDecimal.ROUND_DOWN)));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase(""), PdfUtil.getPhrase(""), totalPayable, PdfUtil.getPhrase(netPayableAmount.setScale(2, BigDecimal.ROUND_DOWN)));

        for (PdfPRow row: billingTable.getRows()) {
            for (PdfPCell cell: row.getCells()){
                cell.setBorder(0);
                cell.setBorderColorBottom(new BaseColor(0xFF9235));
                cell.setBorderWidthBottom(new Float(0.3));
                cell.setPaddingBottom(20);
                cell.setPaddingTop(20);
                cell.setPaddingLeft(50);
            }
        }

        document.add(billingTable);
    }


    private void addCommissionBody(Document document, List<OrderEntity> orders, Map<String, String> preferences) throws Exception {

        PdfPTable commissionTable = new PdfPTable(2);
        commissionTable.setWidthPercentage(100);
        commissionTable.setWidths(new float[]{50, 50});

        com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(FontFactory.getFont(FontFactory.TIMES_ROMAN, 19f));
        titleFont.setColor(new BaseColor(0xFF9235));

        com.itextpdf.text.Font tableHeadFont = new com.itextpdf.text.Font(FontFactory.getFont(FontFactory.TIMES_ROMAN, 13f));
        tableHeadFont.setColor(new BaseColor(0xFF9235));

        String currency = preferences.get("CURRENCY");
        PdfUtil.addRow(commissionTable, PdfUtil.getPhrase("Title", tableHeadFont), PdfUtil.getPhrase("Amount("+currency+")", tableHeadFont));
        BigDecimal totalOrderAmount = BigDecimal.ZERO;
        BigDecimal commissionAmount = BigDecimal.ZERO;
        BigDecimal commissionVatAmount = BigDecimal.ZERO;
        for (OrderEntity order: orders){
            totalOrderAmount = totalOrderAmount.add(order.getTotalCost().subtract(order.getDiscountFromStore()));
            commissionAmount = commissionAmount.add(order.getCommissionAmount());
            for (ItemsOrderEntity itemsOrderEntity: order.getItemsOrder()) {
                commissionVatAmount = commissionVatAmount.add(BigDecimalUtil.percentageOf(itemsOrderEntity.getCommissionAmount(), BigDecimalUtil.checkNull(itemsOrderEntity.getVat())));
            }
        }

        BigDecimal totalAmount = commissionAmount.add(commissionVatAmount);

        Phrase commission = PdfUtil.getPhrase("Commission", tableHeadFont);
        Phrase vat = PdfUtil.getPhrase("VAT", tableHeadFont);
        Phrase total = PdfUtil.getPhrase("Total", tableHeadFont);

        PdfUtil.addRow(commissionTable, commission, PdfUtil.getPhrase(commissionAmount.setScale(2, BigDecimal.ROUND_DOWN)));
        PdfUtil.addRow(commissionTable, vat, PdfUtil.getPhrase(commissionVatAmount.setScale(2, BigDecimal.ROUND_DOWN)));
        PdfUtil.addRow(commissionTable, total, PdfUtil.getPhrase(totalAmount.setScale(2, BigDecimal.ROUND_DOWN)));

        for (PdfPRow row: commissionTable.getRows()) {
            for (PdfPCell cell: row.getCells()){
                cell.setBorder(0);
                cell.setBorderColorBottom(new BaseColor(0xFF9235));
                cell.setBorderWidthBottom(new Float(0.3));
                cell.setPaddingBottom(20);
                cell.setPaddingTop(20);
                cell.setPaddingLeft(50);
            }
        }

        document.add(commissionTable);
    }


    private void addBillBody(Document document, OrderEntity order, BillEntity bill, Map<String, String> preferences) throws Exception {
        //add invoice detail
        com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(FontFactory.getFont(FontFactory.TIMES_ROMAN, 19f));
        titleFont.setColor(new BaseColor(0xFF9235));
        Paragraph title = PdfUtil.getParagraph(titleFont, "Invoice : "+bill.getId());
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingBefore(10);
        document.add(title);
        PdfUtil.addEmptyLine(document, 1);//add empty line


        PdfPCell orderCell = new PdfPCell();
        PdfUtil.setPadding(orderCell, 20, 0, 20, 50);
        com.itextpdf.text.Font infoFont = new com.itextpdf.text.Font(FontFactory.getFont(FontFactory.TIMES_ROMAN, 13f));
        infoFont.setColor(new BaseColor(0x969696));
        Paragraph orderCellTitle = PdfUtil.getParagraph(titleFont,  "Order Detail");
        Paragraph orderInfo = PdfUtil.getParagraph(infoFont, true, "Order Id: "+order.getId(), "Order Date: "+order.getOrderDate(), "Invoice Date: "+bill.getGeneratedDate());
        orderCell.addElement(orderCellTitle);
        orderCell.addElement(orderInfo);



        PdfPCell addressCell = new PdfPCell();
        PdfUtil.setPadding(addressCell, 20, 0, 20, 0);
        String street = order.getAddress().getStreet();
        String city = order.getAddress().getCity();
        String state = order.getAddress().getState();
        String country = order.getAddress().getCountry();
        String mobile = order.getAddress().getMobileNumber();
        Paragraph addressCellTitle = PdfUtil.getParagraph(titleFont,  "Customer Address");
        Paragraph addressInfo = PdfUtil.getParagraph(infoFont, true, "Customer Address", order.getCustomer().getUser().getFullName(), street+", "+city, state+", "+country, "Phone: "+mobile);
        addressCellTitle.setAlignment(Element.ALIGN_RIGHT);
        addressInfo.setAlignment(Element.ALIGN_RIGHT);
        addressCell.addElement(addressCellTitle);
        addressCell.addElement(addressInfo);

        PdfUtil.setBorder(0, orderCell, addressCell);


        addressCell.setBorderColorBottom(new BaseColor(0xFF9235));
        addressCell.setBorderWidthBottom(new Float(0.3));

        orderCell.setBorderColorBottom(new BaseColor(0xFF9235));
        orderCell.setBorderWidthBottom(new Float(0.3));

        //add cells to table
        PdfPTable billTable = new PdfPTable(2);
        billTable.setWidthPercentage(100);

        billTable.addCell(orderCell);
        billTable.addCell(addressCell);


        //billTable.getDefaultCell().setBorder(0);

        document.add(billTable);

        PdfUtil.addEmptyLine(document, 2);//add empty line

        //add billing table
        PdfPTable billingTable = new PdfPTable(2);
        billingTable.setWidthPercentage(100);
        billingTable.setWidths(new float[]{45, 45});

        String currency = preferences.get("CURRENCY");
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase("Title", infoFont), PdfUtil.getPhrase("Amount ("+currency+")", infoFont));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase("Processing Fee", infoFont),
                PdfUtil.getPhrase(bill.getSystemServiceCharge().setScale(2, BigDecimal.ROUND_DOWN), infoFont));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase("Delivery Fee", infoFont),
                PdfUtil.getPhrase(bill.getDeliveryCharge().setScale(2, BigDecimal.ROUND_DOWN), infoFont));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase("Subtotal", infoFont),
                PdfUtil.getPhrase(bill.getDeliveryCharge().add(bill.getSystemServiceCharge()).setScale(2, BigDecimal.ROUND_DOWN), infoFont));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase("Vat", infoFont),
                PdfUtil.getPhrase(bill.getVat().setScale(2, BigDecimal.ROUND_DOWN), infoFont));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase("Grand Total", infoFont),
                PdfUtil.getPhrase(bill.getDeliveryCharge().add(bill.getSystemServiceCharge()).add(bill.getVat()).setScale(2, BigDecimal.ROUND_DOWN), infoFont));
        billingTable.getDefaultCell().setBorder(0);

        for (PdfPRow row: billingTable.getRows()) {
            for (PdfPCell cell: row.getCells()){
                   cell.setBorder(0);
                   cell.setBorderColorBottom(new BaseColor(0xFF9235));
                   cell.setBorderWidthBottom(new Float(0.3));
                   cell.setPaddingBottom(20);
                   cell.setPaddingTop(20);
                   cell.setPaddingLeft(50);
            }
        }

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

        String currency = preferences.get("CURRENCY");
        PdfPCell billAmountCell = new PdfPCell();
        Paragraph amount = PdfUtil.getParagraph(infoFont, true, "Total Amount Billed: "+currency+receipt.getReceiptAmount().setScale(2, BigDecimal.ROUND_DOWN));
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

    private File generatedDBoyPaymentPDF(List<OrderEntity> orders, DeliveryBoyEntity deliveryBoy, DBoyPaymentEntity dBoyPayment, String imageUrl, Map<String, String> preferences) throws Exception {
        FileOutputStream stream = null;
        File payStatementFile = null;

        try{

            Document document = new Document();
            /*Integer cntOrder = 1;*/

            payStatementFile = getPayStatementFile(deliveryBoy, "payStatement");
            stream = new FileOutputStream(payStatementFile);
            PdfWriter writer = PdfWriter.getInstance(document, stream);
            document.open();
            document.setMargins(20, 20, 20, 20);
            //add doc header
            addPdfHeader(document, imageUrl, preferences);

            addPayStatementBody(document, deliveryBoy, dBoyPayment,  orders, preferences);

            addFooter(document);

            document.close();


        }catch (Exception e) {
            log.error("Error occurred while generating coupon bill", e);

            if (stream != null) {
                stream.flush();
                stream.close();
            }

            if (payStatementFile != null) {
                payStatementFile.delete();
            }
            payStatementFile = null;
            throw e;
        }

        return payStatementFile;

    }

    private void addPayStatementBody(Document document, DeliveryBoyEntity deliveryBoy, DBoyPaymentEntity dBoyPayment, List<OrderEntity> orders, Map<String, String> preferences) throws Exception{

        com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(FontFactory.getFont(FontFactory.TIMES_ROMAN, 19f));
        titleFont.setColor(new BaseColor(0xFF9235));
        Paragraph title = PdfUtil.getParagraph(titleFont, "Pay Statement");
        Paragraph shopperName = PdfUtil.getParagraph(titleFont, deliveryBoy.getUser().getFullName());
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingBefore(20);
        title.setSpacingAfter(30);
        shopperName.setAlignment(Element.ALIGN_CENTER);
        shopperName.setSpacingBefore(50);
        document.add(shopperName);
        document.add(title);

        PdfPTable billingTable = new PdfPTable(4);
        billingTable.setWidthPercentage(100);
        billingTable.setWidths(new float[]{20, 30, 20, 30});

        String currency = preferences.get("CURRENCY");
        com.itextpdf.text.Font tableHeadFont = new com.itextpdf.text.Font(FontFactory.getFont(FontFactory.TIMES_ROMAN, 13f));
        tableHeadFont.setColor(new BaseColor(0xFF9235));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase("SN", tableHeadFont), PdfUtil.getPhrase("Date and Time", tableHeadFont), PdfUtil.getPhrase("Order No.", tableHeadFont), PdfUtil.getPhrase("Amount Earned ("+currency+")", tableHeadFont));

        BigDecimal totalAmountEarned = BigDecimal.ZERO;
        Integer cntOrder = 1;
        for (OrderEntity order:orders){

            PdfUtil.addRow(billingTable, PdfUtil.getPhrase(cntOrder), PdfUtil.getPhrase(order.getOrderDate()), PdfUtil.getPhrase(order.getId()), PdfUtil.getPhrase(order.getdBoyOrderHistories().get(0).getAmountEarned()));
            totalAmountEarned = totalAmountEarned.add(order.getdBoyOrderHistories().get(0).getAmountEarned());

            if(cntOrder%20==0){
                document.newPage();
            }
            cntOrder++;
        }

        PdfUtil.addRow(billingTable, PdfUtil.getPhrase(""), PdfUtil.getPhrase(""), PdfUtil.getPhrase("Total"), PdfUtil.getPhrase(totalAmountEarned.setScale(2, BigDecimal.ROUND_DOWN)));
        BigDecimal tdsAmount = totalAmountEarned.multiply(new BigDecimal(preferences.get("TDS_PERCENTAGE"))).divide(new BigDecimal(100));
        BigDecimal totalPayableAmount =  totalAmountEarned.subtract(tdsAmount);
        Phrase tdsPhrase;
        if(BigDecimalUtil.isIntegerValue(new BigDecimal(preferences.get("TDS_PERCENTAGE")))){
            tdsPhrase = PdfUtil.getPhrase("TDS Percentage ("+Integer.parseInt(preferences.get("TDS_PERCENTAGE"))+")");
        } else {
            tdsPhrase = PdfUtil.getPhrase("TDS Percentage ("+preferences.get("TDS_PERCENTAGE")+")");
        }
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase(""), PdfUtil.getPhrase(""), tdsPhrase, PdfUtil.getPhrase(tdsAmount.setScale(2, BigDecimal.ROUND_DOWN)));
        PdfUtil.addRow(billingTable, PdfUtil.getPhrase(""), PdfUtil.getPhrase(""), PdfUtil.getPhrase("Total Payable"), PdfUtil.getPhrase(totalPayableAmount.setScale(2, BigDecimal.ROUND_DOWN)));
        for (PdfPRow row: billingTable.getRows()) {
            for (PdfPCell cell: row.getCells()){
                cell.setBorder(0);
                cell.setBorderColorBottom(new BaseColor(0xFF9235));
                cell.setBorderWidthBottom(new Float(0.3));
                cell.setPaddingBottom(20);
                cell.setPaddingTop(20);
                cell.setPaddingLeft(50);
            }
        }

        document.add(billingTable);
    }

    private void addFooter(Document document) throws Exception {
        PdfUtil.addEmptyLine(document, 2);//add empty line

        document.add(PdfUtil.getParagraph(PdfUtil.smallFont, "If you have any questions, contact us at support@koolkat.in"));
    }


    private File getFile(MerchantEntity merchant, StoreEntity store, String name) {
        String dir = getInvoiceDir(merchant, store, File.separator);

        File invoiceDir = new File(HOME_DIR + File.separator + dir + File.separator);
        if (!invoiceDir.exists()) {
            invoiceDir.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" +name+ ".pdf";
        return new File(invoiceDir, fileName);
    }


    private File getPayStatementFile(DeliveryBoyEntity deliveryBoy, String name) {
        String dir = getDBoyPaymentDir(deliveryBoy, File.separator);

        File invoiceDir = new File(HOME_DIR + File.separator + dir + File.separator);
        if (!invoiceDir.exists()) {
            invoiceDir.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" +name+ ".pdf";
        return new File(invoiceDir, fileName);
    }

    private File getBillAndReceiptFile(MerchantEntity merchant, OrderEntity order, String name) {
        String dir = getBillAndReiceptDir(merchant, order, File.separator);

        File invoiceDir = new File(HOME_DIR + File.separator + dir + File.separator);
        if (!invoiceDir.exists()) {
            invoiceDir.mkdirs();
        }

        String fileName = System.currentTimeMillis()+ "_" +name+ ".pdf";
        return new File(invoiceDir, fileName);
    }

    private String getInvoiceDir(MerchantEntity merchant, StoreEntity store, String separator) {
        return MessageBundle.separateString(separator, "Invoices", "Merchant_" + merchant.getId(), "StoreBrand_"+store.getStoresBrand().getId(), "Store"+store.getId());
    }

    private String getDBoyPaymentDir(DeliveryBoyEntity deliveryBoy, String separator) {
        return MessageBundle.separateString(separator, "Shopper_payment", "Shopper_" + deliveryBoy.getId());
    }

    private String getBillAndReiceptDir(MerchantEntity merchant, OrderEntity order, String separator) {
        return MessageBundle.separateString(separator, "receipts", "Merchant_" + merchant.getId(), "Order_" + order.getId());
    }

}
