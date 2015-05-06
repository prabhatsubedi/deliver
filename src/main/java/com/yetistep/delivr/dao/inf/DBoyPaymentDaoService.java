package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.DBoyPaymentEntity;
import com.yetistep.delivr.model.InvoiceEntity;
import com.yetistep.delivr.model.Page;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 5/5/15
 * Time: 1:06 PM
 * To change this template use File | Settings | File Templates.
 */
public interface DBoyPaymentDaoService extends GenericDaoService<Integer, DBoyPaymentEntity> {

    public List<DBoyPaymentEntity> findAllOfShopper(Page page, Integer dBoyId) throws Exception;

    public Integer getTotalNumberOfPayStatements(Integer dBoyId) throws Exception;

}


