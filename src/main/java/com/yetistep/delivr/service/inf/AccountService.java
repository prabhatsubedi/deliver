package com.yetistep.delivr.service.inf;

import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.model.OrderEntity;
import com.yetistep.delivr.model.StoreEntity;

import java.sql.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/25/14
 * Time: 10:37 AM
 * To change this template use File | Settings | File Templates.
 */
public interface AccountService {

    public String generateInvoice(Integer storeId, String fromDate, String toDate, String serverUrl) throws Exception;

    public String generateBillAndReceiptAndSendEmail(OrderEntity order) throws Exception;

    List<StoreEntity> getAllStores() throws Exception;

}
