package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.CartEntity;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 1/8/15
 * Time: 12:00 PM
 * To change this template use File | Settings | File Templates.
 */
public interface CartDaoService extends GenericDaoService<Integer, CartEntity>{
    public List<Integer> findCarts(Long clientFBId, Integer brandId) throws Exception;

    public Boolean deleteCarts(List<Integer> carts) throws Exception;

    public List<CartEntity> getMyCarts(Long facebookId) throws Exception;

    public List<Integer> findCarts(Long fbId, Integer itemId, Integer brandId, String note) throws Exception;

    public Boolean updateOrderQuantity(Integer cartId, Integer additionalQuantity) throws Exception;

    public CartEntity findCart(Integer cartId) throws Exception;

    public Integer getAvailableOrderItem(Integer cartId) throws Exception;

    public Boolean updateCartWithOrder(List<Integer> cartIds, Integer orderId) throws Exception;

}
