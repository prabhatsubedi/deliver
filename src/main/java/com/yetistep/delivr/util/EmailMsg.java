package com.yetistep.delivr.util;



import com.yetistep.delivr.model.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Surendra
 * Date: 10/27/13
 * Time: 10:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class EmailMsg {
    private static final String COMPANY_NAME = "Delivr";
    public static final String EMAIL_WELCOME_SUBJECT = "Welcome To Delivr";

    private static StringBuilder style = new StringBuilder();

    static {
        style.append("body{font-size: 16px; font-family: Roboto;}");
    }

    public static String createPasswordForNewUser(String url, String userName, String userEmail, String serverUrl) {
        StringBuilder body = new StringBuilder();

        body.append(getHtmlHeader(serverUrl));

        body.append("<div style='width: 100%;float: left;margin-bottom: 30px;background-color: white;border-radius: 15px;padding: 20px 0px;border:1px solid #E0E0E0;'>");
        body.append("<div style='padding: 20px; width:100%; height:90px;'><img src='"+serverUrl+"/resources/images/delivr-logo.png' width='90' height='64' alt='iDelivr Logo' style='float: left;'/> <div style='float: left;font-size: 40px;color: #AAAAAA;font-weight:bold;margin-left:20px;'>iDelivr</div></div>");
        body.append("<div style='min-height: 110px;line-height: 55px; background-color: #F58220;color: #ffffff;font-size: 36px;padding: 70px 40px;'>Please click on the link to verify your account and create your own password.</div>");

        body.append("<div style='font-size: 16px; padding: 30px;'>");
        body.append("<p>Dear <b>"+userName+", </b></p>");
        body.append("<p>Your account details are as follows:</p>");
        body.append("<p><b>Your username: </b>"+userEmail+"</p>");

        body.append("<p>Please click on the link to verify your account and create your own password: <a href='" + url + "'  style='padding: 10px 20px; color: #52B4F3; text-decoration: none; display: inline-block; margin: 10px 0px;font-size: 22px;'>Verify Your Account.</a></p>");
        body.append("<p>If the above link does not work, please copy & paste the following URL in your web browser: </p>");
        body.append("<p>"+url+"</p>");

        body.append("<p>Your account will be activated after verification.</p>");

        body.append("<div style='width=100%; float: left; margin-top: 30px;'>");
        body.append("<p>Sincerely,</p>");
        body.append("<p>iDelivr Team</p>");
        body.append("</div>");
        body.append("</div>");

        return prepareEmail(body.toString(), null, serverUrl );
    }

    public static String resetForgotPassword(String url, String userName, String serverUrl) {
        StringBuilder body = new StringBuilder();

        body.append(getHtmlHeader(serverUrl));

        body.append("<div style='width: 100%;float: left;margin-bottom: 30px;background-color: white;border-radius: 15px;padding: 20px 0px;border:1px solid #E0E0E0;'>");
        body.append("<div style='padding: 20px; width:100%; height:90px;'><img src='"+serverUrl+"/resources/images/delivr-logo.png' width='90' height='64' alt='iDelivr Logo' style='float: left;'/> <div style='float: left;font-size: 40px;color: #AAAAAA;font-weight:bold;margin-left:20px;'>iDelivr</div></div>");
        body.append("<div style='min-height: 110px;line-height: 55px; background-color: #F58220;color: #ffffff;font-size: 36px;padding: 70px 40px;'>We're sorry you are having difficulty logging in.</div>");

        body.append("<div style='font-size: 16px; padding: 30px;'>");
        body.append("<p>Dear <b>"+userName+", </b></p>");
        body.append("<p>Please click on the link below and navigate to reset your new password. </p>");
        body.append(getEmailBodyButton("Reset Password", url));
        body.append("<p>If the link does not work,please copy & paste the following URL in your web browser:</p>");
        body.append("<p>"+url+"</p>");

        body.append("<div style='width=100%; float: left; margin-top: 30px;'>");
        body.append("<p>Sincerely,</p>");
        body.append("<p>iDelivr Team</p>");
        body.append("</div>");
        body.append("</div>");

        return prepareEmail(body.toString(), null, serverUrl);
    }

    public static String activateMerchant(String url, String contactPerson, String serverUrl) {
        StringBuilder body = new StringBuilder();
        body.append(getHtmlHeader(serverUrl));
        body.append("<div style='width: 100%;float: left;margin-bottom: 30px;background-color: white;border-radius: 15px;padding: 20px 0px;border:1px solid #E0E0E0;'>");
        body.append("<div style='padding: 20px; width:100%; height:90px;'><img src='"+serverUrl+"/resources/images/delivr-logo.png' width='90' height='64' alt='iDelivr Logo' style='float: left;'/> <div style='float: left;font-size: 40px;color: #AAAAAA;font-weight:bold;margin-left:20px;'>iDelivr</div></div>");
        body.append("<div style='min-height: 110px;line-height: 55px; background-color: #F58220;color: #ffffff;font-size: 36px;padding: 70px 40px;'>Your account has been approved & activated.</div>");

        body.append("<div style='font-size: 16px; padding: 30px;'>");
        body.append("<p>Dear <b>"+contactPerson+"</b></p>");
        body.append("<p>You can now start uploading your stores, product and services.</p>");

        body.append(getEmailBodyButton("Login to Delivr", url));

        body.append("<div style='width=100%; float: left; margin-top: 30px;'>");
        body.append("<p>Sincerely,</p>");
        body.append("<p>iDelivr Team</p>");
        body.append("</div>");
        body.append("</div>");
        return prepareEmail(body.toString(), null, serverUrl);
    }


    public static String deactivateMerchant(String contactPerson, String serverUrl) {
        StringBuilder body = new StringBuilder();
        body.append(getHtmlHeader(serverUrl));
        body.append("<div style='width: 100%;float: left;margin-bottom: 30px;background-color: white;border-radius: 15px;padding: 20px 0px;border:1px solid #E0E0E0;'>");
        body.append("<div style='padding: 20px; width:100%; height:90px;'><img src='"+serverUrl+"/resources/images/delivr-logo.png' width='90' height='64' alt='iDelivr Logo' style='float: left;'/> <div style='float: left;font-size: 40px;color: #AAAAAA;font-weight:bold;margin-left:20px;'>iDelivr</div></div>");
        body.append("<div style='min-height: 110px;line-height: 55px; background-color: #F58220;color: #ffffff;font-size: 36px;padding: 70px 40px;'>We have deactivated your account as requested.</div>");

        body.append("<div style='font-size: 16px; padding: 30px;'>");
        body.append("<p>Dear <b>"+contactPerson+"</b></p>");
        body.append("<p style='font-weight:bold'>We look forward to seeing you back on iDelivr soon.</p>");

        body.append("<div style='width=100%; float: left; margin-top: 30px;'>");
        body.append("<p>Sincerely,</p>");
        body.append("<p>iDelivr Team</p>");
        body.append("</div>");
        body.append("</div>");
        return prepareEmail(body.toString(), null, serverUrl);
    }

    public static String orderPlaced(String contactPerson, String serverUrl, OrderEntity order) {
        StringBuilder body = new StringBuilder();
        body.append(getHtmlHeader(serverUrl));
        body.append("<div style='width: 100%;float: left;margin-bottom: 30px;background-color: white;border-radius: 15px;padding: 20px 0px;border:1px solid #E0E0E0;'>");
        body.append("<div style='padding: 20px; width:100%; height:90px;'><img src='"+serverUrl+"/resources/images/delivr-logo.png' width='90' height='64' alt='iDelivr Logo' style='float: left;'/> <div style='float: left;font-size: 40px;color: #AAAAAA;font-weight:bold;margin-left:20px;'>iDelivr</div></div>");
        body.append("<div style='min-height: 110px;line-height: 55px; background-color: #F58220;color: #ffffff;font-size: 36px;padding: 70px 40px;'>New order has been placed for your store: Order no - "+order.getId()+"</div>");

        body.append("<div style='font-size: 16px; padding: 30px;'>");
        body.append("<p>Dear <b>"+contactPerson+"</b></p>");
        body.append("<p style='font-weight:bold'>New order is placed as below and "+order.getDeliveryBoy().getUser().getFullName()+" is in route to pick the item.</p>");
        body.append("<p style='font-weight:bold'>Please prepare the item for delivery.</p>");
        body.append("<table>");

        Integer i = 1;
        for (ItemsOrderEntity itemsOrder: order.getItemsOrder()) {
          ItemEntity item = itemsOrder.getItem();
            body.append("<tr>");
                body.append("<td>"+i+"</td>");
                body.append("<td><p>"+item.getName()+"</p>");
                    for (ItemsOrderAttributeEntity itemsOrderAttribute: itemsOrder.getItemOrderAttributes()){
                          body.append("<p>"+itemsOrderAttribute.getItemsAttribute().getType().getType()+":"+itemsOrderAttribute.getItemsAttribute().getAttribute()+"</p>");
                    }

                body.append("</td>");
                body.append("<td>"+itemsOrder.getQuantity()+"</td>");
                body.append("<td>"+item.getUnitPrice()+"</td>");
            body.append("</tr>");
            i++;
        }
        body.append("</table>");

        body.append("<div style='width=100%; float: left; margin-top: 30px;'>");
        body.append("<p>Sincerely,</p>");
        body.append("<p>iDelivr Team</p>");
        body.append("</div>");
        body.append("</div>");
        return prepareEmail(body.toString(), null, serverUrl);
    }

    private static String getEmailBodyButton(String viewLink, String url) {
        StringBuilder builder = new StringBuilder();
        builder.append("<p><a href='" + url + "'  style='padding: 10px 20px; color: #52B4F3; text-decoration: none; display: inline-block; margin: 10px 0px;'>" + viewLink + "</a></p>");
        return builder.toString();
    }

    private static String getEmailFooter() {
        StringBuilder builder = new StringBuilder();
        builder.append("<p style='font-weight:bold; font-size:22px; text-align: center;'>Delivr is available for</p>");
        builder.append("<div style='width:47%; margin:0 auto; align:center; padding: 30px 0;'><a href='#' style='text-decoration: none; float: left; color: #999999; font-weight:bold; text-align:center;'><img src='https://myswiprtests3.s3.amazonaws.com/email_icons/iphone_final.png' style='padding: 10px 20px;display:block;'>i-phone</a>\n" +
               "<a href='https://play.google.com/store/apps/details?id=com.yetistep.dealify' style='text-decoration: none; float: left; color: #999999; font-weight:bold; text-align:center;'><img src='https://myswiprtests3.s3.amazonaws.com/email_icons/android_final.png' style='padding: 10px 20px;display:block;'>Android</a>\n"+
               "<a href='#' style='text-decoration: none;  float: left; color: #999999; font-weight:bold; text-align:center;'><img src='https://myswiprtests3.s3.amazonaws.com/email_icons/desktop_final.png' style='padding: 10px 20px;display:block;'>Desktop</a></div>");
        return builder.toString();
    }

    private static String getHtmlHeader(String serverUrl) {
        String htmlHead = "<html><head><title>Welcome to " + COMPANY_NAME + "</title>" +
                "<style>" + style.toString() + "</style>" + "<link rel='stylesheet' " + "href='" + serverUrl + "/resources/css/fonts/font.css'>" +
                "<link rel='stylesheet' " + "href='" + serverUrl + "/resources/css/email.css'>" +
                "</head><body>";
        return htmlHead;
    }

    private static String getEmailHead(String userHeaderContent) {
        StringBuilder builder = new StringBuilder();
        builder.append("<tr>");
        //builder.append("<td><img src='" + CLOUD_FRONT + "/DealifyLogo/logo.png' width='153' height='64' alt='Dealify Logo' style='padding:10px 10px;'/></td>");
        if (userHeaderContent != null) {
            builder.append("<td>" + userHeaderContent + "</td>");
        }
        builder.append("</tr>");
        return builder.toString();
    }

    private static String getEmailBody(String bodyContent) {
        StringBuilder builder = new StringBuilder();
        builder.append("<tr style='background:#F5F5F5;color:#626262'>");
        builder.append("<td>");
        builder.append(bodyContent);
        builder.append("</td>");
        builder.append("</tr>");
        return builder.toString();
    }

    private static String getEmailTableFooter() {
        StringBuilder footer = new StringBuilder();

        //footer.append("<tr style='background:#F5F5F5'>");
        //footer.append("<td>");
        //footer.append("Sincerely,<br />");

//        String compName = SystemPropertyManager.readPrefValue(PreferenceType.COMPANY_NAME);
//        String address = SystemPropertyManager.readPrefValue(PreferenceType.COMPANY_ADDRESS);
//        String contact = SystemPropertyManager.readPrefValue(PreferenceType.COMPANY_CONTACT_NO);
//        String website = SystemPropertyManager.readPrefValue(PreferenceType.COMPANY_WEBSITE);
//        String vatNo = SystemPropertyManager.readPrefValue(PreferenceType.COMPANY_VAT_NO);

        //footer.append(COMPANY_NAME + " Team " + "<br/><br />");
        //footer.append("</td>");
        //footer.append("</tr>");
        return footer.toString();
    }

    private static String getLinkFooter() {
        StringBuilder builder = new StringBuilder();
        builder.append("<div style='margin: 0 auto; width: 100%; text-align: center;'>");
        builder.append("<a href='#'>Privacy Policy</a>");
        builder.append("</div>");
        return builder.toString();
    }

    private static String getHtmlClosing() {
        StringBuilder builder = new StringBuilder();
        builder.append("</body>");
        builder.append("</html>");
        return builder.toString();
    }

    public static String prepareEmail(String bodyContent, String userHeadContent, String serverUrl) {
        StringBuilder builder = new StringBuilder();
        builder.append(getHtmlHeader(serverUrl));
        builder.append("<div style='background:#EFEFEF;width:900px;padding:40px;'>");
        builder.append("<div style='color:#626262;margin:0 auto;width:700px;'>");
        builder.append(getEmailHead(userHeadContent));
        // builder.append(getEmailSubject(subjectContent));
        builder.append(getEmailBody(bodyContent));
        builder.append(getEmailTableFooter());
        builder.append("</div>");
        builder.append("<div style='width: 100%;float: left;margin-bottom: 30px;background-color:white;border-radius: 15px;padding:20px 0px;width:700px;border:1px solid #E0E0E0;'>");
        builder.append(getEmailFooter());
        builder.append("</div>");
        builder.append(getLinkFooter());
        builder.append("</div>");
        builder.append(getHtmlClosing());
        return builder.toString();
    }

}
