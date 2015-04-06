package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.BillEntity;
import com.yetistep.delivr.model.InvoiceEntity;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 3/3/15
 * Time: 9:41 AM
 * To change this template use File | Settings | File Templates.
 */
public interface BillDaoService extends GenericDaoService<Integer, BillEntity> {
    public List<BillEntity> getBillByOrder(Integer orderId) throws Exception;
}
