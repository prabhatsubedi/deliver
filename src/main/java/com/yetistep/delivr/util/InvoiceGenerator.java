package com.yetistep.delivr.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.mail.EmailException;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 2/20/15
 * Time: 11:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class InvoiceGenerator {

    private String generateInvoice() throws Exception {
       return new String();
    }

    private File generateInvoicePDF() throws Exception {
        FileOutputStream stream = null;
        File invoiceFile = null;



        return invoiceFile;
    }

    //create a header for the company
    private void addInvoiceHeader() throws Exception {

    }

    private void addInvoiceBody() throws Exception {

    }

    private void sendInvoicePaymentNReceiptMail() {

    }


    /**
     * ************ GENERATE INVOICE RECEIPT ***************
     */
    private String generateInvoiceReceipt() throws Exception {
        return new String();
    }

    private File generateReceiptPdf() throws Exception {
        FileOutputStream stream = null;
        File receiptFile = null;



        return receiptFile;
    }

    private void addReceiptHeader() throws Exception {

    }

    private void addReceiptBody() throws Exception {

    }

    private void addFooter(Document document, boolean addCompanyInfo) throws Exception {

    }

    private void sendReceiptEmail() throws EmailException {

    }

    private File getFile() {
        String dir = getInvoiceDir();

        File invoiceDir = new File(File.separator + dir + File.separator);
        if (!invoiceDir.exists()) {
            invoiceDir.mkdirs();
        }

        String fileName = DateUtil.getCurrentDate() + "_"  + ".pdf";
        return new File(invoiceDir, fileName);
    }

    private String getInvoiceDir() {
        return new String();
    }

}
