package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.dao.inf.ActionLogDaoService;
import com.yetistep.delivr.dao.inf.DeliveryBoyDaoService;
import com.yetistep.delivr.dao.inf.StoresBrandDaoService;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.PaginationDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.service.inf.ManagerService;
import com.yetistep.delivr.util.YSException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Autowired
    StoresBrandDaoService storesBrandDaoService;

    @Override
    public PaginationDto getActionLog(Page page) throws Exception {
        log.info("Retrieving list of action logs:");
        PaginationDto paginationDto = new PaginationDto();
        Integer totalRows = actionLogDaoService.getTotalNumberOfActionLogs();
        paginationDto.setNumberOfRows(totalRows);
        List<ActionLogEntity> actionLogEntities;
        if(page != null){
            page.setTotalRows(totalRows);
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
        dBoy.setAvailableAmount(dBoy.getAvailableAmount().add(requestJsonDto.getAdvanceAmount()));

        List<DboyAdvanceAmountEntity> dBoyAdvanceAmounts = new ArrayList<DboyAdvanceAmountEntity>();
        DboyAdvanceAmountEntity dBoyAdvanceAmount = new DboyAdvanceAmountEntity();
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

        List<DboySubmittedAmountEntity> dBoySubmittedAmounts = new ArrayList<DboySubmittedAmountEntity>();
        DboySubmittedAmountEntity dBoySubmittedAmount = new DboySubmittedAmountEntity();
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

        List<DboySubmittedAmountEntity> dBoySubmittedAmounts = new ArrayList<DboySubmittedAmountEntity>();
        DboySubmittedAmountEntity dBoySubmittedAmount = new DboySubmittedAmountEntity();

        dBoySubmittedAmount.setAmountReceived(requestJsonDto.getSubmittedAmount());
        dBoySubmittedAmount.setDeliveryBoy(dBoy);
        dBoySubmittedAmounts.add(dBoySubmittedAmount);
        dBoy.setdBoySubmittedAmount(dBoySubmittedAmounts);

        deliveryBoyDaoService.update(dBoy);

        return dBoy;
    }

    @Override
    public List<StoresBrandEntity> findFeaturedAndPrioritizedStoreBrands() throws Exception {
        return storesBrandDaoService.findFeaturedAndPriorityBrands();
    }

    @Override
    public PaginationDto findNonFeaturedAndPrioritizedStoreBrands(Page page) throws Exception {
        log.info("Retrieving list of Non Featured & prioritized store brands");
        PaginationDto paginationDto = new PaginationDto();
        Integer totalRows = storesBrandDaoService.getTotalNumberOfStoreBrands();
        paginationDto.setNumberOfRows(totalRows);
        List<StoresBrandEntity> storesBrandEntities;
        if(page != null){
            page.setTotalRows(totalRows);
        }
        storesBrandEntities = storesBrandDaoService.findExceptFeaturedAndPriorityBrands(page);
        paginationDto.setData(storesBrandEntities);
        return paginationDto;
    }

    @Override
    public Boolean updateFeatureAndPriorityOfStoreBrands(List<StoresBrandEntity> storesBrands) throws Exception {
        checkDuplicatePriorities(storesBrands);
        return storesBrandDaoService.updateFeatureAndPriorityOfStoreBrands(storesBrands);
    }

    /**
     * checks duplicate priority as well as checks both featured and prioritized restrictions
     */
    private Boolean checkDuplicatePriorities(List<StoresBrandEntity> storesBrands) throws Exception{
        Set<Integer> priorities = new HashSet<Integer>();
        for(StoresBrandEntity storesBrand: storesBrands){
            if(storesBrand.getPriority() != null){
                if(storesBrand.getFeatured() != null && storesBrand.getFeatured())
                    throw new YSException("VLD019");
                if(!priorities.add(storesBrand.getPriority()))
                    throw new YSException("VLD018");
            }
        }
        return true;
    }
}
