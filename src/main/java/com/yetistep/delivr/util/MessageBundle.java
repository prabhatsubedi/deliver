package com.yetistep.delivr.util;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/18/14
 * Time: 12:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class MessageBundle {
    private static final Logger log = Logger.getLogger(MessageBundle.class);

    public static String getMessage(String key, String file) {
        try {
            Properties prop = readProperties(file);
            return prop.getProperty(key);
        } catch (Exception e) {
            log.error("Error occurred while reading file "+file, e);
        }

        return null;
    }

    public static String generateTokenString() {
        return UUID.randomUUID().toString();

    }

    public static Properties readProperties(String fileName) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream(fileName);

        Properties prop = new Properties();
        prop.load(input);

        return prop;
    }

    public static String separateString(String separator, String... args){
        StringBuilder builder = new StringBuilder();

        for(String arg: args)
            builder.append(arg+separator);

        return builder.toString();
    }

    public  static Timestamp getCurrentTimestampSQL(){
        return new Timestamp(System.currentTimeMillis());
    }

    public static String getSenderEmail(){
        return System.getProperty("DELIVR_SENDER_EMAIL");
    }
    public static String getSenderPassword(){
        return System.getProperty("DELIVR_SENDER_PASSWORD");
    }

    public static int getSenderPort(){
        return Integer.parseInt(System.getProperty("DELIVR_SENDER_PORT"));
    }

    public static String getSenderHost(){
        return System.getProperty("DELIVR_SENDER_HOST");
    }

    public static String getHostName() {
        return System.getProperty("DELIVR_HOST");
    }

    public static boolean isLocalHost() {
        return getHostName().equals("localhost");
    }
}
