package com.yetistep.delivr.util;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;
import com.twilio.sdk.resource.list.UsageRecordList;
import com.yetistep.delivr.enums.PreferenceType;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 5/14/15
 * Time: 12:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class SMSUtil {

    public static void sendSMS(String text, String to, String countryCode, String smsProvider) throws Exception {
        if (smsProvider.equals("1")) {
            SparrowSMSUtil.sendSMS(text, to);
        } else {
            TwilioSMSUtil.sendSMS(text, to, countryCode);
        }
    }

}
