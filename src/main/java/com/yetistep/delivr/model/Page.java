package com.yetistep.delivr.model;

import com.yetistep.delivr.util.YSException;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 12/15/14
 * Time: 5:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class Page {
    private Integer pageNumber;
    private Integer pageSize;
    private String sortBy;
    private String sortOrder;
    private Integer rowNumber;
    private Integer totalRows;
    private String searchFor;
    private Map<String, String> searchFields;

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
       this.rowNumber = rowNumber;
    }

    public Integer getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(Integer totalRows) {
        this.totalRows = totalRows;
    }

    public String getSearchFor() {
        return searchFor;
    }

    public void setSearchFor(String searchFor) {
        this.searchFor = searchFor;
    }

    public Map<String, String> getSearchFields() {
        return searchFields;
    }

    public void setSearchFields(Map<String, String> searchFields) {
        this.searchFields = searchFields;
    }

    public Integer getValidRowNumber(){
        if(pageNumber <= 0 || pageSize == 0){
            throw new YSException("ERR013");
        }
        this.rowNumber = (pageNumber-1)*pageSize;
        if(rowNumber >= totalRows){
            throw new YSException("ERR013");
        }
        return rowNumber;
    }
}
