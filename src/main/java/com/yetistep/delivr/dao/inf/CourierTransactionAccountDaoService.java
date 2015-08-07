package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.CourierTransactionAccountEntity;
import com.yetistep.delivr.model.Page;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 8/5/15
 * Time: 5:00 PM
 * To change this template use File | Settings | File Templates.
 */
public interface CourierTransactionAccountDaoService extends GenericDaoService<Integer, CourierTransactionAccountEntity> {

    public Integer getTotalNumberOfRows(Integer shopperId) throws Exception;

    public List<CourierTransactionAccountEntity> findAllCTA(Integer shopperId, Page page) throws Exception;
}
