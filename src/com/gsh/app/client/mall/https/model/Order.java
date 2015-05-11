package com.gsh.app.client.mall.https.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/4/10.
 */
public class Order implements Serializable {
    /*               addUrlParam("orderId", String.valueOf(m11.id)).
                        addUrlParam("addressId", addressId).
                        addUrlParam("paymentType", deliver.getPayMethodInteger()).
                        addUrlParam("deliverType", deliver.getDeliverMethodInteger()).
                        addUrlParam("deliverTime", String.valueOf(deliver.normalTime)).
                        addUrlParam("preDeliverTime", String.valueOf(deliver.orderTime)).
                        addUrlParam("isUseScore", String.valueOf(useScore));
    */
    public String orderId;
    public String addressId;
    public int paymentType;
    public int deliverType;
    public String deliverTime;
    public boolean isUseScore;
}
