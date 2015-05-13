package com.yetistep.delivr.util;

import com.yetistep.delivr.dto.PaymentGatewayDto;
import com.yetistep.delivr.model.PaymentGatewayInfoEntity;

import java.security.MessageDigest;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 4/17/15
 * Time: 12:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class SHAEncoder {

    private static final String CURRENCY_CODE = MessageBundle.getPaymentGatewayMsg("currencyCode");
    private static final String KEY_VERSION = MessageBundle.getPaymentGatewayMsg("keyVersion");
    private static final String CUSTOMER_LANGUAGE = MessageBundle.getPaymentGatewayMsg("customerLanguage");
    private static final String INTERFACE_VERSION = MessageBundle.getPaymentGatewayMsg("interfaceVersion");

    private static final String MERCHANT_ID = System.getProperty("DELIVR_MERCHANT_GATEWAY_ID");
    private static final String MERCHANT_SECRET_KEY = System.getProperty("DELIVR_MERCHANT_SECRET_KEY");
    private static final String RETURN_URL = System.getProperty("DELIVR_PG_RESPONSE_URL");
    private static final String PG_REQUEST_URL = System.getProperty("DELIVR_PG_REQUEST_URL");

    private static final String NORMAL_RETURN_URL_NAME = "normalReturnUrl";
    private static final String AUTOMATIC_RETURN_URL_NAME = "automaticResponseUrl";
    private static final String KEY_VERSION_NAME = "keyVersion";
    private static final String CUSTOMER_LANGUAGE_NAME = "customerLanguage";

    public static final String MERCHANT_ID_NAME = "merchantId";
    public static final String MERCHANT_GATEWAY_ID_NAME = "merchantGatewayId";
    public static final String CURRENCY_CODE_NAME = "currencyCode";
    public static final String ORDER_ID_NAME = "orderId";
    public static final String AMOUNT_NAME = "amount";
    public static final String TRANSACTION_REFERENCE_NAME = "transactionReference";
    public static final String RESPONSE_CODE = "responseCode";
    public static final String SUCCESSFUL_RESPONSE_CODE = "00";

    private static final String SEPARATOR = "|";
    private static final String EQUALS = "=";

    /**
     * table to convert a nibble to a hex char.
     */
    static final char[] hexChar = {
            '0', '1', '2', '3',
            '4', '5', '6', '7',
            '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f'};

    /**
     * Fast convert a byte array to a hex string
     * with possible leading zero.
     *
     * @param b array of bytes to convert to string
     * @return hex representation, two chars per byte.
     */
    public static String encodeHexString(byte[] b) {
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            // look up high nibble char
            sb.append(hexChar[(b[i] & 0xf0) >>> 4]);

            // look up low nibble char
            sb.append(hexChar[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    /**
     * Computes the seal
     *
     * @param Data      the parameters to cipher
     * @param secretKey the secret key to append to the parameters
     * @return hex representation of the seal, two chars per byte.
     */
    public static String computeSeal(String Data, String secretKey) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        md.update((Data + secretKey).getBytes("UTF-8"));

        return encodeHexString(md.digest());
    }

    public static String computeSeal(String Data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update((Data + MERCHANT_SECRET_KEY).getBytes("UTF-8"));
        return encodeHexString(md.digest());
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            System.out.println(computeSeal("amount=1000|currencyCode=356|merchantId=002020000000001|" +
                    "automaticResponseUrl=http://localhost:8080/client/transactions/pgresponse|" +
                    "keyVersion=1|customerLanguage=en|transactionReference=53465562", "002020000000001_KEY1"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PaymentGatewayDto getPaymentRequestData(PaymentGatewayInfoEntity paymentGatewayInfoEntity) throws Exception {
        PaymentGatewayDto paymentGatewayDto = new PaymentGatewayDto();
        String data = generateData(paymentGatewayInfoEntity);
        paymentGatewayDto.setData(data);
        paymentGatewayDto.setInterfaceVersion(INTERFACE_VERSION);
        paymentGatewayDto.setPGRequestURL(PG_REQUEST_URL);
        paymentGatewayDto.setSeal(computeSeal(data, MERCHANT_SECRET_KEY));
        return paymentGatewayDto;
    }

    private static String generateData(PaymentGatewayInfoEntity paymentGatewayInfoEntity) {
        String data = AMOUNT_NAME + EQUALS + paymentGatewayInfoEntity.getInrAmount()
                + SEPARATOR
                + CURRENCY_CODE_NAME + EQUALS + CURRENCY_CODE
                + SEPARATOR
                + MERCHANT_ID_NAME + EQUALS + MERCHANT_ID
                + SEPARATOR
                + NORMAL_RETURN_URL_NAME + EQUALS + RETURN_URL
                + SEPARATOR
                + AUTOMATIC_RETURN_URL_NAME + EQUALS + RETURN_URL
                + SEPARATOR
                + KEY_VERSION_NAME + EQUALS + KEY_VERSION
                + SEPARATOR
                + CUSTOMER_LANGUAGE_NAME + EQUALS + CUSTOMER_LANGUAGE
                + SEPARATOR
                + TRANSACTION_REFERENCE_NAME + EQUALS + paymentGatewayInfoEntity.getTransactionReference()
                + SEPARATOR
                + ORDER_ID_NAME + EQUALS + paymentGatewayInfoEntity.getId();
        return data;
    }

    public static String getResponseHTML(String msg, Boolean success){
        String colorCode = "red";
        if(success)
            colorCode = "green";
        return "<html><title>"+colorCode+"</title><h3 style='color: "+colorCode+"'>"+msg+"</h3>"
                + "<div>Click OK to continue</div>" +
                "<div><button onclick='ok.performClick(this.value);'>OK</button></div></html>";
    }

}



