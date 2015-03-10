package com.yetistep.delivr.schedular;

import com.yetistep.delivr.model.StoreEntity;
import com.yetistep.delivr.service.inf.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    @Autowired
    AccountService accountService;

    /* second(0-59)  minute(0-59)   hour(0-23)  Day of month(1-31)  month(0-11 or JAN-DEC)  Day of week(1-7 or SUN-SAT) */
    @Scheduled(cron="0 0 12 * * ?")
    public void deliveryBoyStatus() {
        System.out.println("Updating Status of delivery boy:");
    }

    @Scheduled(cron="0 0 12 * * TUE")
    public void generateInvoice() throws Exception{
        System.out.println("Generating invoice:");
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
            System.out.println("no stores found. ");
        }
    }
}
