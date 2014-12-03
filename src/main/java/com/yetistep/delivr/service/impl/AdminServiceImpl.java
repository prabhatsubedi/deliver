package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.dao.inf.CountryDaoService;
import com.yetistep.delivr.model.CountryEntity;
import com.yetistep.delivr.service.inf.AdminService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/25/14
 * Time: 10:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class AdminServiceImpl implements AdminService {
    private static final Logger log = Logger.getLogger(AdminServiceImpl.class);

    @Autowired
    CountryDaoService countryDaoService;

    @Override
    public List<CountryEntity> findAllCountries() throws Exception {
        return countryDaoService.findAll();
    }
}
