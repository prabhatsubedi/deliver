package com.yetistep.delivr.util;

import com.yetistep.delivr.model.Page;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;

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
            if(page.getTotalRows() > 0){
                /* Pagination implementation*/
                if(page.getPageNumber()!= null && page.getPageSize() != null){
                    criteria.setFirstResult(page.getValidRowNumber()).setMaxResults(page.getPageSize());
                }
                /* Order By Implementation*/
                if (page.getSortOrder() != null && page.getSortBy() != null) {
                /* Throws NoSuchFieldException if field doesn't exist. */
                    try{
                        String[] sortingInfo = page.getSortBy().split("#");
                        if(sortingInfo.length>1){
                            String classString = sortingInfo[0];
                            String fieldString = sortingInfo[1];

                            Class sortingClass =  clazz.getDeclaredField(classString).getType();

                            sortingClass.getDeclaredField(fieldString);

                            criteria.createAlias(classString, classString);
                            if (page.getSortOrder().equalsIgnoreCase("asc")) {
                                criteria.addOrder(Order.asc(classString+"."+fieldString));
                            } else if (page.getSortOrder().equalsIgnoreCase("desc"))  {
                                criteria.addOrder(Order.desc(classString+"."+fieldString));
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
