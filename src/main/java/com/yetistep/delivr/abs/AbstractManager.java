package com.yetistep.delivr.abs;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/25/14
 * Time: 11:04 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractManager {
    @Autowired
    HttpServletRequest httpServletRequest;

    public String getServerName(){
      //Requested Server Name
      return httpServletRequest.getServerName();
    }

    public String getServerUrl(){
        //Requested URL
        return httpServletRequest.getRequestURL().toString().replace(httpServletRequest.getServletPath(), "").trim();
    }

    public String getIpAddress(){
        //Client IP Address
        String ipAddress = httpServletRequest.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null)
            ipAddress = httpServletRequest.getRemoteAddr();

        return ipAddress;
    }
}
