package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.CartAttributesEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 1/9/15
 * Time: 10:11 AM
 * To change this template use File | Settings | File Templates.
 */
public interface CartAttributesDaoService extends GenericDaoService<Integer, CartAttributesEntity> {
    public List<Integer> findCartAttributes(List<Integer> carts) throws Exception;

    public Boolean deleteCartAttributes(List<Integer> cartAttributes) throws Exception;

    public BigDecimal findAttributesPrice(Integer cartId) throws Exception;

    public List<Integer> findCartAttributes(Integer cartId) throws Exception;
}
