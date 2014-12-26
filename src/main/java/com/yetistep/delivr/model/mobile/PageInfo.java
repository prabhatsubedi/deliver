package com.yetistep.delivr.model.mobile;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/26/14
 * Time: 11:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class PageInfo {
    private Integer pageSize;
    private Integer totalRows;
    private Integer totalPage;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(Integer totalRows) {
        this.totalRows = totalRows;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }
}
