package com.yetistep.delivr.controller;


import com.yetistep.delivr.model.AuthenticatedUser;
import com.yetistep.delivr.model.RoleEntity;
import com.yetistep.delivr.util.ServiceResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;

@Controller
public class MainController {

    @RequestMapping(value = { "/" }, method = RequestMethod.GET)
    public ModelAndView defaultPage() {
        ModelAndView model = new ModelAndView();
        model.addObject("title", "Spring Security + Hibernate Example");
        model.addObject("message", "This is default page!");
        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")) {
            AuthenticatedUser userDetails = (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            for(Iterator iterator = userDetails.getAuthorities().iterator(); iterator.hasNext();){
                String role= String.valueOf(iterator.next());
                if(role.equals("ROLE_ADMIN")){
                    model.setViewName("admin/dashboard");
                }else if(role.equals("ROLE_MANAGER")){
                    model.setViewName("organizer/dashboard");
                }else if(role.equals("ROLE_ACCOUNTANT")){
                    model.setViewName("accountant/dashboard");
                }else if(role.equals("ROLE_MERCHANT")) {
                    model.setViewName("merchant/dashboard");
                }
            }
        }  else {
            model.setViewName("login");
        }
        return model;
    }

    @RequestMapping(value = {"/welcome" }, method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse loginDefaultPage() {
        String url="";
        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")) {
            AuthenticatedUser userDetails = (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            for(Iterator iterator = userDetails.getAuthorities().iterator(); iterator.hasNext();){
                String role= String.valueOf(iterator.next());
                if(role.equals("ROLE_ADMIN")){
                    url="admin/dashboard";
                }else if(role.equals("ROLE_MANAGER")){
                    url="organizer/dashboard";
                }else if(role.equals("ROLE_ACCOUNTANT")){
                    url="accountant/dashboard";
                }else if(role.equals("ROLE_MERCHANT")) {
                    url="merchant/dashboard";
                }
            }
        }
        ServiceResponse serviceResponse = new ServiceResponse("User has been logged in successfully");
        serviceResponse.addParam("url", url);
        return serviceResponse;
    }


    @RequestMapping(value = "/admin**", method = RequestMethod.GET)
    public ModelAndView adminPage() {
        ModelAndView model = new ModelAndView();
        model.addObject("title", "Spring Security + Hibernate Example");

        model.addObject("message", "This page is for ROLE_ADMIN only!");
        model.setViewName("admin");
        return model;
    }

    @RequestMapping(value = "/auth_failed", method = RequestMethod.GET)
    public ModelAndView auth_failed() {
        ModelAndView model = new ModelAndView();
        model.addObject("title", "Authentication failed.");
        model.addObject("message", "Authentication failed.");
        return model;
    }

    @RequestMapping(value = "/user**", method = RequestMethod.GET)
    public ModelAndView userPage() {
        ModelAndView model = new ModelAndView();
        model.addObject("title", "Spring Security + Hibernate Example");
        model.addObject("message", "This page is for ROLE_USER only!");
        model.setViewName("user");
        return model;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse login(@RequestParam(value = "error", required = false) String error,
                                 @RequestParam(value = "logout", required = false) String logout, HttpServletRequest request) {
        String message = "";
        if (error != null) {
            message = getErrorMessage(request, "SPRING_SECURITY_LAST_EXCEPTION");
        }
        if (logout != null) {
            message = "You've been logged out successfully.";
        }
        ServiceResponse serviceResponse = new ServiceResponse(message);
        return serviceResponse;
    }


    @RequestMapping(value = "/signin", method = RequestMethod.GET)
    public ModelAndView signin() {
        ModelAndView model = new ModelAndView();
        model.addObject("title", "Sign in to Delivr");
        model.setViewName("login");
        return model;
    }




    @RequestMapping(value = "/admin/index", method = RequestMethod.GET)
    public ModelAndView indexPage() {
        ModelAndView model = new ModelAndView();
        model.addObject("title", "Spring Security + Hibernate Example");
        model.addObject("message", "This page is for ROLE_USER only!");
        model.setViewName("index");
        return model;
    }


    @RequestMapping(value = "/organizer/add_role", method = RequestMethod.GET)
    public ModelAndView addRole() {
        ModelAndView model = new ModelAndView();
        model.addObject("title", "Spring Security + Hibernate Example");
        model.setViewName("organizer/roles/add_role");
        return model;
    }

    // customize the error message
    private String getErrorMessage(HttpServletRequest request, String key) {
        Exception exception = (Exception) request.getSession().getAttribute(key);
        String error = "";
        if (exception instanceof BadCredentialsException) {
            error = "Invalid username and password!";
        } else if (exception instanceof LockedException) {
            error = exception.getMessage();
        } else {
            error = "Invalid username and password!";
        }
        return error;
    }

    // for 403 access denied page
    @RequestMapping(value = "/403", method = RequestMethod.GET)
    public ModelAndView accesssDenied() {
        ModelAndView model = new ModelAndView();
        // check if user is login
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetail = (UserDetails) auth.getPrincipal();
            System.out.println(userDetail);
            model.addObject("username", userDetail.getUsername());
        }
        model.setViewName("403");
        return model;

    }

    @RequestMapping(value = "/create_password", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse forgotPassword(@RequestParam("code") String code){
        ServiceResponse serviceResponse = new ServiceResponse("Redirecting URL");
        serviceResponse.addParam("url", "forgot_password");
        return serviceResponse;
    }

}