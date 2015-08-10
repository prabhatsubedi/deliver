package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.enums.PreferenceType;
import com.yetistep.delivr.model.CourierTransactionEntity;
import com.yetistep.delivr.model.DeliveryBoySelectionEntity;
import com.yetistep.delivr.model.OrderEntity;
import com.yetistep.delivr.model.WalletTransactionEntity;
import com.yetistep.delivr.service.inf.SystemAlgorithmService;
import com.yetistep.delivr.service.inf.SystemPropertyService;
import com.yetistep.delivr.util.BigDecimalUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/17/14
 * Time: 12:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class SystemAlgorithmServiceImpl implements SystemAlgorithmService {
    private static final Logger log = Logger.getLogger(SystemAlgorithmServiceImpl.class);

    @Autowired
    SystemPropertyService systemPropertyService;

    private static final BigDecimal TWO = new BigDecimal(2);
    public static final BigDecimal ZERO = BigDecimal.ZERO;

    @Override
    public CourierTransactionEntity getCourierTransaction(OrderEntity order, DeliveryBoySelectionEntity dBoySelection, BigDecimal totalCommission, BigDecimal systemProcessingCharge) throws Exception {
        BigDecimal DBOY_ADDITIONAL_PER_KM_CHARGE = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DBOY_ADDITIONAL_PER_KM_CHARGE));  //Shopper per km additional charge
        BigDecimal RESERVED_COMM_PER_BY_SYSTEM = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.RESERVED_COMM_PER_BY_SYSTEM));      //Reserved commission percentage by system
        BigDecimal DBOY_PER_KM_CHARGE_UPTO_NKM = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DBOY_PER_KM_CHARGE_UPTO_NKM));      //shopper per km charge for default distance
        BigDecimal DBOY_PER_KM_CHARGE_ABOVE_NKM = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DBOY_PER_KM_CHARGE_ABOVE_NKM));    //shopper per km charge above default distance
        BigDecimal DBOY_COMMISSION = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DBOY_COMMISSION));       //shopper commission percentage
        BigDecimal DBOY_MIN_AMOUNT = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DBOY_MIN_AMOUNT));       //minimum amount given to shopper
        BigDecimal DELIVERY_FEE_VAT = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DELIVERY_FEE_VAT));     //system vat percentage
        BigDecimal MINIMUM_PROFIT_PERCENTAGE = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.MINIMUM_PROFIT_PERCENTAGE)); //Minimum Profit Percentage for Order Acceptance
        BigDecimal ADDITIONAL_KM_FREE_LIMIT = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.ADDITIONAL_KM_FREE_LIMIT));  //Unpaid KM for Shopper
        BigDecimal DEFAULT_NKM_DISTANCE = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DEFAULT_NKM_DISTANCE));         //default distance in km
        BigDecimal ZERO = BigDecimal.ZERO;

         /* 1. ===== Order Total ======= */
        BigDecimal totalOrder = order.getTotalCost().setScale(2, RoundingMode.HALF_UP);
        totalOrder = totalOrder.subtract(BigDecimalUtil.checkNull(order.getDiscountFromStore()));

        /* 2. ======= Commission Percent ====== */
        //BigDecimal commissionPct = BigDecimalUtil.checkNull(merchantCommission);

        /* 3. ===== Distance Store to Customer(KM) ======== */
        BigDecimal storeToCustomerDistance = dBoySelection.getStoreToCustomerDistance();

        /* 4. ====== Distance Courier to Store (KM) ======== */
        BigDecimal courierToStoreDistance = dBoySelection.getDistanceToStore();

        /* 5. ==== Service Fee % ======= */
        BigDecimal systemProcessingChargePct = BigDecimalUtil.checkNull(systemProcessingCharge);

        /* 6. ====== Additional delivery amt ======= */
        BigDecimal additionalDeliveryAmt = ZERO;
        if (BigDecimalUtil.isGreaterThen(courierToStoreDistance, ADDITIONAL_KM_FREE_LIMIT))
            additionalDeliveryAmt = courierToStoreDistance.subtract(ADDITIONAL_KM_FREE_LIMIT).multiply(DBOY_ADDITIONAL_PER_KM_CHARGE);

       /* 7. ==== Discount on delivery to customer ======= */
        BigDecimal customerDiscount = ZERO;
        BigDecimal systemReservedCommissionAmt = ZERO;
        if (BigDecimalUtil.isGreaterThenZero(totalCommission)) {
            //BigDecimal totalCommission = BigDecimalUtil.percentageOf(totalOrder, commissionPct);
            systemReservedCommissionAmt = BigDecimalUtil.percentageOf(totalCommission, RESERVED_COMM_PER_BY_SYSTEM);
            customerDiscount = totalCommission.subtract(systemReservedCommissionAmt);
        }

        /* 8. ==== Surge Factor ======= */
        Integer surgeFactor = order.getSurgeFactor();

        /* 9. ====== Delivery cost (Does not include additional delivery amt) ============== */
        BigDecimal deliveryCostWithoutAdditionalDvAmt = ZERO;
        if (BigDecimalUtil.isLessThen(storeToCustomerDistance, DEFAULT_NKM_DISTANCE))
            deliveryCostWithoutAdditionalDvAmt = DBOY_PER_KM_CHARGE_UPTO_NKM.multiply(new BigDecimal(surgeFactor));
        else{
            BigDecimal defaultCharge = DBOY_PER_KM_CHARGE_UPTO_NKM.multiply(new BigDecimal(surgeFactor));
            BigDecimal extraDistance = storeToCustomerDistance.subtract(DEFAULT_NKM_DISTANCE);
            deliveryCostWithoutAdditionalDvAmt = defaultCharge.add(extraDistance.multiply(DBOY_PER_KM_CHARGE_ABOVE_NKM).multiply(new BigDecimal(surgeFactor)));
        }

        /* 10. ======= Processing fee Amount with VAT =========== */
        BigDecimal systemProcessingChargeAmount = BigDecimalUtil.percentageOf(totalOrder, systemProcessingChargePct);
        BigDecimal capOnProcessingCharge = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.CAP_ON_PROCESSING_CHARGE));
        if(BigDecimalUtil.isGreaterThen(systemProcessingChargeAmount, capOnProcessingCharge))
            systemProcessingChargeAmount = capOnProcessingCharge;
        systemProcessingChargeAmount = systemProcessingChargeAmount.add(BigDecimalUtil.percentageOf(systemProcessingChargeAmount, DELIVERY_FEE_VAT));

        /* 11. ====== Delivery cost  with VAT and with Discount from system ======== */
        BigDecimal deliveryChargedBeforeDiscount = ZERO;
        if (BigDecimalUtil.isGreaterThenOrEqualTo(deliveryCostWithoutAdditionalDvAmt, customerDiscount)) {
            deliveryChargedBeforeDiscount = deliveryCostWithoutAdditionalDvAmt.subtract(customerDiscount);
            deliveryChargedBeforeDiscount = deliveryChargedBeforeDiscount.add(BigDecimalUtil.percentageOf(deliveryChargedBeforeDiscount, DELIVERY_FEE_VAT));
        }

        /* 12. ======= Customer Available balance before discount ====== */
        //  BigDecimal customerBalanceBeforeDiscount = BigDecimalUtil.checkNull(order.getCustomer().getRewardsEarned());
        /* Set this to zero since no discount in this phase */
        BigDecimal customerBalanceBeforeDiscount = BigDecimal.ZERO;

        /* 13. ======== Delivery charged to customer After Discount with VAT ====== */
        BigDecimal deliveryChargedAfterDiscount = ZERO;
        if (BigDecimalUtil.isGreaterThenOrEqualTo(deliveryChargedBeforeDiscount, customerBalanceBeforeDiscount))
            deliveryChargedAfterDiscount = deliveryChargedBeforeDiscount.subtract(customerBalanceBeforeDiscount);
        deliveryChargedAfterDiscount = deliveryChargedAfterDiscount.subtract(BigDecimalUtil.percentageOf(deliveryChargedAfterDiscount, BigDecimalUtil.checkNull(new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DISCOUNT_ON_DELIVERY_FEE)))));

        /* 14. ======= Delivery Charge after store delivery discount ======== */
        BigDecimal discountInDeliveryFeeFromStore = BigDecimalUtil.percentageOf(deliveryChargedAfterDiscount, BigDecimalUtil.checkNull(order.getStore().getStoresBrand().getDiscountInDeliveryFee()));
        if(order.getStore().getStoresBrand().getDiscountInDeliveryFee() != null)
            deliveryChargedAfterDiscount = deliveryChargedAfterDiscount.subtract(discountInDeliveryFeeFromStore);

        /* 14. ======= Customer available balance after discount ======== */
        BigDecimal customerBalanceAfterDiscount = ZERO;
        if (BigDecimalUtil.isGreaterThen(customerBalanceBeforeDiscount, deliveryChargedBeforeDiscount))
            customerBalanceAfterDiscount = customerBalanceBeforeDiscount.subtract(deliveryChargedBeforeDiscount);

            BigDecimal dfl = BigDecimalUtil.checkNull(order.getStore().getStoresBrand().getDeliveryFeeLimit());
            if(systemPropertyService.readPrefValue(PreferenceType.DELIVERY_FEE_CHARGING_MODEL).equals("Distance Based") )  {
                if(BigDecimalUtil.isGreaterThen(totalOrder, dfl)){
                    order.setDeliveryCharge(BigDecimal.ZERO);
                }  else {
                    order.setDeliveryCharge(deliveryChargedAfterDiscount);
                }
            }else{
                if(BigDecimalUtil.isGreaterThen(totalOrder, dfl)){
                    order.setDeliveryCharge(BigDecimal.ZERO);
                }  else {
                    BigDecimal deliveryFee = BigDecimal.ZERO;
                    if(order.getStore().getStoresBrand().getDeliveryFee()!=null)
                        deliveryFee  = order.getStore().getStoresBrand().getDeliveryFee();
                    else
                        deliveryFee =  new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DEFAULT_DELIVERY_FEE_IN_FLAT_MODEL));

                    deliveryFee =  deliveryFee.add(BigDecimalUtil.percentageOf(deliveryFee, new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DELIVERY_FEE_VAT))));
                    order.setDeliveryCharge(deliveryFee.multiply(new BigDecimal(order.getSurgeFactor())));
                }
            }


        /* 14. ====== Customer Pays ========*/
        BigDecimal customerPays = totalOrder.subtract(order.getCashBackToCustomerAmount()).add(systemProcessingChargeAmount).add(order.getDeliveryCharge()).add(order.getItemServiceAndVatCharge());
        customerPays = customerPays.setScale(2, BigDecimal.ROUND_DOWN);
        order.setGrandTotal(customerPays);

        order.setSystemServiceCharge(systemProcessingChargeAmount);
        order.setStoreDeliveryDiscount(discountInDeliveryFeeFromStore);

        /* 15. ======= Paid to Courier ====== */
        BigDecimal paidToCourier = ZERO;
        if (BigDecimalUtil.isGreaterThen(BigDecimalUtil.percentageOf(deliveryCostWithoutAdditionalDvAmt, DBOY_COMMISSION).add(additionalDeliveryAmt), DBOY_MIN_AMOUNT))
            paidToCourier = BigDecimalUtil.percentageOf(deliveryCostWithoutAdditionalDvAmt, DBOY_COMMISSION).add(additionalDeliveryAmt);
        else
            paidToCourier = DBOY_MIN_AMOUNT;

        paidToCourier = paidToCourier.setScale(0, BigDecimal.ROUND_DOWN);
        dBoySelection.setPaidToCourier(paidToCourier);

        /* 16 ===== Profit ====== */
        // total order * profit% = >  actual profit
        BigDecimal profit = ZERO;
        profit = totalCommission.add(BigDecimalUtil.percentageOf(totalOrder, systemProcessingCharge));
        profit = profit.add(deliveryChargedAfterDiscount.divide(BigDecimal.ONE.add(DELIVERY_FEE_VAT.divide(new BigDecimal(100))), MathContext.DECIMAL128)).subtract(paidToCourier);
        if (BigDecimalUtil.isLessThen(profit, BigDecimalUtil.percentageOf(totalOrder, MINIMUM_PROFIT_PERCENTAGE))) {
            log.warn("No Profit");
        }

        CourierTransactionEntity courierTransactionEntity = new CourierTransactionEntity();
        courierTransactionEntity.setOrder(order);
        courierTransactionEntity.setOrderTotal(totalOrder);
        //courierTransactionEntity.setCommissionPct(commissionPct);
        courierTransactionEntity.setCommissionAmount(totalCommission);
        courierTransactionEntity.setStoreToCustomerDistance(storeToCustomerDistance);
        courierTransactionEntity.setCourierToStoreDistance(courierToStoreDistance);
        courierTransactionEntity.setSystemProcessingChargePct(systemProcessingChargePct);
        courierTransactionEntity.setAdditionalDeliveryAmt(additionalDeliveryAmt);
        courierTransactionEntity.setCustomerDiscount(customerDiscount);
        courierTransactionEntity.setSurgeFactor(surgeFactor);
        courierTransactionEntity.setDeliveryCostWithoutAdditionalDvAmt(deliveryCostWithoutAdditionalDvAmt);
        courierTransactionEntity.setSystemProcessingChargeAmount(systemProcessingChargeAmount);
        courierTransactionEntity.setDeliveryChargedBeforeDiscount(deliveryChargedBeforeDiscount);
        courierTransactionEntity.setCustomerBalanceBeforeDiscount(customerBalanceBeforeDiscount);
        courierTransactionEntity.setDeliveryChargedAfterDiscount(deliveryChargedAfterDiscount);
        courierTransactionEntity.setCustomerBalanceAfterDiscount(customerBalanceAfterDiscount);
        courierTransactionEntity.setCustomerPays(customerPays);
        courierTransactionEntity.setPaidToCourier(paidToCourier);
        courierTransactionEntity.setProfit(profit);
        return courierTransactionEntity;
    }

    @Override
    public void encodeWalletTransaction(WalletTransactionEntity walletTransactionEntity) throws Exception {
        BigDecimal value = walletTransactionEntity.getTransactionAmount().setScale(2, BigDecimal.ROUND_HALF_UP);
        Long currentTime = walletTransactionEntity.getTransactionDate().getTime();
        currentTime = (currentTime / 1000) * 1000;
        walletTransactionEntity.setTransactionDate(new Timestamp(currentTime));
        value = value.add(new BigDecimal(currentTime));
        value = value.multiply(new BigDecimal(walletTransactionEntity.getCustomer().getId()));
        value = value.add(walletTransactionEntity.getAvailableWalletAmount().setScale(2, BigDecimal.ROUND_HALF_UP));
        String encodeData = value + "";
        String[] encode = encodeData.split(Pattern.quote("."));
        String signature = "";
        for (int i = 0; i < encode.length; i++) {
            if (signature.equals("")) {
                Long l = Long.parseLong(encode[i]);
                signature = Long.toHexString(l) + "-";
            } else
                signature += encode[i];
        }
        walletTransactionEntity.setSignature(signature);
    }

    @Override
    public void decodeWalletTransaction(WalletTransactionEntity walletTransactionEntity) throws Exception {
        if(walletTransactionEntity == null)
            return;
        log.info(walletTransactionEntity.toString());
        String[] decode = walletTransactionEntity.getSignature().split("-");
        String parsedResult = "";
        for (int i = 0; i < decode.length; i++) {
            if (parsedResult.equals(""))
                parsedResult = Long.parseLong(decode[i], 16) + ".";
            else
                parsedResult += decode[i].toString();
        }
        BigDecimal decodedData = new BigDecimal(parsedResult);
        decodedData = decodedData.subtract(walletTransactionEntity.getAvailableWalletAmount());
        decodedData = decodedData.divide(new BigDecimal(walletTransactionEntity.getCustomer().getId()), BigDecimal.ROUND_HALF_UP);
        decodedData = decodedData.subtract(new BigDecimal(walletTransactionEntity.getTransactionDate().getTime()));
        log.info("Wallet Transaction Amount:"+decodedData +"\tTransaction Date:"+walletTransactionEntity.getTransactionDate()+"\tAvailable Amount:"+walletTransactionEntity.getAvailableWalletAmount());
        if (BigDecimalUtil.isEqualTo(new BigDecimal(decodedData.intValue()), new BigDecimal(walletTransactionEntity.getTransactionAmount().intValue())))
            walletTransactionEntity.setFlag(true);
        else
            walletTransactionEntity.setFlag(false);
    }
}
