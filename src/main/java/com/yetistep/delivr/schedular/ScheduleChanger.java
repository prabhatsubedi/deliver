package com.yetistep.delivr.schedular;

import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 1/28/15
 * Time: 3:54 PM
 * To change this template use File | Settings | File Templates.
 */
@Component("scheduleChanger")
public class ScheduleChanger {
    private DynamicSchedule dynamicSchedule;

    public void setDynamicSchedule(DynamicSchedule dynamicSchedule) {
        this.dynamicSchedule = dynamicSchedule;
    }

    public void cancelTask() {
        dynamicSchedule.setDelay(24*60*60*1000);
    }

    public Boolean isSchedularRunning(){
       return dynamicSchedule.isRunning();
    }

    public void scheduleTask(int delay){
        int previousDelay = dynamicSchedule.getDelay().intValue();
        if((previousDelay < 0) || (previousDelay > delay)){
            System.out.println("Changing trigger repetition at: " + delay);
            dynamicSchedule.setDelay(delay);
        }
    }

}
