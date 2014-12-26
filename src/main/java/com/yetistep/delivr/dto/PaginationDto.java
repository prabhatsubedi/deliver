package com.yetistep.delivr.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 12/16/14
 * Time: 9:58 AM
 * This class is used to get list of objects which implements pagination.
 */
public class PaginationDto implements Serializable{
    /* Total number of rows. */
    private Integer numberOfRows;
    /* List of entities to be sent. */
    private List<?> data;

    public Integer getNumberOfRows() {
        return numberOfRows;
    }

    public void setNumberOfRows(Integer numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    public List<?> getData() {
        return data;
    }

    public void setData(List<?> data) {
        this.data = data;
    }
}
