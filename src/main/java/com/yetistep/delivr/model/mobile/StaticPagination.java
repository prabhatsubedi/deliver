package com.yetistep.delivr.model.mobile;

import com.yetistep.delivr.util.CommonConstants;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/24/14
 * Time: 12:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class StaticPagination extends PageInfo{
    List<? extends Object> list;

    public StaticPagination(){
        setPageSize(CommonConstants.MAX_STATIC_PAGE_SIZE);
    }

   public StaticPagination paginate(List<? extends Object> raw, Integer pageId) {
       setTotalRows(raw.size());

       //set first page result and return to the user
       if(getTotalRows() > getPageSize()){
           int lastIndex = (getTotalRows() > (getPageSize() * pageId)) ? (getPageSize() * pageId) : getTotalRows();
           raw  = raw.subList(((pageId-1)*getPageSize()), lastIndex);
       }else
           raw = raw.subList(0, getTotalRows());

       setTotalPage((int) Math.ceil(getTotalRows().doubleValue()/getPageSize().doubleValue()));
       setList(raw);
       return this;

   }

    public List<? extends Object> getList() {
        return list;
    }

    public void setList(List<? extends Object> list) {
        this.list = list;
    }
}
