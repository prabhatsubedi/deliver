package com.yetistep.delivr.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;
import com.twilio.sdk.resource.list.MessageList;
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
    public static final String ACCOUNT_SID = "AC32a3c49700934481addd5ce1659f04d2";
    public static final String AUTH_TOKEN = "{{ auth_token }}";


    private static final Logger log = Logger.getLogger(TwilioSMSUtil.class);


    public static Message sendSMS(String text, String to) throws Exception {
        log.info("++++++ Sending SMS to " + to + " +++++++++++");
        TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
        Message message;
        try{
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("Body", text));
            params.add(new BasicNameValuePair("To", to));
            params.add(new BasicNameValuePair("From", MessageBundle.getSMSFrom()));

            MessageFactory messageFactory = client.getAccount().getMessageFactory();
            message = messageFactory.create(params);
        } catch (TwilioRestException e) {
            log.info(e.getErrorMessage());
            throw new RuntimeException(e.getErrorMessage());
        } catch (Exception e) {
            throw new YSException("SEC013");
        }

        return message;
    }

}
