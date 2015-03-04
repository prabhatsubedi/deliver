package com.yetistep.delivr.abs;

import com.yetistep.delivr.util.*;
import eu.bitwalker.useragentutils.UserAgent;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/25/14
 * Time: 11:04 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractManager {
    @Autowired
    HttpServletRequest httpServletRequest;

    Logger log = Logger.getLogger(AbstractManager.class);
    public String getServerName(){
      //Requested Server Name
      return httpServletRequest.getServerName();
    }

    public String getServerUrl(){
        //Requested URL
        return httpServletRequest.getRequestURL().toString().replace(httpServletRequest.getServletPath(), "").trim();
    }

    public String getIpAddress(){
        //Client IP Address
        String ipAddress = httpServletRequest.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null)
            ipAddress = httpServletRequest.getRemoteAddr();

        return ipAddress;
    }

    protected boolean sendMail(String toAddress, String message, String subject) throws Exception {
        EmailUtil.Email mail = new EmailUtil.Email(toAddress, subject, message);
        return sendCommonMail(mail);
    }


    protected boolean sendAttachmentEmail(String toAddress, String message, String subject, String path) throws Exception {
        EmailUtil.Email mail = new EmailUtil.Email(toAddress, subject, message);
        EmailUtil.Attachment attachment = new EmailUtil.Attachment(path);
        return sendMailWithAttachments(mail, attachment);
    }

    private boolean sendCommonMail(EmailUtil.Email mail) throws Exception {
        EmailUtil email = new EmailUtil(mail);
        email.sendMail();
        return true;
    }

    private boolean sendMailWithAttachments(EmailUtil.Email mail, EmailUtil.Attachment attachment) throws Exception {
        EmailUtil email = new EmailUtil(mail);
        email.sendMailWithAttachment(attachment);
        return true;
    }

    public void validateMobileClient(String token) throws Exception {
        log.info("++++++++++++++ Validating mobile client +++++++++++++++");
//
//        UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();
//        ReadableUserAgent agent = parser.parse(httpServletRequest.getHeader("User-Agent"));
//
//        String timeStr = null;
//        String family = agent.getOperatingSystem().getFamily().toString();

        String userAgent = httpServletRequest.getHeader("User-Agent");
        UserAgent ua = UserAgent.parseUserAgentString(userAgent);
        String timeStr = null;
        String family = ua.getOperatingSystem().name();


        if (family.toUpperCase().indexOf("IOS")>= 0 || family.toUpperCase().indexOf("MAC")>= 0 || family.toUpperCase().indexOf("UNKNOWN")>= 0) {
            timeStr = RNCryptoEncDec.decryptAccessToken(token);
        } else if(family.toUpperCase().indexOf("ANDROID")>=0){
            timeStr = EncDecUtil.decryptAccessToken(token, MessageBundle.getSecretKey());
        }
        Long timeVal = Long.valueOf(timeStr).longValue();
        Long now = System.currentTimeMillis();
        log.info("+++++++ previous time "+ timeStr + " Current Time " + now + " +++++++++");
        Long diff = (now - timeVal) / 1000;
        log.info("+++++++ Time Diff " + diff);
//        if (diff > 120) //if diff more than 2 minutes
//            throw  new YSException("SEC002", "total time is " + diff);

    }

}
