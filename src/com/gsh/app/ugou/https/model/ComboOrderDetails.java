package com.gsh.app.ugou.https.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by taosj on 15/4/27.
 */
public class ComboOrderDetails implements Serializable {

    public long orderId;
    public long comboId;
    public boolean isVote;
    public String mainPicturePath;
    public String comboName;
    public double price;

    public String receiverName;
    public String mobile;
    public String address;

    public List<ComboOrderDelivery> delivery;

}
