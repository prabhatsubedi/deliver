package com.yetistep.delivr.schedular;

import com.yetistep.delivr.dao.inf.OrderDaoService;
import com.yetistep.delivr.enums.CancelReason;
import com.yetistep.delivr.enums.JobOrderStatus;
import com.yetistep.delivr.enums.PreferenceType;
import com.yetistep.delivr.model.OrderCancelEntity;
import com.yetistep.delivr.model.OrderEntity;
import com.yetistep.delivr.service.inf.SystemPropertyService;
import com.yetistep.delivr.util.DateUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 1/28/15
 * Time: 3:39 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
@Transactional
public class ScheduledProcessor {
    private static final Logger log = Logger.getLogger(ScheduledProcessor.class);

    @Autowired
    OrderDaoService orderDaoService;

    @Autowired
    SystemPropertyService systemPropertyService;

    @Autowired
    private ScheduleChanger scheduleChanger;

    public void process() {
        try {
            log.info("Looking for elapsed orders");

            Float timeInSeconds = 180f;
            Integer timeOut = timeInSeconds.intValue();
            List<OrderEntity> elapsedOrders = orderDaoService.getElapsedOrders(timeOut);
            for (OrderEntity order : elapsedOrders) {
                OrderCancelEntity orderCancel = new OrderCancelEntity();
                orderCancel.setReason(CancelReason.SYSTEM_CANCELLED_TIME_OUT.toString());
                orderCancel.setJobOrderStatus(order.getOrderStatus());
                orderCancel.setOrder(order);
                order.setOrderCancel(orderCancel);
                order.setOrderStatus(JobOrderStatus.CANCELLED);
                orderDaoService.update(order);
            }
            checkNextOrders(timeOut);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }


    private void checkNextOrders(Integer timeOut) throws Exception{
        OrderEntity pendingOrder = orderDaoService.getNextPendingOrder();
        if (pendingOrder != null) {
            int delay = DateUtil.findDelayDifference(pendingOrder.getOrderDate(), timeOut);
            if(delay > 0){
                log.info("Adding Delay");
                scheduleChanger.scheduleTask(delay);
            }else{
                log.info("running schedular again");
                this.process();
            }
        } else {
            log.info("Cancelling Task");
            scheduleChanger.cancelTask();
        }
    }
}