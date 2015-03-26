package com.yetistep.delivr.model;

import javax.persistence.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 3/26/15
 * Time: 10:51 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "PreferenceTypeEntity")
@Table(name = "preferences_types")
public class PreferenceTypeEntity {


    private Integer id;
    private String groupName;
    private List<PreferenceSectionEntity> section;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "group_name")
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @OneToMany(mappedBy = "group")
    public List<PreferenceSectionEntity> getSection() {
        return section;
    }

    public void setSection(List<PreferenceSectionEntity> section) {
        this.section = section;
    }
}
