package com.yetistep.delivr.schedular;

import com.yetistep.delivr.dao.inf.OrderDaoService;
import com.yetistep.delivr.dao.inf.UserDeviceDaoService;
import com.yetistep.delivr.enums.NotifyTo;
import com.yetistep.delivr.enums.PushNotificationRedirect;
import com.yetistep.delivr.model.OrderEntity;
import com.yetistep.delivr.model.UserDeviceEntity;
import com.yetistep.delivr.service.inf.CustomerService;
import com.yetistep.delivr.service.inf.SystemPropertyService;
import com.yetistep.delivr.util.DateUtil;
import com.yetistep.delivr.util.GeneralUtil;
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
    CustomerService customerService;

    public void process() {
        try {
            log.info("Looking for elapsed orders");

            Float timeInSeconds = 180f;
            Integer timeOut = timeInSeconds.intValue();
            List<OrderEntity> elapsedOrders = orderDaoService.getElapsedOrders(timeOut);
            for (OrderEntity order : elapsedOrders) {
                reprocessOrder(order);
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

    private Boolean reprocessOrder(OrderEntity order) throws Exception{
        int orderReprocessedTime = GeneralUtil.ifNullToZero(order.getReprocessTime());
        boolean status = customerService.reprocessOrder(order.getId());
        if(status && orderReprocessedTime > 0){
            UserDeviceEntity userDevice = userDeviceDaoService.getUserDeviceInfoFromOrderId(order.getId());
            String message = MessageBundle.getMessage("CPN007", "push_notification.properties");
            String extraDetail = order.getId().toString()+"/status/"+order.getOrderStatus().toString();
            PushNotificationUtil.sendPushNotification(userDevice, message, NotifyTo.CUSTOMER, PushNotificationRedirect.ORDER, extraDetail);
        }
        return status;
    }
}