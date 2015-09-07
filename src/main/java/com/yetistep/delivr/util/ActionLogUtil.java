package com.yetistep.delivr.util;

import com.yetistep.delivr.enums.ActionType;
import com.yetistep.delivr.model.ActionLogEntity;
import com.yetistep.delivr.model.UserEntity;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 8/6/15
 * Time: 5:49 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
@Aspect
public class ActionLogUtil {

    private static final Logger log = Logger.getLogger(ActionLogUtil.class);

    public static ActionLogEntity createActionLog(Object firstInstance, Object secondInstance, String[] fields, UserEntity userEntity) throws Exception {

        log.info("----- Creating Action Log -----");

        if (firstInstance != null && secondInstance != null && (firstInstance.getClass().equals(secondInstance.getClass()))) {

            //String userIp = request.getHeader("X-FORWARDED-FOR") == null ? request.getRemoteAddr() : request.getHeader("X-FORWARDED-FOR");

            WebAuthenticationDetails details = (WebAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
            String userIp = details.getRemoteAddress();

            if (userEntity != null) {

                Map<Object, Object[]> methodValueDifference = getMethodValueDifference(firstInstance, secondInstance, fields);

                if (methodValueDifference != null) {

                    String description = createChangedValuesStr(methodValueDifference, firstInstance.getClass().toString());

                    ActionLogEntity actionLog = new ActionLogEntity();

                    actionLog.setUser(userEntity);
                    actionLog.setRole(userEntity.getRole().getRole());
                    actionLog.setActionType(ActionType.UPDATE);
                    actionLog.setDescription(description);
                    //actionLog.setUserIP(userIp);
                    actionLog.setUserIP(userIp);
                    actionLog.setDateTime(DateUtil.getCurrentTimestampSQL());

                    return actionLog;
                } else {
                    log.error("----- There is no difference in the property values of the objects -----");
                }
            } else {
                log.error("----- Unidentified user -----");
            }
        } else {
            log.error("----- Either objects are null or objects are of not same type -----");
        }
        return null;
    }

    //Object firstInstance and Object secondInstance must of same type
    //Object firstInstance is the original object which is being modified
    //Object secondInstance is the modified object instance
    //String[] methods is the list of methods name from the objects
    public static Map<Object, Object[]> getMethodValueDifference(Object firstInstance, Object secondInstance, String[] fields) throws Exception {

        log.info("----- Getting methods along with difference in the values of the properties of the provided objects -----");

        //Map<Object(methodName), Object[](returnedValueOfMethod, original, changed)>
        Map<Object, Object[]> methodDifferenceValue = new HashMap<>();

       /* Method[] classMethods = firstInstance.getClass().getMethods();

        for (Method method : classMethods) {
            for (String methodName : methods) {
                if (method.getName().equals(methodName)) {
                    Object firstValue = method.invoke(firstInstance, null);
                    Object secondValue = method.invoke(secondInstance, null);
                    if (!firstValue.equals(secondValue)) {
                        methodDifferenceValue.put(methodName, new Object[]{firstValue, secondValue});
                    }
                }
            }
        }*/
        for (String field : fields) {
            Object firstValue = PropertyUtils.getProperty(firstInstance, field.trim());
            Object secondValue = PropertyUtils.getProperty(secondInstance, field.trim());
            if (!PropertyUtils.getProperty(firstInstance, field.trim()).equals(PropertyUtils.getProperty(secondInstance, field.trim()))){
                methodDifferenceValue.put(field, new Object[]{firstValue, secondValue});
            }
        }

        return methodDifferenceValue.size() > 0 ? methodDifferenceValue : null;
    }

    //Map<Object(methodName), Object[](returnedValueOfMethod, original, changed)>
    public static String createChangedValuesStr(Map<Object, Object[]> methodDifferenceValue, String className) throws Exception {

        StringBuilder sbChangedValuesStr = new StringBuilder();
        sbChangedValuesStr.append("[" + extractClassName(className) + "] ");

        for (Map.Entry<Object, Object[]> entry : methodDifferenceValue.entrySet()) {
            String field = entry.getKey().toString();
            Object[] differenceValue = entry.getValue();
            sbChangedValuesStr.append(field + " " + " previous: ");
            sbChangedValuesStr.append(differenceValue[0].toString() + " after: " + differenceValue[1]);
            sbChangedValuesStr.append(", ");
        }

        return sbChangedValuesStr.toString().substring(0, sbChangedValuesStr.toString().length() - 2);
    }

    //This method extract only sensible class name from full class name
    // e.g. "class com.yetistep.delivr.model.CityEntity" will be converted to "City"
    public static String extractClassName(String fullClassName) {
        return fullClassName.substring(fullClassName.lastIndexOf(".") + 1, fullClassName.length() - 6);
    }

}
