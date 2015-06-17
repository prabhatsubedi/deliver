package com.yetistep.delivr.model;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 6/16/15
 * Time: 12:10 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "IDGeneratorEntity")
@Table(name = "id_generator")
public class IDGeneratorEntity {
    private Integer id;
    private Long generatedId;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "generated_id")
    public Long getGeneratedId() {
        return generatedId;
    }

    public void setGeneratedId(Long generatedId) {
        this.generatedId = generatedId;
    }
}
