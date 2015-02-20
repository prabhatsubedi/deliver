package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.RatingEntity;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 2/20/15
 * Time: 10:41 AM
 * To change this template use File | Settings | File Templates.
 */
public interface RatingDaoService extends GenericDaoService<Integer, RatingEntity> {
    public RatingEntity getCustomerSideRatingFromOrderId(Integer orderId) throws Exception;
}
