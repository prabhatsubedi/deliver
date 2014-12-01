package com.yetistep.delivr.controller;

import com.yetistep.delivr.model.AuthenticatedUser;
import com.yetistep.delivr.util.ServiceResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
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

    @RequestMapping(value = { "/create_user" }, method = RequestMethod.GET)
    public ModelAndView defaultPage() {
        ModelAndView model = new ModelAndView();
        model.addObject("title", "Create User");

        model.setViewName("admin/create_user");
        return model;
    }

    @RequestMapping(value = { "/dashboard" }, method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView dashboard() {
		ModelAndView model = new ModelAndView();
		model.addObject("title", "Dashboard");

        model.setViewName("admin/dashboard");
        return model;
    }

}
