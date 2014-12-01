package com.yetistep.delivr.util;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Properties;

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

    public static Properties readProperties(String fileName) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream(fileName);

        Properties prop = new Properties();
        prop.load(input);

        return prop;
    }

    public  static Timestamp getCurrentTimestampSQL(){
        return new Timestamp(System.currentTimeMillis());
    }
}
