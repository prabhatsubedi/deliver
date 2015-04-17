package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.enums.PaymentMode;
import com.yetistep.delivr.model.Page;
import com.yetistep.delivr.model.WalletTransactionEntity;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 4/1/15
 * Time: 10:46 AM
 * To change this template use File | Settings | File Templates.
 */
public interface WalletTransactionDaoService extends GenericDaoService<Integer, WalletTransactionEntity> {
     public WalletTransactionEntity getLatestWalletTransaction(Integer customerId) throws Exception;

    public List<WalletTransactionEntity> getWalletTransactions(Page page, Long fbId, List<PaymentMode> paymentModes) throws Exception;

    public Integer getTotalNumberOfWalletTransactions(Long fbId, List<PaymentMode> paymentModes) throws Exception;
}





