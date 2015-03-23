package com.yetistep.delivr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/24/14
 * Time: 11:55 AM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/accountant")
public class AccountantViewController {

    @RequestMapping(value = {"/courier_staff/{page}/**"}, method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView addDeliveryBoy(@PathVariable String page){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("accountant/courier_staff_" + page);
        return modelAndView;
    }

    @RequestMapping(value = {"/merchants"}, method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView getmerchants(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("accountant/merchants");
        return modelAndView;
    }

    @RequestMapping(value = {"/dashboard"}, method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView addDeliveryBoy(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("accountant/dashboard");
        return modelAndView;
    }

}
