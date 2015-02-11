package com.yetistep.delivr.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

/**
 * Created with IntelliJ IDEA.
 * User: yetistep
 * Date: 11/7/13
 * Time: 4:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class PdfUtil {
    public static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
    public static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.RED);
    public static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
    public static Font largeBold = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
    public static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
    public static Font smallFont = new Font(Font.FontFamily.TIMES_ROMAN, 10);

    public static void addEmptyLine(Document document, int number) throws Exception {
        Paragraph paragraph = new Paragraph();
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }

        document.add(paragraph);
    }

    public static void addRow(PdfPTable table, Phrase... cellContent) {
        addRow(table, true, cellContent);
    }


    public static void addRow(PdfPTable table, boolean border, Phrase... cellContent){
        for(Phrase content: cellContent){
            table.addCell(getCell(content, border));
        }
    }

    public static void addTableHead(PdfPTable table, String... header){
        for(String head: header){
            Phrase phrase   =   new Phrase(head,smallBold);

            PdfPCell headCell = new PdfPCell(phrase);
            headCell.setPadding(8);
            headCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headCell.setBackgroundColor(BaseColor.LIGHT_GRAY);

            table.addCell(headCell);
        }

    }


    public static PdfPCell getCell(Paragraph paragraph, boolean border){
        PdfPCell pdfPCell = new PdfPCell(paragraph);
        pdfPCell.setPadding(6);

        if(!border) pdfPCell.setBorder(0);

        return pdfPCell;
    }


    public static void setBorder(int border, PdfPCell... cells){
        for(PdfPCell cell: cells)
            cell.setBorder(border);
    }

    public static void setPadding(PdfPCell cell, int top, int right, int bottom, int left) {
        cell.setPaddingTop(top);
        cell.setPaddingRight(right);
        cell.setPaddingBottom(bottom);
        cell.setPaddingLeft(left);
    }

    public static PdfPCell getEmptyCell(boolean border){
        PdfPCell pdfPCell = new PdfPCell();

        if(!border) pdfPCell.setBorder(0);

        return pdfPCell;
    }


    public static Phrase getPhrase(Object text) {
        return getPhrase(text, smallFont);
    }


    public static Phrase getBoldPhrase(Object text) {
        return getPhrase(text, smallBold);
    }

    public static Phrase getPhrase(Object text, Font font) {
        return new Phrase(text.toString(), font);
    }

    public static Paragraph getParagraph(Font font, String... args) {
        return getParagraph(font, false, args);
    }

    public static Paragraph getParagraph(Font font, boolean lineBreak, String... args) {
        Paragraph paragraph = new Paragraph();

        for(String arg: args) {
            paragraph.add(new Phrase(arg+(lineBreak? "\n":""), font));
        }

        return paragraph;
    }

    private static PdfPCell getCell(Phrase phrase, boolean border){
        PdfPCell pdfPCell = new PdfPCell(phrase);
        pdfPCell.setPadding(6);

        if(!border) pdfPCell.setBorder(0);


        return pdfPCell;
    }

    public static Font getFont(int size) {
        return new Font(Font.FontFamily.TIMES_ROMAN, size);
    }
}
