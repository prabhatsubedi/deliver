package com.yetistep.delivr.enums;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/26/14
 * Time: 12:36 PM
 * To change this template use File | Settings | File Templates.
 */
public enum Gender implements PersistentEnum {

    MALE(1),
    FEMALE(2),
    ALL(3),
    OTHERS(4);


    private final int id;

    Gender(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    public static Gender fromInt(int arg){
        if(arg == 1)
            return MALE;
        else if(arg == 2)
            return FEMALE;
        else if(arg == 3)
            return ALL;
        else
            return OTHERS;
    }

}
