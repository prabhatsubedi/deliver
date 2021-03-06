package com.yetistep.delivr.dao.impl;

import com.yetistep.delivr.dao.inf.PreferencesDaoService;
import com.yetistep.delivr.model.PreferenceSectionEntity;
import com.yetistep.delivr.model.PreferenceTypeEntity;
import com.yetistep.delivr.model.PreferencesEntity;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
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
        return (PreferencesEntity) getCurrentSession().get(PreferenceTypeEntity.class, id);
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

        if(value.getSection().size() > 0){
            for (PreferenceSectionEntity section: value.getSection()){
                if(section.getPreference().size() > 0){
                    for (PreferencesEntity preference: section.getPreference()){

                        String sqQuery =    "update preferences set value = :value, section_id =:sectionId where pref_key = :prefKey";
                        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqQuery);
                        query.setParameter("value", preference.getValue());
                        query.setParameter("prefKey", preference.getPrefKey());
                        query.setParameter("sectionId", section.getId());
                        query.executeUpdate();
                    }
                }
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
    public PreferencesEntity findByKey(String key) throws Exception {
        List<PreferencesEntity> preferences  = new ArrayList<PreferencesEntity>();
        Criteria criteria  = getCurrentSession().createCriteria(PreferencesEntity.class);
        criteria.add(Restrictions.eq("prefKey", key));
        preferences = criteria.list();
        return preferences.size()>0?preferences.get(0):null;
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
