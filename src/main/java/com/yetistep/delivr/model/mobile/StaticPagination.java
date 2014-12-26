package com.yetistep.delivr.model.mobile;

import com.yetistep.delivr.model.StoresBrandEntity;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/24/14
 * Time: 12:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class StaticPagination extends PageInfo{

    public StaticPagination(){
        setPageSize(20);
    }

   public StaticPagination paginate(List<? extends Object> raw, Integer pageId) {
       setTotalRows(raw.size());

       //set first page result and return to the user
       if(getTotalRows() > getPageSize())
           raw  = raw.subList(((pageId-1)*getPageSize()), getPageSize() * pageId);
       else
           raw = raw.subList(0, getTotalRows());

       setTotalPage((int) Math.ceil(getTotalRows().doubleValue()/getPageSize().doubleValue()));

       return this;

   }

}
