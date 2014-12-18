package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.model.DeliveryBoyEntity;
import com.yetistep.delivr.model.MerchantEntity;
import com.yetistep.delivr.model.OrderEntity;
import com.yetistep.delivr.service.inf.SystemAlgorithmService;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/17/14
 * Time: 12:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class SystemAlgorithmServiceImpl implements SystemAlgorithmService{

    @Override
    public void calculateCourierEarning() throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
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
}
