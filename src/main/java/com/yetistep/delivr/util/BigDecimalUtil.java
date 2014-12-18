package com.yetistep.delivr.util;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 3/10/14
 * Time: 10:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class BigDecimalUtil {
    private static final BigDecimal HUNDRED = new BigDecimal(100);
    public static boolean isGreaterThen(BigDecimal param1, BigDecimal param2){
         if(param1.compareTo(param2) == 1){
             return true;
         }
        return false;
    }

    public static final boolean isGreaterThenOrEqualTo(BigDecimal param1, BigDecimal param2){
        if(param1.compareTo(param2) == 1 || param1.compareTo(param2) == 0){
            return true;
        }
        return false;
    }

    public static boolean isLessThanZero(BigDecimal param) {
        return isLessThen(param, BigDecimal.ZERO);
    }

    public static boolean isGreaterThenZero(BigDecimal param){
        return isGreaterThen(param, BigDecimal.ZERO);
    }

    public static boolean isLessThen(BigDecimal param1, BigDecimal param2){
        if(param1.compareTo(param2) == -1) {
             return true;
        }
        return false;
    }

    public static boolean isLessThenOrEqualTo(BigDecimal param1, BigDecimal param2){
        if(param1.compareTo(param2) == -1 || param1.compareTo(param2) == 0) {
            return true;
        }
        return false;
    }

    public static boolean isEqualTo(BigDecimal param1, BigDecimal param2){
        if(param1.compareTo(param2) == 0){
            return true;
        }
        return false;
    }
    /*
    Checking Constraint is param1 is not equal to param2
     */
    public static boolean isNotEqualTo(BigDecimal param1, BigDecimal param2){
         if(param1.compareTo(param2) != 0){
            return true;
         }
        return false;
    }

    public static boolean isZero(BigDecimal param1){
        if(param1.compareTo(BigDecimal.ZERO) == 0){
            return true;
        }
        return false;
    }

    public static boolean isNotZero(BigDecimal param1){
        if(param1.compareTo(BigDecimal.ZERO) != 0){
            return true;
        }
        return false;
    }

    public static BigDecimal percentageOf(BigDecimal base, BigDecimal pct){
        return base.multiply(pct).divide(HUNDRED);
    }
}
