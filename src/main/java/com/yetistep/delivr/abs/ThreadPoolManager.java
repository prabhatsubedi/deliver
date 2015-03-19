package com.yetistep.delivr.abs;

import org.apache.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: yetistep
 * Date: 1/9/14
 * Time: 5:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class ThreadPoolManager {
    private static final Logger log     =   Logger.getLogger(ThreadPoolManager.class);
    private static ExecutorService pool;

    public static void init(){
        if(pool == null)
            pool = Executors.newFixedThreadPool(5);//fixed pool size 5

    }

    public static void runAsynchJob(Runnable job){
       init();
       pool.execute(job);
    }

    public static void shutdownAndAwaitTermination() {
        log.info("Shutting signal obtained and clearing the pool: "+pool);
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {// Wait a while for existing tasks to terminate
                pool.shutdownNow(); // Cancel currently executing tasks

                if (!pool.awaitTermination(60, TimeUnit.SECONDS))// Wait a while for tasks to respond to being cancelled
                    log.error("Pool did not termiate");
            }
        } catch (InterruptedException ie) {
            log.error("Error occurred while clearing the pool",ie);
            pool.shutdownNow();
            Thread.currentThread().interrupt();// Preserve interrupt status
        } catch (Exception e) {
            log.error("Error occurred while clearing the pool", e);
        }
    }
}
