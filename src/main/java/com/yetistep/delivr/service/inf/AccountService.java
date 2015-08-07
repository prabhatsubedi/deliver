package com.yetistep.delivr.service.inf;

import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.PaginationDto;
import com.yetistep.delivr.dto.RequestJsonDto;
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

    public String generateInvoice(Integer storeId, String fromDate, String toDate) throws Exception;

    public String generateBillAndReceiptAndSendEmail(OrderEntity order) throws Exception;

    public void generatedBoyPayStatement(Integer dBoyId, String fromDate, String toDate) throws Exception;

    public List<StoreEntity> getAllStores() throws Exception;

    public void payDboy(HeaderDto headerDto) throws Exception;

    public void payInvoice(HeaderDto headerDto) throws Exception;

    public void settleMerchantsOrder(HeaderDto headerDto) throws Exception;

    public void saveDBoyTransactionNote(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception;

    public PaginationDto getDBoyPayStatement(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception;

    public void payDBoyPayStatement(HeaderDto headerDto) throws Exception;

    public PaginationDto getDBoyAccount(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception;

    public OrderEntity getOrder(HeaderDto headerDto) throws Exception;

    public List<OrderEntity> getOrdersAmountTransferred() throws Exception;

    public PaginationDto getShoppersTransactionAccount(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception;

}
