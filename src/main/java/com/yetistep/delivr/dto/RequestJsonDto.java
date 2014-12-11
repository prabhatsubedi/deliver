package com.yetistep.delivr.dto;

import com.yetistep.delivr.controller.AnonController;
import com.yetistep.delivr.enums.PasswordActionType;
import com.yetistep.delivr.model.TestEntity;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 12/10/14
 * Time: 6:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class RequestJsonDto {

    /* Password Action Type */
   /* ====== Used at AnonController.changePassword ===== */
    private PasswordActionType actionType;

    public PasswordActionType getActionType() {
        return actionType;
    }

    public void setActionType(PasswordActionType actionType) {
        this.actionType = actionType;
    }

}
