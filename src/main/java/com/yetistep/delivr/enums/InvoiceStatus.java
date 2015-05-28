package com.yetistep.delivr.enums;


import com.yetistep.delivr.util.YSException;

/**
 * Created with IntelliJ IDEA.
 * User: yetistep
 * Date: 11/10/13
 * Time: 3:21 PM
 * To change this template use File | Settings | File Templates.
 */
public enum InvoiceStatus {
    UNPAID, PAID, PARTIALLY_PAID;

    public static InvoiceStatus fromString(String status) {
        try {
            return InvoiceStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new YSException("INV001");
        }
    }
}
