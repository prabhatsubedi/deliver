package com.yetistep.delivr.enums;


import java.sql.Date;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 6/10/14
 * Time: 5:15 PM
 * To change this template use File | Settings | File Templates.
 */
public enum AgeGroup {

    /* 0-18(G1), 19 - 25(G2), 26 - 35(G3), 36 - 45(G4), 46 - 55(G5), 56 - 127(G6) */
    GROUP1(18), GROUP2(25), GROUP3(35), GROUP4(45), GROUP5(55), GROUP6(127);

    private final int age;

    private AgeGroup(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }

//    public static AgeGroup findAgeGroup(Date dob) {
//        int age = MessageBundle.findAge(dob);
//        return findAgeGroup(age);
//    }
//
//    public static AgeGroup findAgeGroup(int age) {
//        AgeGroup found = null;
//
//        for (AgeGroup group : values())
//            if (group.getAge() >= age) {
//                found = group;
//                break;
//            }
//
//        return found;
//    }
}
