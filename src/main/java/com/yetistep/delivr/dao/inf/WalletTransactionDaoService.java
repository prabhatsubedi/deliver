package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.WalletTransactionEntity;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 4/1/15
 * Time: 10:46 AM
 * To change this template use File | Settings | File Templates.
 */
public interface WalletTransactionDaoService extends GenericDaoService<Integer, WalletTransactionEntity> {
     public WalletTransactionEntity getLatestWalletTransaction(Integer customerId) throws Exception;
}





