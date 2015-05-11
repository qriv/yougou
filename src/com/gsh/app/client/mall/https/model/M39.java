package com.gsh.app.client.mall.https.model;

import java.io.Serializable;

/**
 *
 */
public class M39 implements Serializable {
    public String deliverInfo;
    public long deliverInfoTime;

    public M39() {
    }

    public M39(String deliverInfo, long deliverInfoTime) {
        this.deliverInfo = deliverInfo;
        this.deliverInfoTime = deliverInfoTime;
    }
}
