package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.enums.PreferenceType;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.service.inf.SystemAlgorithmService;
import com.yetistep.delivr.service.inf.SystemPropertyService;
import com.yetistep.delivr.util.BigDecimalUtil;
import com.yetistep.delivr.util.DateUtil;
import com.yetistep.delivr.util.GeoCodingUtil;
import com.yetistep.delivr.util.YSException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/17/14
 * Time: 12:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class SystemAlgorithmServiceImpl implements SystemAlgorithmService{
    private static final Logger log = Logger.getLogger(SystemAlgorithmServiceImpl.class);

    @Autowired
    SystemPropertyService systemPropertyService;

    private static final BigDecimal TWO = new BigDecimal(2);
    public static final BigDecimal ZERO = BigDecimal.ZERO;

    @Override
    public CourierTransactionEntity calculateCourierEarning() throws Exception {


        /* Preferences Values */
        BigDecimal DBOY_ADDITIONAL_PER_KM_CHARGE = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DBOY_ADDITIONAL_PER_KM_CHARGE));
        BigDecimal RESERVED_COMM_PER_BY_SYSTEM = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.RESERVED_COMM_PER_BY_SYSTEM));
        BigDecimal DBOY_PER_KM_CHARGE_UPTO_2KM = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DBOY_PER_KM_CHARGE_UPTO_NKM));
        BigDecimal DBOY_PER_KM_CHARGE_ABOVE_2KM = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DBOY_PER_KM_CHARGE_ABOVE_NKM));
        BigDecimal DBOY_COMMISSION = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DBOY_COMMISSION));
        BigDecimal DBOY_MIN_AMOUNT = new BigDecimal(systemPropertyService.readPrefValue(PreferenceType.DBOY_MIN_AMOUNT));
        BigDecimal deliveryVat = new BigDecimal(13);//FIXME Get from preferences
        /* Dependency From Other Entity */
        BigDecimal merchantServicePct = new BigDecimal(10); //FIXME retrieve from Item
        BigDecimal merchantVatPct = new BigDecimal(13); // FIXME retrieve from Item
        BigDecimal additionalKmFreeLimit = BigDecimal.ONE; // FIXME: System Preferences

        /* 1. ===== Order Total ======= */
        BigDecimal totalOrder = new BigDecimal(1); //FIXME Get from previous Algorithm

        /* 2. ======= Commission Percent ====== */
        BigDecimal commissionPct = BigDecimal.ZERO; //FIXME: new MerchantEntity().getCommissionPercentage();

        /* 3. ===== Distance Store to Customer(KM) ======== */
        // Client to Store Dhapasi to Bhatbhateni 4.97 */
        String customerLat = "27.749878"; //Customer Entity
        String customerLng = "85.330596"; //Customer Entity

        String storeLat = "27.718962"; //Store Entity
        String storeLng = "85.330988"; //Store Entity

        double storeCustomerDistance = GeoCodingUtil.getDistance(Double.valueOf(customerLat), Double.valueOf(customerLng), Double.valueOf(storeLat), Double.valueOf(storeLng))/1000f;
        BigDecimal storeToCustomerDistance = new BigDecimal(1);//new BigDecimal(String.valueOf(storeCustomerDistance)).setScale(2, BigDecimal.ROUND_UP);

        /* 4. ====== Distance Courier to Store (KM) ======== */
        // Courier to Store (Dillibazar to Bhatbhateni)1.57 KM */
        String dboyLat = "27.70538";
        String dboyLng = "85.32676";
        double dboyCustomerDistance = GeoCodingUtil.getDistance(Double.valueOf(dboyLat), Double.valueOf(dboyLng), Double.valueOf(storeLat), Double.valueOf(storeLng))/1000f;
        BigDecimal courierToStoreDistance = new BigDecimal(4);//new BigDecimal(String.valueOf(dboyCustomerDistance)).setScale(2, BigDecimal.ROUND_UP);

        /* 5. ==== Service Fee % ======= */
        BigDecimal serviceFeePct = BigDecimal.TEN; //FIXME: Merchant Entity Service Fee Pct

        /* 6. ====== Additional delivery amt ======= */
        BigDecimal additionalDeliveryAmt = ZERO;
        if(BigDecimalUtil.isGreaterThen(courierToStoreDistance, additionalKmFreeLimit))
            additionalDeliveryAmt = courierToStoreDistance.subtract(BigDecimal.ONE).multiply(DBOY_ADDITIONAL_PER_KM_CHARGE);


       /* 7. ==== Discount on delivery to customer ======= */
        BigDecimal customerDiscount = ZERO;
        BigDecimal systemReservedCommissionAmt = ZERO;
        if(BigDecimalUtil.isGreaterThenZero(commissionPct)){
            BigDecimal totalCommission = BigDecimalUtil.percentageOf(totalOrder, commissionPct);
            systemReservedCommissionAmt = BigDecimalUtil.percentageOf(totalCommission, RESERVED_COMM_PER_BY_SYSTEM);
            customerDiscount = totalCommission.subtract(systemReservedCommissionAmt);
        }

        /* 8. ==== Surge Factor ======= */
        Integer surgeFactor = 0;
        String currentTime = DateUtil.getCurrentTime().toString();
        if(DateUtil.isTimeBetweenTwoTime("07:00:00", "21:00:00", currentTime))
             // if Time is 7 AM-9 PM
            surgeFactor = 1;
        else if(DateUtil.isTimeBetweenTwoTime("21:00:00", "22:00:00", currentTime))
            // if Time is 9-10 PM
             surgeFactor = 2;
         else if(DateUtil.isTimeBetweenTwoTime("22:00:00", "23:00:00", currentTime))
            // if Time is 10 PM-11 PM
            surgeFactor = 3;
        else
           // if Time is 11 PM-7 AM
            surgeFactor = 4;

        /* 9. ====== Delivery cost (Does not include additional delivery amt) ============== */
        BigDecimal deliveryCostWithoutAdditionalDvAmt = ZERO;
        if(BigDecimalUtil.isLessThen(storeToCustomerDistance, TWO))
             deliveryCostWithoutAdditionalDvAmt = DBOY_PER_KM_CHARGE_UPTO_2KM.multiply(new BigDecimal(surgeFactor));
        else
            deliveryCostWithoutAdditionalDvAmt = storeToCustomerDistance.multiply(DBOY_PER_KM_CHARGE_ABOVE_2KM).multiply(new BigDecimal(surgeFactor));

        /* 10. ======= Service Fee Amount =========== */
        BigDecimal serviceFeeAmt = BigDecimalUtil.percentageOf(totalOrder, serviceFeePct);

        /* 11. ====== Delivery charged to customer before Discount ======== */
        BigDecimal deliveryChargedBeforeDiscount = ZERO;
        if(BigDecimalUtil.isGreaterThenOrEqualTo(deliveryCostWithoutAdditionalDvAmt, customerDiscount)){
            deliveryChargedBeforeDiscount = deliveryCostWithoutAdditionalDvAmt.subtract(customerDiscount);
            deliveryChargedBeforeDiscount = deliveryChargedBeforeDiscount.add(BigDecimalUtil.percentageOf(deliveryChargedBeforeDiscount, deliveryVat));
        }



        /* 12. ======= Customer Available balance before discount ====== */
        BigDecimal customerBalanceBeforeDiscount = new BigDecimal(250); //FIXME Get From Customer Entity

        /* 13. ======== Delivery charged to customer After Discount ====== */
        BigDecimal deliveryChargedAfterDiscount = ZERO;
        if(BigDecimalUtil.isGreaterThen(deliveryChargedBeforeDiscount, customerBalanceBeforeDiscount))
            deliveryChargedAfterDiscount = deliveryChargedBeforeDiscount.subtract(customerBalanceBeforeDiscount);

        /* 14. ======= Customer available balance after discount ======== */
        BigDecimal customerBalanceAfterDiscount = ZERO;
        if(BigDecimalUtil.isGreaterThen(customerBalanceBeforeDiscount, deliveryChargedBeforeDiscount))
            customerBalanceAfterDiscount = customerBalanceBeforeDiscount.subtract(deliveryChargedBeforeDiscount);

        /* 15. ====== Customer Pays ========*/
        BigDecimal customerPays = ZERO;
        BigDecimal payWithMerchantServiceCharge = totalOrder.add(BigDecimalUtil.percentageOf(totalOrder, merchantServicePct));
        BigDecimal payWithMerchantServiceAndVat = payWithMerchantServiceCharge.add(BigDecimalUtil.percentageOf(payWithMerchantServiceCharge, merchantVatPct));
        customerPays = payWithMerchantServiceAndVat.add(deliveryChargedAfterDiscount).add(serviceFeeAmt);

        /* 15. ======= Paid to Courier ====== */
        BigDecimal paidToCourier = ZERO;
        if(BigDecimalUtil.isGreaterThen(BigDecimalUtil.percentageOf(deliveryCostWithoutAdditionalDvAmt, DBOY_COMMISSION), DBOY_MIN_AMOUNT))
               paidToCourier = BigDecimalUtil.percentageOf(deliveryCostWithoutAdditionalDvAmt, DBOY_COMMISSION).add(additionalDeliveryAmt);
        else
            paidToCourier = DBOY_MIN_AMOUNT.add(additionalDeliveryAmt);
        paidToCourier = paidToCourier.setScale(0, BigDecimal.ROUND_DOWN);

        /* 16 ===== Profit ====== */
        //FIXME:
        // min profit % from system preferences
        // total order * profit% = >  actual profit
        BigDecimal profit = ZERO;
        profit = BigDecimalUtil.percentageOf(totalOrder, commissionPct).add(deliveryChargedBeforeDiscount).add(serviceFeeAmt).subtract(paidToCourier);

        if(BigDecimalUtil.isLessThen(profit, BigDecimal.ONE))
            throw new YSException("ORD001");
        //TODO: Set To Database and return

        CourierTransactionEntity courierTransactionEntity = new CourierTransactionEntity();
