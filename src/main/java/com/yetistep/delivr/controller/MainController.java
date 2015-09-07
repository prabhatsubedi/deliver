package com.yetistep.delivr.controller;


import com.yetistep.delivr.enums.PreferenceType;
import com.yetistep.delivr.enums.Role;
import com.yetistep.delivr.model.AuthenticatedUser;
import com.yetistep.delivr.service.inf.SystemPropertyService;
import com.yetistep.delivr.util.GeneralUtil;
import com.yetistep.delivr.util.ServiceResponse;
import com.yetistep.delivr.util.SessionManager;
import com.yetistep.delivr.util.YSException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

@Controller
public class MainController {

    @Autowired
    SystemPropertyService systemPropertyService;

    private static Logger log = Logger.getLogger(MainController.class);

    @RequestMapping(value = {"/"}, method = RequestMethod.GET)
    public ModelAndView defaultPage() {
        ModelAndView model = new ModelAndView();
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null && !SessionManager.isAnonymousUser()) {
            Role role = SessionManager.getRole();
            if (role.toString().equals(Role.ROLE_ADMIN.toString())) {
                model.setViewName("organizer/dashboard");
            } else if (role.toString().equals(Role.ROLE_MANAGER.toString())) {
                model.setViewName("organizer/dashboard");
            } else if (role.toString().equals(Role.ROLE_ACCOUNTANT.toString())) {
                model.setViewName("accountant/merchants");
            } else if (role.toString().equals(Role.ROLE_MERCHANT.toString())) {
                model.setViewName("merchant/store/list");
            }
        } else {
            model.setViewName("login");
        }
        return model;
    }

    @RequestMapping(value = {"/welcome" }, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ServiceResponse> loginDefaultPage() {
        try{
            String url = "";
            AuthenticatedUser userDetails = (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null && !SessionManager.isAnonymousUser()) {
                Role role = SessionManager.getRole();
                if (role.toString().equals(Role.ROLE_ADMIN.toString())) {
                    url = "organizer/dashboard";
                } else if (role.toString().equals(Role.ROLE_MANAGER.toString())) {
                    url = "organizer/dashboard";
                } else if (role.toString().equals(Role.ROLE_ACCOUNTANT.toString())) {
                    url = "accountant/merchants";
                } else if (role.toString().equals(Role.ROLE_MERCHANT.toString())) {
                    url = "merchant/store/list";
                }
            }
            ServiceResponse serviceResponse = new ServiceResponse("User has been logged in successfully");
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            serviceResponse.addParam("url", url);
            serviceResponse.addParam("userDetails", userDetails);
            serviceResponse.addParam("currency", systemPropertyService.readPrefValue(PreferenceType.CURRENCY));
            return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.CREATED);
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred getting welcome page", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.EXPECTATION_FAILED);
        }
    }


    @RequestMapping(value = "/auth_failed", method = RequestMethod.GET)
    public ResponseEntity<ServiceResponse> auth_failed() {
        try{
            throw new YSException("SEC003");
        } catch (Exception e){
            GeneralUtil.logError(log, "Error Occurred while login", e);
            HttpHeaders httpHeaders = ServiceResponse.generateRuntimeErrors(e);
            return new ResponseEntity<ServiceResponse>(httpHeaders, HttpStatus.BAD_REQUEST);
        }
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
        return new ServiceResponse(message);
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

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView signup(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("signup");
        return modelAndView;
    }

    @RequestMapping(value = "/assistance/**", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView forgotPassword(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("assistance");
        return modelAndView;
    }

    @RequestMapping(value = "/balance/**", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView balance(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("balance");
        return modelAndView;
    }

    @RequestMapping(value = {"/store/{id}/", "/store/{id}/{cat}/"}, method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView store(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("store");
        return modelAndView;
    }

}