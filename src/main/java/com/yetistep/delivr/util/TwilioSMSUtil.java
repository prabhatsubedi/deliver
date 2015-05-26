package com.yetistep.delivr.util;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;
import com.twilio.sdk.resource.instance.UsageRecord;
import com.twilio.sdk.resource.list.UsageRecordList;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 5/14/15
 * Time: 12:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class TwilioSMSUtil {
    // Find your Account Sid and Token at twilio.com/user/account
    public static final String ACCOUNT_SID = MessageBundle.getTwilioSMSSid();
    public static final String AUTH_TOKEN = MessageBundle.getTwilioSMSToken();


    private static final Logger log = Logger.getLogger(TwilioSMSUtil.class);
    private static final TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);


    public static Message sendSMS(String text, String to, String countryCode) {
        to = countryCode+to;
        log.info("++++++ Sending SMS to " + to + " +++++++++++");

        Message message;

        try{
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("Body", text));
            params.add(new BasicNameValuePair("To", to));
            params.add(new BasicNameValuePair("From", MessageBundle.getTwilioSMSFrom()));

            MessageFactory messageFactory = client.getAccount().getMessageFactory();
            message = messageFactory.create(params);
            log.info(message.getSid());
        } catch (TwilioRestException e) {
            log.info(e.getErrorMessage());
            throw new RuntimeException(e.getErrorMessage());
        } catch (NoSuchMethodError e) {
            log.info( e.getMessage() );
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            throw new YSException("SEC013");
        }

        log.info("++++++ SMS message " + message + " +++++++++++");
        return message;
    }



    public static UsageRecordList getUsageRecords(){
        return client.getAccount().getUsageRecords();
    }
}
