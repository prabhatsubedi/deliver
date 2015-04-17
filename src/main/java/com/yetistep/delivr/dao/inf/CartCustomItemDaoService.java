package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.CartCustomItemEntity;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 4/13/15
 * Time: 1:04 PM
 * To change this template use File | Settings | File Templates.
 */
public interface CartCustomItemDaoService extends GenericDaoService<Integer, CartCustomItemEntity> {

    public CartCustomItemEntity findCustomItem(Integer cartId) throws Exception;

    public Boolean deleteCartCustomItem(Integer cartId) throws Exception;

    public List<Integer> findCartCustomItems(List<Integer> carts) throws Exception;

    public Boolean deleteCartCustomItems(List<Integer> cartcustomItems) throws Exception;

}
