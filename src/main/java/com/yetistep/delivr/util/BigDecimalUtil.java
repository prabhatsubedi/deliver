package com.yetistep.delivr.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

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
        return param1.compareTo(param2) == 1;
    }

    public static boolean isGreaterThenOrEqualTo(BigDecimal param1, BigDecimal param2){
        return param1.compareTo(param2) == 1 || param1.compareTo(param2) == 0;
    }

    public static boolean isLessThanZero(BigDecimal param) {
        return isLessThen(param, BigDecimal.ZERO);
    }

    public static boolean isGreaterThenZero(BigDecimal param){
        return isGreaterThen(param, BigDecimal.ZERO);
    }

    public static boolean isLessThen(BigDecimal param1, BigDecimal param2){
        return param1.compareTo(param2) == -1;
    }

    public static boolean isLessThenOrEqualTo(BigDecimal param1, BigDecimal param2){
        return param1.compareTo(param2) == -1 || param1.compareTo(param2) == 0;
    }

    public static boolean isEqualTo(BigDecimal param1, BigDecimal param2){
        return param1.compareTo(param2) == 0;
    }
    /*
    Checking Constraint is param1 is not equal to param2
     */
    public static boolean isNotEqualTo(BigDecimal param1, BigDecimal param2){
        return param1.compareTo(param2) != 0;
    }

    public static boolean isZero(BigDecimal param1){
        return param1.compareTo(BigDecimal.ZERO) == 0;
    }

    public static boolean isNotZero(BigDecimal param1){
        return param1.compareTo(BigDecimal.ZERO) != 0;
    }

    public static BigDecimal percentageOf(BigDecimal base, BigDecimal pct){
        if(base != null && pct != null)
            return base.multiply(pct).divide(HUNDRED);
        return BigDecimal.ZERO;
    }

    public static int getMinimumIndex(List<BigDecimal> values){
        int minIndex = -1;
        if(values != null && values.size() > 0){
            int i = 0;
            BigDecimal minValue = values.get(0);
            minIndex = 0;
            for(BigDecimal value: values){
                if(isLessThen(value, minValue)){
                    minValue = value;
                    minIndex = i;
                }
                i++;
            }
        }
        return minIndex;
    }

    public static BigDecimal getDistanceInKiloMeters(BigDecimal distanceInMeters){
        return distanceInMeters.divide(new BigDecimal(1000));
    }

    public static BigDecimal getDistanceInMeters(BigDecimal distanceInKiloMeters){
        return distanceInKiloMeters.multiply(new BigDecimal(1000));
    }

    public static BigDecimal calculateCost(int itemQuantity, BigDecimal itemPrice, BigDecimal attributePrice){
        itemPrice = itemPrice.add(attributePrice);
        return itemPrice.multiply(new BigDecimal(itemQuantity));
    }

    public static BigDecimal checkNull(BigDecimal input){
        if(input == null)
            return BigDecimal.ZERO;
        return input;
    }

    public static BigDecimal getUnitPrice(BigDecimal itemTotal, Integer quantity){
       if(itemTotal != null && quantity != null){
            return itemTotal.divide(new BigDecimal(quantity), RoundingMode.HALF_UP);
       }
       return null;
    }

    public static BigDecimal multiply(BigDecimal input, BigDecimal multiplyFactor){
        return input.multiply(multiplyFactor);
    }

    public static BigDecimal divide(BigDecimal input, BigDecimal divisor){
        //BigDecimal output = input.divide(divisor);
        //return  output.setScale(2, RoundingMode.HALF_UP);
        return input.divide(divisor, RoundingMode.HALF_UP);
    }

    public static BigDecimal convertToINRPaisa(BigDecimal input, BigDecimal divisor){
        //BigDecimal output = input.divide(divisor);
        //return  output.setScale(2, RoundingMode.HALF_UP);
        BigDecimal conversionRateInPaisa = input.multiply(new BigDecimal(100));
        return conversionRateInPaisa.divide(divisor, RoundingMode.HALF_DOWN);
    }

    public static boolean isIntegerValue(BigDecimal bd) {
        return bd.signum() == 0 || bd.scale() <= 0 || bd.stripTrailingZeros().scale() <= 0;
    }

    public static BigDecimal chopToTwoDecimalPlace(BigDecimal number) {
        return number.setScale(2, BigDecimal.ROUND_DOWN);
    }

}
