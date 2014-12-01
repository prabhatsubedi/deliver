package com.yetistep.delivr.util;

import com.yetistep.delivr.util.YSException;
import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/19/14
 * Time: 11:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class GeneralUtil {
    public static void logError(Logger log, String message, Exception e) {
        if (e instanceof YSException)
            log.info(e.getMessage());
        else
            log.error(message, e);
    }

}
