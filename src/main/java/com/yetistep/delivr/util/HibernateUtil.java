package com.yetistep.delivr.util;

import com.yetistep.delivr.model.Page;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

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
                        clazz.getDeclaredField(page.getSortBy());
                    } catch(NoSuchFieldException e){
                        throw new YSException("JSN005");
                    }
                    if (page.getSortOrder().equalsIgnoreCase("asc")) {
                        criteria.addOrder(Order.asc(page.getSortBy()));
                    } else if (page.getSortOrder().equalsIgnoreCase("desc"))  {
                        criteria.addOrder(Order.desc(page.getSortBy()));
                    }
                }
            }
        }
    }
}
