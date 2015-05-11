package com.yetistep.delivr.dao.inf;

import com.yetistep.delivr.abs.GenericDaoService;
import com.yetistep.delivr.model.PreferenceTypeEntity;
import com.yetistep.delivr.model.PreferencesEntity;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/12/14
 * Time: 12:36 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PreferencesDaoService extends GenericDaoService<Integer, PreferencesEntity>{
    public PreferencesEntity findByKey(String key) throws Exception;

    public PreferenceTypeEntity findAll(Integer groupId) throws Exception;

    public void updatePreferences(List<PreferencesEntity> preferencesEntities) throws Exception;

    public void updatePreferenceType(PreferenceTypeEntity preferenceType) throws Exception;
}
