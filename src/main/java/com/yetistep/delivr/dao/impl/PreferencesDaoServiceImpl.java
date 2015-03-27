package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.PreferencesDaoService;
import com.yetistep.delivr.model.PreferenceSectionEntity;
import com.yetistep.delivr.model.PreferenceTypeEntity;
import com.yetistep.delivr.model.PreferencesEntity;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/12/14
 * Time: 12:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class PreferencesDaoServiceImpl implements PreferencesDaoService{
    @Autowired
    SessionFactory sessionFactory;

    @Override
    public PreferencesEntity find(Integer id) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<PreferencesEntity> findAll() throws Exception {

        return (List<PreferencesEntity>) getCurrentSession().createCriteria(PreferencesEntity.class).list();
    }

    @Override
    public PreferenceTypeEntity findAll(Integer groupId) throws Exception {

        return (PreferenceTypeEntity) getCurrentSession().get(PreferenceTypeEntity.class, groupId);
    }

    @Override
    public Boolean save(PreferencesEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Boolean update(PreferencesEntity value) throws Exception {
        Query query = getCurrentSession().createQuery("update PreferencesEntity set value = :value where prefKey = :prefKey");
        query.setParameter("value", value.getValue());
        query.setParameter("prefKey", value.getPrefKey());
        query.executeUpdate();

        return true;
    }


    @Override
    public void updatePreferenceType(PreferenceTypeEntity value) throws Exception {

        for (PreferenceSectionEntity section: value.getSection()){

            for (PreferencesEntity preference: section.getPreference()){

                Query query = getCurrentSession().createQuery("update PreferencesEntity set value = :value, section_id =:sectionId where prefKey = :prefKey");
                query.setParameter("value", preference.getValue());
                query.setParameter("prefKey", preference.getPrefKey());
                query.setParameter("sectionId", preference.getSection().getId());
                query.executeUpdate();

            }

        }
    }

    @Override
    public Boolean delete(PreferencesEntity value) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Session getCurrentSession() throws Exception {
        return sessionFactory.getCurrentSession();
    }


    @Override
    public PreferencesEntity find(String key) throws Exception {
        return (PreferencesEntity) getCurrentSession().get(PreferencesEntity.class, key);
    }

    @Override
    public void updatePreferences(List<PreferencesEntity> preferencesEntities) throws Exception {
        for(PreferencesEntity preferencesEntity: preferencesEntities){
            Query query = getCurrentSession().createQuery("update PreferencesEntity set value = :value where prefKey = :prefKey");
            query.setParameter("value", preferencesEntity.getValue());
            query.setParameter("prefKey", preferencesEntity.getPrefKey());
            query.executeUpdate();
        }
    }
}
