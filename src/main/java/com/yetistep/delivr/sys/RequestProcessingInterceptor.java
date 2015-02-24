package com.yetistep.delivr.sys;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 2/6/15
 * Time: 12:30 PM
 * To change this template use File | Settings | File Templates.
 */
import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestProcessingInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = Logger
            .getLogger(RequestProcessingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {

        logger.info("+++++ Before Handler Given to Spring Framework ++++++");
        logger.info("+++ Request URL " + request.getRequestURL().toString() +
                " +++ Request Type " + request.getMethod() + " ++++ Content Type " + request.getContentType());
        if(request.getHeader("accessToken") !=null && !request.getHeader("accessToken").isEmpty())
            logger.info("+++++ Pre Handle " + request.getHeader("accessToken"));

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        //TODO:
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

        logger.info("content type "+ response.getContentType() + " +++Http status "+ response.getStatus());
        logger.info("++++++ Framework finished the process ++++++");
        if(response.getHeader("accessToken")!=null && !response.getHeader("accessToken").isEmpty())
            logger.info("+++++ After Completion " + response.getHeader("accessToken"));
    }



}

