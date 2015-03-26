package com.yetistep.delivr.model;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/12/14
 * Time: 12:30 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name ="PreferencesEntity")
@Table(name = "preferences")
public class PreferencesEntity {
    private Integer id;
    private String prefKey;
    private String value;
    private PreferenceSectionEntity section;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "pref_key", unique = true)
    public String getPrefKey() {
        return prefKey;
    }

    public void setPrefKey(String prefKey) {
        this.prefKey = prefKey;
    }

    @Column(name = "value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    public PreferenceSectionEntity getSection() {
        return section;
    }

    public void setSection(PreferenceSectionEntity section) {
        this.section = section;
    }
}
