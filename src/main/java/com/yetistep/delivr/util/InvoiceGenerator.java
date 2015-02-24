package com.yetistep.delivr.util;


import com.itextpdf.text.*;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.yetistep.delivr.enums.PreferenceType;
import com.yetistep.delivr.model.InvoiceEntity;
import com.yetistep.delivr.model.MerchantEntity;
import com.yetistep.delivr.model.OrderEntity;
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

        //send emails
        //sendInvoicePaymentNReceiptMail(coupon, merchant, ytInvoice, adminStripeId, invoicePath);
        return invoicePath;
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

    private String getInvoiceDir(MerchantEntity merchant, String separator) {
        String dir = MessageBundle.separateString(separator, "Invoices", "Merchant_" + merchant.getId());

        return dir;
    }

}
