package com.yetistep.delivr.service.inf;

import com.yetistep.delivr.model.CountryEntity;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/25/14
 * Time: 10:37 AM
 * To change this template use File | Settings | File Templates.
 */
public interface AdminService {
    public List<CountryEntity> findAllCountries() throws Exception;
    public List<Map<String, Map<Integer, Map<String, String>>>> getGodsView() throws Exception;
}
