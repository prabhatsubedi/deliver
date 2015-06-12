package com.yetistep.delivr.util;

import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 5/14/15
 * Time: 12:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class SMSUtil {
    private static final Logger log = Logger.getLogger(SMSUtil.class);

    public static void sendSMS(String text, String to, String countryCode, String smsProvider) {
        try {
            if (smsProvider.equals("1")) {
                SparrowSMSUtil.sendSMS(text, to);
            } else {
                TwilioSMSUtil.sendSMS(text, to, countryCode);
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

}
