package com.yetistep.delivr.util;

import com.yetistep.delivr.model.Page;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 12/16/14
 * Time: 12:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class HibernateUtil {

    /**
     * This method fills criteria object based on page object data.
     *
     * @param criteria
     * @param page
     */

    public static void fillPaginationCriteria(Criteria criteria, Page page, Class clazz) throws Exception{
        if (page != null) {
                /*search implementation*/
            if(page.getSearchFor() != null && !page.getSearchFor().equals("") && page.getSearchFields() != null) {
                Iterator itS = page.getSearchFields().entrySet().iterator();
                Criterion criterion = null;
                String aliasPath = criteria.getAlias();
                while (itS.hasNext()) {
                    Map.Entry pairs = (Map.Entry)itS.next();
                    if(pairs.getKey().toString() == "self"){
                        for (String field:pairs.getValue().toString().split(",")) {
                            if(criterion == null) criterion = Restrictions.like(field, "%"+page.getSearchFor()+"%");
                            else
                                criterion = Restrictions.or(criterion, Restrictions.like(field, "%"+page.getSearchFor()+"%"));
                        }
                    }else{
                        String[] searchingPath = pairs.getKey().toString().split("#");
                        if(searchingPath.length==2){
                            //if alias do not exists create it
                            String baseClass = searchingPath[0];
                            if(!criteria.toString().contains("Subcriteria("+aliasPath+"."+baseClass+":"+baseClass+")")) {
                                criteria.createAlias(aliasPath+"."+baseClass, baseClass);
                            }

                            if(!criteria.toString().contains("Subcriteria("+baseClass+"."+searchingPath[1]+":"+searchingPath[1]+")")){
                                criteria.createAlias(aliasPath+"."+baseClass+"."+searchingPath[1], searchingPath[1]);
                            }

                            String fields[] = pairs.getValue().toString().split(",");
                            for (String field:pairs.getValue().toString().split(",")) {
                                if(criterion == null) criterion = Restrictions.like(searchingPath[1]+"."+field, "%"+page.getSearchFor()+"%");
                                else
                                    criterion = Restrictions.or(criterion, Restrictions.like(searchingPath[1]+"."+field, "%"+page.getSearchFor()+"%"));
                            }
                        }else if(searchingPath.length>2){
                            //ToDO: do it  if required
                        } else {
                            if(!criteria.toString().contains("Subcriteria("+aliasPath+"."+pairs.getKey().toString()+":"+pairs.getKey().toString()+")")) {
                                criteria.createAlias(aliasPath+"."+pairs.getKey().toString(), pairs.getKey().toString());
                            }
                            String fields[] = pairs.getValue().toString().split(",");
                            for (String field:pairs.getValue().toString().split(",")) {
                                if(criterion == null) criterion = Restrictions.like(pairs.getKey().toString()+"."+field, "%"+page.getSearchFor()+"%");
                                else
                                    criterion = Restrictions.or(criterion, Restrictions.like(pairs.getKey().toString()+"."+field, "%"+page.getSearchFor()+"%"));
                            }
                        }
                    }
                }
                criteria.add(criterion);
            }

             /* Pagination implementation*/
            if(page.getTotalRows() != null && page.getTotalRows() > 0){
                if(page.getPageNumber()!= null && page.getPageSize() != null){
                    criteria.setFirstResult(page.getValidRowNumber()).setMaxResults(page.getPageSize());
                }

                /* Order By Implementation*/
                if (page.getSortOrder() != null && page.getSortBy() != null) {
                /* Throws NoSuchFieldException if field doesn't exist. */
                    try{
                        String[] sortingInfo = page.getSortBy().split("#");
                        String aliasPath = criteria.getAlias();
                        if(sortingInfo.length==2){
                            String classString = sortingInfo[0];
                            String fieldString = sortingInfo[1];

                            Class sortingClass =  null;

                            if(clazz.getDeclaredField(classString).getType().toString().contains("com.yetistep.delivr.model")) {
                                sortingClass =  clazz.getDeclaredField(classString).getType();
                            }else{
                                String genericString = clazz.getDeclaredField(classString).getGenericType().toString();
                                sortingClass = Class.forName(genericString.split("<")[1].split(">")[0]);
                            }

                            sortingClass.getDeclaredField(fieldString);

                            //if alias do not exists create it
                            if(!criteria.toString().contains("Subcriteria("+aliasPath+"."+classString+":"+classString+")")) {
                                criteria.createAlias(aliasPath+"."+classString, classString);
                            }

                            if (page.getSortOrder().equalsIgnoreCase("asc")) {
                                criteria.addOrder(Order.asc(classString+"."+fieldString));
                            } else if (page.getSortOrder().equalsIgnoreCase("desc"))  {
                                criteria.addOrder(Order.desc(classString+"."+fieldString));
                            }
                        }else if(sortingInfo.length>2){
                            Integer i = 0;
                            String prevModel = "";
                            Class parentClass = clazz;
                            //String aliasPath = criteria.getAlias();
                            for (String key: sortingInfo){
                                if(i != (sortingInfo.length-1)){
                                    //if alias exists do not exists create it
                                    if(!criteria.toString().contains("Subcriteria("+aliasPath+"."+sortingInfo[i]+":"+sortingInfo[i]+")")){
                                        criteria.createAlias(aliasPath+"."+sortingInfo[i], sortingInfo[i]);
                                    }

                                    if(prevModel != "") {
                                        if(parentClass.getDeclaredField(prevModel).getType().toString().contains("com.yetistep.delivr.model")) {
                                            parentClass =  parentClass.getDeclaredField(prevModel).getType();
                                        }else{
                                            String genericString = parentClass.getDeclaredField(prevModel).getGenericType().toString();
                                            parentClass = Class.forName(genericString.split("<")[1].split(">")[0]);
                                        }
                                    }
                                    prevModel = sortingInfo[i];
                                    aliasPath += "."+prevModel;
                                }
                                i++;
                            }
                            Class sortingClass = null;

                            if(parentClass.getDeclaredField(prevModel).getType().toString().contains("com.yetistep.delivr.model")) {
                                sortingClass =  parentClass.getDeclaredField(prevModel).getType();
                            }else{
                                String genericString = parentClass.getDeclaredField(prevModel).getGenericType().toString();
                                sortingClass = Class.forName(genericString.split("<")[1].split(">")[0]);
                            }

                            sortingClass.getDeclaredField(sortingInfo[sortingInfo.length-1]);

                            if (page.getSortOrder().equalsIgnoreCase("asc")) {
                                criteria.addOrder(Order.asc(prevModel+"."+sortingInfo[sortingInfo.length-1]));
                            } else if (page.getSortOrder().equalsIgnoreCase("desc"))  {
                                criteria.addOrder(Order.desc(prevModel+"."+sortingInfo[sortingInfo.length-1]));
                            }
                        }else{
                            String fieldString = sortingInfo[0];
                            clazz.getDeclaredField(fieldString);

                            if (page.getSortOrder().equalsIgnoreCase("asc")) {
                                criteria.addOrder(Order.asc(fieldString));
                            } else if (page.getSortOrder().equalsIgnoreCase("desc"))  {
                                criteria.addOrder(Order.desc(fieldString));
                            }
                        }
                    } catch(NoSuchFieldException e){
                        throw new YSException("JSN005");
                    }  catch (Exception e){
                        throw new YSException("JSN005");
                    }
                }
            }
        }
    }


    /**
     * This method fills query object based on page object data.
     *
     * @param query
     * @param page
     */
    public static void fillPaginationCriteria(Query query, Page page) throws Exception{
        if (page != null) {
            if(page.getTotalRows() > 0){
                /* Pagination implementation*/
                if(page.getPageNumber()!= null && page.getPageSize() != null){
                    query.setFirstResult(page.getValidRowNumber()).setMaxResults(page.getPageSize());
                }
            }
        }
    }
}
