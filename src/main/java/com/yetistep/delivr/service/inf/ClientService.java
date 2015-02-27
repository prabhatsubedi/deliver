package com.yetistep.delivr.service.inf;

import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.dto.OrderSummaryDto;
import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.model.CartEntity;
import com.yetistep.delivr.model.ItemEntity;
import com.yetistep.delivr.model.OrderCancelEntity;
import com.yetistep.delivr.model.mobile.CategoryDto;
import com.yetistep.delivr.model.mobile.dto.CartDto;
import com.yetistep.delivr.model.mobile.dto.ItemDto;
import com.yetistep.delivr.model.mobile.dto.PreferenceDto;

import java.util.ArrayList;
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

    public OrderSummaryDto getOrderSummaryById(Integer orderId, Long facebookId) throws Exception;

    public List<ItemDto> getItems(Integer brandId, Integer categoryId) throws Exception;

    public ItemEntity getItemDetail(Integer itemId) throws Exception;

    public void saveCart(CartEntity cart) throws Exception;

    public CartDto getMyCart(Long facebookId) throws Exception;

    public CartDto validateCart(Long facebookId) throws Exception;

    public void deleteCart(Integer cartId) throws Exception;

    public void deleteAllCart(Long facebookI) throws Exception;

    public CartDto getCartSize(Long facebookId) throws Exception;

    public CartDto getCartDetail(Integer cartId) throws Exception;

    public void updateCart(CartEntity cart) throws Exception;

    public String inviteFriend(HeaderDto headerDto, ArrayList<String> emailList) throws Exception;

    public Boolean updateUserDeviceToken(Long facebookId, String deviceToken) throws Exception;

    public Boolean updateUserDeviceTokenFromUserId(Integer userId, String deviceToken) throws Exception;

    public String getCurrencyType() throws Exception;

    public Boolean reOrder(Integer orderId, Long facebookId, Boolean flushCart) throws Exception;

    public OrderCancelEntity orderCancelDetails(Integer orderId) throws Exception;

    public PreferenceDto getHelpLineDetails() throws Exception;
}
