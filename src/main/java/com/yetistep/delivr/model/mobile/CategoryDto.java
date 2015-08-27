package com.yetistep.delivr.model.mobile;

import com.yetistep.delivr.model.CategoryEntity;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 1/2/15
 * Time: 2:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class CategoryDto extends CategoryEntity{
    private Integer brandId;
    private Boolean hasNext;
    private Integer priority;

    public Boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }


    public CategoryDto setValues(CategoryEntity categoryEntity) {
        setId(categoryEntity.getId());
        setName(categoryEntity.getName());
        setPriority(categoryEntity.getPriority());
        return this;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }
}
