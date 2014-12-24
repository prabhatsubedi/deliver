package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.dao.inf.ActionLogDaoService;
import com.yetistep.delivr.dao.inf.DeliveryBoyDaoService;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.PaginationDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.model.ActionLogEntity;
import com.yetistep.delivr.model.DeliveryBoyEntity;
import com.yetistep.delivr.model.Page;
import com.yetistep.delivr.service.inf.ManagerService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

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
        dBoy.setAdvanceAmount(requestJsonDto.getAdvanceAmount());
        dBoy.setBankAmount(dBoy.getBankAmount().add(requestJsonDto.getAdvanceAmount()));
        deliveryBoyDaoService.update(dBoy);

        return dBoy;
    }

    @Override
    public DeliveryBoyEntity ackSubmittedAmount(HeaderDto headerDto, RequestJsonDto requestJsonDto) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
