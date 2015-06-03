package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.InvoiceEntity;
import com.yetistep.delivr.model.Page;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 2/23/15
 * Time: 11:41 AM
 * To change this template use File | Settings | File Templates.
 */
public interface InvoiceDaoService extends GenericDaoService<Integer, InvoiceEntity> {

    public List<InvoiceEntity> findInvoicesByMerchant(Integer merchantId, Page page, Date fromDate, Date toDate) throws Exception;

    public List<InvoiceEntity> findInvoicesByMerchant(Integer merchantId, Page page) throws Exception;

    public Integer getTotalNumberOfInvoices(Integer merchantId) throws Exception;

    public List<InvoiceEntity> findInvoices(Page page) throws Exception;

    public List<InvoiceEntity> findInvoices(Page page, Date fromDate, Date toDate) throws Exception;

    public Integer getTotalNumberOfInvoices() throws Exception;

    public Integer getTotalNumberOfInvoices(Date fromDate, Date toDate) throws Exception;

    public Integer getTotalNumberOfInvoices(Integer merchantId, Date fromDate, Date toDate) throws Exception;


}
