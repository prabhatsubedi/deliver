package com.yetistep.delivr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private Timestamp anotherDate;
    private Timestamp lastActivityDate;

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
    @Column(name = "another_date", columnDefinition="TIMESTAMP NULL DEFAULT NULL")
    @JsonProperty

    public Timestamp getAnotherDate() {
        return anotherDate;
    }

    public void setAnotherDate(Timestamp anotherDate) {
        this.anotherDate = anotherDate;
    }

    @JsonSerialize(using = JsonDateSerializer.class)
    @Column(name = "last_activity_date", columnDefinition="TIMESTAMP NULL DEFAULT NULL")
    @JsonProperty
    public Timestamp getLastActivityDate() {
        return lastActivityDate;
    }

    public void setLastActivityDate(Timestamp lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }

}
