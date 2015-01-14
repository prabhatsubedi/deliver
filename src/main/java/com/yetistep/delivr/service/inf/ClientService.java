package com.yetistep.delivr.service.inf;

import com.yetistep.delivr.dao.impl.ItemDaoServiceImpl;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.model.CartEntity;
import com.yetistep.delivr.model.ItemEntity;
import com.yetistep.delivr.model.OrderEntity;
import com.yetistep.delivr.model.StoresBrandEntity;
import com.yetistep.delivr.model.mobile.CategoryDto;
import com.yetistep.delivr.model.mobile.dto.CartDto;
import com.yetistep.delivr.model.mobile.dto.ItemDto;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/9/14
 * Time: 12:37 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ClientService {
    public Map<String, Object> getBrands(RequestJsonDto requestJsonDto) throws Exception;

    public List<CategoryDto> getParentCategory(Integer brandId) throws Exception;

    public List<CategoryDto> getSubCategory(CategoryDto categoryDto) throws Exception;


    public OrderEntity getOrderById(Integer orderId) throws Exception;

    public List<ItemDto> getItems(Integer brandId, Integer categoryId) throws Exception;

    public ItemEntity getItemDetail(Integer itemId) throws Exception;

    public void saveCart(CartEntity cart) throws Exception;

    public CartDto getMyCart(Long facebookId) throws Exception;

    public void deleteCart(Integer cartId) throws Exception;

    public CartDto getCartSize(Long facebookId) throws Exception;

    public CartDto getCartDetail(Integer cartId) throws Exception;


}
