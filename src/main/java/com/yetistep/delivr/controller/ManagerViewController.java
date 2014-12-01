package com.yetistep.delivr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/24/14
 * Time: 11:55 AM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/organizer")
public class ManagerViewController {

    @RequestMapping(value = { "/create_delivery_boy_account" }, method = RequestMethod.GET)
    public ModelAndView defaultPage() {

        ModelAndView model = new ModelAndView();
        model.addObject("title", "Create Delivery Boy Account");

        model.setViewName("organizer/create_delivery_boy_account");
        return model;

    }


}
