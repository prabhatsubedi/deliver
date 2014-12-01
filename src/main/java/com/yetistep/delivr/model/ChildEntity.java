package com.yetistep.delivr.model;


import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/24/14
 * Time: 11:58 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name="ChildEntity")
@Table(name="child")
public class ChildEntity {
    @Id
    @GeneratedValue
    @Column(name="id")
    private Integer id;

    @Column(name = "name")
    private String name;

//    @Column(name="parent_id", unique=true, nullable=false)
//    @GeneratedValue(generator="gen")
//    @GenericGenerator(name="gen", strategy="foreign", parameters={@Parameter(name="property", value="parent")})
//    private Integer parentId;
    @Cascade({CascadeType.ALL})
    @OneToOne
    //@OneToOne(cascade = {javax.persistence.CascadeType.ALL, javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.MERGE})
    //@Fetch(FetchMode.JOIN)
    @JoinColumn(name="parent_id")
    private ParentEntity parent;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public Integer getParentId() {
//        return parentId;
//    }
//
//    public void setParentId(Integer parentId) {
//        this.parentId = parentId;
//    }

    public ParentEntity getParent() {
        return parent;
    }

    public void setParent(ParentEntity parent) {
        this.parent = parent;
    }
}
