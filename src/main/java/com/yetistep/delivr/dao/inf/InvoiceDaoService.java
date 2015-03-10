package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.InvoiceEntity;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 2/23/15
 * Time: 11:41 AM
 * To change this template use File | Settings | File Templates.
 */
public interface InvoiceDaoService extends GenericDaoService<Integer, InvoiceEntity> {
    public List<InvoiceEntity> findInvoicesByMerchant(Integer merchantId) throws Exception;
}
