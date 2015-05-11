package com.gsh.app.ugou.https.model;

import java.io.Serializable;

/**
 * Created by taosj on 15/4/27.
 */
public class ComboOrder implements Serializable {

    public long orderId;
    public String sn;

    public String comboName;

    public String receiverName;
    public String mobile;
    public String address;

    public String paymentNo;
    public String name;
    public String summary;
}
