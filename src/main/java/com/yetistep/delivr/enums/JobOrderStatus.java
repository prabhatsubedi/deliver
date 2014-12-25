package com.yetistep.delivr.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 12/12/14
 * Time: 4:59 PM
 * To change this template use File | Settings | File Templates.
 */
public enum JobOrderStatus {
    ORDER_PLACED, /* when checkout is successful */
    ORDER_ACCEPTED, /* when delivery boy clicks ACCEPT */
    IN_ROUTE_TO_PICK_UP, /* when delivery boy clicks START JOB */
    AT_STORE, /* when delivery boy clicks AT STORE */
    IN_ROUTE_TO_DELIVERY, /* when delivery boy clicks IN ROUTE TO DELIVERY */
    DELIVERED, /* when delivery boy enter the receive CODE */
    CANCELLED; /* when order has been cancelled or failed */
}
