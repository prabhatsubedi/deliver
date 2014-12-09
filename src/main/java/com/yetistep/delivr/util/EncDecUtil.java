package com.yetistep.delivr.util;

import org.apache.log4j.Logger;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.StringTokenizer;

/**
 * Created with IntelliJ IDEA.
 * User: raj
 * Date: 11/16/13
 * Time: 17:40
 * To change this template use File | Settings | File Templates.
 */
public class EncDecUtil {
    private static final Logger log = Logger.getLogger(EncDecUtil.class);

    public static String generateSecretKeyString() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey secretKey = keyGen.generateKey();
        //converting sectet key to string to store in database
        // -------------------------------------------------------------------
        byte[] secretKeyByte = secretKey.getEncoded();
        String secretKeyStr = new BASE64Encoder().encode(secretKeyByte);
        log.info("generated secret key:-" + secretKey + "  secretKeyString: " + secretKeyStr);
        return secretKeyStr;
    }

    public static SecretKey retriveSecretKey(String keyStr) throws Exception {
        byte[] encodedKey = new BASE64Decoder().decodeBuffer(keyStr);
        SecretKey originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES"); //EDIT: missing 'new'
        return originalKey;
    }

    public static String encrypt(String message, SecretKey key) throws Exception {
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] byteDataToEncrypt = message.getBytes();
        byte[] byteCipherText = aesCipher.doFinal(byteDataToEncrypt);
        String cipherText = new BASE64Encoder().encode(byteCipherText);
        //System.out.println("Encrypted message:- " + cipherText);

        return cipherText;
    }

    public static String decrypt(String encryptedMessage, SecretKey key) throws Exception {
        byte[] encryptedMessageByte = new BASE64Decoder().decodeBuffer(encryptedMessage);

        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, key);
        byte[] byteDecryptedText = aesCipher.doFinal(encryptedMessageByte);
        String decryptedMessage = new String(byteDecryptedText);
        //System.out.println("Decrypted message:- " + decryptedMessage);
        return decryptedMessage;

    }

    public static String generateResponseAccessToken(String key) throws Exception {
        SecretKey secretKey = retriveSecretKey(key);
        long now = System.currentTimeMillis();
        String responseAccessTokenStr = "response:" + now;
        String responseAccessToken = encrypt(responseAccessTokenStr, secretKey);
        log.info("generateResponseAccessToken:- " + responseAccessTokenStr + " [" + responseAccessToken + "]");
        return responseAccessToken;

    }

    public static String generateRequestAccessToken(String responseAccessToken, String key) throws Exception {
        SecretKey secretKey = retriveSecretKey(key);
        String responseAccessTokenStr = decrypt(responseAccessToken, secretKey);
        StringTokenizer stringTokenizer = new StringTokenizer(responseAccessTokenStr, ":");
        String str1 = stringTokenizer.nextToken();
        String now = stringTokenizer.nextToken();
        String requestAccessTokenStr = "request:" + now;
        String requestAccessToken = encrypt(requestAccessTokenStr, secretKey);
        log.info("generateRequestAccessToken:- "+responseAccessTokenStr + " >> " + requestAccessTokenStr + " [" + requestAccessToken + "]");
        return requestAccessToken;

    }

    public static String decryptAccessToken(String accessToken, String key) throws Exception {
        SecretKey secretKey = retriveSecretKey(key);
        String token = decrypt(accessToken, secretKey);
        log.info("access token:-"  +token);
        StringTokenizer stringTokenizer = new StringTokenizer(token, ":");
        String str1 = stringTokenizer.nextToken();
        if (!str1.equals("request")) throw new YSException("SC001");
        String now = stringTokenizer.nextToken();
        log.info("decryptAccessToken: [" + str1 + "  time:-" + now +"]");
        return now;
    }


}
