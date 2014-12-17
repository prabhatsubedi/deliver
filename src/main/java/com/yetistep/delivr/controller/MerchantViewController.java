package com.yetistep.delivr.controller;

import com.yetistep.delivr.dao.inf.MerchantDaoService;
import com.yetistep.delivr.model.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
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

    @RequestMapping(value = "/store/{page}/**", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView merchantStore(@PathVariable String page){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("merchant/store_" + page);
        return modelAndView;
    }

    @RequestMapping(value = "/item/{page}/**", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView merchantList(@PathVariable String page){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("merchant/item_" + page);
        return modelAndView;
    }

    @RequestMapping(value = "/{page}/**", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView merchant(@PathVariable String page){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("merchant/" + page);
        return modelAndView;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView merchant(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("merchant/dashboard");
        return modelAndView;
    }
}
