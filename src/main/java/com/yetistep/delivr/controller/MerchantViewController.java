package com.yetistep.delivr.controller;

import com.yetistep.delivr.dao.inf.MerchantDaoService;
import com.yetistep.delivr.model.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 11/24/14
 * Time: 11:55 AM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/merchant")
public class MerchantViewController {

    @Autowired
    MerchantDaoService merchantDaoService;

    @RequestMapping(value = { "/add_store" }, method = RequestMethod.GET)
    public ModelAndView defaultPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("title", "Add Store");
        modelAndView.setViewName("merchant/add_store");
        return modelAndView;
    }

    @RequestMapping(value = {"/dashboard"}, method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView addDeliveryBoy(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("title", "Add Store");
        modelAndView.setViewName("merchant/dashboard");
        return modelAndView;
    }
}
