package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.model.DeliveryBoyEntity;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 12:20 PM
 * To change this template use File | Settings | File Templates.
 */
public interface DeliveryBoyDaoService {
    public void saveDeliveryBoy(DeliveryBoyEntity deliveryBoy) throws SQLException;
}
