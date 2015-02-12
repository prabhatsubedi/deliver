package com.yetistep.delivr.sys;

import com.amazonaws.http.IdleConnectionReaper;
import com.mysql.jdbc.AbandonedConnectionCleanupThread;
import com.yetistep.delivr.service.inf.SystemPropertyService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/12/14
 * Time: 3:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class SystemInit {
    @Autowired
    SystemPropertyService systemPropertyService;

    private static final Logger log = Logger.getLogger(SystemInit.class);

    /* Called after bean construction and all dependency injection. */
    @PostConstruct
    public void initialize(){
        log.info("+++++++++++ Starting the application ++++++++++++++");
        systemPropertyService.init();

    }

    @PreDestroy
    /* Called just before a bean is destroyed */
    public void cleanup(){
       log.info("+++++++++++ Shutting down the application ++++++++++++++++");
        unregisterJDBCDriver();
        closeAWSThreads();
    }

    private void unregisterJDBCDriver() {
        log.info("Unregistering JDBC Driver manually");
        Enumeration<Driver> drivers = DriverManager.getDrivers();

        Driver driver = null;

        // clear drivers
        while(drivers.hasMoreElements()) {
            try {
                driver = drivers.nextElement();
                DriverManager.deregisterDriver(driver);

            } catch (Exception e) {
                // deregistration failed, might want to do something, log at the very least
                log.error("Error occurred while deregistering JDBC Driver AWS Thread", e);
            }
        }

        // MySQL driver leaves around a thread. This static method cleans it up.
        try {
            AbandonedConnectionCleanupThread.shutdown();
        } catch (Exception e) {
            log.error("Error occurred while cleaning mysql driver abandonded threads", e);
        }
    }

    private void closeAWSThreads() {
        log.info("Closing AWS Threads");
        try {
            IdleConnectionReaper.shutdown();
        } catch (Exception e) {
            log.error("Error occurred while closing AWS Thread", e);
        }

    }

//    private void closeC3P0DataSource(){
//        log.info("Closing the C3P0 DataSource");
//        try {
//            C3p0DataSource.getInstance().destroy();
//        } catch (Exception e) {
//            log.error("Error occurred while closing C3P0 DataSource", e);
//        }
//
//    }


}
