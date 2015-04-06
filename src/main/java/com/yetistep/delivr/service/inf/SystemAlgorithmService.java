package com.yetistep.delivr.service.inf;

import com.yetistep.delivr.model.*;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/17/14
 * Time: 12:27 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SystemAlgorithmService {
    public CourierTransactionEntity calculateCourierEarning() throws Exception;

    public void courierBoyAccounting(DeliveryBoyEntity deliveryBoy, OrderEntity order) throws Exception;

    public CourierTransactionEntity getCourierTransaction(OrderEntity order, DeliveryBoySelectionEntity dBoySelection, BigDecimal merchantCommission, BigDecimal merchantServiceFee)throws Exception;

    public void encodeWalletTransaction(WalletTransactionEntity walletTransactionEntity) throws Exception;

    public void decodeWalletTransaction(WalletTransactionEntity walletTransactionEntity) throws Exception;
}
