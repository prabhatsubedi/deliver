package com.yetistep.delivr.service.inf;

import com.yetistep.delivr.model.PreferencesEntity;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/12/14
 * Time: 3:28 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SystemPropertyService {
    public void init();

    public List<PreferencesEntity> getAllPreferences() throws Exception;

    public Boolean updateSystemPreferences(List<PreferencesEntity> preferencesEntities) throws Exception;

}
