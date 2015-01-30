package com.yetistep.delivr.schedular;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 1/28/15
 * Time: 3:22 PM
 * To change this template use File | Settings | File Templates.
 */
@Component("dynamicSchedule")
public class DynamicSchedule implements Trigger{
    private TaskScheduler scheduler;
    private Runnable task;
    private ScheduledFuture<?> future;
    private int delay;

    @Autowired
    ScheduleChanger scheduleChanger;

    @PostConstruct
    public void init() {
        scheduleChanger.setDynamicSchedule(this);
    }


    public DynamicSchedule(TaskScheduler scheduler, Runnable task, int delay) {
        this.scheduler = scheduler;
        this.task = task;
        setDelay(delay);
    }

    public void setDelay(int delay) {
        this.delay = delay;
        System.out.println("Starting task with delay..."+delay);
        future = scheduler.schedule(task, this);
    }

    public void cancelFutureTask(){
        if (future != null) {
            System.out.println("Cancelling task at"+new Date());
            future.cancel(true);
        }
    }

    public Boolean isRunning(){
        if (future != null) {
            return future.isCancelled();
        }
        return false;
    }

    public Long getDelay(){
        if(future != null){
            return future.getDelay(TimeUnit.MILLISECONDS);
        }
        return 0L;
    }

    @Override
    public Date nextExecutionTime(TriggerContext triggerContext) {
        Date lastTime = triggerContext.lastActualExecutionTime();
        Date nextExecutionTime = (lastTime == null)
                ? new Date(new Date().getTime() + delay)
                : new Date(lastTime.getTime() + delay);
        System.out.println("DynamicSchedule -- delay: " + delay +
                ", lastActualExecutionTime: " + lastTime +
                "; nextExecutionTime: " + nextExecutionTime);
        return nextExecutionTime;
    }

}
