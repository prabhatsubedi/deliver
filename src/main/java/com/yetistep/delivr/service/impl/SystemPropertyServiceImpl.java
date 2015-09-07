package com.yetistep.delivr.service.impl;

import com.yetistep.delivr.dao.inf.ActionLogDaoService;
import com.yetistep.delivr.dao.inf.PreferencesDaoService;
import com.yetistep.delivr.dao.inf.UserDaoService;
import com.yetistep.delivr.dao.inf.UserDeviceDaoService;
import com.yetistep.delivr.dto.HeaderDto;
import com.yetistep.delivr.enums.NotifyTo;
import com.yetistep.delivr.enums.PreferenceType;
import com.yetistep.delivr.enums.PushNotificationRedirect;
import com.yetistep.delivr.enums.Role;
import com.yetistep.delivr.model.*;
import com.yetistep.delivr.service.inf.SystemPropertyService;
import com.yetistep.delivr.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.*;
import java.util.*;


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

    @Autowired
    UserDeviceDaoService userDeviceDaoService;

    @Autowired
    UserDaoService userDaoService;

    @Autowired
    ActionLogDaoService actionLogDaoService;


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
        return preferencesDaoService.findAll();
    }

    @Override
    public PreferenceTypeEntity getAllPreferences(HeaderDto headerDto) throws Exception {
        log.info("---------------------Fetching All Preferences--------------------------");
        PreferenceTypeEntity preferences = preferencesDaoService.findAll(Integer.parseInt(headerDto.getId()));

        String fields = "id,groupName";

        Map<String, String> assoc = new HashMap<>();
        Map<String, String> subAssoc = new HashMap<>();

        assoc.put("section", "id,section,preference");
        subAssoc.put("preference", "id,prefKey,value,prefTitle");

        return ((PreferenceTypeEntity) ReturnJsonUtil.getJsonObject(preferences, fields, assoc, subAssoc));

    }

    private void savePropertyFileToWEB_INF(List<PreferencesEntity> allPreferences) throws Exception {
        log.info("Saving Preferences in " + SYSTEM_PREF_FILE);
        Resource resource = new ClassPathResource(SYSTEM_PREF_FILE);
        File file = resource.getFile();

        FileInputStream in = new FileInputStream(file);
        Properties prop = new Properties();
        prop.load(in);
        in.close();

        FileOutputStream outputStream = new FileOutputStream(file);

        //Properties prop = new Properties();
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
        List<ActionLogEntity> actionLogs = new ArrayList<>();
        UserEntity userEntity = userDaoService.find(((AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId());
        for (PreferencesEntity preference : systemPreferences){
            PreferencesEntity dbPreference = preferencesDaoService.findByKey(preference.getPrefKey());
            ActionLogEntity actionLog = ActionLogUtil.createActionLog(dbPreference, preference, new String[]{"value"}, userEntity);
            if(actionLog!=null){
                actionLogs.add(actionLog);
            }
        }

        preferencesDaoService.updatePreferences(systemPreferences);

        log.info("saving action log");
        actionLogDaoService.saveAll(actionLogs);

        for (PreferencesEntity preference : systemPreferences) {
            if (preference.getPrefKey().equals(PreferenceType.ANDROID_APP_VER_NO.toString())) {
                String androidVersionNo = readPrefValue(PreferenceType.ANDROID_APP_VER_NO);
                String message = MessageBundle.getPushNotificationMsg("VUM001");
                if (!preference.getValue().equals(androidVersionNo)) {
                    sendNotification(Role.ROLE_CUSTOMER, "ANDROID", NotifyTo.CUSTOMER_ANDROID, message, preference.getValue());
                }
            } else if (preference.getPrefKey().equals(PreferenceType.IOS_APP_VER_NO.toString())) {
                String iosVersionNo = readPrefValue(PreferenceType.IOS_APP_VER_NO);
                String message = MessageBundle.getPushNotificationMsg("VUM001");
                if (!preference.getValue().equals(iosVersionNo)) {
                    sendNotification(Role.ROLE_CUSTOMER, "MAC_OS_X", NotifyTo.CUSTOMER_IOS, message, preference.getValue());
                }
            }
        }
        //update preference in property fle
        savePropertyFileToWEB_INF(systemPreferences);
        return true;
    }

    private void sendNotification(Role role, String family, NotifyTo notifyTo, String message, String extraDetail) throws Exception {
        List<String> deviceTokens = userDeviceDaoService.getAllDeviceTokensForFamilyAndRole(role, family);
        PushNotification pushNotification = new PushNotification();
        pushNotification.setTokens(deviceTokens);
        pushNotification.setMessage(message);
        pushNotification.setPushNotificationRedirect(PushNotificationRedirect.PLAYSTORE);
        pushNotification.setExtraDetail(extraDetail);
        pushNotification.setNotifyTo(notifyTo);
        PushNotificationUtil.sendNotification(pushNotification, family);
    }


    @Override
    public Boolean updateSystemPreferencesType(PreferenceTypeEntity preferenceType) throws Exception {
        log.info("+++++++++++ Updating preferences table and property file ++++++++++++++");
        //update preference in db
        preferencesDaoService.updatePreferenceType(preferenceType);
        List<PreferencesEntity> systemPreferences = new ArrayList<>();
        for (PreferenceSectionEntity sectionEntity : preferenceType.getSection()) {
            systemPreferences.addAll(sectionEntity.getPreference());
        }


        //update preference in property fle
        savePropertyFileToWEB_INF(systemPreferences);
        return true;
    }

    @Override
    public String readPrefValue(PreferenceType preferenceType) throws Exception {
        Resource resource = new ClassPathResource(SYSTEM_PREF_FILE);
        return MessageBundle.getPropertyKey(preferenceType.toString(), resource.getFile());
    }

}
