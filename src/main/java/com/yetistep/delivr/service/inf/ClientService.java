package com.yetistep.delivr.service.inf;

import com.yetistep.delivr.dto.RequestJsonDto;
import com.yetistep.delivr.model.OrderEntity;

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

    public OrderEntity getOrderById(Integer orderId) throws Exception;
}
