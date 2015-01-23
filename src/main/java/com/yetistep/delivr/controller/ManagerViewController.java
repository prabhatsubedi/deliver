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
@RequestMapping(value = "/organizer")
public class ManagerViewController {

    @RequestMapping(value = {"/courier_staff/{page}/**"}, method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView addDeliveryBoy(@PathVariable String page){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("organizer/courier_staff_" + page);
        return modelAndView;
    }

    @RequestMapping(value = "/{page}/**", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView merchant(@PathVariable String page){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("organizer/" + page);
        return modelAndView;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView merchant(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("organizer/dashboard");
        return modelAndView;
    }

    @RequestMapping(value = "category/create", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView createCategory(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("organizer/category_create");
        return modelAndView;
    }

    @RequestMapping(value = "category/update", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView updateCategory(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("organizer/category_update");
        return modelAndView;
    }

    @RequestMapping(value = "category/view", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView viewCategory(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("organizer/view_category");
        return modelAndView;
    }




}
