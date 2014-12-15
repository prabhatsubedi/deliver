package com.yetistep.delivr.sys;

import com.yetistep.delivr.service.inf.SystemPropertyService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

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
    }
}
