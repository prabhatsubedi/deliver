package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.DBoyAdvanceAmountEntity;
import com.yetistep.delivr.model.DeliveryBoyEntity;
import com.yetistep.delivr.model.Page;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 4/1/15
 * Time: 10:10 AM
 * To change this template use File | Settings | File Templates.
 */
public interface DBoyAdvanceAmountDaoService  extends GenericDaoService<Integer, DBoyAdvanceAmountEntity> {

    public Integer getTotalNumbersOfAdvanceAmounts(Integer dBoyId) throws Exception;

    public List<DBoyAdvanceAmountEntity> getAdvanceAmounts(Integer dBoyId, Page page) throws Exception;

    public Timestamp getLatestAckTimestamp(Integer dBoyId) throws Exception;
}
