package com.yetistep.delivr.schedular;

import com.yetistep.delivr.dao.inf.OrderDaoService;
import com.yetistep.delivr.dao.inf.ReasonDetailsDaoService;
import com.yetistep.delivr.dao.inf.UserDeviceDaoService;
import com.yetistep.delivr.enums.*;
import com.yetistep.delivr.model.OrderCancelEntity;
import com.yetistep.delivr.model.OrderEntity;
import com.yetistep.delivr.model.ReasonDetailsEntity;
import com.yetistep.delivr.model.UserDeviceEntity;
import com.yetistep.delivr.service.inf.SystemPropertyService;
import com.yetistep.delivr.util.DateUtil;
import com.yetistep.delivr.util.MessageBundle;
import com.yetistep.delivr.util.PushNotificationUtil;
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
    UserDeviceDaoService userDeviceDaoService;

    @Autowired
    SystemPropertyService systemPropertyService;

    @Autowired
    private ScheduleChanger scheduleChanger;

    @Autowired
    private ReasonDetailsDaoService reasonDetailsDaoService;

    public void process() {
        try {
            log.info("Looking for elapsed orders");

            Float timeInSeconds = 180f;
            Integer timeOut = timeInSeconds.intValue();
            List<OrderEntity> elapsedOrders = orderDaoService.getElapsedOrders(timeOut);
            ReasonDetailsEntity reasonDetailsEntity = reasonDetailsDaoService.find(5);
            for (OrderEntity order : elapsedOrders) {
                OrderCancelEntity orderCancel = new OrderCancelEntity();
                orderCancel.setReason(reasonDetailsEntity.getCancelReason());
                orderCancel.setReasonDetails(reasonDetailsEntity);
                orderCancel.setJobOrderStatus(order.getOrderStatus());
                orderCancel.setOrder(order);
                order.setOrderCancel(orderCancel);
                order.setOrderStatus(JobOrderStatus.CANCELLED);
                boolean status = orderDaoService.update(order);
                if(status){
                    UserDeviceEntity userDevice = userDeviceDaoService.getUserDeviceInfoFromOrderId(order.getId());
                    String message = MessageBundle.getMessage("CPN007", "push_notification.properties");
                    PushNotificationUtil.sendPushNotification(userDevice, message, NotifyTo.CUSTOMER, PushNotificationRedirect.ORDER, order.getId().toString());
                }
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