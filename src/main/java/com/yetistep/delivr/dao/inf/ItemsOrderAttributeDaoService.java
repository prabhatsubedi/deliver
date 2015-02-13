package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 2/13/15
 * Time: 2:51 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ItemsOrderAttributeDaoService extends GenericDaoService<Integer, ItemsOrderAttributeDaoService> {
    public List<Integer> getSelectedAttributesOfItemOrder(Integer itemOrderId) throws Exception;
}
