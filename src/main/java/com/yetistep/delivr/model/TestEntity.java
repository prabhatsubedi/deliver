package com.yetistep.delivr.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yetistep.delivr.util.JsonDateSerializer;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 3/20/15
 * Time: 4:32 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "TestEntity")
@Table(name = "timestamp_test")
public class TestEntity {
    private Integer id;
    private String test;
    private Timestamp createdDate;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "test")
    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    @JsonSerialize(using = JsonDateSerializer.class)
    @Column(name = "created_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }


}
