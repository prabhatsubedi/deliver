package com.yetistep.delivr.model;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yetistep.delivr.util.JsonDateSerializer;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 12/5/14
 * Time: 2:34 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name="CategoryEntity")
@Table(name="categories")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id")
public class CategoryEntity implements Serializable {
   private Integer id;
   private CategoryEntity parent;
   private List<ItemEntity> item;
   private List<CategoryEntity> child;
   private List<BrandsCategoryEntity> brandsCategory;
   private StoresBrandEntity storesBrand;
   private String name;
   private Boolean featured;
   private Integer priority;
   private Timestamp createdDate;
   private String imageUrl;
    //Transient
   private Integer parentId;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "parent_id")
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

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "brand_id")
    public StoresBrandEntity getStoresBrand() {
        return storesBrand;
    }

    public void setStoresBrand(StoresBrandEntity storesBrand) {
        this.storesBrand = storesBrand;
    }

    @OneToMany(mappedBy = "category")
    public List<ItemEntity> getItem() {
        return item;
    }

    public void setItem(List<ItemEntity> item) {
        this.item = item;
    }


    @Column(name = "name")
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

    @JsonSerialize(using = JsonDateSerializer.class)
    @Column(name = "created_date", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    @Column(name = "image_url")
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Transient
    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }



}
