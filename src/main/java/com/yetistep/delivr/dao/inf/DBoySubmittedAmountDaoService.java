package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.DBoySubmittedAmountEntity;
import com.yetistep.delivr.model.DeliveryBoyEntity;
import com.yetistep.delivr.model.Page;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 4/1/15
 * Time: 10:09 AM
 * To change this template use File | Settings | File Templates.
 */
public interface DBoySubmittedAmountDaoService  extends GenericDaoService<Integer, DeliveryBoyEntity> {

    public Integer getTotalNumbersAcknowledgements(Integer dBoyId) throws Exception;

    public List<DBoySubmittedAmountEntity> getcknowledgements(Integer dBoyId, Page page) throws Exception;
}
