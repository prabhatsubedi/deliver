package com.yetistep.delivr.enums;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/21/14
 * Time: 2:29 PM
 * To change this template use File | Settings | File Templates.
 */
public enum Role {

    ROLE_ADMIN(1), ROLE_MANAGER(2), ROLE_ACCOUNTANT(3), ROLE_DELIVERY_BOY(4), ROLE_MERCHANT(5), ROLE_CUSTOMER(6);

    private final int role;

    private Role(int role) {
        this.role = role;
    }

    public Integer toInt() {
        return role;
    }

    public String toStr() {
        return String.valueOf(role);
    }

    public static Role fromInt(Integer arg) {
        switch (arg) {
            case 1:
                return ROLE_ADMIN;
            case 2:
                return ROLE_MANAGER;
            case 3:
                return ROLE_ACCOUNTANT;
            case 4:
                return ROLE_DELIVERY_BOY;
            case 5:
                return ROLE_MERCHANT;
            default:
                return ROLE_CUSTOMER;
        }
    }
}
