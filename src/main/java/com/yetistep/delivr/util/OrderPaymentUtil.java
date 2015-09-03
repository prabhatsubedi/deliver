package com.yetistep.delivr.util;

import com.yetistep.delivr.model.OrderEntity;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 9/2/15
 * Time: 11:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class OrderPaymentUtil {

    public static void calculateOrderWalletPayment(OrderEntity orderEntity) throws Exception{
        BigDecimal paidByBonusAmount = BigDecimal.ZERO;
        BigDecimal paidByCashBackAmount = BigDecimal.ZERO;
        BigDecimal paidByFundTransferAmount = BigDecimal.ZERO;
        BigDecimal remainingAmount = orderEntity.getGrandTotal();

        //first priority to bonus
        if(BigDecimalUtil.isGreaterThenOrEqualTo(BigDecimalUtil.checkNull(orderEntity.getCustomer().getRmBonusAmount()), remainingAmount)){
            paidByBonusAmount = remainingAmount;
            remainingAmount = BigDecimal.ZERO;
        } else {
            paidByBonusAmount = BigDecimalUtil.checkNull(orderEntity.getCustomer().getRmBonusAmount());
            remainingAmount = remainingAmount.subtract(paidByBonusAmount);
        }

        //second priority to cash back
        if(BigDecimalUtil.isGreaterThenOrEqualTo(BigDecimalUtil.checkNull(orderEntity.getCustomer().getRmCashBackAmount()), remainingAmount)){
            paidByCashBackAmount = remainingAmount;
            remainingAmount = BigDecimal.ZERO;
        } else {
            paidByCashBackAmount = BigDecimalUtil.checkNull(orderEntity.getCustomer().getRmCashBackAmount());
            remainingAmount = remainingAmount.subtract(paidByCashBackAmount);
        }


        //third priority to cash back
        if(BigDecimalUtil.isGreaterThenOrEqualTo(BigDecimalUtil.checkNull(orderEntity.getCustomer().getRmFundTransferAmount()), remainingAmount)){
            paidByFundTransferAmount = remainingAmount;
            remainingAmount = BigDecimal.ZERO;
        } else {
            paidByFundTransferAmount = BigDecimalUtil.checkNull(orderEntity.getCustomer().getRmFundTransferAmount());
            remainingAmount = remainingAmount.subtract(paidByFundTransferAmount);
        }

        orderEntity.setPaidBonusAmount(paidByBonusAmount);
        orderEntity.setPaidCashBackAmount(paidByCashBackAmount);
        orderEntity.setPaidFundTransferAmount(paidByFundTransferAmount);

    }

    public static void calculateOrderWalletPaymentOnOrderCancel(OrderEntity orderEntity) throws Exception{
        BigDecimal paidByBonusAmount = BigDecimal.ZERO;
        BigDecimal paidByCashBackAmount = BigDecimal.ZERO;
        BigDecimal paidByFundTransferAmount = BigDecimal.ZERO;
        BigDecimal remainingAmount = orderEntity.getGrandTotal();

        //first priority to bonus
        if(BigDecimalUtil.isGreaterThenOrEqualTo(BigDecimalUtil.checkNull(orderEntity.getCustomer().getRmBonusAmount()), remainingAmount)){
            paidByBonusAmount = remainingAmount;
            remainingAmount = BigDecimal.ZERO;
        } else {
            paidByBonusAmount = BigDecimalUtil.checkNull(orderEntity.getCustomer().getRmBonusAmount());
            remainingAmount = remainingAmount.subtract(paidByBonusAmount);
        }

        //second priority to cash back
        if(BigDecimalUtil.isGreaterThenOrEqualTo(BigDecimalUtil.checkNull(orderEntity.getCustomer().getRmCashBackAmount()), remainingAmount)){
            paidByCashBackAmount = remainingAmount;
            remainingAmount = BigDecimal.ZERO;
        } else {
            paidByCashBackAmount = BigDecimalUtil.checkNull(orderEntity.getCustomer().getRmCashBackAmount());
            remainingAmount = remainingAmount.subtract(paidByCashBackAmount);
        }

        //third priority to cash back
        paidByFundTransferAmount = remainingAmount;

        orderEntity.setPaidBonusAmount(paidByBonusAmount);
        orderEntity.setPaidCashBackAmount(paidByCashBackAmount);
        orderEntity.setPaidFundTransferAmount(paidByFundTransferAmount);

    }


    public static void calculateOrderPayment(OrderEntity order) throws Exception {
        if(BigDecimalUtil.isGreaterThenOrEqualTo(BigDecimalUtil.checkNull(order.getCustomer().getWalletAmount()),order.getGrandTotal())){
            OrderPaymentUtil.calculateOrderWalletPayment(order);
            order.setPaidFromWallet(order.getGrandTotal());
            order.setPaidFromCOD(BigDecimal.ZERO);
        } else if(BigDecimalUtil.isGreaterThen(BigDecimalUtil.checkNull(order.getCustomer().getWalletAmount()), BigDecimal.ZERO)){
            OrderPaymentUtil.calculateOrderWalletPayment(order);
            BigDecimal paidFromWallet = order.getCustomer().getWalletAmount();
            BigDecimal paidFromCOD = order.getGrandTotal().subtract(paidFromWallet);
            order.setPaidFromWallet(paidFromWallet);
            order.setPaidFromCOD(paidFromCOD);
        } else {
            order.setPaidFromWallet(BigDecimal.ZERO);
            order.setPaidFromCOD(order.getGrandTotal());
        }
    }

}
