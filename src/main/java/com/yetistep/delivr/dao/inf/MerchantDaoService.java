package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.model.MerchantEntity;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 12:21 PM
 * To change this template use File | Settings | File Templates.
 */
public interface MerchantDaoService {
    public void saveMerchant(MerchantEntity merchant) throws SQLException;
}
