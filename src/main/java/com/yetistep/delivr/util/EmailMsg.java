package com.yetistep.delivr.util;



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


    public static String createPasswordForNewUser(String url, String userName,String userEmail, String subject) {
        StringBuilder body = new StringBuilder();

        body.append("Dear " + userName + ",<br/><br/>");

        body.append("Your user name : " + userEmail + "<br/><br/>");

        body.append("Click on the link below to set your password. <br/>");
        body.append("<a href='" + url + "'  style='background:#52B4F3; padding: 10px 20px; color: #FFFFFF; text-decoration: none; display: inline-block;; border-radius: 3px; margin: 10px 0px;'>" + url + "</a>");
        body.append("If the link does not work,please copy & paste the following URL in your browser:<br/>");
        body.append(url);
        body.append("<br/><br/>");

        return prepareEmail(subject, body.toString(), null);
    }

    public static String resetForgotPassword(String url, String userName, String subject) {
        StringBuilder body = new StringBuilder();

        body.append("Dear " + userName + ",<br/><br/>");
        body.append("We're sorry you are having difficulty logging in.Click on the link below to reset your password.<br/><br/>");

        body.append("Please click on the link below and navigate to reset your new password. <br/>");
        body.append(getEmailBodyButton("Reset Password", url));
        body.append("If the link does not work,please copy & paste the following URL in your web browser:<br/>");
        body.append(url);

        body.append("<br/><br/>");

        return prepareEmail(subject, body.toString(), null);
    }

    private static String getEmailBodyButton(String viewLink, String url) {
        StringBuilder builder = new StringBuilder();
        builder.append("<a href='" + url + "'  style='background:#52B4F3; padding: 10px 20px; color: #FFFFFF; text-decoration: none; display: inline-block;; border-radius: 3px; margin: 10px 0px;'>" + viewLink + "</a>");
        builder.append("<br clear='all'/>");
        return builder.toString();
    }

    private static String getEmailFooter() {
        StringBuilder builder = new StringBuilder();
        builder.append("<table align='center' border='0' cellspacing='0' cellpadding='20' width='700px;'" +
                " style='background:#F5F5F5;border-radius:15px;color:#878787;width:700'>");
        builder.append("<tr align='center' style='background: #FFFFFF;'>");
        builder.append("<td colspan='3' style='padding:20px;'><strong>DEALIFY IS AVAILABLE FOR</strong></td>");
        builder.append("</tr>");
        builder.append("<tr align='center'>");
        builder.append("<td>");
        builder.append("<table cellpadding='5px'>");
        builder.append("<tr style='text-align:center;'>");
//        builder.append("<td><a href=''><img src='" + CLOUD_FRONT + "/email_icons/iphone_final.png' style='padding: 0px 20px;display:block;'>i-phone</a></td>");
//        builder.append("<td><a href='https://play.google.com/store/apps/details?id=com.yetistep.dealify'><img src='" + CLOUD_FRONT + "/email_icons/android_final.png' style='padding: 0px 20px;display:block;'>Android</a></td>");
//        builder.append("<td><a href=''><img src='" + CLOUD_FRONT + "/email_icons/desktop_final.png' style='padding: 0px 20px;display:block;'>Desktop</a></td>");
        builder.append("</tr></table");
        builder.append("</td>");
        builder.append("</tr>");

        builder.append("<tr align='center' style='background:#F5F5F5'>");
        builder.append("<td colspan='3' style='padding-bottom:20px'>");
//        builder.append("<a href=''><img src='" + CLOUD_FRONT + "/email_icons/facebook_color.png' style='padding: 0px 10px;'></a>");
//        builder.append("<a href=''><img src='" + CLOUD_FRONT + "/email_icons/twitter_color.png' style='padding: 0px 10px;'></a>");
//        builder.append("<a href=''><img src='" + CLOUD_FRONT + "/email_icons/google_plus_color.png' style='padding: 0px 10px;'></a>");
        builder.append("</td>");
        builder.append("</tr>");
        builder.append("</table>");
        return builder.toString();
    }

    private static String getHtmlHeader() {
        String htmlHead = "<html><head><title>Welcome to " + COMPANY_NAME + "</title>" +
                "</head><body>";
        //"<style>" + style.toString() + "</style>" + "<link rel='stylesheet' " + "href='" + MainServlet.serverUrl + "/deal/fonts/font.css'>" +
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

    public static String prepareEmail(String subjectContent, String bodyContent, String userHeadContent) {
        StringBuilder builder = new StringBuilder();
        builder.append(getHtmlHeader());
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
