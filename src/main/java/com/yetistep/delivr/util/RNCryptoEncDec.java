package com.yetistep.delivr.util;

import org.cryptonode.jncryptor.AES256JNCryptor;
import org.cryptonode.jncryptor.JNCryptor;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.util.StringTokenizer;

/**
 * Created by golcha on 4/15/14.
 */
public class RNCryptoEncDec {
    static String password = "password";

    private void showByte(byte[] bytes) {
        for (int j = 1; j < bytes.length + 1; j++) {
            System.out.format("%02X", bytes[j - 1]);
            if (j % 4 == 0) System.out.format(" ");
        }
    }

    public static String encrypt(String message) throws Exception {

        String encryptedString = null;
        JNCryptor cryptor = new AES256JNCryptor();
        byte[] plaintextbyte = message.getBytes();

        byte[] ciphertext = cryptor.encryptData(plaintextbyte, password.toCharArray());
        String secretKeyStr = new BASE64Encoder().encode(ciphertext);
        //System.out.println(secretKeyStr.replaceAll("\0",""));
        encryptedString = secretKeyStr.replaceAll("\\n", "");
        encryptedString = encryptedString.replaceAll("\\r", "");

       // System.out.println(encryptedString);


        return encryptedString;
    }

    public static String decrypt(String message) throws Exception {
        byte[] encodedKey = new byte[0];
        String decryptedMessage = null;
        encodedKey = new BASE64Decoder().decodeBuffer(message);
        JNCryptor cryptor = new AES256JNCryptor();
        byte[] byteDecryptedText = cryptor.decryptData(encodedKey, password.toCharArray());
        decryptedMessage = new String(byteDecryptedText);
        System.out.println("Decrypted Message: "+decryptedMessage);

        return decryptedMessage;
    }

    public static String generateResponseAccessToken() throws Exception{
        long now = System.currentTimeMillis();
        String responseAccessTokenStr = "response:" + now;
        String responseAccessToken = encrypt(responseAccessTokenStr);
        System.out.println("generateResponseAccessToken:- [" + responseAccessToken + "]");
        return responseAccessToken;
    }

    public static String generateRequestAccessToken(String responseAccessToken) throws Exception {
        String responseAccessTokenStr = decrypt(responseAccessToken);
        StringTokenizer stringTokenizer = new StringTokenizer(responseAccessTokenStr, ":");
        String now = stringTokenizer.nextToken();
        String requestAccessTokenStr = "request:" + now;
        String requestAccessToken = encrypt(requestAccessTokenStr);
        System.out.println("generateRequestAccessToken:- [" + requestAccessToken + "]");
        return requestAccessToken;
    }

    public static String decryptAccessToken(String accessToken) throws Exception {

        String token = decrypt(accessToken);
        //System.out.println("access token:-" + token);
        StringTokenizer stringTokenizer = new StringTokenizer(token, ":");
        String str1 = stringTokenizer.nextToken();
        if (!str1.equals("request")) throw new YSException("SC001");
        String now = stringTokenizer.nextToken();
        System.out.println("decryptAccessToken: [" + str1 + "  time:-" + now + "]");
        return now;
    }
}

