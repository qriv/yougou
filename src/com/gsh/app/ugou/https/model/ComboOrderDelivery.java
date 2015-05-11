package com.gsh.app.ugou.https.model;

import java.io.Serializable;

/**
 * Created by taosj on 15/4/27.
 */
public class ComboOrderDelivery implements Serializable {


    public String sn;
    public long sendTime;
    public String deliveryState;//WAIT RUN OVER

    public boolean signAvailable() {
        return deliveryState.equalsIgnoreCase("run");
    }
}
