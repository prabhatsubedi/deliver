package com.yetistep.delivr.util;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 1/29/15
 * Time: 9:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class ReturnJsonUtil {

    /*@return: dynamic Object
     * @param1 - @defaultObject - @type: Object, @description: main object
     * @param2: @params - @type- Map(associated model - string, List(fields required of related model - string)) - @description: Map of associated model and  related fields
     *  @param3: @subAssoc - @type- Map(2nd level associated model - string, List(fields required of related model - string)) - @description: Map of 2nd level associated model and  related fields
     * */
    public static Object getJsonObject(Object defaultObject, String fields, Map<String, String> params, Map<String, String> subAssoc) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException{
        //instantiate the default object
        Object rtnObject = BeanUtils.instantiate(defaultObject.getClass());

        //set fields of the default object to the return object
         String[] arrFields =  fields.split(",");

        for (String field: arrFields){
            PropertyUtils.setProperty(rtnObject, field, PropertyUtils.getProperty(defaultObject, field));
        }

        //get the map of associated model and and related fields and set the values to the return object
        for (Map.Entry<String, String> param: params.entrySet()){

            /*check if the result of the associated model is collection or single
             if collection iterate and set fields to return object
             else set values of fields in return object directly
             */
            if(PropertyUtils.getProperty(defaultObject, param.getKey()).getClass().getName().equals("org.hibernate.collection.internal.PersistentBag")){
                List<Object> assocDBs =  (List<Object>) PropertyUtils.getProperty(defaultObject, param.getKey());
                List<Object> assocRtnList = new ArrayList<>();

                for (Object assocDB: assocDBs){
                    Object assocRtnObj = BeanUtils.instantiate(assocDB.getClass());
                     if(param.getValue() != null)  {
                        String assocFields = param.getValue();
                        String[] arrAssocFields = assocFields.split(",");
                        for (String assocF:arrAssocFields){
                            /*if value of field is collection than this is the another model - iterate and set values of the fields of the related  2nd level associated  model
                              else check if the value is  another model or not
                              if the value is another model than iterate and set the value of the fields of the related  2nd level associated  model
                              else set the values of fields of related model directly
                             */

                            /*if(PropertyUtils.getProperty(assocDB, assocF).getClass().getName().equals("org.hibernate.collection.internal.PersistentBag")) {
                                List<Object> assoc2ndDBs = (List<Object>) PropertyUtils.getProperty(assocDB, assocF);
                                List<Object> assoc2ndRtn = new ArrayList<>();
                                for (Object assoc2ndDB: assoc2ndDBs) {
                                    Object assoc2ndRtnObj = BeanUtils.instantiate(assoc2ndDB.getClass());
                                    List<String> accoc2ndFs = subAssoc.get(assocF);
                                    if(accoc2ndFs != null){
                                        for (String accoc2ndF: accoc2ndFs){
                                            PropertyUtils.setProperty(assoc2ndRtnObj, accoc2ndF,  PropertyUtils.getProperty(assoc2ndDB, accoc2ndF));
                                        }
                                    }
                                    assoc2ndRtn.add(assoc2ndRtnObj);
                                }
                                PropertyUtils.setProperty(assocRtnObj, assocF,  assoc2ndRtn);
                            } else {
                                if(PropertyUtils.getProperty(assocDB, assocF).getClass().toString().contains("com.yetistep.delivr")){
                                    Object assoc2ndRtnObj = BeanUtils.instantiate(PropertyUtils.getProperty(assocDB, assocF).getClass());
                                    List<String> accoc2ndFs = subAssoc.get(assocF);
                                    if(accoc2ndFs != null){
                                        for (String accoc2ndF: accoc2ndFs){
                                            PropertyUtils.setProperty(assoc2ndRtnObj, accoc2ndF,  PropertyUtils.getProperty(PropertyUtils.getProperty(assocDB, assocF), accoc2ndF));
                                        }
                                    }
                                    PropertyUtils.setProperty(assocRtnObj, assocF,  assoc2ndRtnObj);
                                }else{
                                    PropertyUtils.setProperty(assocRtnObj, assocF,  PropertyUtils.getProperty(assocDB, assocF));
                                }
                            }*/
                            setValues(assocDB, assocRtnObj,  assocF.trim(), subAssoc);
                        }
                     }
                    assocRtnList.add(assocRtnObj);
                }
                PropertyUtils.setProperty(rtnObject, param.getKey(), assocRtnList);
            } else {
                Object assocDB = PropertyUtils.getProperty(defaultObject, param.getKey());
                Object assocRtnObj = BeanUtils.instantiate(PropertyUtils.getProperty(defaultObject, param.getKey()).getClass());
                if(param.getValue() != null) {
                    String assocFields = param.getValue();
                    String[] arrAssocFields = assocFields.split(",");
                    for (String assocF:arrAssocFields){
                        if(PropertyUtils.getProperty(assocDB, assocF).getClass().toString().contains("com.yetistep.delivr.model")){
                              //PropertyUtils.setProperty(assocRtn, assocF,  PropertyUtils.getProperty(assocDB, assocF));
                             Object assoc2ndDB = PropertyUtils.getProperty(assocDB, assocF.trim());
                             Object assoc2ndRtnObj = BeanUtils.instantiate(assoc2ndDB.getClass());
                             String accoc2ndFs = subAssoc.get(assocF.trim());
                             if(accoc2ndFs != null){
                                    String[] arrAssoc2ndFields = accoc2ndFs.split(",");
                                    for (String accoc2ndF: arrAssoc2ndFields){
                                        //PropertyUtils.setProperty(assoc2ndRtnObj, accoc2ndF,  PropertyUtils.getProperty(assoc2ndDB, accoc2ndF));
                                        setValues(assoc2ndDB, assoc2ndRtnObj,  accoc2ndF.trim(), subAssoc);
                                    }
                              }
                               PropertyUtils.setProperty(assocRtnObj, assocF.trim(),  assoc2ndRtnObj);
                        } else {
                              PropertyUtils.setProperty(assocRtnObj, assocF.trim(),  PropertyUtils.getProperty(assocDB, assocF.trim()));
                        }
                    }
                }
                PropertyUtils.setProperty(rtnObject, param.getKey(), assocRtnObj);
            }
        }

        return rtnObject;
    }

    private static void setValues(Object assocDB, Object assocRtnObj,  String assocF, Map<String, String> subAssoc) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException{
        if(PropertyUtils.getProperty(assocDB, assocF).getClass().getName().equals("org.hibernate.collection.internal.PersistentBag")) {
            List<Object> assoc2ndDBs = (List<Object>) PropertyUtils.getProperty(assocDB, assocF.trim());
            List<Object> assoc2ndRtn = new ArrayList<>();
            for (Object assoc2ndDB: assoc2ndDBs) {
                Object assoc2ndRtnObj = BeanUtils.instantiate(assoc2ndDB.getClass());
                String accoc2ndFs = subAssoc.get(assocF.trim());
                if(accoc2ndFs != null){
                    String[] arrAssoc2ndFields = accoc2ndFs.split(",");
                    for (String accoc2ndF: arrAssoc2ndFields){
                        //PropertyUtils.setProperty(assoc2ndRtnObj, accoc2ndF,  PropertyUtils.getProperty(assoc2ndDB, accoc2ndF));
                        setValues(assoc2ndDB, assoc2ndRtnObj,  accoc2ndF.trim(), subAssoc);
                    }
                }
                assoc2ndRtn.add(assoc2ndRtnObj);
            }
            PropertyUtils.setProperty(assocRtnObj, assocF.trim(),  assoc2ndRtn);
        } else {
            if(PropertyUtils.getProperty(assocDB, assocF.trim()).getClass().toString().contains("com.yetistep.delivr.model")){
                Object assoc2ndRtnObj = BeanUtils.instantiate(PropertyUtils.getProperty(assocDB, assocF.trim()).getClass());
                String accoc2ndFs = subAssoc.get(assocF.trim());
                if(accoc2ndFs != null){
                    String[] arrAssoc2ndFields = accoc2ndFs.split(",");
                    for (String accoc2ndF: arrAssoc2ndFields){
                        //PropertyUtils.setProperty(assoc2ndRtnObj, accoc2ndF,  PropertyUtils.getProperty(PropertyUtils.getProperty(assocDB, assocF), accoc2ndF));
                        setValues(PropertyUtils.getProperty(assocDB, assocF.trim()), assoc2ndRtnObj,  accoc2ndF.trim(), subAssoc);
                    }
                }
                PropertyUtils.setProperty(assocRtnObj, assocF.trim(),  assoc2ndRtnObj);
            }else{
                PropertyUtils.setProperty(assocRtnObj, assocF.trim(),  PropertyUtils.getProperty(assocDB, assocF.trim()));
            }
        }
    }


}