//        courierTransactionEntity.setOrderId(1);
//        courierTransactionEntity.setCustomerId(2);
//        courierTransactionEntity.setDeliveryBoyId(2);
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
        courierTransactionEntity.setCustomerBalanceAfterDiscount(customerBalanceAfterDiscount.setScale(2, BigDecimal.ROUND_UP));
        courierTransactionEntity.setCustomerPays(customerPays.setScale(2, BigDecimal.ROUND_UP));
        courierTransactionEntity.setPaidToCourier(paidToCourier);
        courierTransactionEntity.setProfit(profit);

//        System.out.println("Order total = " + courierTransactionEntity.getOrderTotal());
//        System.out.println("Commission Percent = " + courierTransactionEntity.getCommissionPct());
//        System.out.println("Distance (store to customer) (KM) "+ courierTransactionEntity.getStoreToCustomerDistance());
//        System.out.println("Distance Courier to Store (KM) " + courierTransactionEntity.getCourierToStoreDistance());
//        System.out.println("Service Fee % "+ courierTransactionEntity.getServiceFeePct());
//        System.out.println("Additional delivery amt "+ courierTransactionEntity.getAdditionalDeliveryAmt());
//        System.out.println("Discount on delivery to customer " + courierTransactionEntity.getCustomerDiscount());
//        System.out.println("Surge Factor " + courierTransactionEntity.getSurgeFactor());
//        System.out.println("Delivery cost (Does not include additional delivery amt) " + courierTransactionEntity.getDeliveryCostWithoutAdditionalDvAmt());
//        System.out.println("Service fee Amount " + courierTransactionEntity.getServiceFeeAmt());
//        System.out.println("Delivery charged to customer before Discount "+ courierTransactionEntity.getDeliveryChargedBeforeDiscount());
//        System.out.println("Customer Available balance before discount " + courierTransactionEntity.getCustomerBalanceBeforeDiscount());
//        System.out.println("Delivery charged to customer After Discount "+ courierTransactionEntity.getDeliveryChargedAfterDiscount());
//        System.out.println("Customer available balance after discount "+ courierTransactionEntity.getCustomerBalanceAfterDiscount());
//        System.out.println("Customer Pays " + courierTransactionEntity.getCustomerPays());
//        System.out.println("Paid to Courier " + courierTransactionEntity.getPaidToCourier());
//        System.out.println("Profit "+ courierTransactionEntity.getProfit());


        return courierTransactionEntity;
    }

    @Override
    public void courierBoyAccounting(DeliveryBoyEntity deliveryBoy, OrderEntity order) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    public static void courierBoyAccountings(DeliveryBoyEntity deliveryBoy, OrderEntity order, MerchantEntity merchant, BigDecimal orderAmtReceived){
        BigDecimal orderAmount = order.getTotalCost();
        BigDecimal walletAmount = deliveryBoy.getWalletAmount();
        BigDecimal bankAmount = deliveryBoy.getBankAmount();
        BigDecimal availableAmount = deliveryBoy.getAvailableAmount();

        System.out.println("== BEFORE TAKING ORDER ==");
        System.out.print("\t \t Order Amount: "+ orderAmount);
        System.out.print("\t \t Wallet Amount: "+ walletAmount);
        System.out.print("\t \t Bank Amount: "+ bankAmount);
        System.out.println("\t \t Available Amount: "+ availableAmount);

        if(!merchant.getPartnershipStatus()){
            if(availableAmount.compareTo(orderAmount) != -1){
                System.out.print("Sufficient Amount in ==>> ");
                if(walletAmount.compareTo(orderAmount) != -1){
                    System.out.println("[[WALLET]]");
                    walletAmount = walletAmount.subtract(orderAmount);
                }else{
                    System.out.println("[[BANK]]");
                    bankAmount = bankAmount.subtract(orderAmount.subtract(walletAmount));
                    walletAmount = BigDecimal.ZERO;
                }
                availableAmount = availableAmount.subtract(orderAmount);
            }else{
                System.err.println("WARN: Insufficient Amount");
            }
        }

        System.out.println("== AFTER TAKING ORDER ==");
        System.out.print("\t \t Order Amount: "+ orderAmount);
        System.out.print("\t \t Wallet Amount: "+ walletAmount);
        System.out.print("\t \t Bank Amount: "+ bankAmount);
        System.out.println("\t \t Available Amount: "+ availableAmount);

        availableAmount = availableAmount.add(orderAmtReceived);
        walletAmount =walletAmount.add(orderAmtReceived);
        deliveryBoy.setPreviousDue(walletAmount);
        deliveryBoy.setBankAmount(bankAmount);
        deliveryBoy.setWalletAmount(walletAmount);
        deliveryBoy.setAvailableAmount(availableAmount);

        System.out.println("== AFTER ORDER DELIVERY ==");
        System.out.print("\t \t Wallet Amount: "+ walletAmount);
        System.out.print("\t \t Bank Amount: "+ bankAmount);
        System.out.print("\t \t Available Amount: "+ availableAmount);
        System.out.println("\t \t Amount to be Submitted: "+ walletAmount);
        System.out.println("=================================================================");
    }

    public static void main(String args[]){
        DeliveryBoyEntity dboy = new DeliveryBoyEntity();
        OrderEntity order = new OrderEntity();
        //order.getStore().getStoresBrand().getMerchant();
        MerchantEntity merchant = new MerchantEntity();


        dboy.setId(1);
        dboy.setAvailableAmount(new BigDecimal(10000));
        dboy.setBankAmount(new BigDecimal(10000));
        dboy.setWalletAmount(new BigDecimal(0));

        for(int i=0; i<5; i++){
            System.out.print("Enter Order Amount : ");
            Scanner scanInput = new Scanner(System.in);
            String orderAmt = scanInput.nextLine();
            order.setTotalCost(new BigDecimal(orderAmt));

            System.out.print("Enter Amount to be paid by customer: ");
            scanInput = new Scanner(System.in);
            String orderAmtReceived = scanInput.nextLine();

            System.out.print("Is merchant a partner: ");
            scanInput = new Scanner(System.in);
            String isPartner = scanInput.nextLine();
            if(isPartner.equals("1") || isPartner.equalsIgnoreCase("true") || isPartner.equalsIgnoreCase("Y")){
                merchant.setPartnershipStatus(true);
            }else{
                merchant.setPartnershipStatus(false);
            }
            courierBoyAccountings(dboy, order, merchant, new BigDecimal(orderAmtReceived));
        }



    }

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
        BigDecimal totalOrder = order.getTotalCost();

        /* 2. ======= Commission Percent ====== */
        BigDecimal commissionPct = BigDecimalUtil.checkNull(merchantCommission);

        /* 3. ===== Distance Store to Customer(KM) ======== */
        BigDecimal storeToCustomerDistance =  dBoySelection.getStoreToCustomerDistance();

        /* 4. ====== Distance Courier to Store (KM) ======== */
        BigDecimal courierToStoreDistance = dBoySelection.getDistanceToStore();

        /* 5. ==== Service Fee % ======= */
        BigDecimal serviceFeePct = BigDecimalUtil.checkNull(merchantServiceFee);

        /* 6. ====== Additional delivery amt ======= */
        BigDecimal additionalDeliveryAmt = ZERO;
        if(BigDecimalUtil.isGreaterThen(courierToStoreDistance, ADDITIONAL_KM_FREE_LIMIT))
            additionalDeliveryAmt = courierToStoreDistance.subtract(ADDITIONAL_KM_FREE_LIMIT).multiply(DBOY_ADDITIONAL_PER_KM_CHARGE);

       /* 7. ==== Discount on delivery to customer ======= */
        BigDecimal customerDiscount = ZERO;
        BigDecimal systemReservedCommissionAmt = ZERO;
        if(BigDecimalUtil.isGreaterThenZero(commissionPct)){
            BigDecimal totalCommission = BigDecimalUtil.percentageOf(totalOrder, commissionPct);
            systemReservedCommissionAmt = BigDecimalUtil.percentageOf(totalCommission, RESERVED_COMM_PER_BY_SYSTEM);
            customerDiscount = totalCommission.subtract(systemReservedCommissionAmt);
        }

        /* 8. ==== Surge Factor ======= */
        Integer surgeFactor = order.getSurgeFactor();

        /* 9. ====== Delivery cost (Does not include additional delivery amt) ============== */
        BigDecimal deliveryCostWithoutAdditionalDvAmt = ZERO;
        if(BigDecimalUtil.isLessThen(storeToCustomerDistance, DEFAULT_NKM_DISTANCE))
            deliveryCostWithoutAdditionalDvAmt = DBOY_PER_KM_CHARGE_UPTO_NKM.multiply(new BigDecimal(surgeFactor));
        else
            deliveryCostWithoutAdditionalDvAmt = storeToCustomerDistance.multiply(DBOY_PER_KM_CHARGE_ABOVE_NKM).multiply(new BigDecimal(surgeFactor));

        /* 10. ======= Service fee Amount with VAT =========== */
        BigDecimal serviceFeeAmt = BigDecimalUtil.percentageOf(totalOrder, serviceFeePct);
        serviceFeeAmt = serviceFeeAmt.add(BigDecimalUtil.percentageOf(serviceFeeAmt, DELIVERY_FEE_VAT));

        /* 11. ====== Delivery cost  with VAT and with Discount from system ======== */
        BigDecimal deliveryChargedBeforeDiscount = ZERO;
        if(BigDecimalUtil.isGreaterThenOrEqualTo(deliveryCostWithoutAdditionalDvAmt, customerDiscount)){
            deliveryChargedBeforeDiscount = deliveryCostWithoutAdditionalDvAmt.subtract(customerDiscount);
            deliveryChargedBeforeDiscount = deliveryChargedBeforeDiscount.add(BigDecimalUtil.percentageOf(deliveryChargedBeforeDiscount, DELIVERY_FEE_VAT));
        }

        /* 12. ======= Customer Available balance before discount ====== */
        //  BigDecimal customerBalanceBeforeDiscount = BigDecimalUtil.checkNull(order.getCustomer().getRewardsEarned());
        /* Set this to zero since no discount in this phase */
        BigDecimal customerBalanceBeforeDiscount = BigDecimal.ZERO;

        /* 13. ======== Delivery charged to customer After Discount with VAT ====== */
        BigDecimal deliveryChargedAfterDiscount = ZERO;
        if(BigDecimalUtil.isGreaterThenOrEqualTo(deliveryChargedBeforeDiscount, customerBalanceBeforeDiscount))
            deliveryChargedAfterDiscount = deliveryChargedBeforeDiscount.subtract(customerBalanceBeforeDiscount);


        /* 14. ======= Customer available balance after discount ======== */
        BigDecimal customerBalanceAfterDiscount = ZERO;
        if(BigDecimalUtil.isGreaterThen(customerBalanceBeforeDiscount, deliveryChargedBeforeDiscount))
            customerBalanceAfterDiscount = customerBalanceBeforeDiscount.subtract(deliveryChargedBeforeDiscount);

        /* 14. ====== Customer Pays ========*/
        BigDecimal customerPays = order.getTotalCost().add(serviceFeeAmt).add(deliveryChargedAfterDiscount).add(order.getItemServiceAndVatCharge());
        order.setGrandTotal(customerPays);
        order.setDeliveryCharge(deliveryChargedAfterDiscount);
        order.setSystemServiceCharge(serviceFeeAmt);

        /* 15. ======= Paid to Courier ====== */
        BigDecimal paidToCourier = ZERO;
        if(BigDecimalUtil.isGreaterThen(BigDecimalUtil.percentageOf(deliveryCostWithoutAdditionalDvAmt, DBOY_COMMISSION), DBOY_MIN_AMOUNT))
            paidToCourier = BigDecimalUtil.percentageOf(deliveryCostWithoutAdditionalDvAmt, DBOY_COMMISSION).add(additionalDeliveryAmt);
        else
            paidToCourier = DBOY_MIN_AMOUNT.add(additionalDeliveryAmt);
        paidToCourier = paidToCourier.setScale(0, BigDecimal.ROUND_DOWN);
        dBoySelection.setPaidToCourier(paidToCourier);

        /* 16 ===== Profit ====== */
        // total order * profit% = >  actual profit
        BigDecimal profit = ZERO;
        profit =   BigDecimalUtil.percentageOf(totalOrder, commissionPct).add(BigDecimalUtil.percentageOf(totalOrder, serviceFeePct));
        profit =  profit.add(deliveryChargedAfterDiscount.divide(BigDecimal.ONE.add(DELIVERY_FEE_VAT.divide(new BigDecimal(100))), MathContext.DECIMAL128)).subtract(paidToCourier);
        if(BigDecimalUtil.isLessThen(profit, BigDecimalUtil.percentageOf(totalOrder, MINIMUM_PROFIT_PERCENTAGE))){
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
}
