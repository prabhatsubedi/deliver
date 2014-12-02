package com.yetistep.delivr.util;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 8/22/14
 * Time: 12:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class EmailSendUtil {

    public static boolean sendMail(String toAddress, String message, String subject) throws Exception {   //for admin.
        EmailUtil.Email mail = new EmailUtil.Email(toAddress, subject, message);
        return sendCommonMail(mail, null, false);
    }

    public static boolean sendMail(String toAddress, String message, String subject, String emailFrom) throws Exception {
        EmailUtil.Email mail = new EmailUtil.Email(toAddress, subject, message);
        return sendCommonMail(mail, emailFrom, false);
    }

    public static boolean sendMail(String toAddress, String ccAddress, String message, String subject, String emailFrom) throws Exception {
        EmailUtil.Email mail = new EmailUtil.Email(toAddress, ccAddress, subject, message);
        return sendCommonMail(mail, emailFrom, false);
    }

    public static boolean sendMail(String toAddress, String ccAddress, String bccAddress, String message, String subject, String emailFrom) throws Exception {
        EmailUtil.Email mail = new EmailUtil.Email(toAddress, ccAddress, bccAddress, subject, message);
        return sendCommonMail(mail, emailFrom, false);
    }

    public static boolean sendAsynchMail(String toAddress, String message, String subject, String emailFrom) throws Exception {
        EmailUtil.Email mail = new EmailUtil.Email(toAddress, subject, message);
        return sendCommonMail(mail, emailFrom, true);
    }

    public static boolean sendAsynchMail(String toAddress, String ccAddress, String message, String subject, String emailFrom) throws Exception {
        EmailUtil.Email mail = new EmailUtil.Email(toAddress, ccAddress, subject, message);
        return sendCommonMail(mail, emailFrom, true);
    }

    public static boolean sendAsynchMail(String toAddress, String ccAddress, String bccAddress, String message, String subject, String emailFrom) throws Exception {
        EmailUtil.Email mail = new EmailUtil.Email(toAddress, ccAddress, bccAddress, subject, message);
        return sendCommonMail(mail, emailFrom, true);
    }

    private static boolean sendCommonMail(EmailUtil.Email mail, String emailFrom, Boolean isAsynchronous) throws Exception {
        EmailUtil email = null;
        if (emailFrom != null) {
            email = new EmailUtil(mail, emailFrom);
        } else {
            email = new EmailUtil(mail);
        }
        //check for mail type.
        if (isAsynchronous) {
            email.sendMail();
        } else {
            email.sendAsynchMail();
        }
        return true;
    }

}
