package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.dao.impl.PreferencesDaoServiceImpl;
import com.yetistep.delivr.dao.inf.PreferencesDaoService;
import com.yetistep.delivr.model.PreferencesEntity;
import com.yetistep.delivr.service.inf.SystemPropertyService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/12/14
 * Time: 3:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class SystemPropertyServiceImpl implements SystemPropertyService {
    private static String SYSTEM_PREF_FILE = "system_pref.properties";
    private static final Logger log = Logger.getLogger(SystemPropertyServiceImpl.class);

    @Autowired
    PreferencesDaoService preferencesDaoService;

    @Override
    public void init() {
        OutputStream output = null;
        try {
            log.info("Initializing the system_pref property file: " + new ClassPathResource(SYSTEM_PREF_FILE).getURL().toString());
            List<PreferencesEntity> preferencesEntities = preferencesDaoService.findAll();

            //Save to File
            savePropertyFileToWEB_INF(preferencesEntities);
            // Now Update

        } catch (Exception e) {
            log.error("Error occurred while initializing system preferences", e);
        } finally {

            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    log.error("Error occurred while initializing system preferences", e);
                }
            }
        }

    }

    @Override
    public List<PreferencesEntity> getAllPreferences() throws Exception {
        log.info("---------------------Fetching All Preferences--------------------------");
        List<PreferencesEntity> allPreferences = preferencesDaoService.findAll();
        return allPreferences;
    }

    private void savePropertyFileToWEB_INF(List<PreferencesEntity> allPreferences) throws Exception {
        log.info("Saving Preferences in " + SYSTEM_PREF_FILE);
        Resource resource = new ClassPathResource(SYSTEM_PREF_FILE);
        File file = resource.getFile();

        FileOutputStream outputStream = new FileOutputStream(file);

        Properties prop = new Properties();
        for (PreferencesEntity preferences : allPreferences) {
            prop.setProperty(preferences.getPrefKey().toString(), preferences.getValue());
        }

        prop.store(outputStream, null);
        outputStream.close();

    }

    @Override
    public Boolean updateSystemPreferences(List<PreferencesEntity> systemPreferences) throws Exception {
        log.info("+++++++++++ Updating preferences table and property file ++++++++++++++");
        //update preference in db
        preferencesDaoService.updatePreferences(systemPreferences);

        //update preference in property fle
        savePropertyFileToWEB_INF(systemPreferences);
        return true;
    }
}
