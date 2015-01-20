package com.yetistep.delivr.util;



import com.yetistep.delivr.model.MerchantEntity;

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

    /*
    * From: <Admin Email Address>
To: <Merchant Email Address>

Subject: Welcome to iDelivr

Dear <Merchant Contact Person Name>,


Welcome & thank you for joining iDelivr.

Your account details are as follows:
Your username: <Merchant Email Address>

Please click on the link to verify your account and create your own password: [Verify My Account]

If the above link does not work, please copy & paste the following URL in your web browser:
http://54.254.205.135/verify_account.jsp?code=ff119733-a429-443c-86c8-29f7e2606a63_1399083532794

Your account will be activated after verification.


Sincerely,

iDelivr Team
<Tagline>
    * */

    public static String createPasswordForNewUser(String url, String userName, String userEmail, String subject, String serverUrl) {
        StringBuilder body = new StringBuilder();

        body.append(getHtmlHeader(serverUrl));
        body.append("<div style='padding: 20px;'><img src='"+serverUrl+"/resources/images/delivr-logo.png' width='153' height='64' alt='iDelivr Logo' style='float: left;'/> <div style='float: left;font-size: 32px;color: #AAAAAA;'>iDelivr</div></div>");
        body.append("<div style='height: 180px;line-height: 80px;background-color: #F58220;color: #ffffff;font-size: 24px;'>Welcome & thank you for joining iDelivr.</div>");

        body.append("Your account details are as follows:<br/>");
        body.append("Your username: "+userEmail+"<br/><br/>");

        body.append("Please click on the link to verify your account and create your own password: <br/>");
        body.append("<a href='" + url + "'  style='background:#52B4F3; padding: 10px 20px; color: #FFFFFF; text-decoration: none; display: inline-block;; border-radius: 3px; margin: 10px 0px;'>" + url + "</a>");
        body.append("If the above link does not work, please copy & paste the following URL in your web browser: <br/>");
        body.append(url);
        body.append("<br/><br/>");

        body.append("Your account will be activated after verification.<br/><br/><br/><br/>");

        body.append("Sincerely,<br/><br/><br/>");
        body.append("iDelivr Team");

        return prepareEmail(subject, body.toString(), null, serverUrl );
    }

    public static String resetForgotPassword(String url, String userName, String subject, String serverUrl) {
        StringBuilder body = new StringBuilder();

        body.append("Dear " + userName + ",<br/><br/>");
        body.append("We're sorry you are having difficulty logging in.Click on the link below to reset your password.<br/><br/>");

        body.append("Please click on the link below and navigate to reset your new password. <br/>");
        body.append(getEmailBodyButton("Reset Password", url));
        body.append("If the link does not work,please copy & paste the following URL in your web browser:<br/>");
        body.append(url);

        body.append("<br/><br/>");

        return prepareEmail(subject, body.toString(), null, serverUrl);
    }

    public static String activateMerchant(String url, String contactPerson, String subject, String serverUrl) {
        StringBuilder body = new StringBuilder();
        body.append(getHtmlHeader(serverUrl));
        body.append("Dear  " + contactPerson + ",<br/><br/>");
        body.append("Your account has been approved & activated. You can access your dashboard." + "<br/><br/>");

        body.append(getEmailBodyButton("Login to Delivr", url));
        body.append("<br/>");
        return prepareEmail(subject, body.toString(), null, serverUrl);
    }


    private static String getEmailBodyButton(String viewLink, String url) {
        StringBuilder builder = new StringBuilder();
        builder.append("<a href='" + url + "'  style='background:#52B4F3; padding: 10px 20px; color: #FFFFFF; text-decoration: none; display: inline-block;; border-radius: 3px; margin: 10px 0px;'>" + viewLink + "</a>");
        builder.append("<br clear='all'/>");
        return builder.toString();
    }

    private static String getEmailFooter() {
        StringBuilder builder = new StringBuilder();
        builder.append("Delivr is available for");
        builder.append("<div style='width:47%; margin:0 auto; align:center;'><a href='#' style='text-decoration: none; float: left; color: #999999;'><img src='https://myswiprtests3.s3.amazonaws.com/email_icons/iphone_final.png' style='padding: 10px 20px;display:block;'>i-phone</a>\n" +
               "<a href='https://play.google.com/store/apps/details?id=com.yetistep.dealify' style='text-decoration: none; float: left; color: #999999;'><img src='https://myswiprtests3.s3.amazonaws.com/email_icons/android_final.png' style='padding: 10px 20px;display:block;'>Android</a>\n"+
               "<a href='#' style='text-decoration: none;  float: left; color: #999999;'><img src='https://myswiprtests3.s3.amazonaws.com/email_icons/desktop_final.png' style='padding: 10px 20px;display:block;'>Desktop</a></div>");
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

        footer.append("<tr style='background:#F5F5F5'>");
        footer.append("<td>");
        footer.append("Sincerely,<br />");

//        String compName = SystemPropertyManager.readPrefValue(PreferenceType.COMPANY_NAME);
//        String address = SystemPropertyManager.readPrefValue(PreferenceType.COMPANY_ADDRESS);
//        String contact = SystemPropertyManager.readPrefValue(PreferenceType.COMPANY_CONTACT_NO);
//        String website = SystemPropertyManager.readPrefValue(PreferenceType.COMPANY_WEBSITE);
//        String vatNo = SystemPropertyManager.readPrefValue(PreferenceType.COMPANY_VAT_NO);

        footer.append(COMPANY_NAME + " Team " + "<br/><br />");
        footer.append("</td>");
        footer.append("</tr>");
        return footer.toString();
    }

    private static String getLinkFooter() {
        StringBuilder builder = new StringBuilder();
        builder.append("<tr align='center'>");
        builder.append("<td><a href='#'>Privacy Policy</a></td>");
        builder.append("</tr>");
        return builder.toString();
    }

    private static String getHtmlClosing() {
        StringBuilder builder = new StringBuilder();
        builder.append("</body>");
        builder.append("</html>");
        return builder.toString();
    }

    public static String prepareEmail(String subjectContent, String bodyContent, String userHeadContent, String serverUrl) {
        StringBuilder builder = new StringBuilder();
        builder.append(getHtmlHeader(serverUrl));
        builder.append("<table width='900px;' align='center' border='0' style='background:#DCDCDC' cellspacing='20px' cellpadding='10px'><tr><td>");
        builder.append("<table width='700' border='0' cellspacing='0px' cellpadding='30' align='center' style='background:white;border-radius:15px;color:#626262'>");
        builder.append(getEmailHead(userHeadContent));
        // builder.append(getEmailSubject(subjectContent));
        builder.append(getEmailBody(bodyContent));
        builder.append(getEmailTableFooter());
        builder.append("</table></td></tr>");
        builder.append("<tr><td>");
        builder.append(getEmailFooter());
        builder.append("</td></tr>");
        builder.append(getLinkFooter());
        builder.append("</table>");
        builder.append(getHtmlClosing());
        return builder.toString();
    }

}
