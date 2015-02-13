package com.yetistep.delivr.util;

import com.yetistep.delivr.enums.NotifyTo;
import com.yetistep.delivr.enums.PushNotificationRedirect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 2/2/15
 * Time: 10:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class PushNotification {
    private String message;
    private Integer badge;
    private String sound;
    private List<String> tokens;
    private String extraDetail;//is attached after the redirect string only when required
    private PushNotificationRedirect pushNotificationRedirect = PushNotificationRedirect.DELIVR;
    private NotifyTo notifyTo;

    public PushNotification(){}

    public PushNotification(String message){
        this.message = message;
        this.badge = 1;
        this.sound = "default";
        this.tokens = new ArrayList<String>();
    }

    public PushNotification(String message, String token) {
        this(message);
        tokens.add(token);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getBadge() {
        return badge;
    }

    public void setBadge(Integer badge) {
        this.badge = badge;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }

    public String getExtraDetail() {
        return extraDetail;
    }

    public void setExtraDetail(String extraDetail) {
        this.extraDetail = extraDetail;
    }

    public PushNotificationRedirect getPushNotificationRedirect() {
        return pushNotificationRedirect;
    }

    public void setPushNotificationRedirect(PushNotificationRedirect pushNotificationRedirect) {
        this.pushNotificationRedirect = pushNotificationRedirect;
    }

    public NotifyTo getNotifyTo() {
        return notifyTo;
    }

    public void setNotifyTo(NotifyTo notifyTo) {
        this.notifyTo = notifyTo;
    }
}
