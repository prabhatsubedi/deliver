package com.yetistep.delivr.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.yetistep.delivr.enums.ActionType;
import com.yetistep.delivr.enums.Role;
import com.yetistep.delivr.util.JsonDateSerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 12/2/14
 * Time: 11:37 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "ActionLogEntity")
@Table(name = "action_log")
public class ActionLogEntity implements Serializable {
    private Long id;
    private UserEntity user;
    private Role role;
    private ActionType actionType;
    private String description;
    private String userIP;
    private Timestamp dateTime;

    public ActionLogEntity(Role role, UserEntity user, ActionType actionType, String description, String userIP) {
        this.role = role;
        this.user = user;
        this.actionType = actionType;
        this.description = description;
        this.userIP = userIP;
    }

    public ActionLogEntity() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @ManyToOne
    @JoinColumn(name = "user_id")
    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @Column(name = "user_role")
    @Enumerated(EnumType.STRING)
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Column(name = "action")
    @Enumerated(EnumType.STRING)
    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    @Column(name = "description", columnDefinition = "longtext")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "user_ip")
    public String getUserIP() {
        return userIP;
    }

    public void setUserIP(String userIP) {
        this.userIP = userIP;
    }

    @JsonSerialize(using = JsonDateSerializer.class)
    @Column(name = "date_time", columnDefinition="TIMESTAMP NULL DEFAULT NULL")
    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }
}
