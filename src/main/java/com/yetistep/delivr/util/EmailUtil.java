package com.yetistep.delivr.util;

import com.yetistep.delivr.abs.ThreadPoolManager;
import org.apache.commons.mail.*;
import org.apache.log4j.Logger;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: golchas
 * Date: 10/25/13
 * Time: 10:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class EmailUtil {
    private static final Logger log = Logger.getLogger(EmailUtil.class);
    private static final String HOST_NAME = MessageBundle.getSenderHost();
    private static final int SMTP_PORT = MessageBundle.getSenderPort();
    public static final String FROM_ADMIN="FROM ADMIN";
    public static final String FROM_ACCOUNTANT="FROM ACCOUNTANT";


    private String fromEmail;
    private String fromCredential;
    private Email mail;

    private static Pattern pattern;
    private static Matcher matcher;
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public EmailUtil(Email mail) {
        this(MessageBundle.getSenderEmail(), MessageBundle.getSenderPassword(), mail);
    }

    public EmailUtil(Email mail,String fromEmail){
        if(fromEmail.equals(EmailUtil.FROM_ADMIN)){
            new  EmailUtil(MessageBundle.getSenderEmail(), MessageBundle.getSenderPassword(),mail);
        }else if(fromEmail.equals(EmailUtil.FROM_ACCOUNTANT)){
            new EmailUtil(MessageBundle.getSenderEmail(), MessageBundle.getSenderPassword(),mail);
        }
    }

    public EmailUtil(String fromEmail, String fromCredential, Email mail) {
        this.mail = mail;
        this.fromEmail = fromEmail;
        this.fromCredential = fromCredential;
    }

    public void sendMail() throws Exception {
        log.debug("Sending email to " + this.mail.toAddress);
        HtmlEmail email = prepareEmail();
        email.send();
    }

    public void sendAsynchMail() {
        Runnable asynchMailJob = new Runnable() {
            @Override
            public void run() {
                log.debug("Sending asynch email to " + mail.toAddress);
                try {
                    HtmlEmail email = prepareEmail();
                    email.send();
                } catch (Exception e) {
                    log.error("Asynch Email to " + mail.toAddress + " FAILED ", e);
                }
            }
        };

        ThreadPoolManager.runAsynchJob(asynchMailJob);
    }

    public void sendAsynchMail(final Attachment... attachments) {
        Runnable asynchMailJob = new Runnable() {
            @Override
            public void run() {
                log.debug("Sending asynch email to " + mail.toAddress);
                try {
                    MultiPartEmail email = prepareMultipartEmail(attachments);
                    email.send();
                } catch (Exception e) {
                    log.error("Asynch Email to " + mail.toAddress + " FAILED ", e);
                }
            }
        };

        ThreadPoolManager.runAsynchJob(asynchMailJob);
    }

    public void sendMailWithAttachment(Attachment... attachments) throws Exception {
        log.debug("Sending email to " + this.mail.toAddress);
        MultiPartEmail email = prepareMultipartEmail(attachments);
        email.send();
    }


    public static class Email {
        private String toAddress;
        private String subject;
        private String body;//can be either a plain text or html
        private String ccAddress;
        private String bccAddress;

        public Email(String toAddress, String subject, String body) {
            this.toAddress = toAddress;
            this.subject = subject;
            this.body = body;
        }

        public Email(String toAddress, String ccAddress, String subject, String body) {
            this.toAddress = toAddress;
            this.ccAddress = ccAddress;
            this.subject = subject;
            this.body = body;
        }

        public Email(String toAddress, String ccAddress, String bccAddress, String subject, String body) {
            this.toAddress = toAddress;
            this.ccAddress = ccAddress;
            this.bccAddress = bccAddress;
            this.subject = subject;
            this.body = body;
        }

        public String getToAddress() {
            return toAddress;
        }

        public void setToAddress(String toAddress) {
            this.toAddress = toAddress;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getCcAddress() {
            return ccAddress;
        }

        public void setCcAddress(String ccAddress) {
            this.ccAddress = ccAddress;
        }

        public String getBccAddress() {
            return bccAddress;
        }

        public void setBccAddress(String bccAddress) {
            this.bccAddress = bccAddress;
        }
    }


    public static class Attachment {
        private String name;
        private String path;

        public Attachment(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getName() {
            if (name != null)
                name += path.substring(path.lastIndexOf("."));

            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


    private HtmlEmail prepareEmail() throws Exception {
        HtmlEmail email = new HtmlEmail();

        email.setHostName(HOST_NAME);
        email.setSmtpPort(SMTP_PORT);
        email.setAuthenticator(new DefaultAuthenticator(fromEmail, fromCredential));
        email.setTLS(true);

        email.setFrom(fromEmail);
        email.setSubject(mail.getSubject());
        email.setHtmlMsg(mail.getBody());
        addToAddresses(email);

        return email;
    }

    private void addToAddresses(MultiPartEmail email) throws Exception {
        email.addTo(mail.getToAddress());
        /*if (mail.getCcAddress() != null)
            email.addBcc(mail.getCcAddress());
        if (mail.getBccAddress() != null)
            email.addCc(mail.getBccAddress());*/
    }

    private MultiPartEmail prepareMultipartEmail(Attachment... attachments) throws Exception {
        MultiPartEmail email = new HtmlEmail();
        email.setHostName(HOST_NAME);
        email.setSmtpPort(SMTP_PORT);
        email.setAuthenticator(new DefaultAuthenticator(fromEmail, fromCredential));
        email.setTLS(true);

        for (Attachment attachment : attachments) {
            EmailAttachment emailAttachment = new EmailAttachment();

            if (attachment.getPath().contains("http")) {
                URL url = new URL(attachment.getPath());
                emailAttachment.setURL(url);
            } else if (attachment.getPath() != null) {
                emailAttachment.setPath(attachment.getPath());
            }

            emailAttachment.setDisposition(EmailAttachment.ATTACHMENT);
            emailAttachment.setName(attachment.getName());

            email.setFrom(fromEmail);
            email.setSubject(mail.getSubject());
            addToAddresses(email);
            email.setMsg(mail.getBody());
            email.attach(emailAttachment);
        }
        return email;
    }

    public static boolean validate(final String hex) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(hex);
        return matcher.matches();

    }

}
