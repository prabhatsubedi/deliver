package com.yetistep.delivr.service.inf;

import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.model.OrderEntity;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/25/14
 * Time: 10:37 AM
 * To change this template use File | Settings | File Templates.
 */
public interface AccountService {

    public String getGenerateInvoice(HeaderDto headerDto) throws Exception;

    public String generateBillAndReceiptAndSendEmail(OrderEntity order) throws Exception;

}
