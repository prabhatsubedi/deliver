package com.yetistep.delivr.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 1/12/15
 * Time: 11:43 AM
 * To change this template use File | Settings | File Templates.
 */
public enum CancelReason {

    CUSTOMER_UNREACHABLE {
            @Override
            public String toString() {
                return "Customer Not Reachable";
            }
        },
    ITEM_NOT_FOUND {
            @Override
            public String toString() {
                return "Item Not Found";
            }
        },
    CUSTOMER_DENY_TO_TAKE_ORDER {
            @Override
            public String toString() {
                return "Customer Denied to take order";
            }
        },
    CUSTOMER_LOCATION_UNKNOWN {
        @Override
        public String toString() {
            return "Customer Location Unidentified";
        }
    },
    SYSTEM_CANCELLED_TIME_OUT {
        @Override
        public String toString() {
            return "Order not accepted in given time";
        }
    },
    OTHERS {
        @Override
        public String toString() {
            return "Others";
        }
    };

}
