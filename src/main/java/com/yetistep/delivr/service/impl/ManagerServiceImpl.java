package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.dao.inf.ActionLogDaoService;
import com.yetistep.delivr.dao.inf.DeliveryBoyDaoService;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.PaginationDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.service.inf.ManagerService;
import com.yetistep.delivr.util.YSException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/25/14
 * Time: 10:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class ManagerServiceImpl implements ManagerService {
    private static final Logger log = Logger.getLogger(ManagerServiceImpl.class);
    @Autowired
    ActionLogDaoService actionLogDaoService;

    @Autowired
    DeliveryBoyDaoService deliveryBoyDaoService;

    @Override
    public PaginationDto getActionLog(Page page) throws Exception {
        log.info("Retrieving list of action logs:");
        PaginationDto paginationDto = new PaginationDto();
        paginationDto.setNumberOfRows(actionLogDaoService.getTotalNumberOfActionLogs());
        List<ActionLogEntity> actionLogEntities;
        if(page != null){
            page.setTotalRows(actionLogDaoService.getTotalNumberOfActionLogs());
            actionLogEntities = actionLogDaoService.findActionLogPaginated(page);
        }else{
            actionLogEntities = actionLogDaoService.findAll();
        }
        paginationDto.setData(actionLogEntities);
        return paginationDto;
    }

    @Override
    public DeliveryBoyEntity updateDboyAccount(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception {

        DeliveryBoyEntity dBoy = deliveryBoyDaoService.find(Integer.parseInt(headerDto.getId()));

        if(dBoy.getPreviousDue().compareTo(BigDecimal.ZERO) != 0)
            throw new YSException("ERR015");

        dBoy.setAdvanceAmount(requestJsonDto.getAdvanceAmount());
        dBoy.setBankAmount(dBoy.getBankAmount().add(requestJsonDto.getAdvanceAmount()));

        List<DBoyAdvanceAmountEntity> dBoyAdvanceAmounts = new ArrayList<DBoyAdvanceAmountEntity>();
        DBoyAdvanceAmountEntity dBoyAdvanceAmount = new DBoyAdvanceAmountEntity();
        dBoyAdvanceAmount.setAmountAdvance(requestJsonDto.getAdvanceAmount());
        dBoyAdvanceAmount.setDeliveryBoy(dBoy);
        dBoyAdvanceAmounts.add(dBoyAdvanceAmount);

        dBoy.setdBoyAdvanceAmounts(dBoyAdvanceAmounts);

        deliveryBoyDaoService.update(dBoy);

        return dBoy;
    }

    @Override
    public DeliveryBoyEntity ackSubmittedAmount(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception {
        DeliveryBoyEntity dBoy = deliveryBoyDaoService.find(Integer.parseInt(headerDto.getId()));

        dBoy.setPreviousDue(BigDecimal.ZERO);

        List<DBoySubmittedAmountEntity> dBoySubmittedAmounts = new ArrayList<DBoySubmittedAmountEntity>();
        DBoySubmittedAmountEntity dBoySubmittedAmount = new DBoySubmittedAmountEntity();
        dBoySubmittedAmount.setAmountReceived(requestJsonDto.getSubmittedAmount());
        dBoySubmittedAmount.setDeliveryBoy(dBoy);
        dBoySubmittedAmounts.add(dBoySubmittedAmount);
        dBoy.setdBoySubmittedAmount(dBoySubmittedAmounts);

        deliveryBoyDaoService.update(dBoy);

        return dBoy;
    }

    @Override
    public DeliveryBoyEntity walletSubmittedAmount(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception {
        DeliveryBoyEntity dBoy = deliveryBoyDaoService.find(Integer.parseInt(headerDto.getId()));

        dBoy.setWalletAmount(BigDecimal.ZERO);

        List<DBoySubmittedAmountEntity> dBoySubmittedAmounts = new ArrayList<DBoySubmittedAmountEntity>();
        DBoySubmittedAmountEntity dBoySubmittedAmount = new DBoySubmittedAmountEntity();
        dBoySubmittedAmount.setAmountReceived(requestJsonDto.getSubmittedAmount());
        dBoySubmittedAmount.setDeliveryBoy(dBoy);
        dBoySubmittedAmounts.add(dBoySubmittedAmount);
        dBoy.setdBoySubmittedAmount(dBoySubmittedAmounts);

        deliveryBoyDaoService.update(dBoy);

        return dBoy;
    }
}
