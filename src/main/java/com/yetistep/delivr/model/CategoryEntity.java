package com.yetistep.delivr.model;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 12/5/14
 * Time: 2:34 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name="CategoryEntity")
@Table(name="categories")
public class CategoryEntity implements Serializable {


   private Integer id;
   private CategoryEntity parent;
   private List<ItemEntity> item;
   private List<CategoryEntity> child;
   private List<BrandsCategoryEntity> brandsCategory;
   private StoreEntity store;
   private String name;
   private Boolean featured;
   private Integer priority;
   private Timestamp createdDate;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public CategoryEntity getParent() {
        return parent;
    }

    public void setParent(CategoryEntity parent) {
        this.parent = parent;
    }

    @OneToMany(mappedBy = "parent")
    public List<CategoryEntity> getChild() {
        return child;
    }

    public void setChild(List<CategoryEntity> child) {
        this.child = child;
    }

    @OneToMany(mappedBy = "category")
    public List<BrandsCategoryEntity> getBrandsCategory() {
        return brandsCategory;
    }

    public void setBrandsCategory(List<BrandsCategoryEntity> brandsCategory) {
        this.brandsCategory = brandsCategory;
    }

    @OneToMany(mappedBy = "category")
    public List<ItemEntity> getItem() {
        return item;
    }

    public void setItem(List<ItemEntity> item) {
        this.item = item;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    public StoreEntity getStore() {
        return store;
    }

    public void setStore(StoreEntity store) {
        this.store = store;
    }


    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "featured", columnDefinition = "TINYINT(1) default '0'")
    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    @Column(name = "priority")
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Column(name = "created_date", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

}
