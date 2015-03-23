package com.yetistep.delivr.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Chandra Prakash Panday
 * Date: 2/10/15
 * Time: 11:20 AM
 * To change this template use File | Settings | File Templates.
 */
public enum PushNotificationRedirect {
    ORDER{
        @Override
        public String toString() {
            return ":order";
        }
    },
    DELIVR{
        @Override
        public String toString() {
            return ":delivr";
        }
    }, PLAYSTORE{
        @Override
        public String toString() {
            return ":playstore";
        }
    }, RECEIPT{
        @Override
        public String toString() {
            return ":receipt";
        }
    }, INFO{
        @Override
        public String toString() {
            return ":info";
        }
    };
}
