package com.yetistep.delivr.model;

import javax.persistence.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 3/26/15
 * Time: 10:00 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "PreferenceSectionEntity")
@Table(name = "preferences_sections")
public class PreferenceSectionEntity {


    private Integer id;
    private String section;
    private PreferenceTypeEntity group;
    private List<PreferencesEntity> preference;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "section")
    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    public PreferenceTypeEntity getGroup() {
        return group;
    }

    public void setGroup(PreferenceTypeEntity group) {
        this.group = group;
    }

    @OneToMany(mappedBy = "section")
    public List<PreferencesEntity> getPreference() {
        return preference;
    }

    public void setPreference(List<PreferencesEntity> preference) {
        this.preference = preference;
    }
}
