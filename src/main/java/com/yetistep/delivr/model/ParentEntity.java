package com.yetistep.delivr.model;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/24/14
 * Time: 11:58 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name="ParentEntity")
@Table(name="parent")
public class ParentEntity {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "address")
    private String address;

    //@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    @OneToOne(mappedBy="parent")
    @Fetch(FetchMode.JOIN)
    private ChildEntity childEntity;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ChildEntity getChildEntity() {
        return childEntity;
    }

    public void setChildEntity(ChildEntity childEntity) {
        this.childEntity = childEntity;
    }
}
