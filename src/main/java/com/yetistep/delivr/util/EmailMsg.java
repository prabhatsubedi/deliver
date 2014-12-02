package com.yetistep.delivr.util;



import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: golchas
 * Date: 10/27/13
 * Time: 10:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class EmailMsg {
    private static final String COMPANY_NAME = "Delivr";
    //private static final String CURRENCY = SystemPropertyManager.readPrefValue(PreferenceType.CURRENCY);
    public static final String EMAIL_WELCOME_SUBJECT = "Welcome To Delivr";
    //private static final String CLOUD_FRONT = System.getProperty("CLOUD_FRONT");

    private static StringBuilder style = new StringBuilder();

    static {
        style.append("body{font-size: 16px; font-family: Roboto;}");
    }

//    public static String couponCreateByMerchantToAdmin(String url, Merchant advertiser, Coupon coupon, String subject) {
//        StringBuilder body = new StringBuilder();
//
//        body.append("The following Deal has been created by " + advertiser.getName() + "<br /><br />");
//        body.append("<b>Deal Summary:</b> <br />");
//        body.append("Deal Reference Number: " + coupon.getId() + "<br />");
//        body.append("Deal Name: " + coupon.getName() + "<br />");
//        body.append("Start Date: " + coupon.getStartDate() + "<br />");
//        body.append("End Date: " + coupon.getEndDate() + "<br />");
//
//        body.append("Click here to review and publish the Deal <br />");
//        body.append(getEmailBodyButton("View Deal", url));
//        return prepareAdminEmail(subject, body.toString(), null);
//    }
//
//    public static String couponCreatedByAdmin(String url, Merchant advertiser, Coupon coupon, String subject) {
//        StringBuilder body = new StringBuilder();
//
//        body.append("Dear admin,<br />");
//        body.append("A Deal has been created. A short detail is given below: <br />");
//        body.append("<b>Deal Name:</b> " + coupon.getName() + "<br />");
//        body.append("<b>Start Date:</b> " + coupon.getStartDate() + "<br />");
//        body.append("<b>End Date:</b> " + coupon.getEndDate() + "<br /><br />");
//
//        body.append("Please click on the link below to view the Deal: <br />");
//        body.append(getEmailBodyButton("View Deal", url));
//        body.append("If the link does not work, copy paste the following url in the browse: <br />");
//        body.append(url);
//        body.append("<br /><br />");
//
//        return prepareEmail(subject, body.toString(), null);
//    }
//
//    public static String campaignCreatedForAdvertiser(String url, Merchant advertiser, Coupon coupon, String subject) {
//        StringBuilder body = new StringBuilder();
//
//        body.append("Dear " + advertiser.getContactPerson() + ",<br />");
//        body.append("Admin has created a Deal on your behalf. A short detail is given below: <br />");
//        body.append("<b>Deal Name:</b> " + coupon.getName() + "<br />");
//        body.append("<b>Start Date:</b> " + coupon.getStartDate() + "<br />");
//        body.append("<b>End Date:</b> " + coupon.getEndDate() + "<br /><br />");
//        body.append("Please click on the link below to view the Deal: <br />");
//        body.append(getEmailBodyButton("View Deal", url));
//        body.append("If the link does not work, copy paste the following url in the browse: <br />");
//        body.append(url);
//        body.append("<br /><br />");
//        return prepareEmail(subject, body.toString(), null);
//    }
//
//    public static String campaignCreatedByAdvForAdv(String url, Merchant advertiser, Coupon coupon, String subject) {
//        StringBuilder body = new StringBuilder();
//
//        body.append("Dear " + advertiser.getContactPerson() + ",<br /><br />");
//        body.append("Thank you for submitting an Deal." + "<br /><br />");
//        body.append(" We'll let you know when your Deal is verified and approved by our team." + "<br /><br />");
//
//        body.append("<b>Deal Summary:</b><br />");
//        body.append("Deal Reference number: " + coupon.getId() + "<br/>");
//        body.append("Deal  Name: " + coupon.getName() + "<br/>");
//        body.append("Start Date: " + coupon.getStartDate() + "<br />");
//        body.append("End Date: " + coupon.getEndDate() + "<br />");
//
//        body.append("<br /><br />");
//
//        return prepareEmail(subject, body.toString(), null);
//    }
//
//    public static String couponExpired(Advertiser advertiser, Coupon coupon, String subject, String url) {
//        StringBuilder body = new StringBuilder();
//
//        body.append("Dear " + advertiser.getContactPerson() + ",<br/><br/>");
//        body.append("Your deal " + coupon.getName() + " with ID: " + coupon.getId() + " has expired. " +
//                "If you want to continue your deal please recreate another deal with the desired start and end dates.<br/><br/>");
//        body.append("Log onto Dealify now and re-submit a deal.<br/>");
//        body.append(getEmailBodyButton("Log in", url));
//        body.append("<br/>");
//        return prepareEmail(subject, body.toString(), null);
//    }
//
//    public static String merchantProfileUpdate(Merchant merchant, String subject) {
//        StringBuilder body = new StringBuilder();
//
//        body.append("Dear " + merchant.getContactPerson() + ",<br/><br/>");
//        body.append("Thank you for updating your profile.<br/>");
//        body.append(" If you did not update your profile and you still received this email,<br/> please reply and we will try to fix the issue.");
//
//        body.append("<br/><br/>");
//        return prepareEmail(subject, body.toString(), null);
//    }
//
////    public static String advertiserProfileUpdateToAdmin(Advertiser advertiser, String subject) {
////        StringBuilder body = new StringBuilder();
////
////        body.append(advertiser.getContactPerson() + " has modified profile page." + "<br/><br/>");
////        body.append("Updated Date: " + MessageBundle.getCurrentTimestampSQL());
////
////        body.append("<br/><br/><br/>");
////        return prepareEmail(subject, body.toString(), null);
////    }
//
//    public static String perkProviderProfileUpdate(PerkProvider perkProvider) {
//        StringBuilder builder = new StringBuilder();
//
//        builder.append("Dear " + perkProvider.getContactPerson() + ",<br/><br/>");
//        builder.append("Thank you for updating your profile.  If you did not update your profile and you are receiving " +
//                "this email, please reply to this email and we will try to find out what happened.");
//
//        builder.append("<br/><br/>");
//        return prepareEmail("", builder.toString(), null);
//
//    }
//
//    public static String perkProviderProfileUpdateToAdmin(PerkProvider perkProvider) {
//        StringBuilder builder = new StringBuilder();
//
//        builder.append(perkProvider.getName() + " has modified profile page." + ",<br/><br/>");
//        builder.append("Updated Date: " + MessageBundle.getCurrentTimestampSQL());
//
//        builder.append("<br/><br/>");
////        builder.append(getFooterForAdmin());
//        return prepareAdminEmail("", builder.toString(), null);
//    }
//
//
//    public static String resetForgotPassword(String url, String userName, String subject) {
//        StringBuilder body = new StringBuilder();
//
//        body.append("Dear " + userName + ",<br/><br/>");
//        body.append("We're sorry you are having difficulty logging in.Click on the link below to reset your password.<br/><br/>");
//
//        body.append("Please click on the link below and navigate to reset your new password. <br/>");
//        body.append(getEmailBodyButton("Reset Password", url));
//        body.append("If the link does not work,please copy & paste the following URL in your web browser:<br/>");
//        body.append(url);
//
//        body.append("<br/><br/>");
//
//        return prepareEmail(subject, body.toString(), null);
//    }
//
//    public static String createPasswordForNewUser(String url, String userName, Advertiser advertiser, String userEmail, String loginUrl, String subject) {
//        StringBuilder body = new StringBuilder();
//
//        body.append("Dear " + userName + ",<br/><br/>");
//        body.append(advertiser.getName() + " would like to add you as a cashier.  After activating your account, " +
//                "you will be able to use  cashier dashboard and " +
//                "start verifying QR code of the deal." + "<br/><br/>");
//        body.append("Your user name : " + userEmail + "<br/><br/>");
//
//        body.append("Click on the link below to set your password. <br/>");
//        body.append(getEmailBodyButton("Create Password", url));
//        body.append("If the link does not work,please copy & paste the following URL in your browser:<br/>");
//        body.append(url);
//        body.append("<br/><br/>");
////        body.append("Link to sign in cashier dashboard" + "<br/>");
////        body.append(getEmailBodyButton("Sign in Page", loginUrl));
////        body.append("<br/><br/>");
//
//        return prepareEmail(subject, body.toString(), null);
//    }
//
//    public static String newPasswordLinkForPerkProvider(String url, String perkProviderName, String userEmail, String subject) {
//        StringBuilder body = new StringBuilder();
//
//        body.append("Dear " + perkProviderName + ",<br/><br/>");
//        body.append("Welcome & thank you for joining Dealify." + "<br/><br/>");
//        // builder.append(perkProviderName + " would like to add you as a user. After setting your password " +
//        //       "you will be able to log in your" + perkProviderName + " dashboard <br/>");
//
//        body.append("Your account as perk provider is created and details are as follows:<br/><br/>");
//        body.append("Your user name : " + userEmail + "<br/><br/>");
//        body.append("Please click on the link below and navigate to create your new password. <br/>");
//        body.append(getEmailBodyButton("Create Your Password", url));
//        body.append("If the link does not work,please copy paste the following URL in the browser:<br/>");
//        body.append(url);
//        body.append("<br/><br/>");
//
//        return prepareEmail(subject, body.toString(), null);
//    }
//
//    public static String newPasswordLinkForAffiliate(String url, String affiliateName, String userEmail) {
//        StringBuilder builder = new StringBuilder();
//        builder.append("Dear " + affiliateName + ",<br/><br/>");
//        builder.append("Welcome & thank you for joining Dealify." + "<br/><br/>");
//        builder.append("Your account as affiliate is created and details are as follows:<br/><br/>");
//        builder.append("Your user name : " + userEmail + "<br/><br/>");
//        builder.append("Please click on the link below and navigate to create your new password. <br/>");
//        builder.append(getEmailBodyButton("Create Your Password", url));
//        builder.append("If the link does not work,please copy paste the following URL in the browser:<br/>");
//        builder.append(url);
//        builder.append("<br/><br/>");
//        return prepareEmail("", builder.toString(), null);
//    }
//
//    public static <T extends Merchant> String activateMerchant(String url, T merchant, String subject) {
//        StringBuilder body = new StringBuilder();
//        body.append(getHtmlHeader());
//        body.append("Dear  " + merchant.getContactPerson() + ",<br/><br/>");
//        if (merchant instanceof Reseller) {
//            body.append("Your account is now active . You can start uploading deals for your customers." + "<br/><br/>");
//        } else {
//            body.append("Your account has been approved & activated. You can now start uploading deals ." + "<br/><br/>");
//        }
//        body.append(getEmailBodyButton("Login to Dealify", url));
//        body.append("<br/>");
//        return prepareEmail(subject, body.toString(), null);
//    }
//
//    public static <T extends Merchant> String reactivateMerchant(String url, T merchant, String subject) {
//        StringBuilder body = new StringBuilder();
//        body.append("Dear  " + merchant.getContactPerson() + ",<br/><br/>");
//        if (merchant instanceof Advertiser) {
//            body.append("Your account is now active . You can start uploading deals for your customers." + "<br/><br/>");
//        } else {
//            body.append("Your account has been approved & activated. You can now start uploading deals." + "<br/><br/>");
//        }
//        body.append(getEmailBodyButton("Login to Dealify", url));
//        body.append("<br/><br/>");
//
//        return prepareEmail(subject, body.toString(), null);
//    }
//
//    public static <T extends Merchant> String inactiveMerchant(T merchant, String subject) {
//
//        StringBuilder body = new StringBuilder();
//
//        body.append("Dear " + merchant.getContactPerson() + ",<br/><br/>");
//
//        body.append("We have deactivated your account as requested. " + "<br/><br/>");
//        body.append("We look forward to seeing you back on Dealify soon." + "<br/>");
//
//        body.append("<br/><br/>");
//        return prepareEmail(subject, body.toString(), null);
//    }
//
//    public static String activeCashier(String url, String userName, String subject) {
//        StringBuilder body = new StringBuilder();
//
//        body.append("Dear  " + userName + ",<br/><br/>");
//        body.append("Your account has been approved & activated. You can now validate & redeem deals." + "<br/><br/>");
//        body.append("Click below to sign into the cashier dashboard. <br/>");
//        body.append(getEmailBodyButton("Cashier Dashboard", url));
//        body.append("<br/>Link to download the cashier scanner app is as below:<br/>");
//        body.append(getEmailBodyButton("Download Dealify Scanner App", "https://play.google.com/store/apps/details?id=com.yetistep.dealifyscanner"));
//        body.append("<br/>");
//        return prepareEmail(subject, body.toString(), null);
//    }
//
//    public static String inactiveCashier(String userName, String providerName, String subject) {
//        StringBuilder body = new StringBuilder();
//        body.append("Dear " + userName + ",<br/><br/>");
//
//        body.append(providerName + " has deactivated your account. " + "<br/><br/>");
//        body.append("Please contact your administrator at " + providerName + " for more details." + "<br/>");
//
//        body.append("<br/>");
//        return prepareEmail(subject, body.toString(), null);
//    }
//
//    public static String invoice(Merchant merchant, Coupon coupon, YTInvoice invoice, String card) {
//        StringBuilder body = new StringBuilder();
//
//        String invoiceTitle = SystemPropertyManager.readPrefValue(PreferenceType.COMPANY_NAME);
//
//        body.append("Dear " + merchant.getContactPerson() + ",<br/><br/>");
//        body.append("Payment for your deal " + coupon.getName() + " has been successfully processed.<br/><br/>");
//        body.append("The details are:<br/><br/>");
//        body.append("Bill Issued on:" + invoice.getPaidDate() + "<br/>");
//        body.append("Receipt No:" + invoice.getInvoiceNo() + "<br/><br/>");
//        body.append("Deal ID:" + coupon.getId() + "<br/>");
//        body.append("Deal Name:" + coupon.getName() + "<br/>");
//        body.append("Deal Offer:" + coupon.getOfferName() + "<br/><br/>");
//        body.append("Billing Period: " + invoice.getFromDate() + "to " + invoice.getToDate() + "<br/>");
//        body.append("Time of Payment:" + invoice.getPaidDate() + " at " + DateUtil.formatDate(invoice.getPaidDate(), "HH:mm") + "<br/>");
//
//        body.append("Card Number : **** **** **** " + card + "<br/>");
//        body.append("Total Amount Billed: " + CURRENCY + invoice.getAmount() + "<br/><br/>");
//        body.append("All the invoices and receipts are attached below." + "<br/><br/>");
//        body.append("Please reply to this email if there is an error." + "<br/><br/>");
//        return prepareEmail(" Payment Processed ", body.toString(), null);
//    }
//
//
//    public static String dealVerified(Coupon coupon, Advertiser advertiser, String url) {
//        StringBuilder builder = new StringBuilder();
//
//        builder.append("Dear " + advertiser.getContactPerson() + ",<br /><br />");
//        builder.append("Congratulations! Your Deal has been verified and approved!  <br /><br />");
//        builder.append("Below is the detail of your Deal<br /><br />");
//        builder.append("<b>Deal Summary:</b><br />");
//        builder.append("Deal Reference Number: " + coupon.getId() + "<br />");
//        builder.append("Deal Name: " + coupon.getName() + "<br/>");
//        builder.append("Start Date: " + coupon.getStartDate() + "<br/>");
//        builder.append("End Date: " + coupon.getEndDate() + "<br/>");
//        builder.append("Dealify Service Fee :" + coupon.getCommissionPct() + "<br/>");
//
//        builder.append("<br/>");
//        builder.append(getEmailBodyButton("View Deal Analytics", url));
//        builder.append("<br/>");
//
//        return prepareEmail("", builder.toString(), null);
//    }
//
//
//    public static String campaignModified(Ad_Campaign campaign, Advertiser advertiser) {
//        StringBuilder builder = new StringBuilder();
//        builder.append(getHtmlHeader());
//
//        builder.append(advertiser.getName() + ",has modified a Deal. <br /><br />");
//        builder.append("Below is the detail of the modified Deal.<br /><br />");
//        builder.append("Deal number: " + campaign.getId() + "<br /><br />");
//        builder.append("<b>Deal Summary:</b><br />");
//        builder.append("Deal Name: " + campaign.getName() + "<br/>");
//        builder.append("Start Date: " + campaign.getStart_date() + "<br/>");
//        builder.append("End Date: " + campaign.getEnd_date() + "<br/>");
//
//        if (!campaign.getRedirectType().equals(RedirectType.COUPON)) {
//            builder.append("Daily Budget: " + CURRENCY + " " + campaign.getDaily_budget_limit() + "<br/>");
//            builder.append("Total Budget: " + CURRENCY + " " + campaign.getCampaign_budget() + "<br/>");
//        }
//
//        builder.append("Modified Date: " + MessageBundle.getCurrentTimestampSQL());
//        builder.append("<br/><br/>");
//
//        return prepareEmail("", builder.toString(), null);
//    }
//
//    public static String couponUnpublishedByAdmin(Coupon coupon, Advertiser advertiser, String url, String subject) {
//        StringBuilder body = new StringBuilder();
//
//        body.append("Dear " + advertiser.getContactPerson() + ",<br /><br />");
//        body.append("As per your request, we've unpublished your running  Deal.  <br /><br />");
//        body.append("Below is the detail of your unpublished Deal.<br /><br />");
//        body.append("<b>Deal Summary:</b><br />");
//        body.append("Deal Reference Number: " + coupon.getId() + "<br/>");
//        body.append("Deal Name: " + coupon.getName() + "<br/>");
//        body.append("Start Date: " + coupon.getStartDate() + "<br/>");
//        body.append("End Date: " + coupon.getEndDate() + "<br/>");
//
//        body.append("<br/>");
//        body.append(getEmailBodyButton("Republish this deal again", url));
//        body.append("<br/>");
//
//        return prepareEmail(subject, body.toString(), null);
//    }
//
//    public static String couponUnpublished(Coupon coupon, Advertiser advertiser, String subject) {
//        StringBuilder body = new StringBuilder();
//
//        body.append(advertiser.getName() + " unpublished running Deal( ID: " + coupon.getId() + " ,Name: " + coupon.getName() + ")<br /><br />");
//        body.append("Initiate to find why.<br /><br />");
//        body.append("<br/><br/>");
//        return prepareEmail(subject, body.toString(), null);
//    }
//
//    public static String couponPublishedByAdmin(Coupon coupon, Advertiser advertiser, String url) {
//        StringBuilder builder = new StringBuilder();
//        builder.append(getHtmlHeader());
//
//        builder.append("Dear " + advertiser.getContactPerson() + ",<br /><br />");
//        builder.append("As per your request, we've published your Deal.  <br /><br />");
//        builder.append("Below is the detail of your published Deal.<br /><br />");
//
//        builder.append("<b>Deal Summary:</b><br />");
//        builder.append("Deal Reference Number: " + coupon.getId() + "<br />");
//        builder.append("Deal Name: " + coupon.getName() + "<br/>");
//        builder.append("Start Date: " + coupon.getStartDate() + "<br/>");
//        builder.append("End Date: " + coupon.getEndDate() + "<br/>");
//
//        builder.append("<br/>");
//        builder.append(getEmailBodyButton("View Deal Analytics", url));
//        builder.append("<br/>");
//
//        return prepareEmail("", builder.toString(), null);
//    }
//
//    public static String couponPublished(Coupon coupon, Advertiser advertiser) {
//        StringBuilder builder = new StringBuilder();
//        builder.append(advertiser.getName() + " published  running  Deal( ID: " + coupon.getId() + " ,Name: " + coupon.getName() + ")<br /><br />");
//        builder.append("<br/><br/>");
//        return prepareEmail("", builder.toString(), null);
//    }
//
//
//    public static String dealArchived(Coupon coupon, Merchant merchant, String subject, Role role, String archiveUrl) {
//        StringBuilder body = new StringBuilder();
//
//        body.append("Dear " + merchant.getContactPerson() + ",<br /><br />");
//        if (role.equals(Role.Advertiser)) {
//            body.append("Your deal has been archived as requested.  <br /><br />");
//        } else if (role.equals(Role.Reseller)) {
//            body.append("Your deal has been archived by your authorised reseller(name)<br/>");
//        } else if (role.equals(Role.Admin)) {
//            body.append("As per your request we've archived your Deal.");
//        }
//        body.append("The details are:<br /><br />");
//        body.append("Deal number: " + coupon.getId() + "<br />");
//        body.append("Deal name: " + coupon.getName() + "<br/>");
//        body.append("<br/>");
//        body.append(getEmailBodyButton("View Deal", archiveUrl));
//        body.append("<br/>Please reply to this email if there is an error.");
//
//        body.append("<br/><br/>");
//        return prepareEmail("", body.toString(), null);
//    }
//
//    public static String payoutRequestBody(String type, PayoutRequest payout, Client client, String url) {
//        //String salutation   =   client.getGender().equalsIgnoreCase("male")? "Mr. " : "Ms./Mrs. ";
//
//        StringBuilder builder = new StringBuilder();
//        builder.append(getHtmlHeader());
//
////      builder.append("Dear account,<br />");
//        builder.append(client.getName() + " has created a " + type + " request. " + "<br /><br />");
//        builder.append("Details as below:<br /><br />");
//        builder.append("Name of user: " + client.getName() + "<br/>");
//        builder.append("User Id: " + client.getClientId() + "<br/>");
//        builder.append("Amount requested for " + type + " request : " + CURRENCY + " " + payout.getAmount() + "<br/>");
//        builder.append("Date Requested: " + MessageBundle.getCurrentTimestampSQL() + "<br/>");
//
//        // builder.append(payout.getCashoutdetail() + " of " +MessageBundle.getCurrency()+" "+ payout.getAmount());
//        builder.append("<br />");
//        builder.append(getEmailBodyButton("View Pending Requests", url));
//        builder.append("<br />");
//
//        builder.append(getFooterForAdmin());
//        return builder.toString();
//    }
//
//
//    public static String couponRedeemForClient(Client client, Coupon coupon, ClientCoupon clientCoupon, CouponTransaction couponTransaction, Integer pointsEarned) {
//        StringBuilder builder = new StringBuilder();
//
//        builder.append("Dear " + client.getName() + ",<br/><br/>");
//        builder.append("<b>" + coupon.getName() + "</b>" + " has successfully been redeemed by you on " + couponTransaction.getDate() + " at " + coupon.getProviderName() + "<br/>");
//        builder.append("<br/><br/>");
//        builder.append("<b>Deal Detail</b><br/>");
//        builder.append("Deal Name: " + coupon.getName() + "<br/>");
//        builder.append("Deal Serial no: " + clientCoupon.getSerialNo() + "<br/>");
//        builder.append("Bill amount: " + CURRENCY + " " + couponTransaction.getBillAmount() + "<br/>");
//        builder.append("Discount Amount: " + CURRENCY + " " + couponTransaction.getSavingAmount() + "<br/>");
//        builder.append("Points Earned: <b>" + (pointsEarned == 0 ? "None" : pointsEarned) + "</b><br/>");
//
//        builder.append("<br/><br/>");
//
//        return prepareEmail("", builder.toString(), null);
//    }
//
////    public static String couponRedeemForAdvertiser(Cashier cashier, Coupon coupon, Advertiser advertiser, BigDecimal billAmount,
////                                                   ClientCoupon clientCoupon, CouponTransaction couponTransaction, BigDecimal dealifyCommission, String subject) {
////        StringBuilder body = new StringBuilder();
////
////        body.append("Dear " + advertiser.getContactPerson() + ",<br/><br/>");
////        body.append("A Deal with serial no " + clientCoupon.getSerialNo() + " for Deal " + coupon.getId() + " " + coupon.getName() +
////                " has successfully been redeemed." + "<br/><br/><br/>");
////
////        body.append("<b>Payment Information</b><br/>");
////        body.append("Deal Name: " + coupon.getName() + "<br/>");
////        body.append("Deal Serial no: " + clientCoupon.getSerialNo() + "<br/>");
////        body.append("Deal verified by: " + cashier.getName() + "<br/>");
////        body.append("Bill amount: " + CURRENCY + " " + billAmount + "<br/>");
////        body.append("Discount Amount: " + CURRENCY + " " + couponTransaction.getSavingAmount() + "<br/>");
////        body.append("Dealify Commission (commission%): " + dealifyCommission + "<br/>");
////        body.append("Dealify service fee: " + CURRENCY + " " + couponTransaction.getBillAmount().multiply(dealifyCommission));
////        body.append("Date and Time: " + couponTransaction.getDate() + "<br/><br/>");
////
////
////        body.append("Please reply to this email if there has been an error." + "<br/><br/>");
////        body.append("<br/><br/>");
////
////        return prepareEmail(subject, body.toString(), null);
////    }
//
//    public static String couponRedeemForAdvertiser(Cashier cashier, Coupon coupon, Advertiser advertiser, BigDecimal billAmount,
//                                                   ClientCoupon clientCoupon, CouponTransaction couponTransaction, String subject) {
//        StringBuilder body = new StringBuilder();
//
//        body.append("Dear " + advertiser.getContactPerson() + ",<br/><br/>");
//        body.append("A deal for " + coupon.getName() + " with ID " + coupon.getId() + " has been successfully redeemed.<br/>");
//
//        body.append("<b>Deal Details</b><br/>");
//        body.append("Deal Name: " + coupon.getName() + "<br/>");
//        body.append("Deal Serial no: " + clientCoupon.getSerialNo() + "<br/>");
//        body.append("Deal verified by: " + cashier.getName() + "<br/><br/>");
//        body.append("Date and Time of redemption: " + couponTransaction.getDate());
//        body.append("<br/><br/>");
//
//        return prepareEmail(subject, body.toString(), null);
//    }
//
//    public static String getCouponRedeemMailForClient(Advertiser advertiser, String subject, Client client, Coupon coupon) {
//        StringBuilder body = new StringBuilder();
//        body.append("Dear " + client.getName() + ",<br/><br/>");
//        body.append("You have successfully redeemed the deal at " + coupon.getProviderName() + ".<br/>");
//        body.append("Keep coming back to Dealify for more exciting deals and offers.<br/>");
//        return prepareEmail(subject, body.toString(), null);
//    }
//
//    public static String dealRedeemForAdmin(Client client, Coupon coupon, CouponTransaction couponTransaction, ClientCoupon clientCoupon, BigDecimal dealifyCommission, Integer pointsEarned) {
//        StringBuilder builder = new StringBuilder();
//
//        builder.append(getHtmlHeader());
//        builder.append("Below is the detail : " + "<br/><br/>");
//        builder.append("Mobile user name: " + client.getName() + "<br/>");
//        builder.append("Client id of: " + client.getClientId() + "<br/>");
//        builder.append("Deal ID : " + coupon.getId() + "<br/>");
//        builder.append("Deal Name : " + coupon.getName() + "<br/>");
//        builder.append("Deal Serial no : " + clientCoupon.getSerialNo() + "<br/>");
//        builder.append("Bill Amount : " + CURRENCY + " " + couponTransaction.getBillAmount() + "<br/>");
//        builder.append("Discount : " + CURRENCY + " " + couponTransaction.getSavingAmount() + "<br/>");
//        //builder.append("Dealify Commission (commission%) : " + dealifyCommission + "<br/>");
//        builder.append("Redeem Date and Time : " + couponTransaction.getDate() + "<br/>");
//        builder.append("Dealify service fee :" + 6 + "<br/>");
//        builder.append("Dealify service fee:" + couponTransaction.getBillAmount().multiply(new BigDecimal(6)) + "<br/>");
//        builder.append("Points credited to User :" + pointsEarned + "<br/>");
//        builder.append("<br/><br/>");
//
//        builder.append(getFooterForAdmin());
//        return builder.toString();
//
//    }
//
//
//    public static String resetPassword(String newPassword, User user) {
//        StringBuilder builder = new StringBuilder();
//        builder.append(getHtmlHeader());
//
//        builder.append("Dear user,<br /><br />");
//        builder.append("Your new password is: " + newPassword + "<br />");
//        builder.append("Make sure to change the password, after you login.");
//        builder.append("<br/><br/>");
//        return prepareEmail("", builder.toString(), null);
//    }
//
//    public static String paypalPaymentMail(Client client, PayoutRequest payout, String processedTime) {
//        StringBuilder builder = new StringBuilder();
//        builder.append("<br/><br/>");
//        builder.append("Dear " + client.getName() + "<br/><br/>");
//        builder.append("Your cashout request for the paypal has been processed from our side.<br/><br/>");
//        builder.append("Below is the details of cashout.<br/><br/>");
//        builder.append("Cashout type : Paypal <br/>");
//        builder.append("Cashout done on : " + payout.getCashoutdetail() + "<br/>");
//        builder.append("Cashout done date : " + processedTime + "<br/>");
//        builder.append("Amount requested for cashout : " + CURRENCY + " " + payout.getAmount() + "<br/>");
//        builder.append("Processing fee deducted from your swipr balance : " + CURRENCY + "[PAYPAL TDS AMOUNT] <br/><br/>");
//        builder.append("Please check your paypal account for amount transferred.<br/>");
//        builder.append("It normally takes maximum of 72 hours for the transaction to complete.<br/><br/>");
//
//        builder.append("PLEASE DO NOT REPLY TO THIS MAIL. THIS IS AN AUTO GENERATED MAIL\n" + "<br/>" +
//                "AND REPLIES TO THIS EMAIL ID ARE NOT ATTENDED TO.");
//        builder.append("<br/><br/>");
//        return prepareEmail("", builder.toString(), null);
//    }
//
//    public static String merchantSignUpInfoToAdmin(String merchantt, String subject, String loginUrl) {
//        StringBuilder body = new StringBuilder();
//        String resellerText = (merchantt.equals("Reseller") ? ", waiting for approval." : ".");
//        body.append("New " + merchantt + " has signed up " + resellerText + "<br/><br/>");
//        body.append("Log in to review a new " + merchantt + "'s application.<br/>");
//        body.append(getEmailBodyButton("Log in", loginUrl));
//        body.append("<br/><br/>");
//
//        return prepareAdminEmail(subject, body.toString(), null);
//    }
//
//    public static String stripeDisconnectEmail(Advertiser advertiser) {
//        StringBuilder builder = new StringBuilder();
//
//        builder.append("Dear " + advertiser.getName() + ",<br/><br/>");
//        builder.append("Unfortunately, your Stripe connection with Swipr is broken. We will not be able to process your Deals without it.<br />");
//        builder.append("Please reconnect your stripe connect account with Swipr, by logging into Swipr and procceding with Stripe Connect.");
//        builder.append("<br/><br/>");
//
//        return prepareEmail("", builder.toString(), null);
//    }
//
//    public static String stripeDisconnectEmailForAdmin(Advertiser advertiser) {
//        StringBuilder builder = new StringBuilder();
//
//        builder.append("Advertiser " + advertiser.getName() + " has dis-connected his/her Stripe connection with Swipr.");
//        builder.append("<br/><br/>");
//
//
//        return prepareAdminEmail("", builder.toString(), null);
//    }
//
//    public static String invoicePayment(Boolean forAdmin, String invoiceTable, Advertiser advertiser, List<PaidInvoiceDetail> paidInvoiceDetailList) {
//        StringBuilder builder = new StringBuilder();
//        builder.append(getHtmlHeader());
//        if (forAdmin) {
//            builder.append("Payment has been processed successfully via " + advertiser.getName() + "'s credit card for the below mentioned expired Deals." + "<br/><br/>");
//        } else {
//            builder.append("Dear " + advertiser.getContactPerson() + ",<br/><br/>");
//            builder.append("Payment has been processed successfully via your credit card for the below mentioned expired Deals." + "<br/><br/>");
//        }
//
//        builder.append("Date of Payment Processed: " + MessageBundle.getCurrentTimestampSQL() + "<br/></br>");
//
//        //Table Start
//        builder.append(invoiceTable);
//
//        //Table End
//
//        if (forAdmin) {
//            builder.append("<br/><br/><br/>");
//            builder.append(getFooterForAdmin());
//        } else {
//            builder.append("<br/><br/>");
//            builder.append("All the invoices are attached below for your reference." + "<br/><br/>");
//
//            int increment = 1;
//            for (PaidInvoiceDetail paidInvoiceDetail : paidInvoiceDetailList) {
//                String filePath = paidInvoiceDetail.getPath();
//                builder.append(increment + ") " + "<a href=\"" + filePath + "\" target=\"_blank\" >" + "Deal " + paidInvoiceDetail.getCampaignName() + "</a>");
//                builder.append("<br/>");
//                increment++;
//            }
//
//            builder.append("<br/><br/>");
//            builder.append("Please reply to this email if there has been an error.");
//            builder.append("<br/><br/><br/>");
//            builder.append(getFooter());
//        }
//
//        return builder.toString();
//    }
//
//
//    public static String dealModifiedByMerchant(Merchant merchant, Coupon coupon, Role userRole, String url) {
//        StringBuilder body = new StringBuilder();
//
//        body.append("Dear, " + merchant.getContactPerson() + "<br/><br/>");
//        if (userRole.equals(Role.Advertiser)) {
//            body.append("You have modified your current deal.<br/>");
//        } else {
//            body.append("Your authorized re-seller has modified your current deal.<br/>");
//        }
//        body.append("The details of your modified deal are:<br/>");
//        body.append("Deal reference number: " + coupon.getId() + "<br/>");
//        body.append("Deal Name: " + coupon.getName() + "<br/> ");
//        body.append("Date of Modification: " + coupon.getModifiedDate() + "<br/><br/>");
//        body.append("Click below to view your deal.<br/>");
//        body.append(getEmailBodyButton("View Your Deal", url));
//        body.append("<br/>");
//        return prepareEmail("", body.toString(), null);
//    }
//
//    public static String dealModifiedByAdmin(Coupon coupon, Advertiser advertiser, String dealAnalyticsUrl, String subject) {
//        StringBuilder body = new StringBuilder();
//
//        body.append("Dear " + advertiser.getContactPerson() + ",<br /><br />");
//        body.append("We have modified your current deal as requested. <br /><br />");
//        body.append("The details of your modified deal are:<br /><br />");
//        body.append("Deal number: " + coupon.getId() + "<br />");
//        body.append("Deal Name: " + coupon.getName() + "<br/>");
//        body.append("Date of Modification: " + coupon.getModifiedDate());
//        body.append("Click below to view deal analytics.<br/>");
//        body.append("<br/>");
//        body.append(getEmailBodyButton("View Deal Analytics", dealAnalyticsUrl));
//        body.append("<br/>");
//
//        return prepareEmail(subject, body.toString(), null);
//    }
//
//    //invoice payment pending mail to admin and advertiser.
//    public static String pendingInvoicePayments(Boolean isAdmin, Coupon coupon, Advertiser advertiser, List<InvoiceDTO> pendingInvoices) {
//        StringBuilder builder = new StringBuilder();
//        builder.append(getHtmlHeader());
//        builder.append("<br/><br/>");
//
//        builder.append("Dear " + advertiser.getContactPerson() + ",<br/><br/>");
//        builder.append("Below are list of invoices whose payment are pending<br/><br/>");
//        builder.append("Date of Payment Processed:" + MessageBundle.getCurrentTimestampSQL() + "<br/>");
//
//        builder.append("<table cellspacing=\"0\" cellpadding=\"10\" border=\"1\">");
//        builder.append("<tr>");
//        builder.append("<th>ID</th>" + "<th>Deal Name</th>" + "<th>Invoice Status</th>" + "<th>Invoice No</th>" + "<th>Sales Amount</th>");
//        builder.append("<th>Service Fee(%)</th>" + "<th>Payable Dealify Service Fee</th>" + "<th>Paid Amount</th>");
//        builder.append("</tr>");
//        int i = 1;
//        BigDecimal totalSalesSum = new BigDecimal(BigInteger.ZERO);
//        BigDecimal totalPayableSum = new BigDecimal(BigInteger.ZERO);
//        BigDecimal totalPaidAmount = new BigDecimal(BigInteger.ZERO);
//
//        for (YTInvoice invoice : pendingInvoices) {
//            if (invoice.getInvoiceStatus().equals(InvoiceStatus.UNPAID)) {
//                totalSalesSum = totalSalesSum.add(invoice.getTotalSales());
//                totalPayableSum = totalPayableSum.add(invoice.getAmount());
//
//            } else if (invoice.getInvoiceStatus().equals(InvoiceStatus.PARTIALLY_PAID)) {
//                totalPaidAmount = totalPaidAmount.add(invoice.getPaidAmount());
//            }
//
//
//            builder.append("<tr>");
//            builder.append("<td>" + i + "</td>");
//            builder.append("<td>" + coupon.getName() + "</td>");
//            builder.append("<td>" + invoice.getInvoiceStatus() + "</td>");
//            builder.append("<td>" + invoice.getInvoiceNo() + "</td>");
//            builder.append("<td>" + CURRENCY + " " + invoice.getTotalSales() + "</td>");
//            builder.append("<td>" + coupon.getCommissionPct() + "</td>");
//            builder.append("<td>" + MessageBundle.roundTwoDecimalPlaces(invoice.getAmount().doubleValue()) + "</td>");
//            builder.append("<td>" + invoice.getPaidAmount() + "</td>");
//            builder.append("</tr>");
//            i++;
//        }
//
//        builder.append("<tr>");
//        builder.append("<td></td>" + "<td></td><td></td>");
//        builder.append("<td> Total </td>");
//        builder.append("<td>" + MessageBundle.roundTwoDecimalPlaces(totalSalesSum.doubleValue()) + "</td>");
//        builder.append("<td></td>");
//        builder.append("<td>" + MessageBundle.roundTwoDecimalPlaces(totalPayableSum.doubleValue()) + "</td>");
//        builder.append("<td>" + MessageBundle.roundTwoDecimalPlaces(totalPaidAmount.doubleValue()) + "</td>");
//        builder.append("/<tr>");
//        builder.append("</table>");
//
//        builder.append("<br/><br/><br/><br/>");
//
//        builder.append("Due Amount :" + CURRENCY + (totalPayableSum.subtract(totalPaidAmount)) + "<br/><br/>");
//
//        builder.append(" All the invoices are attached here with this email for your reference.<br/><br/>");
//        int increment = 1;
//        for (YTInvoice invoice : pendingInvoices) {
//            String filePath = invoice.getPath();
//            builder.append(increment + ") " + "<a href=\"" + filePath + "\" target=\"_blank\" >" + "Invoice No " + invoice.getInvoiceNo() + "</a>");
//            builder.append("<br/>");
//            increment++;
//        }
//
//        builder.append("<br/>");
//        builder.append(" Please reply to this email if there has been an error.<br/><br/>");
//
//        if (isAdmin) {
//            builder.append(getFooterForAdmin());
//        } else {
//            builder.append(getFooter());
//        }
//        return builder.toString();
//    }
//
//    public static String invoiceReceiptMsg(Merchant merchant, Coupon coupon, YTInvoice invoice, String card) {
//        return invoice(merchant, coupon, invoice, card);
//    }
//
//    public static String emailCouponQrCode(ClientCoupon clientCoupon, Client client, Coupon coupon, String subject) {
//        StringBuilder body = new StringBuilder();
//        body.append("<div align='center'>");
//        body.append("<div>");
//        body.append("<img src='" + coupon.getProviderLogo() + "'>");
//        body.append("</div>");
//        body.append("<div>");
//        body.append(coupon.getProviderName() + "<br/>");
//        body.append(coupon.getName() + "<br/>");
//        body.append("</div>");
//        body.append("<div>");
//        body.append("<h3>Print the QR CODE</h3><br/>");
//        body.append("<h3>and show to the merchant</h3>");
//        body.append("</div>  ");
//        body.append("<div>");
//        body.append("<img align='center' src='" + clientCoupon.getQrCodeUrl() + "'> ");
//        body.append("<br/>");
//        body.append("<h3 align='center'>" + clientCoupon.getSerialNo() + "</h3>");
//        body.append("</div>");
//        body.append("</div>");
//        return prepareEmail(subject, body.toString(), null);
//    }
//
//    public static String contactUs() {
//        StringBuilder builder = new StringBuilder();
//        builder.append("<form method='post' action='mailto:Frank@cohowinery.com'>");
//        builder.append("<form method='post' action='" + MessageBundle.getDefaultAdmin() + "'");
//        builder.append("<p>Name: <input type='text' size='65'></p>");
//        builder.append("<p>E-mail Address:  <input type='text' size='65'></p>");
//        builder.append("<p>Telephone: <input type='text' size='65'><br> ");
//        builder.append("<input type='checkbox'> Please do not call me.</p> ");
//        builder.append("<p> What can we help you with?");
//        builder.append("<select type='text' value=''>");
//        builder.append("<option>  </option> ");
//        builder.append("<option>Customer Service</option>");
//        builder.append("<option>Question</option>");
//        builder.append("<option>Comment</option>");
//        builder.append("<option>Consultation</option>");
//        builder.append("<option>Other</option>");
//        builder.append("</select></p>");
//        builder.append("<p>Comments:  <textarea cols='55'> </textarea></p>");
//        builder.append("<p><input type='submit' value='Send' name='submit'><br>");
//        builder.append("<input type='reset' value='Reset' name='reset'></p>");
//        builder.append("</form>");
//        return builder.toString();
//    }
//
//
//    private static String getFooter() {
//        StringBuilder footer = new StringBuilder();
//
//        footer.append("<div>");
//        footer.append("<br />Sincerely,<br />");
//
//        String compName = SystemPropertyManager.readPrefValue(PreferenceType.COMPANY_NAME);
////        if (compName.contains(" "))
////            compName = compName.substring(0, compName.indexOf(" "));
//
//        String address = SystemPropertyManager.readPrefValue(PreferenceType.COMPANY_ADDRESS);
//        String contact = SystemPropertyManager.readPrefValue(PreferenceType.COMPANY_CONTACT_NO);
//        String website = SystemPropertyManager.readPrefValue(PreferenceType.COMPANY_WEBSITE);
//        String vatNo = SystemPropertyManager.readPrefValue(PreferenceType.COMPANY_VAT_NO);
//
//        footer.append(COMPANY_NAME + " Team " + "<br/><br /><br />");
//        //  footer.append("Discover.Shop.Earn." + "<br/><br />");
//
//        footer.append("<p><font color='gray'> The information here in transmitted is intended only for the person or entity " +
//                "to which it is addressed and may contain confidential and/or privileged material. " +
//                "If you are not the intended recipient, you should delete the material from your system and contact " +
//                "the sender. E-mail may be susceptible to data corruption, interception and unauthorised amendment. " +
//                "We do not accept liability for any such corruption, interception, amendment or the consequences thereof. " +
//                "If in doubt, please verify the authenticity of the contents with the sender.</font></p><br/><br/>");
//
//        footer.append("<font color='gray'>" + compName + ", " +
//                address + ", " + contact + ", " + website +
//                ", Vat No. : " + vatNo + "</font>");
//
//        footer.append("</div></body></html>");
//
//        return footer.toString();
//    }
//
//    private static String getFooterForAdmin() {
//        StringBuilder footer = new StringBuilder();
//        footer.append("<div>");
//
//        String compName = SystemPropertyManager.readPrefValue(PreferenceType.COMPANY_NAME);
//        if (compName.contains(" "))
//            compName = compName.substring(0, compName.indexOf(" "));
//
//        footer.append(compName + "<br />");
//        footer.append("</div>");
//        return footer.toString();
//    }
//
//    private static String getHtmlHeader() {
//        String htmlHead = "<html><head><title>Welcome to " + COMPANY_NAME + "</title>" +
//                "<style>" + style.toString() + "</style>" + "<link rel='stylesheet' " + "href='" + MainServlet.serverUrl + "/deal/fonts/font.css'>" +
//                "</head><body>";
//        return htmlHead;
//    }
//
//    private static String getAdminHtmlHeader() {
//        String htmlHead = "<html><head><title> " + COMPANY_NAME + "</title>" +
//                "<style>" + style.toString() + "</style></head><body>";
//        return htmlHead;
//    }
//
//    //------------------------------------------------------------------------- EMAIL DESIGN ACCORDING TO NEW TEMPLATE FROM HERE ----------------------------------------------------
//
//    public static String qrCodeEmail(Coupon coupon, ClientCoupon clientCoupon, Merchant advertiser, List<TargetRedeemDate> targetRedeemDates, List<TargetRedeemLocation> targetRedeemLocations) {
//        StringBuilder body = new StringBuilder();
//        body.append(getHtmlHeader());
//        body.append(" <table width='100%;' align='center' border='0' style='background:#DCDCDC' cellspacing='0px' cellpadding='20px'>");
//        body.append(" <tr>");
//        body.append(" <td><table width='700' border='0' cellspacing='0px' cellpadding='30' align='center' style='background: #FFFFFF;border-radius:15px;color:#626262; overflow: hidden; border: 1px solid #E0E0E0;'> ");
//        body.append(" <tr> ");
//        body.append(" <td colspan='2'><img src='https://s3.amazonaws.com/DealifyLogo/logo.png' width='153' height='64' alt='Dealify Logo' style='padding:10px 10px;'/></td> ");
//        body.append(" </tr>");
//        body.append(" <tr style='background-color: #F5F5F5;'>");
//        body.append(" <td style='width:100px; border-top: 1px solid #E0E0E0;'><img src='" + coupon.getProviderLogo() + "' style='display:block; width:100%; height: auto; border-radius: 5px; border: 1px solid #E0E0E0' /></td>");
//        body.append(" <td style='border-top: 1px solid #E0E0E0; padding-left: 0px;'>");
//        body.append(" <p style='font-size: 22px; margin: 0px 0px 10px 0px'>");
//        body.append(coupon.getOfferName());
//        body.append(" <br/>");
//        body.append(" ");
//        body.append(" </p> ");
//        body.append(" <p style='color: #999; margin: 0px;'>" + coupon.getProviderName() + "</p></td>");
//        body.append(" </tr>");
//        body.append(" <tr> ");
//        body.append(" <td align='center' colspan='2' style='border-top: 1px solid #E0E0E0;'> ");
//        body.append("");
//        body.append(" <table width='100%' border='0px' cellpadding='0px' cellspacing='0px'>");
//        body.append(" <tr align='center'>");
//        body.append(" <td width='250px;'>");
//
//        if(coupon.getType().equals(CouponType.PHYSICAL_STORE)) {
//            if(coupon.getPromoCode()!=null && !coupon.getPromoCode().isEmpty()) {
//                body.append(" <p style='margin: 0px 0px 5px 0px; color: #999999;'>Show this code to Merchant</p>");
//                body.append(" <p style='margin: 0px;'><b>Coupon Code : "+coupon.getPromoCode()+ "</b></p>");
//            } else {
//                body.append(" <p style='margin: 0px; color: #999999;'>Show this code to Merchant</p>");
//            }
//
//            body.append(" <p style='margin: 0px;'><img src='" + clientCoupon.getQrCodeUrl() + "' style='width: 200px; height:auto;'/></p>");
//            body.append(" <p style='font-size:26px; margin: 0px 0px 10px 0px; font-weight: bold; letter-spacing: 2px;'>" + clientCoupon.getSerialNo() + "</p>");
//        }
//
//        body.append(" <p style='font-size:16px; margin: 0px 0px 10px 0px; letter-spacing: 2px;'>" + coupon.getRedeemInstruction() + "</p>");
//
//        if(coupon.getType().equals(CouponType.ONLINE_STORE)) {
//            body.append(" <p style='margin: 0px; font-size: 18px; font-weight: bold;'><b>Coupon Code : "+coupon.getPromoCode()+ "</b></p>");
//            body.append(" <p style='margin: 0px;'><a href='" + coupon.getThirdPartyUrl() + "' style='width: 200px; height:auto; font-size:13px; margin-top: 20px;'/>"+coupon.getThirdPartyUrl()+"</a></p>");
//        }  else if (coupon.getType().equals(CouponType.CONTEST))  {
//            body.append(" <span style='margin:0 auto; width: 90px'><img src='" + CLOUD_FRONT + "//email_icons/Time.png' style='width: 100%; height:auto;'/></span>");
//        }
//
//
//        if(!coupon.getType().equals(CouponType.CONTEST)) {
//            String use = coupon.getRedemptionLimitPerUser() - clientCoupon.getCouponUsedCount() == 1 ? "Single Use" : "";
//            body.append(" <p style='margin: 0px;'>" + use + "</p>");
//        }
//        body.append(" </td>");
//        body.append(" <td style='padding-left: 20px;'>");
//        body.append("");
//        body.append(" <table width='100%' border='0px' cellpadding='15px' cellspacing='0px' style='font-size: 14px; color: #707070;'>");
//        body.append(" <tr>");
//        body.append(" <td align='center' valign='top' width='40' style='padding: 15px 25px 0px 0px;'><img src='" + CLOUD_FRONT + "/email_icons/Time.png' style='max-width: 100%; height: auto;' /></td>");
//        body.append(" <td style='padding-left: 0px; padding-bottom: 0px;'>");
//
//        if (targetRedeemDates != null || !targetRedeemDates.isEmpty()) {
//            body.append(" <p style='margin: 0px; font-weight: bold;'>VALID UPTO :</p>");
//            body.append("<p style='margin: 0px 0px 10px 0px;'>" + coupon.getEndDate() + "</p>");
//        } else {
//            body.append(" <p style='margin: 0px; font-weight: bold;'>VALID ON :</p>");
//            body.append("<p style='margin: 0px 0px 10px 0px;'> ");
//            for (TargetRedeemDate t : targetRedeemDates) {
//                body.append(t.getDate() + " ");
//            }
//            body.append("</p>");
//        }
//        if(coupon.getType().equals(CouponType.PHYSICAL_STORE)) {
//            body.append(" <p style='margin: 0px; font-weight: bold;'>REDEEM TIME :</p>");
//            body.append(" <p style='margin: 0px;'>" + coupon.getRedeemTime() + "</p> ");
//        }
//        body.append(" </td>");
//        body.append(" </tr>");
//        if(coupon.getType().equals(CouponType.PHYSICAL_STORE)) {
//            body.append(" <tr>");
//            body.append(" <td align='center' valign='top' style='padding: 15px 25px 0px 0px;'><img src='" + CLOUD_FRONT + "/email_icons/location_letter.png' style='max-width: 100%; height: auto;' /></td>");
//            body.append(" <td style='padding-left: 0px; padding-bottom: 0px;'> ");
//            body.append(" <p style='margin: 0px; font-weight: bold;'>REDEEM LOCATION :</p>");
//            body.append(" <p style='margin: 0px;'>");
//            if (targetRedeemLocations.size() == 1) {
//                for (TargetRedeemLocation t : targetRedeemLocations) {
//                    body.append(t.getLocationName() + ", " + t.getStreet() + ", " + t.getCity());
//                }
//            } else {
//                body.append("Multiple locations");
//            }
//            body.append("</p>");
//            body.append(" </td>");
//            body.append(" </tr>");
//        }
//        body.append(" <tr>");
//        body.append(" <td align='center' valign='top' style='padding: 15px 25px 15px 0px;'><img src='" + CLOUD_FRONT + "/email_icons/Call_Letter.png' style='max-width: 100%; height: auto;' /></td>");
//        body.append(" <td style='padding-left: 0px; padding-bottom: 15px;'>");
//        body.append(" <p style='margin: 0px; font-weight: bold;'>FOR MORE INFO :</p>");
//        String viewUrl = MainServlet.serverUrl + "/deal/?id=" + coupon.getId();
//        body.append(" <p style='margin: 0px;'><a href='" + viewUrl + "' style='text-decoration: none; color: #999999;'>" + viewUrl + "</a></p>");
//        body.append(" </td> ");
//        body.append(" </tr>");
//        if(coupon.getType().equals(CouponType.PHYSICAL_STORE)) {
//            body.append(" <tr> ");
//            body.append(" <td style='padding-left: 0px;' colspan='2'>");
//            body.append(" <p style='margin: 0px; font-weight: bold;'>NOTES :</p>");
//            body.append(" <p style='margin: 0px;'>");
//            body.append(" <ul style='margin: 0px; padding-left: 15px;'>");
//            body.append(" <li>Get your QR code scanned by the merchant's cashier and get bonus points.</li>");
//            if(!coupon.getType().equals(CouponType.CONTEST)) {
//                int useCountRemaining = coupon.getRedemptionLimitPerUser() - clientCoupon.getCouponUsedCount() > 0 ? coupon.getRedemptionLimitPerUser() - clientCoupon.getCouponUsedCount() : 0;
//                body.append(" <li>You can reuse this QR code to redeem this deal for " + useCountRemaining + " times</li>");
//            }
//            body.append(" <li>Deal is valid only for the mentioned date and time.</li>");
//            body.append(" <li>Please check latest deal status online.</li>");
//            body.append(" </ul>");
//            body.append(" </p>");
//            body.append(" </td> ");
//            body.append(" </tr>");
//        }
//        body.append(" </table>");
//        body.append(" </td>");
//        body.append(" </tr>");
//        body.append(" </table>");
//        body.append(" </td> ");
//        body.append(" </tr> ");
//        body.append(" </table></td>");
//        body.append(" </tr>");
//        body.append(" <tr>");
//        body.append(" <td><table width='700' border='0' cellspacing='0px' cellpadding='20' align='center' style='background:#F5F5F5;border-radius:15px;color:#999999; overflow: hidden; border: 1px solid #E0E0E0;'>");
//        body.append(" <tr align='center' >");
//        body.append(" <td style='background-color: #FFFFFF; border-bottom: 1px solid #E0E0E0;'><strong>DEALIFY IS AVAILABLE FOR</strong></td> ");
//        body.append(" </tr>");
//        body.append(" <tr>");
//        body.append(" <td style='padding-bottom: 0px;'><table cellpadding='5px' align='center' width='100%'>");
//        body.append(" <tr style='text-align:center;'>");
//        body.append(" <td align='center' width='100%' colspan='3'><div style='width:47%; margin:0 auto; align:center;'><a href='#' style='text-decoration: none; float: left; color: #999999;'><img src='https://myswiprtests3.s3.amazonaws.com/email_icons/iphone_final.png' style='padding: 10px 20px;display:block;'>i-phone</a>"+
//        " <a href='https://play.google.com/store/apps/details?id=com.yetistep.dealify' style='text-decoration: none; float: left; color: #999999;'><img src='https://myswiprtests3.s3.amazonaws.com/email_icons/android_final.png' style='padding: 10px 20px;display:block;'>Android</a>"+
//        " <a href='"+viewUrl+"' style='text-decoration: none;  float: left; color: #999999;'><img src='https://myswiprtests3.s3.amazonaws.com/email_icons/desktop_final.png' style='padding: 10px 20px;display:block;'>Desktop</a></div></td>");
//        body.append(" </tr>");
//        body.append(" </table></td>");
//        body.append(" </tr>");
//        body.append(" <tr> ");
//        body.append(" <td align='center'><a href=''><img src='https://myswiprtests3.s3.amazonaws.com/email_icons/facebook_color.png' style='padding: 0px 10px;'></a><a href=''><img src='https://myswiprtests3.s3.amazonaws.com/email_icons/twitter_color.png' style='padding: 0px 10px;'></a><a href=''><img src='https://myswiprtests3.s3.amazonaws.com/email_icons/google_plus_color.png' style='padding: 0px 10px;'></a></td>");
//        body.append(" </tr> ");
//        body.append(" </table>");
//        body.append(" <p style='text-align: center;'><a href='http://www.mydealify.com/privacy.jsp' style='text-decoration: none; color: #999999; font-weight: bold;'>Privacy Policy</a></p></td>");
//        body.append(" </tr> ");
//        body.append(" </table> ");
//        body.append(" </body>");
//        body.append(" </html>");
//
//        return body.toString();
//    }
//
//
//    public static String webUserNotification(String url, Client client, Coupon coupon, String subject) {
//        StringBuilder body = new StringBuilder();
//        body.append("Hello " + client.getName() + ",");
//        body.append("Thank you for engaging with deal " + coupon.getName() + " at Dealify.<br/>");
//        body.append("For redeeming exciting deals and offer with ease , please get our mobile app from the google app store.<br/>");
//        //   body.append("<a href='" + url + "'>Get the app now from play store.<br/>");
//        body.append(getEmailBodyButton("Get App", url));
//        body.append("<br/>");
//        return prepareClientEmail(subject, body.toString(), null);
//    }
//
//    public static String userReminderEmail(String url, Client client, String subject) {
//        StringBuilder body = new StringBuilder();
//        body.append("You've been gone for a while. Let us remind you that you have unlocked deals.<br/>");
//        body.append(" Redeem them now to enjoy exciting offers and great savings via Dealify.<br/>");
//        body.append("Log on to Dealify to know more about new interesting deals and offers.<br/>");
//        body.append(" Your patronage and support are important to us. We look forward to seeing you soon.<br/>");
//        body.append("Click on the link to download the app play store.");
//        body.append("<br/><br/>");
//        return prepareClientEmail(subject, body.toString(), null);
//    }
//
//    public static String invoicePaymentFailure(Coupon coupon, Merchant merchant, String subject) {
//        StringBuilder body = new StringBuilder();
//        body.append("Hello " + merchant.getContactPerson() + ",<br/>");
//        body.append("We were unable to process the payment for your deal . Please check your credit card status.");
//        body.append("<br/><br/>");
//        return prepareEmail(subject, body.toString(), null);
//    }
//
//    public static String finalInvoicePaymentFailure(Coupon coupon, Merchant merchant, String subject) {
//        StringBuilder body = new StringBuilder();
//        body.append("Hello " + merchant.getContactPerson() + ",<br/><br/>");
//        body.append("Sorry , even after three attempt to charge your credit card for the monthly subscription fee ,we were unable to process the payment for your deal .<br/>");
//        body.append("We regret to inform you that we have to discontinue your deal.<br/>");
//        body.append("Please check your credit card status and try uploading a new deal.");
//        body.append("<br/><br/>");
//        return prepareEmail(subject, body.toString(), null);
//    }
//
//    public static String advertiserCouponCreate(String viewUrl, String cashierAddUrl, String merchantDashBoardUrl, Merchant advertiser, Coupon coupon, String subject) {
//        StringBuilder body = new StringBuilder();
//
//        body.append("Dear " + advertiser.getContactPerson() + ",<br/><br/>");
//        body.append("Thank you for submitting your deal.<br/>");
//        body.append("Your deal " + coupon.getName() + " is live now and can be viewed from the link below.<br/>");
//        body.append(getEmailBodyButton("View Deal", viewUrl));
//
//        body.append("<br/>Please add a cashier who will validate and redeem each deal.<br/>");
//        body.append(getEmailBodyButton("Add Cashier", cashierAddUrl));
//
//        body.append("If the link does not work, copy & paste the following url in the browser: <br />");
//        body.append(viewUrl);
//        body.append("<br/><br/>You can track, monitor and analyse the performance of your deal from our merchant dashboard.<br/>");
//        body.append(getEmailBodyButton("Merchant dashboard", merchantDashBoardUrl));
//        body.append("Thanks again for your interest in Dealify and good luck!");
//        body.append("<br/><br/>");
//        return prepareEmail(subject, body.toString(), null);
//    }
//
//    public static String resellerCouponCreate(String viewUrl, String cashierAddUrl, Merchant reseller, Advertiser advertiser, Coupon coupon, String subject) {
//        StringBuilder body = new StringBuilder();
//
//        body.append("Dear " + advertiser.getContactPerson() + ",<br/><br/>");
//        body.append(" Your deal has been submitted by " + reseller.getContactPerson() + ".<br/>");
//        body.append("Your deal " + coupon.getName() + " is now live and can be viewed from the link below. <br/>");
//        body.append(getEmailBodyButton("View Deal", viewUrl));
//        body.append("If the above link does not work,please copy & paste the following link in your browser:<br/>");
//        body.append(viewUrl);
//        body.append("<br/>Please add a cashier who will validate and redeem each deal.<br/>");
//        body.append(getEmailBodyButton("Add Cashier", cashierAddUrl));
//        body.append("Thanks again for your interest in Dealify and good luck! ");
//        body.append("<br/><br/>");
//        return prepareEmail(subject, body.toString(), null);
//    }
//
//
//    public static String merchantSignUpByReseller(Reseller reseller, Advertiser advertiser, String url, String subject, String loginUrl) {
//        StringBuilder body = new StringBuilder();
//        body.append("Dear " + advertiser.getContactPerson() + ",<br/><br/>");
//        body.append("Welcome & thank you for joining Dealify.<br/><br/>");
//        body.append("Your account has been created by " + reseller.getName() + "<br/><br/>");
//        body.append(" Details are as follows:<br/>");
//        body.append("Your username: " + advertiser.getContactEmail() + "<br/><br/>");
//        body.append("Please click on the link to create password. <br/>");
//        body.append(getEmailBodyButton("Create Password", url));
//        body.append("If the above link does not work, please copy & paste the following URL in your browser:<br/>");
//        body.append(url + "<br/><br/>");
//        body.append("You can view all the deals & their analytics after logging in to the merchant dashboard.<br/><br/>");
//        body.append("In case of any queries please contact the re-seller.<br/>");
//        body.append(getEmailBodyButton("Login", loginUrl));
//        body.append("<br/><b>Re-seller Information:</b><br/>");
//        body.append("Contact person Name: " + reseller.getContactPerson() + "<br/>");
//        body.append("Company Name: " + reseller.getName() + "<br/>");
//        body.append("Email Address: " + reseller.getContactEmail() + "<br/>");
//        body.append("Phone No: " + reseller.getPhoneNo());
//        body.append("<br/><br/>");
//        return prepareEmail(subject, body.toString(), null);
//    }
//
//    public static String merchantSignUp(BusinessOwner merchant, String url, String merchantt) {//for advertiser,reseller sign up
//        StringBuilder bodyContent = new StringBuilder();
//        bodyContent.append("<br/> Dear " + merchant.getContactPerson() + ",<br /><br />");
//        bodyContent.append("Welcome & thank you for joining " + COMPANY_NAME + ".<br/>");
//        bodyContent.append("<br/>Your account details are as follows:<br/>");
//        bodyContent.append("Your username: " + merchant.getContactEmail() + "<br /><br />");
//        bodyContent.append("Please click on the link to verify your account:<br/> ");
//        bodyContent.append(getEmailBodyButton("Verify My Account", url));
//        bodyContent.append("If the above link does not work, please copy & paste the following url in your browser: <br />");
//        bodyContent.append(url);
//        bodyContent.append("<br /><br/>");
//        if (merchantt.equals("Advertiser")) {
//            bodyContent.append("Your account will be activated after verification.");
//        } else {
//            bodyContent.append("Your account will be activated within 24hrs after verification.");
//        }
//        bodyContent.append("<br/>");
//
//        return prepareEmail("", bodyContent.toString(), null).toString();
//    }
//
//    public static String advertiserRequestToReseller(String advertiserUrl, Reseller reseller, ResellerAdvertiser resellerAdvertiser, String subject) {
//        StringBuilder body = new StringBuilder();
//        body.append("Hi " + reseller.getName() + ", <br/><br/>");
//        body.append(resellerAdvertiser.getContactPerson() + "  has chosen you as their re-seller for Dealify.<br/><br/>");
//        body.append("Please contact them and assist them.<br/><br/>");
//        body.append("Click below to view their information<br/>");
//        body.append(getEmailBodyButton("View Info", advertiserUrl));
//        return prepareEmail(subject, body.toString(), null);
//    }
//
//    public static String prepareEmail(String subjectContent, String bodyContent, String userHeadContent) {
//        StringBuilder builder = new StringBuilder();
//        builder.append(getHtmlHeader());
//        builder.append("<table width='900px;' align='center' border='0' style='background:#DCDCDC' cellspacing='20px' cellpadding='10px'><tr><td>");
//        builder.append("<table width='700' border='0' cellspacing='0px' cellpadding='30' align='center' style='background:white;border-radius:15px;color:#626262'>");
//        builder.append(getEmailHead(userHeadContent));
//        // builder.append(getEmailSubject(subjectContent));
//        builder.append(getEmailBody(bodyContent));
//        builder.append(getEmailTableFooter());
//        builder.append("</table></td></tr>");
//        builder.append("<tr><td>");
//        builder.append(getEmailFooter());
//        builder.append("</td></tr>");
//        builder.append(getLinkFooter());
//        builder.append("</table>");
//        builder.append(getHtmlClosing());
//        return builder.toString();
//    }
//
//    public static String prepareAdminEmail(String subjectContent, String bodyContent, String userHeadContent) {
//        StringBuilder builder = new StringBuilder();
//        builder.append(getAdminHtmlHeader());
//        builder.append("<table width='900px;' align='center' border='0' style='background:#EAEAEA' cellspacing='20px' cellpadding='10px'><tr><td>");
//        builder.append("<table width='700' border='0' cellspacing='0px' cellpadding='30' align='center' style='background:white;border-radius:15px;color:#878787'>");
//        builder.append(getEmailHead(userHeadContent));
//        // builder.append(getEmailSubject(subjectContent));
//        builder.append(getEmailBody(bodyContent));
//        builder.append(getFooterForAdmin());
//        builder.append(getHtmlClosing());
//        builder.append("</table></td></tr></table>");
//        return builder.toString();
//    }
//
//    public static String prepareClientEmail(String subjectContent, String bodyContent, String userHeadContent) {
//        StringBuilder builder = new StringBuilder();
//        builder.append(getHtmlHeader());
//        builder.append("<table width='900px;' align='center' border='0' style='background:#DCDCDC' cellspacing='20px' cellpadding='10px'><tr><td>");
//        builder.append("<table width='700' border='0' cellspacing='0px' cellpadding='30' align='center' style='background:white;border-radius:15px;color:#626262'>");
//        builder.append(getEmailHead(userHeadContent));
//        // builder.append(getEmailSubject(subjectContent));
//        builder.append(getEmailBody(bodyContent));
//        builder.append(getClientEmailTableFooter());
//        builder.append("</table></td></tr>");
//        builder.append("<tr><td>");
//        builder.append(getEmailFooter());
//        builder.append("</td></tr>");
//        builder.append(getLinkFooter());
//        builder.append("</table>");
//        builder.append(getHtmlClosing());
//        return builder.toString();
//    }
//
//    private static String getEmailBodyButton(String viewLink, String url) {
//        StringBuilder builder = new StringBuilder();
//        builder.append("<a href='" + url + "'  style='background:#52B4F3; padding: 10px 20px; color: #FFFFFF; text-decoration: none; display: inline-block;; border-radius: 3px; margin: 10px 0px;'>" + viewLink + "</a>");
//        builder.append("<br clear='all'/>");
//        return builder.toString();
//    }
//
//    private static String getEmailHead(String userHeaderContent) {
//        StringBuilder builder = new StringBuilder();
//        builder.append("<tr>");
//        builder.append("<td><img src='" + CLOUD_FRONT + "/DealifyLogo/logo.png' width='153' height='64' alt='Dealify Logo' style='padding:10px 10px;'/></td>");
//        if (userHeaderContent != null) {
//            builder.append("<td>" + userHeaderContent + "</td>");
//        }
//        builder.append("</tr>");
//        return builder.toString();
//    }
//
//    private static String getEmailBody(String bodyContent) {
//        StringBuilder builder = new StringBuilder();
//        builder.append("<tr style='background:#F5F5F5;color:#626262'>");
//        builder.append("<td>");
//        builder.append(bodyContent);
//        builder.append("</td>");
//        builder.append("</tr>");
//        return builder.toString();
//    }
//
//    private static String getEmailSubject(String subjectContent) {
//        StringBuilder builder = new StringBuilder();
//        builder.append("<tr>");
//        builder.append("<td style='background-color:#24B8E5;height:180px;'><p style='color:white;text-align:justify;font-size:30px;'>"
//                + subjectContent + "</p>");
//        builder.append("</td>");
//        builder.append("</tr>");
//        return builder.toString();
//    }
//
//    private static String getClientEmailTableFooter() {
//        StringBuilder footer = new StringBuilder();
//
//        footer.append("<tr>");
//        footer.append("<td>");
//        footer.append("Regards,<br />");
//        footer.append(COMPANY_NAME + " Team " + "<br/><br />");
//        footer.append("</td>");
//        footer.append("</tr>");
//        return footer.toString();
//    }
//
//    private static String getEmailTableFooter() {
//        StringBuilder footer = new StringBuilder();
//
//        footer.append("<tr style='background:#F5F5F5'>");
//        footer.append("<td>");
//        footer.append("Sincerely,<br />");
//
//        String compName = SystemPropertyManager.readPrefValue(PreferenceType.COMPANY_NAME);
////        if (compName.contains(" "))
////            compName = compName.substring(0, compName.indexOf(" "));
//
//        String address = SystemPropertyManager.readPrefValue(PreferenceType.COMPANY_ADDRESS);
//        String contact = SystemPropertyManager.readPrefValue(PreferenceType.COMPANY_CONTACT_NO);
//        String website = SystemPropertyManager.readPrefValue(PreferenceType.COMPANY_WEBSITE);
//        String vatNo = SystemPropertyManager.readPrefValue(PreferenceType.COMPANY_VAT_NO);
//
//        footer.append(COMPANY_NAME + " Team " + "<br/><br />");
//        //  footer.append("Discover.Shop.Earn." + "<br/><br />");
//
////        footer.append("<p width='600px;'><font color='gray' style='text-align:justify;'> The information here in transmitted is intended only for the person or entity" +
////                "to which it is addressed and may contain confidential and/or privileged material. " +
////                "If you are not the intended recipient, you should delete the material from your system and contact " +
////                "the sender. E-mail may be susceptible to data corruption, interception and unauthorised amendment." +
////                "We do not accept liability for any such corruption, interception, amendment or the consequences thereof. " +
////                "If in doubt, please verify the authenticity of the contents with the sender.</font></p><br/><br/>");
//
////        footer.append("<font color='gray'>" + compName + ", " +
////                address + ", " + contact + ", " + website +
////                ", Vat No. : " + vatNo + "</font>");
//
//        footer.append("</td>");
//        footer.append("</tr>");
//        return footer.toString();
//    }
//
//    private static String getEmailFooter() {
//        StringBuilder builder = new StringBuilder();
//        builder.append("<table align='center' border='0' cellspacing='0' cellpadding='20' width='700px;'" +
//                " style='background:#F5F5F5;border-radius:15px;color:#878787;width:700'>");
//        builder.append("<tr align='center' style='background: #FFFFFF;'>");
//        builder.append("<td colspan='3' style='padding:20px;'><strong>DEALIFY IS AVAILABLE FOR</strong></td>");
//        builder.append("</tr>");
//        builder.append("<tr align='center'>");
//        builder.append("<td>");
//        builder.append("<table cellpadding='5px'>");
//        builder.append("<tr style='text-align:center;'>");
//        builder.append("<td><a href=''><img src='" + CLOUD_FRONT + "/email_icons/iphone_final.png' style='padding: 0px 20px;display:block;'>i-phone</a></td>");
//        builder.append("<td><a href='https://play.google.com/store/apps/details?id=com.yetistep.dealify'><img src='" + CLOUD_FRONT + "/email_icons/android_final.png' style='padding: 0px 20px;display:block;'>Android</a></td>");
//        builder.append("<td><a href=''><img src='" + CLOUD_FRONT + "/email_icons/desktop_final.png' style='padding: 0px 20px;display:block;'>Desktop</a></td>");
//        builder.append("</tr></table");
//        builder.append("</td>");
//        builder.append("</tr>");
//
//        builder.append("<tr align='center' style='background:#F5F5F5'>");
//        builder.append("<td colspan='3' style='padding-bottom:20px'>");
//        builder.append("<a href=''><img src='" + CLOUD_FRONT + "/email_icons/facebook_color.png' style='padding: 0px 10px;'></a>");
//        builder.append("<a href=''><img src='" + CLOUD_FRONT + "/email_icons/twitter_color.png' style='padding: 0px 10px;'></a>");
//        builder.append("<a href=''><img src='" + CLOUD_FRONT + "/email_icons/google_plus_color.png' style='padding: 0px 10px;'></a>");
//        builder.append("</td>");
//        builder.append("</tr>");
//        builder.append("</table>");
//        return builder.toString();
//    }
//
//    private static String getUnscribeLinkFooter() {
//        StringBuilder builder = new StringBuilder();
//        builder.append("<tr align='center'>");
//        builder.append("<td><a href=''>Unsubscribe</a> | <a href=''>Privacy Policy</a></td>");
//        builder.append("</tr>");
//        return builder.toString();
//    }
//
//    private static String getLinkFooter() {
//        StringBuilder builder = new StringBuilder();
//        builder.append("<tr align='center'>");
//        builder.append("<td><a href='http://www.mydealify.com/privacy.jsp'>Privacy Policy</a></td>");
//        builder.append("</tr>");
//        return builder.toString();
//    }
//
//    private static String getHtmlClosing() {
//        StringBuilder builder = new StringBuilder();
//        builder.append("</body>");
//        builder.append("</html>");
//        return builder.toString();
//    }
}
