package com.yetistep.delivr.controller;

import com.yetistep.delivr.model.AuthenticatedUser;
import com.yetistep.delivr.util.ServiceResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/24/14
 * Time: 10:00 AM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/admin")
public class AdminViewController {

    @RequestMapping(value = "/{page}/**", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView admin(@PathVariable String page){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin/" + page);
        return modelAndView;
    }


}
