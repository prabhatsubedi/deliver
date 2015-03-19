package com.yetistep.delivr.schedular;

import com.yetistep.delivr.dao.inf.DeliveryBoyDaoService;
import com.yetistep.delivr.model.StoreEntity;
import com.yetistep.delivr.service.inf.AccountService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 12/12/14
 * Time: 3:18 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
@Component
public class TaskSchedule {
    private static final Logger log = Logger.getLogger(TaskSchedule.class);

    @Autowired
    AccountService accountService;

    @Autowired
    DeliveryBoyDaoService deliveryBoyDaoService;

    /* second(0-59)  minute(0-59)   hour(0-23)  Day of month(1-31)  month(0-11 or JAN-DEC)  Day of week(1-7 or SUN-SAT) */
    @Scheduled(cron="0 0 0 * * ?")
    @Transactional
    public void deliveryBoyStatus() {
        log.info("Updating Previous Day Due Transaction of shopper:");
        try {
            deliveryBoyDaoService.updatePreviousDayDueAmount();
            log.info("Previous Day Due Transaction of shopper updated successfully");
        } catch (Exception e) {
            log.error("Error occurred while updating previous day due amount",e);
        }

    }

    @Scheduled(cron="0 0 22 * * THU")
    public void generateInvoice() throws Exception{
        log.info("Generating invoice:");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 0);
        Calendar calPrev = Calendar.getInstance();
        calPrev.add(Calendar.DATE, -7);

        List<StoreEntity> stores=accountService.getAllStores();

        if(stores.size()>0){
            String filePath = new String();
            for (StoreEntity store: stores){
                accountService.generateInvoice(store.getId(), dateFormat.format(calPrev.getTime()), dateFormat.format(cal.getTime()), "http://test.idelivr.com/");
            }
        } else {
            log.info("no stores found. ");
        }
    }
}
