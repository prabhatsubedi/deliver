package com.yetistep.delivr.enums;

import com.yetistep.delivr.util.YSException;

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

    public static JobOrderStatus fromInt(int arg){
        if(arg == 0)
            return ORDER_PLACED;
        else if(arg == 1)
            return ORDER_ACCEPTED;
        else if(arg == 2)
            return IN_ROUTE_TO_PICK_UP;
        else if(arg == 3)
            return AT_STORE;
        else if(arg == 4)
            return IN_ROUTE_TO_DELIVERY;
        else if(arg == 5)
            return DELIVERED;
        else
            return CANCELLED;
    }


    /**
     * This method is used to check validation of job order while traversing from one state to other.
     * @param currentJobOrderStatus  Current Job Order Status
     * @param nextJobOrderStatus Next Job Order Status
     * @throws Exception  Business Exception based on job order status
     */
    public static void traverseJobStatus(JobOrderStatus currentJobOrderStatus, JobOrderStatus nextJobOrderStatus) throws Exception {
        if(nextJobOrderStatus.equals(DELIVERED))
            checkConditions(currentJobOrderStatus, false, false, false, false, false, true);
        else if(nextJobOrderStatus.equals(IN_ROUTE_TO_DELIVERY))
            checkConditions(currentJobOrderStatus, false, true, false, false, true, true);
        else if(nextJobOrderStatus.equals(AT_STORE))
            checkConditions(currentJobOrderStatus, false, true, false, true, true, true);
        else if(nextJobOrderStatus.equals(IN_ROUTE_TO_PICK_UP))
            checkConditions(currentJobOrderStatus, false, true, true, true, true, true);
        else if(nextJobOrderStatus.equals(ORDER_ACCEPTED))
            checkConditions(currentJobOrderStatus, true, true, true, true, true, true);
    }

    private static void checkConditions(JobOrderStatus currentJobOrderStatus, boolean orderAccepted, boolean cancelled,
                                        boolean inRoutePickUp, boolean atStore, boolean inRouteDelivery, boolean delivered) throws Exception {
        if (orderAccepted) {
            if (currentJobOrderStatus.equals(ORDER_ACCEPTED))
                throw new YSException("ORD005");
        }
        if (cancelled) {
            if (currentJobOrderStatus.equals(CANCELLED))
                throw new YSException("ORD006");
        }
        if (inRoutePickUp) {
            if (currentJobOrderStatus.equals(IN_ROUTE_TO_PICK_UP))
                throw new YSException("ORD007");
        }
        if (atStore) {
            if (currentJobOrderStatus.equals(AT_STORE))
                throw new YSException("ORD008");
        }
        if (inRouteDelivery) {
            if (currentJobOrderStatus.equals(IN_ROUTE_TO_DELIVERY))
                throw new YSException("ORD009");
        }
        if (delivered) {
            if (currentJobOrderStatus.equals(DELIVERED))
                throw new YSException("ORD010");
        }
    }
}
