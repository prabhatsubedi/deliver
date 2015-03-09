package com.yetistep.delivr.schedular;

import com.yetistep.delivr.service.inf.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 12/12/14
 * Time: 3:18 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class TaskSchedule {

    @Autowired
    AccountService accountService;

    /* second(0-59)  minute(0-59)   hour(0-23)  Day of month(1-31)  month(0-11 or JAN-DEC)  Day of week(1-7 or SUN-SAT) */
    @Scheduled(cron="0 0 12 * * ?")
    public void deliveryBoyStatus() {
        System.out.println("Updating Status of delivery boy:");
    }


    /*@Scheduled(cron="5 * * * * ?")
    public void generateInvoice() throws Exception{
        System.out.println("Generating invoice:");
        accountService.generateInvoice(22, "2015-02-18", "2015-03-06");
    }*/
}
