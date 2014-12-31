package com.yetistep.delivr.util;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/30/14
 * Time: 2:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class Validator {
    public static boolean validateString(String value, String type) {
        if (value==null || value.isEmpty()) {
            throw new YSException("VLD003", type);
        }
        return true;
    }
}
