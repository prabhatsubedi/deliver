package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.enums.DBoyStatus;
import com.yetistep.delivr.enums.DeliveryStatus;
import com.yetistep.delivr.enums.JobOrderStatus;
import com.yetistep.delivr.model.OrderEntity;
import org.hibernate.Session;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 2/3/15
 * Time: 2:40 PM
 * To change this template use File | Settings | File Templates.
 */
public interface AdminDaoService {

    public Session getCurrentSession() throws Exception;

    public Integer getRegisteredUserCount() throws Exception;

    public Integer getNewUserCount(Integer dayCount) throws Exception;

    public Integer getActiveUserCount() throws Exception;

    public Integer getPartnerStoreCount() throws Exception;

    public Integer getDBoyCount(List<Integer> statuses) throws Exception;

    public Integer getOrderCount(List<Integer> status) throws Exception;

    public Integer getOrderCount(Integer status) throws Exception;

    public Integer getOnTimeCount(String type) throws Exception;

    public Integer getOrderTotalTime() throws Exception;

    public Integer getOrderByDayCount(Integer dayCount, Integer prev) throws Exception;

    public Integer getTodayOrderCount() throws Exception;

    public Integer getTodayOrderTotalTime() throws Exception;

    public List<OrderEntity> getOrderRoute(List<JobOrderStatus> status) throws Exception;

    public Integer getOrderTotalTimeByDay(Integer dayCount, Integer prev) throws Exception;

    public Integer getNewUserByDayCount(Integer dayCount, Integer prev) throws Exception;

    public Integer getOrderByDayCount(List<JobOrderStatus> orderStatuses, Integer dayCount, Integer prev) throws Exception;

}
