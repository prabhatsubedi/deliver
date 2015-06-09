package com.yetistep.delivr.dao.v2.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.DeliveryBoyEntity;
import com.yetistep.delivr.model.Page;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/21/14
 * Time: 12:20 PM
 * To change this template use File | Settings | File Templates.
 */
public interface DeliveryBoyDaoService extends GenericDaoService<Integer, DeliveryBoyEntity> {


    public List<DeliveryBoyEntity> findAll(Page page) throws Exception;

    public Integer getTotalNumberOfDboys(Page page) throws Exception;


}
