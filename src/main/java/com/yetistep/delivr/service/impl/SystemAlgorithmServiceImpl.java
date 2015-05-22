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
    public CourierTransactionEntity getCourierTransaction(OrderEntity order, DeliveryBoySelectionEntity dBoySelection, BigDecimal merchantCommission, BigDecimal merchantServiceFee) throws Exception {
        BigDecimal DBOY_ADDITIONAL_PER_KM_CHARGE = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DBOY_ADDITIONAL_PER_KM_CHARGE));
        BigDecimal RESERVED_COMM_PER_BY_SYSTEM = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.RESERVED_COMM_PER_BY_SYSTEM));
        BigDecimal DBOY_PER_KM_CHARGE_UPTO_NKM = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DBOY_PER_KM_CHARGE_UPTO_NKM));
        BigDecimal DBOY_PER_KM_CHARGE_ABOVE_NKM = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DBOY_PER_KM_CHARGE_ABOVE_NKM));
        BigDecimal DBOY_COMMISSION = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DBOY_COMMISSION));
        BigDecimal DBOY_MIN_AMOUNT = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DBOY_MIN_AMOUNT));
        BigDecimal DELIVERY_FEE_VAT = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DELIVERY_FEE_VAT));
        BigDecimal MINIMUM_PROFIT_PERCENTAGE = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.MINIMUM_PROFIT_PERCENTAGE));
        BigDecimal ADDITIONAL_KM_FREE_LIMIT = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.ADDITIONAL_KM_FREE_LIMIT));
        BigDecimal DEFAULT_NKM_DISTANCE = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DEFAULT_NKM_DISTANCE));
        BigDecimal ZERO = BigDecimal.ZERO;

         /* 1. ===== Order Total ======= */
        BigDecimal totalOrder = order.getTotalCost().setScale(2, RoundingMode.HALF_UP);

        /* 2. ======= Commission Percent ====== */
        BigDecimal commissionPct = BigDecimalUtil.checkNull(merchantCommission);

        /* 3. ===== Distance Store to Customer(KM) ======== */
        BigDecimal storeToCustomerDistance = dBoySelection.getStoreToCustomerDistance();

        /* 4. ====== Distance Courier to Store (KM) ======== */
        BigDecimal courierToStoreDistance = dBoySelection.getDistanceToStore();

        /* 5. ==== Service Fee % ======= */
        BigDecimal serviceFeePct = BigDecimalUtil.checkNull(merchantServiceFee);

        /* 6. ====== Additional delivery amt ======= */
        BigDecimal additionalDeliveryAmt = ZERO;
        if (BigDecimalUtil.isGreaterThen(courierToStoreDistance, ADDITIONAL_KM_FREE_LIMIT))
            additionalDeliveryAmt = courierToStoreDistance.subtract(ADDITIONAL_KM_FREE_LIMIT).multiply(DBOY_ADDITIONAL_PER_KM_CHARGE);

       /* 7. ==== Discount on delivery to customer ======= */
        BigDecimal customerDiscount = ZERO;
        BigDecimal systemReservedCommissionAmt = ZERO;
        if (BigDecimalUtil.isGreaterThenZero(commissionPct)) {
            BigDecimal totalCommission = BigDecimalUtil.percentageOf(totalOrder, commissionPct);
            systemReservedCommissionAmt = BigDecimalUtil.percentageOf(totalCommission, RESERVED_COMM_PER_BY_SYSTEM);
            customerDiscount = totalCommission.subtract(systemReservedCommissionAmt);
        }

        /* 8. ==== Surge Factor ======= */
        Integer surgeFactor = order.getSurgeFactor();

        /* 9. ====== Delivery cost (Does not include additional delivery amt) ============== */
        BigDecimal deliveryCostWithoutAdditionalDvAmt = ZERO;
        if (BigDecimalUtil.isLessThen(storeToCustomerDistance, DEFAULT_NKM_DISTANCE))
            deliveryCostWithoutAdditionalDvAmt = DBOY_PER_KM_CHARGE_UPTO_NKM.multiply(new BigDecimal(surgeFactor));
        else
            deliveryCostWithoutAdditionalDvAmt = storeToCustomerDistance.multiply(DBOY_PER_KM_CHARGE_ABOVE_NKM).multiply(new BigDecimal(surgeFactor));

        /* 10. ======= Service fee Amount with VAT =========== */
        BigDecimal serviceFeeAmt = BigDecimalUtil.percentageOf(totalOrder, serviceFeePct);
        serviceFeeAmt = serviceFeeAmt.add(BigDecimalUtil.percentageOf(serviceFeeAmt, DELIVERY_FEE_VAT));

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


        /* 14. ======= Customer available balance after discount ======== */
        BigDecimal customerBalanceAfterDiscount = ZERO;
        if (BigDecimalUtil.isGreaterThen(customerBalanceBeforeDiscount, deliveryChargedBeforeDiscount))
            customerBalanceAfterDiscount = customerBalanceBeforeDiscount.subtract(deliveryChargedBeforeDiscount);

        /* 14. ====== Customer Pays ========*/
        BigDecimal customerPays = order.getTotalCost().add(serviceFeeAmt).add(deliveryChargedAfterDiscount).add(order.getItemServiceAndVatCharge());
        customerPays = customerPays.setScale(2, BigDecimal.ROUND_DOWN);
        order.setGrandTotal(customerPays);
        order.setDeliveryCharge(deliveryChargedAfterDiscount);
        order.setSystemServiceCharge(serviceFeeAmt);

        /* 15. ======= Paid to Courier ====== */
        BigDecimal paidToCourier = ZERO;
        if (BigDecimalUtil.isGreaterThen(BigDecimalUtil.percentageOf(deliveryCostWithoutAdditionalDvAmt, DBOY_COMMISSION), DBOY_MIN_AMOUNT))
            paidToCourier = BigDecimalUtil.percentageOf(deliveryCostWithoutAdditionalDvAmt, DBOY_COMMISSION).add(additionalDeliveryAmt);
        else
            paidToCourier = DBOY_MIN_AMOUNT.add(additionalDeliveryAmt);
        paidToCourier = paidToCourier.setScale(0, BigDecimal.ROUND_DOWN);
        dBoySelection.setPaidToCourier(paidToCourier);

        /* 16 ===== Profit ====== */
        // total order * profit% = >  actual profit
        BigDecimal profit = ZERO;
        profit = BigDecimalUtil.percentageOf(totalOrder, commissionPct).add(BigDecimalUtil.percentageOf(totalOrder, serviceFeePct));
        profit = profit.add(deliveryChargedAfterDiscount.divide(BigDecimal.ONE.add(DELIVERY_FEE_VAT.divide(new BigDecimal(100))), MathContext.DECIMAL128)).subtract(paidToCourier);
        if (BigDecimalUtil.isLessThen(profit, BigDecimalUtil.percentageOf(totalOrder, MINIMUM_PROFIT_PERCENTAGE))) {
            log.warn("No Profit");
        }

        CourierTransactionEntity courierTransactionEntity = new CourierTransactionEntity();
        courierTransactionEntity.setOrder(order);
        courierTransactionEntity.setOrderTotal(totalOrder);
        courierTransactionEntity.setCommissionPct(commissionPct);
        courierTransactionEntity.setStoreToCustomerDistance(storeToCustomerDistance);
        courierTransactionEntity.setCourierToStoreDistance(courierToStoreDistance);
        courierTransactionEntity.setServiceFeePct(serviceFeePct);
        courierTransactionEntity.setAdditionalDeliveryAmt(additionalDeliveryAmt);
        courierTransactionEntity.setCustomerDiscount(customerDiscount);
        courierTransactionEntity.setSurgeFactor(surgeFactor);
        courierTransactionEntity.setDeliveryCostWithoutAdditionalDvAmt(deliveryCostWithoutAdditionalDvAmt);
        courierTransactionEntity.setServiceFeeAmt(serviceFeeAmt);
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
        System.out.println("Transaction Amount:" + value);
        System.out.println("Current Milliseconds:" + walletTransactionEntity.getTransactionDate().getTime());
        Long currentTime = walletTransactionEntity.getTransactionDate().getTime();
        currentTime = (currentTime % 1000) >= 500 ? ((currentTime / 1000) * 1000) + 1000 : (currentTime / 1000) * 1000;
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
        decodedData = decodedData.divide(new BigDecimal(walletTransactionEntity.getCustomer().getId()));
        decodedData = decodedData.subtract(new BigDecimal(walletTransactionEntity.getTransactionDate().getTime()));
        if (BigDecimalUtil.isEqualTo(decodedData, walletTransactionEntity.getTransactionAmount()))
            walletTransactionEntity.setFlag(true);
        else
            walletTransactionEntity.setFlag(false);
    }
}
