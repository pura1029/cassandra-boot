/* **********************************************************************
 * Copyright 2019 All rights reserved. VMware Confidential
 * *********************************************************************/

package com.gtm.model;

/**
 * Insert your comment for Gender here
 *
 * @author kumargautam
 */
public enum Gender {

    MALE("male"), FEMALE("female"), OTHERS("others");

    private String type;

    Gender(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
