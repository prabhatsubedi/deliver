package com.yetistep.delivr.util;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.PayloadBuilder;
import com.yetistep.delivr.enums.NotifyTo;
import com.yetistep.delivr.enums.PushNotificationRedirect;
import com.yetistep.delivr.model.UserDeviceEntity;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.util.Collections;

/**
* Created with IntelliJ IDEA.
* User: Chandra Prakash Panday
* Date: 1/30/15
* Time: 5:12 PM
* To change this template use File | Settings | File Templates.
*/
public class PushNotificationUtil {
    private static final Logger log = Logger.getLogger(PushNotificationUtil.class);

    private static final String DBOY_API_KEY = "AIzaSyBp36qfwThBp57gVjC0dOA4qf3vTv3gHCA";
    private static final String CUSTOMER_API_KEY = "AIzaSyCXcn_iKKSCnBnYbrinDFCmTdt6xyPcbTI";
    public static final String CERTIFICATE_IOS = "iphone_dev.p12";
    public static final String PASSWORD_IOS = "password123";


    public static void sendPushNotification(UserDeviceEntity userDevice, String message, NotifyTo notifyTo, PushNotificationRedirect pushNotificationRedirect, String extraDetail) {
        if (userDevice != null) {
            if(userDevice.getFamily() != null && userDevice.getDeviceToken() != null) {
                PushNotification pushNotification = new PushNotification();
                pushNotification.setTokens(Collections.singletonList(userDevice.getDeviceToken()));
                pushNotification.setMessage(message);
                pushNotification.setNotifyTo(notifyTo);
                pushNotification.setPushNotificationRedirect(pushNotificationRedirect);
                pushNotification.setExtraDetail(extraDetail);
                PushNotificationUtil.sendNotification(pushNotification, userDevice.getFamily());
            }
        }else{
            log.warn("Missing information of user device");
        }
    }

    public static void sendNotification(PushNotification pushNotification, String deviceFamily) {
        if (deviceFamily.equalsIgnoreCase("IOS") || deviceFamily.equalsIgnoreCase("MAC_OS_X"))//send to ios
            sendNotificationToiOSDevice(pushNotification);
        else//send to android
            sendNotificationToAndroidDevice(pushNotification);
    }

    public static void sendNotificationToAndroidDevice(PushNotification pushNotification) {
        log.info("Sending push notification to Android Device:" + pushNotification.getMessage() + ": " + pushNotification.getTokens());
        try {
            if(pushNotification.getTokens().size()  > 0){
                Sender sender = null;
                int retries = 3;
                if(pushNotification.getNotifyTo().equals(NotifyTo.DELIVERY_BOY)){
                    sender = new Sender(DBOY_API_KEY);
                }else if(pushNotification.getNotifyTo().equals(NotifyTo.CUSTOMER) || pushNotification.getNotifyTo().equals(NotifyTo.CUSTOMER_ANDROID)){
                    sender = new Sender(CUSTOMER_API_KEY);
                }

                String msg =pushNotification.getMessage();

                if(pushNotification.getPushNotificationRedirect() != null){
                    msg += pushNotification.getPushNotificationRedirect().toString();
                }
                if (pushNotification.getExtraDetail() != null)
                    msg += "/" + pushNotification.getExtraDetail();

                Message message = new Message.Builder()
                        .collapseKey("1")
                        .timeToLive(3)
                        .delayWhileIdle(false)
                        .addData("message", msg)
                        .build();
                MulticastResult result = sender.send(message, pushNotification.getTokens(), retries);


                log.info("Message:"+msg+" Result:"+result.toString());
                if (result.getResults() != null) {
                    int canonicalRegId = result.getCanonicalIds();
                    if (canonicalRegId != 0) {
                    }
                } else {
                    int error = result.getFailure();
                    log.error("failed push notification ids" + error);
                }
            }
        } catch (Exception e) {
            log.error("Error occurred while sending push notification to android device ", e);
        }
    }

    private static void sendNotificationToiOSDevice(PushNotification pushNotification) {
        try {
            log.info("Sending push notification to iOS Device");
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            PushNotificationRedirect pushNotificationRedirect = pushNotification.getPushNotificationRedirect();

            String message = pushNotification.getMessage();

            String customMessage = "";
            if(pushNotification.getPushNotificationRedirect() != null){
                customMessage += pushNotification.getPushNotificationRedirect().toString();
            }
            if (pushNotification.getExtraDetail() != null)
                customMessage += "/" + pushNotification.getExtraDetail();

            InputStream inputStream = classLoader.getResourceAsStream(CERTIFICATE_IOS);
            PayloadBuilder payloadBuilder = APNS.newPayload();
           /* payloadBuilder = payloadBuilder.badge(pushNotification.getBadge());*/
            payloadBuilder = payloadBuilder.sound("default");
            payloadBuilder = payloadBuilder.alertBody(message);
            payloadBuilder = payloadBuilder.customField("customMessage",customMessage);
            String payload = payloadBuilder.build();

            ApnsService service = APNS.newService().withCert(inputStream, PASSWORD_IOS).withSandboxDestination().build();
            service.push(pushNotification.getTokens(), payload);
            log.info("Notification sent successfully");
        } catch (Exception e) {
            log.error("Error occurred while sending push notification to ios device ", e);
        }
    }

}
